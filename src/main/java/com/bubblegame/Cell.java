package com.bubblegame;

import java.awt.geom.Point2D;

import static com.bubblegame.Bubble.R;

public class Cell {
    private int x;
    private final int y;
    private Bubble bubble;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        bubble = null;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Bubble getBubble() {
        return bubble;
    }

    public void setBubble(Bubble bubble) {
        this.bubble = bubble;
        if (bubble != null) bubble.setMiddlePoint(getMiddle());
    }

    public void shiftX(int direction) {
        x += direction * R;
//        if(bubble != null) bubble.shiftX(direction);
    }

    public Point2D getMiddle() {
        return new Point2D.Double(x + R, y + R);
    }
}
