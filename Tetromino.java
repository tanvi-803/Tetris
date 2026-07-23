import java.awt.Color;


//   Represents a single Tetris piece (tetromino).
//   Stores the 4x4 shape grid, position on the board, type, and color.
 
public class Tetromino {

    public static final int BLOCK_SIZE = 4;

    // All 7 standard tetromino shapes
    public static final int[][][] SHAPES = {
        {{0,0,0,0}, {1,1,1,1}, {0,0,0,0}, {0,0,0,0}}, // 0 - I
        {{0,0,0,0}, {0,1,1,0}, {0,1,1,0}, {0,0,0,0}}, // 1 - O
        {{0,0,0,0}, {0,1,0,0}, {1,1,1,0}, {0,0,0,0}}, // 2 - T
        {{0,0,0,0}, {0,1,1,0}, {1,1,0,0}, {0,0,0,0}}, // 3 - S
        {{0,0,0,0}, {1,1,0,0}, {0,1,1,0}, {0,0,0,0}}, // 4 - Z
        {{0,0,0,0}, {1,0,0,0}, {1,1,1,0}, {0,0,0,0}}, // 5 - J
        {{0,0,0,0}, {0,0,1,0}, {1,1,1,0}, {0,0,0,0}}  // 6 - L
    };

    // Colours
    public static final Color[] COLORS = {
        new Color(0,   220, 220), // 0 - I : Cyan
        new Color(240, 220,   0), // 1 - O : Yellow
        new Color(180,   0, 220), // 2 - T : Magenta
        new Color(0,   200,  50), // 3 - S : Green
        new Color(220,  30,  30), // 4 - Z : Red
        new Color(30,   80, 220), // 5 - J : Blue
        new Color(230, 120,   0), // 6 - L : Orange
    };

    public int[][] shape;  // 4x4 grid for this piece
    public int x, y;       // Position on the game grid (column, row)
    public int type;       // Piece type index (0-6)
    public Color color;    // Rendering color

    //  Creates a random tetromino positioned at the top-center of the board.
     
    public Tetromino(int boardWidth) {
        this.type  = (int)(Math.random() * 7);
        this.shape = copyShape(SHAPES[type]);
        this.color = COLORS[type];
        this.x     = boardWidth / 2 - BLOCK_SIZE / 2;
        this.y     = 0;
    }

    //  Copy constructor — deep-copies another Tetromino.
    
    public Tetromino(Tetromino other) {
        this.type  = other.type;
        this.shape = copyShape(other.shape);
        this.color = other.color;
        this.x     = other.x;
        this.y     = other.y;
    }

    // Rotates the piece 90 degrees clockwise (in-place).
     
    public void rotate() {
        int[][] temp = new int[BLOCK_SIZE][BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i++)
            for (int j = 0; j < BLOCK_SIZE; j++)
                temp[j][BLOCK_SIZE - 1 - i] = shape[i][j];
        shape = temp;
    }

    // Deep-copies a 2D shape array. 
    private static int[][] copyShape(int[][] src) {
        int[][] dst = new int[BLOCK_SIZE][BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i++)
            dst[i] = src[i].clone();
        return dst;
    }
}
