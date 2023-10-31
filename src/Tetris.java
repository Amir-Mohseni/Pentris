import java.util.Arrays;

public class Tetris {
    static int[][] board;
    static int[] permutation;

    static int[] rotation;
    final static int WIDTH = 5, HEIGHT = 15, NUM_PIECES = 12;
    public static char[] input = { 'W', 'Y', 'I', 'L', 'N', 'P', 'F', 'V', 'X', 'U', 'T', 'Z'};
    Tetris() {
        board = new int[HEIGHT][WIDTH];
        permutation = new int[NUM_PIECES];
        rotation = new int[NUM_PIECES];

        for (int i = 0; i < HEIGHT; i++)
            for (int j = 0; j < WIDTH; j++)
                board[i][j] = -1;

        for (int i = 0; i < NUM_PIECES; i++) {
            permutation[i] = i;
            rotation[i] = 0;
        }
        // x y -> (x < 0 || x >= HEIGHT) (y < 0 || y >= WIDTH)
    }

    private static int characterToID(char character) {
        int pentID = -1;
        if (character == 'X') {
            pentID = 0;
        } else if (character == 'I') {
            pentID = 1;
        } else if (character == 'Z') {
            pentID = 2;
        } else if (character == 'T') {
            pentID = 3;
        } else if (character == 'U') {
            pentID = 4;
        } else if (character == 'V') {
            pentID = 5;
        } else if (character == 'W') {
            pentID = 6;
        } else if (character == 'Y') {
            pentID = 7;
        } else if (character == 'L') {
            pentID = 8;
        } else if (character == 'P') {
            pentID = 9;
        } else if (character == 'N') {
            pentID = 10;
        } else if (character == 'F') {
            pentID = 11;
        }
        return pentID;
    }

    public static void RandomShuffle() {
        //TODO
        //Randomly shuffle the permutation
    }

    public static void addPiece(int x,  int y, int index) {
        //TODO
        //Add the piece to board
        int mutation = rotation[index];
        int pentID = permutation[index];
        int[][] piece = PentominoDatabase.data[pentID][mutation];
        for (int i = 0; i < piece.length; i++) {
            for (int j = 0; j < piece[i].length; j++) {
                int nx = x + i, ny = y + j;
                if(piece[i][j] == 1) {
                    board[nx][ny] = pentID;
                }
            }
        }
    }

    public static void movePiece() {
        //TODO
        //moves the piece on the board
    }

    public static void removePiece() {
        //TODO
        //removes the piece from the board
    }

    public static void rotate(int index) {
        int pentID = permutation[index];
        rotation[index] = (rotation[index] + 1) % PentominoDatabase.data[pentID].length;
    }

    //Before adding/Moving a block we need to check if that move is valid
    public static boolean checkValidity(int x, int y, int index) {
        int mutation = rotation[index];
        int pentID = permutation[index];
        int[][] piece = PentominoDatabase.data[pentID][mutation];

        for (int i = 0; i < piece.length; i++)
            for (int j = 0; j < piece[i].length; j++) {
                int nx = x + i, ny  = y + j;
                if(nx < 0 || nx >= HEIGHT || ny < 0 || ny >= WIDTH)
                    return false;
                if(piece[i][j] == 1 && (board[nx][ny] != -1 && board[nx][ny] != pentID)) {
                    return false;
                }
            }
        return true;
    }

    public static void printBoard() {
        for (int i = HEIGHT - 1; i >= 0; i--) {
            System.out.println(Arrays.toString(board[i]));
        }
    }



    public static void main(String[] args) {
        Tetris game = new Tetris();
//        addPiece(5, 2, 2);
        game.printBoard();
    }
}
