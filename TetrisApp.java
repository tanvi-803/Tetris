import javax.swing.*;
import java.awt.event.*;

/**
 * Entry point for the Java Tetris game.
 *
 * Creates the JFrame, wires up keyboard input, and drives the game loop
 * with a javax.swing.Timer (200 ms tick — matches the C++ timeout(200)).
 */
public class TetrisApp {

    public static void main(String[] args) {
        // Always create Swing components on the Event Dispatch Thread
        SwingUtilities.invokeLater(TetrisApp::startGame);
    }

    private static void startGame() {
        TetrisGame game  = new TetrisGame();
        TetrisPanel panel = new TetrisPanel(game);

        JFrame frame = new JFrame("Tetris");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null); // centre on screen
        frame.setVisible(true);
        panel.requestFocusInWindow();

        // -------------------------------------------------------------------
        // Keyboard input
        // -------------------------------------------------------------------
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (game.gameOver || game.quit) return;

                int kc = e.getKeyCode();
                char ch = Character.toLowerCase(e.getKeyChar());

                // Hard-drop and quit are processed even when paused? No — match C++ behaviour:
                // p/r toggle pause; all other movement is blocked while paused.

                if (ch == 'q' || kc == KeyEvent.VK_ESCAPE) {
                    game.quit = true;
                    frame.dispose();
                    return;
                }

                if (ch == 'p')               { game.paused = true;  panel.repaint(); return; }
                if (ch == 'r')               { game.paused = false; panel.repaint(); return; }
                if (game.paused)             return;  // block input while paused

                if (ch == 'a' || kc == KeyEvent.VK_LEFT)  game.moveLeft();
                if (ch == 'd' || kc == KeyEvent.VK_RIGHT) game.moveRight();
                if (ch == 's' || kc == KeyEvent.VK_DOWN)  game.softDrop();
                if (ch == 'w' || kc == KeyEvent.VK_UP)    game.rotate();
                if (ch == 'h' || kc == KeyEvent.VK_SPACE) game.hardDrop();
                if (ch == 'g')                             game.holdPiece();

                panel.repaint();
            }
        });

        // -------------------------------------------------------------------
        // Game loop — gravity tick every 200 ms (same as C++ timeout(200))
        // -------------------------------------------------------------------
        Timer timer = new Timer(200, null);
        timer.addActionListener(e -> {
            if (!game.tick()) {
                timer.stop();
            }
            panel.repaint();
        });
        timer.start();
    }
}
