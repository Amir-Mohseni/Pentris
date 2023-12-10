package pentris.game;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.time.LocalDateTime;

public class Bot {
    Board model;
    Main game;
    String botMove = "";
    Board trainingGame;
    ArrayList<Integer> gameHistory;
    int[] parameterList;

    public static final int
    AGGREGATE_HEIGHT = 0,
    MAX_HEIGHT = 1,
    CENTER_HEIGHT = 2,
    ROW_TRANSITIONS = 3,
    COLUMN_TRANSITIONS = 4,
    NUM_HOLES = 5,
    DELETED_ROWS = 6,
    DEEPEST_WELL = 7,
    NUM_PITS = 8;

    /**
     * Constructor.
     * Creates a new Bot
     * @param parameterList List of parameters to use for the bot
     * @param genotype The genotype (weights) of the bot
     */
    public Bot(int[] parameterList, double[] genotype) {
        model = new Board();
        botMove = "";
        minAlele = -5;
        maxAlele = 5;
        this.parameterList = parameterList;
        if (genotype != null) {
            this.genotype = genotype;
        } else {
            this.genotype = new double[parameterList.length];
            for (int i=0; i<this.genotype.length; i++) {
                this.genotype[i] = Math.random()*(maxAlele-minAlele) + minAlele;
            }
        }
        gameHistory = new ArrayList<Integer>();
        parameterList = new int[0];
    }

    /**
     * Chooses the best move for the bot based on the current game board
     * @param gameBoard The current game board to use for finding the best moves
     */
    public void chooseMoves(Board gameBoard) {
        gameBoard.storeCheckpoint();
        model = gameBoard.getLastCheckpoint();
        String[] allMoves = new String[1000];
        int movesLength = 0;
        for (int rotation=0; rotation<4; rotation++) {
            model.storeCheckpoint(); // Store the board state before performing rotations
            model.removeShape(model.currentPent.getCurrentRotation());
            for (int i=0; i<rotation; i++) {
                model.rotateRight();
            }
            model.placeShape(model.currentPent.getCurrentRotation());
            for (int xx=-5; xx<=5; xx++) {
                model.storeCheckpoint(); // Store the board state before moving left/right
                model.removeShape(model.currentPent.getCurrentRotation());
                if (xx < 0) {
                    for (int i=0; i<-xx; i++) {
                        model.moveLeft();
                    }
                } else if (xx > 0) {
                    for (int i=0; i<xx; i++) {
                        model.moveRight();
                    }
                }
                model.placeShape(model.currentPent.getCurrentRotation());
                // Drop it and store that move
                String move = "";
                for (int i=0; i<rotation; i++) {
                    move += Board.ROTATE_RIGHT;
                }
                for (int i=0; i<Math.abs(xx); i++) {
                    if (xx < 0) {
                        move += Board.MOVE_LEFT;
                    } else if (xx > 0) {
                        move += Board.MOVE_RIGHT;
                    }
                }
                move += Board.HARD_DROP;
                allMoves[movesLength] = move;
                movesLength++;
                model = model.getLastCheckpoint(); // Restore to model before moving left/right
            }
            model = model.getLastCheckpoint(); // Restore to model before rotating
        }
        // Find the move with the best utility
        String bestMove = "";
        double bestUtility = -10000;
        for (int i=0; i<movesLength; i++) {
            model.storeCheckpoint();
            int score1 = model.getNumRemovedRows();
            for (int j=0; j<allMoves[i].length(); j++) {
                model.addMove(allMoves[i].charAt(j));
            }
            while (!(model.getBoardState().equals("init move") || model.getBoardState().equals("lose"))) {
                model.tick();
            }
            int score2 = model.getNumRemovedRows();
            double utility = utilityFunction(model.getState(), score2-score1);
            model = model.getLastCheckpoint();
            if (utility > bestUtility) {
                bestMove = allMoves[i];
                bestUtility = utility;
            }
        }
        botMove = bestMove;
    }

