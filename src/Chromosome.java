import java.util.Random;

public class Chromosome {
    double[] weights;
    static double mutationRate = 0.08;

    Chromosome(double[] weights) {
        this.weights = weights;
    }

    Chromosome() {
        Random rand = new Random();
        this.weights = new double[4];
        for (int i = 0; i < 4; i++)
            this.weights[i] = rand.nextDouble() * 10 - 5;
    }

    public Chromosome crossover(Chromosome otherChromosome) {
        Chromosome child = new Chromosome();
        for (int i = 0; i < 4; i++) {
            if(i < 2)
                child.weights[i] = this.weights[i];
            else
                child.weights[i] = otherChromosome.weights[i];
            child.weights[i] = mutation(child.weights[i]);
        }
        return child;
    }

    public double mutation(double val) {
        Random rand = new Random();
        if(rand.nextDouble() < mutationRate)
            return rand.nextDouble() * 10 - 5;
        else
            return val;
    }
}
