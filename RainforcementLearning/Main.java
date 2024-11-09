public class Main {
    public static void main(String[] args) {
        GravityConnectFour game = new GravityConnectFour();
        QLearningAgent agent1 = new QLearningAgent();
        QLearningAgent agent2 = new QLearningAgent();

        for (int episode = 0; episode < 150000; episode++) {
            game = new GravityConnectFour(); // 新しいゲームを開始
            boolean gameOver = false;
            char currentPlayer = 'X'; // 'X' が agent1, 'O' が agent2

            while (!gameOver) {
                int action;
                QLearningAgent currentAgent;

                // 現在のプレイヤーに基づいてエージェントを選択
                if (currentPlayer == 'X') {
                    currentAgent = agent1;
                } else {
                    currentAgent = agent2;
                }

                // エージェントの行動を選択し、駒を置く
                action = currentAgent.chooseAction(game.getBoard());
                boolean validMove = game.dropPiece(action, currentPlayer);

                if (validMove) {
                    // 報酬を計算し、Q値を更新
                    double reward = currentAgent.getReward(game.getBoard(), currentPlayer, gameOver, action);
                    char[][] nextBoard = game.getBoard();
                    currentAgent.updateQTable(game.getBoard(), action, reward, nextBoard);
                    gameOver = game.checkWin(currentPlayer) || game.isFull();

                    if (gameOver) {
                        // 勝利や引き分けの場合、相手のエージェントにも報酬を与える
                        double opponentReward = (currentPlayer == 'X') ? -1.0 : 1.0;
                        QLearningAgent opponentAgent = (currentPlayer == 'X') ? agent2 : agent1;
                        opponentAgent.updateQTable(game.getBoard(), action, opponentReward, nextBoard);
                        break;
                    }

                    // プレイヤーを交代
                    currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                }
            }

            // 学習後に探索率を調整
            agent1.updateExplorationRate();
            agent2.updateExplorationRate();
        }

        System.out.println("エージェント同士のトレーニングが完了しました！");
    }
}
