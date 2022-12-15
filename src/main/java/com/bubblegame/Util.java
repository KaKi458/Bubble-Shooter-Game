package com.bubblegame;

import java.awt.geom.Point2D;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class Util {
    public static double distance(Point2D pointA, Point2D pointB) {
        return sqrt(pow(pointA.getX() - pointB.getX(), 2) + pow(pointA.getY() - pointB.getY(), 2));
    }
}
