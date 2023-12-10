package runners;

import java.util.ArrayList;
import java.util.Scanner;

import pentris.game.Bot;
import pentris.game.GeneticAlgorithm;

public class GeneticAlgorithmRunner {
    private GeneticAlgorithm ga;

    /**
     * Constructor.
     * Creates a new genetic algorithm runner
     * @param parameterList The list of parameters to use in this genetic algorithm.
     * @param initialPopulationSize The initial population size of the genetic algorithm.
     * @param generations The maximum number of generations in the genetic algorithm.
     * @param mutationRate The mutation rate of the genetic algorithm.
     */
    public GeneticAlgorithmRunner(int[] parameterList, int initialPopulationSize, int generations, double mutationRate) {
        ga = new GeneticAlgorithm(parameterList, initialPopulationSize, generations, mutationRate);
    }

    /**
     * Runs the genetic algorithm.
     */
    public void run() {
        ga.run();
    }

    /**
     * Main method.
     * Asks for an input of parameters to use in the genetic algorithm, and then runs the algorithms. 
     * Run this method to run a new genetic algorithm (warning! computationally heavy and might take a long time (up to 8 hours))
     * @param args
     */
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("========== Parameters ==========");
        for (int i=0; i<9; i++) {
            System.out.println("> "+i+" - "+Bot.parameterIDtoString(i));
        }
        System.out.println("Type the wanted parameters in one line without spaces");
        System.out.print(">> ");
        ArrayList<Integer> params = new ArrayList<Integer>();
        String line = s.nextLine();
        for (int i=0; i<line.length(); i++) {
            char at = line.charAt(i);
            int par = at-'0';
            if (par < 9 && params.indexOf(par) == -1) {
                params.add(par);
            } else if (par >= 9) {
                System.out.println("Parameter \""+at+"\" is not valid and won't be used");
            } else {
                System.out.println("Parameter \""+at+"\" was provided multiple times, but will be used only once");
            }
        }
        s.close();
        int[] intParams = new int[params.size()];
        System.out.print("Using parameters: ");
        for (int i=0; i<intParams.length; i++) {
            intParams[i] = params.get(i);
            System.out.print("\""+Bot.parameterIDtoString(intParams[i])+"\"");
            if (i != intParams.length-1) {
                System.out.print(", ");
            } else {
                System.out.println();
            }
        }
        GeneticAlgorithmRunner gr = new GeneticAlgorithmRunner(intParams, 20, 50, 0.3);
        gr.run();
        System.out.println("Done!");
    }
}