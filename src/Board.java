import java.util.*;
import java.util.stream.IntStream;

public class Board {
    int[][] grid;

    int[][][] rotationMatrix = {
            {
                    {0, 1},
                    {-1, 0},
            },
            {
                    {0, -1},
                    {1, 0},
            }
    };
    int WIDTH;
    int HEIGHT;
    int NUMBER_OF_PIECES = 12;
    int[] permutation = new int[NUMBER_OF_PIECES];
    ArrayList <Piece> pieces = new ArrayList<>();

    private int[] emptyRow = new int[]{-1, -1, -1, -1, -1};

    Board(int width, int height) {
        Random rand = new Random();
        for (int i = 0; i < NUMBER_OF_PIECES; i++)
            permutation[i] = i;
        randomShuffle(permutation);

        for (int i = 0; i < NUMBER_OF_PIECES; i++)
            pieces.add(new Piece(permutation[i], 0));
            //pieces.add(new Piece(permutation[i], rand.nextInt(PentominoDatabase.data[permutation[i]].length)));

        this.HEIGHT = height;
        this.WIDTH = width;
        this.grid = new int[HEIGHT][WIDTH];
        for (int i = 0; i < HEIGHT; i++)
            for (int j = 0; j < WIDTH; j++) {
                grid[i][j] = -1;
            }
    }

    public void addPiece(Cords cord, Piece piece) {
        int[][] mat = PentominoDatabase.data[piece.id][piece.rotation];
        TreeSet <Cords> newCords = new TreeSet<>();
        for (int i = 0; i < mat.length; i++)
            for (int j = 0; j < mat[0].length; j++)
                if(mat[i][j] != 0) {
                    Cords newCord = cord.add(i, j);
                    newCords.add(newCord);
                    if(mat[i][j] == 2)
                        piece.center = newCord;
                }

        piece.occupiedSpaces = newCords;
        for (Cords item : piece.occupiedSpaces)
            grid[item.x][item.y] = piece.id;
    }

    public void movePiece(Cords cord, Piece piece) {
        TreeSet <Cords> newCords = new TreeSet<>();

        for (Cords item : piece.occupiedSpaces) {
            grid[item.x][item.y] = -1;
            Cords newCord = item.add(cord.x, cord.y);
            newCords.add(newCord);
        }
        piece.center = piece.center.add(cord.x, cord.y);
        piece.occupiedSpaces = newCords;
        for (Cords item : piece.occupiedSpaces)
            grid[item.x][item.y] = piece.id;
    }

    public boolean applyGravity(Piece piece) {
        Cords gravity = new Cords(1, 0);
        if (validMove(gravity, piece)) {
            movePiece(gravity, piece);
            return true;
        }
        else
            return false;
    }

    public boolean validRotation(Piece piece) {
        TreeSet<Cords> newCords = new TreeSet<>();

        for (Cords item : piece.occupiedSpaces) {
            Cords newCord = getRotatedCord(item, piece.center);
            newCords.add(newCord);
        }

        for (Cords newCord: newCords) {
            if(outOfBounds(newCord) || !(grid[newCord.x][newCord.y] == -1 || grid[newCord.x][newCord.y] == piece.id))
                return false;
        }
        return true;
    }

    public void rotatePiece(Piece piece) {
        TreeSet<Cords> newCords = new TreeSet<>();

        for (Cords item : piece.occupiedSpaces) {
            grid[item.x][item.y] = -1;
            Cords newCord = getRotatedCord(item, piece.center);
            newCords.add(newCord);
        }

        piece.rotate();
        piece.occupiedSpaces = newCords;
        for (Cords item : piece.occupiedSpaces)
            grid[item.x][item.y] = piece.id;
    }

    public Cords getRotatedCord(Cords A, Cords B) {
        Cords C = new Cords(A.x - B.x, A.y - B.y);
        Cords D = new Cords(C.y, -C.x);
        return D.add(B.x, B.y);
    }