    /**
     * Calculates a move utility based on the provided board state
     * @param boardState 2D array of 1s and 0s representing the board state
     * @param deletedRows The number of deleted rows between 2 moves
     * @return The utility
     */
    public double utilityFunction(int[][] boardState, int deletedRows){
        // Aggregate Height: Sum of all the columns height.
        // maxHeight: The height of the highest column
        // deepestWell: The size of the largest empty column from the top down
        int AggregateHeight = 0;
        double centerHeight = 0;
        int maxHeight = 0;
        int[] heightArray = new int[boardState[0].length];
        for (int i = 0; i < boardState[0].length; i++) {
            for (int j = 0; j < boardState.length; j++) {
                int height = 0;
                if(j == boardState.length || boardState[j][i] == 1){
                    height = boardState.length-j;
                    heightArray[i] = height;
                    AggregateHeight += height;
                    centerHeight += (double) height / ((Math.abs(i-boardState[0].length/2.0)+1));
                    if (height > maxHeight) {
                        maxHeight = height;
                    }
                    break;
                }
            }
        }

        // The Number of Holes on the board
        // A hole is defined as any empty cell that has a filled cell somewhere above it in the column
        int numberOfHoles = 0;
        for (int i = 1; i < boardState.length; i++) {
            for (int j = 0; j < boardState[i].length; j++) {
                if (boardState[i][j] == 0) {
                    for (int h=i-1; h>=0; h--) {
                        if (boardState[h][j] == 1) {
                            numberOfHoles += 1;
                            break;
                        }
                    }
                }
            }  
        }

        // Row Transitions and Column Transitions;
        int RowTransitions = 0;
        int ColTransitions = 0;
        for (int i = 0; i < boardState[0].length; i++) {
            for (int j = 0; j < boardState.length; j++) {
                if(i-1 >= 0 && boardState[j][i] == 1 && boardState[j][i-1] == 0){
                    RowTransitions +=1;
                }
                if(i+1 < boardState[0].length && boardState[j][i] == 1 && boardState[j][i+1] == 0){
                    RowTransitions +=1;
                }

                if(j-1 >= 0 && boardState[j][i] == 1 && boardState[j-1][i] == 0){
                    ColTransitions +=1;
                }
                if(j+1 < boardState.length && boardState[j][i] == 1 && boardState[j+1][i] == 0){
                    ColTransitions +=1;
                } 
            }      
        }

        //Number of Pits
        int pits = 0;
        int counter = 0;
        for (int i = 0; i < boardState[0].length; i++) {
            for (int j = 0; j < boardState.length; j++) {
                if(boardState[j][i] == 1){
                    counter +=1;
                    break;
                } 
            }
            if(counter == 0){
                pits +=1;
            }
            counter = 0; 
        }

        // Deepest Well; Founds the deep value according the sides of columns.
        int[] deepest = new int[boardState[0].length];
        for (int i = 0; i < heightArray.length; i++) {
            if(i!=0 && i != heightArray.length -1){
                if(heightArray[i-1]-heightArray[i] < heightArray[i+1]-heightArray[i]){
                    deepest[i] = heightArray[i-1]-heightArray[i];
                }
                else{
                    deepest[i] =  heightArray[i+1]-heightArray[i];
                }
            }
            else if(i == 0){
                deepest[i] = heightArray[i+1]-heightArray[i];
            }
            else if(i == heightArray.length -1){
                deepest[i] = heightArray[i-1]-heightArray[i];
            }
        }
        
        int deepestWell = deepest[0];
        for(int i=1;i<deepest.length;i++){ 
            if(deepest[i] > deepestWell){ 
                deepestWell = deepest[i]; 
            }
        } 

        // Check lose condition
        for (int i=0; i<3; i++) {
            for (int j=0; j<boardState[i].length; j++) {
                if (boardState[i][j] == 1) {
                    return -1000;
                }
            }
        }

        double[] allParameters = new double[] {AggregateHeight, maxHeight, centerHeight, RowTransitions, ColTransitions, numberOfHoles, deletedRows, deepestWell, pits};

        //genotype = new double[] {-5.0, -5.0, -4.21327562580109, 0, -0.5663311444791244, -4.872798075149198, 1.8944890993796275};
        double utility = 0;
        for (int i=0; i<parameterList.length; i++) {
            utility += genotype[i] * allParameters[parameterList[i]];
        }
        return utility;
    }

