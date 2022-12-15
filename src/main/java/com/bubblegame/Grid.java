package com.bubblegame;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import static com.bubblegame.Bubble.R;
import static com.bubblegame.Util.distance;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;

public class Grid {

    public static final int numberOfColumns = 15;
    public static final int numberOfRows = 15;
    private final List<Cell> cells;
    private int shiftCounter = 0;

    public Grid() {
        cells = new ArrayList<>(numberOfRows * numberOfColumns);
        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfColumns; j++) {
                int beginning = (i % 2) == 0 ? 0 : Bubble.R;
                cells.add(new Cell(beginning + j * (2 * Bubble.R), i * Bubble.R * (int) round(sqrt(3))));
            }
        }
    }

    public void attachBubble(Bubble bubble) {
        try {
            findTheNearestCell(bubble.getMiddle()).setBubble(bubble);
        } catch (NoSuchElementException exception) {
            System.out.println("Error");
        }
    }

    public void removeBubble(Bubble bubble) throws NoSuchElementException {
        Cell cellContainingBubble =
                cells.stream()
                        .filter(cell -> cell.getBubble() == bubble)
                        .findAny()
                        .orElseThrow(NoSuchElementException::new);

        cellContainingBubble.setBubble(null);
    }

    private Cell findTheNearestCell(Point2D point) throws NoSuchElementException {
        return cells.stream()
                .min(Comparator.comparing(cell -> distance(cell.getMiddle(), point)))
                .orElseThrow(NoSuchElementException::new);
    }

    public List<Bubble> getBubbleNeighboursHavingSameColor(Bubble bubble) {
        return getBubbleNeighbours(bubble).stream()
                .filter(b -> b.getColor() == bubble.getColor() && b != bubble)
                .toList();
    }

    public List<Bubble> getBubbleNeighbours(Bubble bubble) {
        Cell bubbleCell = getCellOfBubble(bubble);
        Rectangle2D CellRect =
                new Rectangle2D.Double(
                        bubbleCell.getX() - 0.1, bubbleCell.getY() - 0.1, 2 * R + 0.2, 2 * R + 0.2);
        return cells.stream()
                .filter(c -> CellRect.intersects(c.getX(), c.getY(), 2 * R, 2 * R) && c.getBubble() != null)
                .map(Cell::getBubble)
                .toList();
    }

    private Cell getCellOfBubble(Bubble bubble) throws NoSuchElementException {
        return cells.stream()
                .filter(cell -> cell.getBubble() == bubble)
                .findAny()
                .orElseThrow(NoSuchElementException::new);
    }

    public boolean isInFirstRow(Bubble bubble) {
        Cell cell = getCellOfBubble(bubble);
        return cells.indexOf(cell) < numberOfColumns;
    }

    public void addRow(List<Bubble> bubbles) {
        for (int i = numberOfRows - 1; i > 0; i--) {
            for (int j = 0; j < numberOfColumns; j++) {
                Cell cell = cells.get(i * numberOfColumns + j);
                if (i % 2 == 0) {
                    cell.shiftX(shiftCounter % 2 == 0 ? +1 : -1);
                } else {
                    cell.shiftX(shiftCounter % 2 == 0 ? -1 : +1);
                }
                cell.setBubble(cells.get((i - 1) * numberOfColumns + j).getBubble());
            }
        }
        for (int j = 0; j < numberOfColumns; j++) {
            Cell cell = cells.get(j);
            cell.shiftX(shiftCounter % 2 == 0 ? +1 : -1);
            cell.setBubble(bubbles.get(j));
        }
        shiftCounter++;
    }
}
