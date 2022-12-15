package com.bubblegame;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Random;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Bubble {

    public static final int R = 15;
    private static final Color[] COLOR_SET = {
            Color.RED, Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW
    };
    private static final double dl = 8;

    private Color color;
    private double x, y;
    private double dx, dy;

    public Bubble() {
        color = getRandomColor();
    }

    public static Bubble copyOf(Bubble someBubble) {
        Bubble bubble = new Bubble();
        bubble.color = someBubble.color;
        bubble.x = someBubble.x;
        bubble.y = someBubble.y;
        return bubble;
    }

    public void setInitialPoint() {
        setMiddlePoint(Game.initialPoint);
    }

    public void setMiddlePoint(Point2D middlePoint) {
        x = middlePoint.getX() - R;
        y = middlePoint.getY() - R;
    }

    public Color getColor() {
        return color;
    }

    public void setAngle(double angle) {
        if (angle > 0 && angle < 180) {
            dx = dl * cos(angle);
            dy = -dl * sin(angle);
        } else throw new IllegalComponentStateException("Invalid angle");
    }

    private Color getRandomColor() {
        Random random = new Random();
        return COLOR_SET[random.nextInt(COLOR_SET.length)];
    }

    public Point2D.Double getMiddle() {
        return new Point2D.Double(x + R, y + R);
    }

    public void move() {
        x += dx;
        y += dy;
    }

    public void shiftX(int direction) {
        x += direction * R;
    }

    public void changeXDirection() {
        dx = -dx;
    }

    public void paint(Graphics g) {
        g.setColor(color);
        g.fillOval((int) x, (int) y, 2 * R, 2 * R);
    }
}
