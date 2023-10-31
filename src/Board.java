public class Board {
    int[][] grid;
    int WIDTH;
    int HEIGHT;

    Board(int width, int height) {
        this.HEIGHT = height;
        this.WIDTH = width;
        this.grid = new int[HEIGHT][WIDTH];
        for (int i = 0; i < HEIGHT; i++)
            for (int j = 0; j < WIDTH; j++)
                grid[i][j] = -1;
    }
}
