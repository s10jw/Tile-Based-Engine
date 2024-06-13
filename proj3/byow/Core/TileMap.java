package byow.Core;

import byow.TileEngine.TETile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TileMap extends HashMap<Integer, TETile> {
    int height;
    int width;
    List<Room> roomData;
    public TileMap(int height, int width) {
        this.roomData = new ArrayList<>();
        this.height = height;
        this.width = width;
    }


    public void setRoomData(List<Room> roomData) {
        this.roomData = roomData;
    }

    public List<Room> getRoomData() {
        return this.roomData;
    }
}
