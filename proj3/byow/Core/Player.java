package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.List;

public class Player {

    protected int playerI, playerJ; // Player's current position
    protected double gpa; // Player's HP. Presumably the game will end when player runs out of HP.
    protected Room currentPlayerRoom;
    protected List<Room> totalRoomData;
    protected boolean hasRoom;
    private char lastEntered;
    protected TETile[][] worldArray; // worldArray for the player to change, same worldArray
    // object that belongs to the {@code World} class.

    /**
     * Player instance constructor method.
     *
     * @param initialI initial horizontal coordinate where the player will be placed.
     * @param initialJ initial vertical coordinate where the player will be placed.
     * @param worldArray for the player to handle the consequences of its actions with. This is
     *                   the main means by which the player effectuates change on the world it is
     *                   in.
     */
    public Player(int initialI, int initialJ, TETile[][] worldArray, List<Room> totalRoomData) {
        this.gpa = 4.0;
        this.totalRoomData = totalRoomData;
        this.playerI = initialI;
        this.playerJ = initialJ;
        this.worldArray = worldArray;
        this.hasRoom = false;
        this.lastEntered = 'X';
    }

    /**
     * Walking method that handles the consequence of advancing a player in a given direction.
     * This method will change the location of the Avatar Tile and restore the tile that the
     * player was previously on to a Floor Tile.
     *
     * @param direction character representation of which direction the player wishes to walk.
     */
    protected TETile[][] walk(char direction) {
        if (direction == 'W') { // up
            if (nextTileValid(direction, playerI, playerJ)) {
                worldArray[playerI][playerJ] = Tileset.FLOOR;
                worldArray[playerI][playerJ + 1] = Tileset.AVATAR;
                playerJ = playerJ + 1;
            }
        } else if (direction == 'S') { // down
            if (nextTileValid(direction, playerI, playerJ)) {
                worldArray[playerI][playerJ] = Tileset.FLOOR;
                worldArray[playerI][playerJ - 1] = Tileset.AVATAR;
                playerJ = playerJ - 1;
            }
        } else if (direction == 'A') { // left
            if (nextTileValid(direction, playerI, playerJ)) {
                worldArray[playerI][playerJ] = Tileset.FLOOR;
                worldArray[playerI - 1][playerJ] = Tileset.AVATAR;
                playerI = playerI - 1;
            }
        } else if (direction == 'D') { // right
            if (nextTileValid(direction, playerI, playerJ)) {
                worldArray[playerI][playerJ] = Tileset.FLOOR;
                worldArray[playerI + 1][playerJ] = Tileset.AVATAR;
                playerI = playerI + 1;
            }
        }
        return worldArray;
    }

    /**
     * Validity checking method to determine if where the player wishes to move next is a valid
     * tile.
     *
     * @param direction character representation of direction player wishes to advance
     * @param i player's current horizontal coordinate.
     * @param j player's current vertical coordinate.
     * @return {@code true} if the next tile in the direction the player wishes to move is a
     * valid tile, {@code false} otherwise.
     */
    private boolean nextTileValid(char direction, int i, int j) {
        if (direction == 'W') { // up
            if (worldArray[i][j + 1] == Tileset.FLOOR) {
                return true;
            }
        } else if (direction == 'S') { // down
            if (worldArray[i][j - 1] == Tileset.FLOOR) {
                return true;
            }
        } else if (direction == 'A') { // left
            if (worldArray[i - 1][j] == Tileset.FLOOR) {
                return true;
            }
        } else if (direction == 'D') { // right
            if (worldArray[i + 1][j] == Tileset.FLOOR) {
                return true;
            }
        }
        return false;
    }

    /**
     * Simple method for getting players current health, we will use this later for displaying
     * health status in the HUD.
     *
     * @return player's current health status.
     */
    protected double getPlayerGpa() {
        return this.gpa;
    }

    /**
     * Assigns the correct room/player relationship. More detail in the respective helper methods
     * within the body of this method.
     *
     * @param direction player wishes to walk next. Walk will be executed after the room/player
     *                  assignment has been handled.
     * @return updated {@code totalRoomData} to the {@code World} class to update game state.
     */
    protected List<Room> setPlayerRoom(char direction) {
        if (hasRoom) {
            return exitRoom(direction);
        } else {
            return enterRoom(direction);
        }
    }

    /**
     * Procedure to cleanly put a player in a given room. Sets {@code currentPlayerRoom} to
     * correct {@code Room} instance and adds the player instance to that room instance as well.
     *
     * @param direction player wishes to walk next.
     * @return updated {@code totalRoomData} to the {@code World} class to update game state.
     */
    protected List<Room> enterRoom(char direction) {
        for (Room room : totalRoomData) {
            if (!room.isStartRoom) {
                if (room.entranceI == playerI && room.entranceJ == playerJ && direction == room.enterDir) {
                    this.hasRoom = true;
                    this.currentPlayerRoom = room;
                    room.hasPlayer = true;
                }
            }
        }
        return totalRoomData;
    }

    /**
     * Procedure to cleanly exit player from room. Sets {@code currentPlayerRoom} to null and
     * removes player instance from room instance once executed.
     *
     * @param direction player wishes to walk next.
     * @return updated {@code totalRoomData} to the {@code World} class to update game state.
     */
    protected List<Room> exitRoom(char direction) {
        Room room = currentPlayerRoom;
        if (!room.isStartRoom) {
            if (direction == room.exitDir) {
                this.hasRoom = false;
                this.currentPlayerRoom = null;
                room.hasPlayer = false;
            }
        }
        return totalRoomData;
    }

    /**
     * Checks if player is currently standing on the entrance of a room.
     *
     * @return {@code true} if player is standing on the entrance of a room, false otherwise.
     */
    protected boolean onEntrance() {
        for (Room room : totalRoomData) {
            int roomI = room.entranceI;
            int roomJ = room.entranceJ;
            if (playerI == roomI && playerJ == roomJ) {
                return true;
            }
        }
        return false;
    }
}
