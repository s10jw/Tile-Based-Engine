package byow.Core;

import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HUD {
    private int width, height;
    private Player player;
    private Mouse mouse;

    private TETile[][] worldArray;
    public HUD (int width, int height, Player player, Mouse mouse, TETile[][] worldArray) {
        this.player = player;
        this.mouse = mouse;
        this.width = width;
        this.height = height;
        this.worldArray = worldArray;
        Font font = new Font("Monaco", Font.BOLD, 16);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width - 1);
        StdDraw.setYscale(0, this.height - 1);
        StdDraw.enableDoubleBuffering();
    }

    public void displayPlayerGPA() {
        String gpa = String.valueOf(player.gpa);
        gpa = "GPA: " + gpa;
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.textRight(this.width - 2, this.height - 2, gpa);
    }

    public void displayTileType() {
        List<Double> mouseCoordinate = new ArrayList<>();
        int mouseX = (int) mouse.mouseX;
        int mouseY = (int) mouse.mouseY;
        mouseCoordinate.add(mouse.mouseX);
        mouseCoordinate.add(mouse.mouseY);
        if ((0 <= mouseX && mouseX < worldArray.length) && (0 <= mouseY && mouseY < worldArray[0].length)) {
            TETile tile = worldArray[mouseX][mouseY];
            //String mouseTile = mouseCoordinate.toString();
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.textLeft(2, this.height - 2, tile.description());
            //System.out.println(mouseCoordinate.toString());
        }
    }

    public void getMousePosition() {
        mouse.mouseX = StdDraw.mouseX();
        mouse.mouseY = StdDraw.mouseY();
    }
}
