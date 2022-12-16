package com.bubblegame;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.*;
import java.util.concurrent.Semaphore;

import static com.bubblegame.Util.distance;

public class Game extends Thread {

    public static final Point2D initialPoint =
            new Point2D.Double(BubbleGameApp.WIDTH / 2.0, BubbleGameApp.HEIGHT - 4 * Bubble.R);
    private static final int initialNumberOfRows = 5;
    private final List<Bubble> bubbles;
    private final Grid grid;
    private Bubble nextBubble;
    private Bubble movingBubble;
    private boolean isMoving = false;
    private final Semaphore semaphore;
    private int shootingCounter = 0;

    public Game() {
        bubbles = new ArrayList<>();
        grid = new Grid();
        for (int i = 0; i < initialNumberOfRows; i++) {
            addRowOfBubbles();
        }
        nextBubble = new Bubble();
        nextBubble.setInitialPoint();
        semaphore = new Semaphore(1);
    }

    public void paintBubbles(Graphics g) throws InterruptedException {
        semaphore.acquire();
        for (Bubble bubble : bubbles) {
            bubble.paint(g);
        }
        if (isMoving) movingBubble.paint(g);
        nextBubble.paint(g);
        semaphore.release();
    }

    public void shootBubble(double angle) {
        movingBubble = Bubble.copyOf(nextBubble);
        nextBubble = new Bubble();
        nextBubble.setInitialPoint();
        setMovingBubbleDirection(angle);
        new MovingBubbleAction().run();
    }

    private void attachMovingBubbleToGrid() {
        bubbles.add(movingBubble);
        grid.attachBubble(movingBubble);
    }

    private void removeBubblesFromGrid(List<Bubble> bubblesToRemove) {
        for(Bubble removingBubble : bubblesToRemove) {
            try {
                semaphore.acquire();
                bubbles.remove(removingBubble);
                grid.removeBubble(removingBubble);
                semaphore.release();
                sleep(50);
            } catch (NoSuchElementException e) {
                System.out.println("Error");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean checkRebounds() {
        double movingBubbleMiddleX = movingBubble.getMiddle().getX();
        return movingBubbleMiddleX - Bubble.R <= 0 || movingBubbleMiddleX + Bubble.R >= BubbleGameApp.WIDTH;
    }

    private boolean checkIntersects() {
        return bubbles.stream()
                .anyMatch(bubble ->
                        distance(bubble.getMiddle(), movingBubble.getMiddle()) <= 2 * Bubble.R);
    }

    private boolean checkTopOfGrid() {
        return movingBubble.getMiddle().getY() <= 0;
    }

    private void setMovingBubbleDirection(double angle) {
        movingBubble.setAngle(angle);
    }

    private class MovingBubbleAction implements Runnable {
        @Override
        public void run() {
            isMoving = true;
            new Timer().schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            movingBubble.move();
                            if (checkRebounds()) {
                                movingBubble.changeXDirection();
                            }
                            if (checkIntersects() || checkTopOfGrid()) {
                                cancel();
                                updateGrid();
                                isMoving = false;
                            }
                        }
                    }, 0, 20);

        }
    }

    private synchronized void updateGrid() {
        attachMovingBubbleToGrid();
        List<Bubble> connectedBubbles = findAllConnectedBubblesHavingSameColor();
        if (connectedBubbles.size() >= 3) {
            removeBubblesFromGrid(connectedBubbles);
            removeSeparateBubbles(findAllConnectedAreasOfBubbles());
        } else {
            shootingCounter++;
            if (shootingCounter >= 5) {
                shootingCounter = 0;
                addRowOfBubbles();
            }
        }
    }

    private List<Bubble> findAllConnectedBubblesHavingSameColor() {
        List<Bubble> connectedBubbles = new ArrayList<>();
        connectedBubbles.add(movingBubble);
        Stack<Bubble> bubbleStack = new Stack<>();
        bubbleStack.push(movingBubble);
        while (!bubbleStack.empty()) {
            Bubble tempBubble = bubbleStack.pop();
            for (Bubble b : grid.getBubbleNeighboursHavingSameColor(tempBubble)) {
                if (!connectedBubbles.contains(b)) {
                    connectedBubbles.add(b);
                    bubbleStack.push(b);
                }
            }
        }
//    System.out.println(connectedBubbles.size());
        return connectedBubbles;
    }

    private List<List<Bubble>> findAllConnectedAreasOfBubbles() {
        List<List<Bubble>> listOfAreas = new ArrayList<>();
        List<Bubble> bubblesAlreadyConsidered = new ArrayList<>();
        for (Bubble bubble : bubbles) {
            if (bubblesAlreadyConsidered.contains(bubble)) continue;
            List<Bubble> listOfBubblesInOneArea = new ArrayList<>();
            Stack<Bubble> bubbleStack = new Stack<>();
            bubbleStack.push(bubble);
            while (!bubbleStack.empty()) {
                Bubble tempBubble = bubbleStack.pop();
                for (Bubble b : grid.getBubbleNeighbours(tempBubble)) {
                    if (!bubblesAlreadyConsidered.contains(b)) {
                        bubblesAlreadyConsidered.add(b);
                        listOfBubblesInOneArea.add(b);
                        bubbleStack.push(b);
                    }
                }
            }
            listOfAreas.add(listOfBubblesInOneArea);
        }
        System.out.println(listOfAreas.size());
        return listOfAreas;
    }

    private void removeSeparateBubbles(List<List<Bubble>> listOfAreas) {
        for (List<Bubble> list : listOfAreas) {
            if (list.stream().noneMatch(grid::isInFirstRow)) {
                removeBubblesFromGrid(list);
            }
        }
    }

    private void addRowOfBubbles() {
        List<Bubble> newBubbles = new ArrayList<>();
        for (int j = 0; j < Grid.numberOfColumns; j++) {
            newBubbles.add(new Bubble());
        }
        bubbles.addAll(newBubbles);
        grid.addRow(newBubbles);
    }

    public boolean isMoving() {
        return isMoving;
    }
}