    /**
     * Stores a list of bot scores in a csv file
     */
    public void storeScoreData() {
        FileWriter fw;
        try {
            String tempName = LocalDateTime.now()+"";
            String timeName = "";
            for (int i=0; i<tempName.length(); i++) {
                if (tempName.charAt(i) == ':') {
                    timeName += '-';
                } else {
                    timeName += tempName.charAt(i);
                }
            }
            File bf = new File("src/pentris/Results/Bot runs/Bot"+timeName+".csv");
            fw = new FileWriter(bf);
            String scores = "";
            for (int i=0; i<gameHistory.size(); i++) {
                scores += gameHistory.get(i)+",\n";
            }
            for (int i=0; i<parameterList.length; i++) {
                scores += parameterIDtoString(parameterList[i])+",\n";
            }
            for (int i=0; i<genotype.length; i++) {
                scores += genotype[i]+",";
                if (i != genotype.length-1) {
                    scores += "\n";
                }
            }
            fw.append(scores);
            fw.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Error. File not found!");
            return;
        }
    }

    
    //
    // GENETIC ALGORITHM METHODS AND VARIABLES
    //
    private double[] genotype;
    private double minAlele, maxAlele;
    private double fitness = -1;

    /**
     * Runs 100 model games and takes the median score and sets it to the fitness
     */
    public void calculateFitness(int numGames) {
        int[] scores = new int[numGames];
        for (int i=0; i<numGames; i++) {
            // Play this game
            trainingGame = new Board();
            while (!trainingGame.getBoardState().equals("lose")) {
                String state1 = trainingGame.getBoardState();
                trainingGame.tick();
                String state2 = trainingGame.getBoardState();
                // Setting the moves
                if (state1.equals("init move") && state2.equals("move")) {
                    chooseMoves(trainingGame);
                    for (int n=0; n<botMove.length(); n++) {
                        trainingGame.addMove(botMove.charAt(n));
                    }
                    botMove = "";
                } 
            }
            scores[i] = trainingGame.getNumRemovedRows();
            gameHistory.add(trainingGame.getNumRemovedRows());
        }
        Arrays.sort(scores, 0, scores.length);
        if (scores.length%2 == 0) {
            fitness = (scores[scores.length/2-1]+scores[scores.length/2]) / 2.0;
        } else {
            fitness = scores[scores.length/2];
        }
    }


    /**
     * Gets the fitness of this individual
     * @return The fitness
     */
    public double getFitness() {
        return fitness;
    }

    /**
     * Gets the current genotype of this bot
     * @return An array containing the aleles
     */
    public double[] getGenotype() {
        return genotype;
    }

    /**
     * Mutate this individual
     * @param mutationRate The chance that an alele will mutate
     */
    public void mutate(double mutationRate) {
        for (int i=0; i<genotype.length; i++) {
            if (Math.random() < mutationRate) {
                genotype[i] += (Math.random()*2-1)*(maxAlele-minAlele)/2; // Change the alele by max 50% of the possible range of alele values
                if (genotype[i] > maxAlele) {
                    genotype[i] = maxAlele;
                }
                if (genotype[i] < minAlele) {
                    genotype[i] = minAlele;
                }
            }
        }
    }

    //
    // Finding the best batch 
    //
    private int n = 0;

