import java.util.concurrent.TimeUnit;

public class Bot {
    static Board gameBoard = new Board(5, 18);
    public static UI ui = new UI(gameBoard.WIDTH, gameBoard.HEIGHT, 45);;
    static final char[] moves = {'A', 'S', 'D', 'Q', 'R', 'N'};

    public static Board getMaxScore(Board currentBoard, int currentPiece, int depthLimit) throws CloneNotSupportedException {
        if(depthLimit == 0)
            return currentBoard;
        int id = currentBoard.permutation[currentPiece];
        Board bestBoard = gameBoard;
        int bestScore = gameBoard.score;
        for (char move: moves) {
            Board newBoard = (Board) gameBoard.clone();
            newBoard.applyButtonPress(move, id);
            newBoard.score += newBoard.emptyFullRows2() + gameBoard.getHighestEmptyRow();
            newBoard = getMaxScore(newBoard, currentPiece, depthLimit - 1);
            if (newBoard.score > bestScore) {
                bestBoard = newBoard;
                bestScore = newBoard.score;
            }
        }
        return bestBoard;
    }



    public static void main(String[] args) throws CloneNotSupportedException, InterruptedException {
        int currentPiece = 0;
        int MOVE_TIMER = 2 * 1000;
        while(true) {
            int id = gameBoard.permutation[currentPiece];
            if(!gameBoard.validPlacement(new Cords(1, 2), gameBoard.pieces.get(id)))
                break;
            gameBoard.addPiece(new Cords(1, 2), gameBoard.pieces.get(id));
            updateDisplay(gameBoard);
            while(gameBoard.applyGravity(gameBoard.pieces.get(id))) {
                gameBoard = getMaxScore(gameBoard, currentPiece, 5);
                gameBoard.score += gameBoard.emptyFullRows2() + gameBoard.getHighestEmptyRow();
                updateDisplay(gameBoard);
                TimeUnit.MILLISECONDS.sleep(MOVE_TIMER);
                MOVE_TIMER *= 0.99;
            }
            currentPiece++;
            if(currentPiece == 12) {
                break;
            }
        }
    }

    public static void updateDisplay(Board gameBoard) {
        ui.setState(transpose(gameBoard.grid));
    }

    public static int[][] transpose(int[][] curGrid) {
        int n = curGrid[0].length, m = curGrid.length;
        int[][] res = new int[n][m];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                res[i][j] = curGrid[j][i];
        return res;
    }
}
