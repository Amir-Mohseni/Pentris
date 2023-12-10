package pentris.game;

import java.io.*;
import java.util.Arrays;
import java.time.LocalDateTime;

public class GeneticAlgorithm {
    private double mutationRate;
    private int initialPopulationSize;
    private int generations;
    public Bot[] population;
    private int[] parameterList;

    /**
     * Constructor.
     * Creates a genetic algorithm instance
     * @param parameterList The list of parameters the bot will use
     * @param name The name of the genetic algorithm
     * @param initialPopulationSize The size of the initial population
     * @param generations The number of generations the algorithm will run for
     * @param mutationRate The mutation rate of the genetic algorithm
     */
    public GeneticAlgorithm(int[] parameterList, int initialPopulationSize, int generations, double mutationRate) {
        this.parameterList = parameterList;
        this.initialPopulationSize = initialPopulationSize;
        this.generations = generations;
        this.mutationRate = mutationRate;
        population = new Bot[this.initialPopulationSize];
    }

    /**
     * Do elitist selection to keep only the most fit individuals
     */
    public void selection() {
        HeapSort.sort(population);
        Bot[] tempPopulation = new Bot[initialPopulationSize];
        for (int i=0; i<tempPopulation.length; i++) {
            tempPopulation[i] = population[i];
        }
        population = tempPopulation;
    }

    /**
     * Reproduce individuals using tournament selection.
     * Selects a random batch od 10% of the population and picks the top 2 in that batch.
     * They then reproduce.
     */
    public void reproduce() {
        Bot[] batch = new Bot[(int) (initialPopulationSize*0.4)];
        if (batch.length < 2) {
            batch = new Bot[2];
        }
        for (int i=0; i<batch.length; i++) {
            batch[i] = population[(int) (Math.random()*population.length)];
        }
        HeapSort.sort(batch);
        crossover(batch[0], batch[1]);
    }

    /**
     * Crossover function for 2 bots.
     * Creates 2 children using weighted average crossover.
     * Adds the child into the populaion.
     * @param bot1 The first parent bot
     * @param bot2 The second parent bot
     */
    public void crossover(Bot bot1, Bot bot2) {
        double fitness1 = bot1.getFitness();
        double fitness2 = bot2.getFitness();

        double average1 = fitness1/(fitness1 + fitness2);
        double average2 = fitness2/(fitness1 + fitness2);

        double[] geno1 = new double[bot1.getGenotype().length];
        double[] geno2 = new double[bot1.getGenotype().length];
        for (int i=0; i<geno1.length; i++) {
            geno1[i] = bot1.getGenotype()[i]*average1 + bot2.getGenotype()[i]*average2;
        }
        for (int i=0; i<geno2.length; i++) {
            geno2[i] = bot1.getGenotype()[i]*average1 + bot2.getGenotype()[i]*average2;
        }

        Bot child1 = new Bot(parameterList, geno1);
        Bot child2 = new Bot(parameterList, geno2);
        
        child1.mutate(mutationRate);
        child2.mutate(mutationRate);

        Bot[] tempPopulation = new Bot[population.length+2];
        for (int i=0; i<population.length; i++) {
            tempPopulation[i] = population[i];
        }
        tempPopulation[tempPopulation.length-2] = child1;
        tempPopulation[tempPopulation.length-1] = child2;
        population = tempPopulation;
    }

    public double[] bestSolution;

    /**
     * Class to evaluate the bots in Threads
     */
    class FitnessCalculator implements Runnable {
        Bot bot;
        boolean done;
        /**
         * Constructor.
         * @param bot The bot to evaluate
         */
        FitnessCalculator(Bot bot) {
            this.bot = bot;
            done = false;
        }
        /**
         * Runs the fitness evaluation for this bot
         */
        @Override
        public void run() {
            bot.calculateFitness(100);
            done = true;
        }
        /**
         * Checks if the evaluation is done
         * @return {@code true} if the evaluation is done and {@code false} otherwise.
         */
        synchronized boolean isDone() {
            return done;
        }
    }

