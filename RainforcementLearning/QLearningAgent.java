import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class QLearningAgent {
    private Map<String, double[]> qTable;
    private double learningRate = 0.1;
    private double discountFactor = 0.9;
    private double explorationRate = 1.0; // 初期のε-greedy探索率
    private double explorationDecay = 0.995; // 探索率の減少率
    private double minExplorationRate = 0.1; // 最小探索率
    private Random random;

    public QLearningAgent() {
        qTable = new HashMap<>();
        random = new Random();
    }

    public String getStateKey(char[][] board) {
        StringBuilder sb = new StringBuilder();
        for (char[] row : board) {
            for (char cell : row) {
                sb.append(cell);
            }
        }
        return sb.toString();
    }

    public int chooseAction(char[][] board) {
        String stateKey = getStateKey(board);

        // まず、自分の駒が3つ揃っていて4つ目を置ける場所を探す
        for (int col = 0; col < board[0].length; col++) {
            if (canPlacePiece(board, col) && canWinWithNextMove(board, col, 'X')) {
                return col; // 勝てる手があるならその列を選ぶ
            }
        }

        // 通常のε-greedyロジック
        if (!qTable.containsKey(stateKey) || random.nextDouble() < explorationRate) {
            return random.nextInt(board[0].length); // ランダムに列を選ぶ
        }

        double[] qValues = qTable.get(stateKey);
        int bestAction = 0;
        for (int i = 1; i < qValues.length; i++) {
            if (qValues[i] > qValues[bestAction]) {
                bestAction = i;
            }
        }
        return bestAction;
    }

    // 自分の駒が3つ揃っていて4つ目を置けるか確認するメソッド
    private boolean canWinWithNextMove(char[][] board, int col, char piece) {
        char[][] testBoard = copyBoard(board);
        if (dropPieceSimulated(testBoard, col, piece)) {
            return checkWin(testBoard, piece);
        }
        return false;
    }

    // 指定された列に駒を置けるか確認するメソッド
    private boolean canPlacePiece(char[][] board, int col) {
        return board[0][col] == ' '; // 最上段が空いていれば駒を置ける
    }

    private boolean canWin(char[][] board, int col, char piece) {
        GravityConnectFour testGame = new GravityConnectFour(); // テスト用のゲームロジック
        testGame.setBoard(copyBoard(board)); // 現在の盤面をセット
        if (testGame.dropPiece(col, piece) && testGame.checkWin(piece)) {
            return true; // この列に駒を置けば勝てる
        }
        return false;
    }

    public void updateQTable(char[][] board, int action, double reward, char[][] nextBoard) {
        String stateKey = getStateKey(board);
        String nextStateKey = getStateKey(nextBoard);

        qTable.putIfAbsent(stateKey, new double[7]); // 7列分のQ値を初期化
        qTable.putIfAbsent(nextStateKey, new double[7]);

        double[] qValues = qTable.get(stateKey);
        double[] nextQValues = qTable.get(nextStateKey);

        double maxNextQValue = Double.NEGATIVE_INFINITY;
        for (double nextQ : nextQValues) {
            if (nextQ > maxNextQValue) {
                maxNextQValue = nextQ;
            }
        }

        // Q値を更新
        qValues[action] += learningRate * (reward + discountFactor * maxNextQValue - qValues[action]);
    }

    public double getReward(char[][] board, char currentPlayer, boolean gameOver, int action) {
        if (gameOver && checkWin(board, currentPlayer)) {
            return 1.0; // 勝利の報酬
        } else if (gameOver && !checkWin(board, currentPlayer)) {
            return -1.0; // 敗北のペナルティ
        } else if (isFull(board)) {
            return 0.5; // 引き分け
        } else {
            // 戦略的な行動に対して報酬を設定し、無価値な行動には小さな負の報酬を与える
            double futureReward = 0.0;
            char[][] nextBoard = copyBoard(board);
            dropPieceSimulated(nextBoard, action, currentPlayer);

            if (checkWin(nextBoard, currentPlayer)) {
                futureReward += 0.9; // 勝利に近づく行動への報酬
            } else if (isMeaninglessMove(nextBoard, action)) {
                futureReward -= 0.1; // 無価値な行動には負の報酬
            }

            return futureReward;
        }
    }

    // 無価値な行動かどうかを判定するメソッド（例: ランダムに置いた場合や無意味な動き）
    private boolean isMeaninglessMove(char[][] board, int col) {
        // 無価値な行動を判定するロジックを追加
        // 例えば、駒を置いても戦略的な変化がない場合など
        return !hasPotentialForWinOrBlock(board, col);
    }

    // 戦略的な手かどうかを判定する簡単なロジック（例: 勝利やブロックに寄与するか）
    private boolean hasPotentialForWinOrBlock(char[][] board, int col) {
        // 自分または相手が3つ揃っているかどうかを確認し、4つ目の駒を置くことで勝利または阻止になるかを判定
        char[][] testBoard = copyBoard(board);
        if (dropPieceSimulated(testBoard, col, 'X') && checkWin(testBoard, 'X')) {
            return true; // 勝利に寄与する手
        }
        if (dropPieceSimulated(testBoard, col, 'O') && checkWin(testBoard, 'O')) {
            return true; // 相手の勝利を阻止する手
        }
        return false;
    }

    private char getOpponent(char currentPlayer) {
        return (currentPlayer == 'X') ? 'O' : 'X';
    }

    private int countAlignedPieces(char[][] board, char piece, int length) {
        int count = 0;
        // 横方向のチェック
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j <= board[0].length - length; j++) {
                boolean aligned = true;
                for (int k = 0; k < length; k++) {
                    if (board[i][j + k] != piece) {
                        aligned = false;
                        break;
                    }
                }
                if (aligned)
                    count++;
            }
        }

        // 縦方向のチェック
        for (int i = 0; i <= board.length - length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                boolean aligned = true;
                for (int k = 0; k < length; k++) {
                    if (board[i + k][j] != piece) {
                        aligned = false;
                        break;
                    }
                }
                if (aligned)
                    count++;
            }
        }

        // 右斜め下方向のチェック
        for (int i = 0; i <= board.length - length; i++) {
            for (int j = 0; j <= board[0].length - length; j++) {
                boolean aligned = true;
                for (int k = 0; k < length; k++) {
                    if (board[i + k][j + k] != piece) {
                        aligned = false;
                        break;
                    }
                }
                if (aligned)
                    count++;
            }
        }

        // 左斜め下方向のチェック
        for (int i = 0; i <= board.length - length; i++) {
            for (int j = length - 1; j < board[0].length; j++) {
                boolean aligned = true;
                for (int k = 0; k < length; k++) {
                    if (board[i + k][j - k] != piece) {
                        aligned = false;
                        break;
                    }
                }
                if (aligned)
                    count++;
            }
        }

        return count;
    }

    private boolean isFull(char[][] board) {
        for (int j = 0; j < board[0].length; j++) {
            if (board[0][j] == ' ') {
                return false;
            }
        }
        return true;
    }

    private boolean checkWin(char[][] board, char piece) {
        // 勝利条件をチェックするロジック
        for (int i = 0; i < board.length - 3; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == piece && board[i + 1][j] == piece &&
                        board[i + 2][j] == piece && board[i + 3][j] == piece) {
                    return true;
                }
            }
        }

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length - 3; j++) {
                if (board[i][j] == piece && board[i][j + 1] == piece &&
                        board[i][j + 2] == piece && board[i][j + 3] == piece) {
                    return true;
                }
            }
        }

        for (int i = 0; i < board.length - 3; i++) {
            for (int j = 0; j < board[0].length - 3; j++) {
                if (board[i][j] == piece && board[i + 1][j + 1] == piece &&
                        board[i + 2][j + 2] == piece && board[i + 3][j + 3] == piece) {
                    return true;
                }
            }
        }

        for (int i = 0; i < board.length - 3; i++) {
            for (int j = 3; j < board[0].length; j++) {
                if (board[i][j] == piece && board[i + 1][j - 1] == piece &&
                        board[i + 2][j - 2] == piece && board[i + 3][j - 3] == piece) {
                    return true;
                }
            }
        }

        return false;
    }

    private char[][] copyBoard(char[][] originalBoard) {
        char[][] newBoard = new char[originalBoard.length][originalBoard[0].length];
        for (int i = 0; i < originalBoard.length; i++) {
            for (int j = 0; j < originalBoard[i].length; j++) {
                newBoard[i][j] = originalBoard[i][j];
            }
        }
        return newBoard;
    }

    public void updateExplorationRate() {
        if (explorationRate > minExplorationRate) {
            explorationRate *= explorationDecay;
        }
    }

    private boolean dropPieceSimulated(char[][] board, int col, char piece) {
        for (int i = board.length - 1; i >= 0; i--) {
            if (board[i][col] == ' ') {
                board[i][col] = piece;
                return true;
            }
        }
        return false; // 列が埋まっている場合
    }

}
