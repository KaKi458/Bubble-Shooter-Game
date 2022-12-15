package com.bubblegame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Timer;
import java.util.TimerTask;

public class BubbleGameApp extends JFrame {

    public static int HEIGHT = Grid.numberOfRows * 2 * Bubble.R + 2 * Bubble.R;
    public static int WIDTH = Grid.numberOfColumns * 2 * Bubble.R + 2 * Bubble.R;
    private final Game game;
    private double angle;

    public BubbleGameApp() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(100, 100, WIDTH, HEIGHT);
        setResizable(false);

        game = new Game();
        JPanel gamePanel =
                new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        game.paintBubbles(g);
                    }
                };
        setBackground(Color.WHITE);
        add(gamePanel);
        repaint();
        addMouseMotionListener(new GameMouseMotionListener());
        addMouseListener(new GameMouseListener());
        setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(BubbleGameApp::new);
    }

    private class GameMouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if (game.isMoving()) return;
            game.shootBubble(angle);
            new Timer()
                    .schedule(
                            new TimerTask() {
                                @Override
                                public void run() {
                                    repaint();
                                    if (!game.isMoving()) {
                                        cancel();
                                    }
                                }
                            },
                            0,
                            15);
        }
    }

    private class GameMouseMotionListener extends MouseMotionAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {
            double x = e.getX() - Game.initialPoint.getX();
            double y = Math.abs(Game.initialPoint.getY() - e.getY());
            angle = Math.atan2(y, x);
        }
    }
}