    /**
     * Checks if the genetic algorithm has converged.
     * @param percent The maximum percentage of the maximum fitness that the difference between the maximum and minimum fitness can be.
     * @return {@code true} if converged and {@code false} otherwise.
     */
    public boolean converged(double percent) {
        HeapSort.sort(population);
        double minFitness = population[population.length-1].getFitness();
        double maxFitness = population[0].getFitness();
        if (maxFitness-minFitness <= percent*maxFitness) {
            return true;
        }
        return false;
    }

    /**
     * Calculates the fitness of all individuals in the population by running it in batches on multiple threads.
     * @param batchSize The size of the batch to use (corresponds to the number of threads created per batch)
     */
    public void runBatch(int batchSize) {
        // Evaluate bot fitnesses in batches
        for (int i=0; i<population.length; i+=batchSize) {
            FitnessCalculator[] evaluators = new FitnessCalculator[batchSize];
            int pos = 0;
            for (int j=i; j<Math.min(i+batchSize, population.length); j++) {
                FitnessCalculator be = new FitnessCalculator(population[j]);
                evaluators[pos] = be;
                pos++;
                Thread t = new Thread(be, "Bot Fitness Calculator");
                t.start();
            }
            // Stay in while loop while evaluating
            boolean running = true;
            while (running) {
                running = false;
                for (int j=0; j<evaluators.length; j++) {
                    if (evaluators[j] != null && !evaluators[j].isDone()) {
                        running = true;
                    }
                }
            }
        }
    }

    /**
     * Run the genetic algorithm
     */
    public void run() {
        String tempName = LocalDateTime.now()+"";
        String timeName = "";
        for (int i=0; i<tempName.length(); i++) {
            if (tempName.charAt(i) == ':') {
                timeName += '-';
            } else {
                timeName += tempName.charAt(i);
            }
        }
        String data = "";
        for (int i=0; i<parameterList.length; i++) {
            data += Bot.parameterIDtoString(parameterList[i])+",";
        }
        data += "Fitness,\n";
        long c = System.currentTimeMillis();
        // Initialize it
        population = new Bot[initialPopulationSize];
        System.out.println("Initializing...");
        for (int i=0; i<initialPopulationSize; i++) {
            population[i] = new Bot(parameterList, null);
        }
        runBatch(10);
        // Run the algorithm for a given number of generations
        for (int generation=0; generation<generations; generation++) {
            System.out.println("========================= Generation #"+(generation+1)+" =========================");
            while (population.length < initialPopulationSize*1.5) {
                reproduce();
            }
            long c1 = System.currentTimeMillis();
            runBatch(10);
            selection();
            System.out.println("Best individual: "+Arrays.toString(population[0].getGenotype())+"; --Fitnes: "+population[0].getFitness());
            System.out.println("Worst individual: "+Arrays.toString(population[population.length-1].getGenotype())+"; --Fitnes: "+population[population.length-1].getFitness());
            long timeRun = (System.currentTimeMillis()-c1)/1000;
            System.out.println("Generation runtime: "+timeRun + " s");
            System.out.println("Estimated time remaining: " + timeRun*(generations-1-generation)/60 + " min");
            System.out.println();
            
            // Store the data
            for (int i=0; i<population[0].getGenotype().length; i++) {
                data += population[0].getGenotype()[i]+",";
            }
            data += population[0].getFitness()+",\n";
            // Try storing the data
            // Stored on every step so the program can be quit
            try {
                File f = new File("src/pentris/Results/Genetic algorithm runs/GeneticAlgorithm"+timeName+".csv");
                FileWriter fw = new FileWriter(f);
                fw.append(data);
                fw.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println("Something went wrong with opening/creating the file");
            }
            // Check convergence
            if (converged(0.2)) {
                System.out.println("========================= Converged! =========================");
                break;
            }
        }
        // Delete the temporary file before storing the real one
        // Try storing the data
        try {
            File f = new File("src/pentris/Results/Genetic algorithm runs/GeneticAlgorithm"+timeName+".csv");
            FileWriter fw = new FileWriter(f);
            fw.append(data);
            fw.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Something went wrong with opening/creating the file");
        }
        // Print out the best bots weights
        HeapSort.sort(population);
        Bot best = population[0];
        System.out.println(Arrays.toString(best.getGenotype()));
        bestSolution = best.getGenotype();
        System.out.println("Runtime: "+((System.currentTimeMillis() - c)/60000) + " min");
    }
}
