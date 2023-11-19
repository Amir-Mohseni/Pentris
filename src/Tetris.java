import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Tetris {
    Board gameBoard = new Board(5, 18);
    public static UI ui;

    Tetris() {
        ui = new UI(gameBoard.WIDTH, gameBoard.HEIGHT, 45);
    }
    public static void main(String[] args) throws InterruptedException {

        Tetris tetris = new Tetris();
        Scanner scanner = new Scanner(System.in);
        Board gameBoard = tetris.gameBoard;
        long MOVE_TIMER = 10 * 1000;
        boolean gameEnded = false;
//        test(gameBoard);
        int currentPiece = 0;

        while(!gameEnded) {
            int id = gameBoard.permutation[currentPiece];
            if(!gameBoard.validPlacement(new Cords(1, 2), gameBoard.pieces.get(id))) {
                System.out.println("Game Over");
                gameEnded = true;
                break;
            }
            gameBoard.addPiece(new Cords(1, 2), gameBoard.pieces.get(id));
            updateDisplay(gameBoard);
            while(gameBoard.applyGravity(gameBoard.pieces.get(id))) {
                //Read inputs from keyboard
                getJFrame(gameBoard, id);

                TimeUnit.MILLISECONDS.sleep(MOVE_TIMER);
                MOVE_TIMER *= 0.99;

                updateDisplay(gameBoard);
            }
            //TODO Complete
            //gameBoard.emptyFullRows();

            updateDisplay(gameBoard);
            currentPiece++;
            if(currentPiece == 12) {
                gameEnded = true;
                System.out.println("You Won!");
                break;
            }
        }
    }

    private static void getJFrame(Board gameBoard, int id) {
        JFrame tempFrame = new JFrame();
        tempFrame.setFocusable(true);
        tempFrame.setVisible(true);
        tempFrame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode){
                    case KeyEvent.VK_A:
                    case KeyEvent.VK_LEFT:
                        if (gameBoard.validMove(new Cords(0, -1), gameBoard.pieces.get(id))) {
                            gameBoard.movePiece(new Cords(0, -1), gameBoard.pieces.get(id));
                            updateDisplay(gameBoard);
                        }
                        break;
                    case KeyEvent.VK_D:
                    case KeyEvent.VK_RIGHT:
                        if (gameBoard.validMove(new Cords(0, 1), gameBoard.pieces.get(id))) {
                            gameBoard.movePiece(new Cords(0, 1), gameBoard.pieces.get(id));
                            updateDisplay(gameBoard);
                        }
                        break;
                    case KeyEvent.VK_R:
                        if (gameBoard.validRotation(gameBoard.pieces.get(id))) {
                            gameBoard.rotatePiece(gameBoard.pieces.get(id));
                            updateDisplay(gameBoard);
                        }
                        break;
                    case KeyEvent.VK_S:
                    case KeyEvent.VK_DOWN:
                        if (gameBoard.validMove(new Cords(1, 0), gameBoard.pieces.get(id))) {
                            gameBoard.movePiece(new Cords(1, 0), gameBoard.pieces.get(id));
                            updateDisplay(gameBoard);
                        }
                        break;
                    case KeyEvent.VK_SPACE:
                        while (gameBoard.applyGravity(gameBoard.pieces.get(id))) {
                            updateDisplay(gameBoard);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
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
