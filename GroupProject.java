import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GroupProject {

    private JFrame frame;
    private JButton[][] buttons = new JButton[8][8];
    private Piece[][] board = new Piece[8][8];
    private int selectedRow = -1;
    private int selectedCol = -1;

    public GroupProject() {
        frame = new JFrame("Chess");
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(8, 8));
        setupBoard();
        setupButtons();
        frame.setVisible(true);
    }

    private void setupBoard() {
        // Pawns
        for (int col = 0; col < 8; col++) {
            board[1][col] = new Pawn(1, col, false);
            board[6][col] = new Pawn(6, col, true);
        }

        // Rooks
        board[0][0] = new Rook(0, 0, false);
        board[0][7] = new Rook(0, 7, false);
        board[7][0] = new Rook(7, 0, true);
        board[7][7] = new Rook(7, 7, true);

        // Knights
        board[0][1] = new Knight(0, 1, false);
        board[0][6] = new Knight(0, 6, false);
        board[7][1] = new Knight(7, 1, true);
        board[7][6] = new Knight(7, 6, true);

        // Bishops
        board[0][2] = new Bishop(0, 2, false);
        board[0][5] = new Bishop(0, 5, false);
        board[7][2] = new Bishop(7, 2, true);
        board[7][5] = new Bishop(7, 5, true);

        // Queens
        board[0][3] = new Queen(0, 3, false);
        board[7][3] = new Queen(7, 3, true);

        // Kings
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
                if ((r + c) % 2 == 0)
                    color = new Color(240, 217, 181);
                else
                    color = new Color(181, 136, 99);
                button.setBackground(color);
                buttons[r][c] = button;
                frame.add(button);
            }
        }
    }

    private String getPieceSymbol(Piece p) {
        if (p == null) return "";
        return p.symbol();
    }

    public void handleClick(int row, int col) {
        if (selectedRow == -1 && board[row][col] != null) {
            selectedRow = row;
            selectedCol = col;
        } else if (selectedRow != -1) {
            Piece selected = board[selectedRow][selectedCol];
            if (selected.canMove(row, col, board)) {
                board[row][col] = selected;
                selected.move(row, col);
                board[selectedRow][selectedCol] = null;
                updateButtons();
            }
            selectedRow = -1;
            selectedCol = -1;
        }
    }

    private void updateButtons() {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                buttons[r][c].setText(getPieceSymbol(board[r][c]));
    }

    public static void main(String[] args) {
        new GroupProject();
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
// Abstract Piece class
abstract class Piece {
    protected int x, y;
    protected boolean isWhite;

    public Piece(int x, int y, boolean isWhite) {
        this.x = x;
        this.y = y;
        this.isWhite = isWhite;
    }

    public abstract boolean canMove(int newX, int newY, Piece[][] board);
    public void move(int newX, int newY) {
        x = newX;
        y = newY;
    }

    public abstract String symbol();
}

// Pawn
class Pawn extends Piece {
    private boolean hasMoved = false;

    public Pawn(int x, int y, boolean isWhite) {
        super(x, y, isWhite);
    }

    public boolean canMove(int newX, int newY, Piece[][] board) {
        int dir;
        if (isWhite) dir = -1;
        else dir = 1;

        if (!hasMoved && newX == x + 2 * dir && newY == y && board[newX][newY] == null)
            return true;

        if (newX == x + dir && newY == y && board[newX][newY] == null)
            return true;

        return false;
    }

    public void move(int newX, int newY) {
        super.move(newX, newY);
        hasMoved = true;
    }

    public String symbol() {
        if (isWhite) return "♙";
        else return "♟";
    }
}

// Rook
class Rook extends Piece {
    public Rook(int x, int y, boolean isWhite) {
        super(x, y, isWhite);
    }

    public boolean canMove(int newX, int newY, Piece[][] board) {
        return newX == x || newY == y;
    }

    public String symbol() {
        if (isWhite) return "♖";
        else return "♜";
    }
}

// Knight
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
        if (isWhite) return "♘";
        else return "♞";
    }
}

// Bishop
class Bishop extends Piece {
    public Bishop(int x, int y, boolean isWhite) {
        super(x, y, isWhite);
    }

    public boolean canMove(int newX, int newY, Piece[][] board) {
        return Math.abs(newX - x) == Math.abs(newY - y);
    }

    public String symbol() {
        if (isWhite) return "♗";
        else return "♝";
    }
}

// Queen
class Queen extends Piece {
    public Queen(int x, int y, boolean isWhite) {
        super(x, y, isWhite);
    }

    public boolean canMove(int newX, int newY, Piece[][] board) {
        return newX == x || newY == y || Math.abs(newX - x) == Math.abs(newY - y);
    }

    public String symbol() {
        if (isWhite) return "♕";
        else return "♛";
    }
}

// King
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
        if (isWhite) return "♔";
        else return "♚";
    }
}

