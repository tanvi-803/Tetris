# 🎮 Tetris — Java

A fully-featured Tetris game built in Java using **Swing** for rendering. Features a dark premium UI with gradient block effects, ghost piece, hold system, and a 3-piece next queue.

---

## ✨ Features

- All 7 standard tetrominoes (I, O, T, S, Z, J, L)
- 👻 **Ghost piece** — shows exactly where the piece will land
- 🔒 **Hold piece** — swap the current piece with a stored one
- 👀 **Next 3 pieces** preview
- 💥 **Hard drop** — instantly place the piece at the bottom
- ⏸️ **Pause / Resume**
- 🏆 **Scoring** — classic multi-line clear bonuses (1→100, 2→300, 3→500, 4→800)
- 🎨 Dark premium UI with gradient block rendering and sidebars

---

## 🚀 How to Run

**Prerequisites:** Java JDK 8 or later → [Download here](https://adoptium.net)

```bash
# 1. Clone the repo
git clone https://github.com/<tanvi-803>/tetris.git
cd tetris

# 2. Compile
javac Tetromino.java TetrisGame.java TetrisPanel.java TetrisApp.java

# 3. Run
java TetrisApp
```

---

## 🎮 Controls

| Key | Action |
|-----|--------|
| `A` / `←` | Move Left |
| `D` / `→` | Move Right |
| `S` / `↓` | Soft Drop |
| `W` / `↑` | Rotate |
| `H` / `Space` | Hard Drop |
| `G` | Hold Piece |
| `P` | Pause |
| `R` | Resume |
| `Q` / `Esc` | Quit |

---

## 📁 Project Structure

```
tetris/
├── Tetromino.java    # Piece data: shapes, colors, rotation logic
├── TetrisGame.java   # Core game logic: grid, collision, scoring, hold, queue
├── TetrisPanel.java  # Swing rendering: board, sidebars, overlays
└── TetrisApp.java    # Entry point: JFrame + 200ms game loop timer
```

---

## 📜 License

MIT License — feel free to use, modify, and share.