    /**
     * Finds the best batch (infinite batch).
     * Works recursively
     * @param batch The previous batch state (initially should be an integer array of length 12 filled with -1)
     * @param numGames The number of games to run for
     */
    public void findBestSolution(int[] batch, int numGames) {
        // Check if batch is full
        boolean full = true;
        int posEmpty = -1;
        for (int i=0; i<batch.length; i++) {
            if (batch[i] == -1) {
                full = false;
                posEmpty = i;
                break;
            }
        }
        // If it's a full pentomino batch, check if it's an infinite batch
        if (full) {
            Board batchGame = new Board();
            batchGame.setFixedBatch(batch);
            int moves = 0;
            // Run the game for a maximum of 12 pentominoes
            while (!batchGame.getBoardState().equals("lose") && moves <= batch.length) {
                String state1 = batchGame.getBoardState();
                batchGame.tick();
                String state2 = batchGame.getBoardState();
                // Setting the moves
                if (state1.equals("init move") && state2.equals("move")) {
                    chooseMoves(batchGame);
                    for (int m=0; m<botMove.length(); m++) {
                        batchGame.addMove(botMove.charAt(m));
                    }
                    moves++;
                    botMove = "";
                } 
            }
            // Check if the score is 12, and if so, store the solution
            if (batchGame.getNumRemovedRows() == batch.length) {
                System.out.println(n+" Found solution! "+Arrays.toString(batch));
                // Store the data
                try {
                    File f = new File("src/pentris/Results/Bot runs/BestBatches.csv");
                    if (f.createNewFile()) {
                        // Notify Java
                    } 
                    BufferedReader bw = new BufferedReader(new FileReader(f));
                    String data = "";
                    while (bw.ready()) {
                        data += bw.readLine()+"\n";
                    }
                    for (int i=0; i<batch.length; i++) {
                        data += batch[i]+",";
                    }
                    bw.close();
                    FileWriter fw = new FileWriter(f);
                    fw.append(data);
                    fw.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } 
            n++;
        } else {
            // Find which pentominoes were already used
            boolean[] usedPents = new boolean[Main.database.length];
            for (int i=0; i<batch.length; i++) {
                if (batch[i] == -1) {
                    break;
                } else {
                    usedPents[batch[i]] = true;
                    // Count flips as the same pentomino
                    if (batch[i] < Main.numFlippable) {
                        if (batch[i]%2 == 0) {
                            usedPents[batch[i]+1] = true;
                        } else {
                            usedPents[batch[i]-1] = true;
                        }
                    }
                }
            }
            // Add new pentomino and try with that
            for (int i=0; i<usedPents.length; i++) {
                // Termination
                if (n >= numGames) {
                    return;
                }
                if (!usedPents[i]) {
                    batch[posEmpty] = i;
                    int[] batchCopy = new int[batch.length];
                    for (int p=0; p<batch.length; p++) {
                        batchCopy[p] = batch[p];
                    }
                    findBestSolution(batchCopy, numGames);
                }
            }
        }
    }

    /**
     * Gives the name of a utility parameter based on the id of that utility
     * @param paramID The ID of the utility to get the name for
     * @return The String representation of the name
     */
    public static String parameterIDtoString(int paramID) {
        if (paramID == Bot.AGGREGATE_HEIGHT) return "Aggregate height";
        else if (paramID == Bot.MAX_HEIGHT) return "Max height";
        else if (paramID == Bot.CENTER_HEIGHT) return "Center height";
        else if (paramID == Bot.ROW_TRANSITIONS) return "Row transitions";
        else if (paramID == Bot.COLUMN_TRANSITIONS) return "Column transitions";
        else if (paramID == Bot.DELETED_ROWS) return "Deleted rows";
        else if (paramID == Bot.DEEPEST_WELL) return "Deepest well";
        else if (paramID == Bot.NUM_PITS) return "Number of pits";
        else if (paramID == Bot.NUM_HOLES) return "Number of holes";
        else return "Not a valid parameter";
    }
}
