import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class GroupProject {
    private JFrame frame;
    private JButton[][] buttons = new JButton[8][8];
    private Piece[][] board = new Piece[8][8];
    private int selectedRow = -1;
    private int selectedCol = -1;
    private boolean isWhiteTurn = true;
    private SimpleAI ai = new SimpleAI(false);
    private boolean isSinglePlayer = true;


    public GroupProject() {
        frame = new JFrame("Chess");
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null); // manual layout
        showModeButtons();
        frame.setVisible(true);
    }
    private void showModeButtons() {
        JButton singleButton = new JButton("Singleplayer");
        singleButton.setBounds(250, 300, 300, 80);
        singleButton.setFont(new Font("Times New Roman", Font.BOLD, 27));

        JButton multiButton = new JButton("Play VS Friend");
        multiButton.setBounds(250, 400, 300, 80);
        multiButton.setFont(new Font("Times New Roman", Font.BOLD, 27));

        frame.add(singleButton);
        frame.add(multiButton);

        SingleListener single = new SingleListener(this, singleButton, multiButton);
        MultiListener multi = new MultiListener(this, singleButton, multiButton);

        singleButton.addActionListener(single);
        multiButton.addActionListener(multi);
    }
    class SingleListener implements ActionListener {
        private GroupProject game;
        private JButton b1, b2;

        public SingleListener(GroupProject game, JButton b1, JButton b2) {
            this.game = game;
            this.b1 = b1;
            this.b2 = b2;
        }

        public void actionPerformed(ActionEvent e) {
            game.isSinglePlayer = true;
            game.frame.remove(b1);
            game.frame.remove(b2);
            game.frame.setLayout(new GridLayout(8, 8));
            game.setupBoard();
            game.setupButtons();
            game.frame.revalidate();
            game.frame.repaint();
        }
    }

    class MultiListener implements ActionListener {
        private GroupProject game;
        private JButton b1, b2;

        public MultiListener(GroupProject game, JButton b1, JButton b2) {
            this.game = game;
            this.b1 = b1;
            this.b2 = b2;
        }

        public void actionPerformed(ActionEvent e) {
            game.isSinglePlayer = false;
            game.frame.remove(b1);
            game.frame.remove(b2);
            game.frame.setLayout(new GridLayout(8, 8));
            game.setupBoard();
            game.setupButtons();
            game.frame.revalidate();
            game.frame.repaint();
        }
    }



    private void setupBoard() {
        for (int col = 0; col < 8; col++) {
            board[1][col] = new Pawn(1, col, false);
            board[6][col] = new Pawn(6, col, true);
        }

        board[0][0] = new Rook(0, 0, false);
        board[0][7] = new Rook(0, 7, false);
        board[7][0] = new Rook(7, 0, true);
        board[7][7] = new Rook(7, 7, true);

        board[0][1] = new Knight(0, 1, false);
        board[0][6] = new Knight(0, 6, false);
        board[7][1] = new Knight(7, 1, true);
        board[7][6] = new Knight(7, 6, true);

        board[0][2] = new Bishop(0, 2, false);
        board[0][5] = new Bishop(0, 5, false);
        board[7][2] = new Bishop(7, 2, true);
        board[7][5] = new Bishop(7, 5, true);

        board[0][3] = new Queen(0, 3, false);
        board[7][3] = new Queen(7, 3, true);

        board[0][4] = new King(0, 4, false);
        board[7][4] = new King(7, 4, true);
    }

    private void setupButtons() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                CellButton button = new CellButton(r, c, this);
                button.setText(getPieceSymbol(board[r][c]));
                button.setFont(new Font("Arial", Font.PLAIN, 48));
                Color color;
                if ((r + c) % 2 == 0) {
                    color = new Color(241, 191, 191);
                } else {
                    color = new Color(181, 136, 99);
                }
                button.setBackground(color);
                buttons[r][c] = button;
                frame.add(button);
            }
        }
    }

    private String getPieceSymbol(Piece p) {
        if (p == null) {
            return "";
        }
        return p.symbol();
    }

    public void handleClick(int row, int col) {
        if (selectedRow == -1 && board[row][col] != null && board[row][col].isWhite == isWhiteTurn) {
            selectedRow = row;
            selectedCol = col;
        } else if (selectedRow != -1) {
            Piece selected = board[selectedRow][selectedCol];
            if (selected != null && selected.canMove(row, col, board) && selected.isWhite == isWhiteTurn) {
                board[row][col] = selected;
                selected.move(row, col);
                board[selectedRow][selectedCol] = null;
                updateButtons();
                isWhiteTurn = false;

                if (isSinglePlayer) {
                    ai.makeMove(board);
                    updateButtons();
                    isWhiteTurn = true;
                }
            }
            selectedRow = -1;
            selectedCol = -1;
        }
    }


    private void updateButtons() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                buttons[r][c].setText(getPieceSymbol(board[r][c]));
            }
        }
    }

    public static void main(String[] args) {
        new GroupProject();
    }
}

