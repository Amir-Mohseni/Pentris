package runners;

import pentris.game.Bot;

public class BotEvaluator {
    private Bot b;

    /**
     * Constructor.
     * Creates a new bot evaluator.
     * @param bot The bot to evaluate
     */
    public BotEvaluator(Bot bot) {
        b = bot;
    }

    /**
     * Runs the bot evaluator method and stores the data in a new file
     */
    public void run() {
        b.calculateFitness(1000);
        b.storeScoreData();
    }

    /**
     * Main method.
     * Run this to start the bot evaluator.
     * @param args
     */
    public static void main(String[] args) {
        Bot bot = new Bot(new int[] {Bot.MAX_HEIGHT, Bot.CENTER_HEIGHT, Bot.ROW_TRANSITIONS, Bot.COLUMN_TRANSITIONS, Bot.NUM_HOLES, Bot.DEEPEST_WELL},
                          new double[] {2.2112334071595248,-4.730949602485206,-3.9557429442760115,1.6151794034934457,-5.0,-3.6366230443930787});
        BotEvaluator be = new BotEvaluator(bot);
        be.run();
        System.out.println("Done!");
    }
}