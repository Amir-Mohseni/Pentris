public class Runner {
    public static void main(String[] args) throws InterruptedException, CloneNotSupportedException {
        int NUM_OF_TEST = 500;

        Board bestBoard = new Board(5, 18);
        UI bestUI = new UI(bestBoard.WIDTH, bestBoard.HEIGHT, 45);

//        Bot newBot = new Bot();
//        newBot.run();

        for (int i = 0; i < NUM_OF_TEST; i++) {
            Bot newBot = new Bot();
            Board result = newBot.run();
            if(result.score > bestBoard.score)
                bestBoard = result.clone();

            //Print Results
            if(i % 10 == 0) {
                System.out.println("---------------");
                System.out.println(bestBoard.score);
                bestUI.setState(transpose(bestBoard.grid));
            }
        }
        System.exit(0);
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
