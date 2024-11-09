public class GravityConnectFour {
    private final int rows = 6;
    private final int cols = 7;
    private char[][] board;

    public GravityConnectFour() {
        board = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = ' ';
            }
        }
    }

    public char[][] getBoard() {
        return board;
    }

    public boolean dropPiece(int col, char piece) {
        for (int i = rows - 1; i >= 0; i--) {
            if (board[i][col] == ' ') {
                board[i][col] = piece;
                return true;
            }
        }
        return false;
    }

    public boolean checkWin(char piece) {
        // 縦、横、斜めの勝利条件をチェック
        // 縦チェック
        for (int i = 0; i < rows - 3; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] == piece && board[i + 1][j] == piece &&
                        board[i + 2][j] == piece && board[i + 3][j] == piece) {
                    return true;
                }
            }
        }

        // 横チェック
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols - 3; j++) {
                if (board[i][j] == piece && board[i][j + 1] == piece &&
                        board[i][j + 2] == piece && board[i][j + 3] == piece) {
                    return true;
                }
            }
        }

        // 斜め右下チェック
        for (int i = 0; i < rows - 3; i++) {
            for (int j = 0; j < cols - 3; j++) {
                if (board[i][j] == piece && board[i + 1][j + 1] == piece &&
                        board[i + 2][j + 2] == piece && board[i + 3][j + 3] == piece) {
                    return true;
                }
            }
        }

        // 斜め左下チェック
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

    public boolean isFull() {
        for (int j = 0; j < cols; j++) {
            if (board[0][j] == ' ') {
                return false;
            }
        }
        return true;
    }

    public void setBoard(char[][] newBoard) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = newBoard[i][j];
            }
        }
    }
}
