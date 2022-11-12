package com.bubblegame;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.*;

import static com.bubblegame.Util.distance;

public class Game {

  public static final Point2D initialPoint =
      new Point2D.Double(BubbleGameApp.WIDTH / 2.0, BubbleGameApp.HEIGHT - 4 * Bubble.R);
  private static final int initialNumberOfRows = 5;
  private final List<Bubble> bubbles;
  private final Grid grid;
  private Bubble nextBubble;
  private Bubble movingBubble;
  private boolean isMoving = false;
  private int shootingCounter = 0;

  public Game() {
    bubbles = new ArrayList<>();
    grid = new Grid();
    for (int i = 0; i < initialNumberOfRows; i++) {
      addRowOfBubbles();
    }
    nextBubble = new Bubble();
    nextBubble.setInitialPoint();
  }

  public synchronized void paintBubbles(Graphics g) {
    bubbles.forEach(b -> b.paint(g));
    if (isMoving) movingBubble.paint(g);
    nextBubble.paint(g);
  }

  public void shootBubble(double angle) {
    movingBubble = Bubble.copyOf(nextBubble);
    nextBubble = new Bubble();
    nextBubble.setInitialPoint();
    setMovingBubbleDirection(angle);
    movingBubbleAction();
  }

  private void attachMovingBubbleToGrid() {
    bubbles.add(movingBubble);
    grid.attachBubble(movingBubble);
  }

  private synchronized void removeBubblesFromGrid(List<Bubble> bubblesToRemove) {
    bubbles.removeAll(bubblesToRemove);
    for (Bubble bubble : bubblesToRemove) {
      try {
        grid.removeBubble(bubble);
      } catch (NoSuchElementException e) {
        System.out.println("Error");
      }
    }
  }

  private boolean checkIntersects() {
    return bubbles.stream()
        .anyMatch(bubble -> distance(bubble.getMiddle(), movingBubble.getMiddle()) <= 2 * Bubble.R);
  }

  private void setMovingBubbleDirection(double angle) {
    movingBubble.setAngle(angle);
  }

  private void movingBubbleAction() {
    isMoving = true;
    new Timer()
        .schedule(
            new TimerTask() {
              @Override
              public void run() {
                movingBubble.move();
                if (checkIntersects()) {
                  cancel();
                  updateGrid();
                  isMoving = false;
                }
              }
            },
            0,
            20);
  }

  private void updateGrid() {
    attachMovingBubbleToGrid();
    List<Bubble> connectedBubbles = findAllConnectedBubblesHavingSameColor();
    if (connectedBubbles.size() >= 3) {
      removeBubblesFromGrid(connectedBubbles);
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
    System.out.println(connectedBubbles.size());
    return connectedBubbles;
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
