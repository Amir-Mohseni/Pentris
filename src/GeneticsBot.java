import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class GeneticsBot {
    ArrayList <Chromosome> generation = new ArrayList<>();
    final int numberOfGenomes = 100;
    final int numberOfGenerations = 5;

    UI ui = new UI(5, 18, 45);

    GeneticsBot() throws CloneNotSupportedException {
        for (int i = 0; i < numberOfGenomes; i++) {
            generation.add(new Chromosome());

//            generation.getLast().print();
        }
        calculateFitness(generation);
    }



    public void evolve() throws CloneNotSupportedException {
        ArrayList <Chromosome> newGeneration = new ArrayList<>();
        newGeneration.add(this.generation.getLast());
        double sum = 0;
        for (Chromosome chromosome : this.generation) {
            sum += chromosome.fitness;
        }
        for (int i = 0; i < numberOfGenomes - 1; i++) {
            Chromosome X = pickRandomGenome(sum);
            Chromosome Y = pickRandomGenome(sum);
            newGeneration.add(X.crossover(Y));
        }
        calculateFitness(newGeneration);
        sortList(newGeneration);
        this.generation = newGeneration;
    }

    public void calculateFitness(ArrayList<Chromosome> newGeneration) throws CloneNotSupportedException {
        for (Chromosome chromosome : newGeneration) {
            Bot newBot = new Bot();
            newBot.gameBoard.chromosome = chromosome;
            Board result = newBot.run2();
            chromosome.fitness = result.evaluateScore();
        }
    }

    public Chromosome pickRandomGenome(double sum) {
        Random random = new Random();
        double P = random.nextDouble();
        double cur = sum;
        for (int i = 0; i < generation.size(); i++) {
            cur += generation.get(i).fitness;
            if(cur / sum >= P)
                return generation.get(i);
        }
        return generation.getLast();
    }

    public void sortList(ArrayList <Chromosome> arrayList) {
        arrayList.sort(new Comparator<>() {
                           @Override
                           public int compare(Chromosome obj1, Chromosome obj2) {
                               // Compare based on variable1
                               return Double.compare(obj1.fitness, obj2.fitness);
                           }
                       }
        );
    }

    public void run() throws CloneNotSupportedException {
        for (int i = 0; i < numberOfGenerations; i++) {
            System.out.println("Generation " + (i + 1));
            evolve();
            Chromosome bestChromosome = generation.getLast();
            bestChromosome.print();
            Bot newBot = new Bot();
            newBot.gameBoard.chromosome = bestChromosome;
            Board bestBoard = newBot.run2();
            ui.setState(transpose(bestBoard.grid));
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

    public static void main(String[] args) throws CloneNotSupportedException {
        GeneticsBot geneticsBot = new GeneticsBot();
        geneticsBot.run();
    }
}