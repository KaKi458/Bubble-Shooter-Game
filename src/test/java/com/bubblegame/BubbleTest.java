package com.bubblegame;

import org.junit.jupiter.api.Test;

public class BubbleTest {

  @Test
  void setInvalidAngle() {
    Bubble bubble = new Bubble();
    bubble.setAngle(600);
  }
}
