package byow.Core;

import edu.princeton.cs.algs4.StdDraw;

public class Mouse {
    protected double mouseX, mouseY;
    public Mouse() {

    }

    public void updateMousePosition() {
        this.mouseX = StdDraw.mouseX();
        this.mouseY = StdDraw.mouseY();
    }
}
