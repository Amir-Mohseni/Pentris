import java.util.ArrayList;

public class GeneticsBot {
    ArrayList <Chromosome> generation = new ArrayList<>();
    final int numberOfGenomes = 1000;

    GeneticsBot() {
        for (int i = 0; i < numberOfGenomes; i++)
            generation.add(new Chromosome());

    }


}
