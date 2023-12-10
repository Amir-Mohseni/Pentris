package runners;

import pentris.game.Bot;

public class GeneticsFinder {
    private Bot b;

    /**
     * Constructor.
     * Creates a new best batch finder.
     * @param bot The bot to use to find the batches.
     */
    public GeneticsFinder(Bot bot) {
        b = bot;
    }

    /**
     * Runs the method to find the best batch.
     */
    public void run() {
        b.findBestSolution(new int[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, 100000);
    }

    /**
     * Main method. 
     * Run this method to find the best batches.
     * Won't override previous data in the BestBatch.csv file, but will add to it.
     * @param args
     */
    public static void main(String[] args) {
        Bot bot = new Bot(new int[] {Bot.MAX_HEIGHT, Bot.CENTER_HEIGHT, Bot.ROW_TRANSITIONS, Bot.COLUMN_TRANSITIONS, Bot.NUM_HOLES, Bot.DEEPEST_WELL},
                          new double[] {2.2112334071595248,-4.730949602485206,-3.9557429442760115,1.6151794034934457,-5.0,-3.6366230443930787});
        GeneticsFinder bf = new GeneticsFinder(bot);
        bf.run();
        System.out.println("Done!");
    }
}