    boolean validMove(Cords cord, Piece piece) {
        for (Cords item : piece.occupiedSpaces) {
            Cords newCord = item.add(cord.x, cord.y);
            if(outOfBounds(newCord) || !(grid[newCord.x][newCord.y] == -1 || grid[newCord.x][newCord.y] == piece.id))
                return false;
        }
        return true;
    }

    public boolean validPlacement(Cords cord, Piece piece) {
        int[][] mat = PentominoDatabase.data[piece.id][piece.rotation];
        for (int i = 0; i < mat.length; i++)
            for (int j = 0; j < mat[0].length; j++)
                if(mat[i][j] != 0) {
                    Cords newCord = cord.add(i, j);
                    if(mat[i][j] != 0) {
                        if (outOfBounds(newCord))
                            return false;
                        if (!(grid[newCord.x][newCord.y] == -1 || grid[newCord.x][newCord.x] == piece.id))
                            return false;
                    }
                }
        return true;
    }

    public void randomShuffle(int[] arr) {
        Random rand = new Random();
        for (int i = 0; i < arr.length; i++) {
            int temp = arr[i];
            int swapIndex = rand.nextInt(i + 1);
            arr[i] = arr[swapIndex];
            arr[swapIndex] = temp;
        }
    }

    public boolean outOfBounds(Cords cord) {
        if(cord.x < 0 || cord.x >= HEIGHT || cord.y < 0 || cord.y >= WIDTH)
            return true;
        else
            return false;
    }

    public void emptyFullRows() {
        boolean fullRow = false;
        for (int i = 0; i < grid.length; i++) {
            boolean full = true;
            for (int j = 0; j < grid[i].length; j++)
                if(grid[i][j] == -1) {
                    full = false;
                    break;
                }
            if(full) {
                fullRow = true;
                Arrays.fill(grid[i], -1);
                for (int ii = 0; ii < grid.length - 1; ii++) {
                    for (int jj = 0; jj < grid[ii].length; jj++)
                        if(grid[ii][jj] != -1 && grid[ii + 1][jj] == -1) {
                            grid[ii + 1][jj] = grid[ii][jj];
                            grid[ii][jj] = -1;
                        }
                }
            }
        }
        if(fullRow)
            emptyFullRows();
    }

    public int emptyFullRows2() {
        int totalFullRows = 0;
        List<Integer> fullRows = this.getFullRows();
        List<int[]> nonFullRows = new ArrayList<>();

        while(!fullRows.isEmpty()){
            for (int row = 0; row < this.grid.length; row++){
                if (fullRows.contains(row)){
                    continue;
                }
                nonFullRows.add(this.grid[row]);
            }

            totalFullRows += fullRows.size();


            int[][] newGrid = new int[this.HEIGHT][this.WIDTH];
            for (int[] row : newGrid){
                Arrays.fill(row, -1);
            }

            // access nonFullRows and grid backwards
            for (int i = 1; i < nonFullRows.size() - 1; i++){
                newGrid[newGrid.length - i] = nonFullRows.get(nonFullRows.size() - i);
            }

            this.grid = newGrid;

            fullRows = this.getFullRows();
        }
        return totalFullRows;
    }



    public boolean arrayContainsFull(int[] arr){
        for (int i : arr){
            if (i==-1)
                return false;
        }
        return true;
    }

    private List<Integer> getFullRows(){
        List<Integer> fullRows = new ArrayList<>();
        for (int row = 0; row < this.grid.length; row++){
            int[] currentRow = this.grid[row];

            if (arrayContainsFull(currentRow)){
                fullRows.add(row);
            }
        }
        return fullRows;
    }

    public int getPentominoIndex(int id) {
        for (int i = 0; i < NUMBER_OF_PIECES; i++)
            if(id == permutation[i])
                return i;
        return -1;
    }

    public static void main(String[] args) {
        Board board = new Board(5,15);
//        board.grid[0] = new int[]{-1, -1, 4, 4, -1};
        int[] row = new int[]{4, 4, 4 ,4, 4};
        System.out.println(board.arrayContainsFull(row));
    }
}
