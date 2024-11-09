import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GravityConnectFourGUI extends JFrame {
    private final int rows = 6;
    private final int cols = 7;
    private JButton[] buttons;
    private char[][] board;
    private QLearningAgent agent;
    private boolean playerTurn = true;
    private JPanel boardPanel;

    public GravityConnectFourGUI() {
        board = new char[rows][cols];
        agent = new QLearningAgent();

        // Initialize the board with empty spaces
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = ' ';
            }
        }

        // GUI setup
        setTitle("Gravity Connect Four");
        setSize(700, 600);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(rows + 1, cols));
        buttons = new JButton[cols];

        // Add buttons for column selection
        for (int i = 0; i < cols; i++) {
            final int col = i;
            buttons[i] = new JButton("Drop");
            buttons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (playerTurn) {
                        handlePlayerMove(col);
                    }
                }
            });
            boardPanel.add(buttons[i]);
        }

        // Create the grid for the game board
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JLabel label = new JLabel("", SwingConstants.CENTER);
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                label.setOpaque(true);
                label.setBackground(Color.WHITE);
                label.setPreferredSize(new Dimension(100, 100));
                boardPanel.add(label);
            }
        }

        add(boardPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void handlePlayerMove(int col) {
        if (dropPiece(col, 'X')) {
            updateBoard();
            if (checkWin('X')) {
                JOptionPane.showMessageDialog(this, "Player wins!");
                resetGame();
                return;
            } else if (isFull()) {
                JOptionPane.showMessageDialog(this, "It's a draw!");
                resetGame();
                return;
            }
            playerTurn = false;
            handleAgentMove();
        } else {
            JOptionPane.showMessageDialog(this, "Column is full! Try another column.");
        }
    }

    private void handleAgentMove() {
        int action = agent.chooseAction(board);
        dropPiece(action, 'O');
        updateBoard();
        if (checkWin('O')) {
            JOptionPane.showMessageDialog(this, "Agent wins!");
            resetGame();
        } else if (isFull()) {
            JOptionPane.showMessageDialog(this, "It's a draw!");
            resetGame();
        }
        playerTurn = true;
    }

    private boolean dropPiece(int col, char piece) {
        for (int i = rows - 1; i >= 0; i--) {
            if (board[i][col] == ' ') {
                board[i][col] = piece;
                return true;
            }
        }
        return false;
    }

    private void updateBoard() {
        int labelIndex = cols;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JLabel label = (JLabel) boardPanel.getComponent(labelIndex);
                if (board[i][j] == 'X') {
                    label.setBackground(Color.RED);
                } else if (board[i][j] == 'O') {
                    label.setBackground(Color.BLUE);
                } else {
                    label.setBackground(Color.WHITE);
                }
                labelIndex++;
            }
        }
        repaint();
    }

    private boolean checkWin(char piece) {
        // 縦のチェック
        for (int i = 0; i < rows - 3; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] == piece && board[i + 1][j] == piece &&
                        board[i + 2][j] == piece && board[i + 3][j] == piece) {
                    return true;
                }
            }
        }

        // 横のチェック
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols - 3; j++) {
                if (board[i][j] == piece && board[i][j + 1] == piece &&
                        board[i][j + 2] == piece && board[i][j + 3] == piece) {
                    return true;
                }
            }
        }

        // 右斜め下（↘）のチェック
        for (int i = 0; i < rows - 3; i++) {
            for (int j = 0; j < cols - 3; j++) {
                if (board[i][j] == piece && board[i + 1][j + 1] == piece &&
                        board[i + 2][j + 2] == piece && board[i + 3][j + 3] == piece) {
                    return true;
                }
            }
        }

        // 左斜め下（↙）のチェック
        for (int i = 0; i < rows - 3; i++) {
            for (int j = 3; j < cols; j++) {
                if (board[i][j] == piece && board[i + 1][j - 1] == piece &&
                        board[i + 2][j - 2] == piece && board[i + 3][j - 3] == piece) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isFull() {
        for (int j = 0; j < cols; j++) {
            if (board[0][j] == ' ') {
                return false;
            }
        }
        return true;
    }

    private void resetGame() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = ' ';
            }
        }
        updateBoard();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GravityConnectFourGUI());
    }
}
