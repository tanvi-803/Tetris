import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.ArrayList;

/**
 * Swing rendering panel for the Tetris game.
 *
 * Layout (pixels):
 *   Left sidebar  (180 px) | Board (720 px) | Right sidebar (220 px)
 *
 * The board cell size is CELL px × CELL px.
 * Total window width  = 180 + 720 + 220 = 1120 px
 * Total window height = 720 + top/bottom padding  ≈ 780 px
 */
public class TetrisPanel extends JPanel {

    // -----------------------------------------------------------------------
    // Layout constants
    // -----------------------------------------------------------------------
    static final int CELL         = 24;          // pixels per grid cell
    static final int BOARD_W      = TetrisGame.WIDTH  * CELL;  // 720
    static final int BOARD_H      = TetrisGame.HEIGHT * CELL;  // 720
    static final int LEFT_W       = 180;
    static final int RIGHT_W      = 220;
    static final int V_PAD        = 30;          // top/bottom padding

    static final int PANEL_W = LEFT_W + BOARD_W + RIGHT_W;
    static final int PANEL_H = BOARD_H + V_PAD * 2;

    // -----------------------------------------------------------------------
    // Colours
    // -----------------------------------------------------------------------
    private static final Color BG          = new Color(10,  10,  25);
    private static final Color GRID_LINE   = new Color(30,  30,  55);
    private static final Color BORDER_COL  = new Color(80, 100, 200);
    private static final Color GHOST_COL   = new Color(255,255,255, 50);
    private static final Color PANEL_BG    = new Color(18,  18,  40);
    private static final Color TEXT_HEAD   = new Color(200,210,255);
    private static final Color TEXT_BODY   = new Color(150,160,210);
    private static final Color ACCENT      = new Color(80, 120, 255);

    // -----------------------------------------------------------------------
    // Fonts (loaded lazily the first time paint is called)
    // -----------------------------------------------------------------------
    private Font fontTitle;
    private Font fontLabel;
    private Font fontMono;

    private final TetrisGame game;

    public TetrisPanel(TetrisGame game) {
        this.game = game;
        setPreferredSize(new Dimension(PANEL_W, PANEL_H));
        setBackground(BG);
        setFocusable(true);

        // Try to load a clean system font; fall back to plain defaults
        try {
            fontTitle = new Font("SansSerif", Font.BOLD,  14);
            fontLabel = new Font("SansSerif", Font.PLAIN, 12);
            fontMono  = new Font("Monospaced", Font.BOLD, 13);
        } catch (Exception e) {
            fontTitle = getFont().deriveFont(Font.BOLD, 14f);
            fontLabel = getFont().deriveFont(Font.PLAIN, 12f);
            fontMono  = getFont().deriveFont(Font.BOLD, 13f);
        }
    }

    // -----------------------------------------------------------------------
    // Main paint entry point
    // -----------------------------------------------------------------------
    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Fill background
        g.setColor(BG);
        g.fillRect(0, 0, PANEL_W, PANEL_H);

        int boardX = LEFT_W;          // pixel x of board left edge
        int boardY = V_PAD;           // pixel y of board top edge

        drawLeftSidebar(g, boardX, boardY);
        drawBoard(g, boardX, boardY);
        drawRightSidebar(g, boardX + BOARD_W, boardY);

