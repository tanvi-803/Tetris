import java.util.ArrayDeque;
import java.util.Deque;

// Core Tetris game logic.
// Manages the board grid, active piece, held piece, next-piece queue,
// scoring, and all game-state transitions

public class TetrisGame {

    public static final int WIDTH  = 30; // Board columns
    public static final int HEIGHT = 30; // Board rows    

    // Board state: 0 = empty, 1 = filled
    public final int[][]     grid;
    // Per-cell color index (maps to Tetromino.COLORS)
    public final int[][]     colorGrid;

    public  Tetromino         currentPiece;
    public  final Deque<Tetromino> nextPieces = new ArrayDeque<>();
    public  Tetromino         heldPiece  = null;

    private boolean  canHold   = true;
    public  int      score     = 0;
    public  boolean  paused    = false;
    public  boolean  gameOver  = false;
    public  boolean  quit      = false;

    // Number of lines cleared (used to calculate score display). 
    public  int      linesCleared = 0;

    public TetrisGame() {
        grid      = new int[HEIGHT][WIDTH];
        colorGrid = new int[HEIGHT][WIDTH];
        refillNextQueue();
        spawnPiece();
    }

    // Public game-state mutators (called from the Swing key listener)    

    // Move current piece left
    public void moveLeft()  { tryMove(currentPiece.x - 1, currentPiece.y); }

    // Move current piece right
    public void moveRight() { tryMove(currentPiece.x + 1, currentPiece.y); }

    // Soft-drop: move piece down one row
    public void softDrop()  { tryMove(currentPiece.x, currentPiece.y + 1); }

    // Rotate piece clockwise; reject if invalid
    public void rotate() {
        Tetromino temp = new Tetromino(currentPiece);
        temp.rotate();
        if (isValidMove(temp, temp.x, temp.y))
            currentPiece.rotate();
    }

    // Hard-drop: instantly place piece at lowest valid row.
    public void hardDrop() {
        currentPiece.y += ghostDropDistance();
        mergePiece();
        clearLines();
        spawnPiece();
    }

    // Hold the current piece (swap or store). 
    public void holdPiece() {
        if (!canHold) return;
        if (heldPiece == null) {
            heldPiece = new Tetromino(currentPiece);
            resetHeldPosition(heldPiece);
            spawnPiece();
        } else {
            Tetromino temp = heldPiece;
            heldPiece = new Tetromino(currentPiece);
            resetHeldPosition(heldPiece);
            currentPiece = temp;
            currentPiece.x = WIDTH / 2 - Tetromino.BLOCK_SIZE / 2;
            currentPiece.y = 0;
        }
        canHold = false;
    }

    // Gravity tick — called by the Swing Timer every ~200 ms

    // Advances the game by one gravity step.
    // Returns false when the game is over.
    
    public boolean tick() {
        if (quit || gameOver) return false;
        if (paused) return true;

        if (isValidMove(currentPiece, currentPiece.x, currentPiece.y + 1)) {
            currentPiece.y++;
        } else {
            mergePiece();
            clearLines();
            spawnPiece();
            if (gameOver) return false;
        }
        return true;
    }

    // Ghost piece (drop shadow)

    // Returns how many rows the current piece can still fall.
    // Used to render the ghost piece.
    public int ghostDropDistance() {
        int d = 0;
        while (isValidMove(currentPiece, currentPiece.x, currentPiece.y + d + 1))
            d++;
        return d;
    }

    // Internal helpers

    private void tryMove(int newX, int newY) {
        if (isValidMove(currentPiece, newX, newY)) {
            currentPiece.x = newX;
            currentPiece.y = newY;
        }
    }

    // Checks whether placing {@code piece} at (nx, ny) is a legal position.
     
    private boolean isValidMove(Tetromino piece, int nx, int ny) {
        for (int i = 0; i < Tetromino.BLOCK_SIZE; i++) {
            for (int j = 0; j < Tetromino.BLOCK_SIZE; j++) {
                if (piece.shape[i][j] != 0) {
                    int gx = nx + j;
                    int gy = ny + i;
                    if (gx < 0 || gx >= WIDTH || gy >= HEIGHT)
                        return false;
                    if (gy >= 0 && grid[gy][gx] != 0)
                        return false;
                }
            }
        }
        return true;
    }

    // Stamps the current piece into the permanent grid. 
    private void mergePiece() {
        for (int i = 0; i < Tetromino.BLOCK_SIZE; i++) {
            for (int j = 0; j < Tetromino.BLOCK_SIZE; j++) {
                if (currentPiece.shape[i][j] != 0) {
                    int gy = currentPiece.y + i;
                    int gx = currentPiece.x + j;
                    if (gy >= 0 && gy < HEIGHT && gx >= 0 && gx < WIDTH) {
                        grid[gy][gx]      = 1;
                        colorGrid[gy][gx] = currentPiece.type;
                    }
                }
            }
        }
    }

    // Scans for completed rows, clears them, and updates score. 
    private void clearLines() {
        int cleared = 0;
        for (int i = HEIGHT - 1; i >= 0; i--) {
            boolean full = true;
            for (int j = 0; j < WIDTH; j++) {
                if (grid[i][j] == 0) { full = false; break; }
            }
            if (full) {
                // Shift every row above down by one
                for (int row = i; row > 0; row--) {
                    grid[row]      = grid[row - 1].clone();
                    colorGrid[row] = colorGrid[row - 1].clone();
                }
                grid[0]      = new int[WIDTH];
                colorGrid[0] = new int[WIDTH];
                i++;        // Re-check same index after shift
                cleared++;
            }
        }
        // Score table: 1→100, 2→300, 3→500, 4→800 (classic Tetris scoring)
        int[] bonuses = {0, 100, 300, 500, 800};
        if (cleared > 0 && cleared <= 4)
            score += bonuses[cleared];
        linesCleared += cleared;
    }

    // Dequeues the next piece and checks for game-over. 
    private void spawnPiece() {
        if (nextPieces.isEmpty()) refillNextQueue();
        currentPiece = nextPieces.poll();
        refillNextQueue();
        currentPiece.x = WIDTH / 2 - Tetromino.BLOCK_SIZE / 2;
        currentPiece.y = 0;
        canHold = true;

        // If the new piece immediately overlaps, it's game over
        if (!isValidMove(currentPiece, currentPiece.x, currentPiece.y))
            gameOver = true;
    }

    // Keeps the next-piece queue at 3 pieces. 
    private void refillNextQueue() {
        while (nextPieces.size() < 3)
            nextPieces.add(new Tetromino(WIDTH));
    }

    // Resets a piece's position to top-center (for the held slot). 
    private void resetHeldPosition(Tetromino t) {
        t.x = WIDTH / 2 - Tetromino.BLOCK_SIZE / 2;
        t.y = 0;
    }
}