class Move {
    public int fromRow;
    public int fromCol;
    public int toRow;
    public int toCol;

    public Move(int fromRow, int fromCol, int toRow, int toCol) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
    }
}

class CellButton extends JButton implements ActionListener {
    private int row;
    private int col;
    private GroupProject game;

    public CellButton(int row, int col, GroupProject game) {
        this.row = row;
        this.col = col;
        this.game = game;
        addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        game.handleClick(row, col);
    }
}

class SimpleAI {
    private Piece[][] board;
    private boolean isWhiteAI;

    public SimpleAI(boolean isWhiteAI) {
        this.isWhiteAI = isWhiteAI;
    }

    public void makeMove(Piece[][] board) {
        this.board = board;
        ArrayList<Move> possibleMoves = new ArrayList<Move>();

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board[r][c];
                if (piece != null && piece.isWhite == isWhiteAI) {
                    for (int i = 0; i < 8; i++) {
                        for (int j = 0; j < 8; j++) {
                            if (piece.canMove(i, j, board)) {
                                possibleMoves.add(new Move(r, c, i, j));
                            }
                        }
                    }
                }
            }
        }

        if (possibleMoves.size() > 0) {
            Random rand = new Random();
            Move move = possibleMoves.get(rand.nextInt(possibleMoves.size()));
            Piece piece = board[move.fromRow][move.fromCol];
            board[move.toRow][move.toCol] = piece;
            piece.move(move.toRow, move.toCol);
            board[move.fromRow][move.fromCol] = null;
        }
    }
}

// === Pieces ===

abstract class Piece {
    protected int x, y;
    protected boolean isWhite;

    public Piece(int x, int y, boolean isWhite) {
        this.x = x;
        this.y = y;
        this.isWhite = isWhite;
    }

    public void move(int newX, int newY) {
        x = newX;
        y = newY;
    }

    public abstract boolean canMove(int newX, int newY, Piece[][] board);

    public abstract String symbol();
}

class Pawn extends Piece {
    private boolean hasMoved = false;

    public Pawn(int x, int y, boolean isWhite) {
        super(x, y, isWhite);
    }

    public boolean canMove(int newX, int newY, Piece[][] board) {
        int dir;
        if (isWhite) {
            dir = -1;
        } else {
            dir = 1;
        }

        if (!hasMoved && newX == x + 2 * dir && newY == y && board[newX][newY] == null) {
            return true;
        }

        if (newX == x + dir && newY == y && board[newX][newY] == null) {
            return true;
        }

        if (newX == x + dir && Math.abs(newY - y) == 1 && board[newX][newY] != null && board[newX][newY].isWhite != isWhite) {
            return true;
        }

        return false;
    }

    public void move(int newX, int newY) {
        super.move(newX, newY);
        hasMoved = true;
    }

    public String symbol() {
        if (isWhite) {
            return "♙";
        } else {
            return "♟";
        }
    }
}

class Rook extends Piece {
    public Rook(int x, int y, boolean isWhite) {
        super(x, y, isWhite);
    }

    public boolean canMove(int newX, int newY, Piece[][] board) {
        return newX == x || newY == y;
    }

    public String symbol() {
        if (isWhite) {
            return "♖";
        } else {
            return "♜";
        }
    }
}

class Knight extends Piece {
    public Knight(int x, int y, boolean isWhite) {
        super(x, y, isWhite);
    }

    public boolean canMove(int newX, int newY, Piece[][] board) {
        int dx = Math.abs(newX - x);
        int dy = Math.abs(newY - y);
        return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
    }

    public String symbol() {
        if (isWhite) {
            return "♘";
        } else {
            return "♞";
        }
    }
}

class Bishop extends Piece {
    public Bishop(int x, int y, boolean isWhite) {
        super(x, y, isWhite);
    }

    public boolean canMove(int newX, int newY, Piece[][] board) {
        return Math.abs(newX - x) == Math.abs(newY - y);
    }

    public String symbol() {
        if (isWhite) {
            return "♗";
        } else {
            return "♝";
        }
    }
}

class Queen extends Piece {
    public Queen(int x, int y, boolean isWhite) {
        super(x, y, isWhite);
    }

    public boolean canMove(int newX, int newY, Piece[][] board) {
        return newX == x || newY == y || Math.abs(newX - x) == Math.abs(newY - y);
    }

    public String symbol() {
        if (isWhite) {
            return "♕";
        } else {
            return "♛";
        }
    }
}

class King extends Piece {
    public King(int x, int y, boolean isWhite) {
        super(x, y, isWhite);
    }

    public boolean canMove(int newX, int newY, Piece[][] board) {
        int dx = Math.abs(newX - x);
        int dy = Math.abs(newY - y);
        return dx <= 1 && dy <= 1;
    }

    public String symbol() {
        if (isWhite) {
            return "♔";
        } else {
            return "♚";
        }
    }
}


