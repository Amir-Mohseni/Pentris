import java.util.Arrays;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class Tetris {
    Board gameBoard = new Board(5, 15);
    public static UI ui;

    Tetris() {
        ui = new UI(gameBoard.WIDTH, gameBoard.HEIGHT, 50);
    }
    public static void main(String[] args) throws InterruptedException {
        Tetris tetris = new Tetris();
        Board gameBoard = tetris.gameBoard;
        int MOVE_TIMER = 2;
        boolean gameEnded = false;
        int currentPiece = 0;

        while(!gameEnded) {
            int id = gameBoard.permutation[currentPiece];
            gameBoard.addPiece(new Cords(1, 2), gameBoard.pieces.get(id));
            ui.setState(transpose(gameBoard.grid));
            TimeUnit.SECONDS.sleep(2);
            gameBoard.applyGravity(gameBoard.pieces.get(id));

            gameEnded = true;
            ui.setState(transpose(gameBoard.grid));
        }
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
