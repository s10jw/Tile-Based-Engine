package byow.Core;

import java.util.*;

public class Room {
    private TileMap layout;
    protected List<Room> totalRoomData;
    protected int i, j, n, m, width, height;
    protected boolean connected, isValid;
    protected int totalEntrances;
    private final long SEED;
    protected int entranceI;
    protected int entranceJ;
    public int eventPosI;
    public int eventPosJ;
    public char exitDir;
    public char enterDir;
    private final int ROOMCAP = 30;
    private final int BUFFER = 3;
    protected Teacher roomTeacher;
    protected boolean hasTeacher = false;
    public boolean hasPlayer;
    protected boolean isStartRoom = false;
    public Room(TileMap tileMap, long seed) {
        this.layout = tileMap;
        this.SEED = seed;
        this.totalRoomData = tileMap.roomData;
        this.width = tileMap.width;
        this.height = tileMap.height;
        this.connected = false;
        this.totalEntrances = 0;
        this.hasPlayer = false;

        createRoom();
    }

    private void createRoom() {
        Random random = new Random(SEED);
        int count = ROOMCAP;
        while (count > 0) {
            i = RandomUtils.uniform(random, 3, width - 9);
            j = RandomUtils.uniform(random, 3, height - 9);
            n = i + RandomUtils.uniform(random, 4, 7);
            m = j + RandomUtils.uniform(random, 3, 8);
            eventPosI = ((n - i) / 2) + i;
            eventPosJ = ((m - j) / 2) + j;
            if (verify()) {
                totalRoomData.add(this);
                isValid = true;
                return;
            }
            count -= 1;
        }
        isValid = false;
    }
    public void hasPlayer(boolean bool) {
        this.hasPlayer = bool;
    }

    private boolean verify() {
        /**
         * Returns a boolean. Returns true if roomData is a valid room and does not overlap with previously placed
         * rooms, returns false otherwise.
         *
         * @return a boolean describing validity of proposed room.
         */
        if (totalRoomData.isEmpty()) {
            return true;
        }
        for (Room room : totalRoomData) {
            if (!compare(room)) {
                return false;
            }
        }
        return true;
    }

    private boolean compare(Room otherRoom) {
        int newTop = n, newBottom = i, newLeft = j, newRight = m;
        int oldTop = otherRoom.n, oldBottom = otherRoom.i, oldLeft = otherRoom.j, oldRight = otherRoom.m;
        int buffer = BUFFER;
        boolean rightShift = newLeft > (oldRight + buffer) && newRight > (oldRight + buffer);
        boolean leftShift = newRight > (oldLeft - buffer) && newLeft > (oldRight + buffer);
        if (newTop > oldTop + buffer) {
            if (newBottom > oldTop + buffer) {
                return true;
            } else if (leftShift) {
                return true;
            } else {
                return rightShift;
            }
        } else {
            if (newTop < oldTop + buffer) {
                if (newTop < oldBottom - buffer) {
                    return true;
                } else if (leftShift) {
                    return true;
                } else {
                    return rightShift;
                }
            } else {
                if (leftShift) {
                    return true;
                } else {
                    return rightShift;
                }
            }
        }
    }

    public int randomEntrance() {
        totalEntrances += 1;
        Random random = new Random(SEED);
        int wall = RandomUtils.uniform(random, 1, 5);
        int x, y;
        if (wall == 1) {
            x = this.i;
            y = RandomUtils.uniform(random, this.j + 1, this.m);
            exitDir = 'A';
            enterDir = 'D';
        } else if (wall == 2) {
            x = RandomUtils.uniform(random, this.i + 1, this.n);
            y = this.m;
            exitDir = 'W';
            enterDir = 'S';
        } else if (wall == 3) {
            x = this.n;
            y = RandomUtils.uniform(random, this.j + 1, this.m);
            exitDir = 'D';
            enterDir = 'A';
        } else {
            x = RandomUtils.uniform(random, this.i + 1, this.n);
            y = this.j;
            exitDir = 'S';
            enterDir = 'W';
        }
        entranceI = x;
        entranceJ = y;
        return y + (x * width);
    }

    public void setRoomData(List<Room> roomData) {
        this.totalRoomData = roomData;
    }

    public List<Room> getRoomData() {
        return this.totalRoomData;
    }

    public boolean isConnected() {
        return connected;
    }
}
