package byow.Core;

import byow.TileEngine.Tileset;

import java.util.Random;
import java.util.*;

public class Builder {
    protected TileMap worldData;
    protected List<Room> totalRoomData;
    private final int ROOMLOWERBND = 10;
    private final int ROOMUPPERBND = 20;
    private final int width;
    private final long SEED;

    public Builder(TileMap tileMap, long seed) {
        this.width = tileMap.width;
        this.worldData = tileMap;
        this.SEED = seed;
        this.totalRoomData = worldData.roomData;
    }

    public TileMap buildWorld() {
        buildRooms();
        buildHallways();
        return this.worldData;
    }
    public void buildRooms() {
        /**
         * Returns a layout mapping. A layout map maps elements in the world array to their respective tile type.
         * Tile type is determined upon room generation in generateRoom, and subsequently in room connection in
         * connectRooms.
         *
         * @return a hashmap that maps indices to their respective tile type.
         */

        Random random = new Random(SEED);
        int totalRooms = RandomUtils.uniform(random, ROOMLOWERBND, ROOMUPPERBND);
        int count = 0;
        while (count < totalRooms) {
            generateRoom();
            count += 1;
        }
    }

    private void generateRoom() {
        /**
         * generateRoom generates a randomly placed room through the creation of roomData. This function then finds
         * all indices inside and on the room boundaries as described by roomData, and maps all of those indices to
         * their respective tile type in the map layout.
         */
        Room newRoom = new Room(worldData, SEED);
        if (!newRoom.isValid) {
            return;
        }
        worldData.setRoomData(newRoom.getRoomData());
        int n = newRoom.i;
        int m = newRoom.j;
        int roomWidth = newRoom.n;
        int roomHeight = newRoom.m;
        int index;

        for (int i = n; i <= roomWidth; i++) {
            for (int j = m; j <= roomHeight; j++) {
                if (i == n || i == roomWidth) {
                    // The below is how we convert the ith, jth element of the array to a unique integer.
                    index = j + (i * width);
                    worldData.put(index, Tileset.WALL);
                } else if (j == m || j == roomHeight) {
                    index = j + (i * width);
                    worldData.put(index, Tileset.WALL);
                } else {
                    index = j + (i * width);
                    worldData.put(index, Tileset.FLOOR);
                }
            }
        }
    }

    private Map<Integer, Integer> getEntrances(Room startRoom, Room endRoom) {
        Map<Integer, Integer> entranceMap = new HashMap<>();
        int count = 1; // Can set to random number in the future.
        int startIndex, endIndex;
        while (count > 0) {
            startIndex = startRoom.randomEntrance();
            endIndex = endRoom.randomEntrance();
            entranceMap.put(startIndex, endIndex);
            count -= 1;
        }
        return entranceMap;
    }

    public void buildHallways() {
        List<List<Integer>> paths = connectRooms();
        for (List<Integer> path : paths) {
            if (path == null) {
                continue;
            }
            drawPath(path);
        }
    }

    public List<List<Integer>> connectRooms() {
        PathSearch pathFinder = new PathSearch(worldData);
        List<List<Integer>> paths = new ArrayList<>();
        List<Integer> path;
        Map<Integer, Integer> connectionPoints;
        for (int i = 1; i < totalRoomData.size(); i++) {
            connectionPoints = getEntrances(totalRoomData.get(i - 1), totalRoomData.get(i));
            for (int start : connectionPoints.keySet()) {
                int target = connectionPoints.get(start);
                path = pathFinder.findPath(start, target);
                paths.add(path);
            }
        }
        return paths;
    }

    public void drawPath(List<Integer> path) {
        int index;
        for (int i = 0; i < path.size(); i++) {
            if (i == 0 || i == path.size() - 1) {
                continue;
            }
            index = path.get(i);
            walker(index);
        }
    }

    public void walker(int index) {
        int neighborX, neighborY, neighborIndex;
        int x = index / width;
        int y = index % width;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) {
                    worldData.put(index, Tileset.FLOOR);
                }
                neighborX = x + dx;
                neighborY = y + dy;
                neighborIndex = neighborY + (neighborX * width);
                if (!(worldData.get(neighborIndex) == Tileset.FLOOR || worldData.get(neighborIndex) == Tileset.WALL)) {
                    worldData.put(neighborIndex, Tileset.WALL);
                }
            }
        }
    }

}