        if (game.paused)    drawPauseOverlay(g, boardX, boardY);
        if (game.gameOver)  drawGameOverOverlay(g, boardX, boardY);
    }

    // -----------------------------------------------------------------------
    // Left sidebar: Score + Held piece
    // -----------------------------------------------------------------------
    private void drawLeftSidebar(Graphics2D g, int boardX, int boardY) {
        int x = 0, y = boardY;

        // Sidebar background
        g.setColor(PANEL_BG);
        g.fillRect(x, 0, LEFT_W, PANEL_H);

        // ---- Score card ----
        drawCard(g, x + 10, y + 10, LEFT_W - 20, 90);
        g.setFont(fontTitle);
        g.setColor(ACCENT);
        g.drawString("SCORE", x + 20, y + 35);
        g.setFont(new Font("Monospaced", Font.BOLD, 20));
        g.setColor(TEXT_HEAD);
        g.drawString(String.valueOf(game.score), x + 20, y + 65);

        // ---- Lines cleared ----
        drawCard(g, x + 10, y + 115, LEFT_W - 20, 70);
        g.setFont(fontTitle);
        g.setColor(ACCENT);
        g.drawString("LINES", x + 20, y + 140);
        g.setFont(new Font("Monospaced", Font.BOLD, 18));
        g.setColor(TEXT_HEAD);
        g.drawString(String.valueOf(game.linesCleared), x + 20, y + 165);

        // ---- Held piece ----
        drawCard(g, x + 10, y + 200, LEFT_W - 20, 120);
        g.setFont(fontTitle);
        g.setColor(ACCENT);
        g.drawString("HOLD", x + 20, y + 225);

        if (game.heldPiece != null) {
            drawMiniPiece(g, game.heldPiece, x + 20, y + 235, 18);
        } else {
            g.setFont(fontLabel);
            g.setColor(TEXT_BODY);
            g.drawString("(empty)", x + 20, y + 260);
        }
    }

    // -----------------------------------------------------------------------
    // Right sidebar: Next pieces + Controls
    // -----------------------------------------------------------------------
    private void drawRightSidebar(Graphics2D g, int startX, int boardY) {
        int x = startX;
        int y = boardY;

        // Sidebar background
        g.setColor(PANEL_BG);
        g.fillRect(x, 0, RIGHT_W, PANEL_H);

        // ---- Next pieces ----
        drawCard(g, x + 10, y + 10, RIGHT_W - 20, 310);
        g.setFont(fontTitle);
        g.setColor(ACCENT);
        g.drawString("NEXT", x + 20, y + 35);

        List<Tetromino> nextList = new ArrayList<>(game.nextPieces);
        for (int p = 0; p < Math.min(3, nextList.size()); p++) {
            drawMiniPiece(g, nextList.get(p), x + 20, y + 45 + p * 85, 18);
        }

        // ---- Controls ----
        drawCard(g, x + 10, y + 335, RIGHT_W - 20, 280);
        g.setFont(fontTitle);
        g.setColor(ACCENT);
        g.drawString("CONTROLS", x + 20, y + 358);

        String[][] controls = {
            {"A / ←",   "Move Left"},
            {"D / →",   "Move Right"},
            {"S / ↓",   "Soft Drop"},
            {"W / ↑",   "Rotate"},
            {"H",       "Hard Drop"},
            {"G",       "Hold Piece"},
            {"P",       "Pause"},
            {"R",       "Resume"},
            {"Q",       "Quit"}
        };
        int cy = y + 378;
        for (String[] row : controls) {
            g.setFont(fontMono);
            g.setColor(TEXT_HEAD);
            g.drawString(row[0], x + 20, cy);
            g.setFont(fontLabel);
            g.setColor(TEXT_BODY);
            g.drawString(row[1], x + 75, cy);
            cy += 24;
        }
    }

    // -----------------------------------------------------------------------
    // Board
    // -----------------------------------------------------------------------
    private void drawBoard(Graphics2D g, int bx, int by) {
        // --- Grid background ---
        g.setColor(new Color(15, 15, 35));
        g.fillRect(bx, by, BOARD_W, BOARD_H);

        // --- Grid lines ---
        g.setColor(GRID_LINE);
        for (int c = 0; c <= TetrisGame.WIDTH; c++)
            g.drawLine(bx + c * CELL, by, bx + c * CELL, by + BOARD_H);
        for (int r = 0; r <= TetrisGame.HEIGHT; r++)
            g.drawLine(bx, by + r * CELL, bx + BOARD_W, by + r * CELL);

        // --- Placed blocks ---
        for (int r = 0; r < TetrisGame.HEIGHT; r++) {
            for (int c = 0; c < TetrisGame.WIDTH; c++) {
                if (game.grid[r][c] != 0) {
                    int colorIdx = game.colorGrid[r][c];
                    drawBlock(g, bx + c * CELL, by + r * CELL, CELL,
                              Tetromino.COLORS[colorIdx], false);
                }
            }
        }

        // --- Ghost piece ---
        int ghostDrop = game.ghostDropDistance();
        Tetromino cur = game.currentPiece;
        if (ghostDrop > 0) {
            for (int i = 0; i < Tetromino.BLOCK_SIZE; i++) {
                for (int j = 0; j < Tetromino.BLOCK_SIZE; j++) {
                    if (cur.shape[i][j] != 0) {
                        int gr = cur.y + ghostDrop + i;
                        int gc = cur.x + j;
                        if (gr >= 0 && gr < TetrisGame.HEIGHT && gc >= 0 && gc < TetrisGame.WIDTH) {
                            g.setColor(GHOST_COL);
                            g.fillRect(bx + gc * CELL + 1, by + gr * CELL + 1, CELL - 2, CELL - 2);
                            g.setColor(new Color(255,255,255,100));
                            g.drawRect(bx + gc * CELL + 1, by + gr * CELL + 1, CELL - 2, CELL - 2);
                        }
                    }
                }
            }
        }

        // --- Current (active) piece ---
        for (int i = 0; i < Tetromino.BLOCK_SIZE; i++) {
            for (int j = 0; j < Tetromino.BLOCK_SIZE; j++) {
                if (cur.shape[i][j] != 0) {
                    int pr = cur.y + i;
                    int pc = cur.x + j;
                    if (pr >= 0 && pr < TetrisGame.HEIGHT && pc >= 0 && pc < TetrisGame.WIDTH)
                        drawBlock(g, bx + pc * CELL, by + pr * CELL, CELL, cur.color, true);
                }
            }
        }

        // --- Border ---
        g.setColor(BORDER_COL);
        g.setStroke(new BasicStroke(2f));
        g.drawRect(bx, by, BOARD_W, BOARD_H);
        g.setStroke(new BasicStroke(1f));
    }

    // -----------------------------------------------------------------------
    // Overlays
    // -----------------------------------------------------------------------
    private void drawPauseOverlay(Graphics2D g, int bx, int by) {
        // Semi-transparent dark veil
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(bx, by, BOARD_W, BOARD_H);

        g.setFont(new Font("SansSerif", Font.BOLD, 36));
        g.setColor(new Color(200, 220, 255));
        drawCenteredString(g, "PAUSED", bx, by, BOARD_W, BOARD_H / 2 - 20);

        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        g.setColor(TEXT_BODY);
        drawCenteredString(g, "Press  R  to Resume", bx, by, BOARD_W, BOARD_H / 2 + 20);
    }

    private void drawGameOverOverlay(Graphics2D g, int bx, int by) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(bx, by, BOARD_W, BOARD_H);

        g.setFont(new Font("SansSerif", Font.BOLD, 40));
        g.setColor(new Color(255, 80, 80));
        drawCenteredString(g, "GAME OVER", bx, by, BOARD_W, BOARD_H / 2 - 40);

        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.setColor(TEXT_HEAD);
        drawCenteredString(g, "Score: " + game.score, bx, by, BOARD_W, BOARD_H / 2 + 10);

        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.setColor(TEXT_BODY);
        drawCenteredString(g, "Close the window to exit", bx, by, BOARD_W, BOARD_H / 2 + 50);
    }

    // -----------------------------------------------------------------------
    // Drawing helpers
    // -----------------------------------------------------------------------

    /**
     * Draws a single tetris block with gradient and highlight effect.
     */
    private void drawBlock(Graphics2D g, int px, int py, int size, Color base, boolean active) {
        // Main fill
        GradientPaint grad = new GradientPaint(
            px, py,                base.brighter(),
            px + size, py + size,  base.darker().darker()
        );
        g.setPaint(grad);
        g.fillRect(px + 1, py + 1, size - 2, size - 2);

        // Top/left highlight
        g.setColor(new Color(255, 255, 255, active ? 80 : 40));
        g.fillRect(px + 1, py + 1, size - 2, 3);
        g.fillRect(px + 1, py + 1, 3, size - 2);

        // Dark border
        g.setColor(base.darker().darker());
        g.drawRect(px, py, size - 1, size - 1);
    }

    /**
     * Draws a small (mini) tetromino preview in a sidebar slot.
     */
    private void drawMiniPiece(Graphics2D g, Tetromino piece, int px, int py, int cellSize) {
        for (int i = 0; i < Tetromino.BLOCK_SIZE; i++) {
            for (int j = 0; j < Tetromino.BLOCK_SIZE; j++) {
                if (piece.shape[i][j] != 0) {
                    int bx = px + j * cellSize;
                    int by = py + i * cellSize;
                    drawBlock(g, bx, by, cellSize, piece.color, false);
                }
            }
        }
    }

    /** Draws a rounded-rectangle card background for a sidebar section. */
    private void drawCard(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(new Color(25, 25, 55));
        g.fill(new RoundRectangle2D.Float(x, y, w, h, 12, 12));
        g.setColor(new Color(50, 60, 120));
        g.draw(new RoundRectangle2D.Float(x, y, w, h, 12, 12));
    }

    /** Horizontally-centres a string within a given region. */
    private void drawCenteredString(Graphics2D g, String text,
                                    int rx, int ry, int rw, int yBaseline) {
        FontMetrics fm = g.getFontMetrics();
        int tx = rx + (rw - fm.stringWidth(text)) / 2;
        g.drawString(text, tx, ry + yBaseline);
    }
}
