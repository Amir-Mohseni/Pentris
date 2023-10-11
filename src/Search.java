/**
 * @author Department of Data Science and Knowledge Engineering (DKE)
 * @version 2022.0
 */

 import java.util.Arrays;
 import java.util.Random;

 import java.util.concurrent.TimeUnit;

/**
 * This class includes the methods to support the search of a solution.
 */
public class Search
{
    public static final int horizontalGridSize = 5;
    public static final int verticalGridSize = 6;

    public static final int NumberOfPieces = horizontalGridSize * verticalGridSize / 5;
    
    public static final char[] input = { 'W', 'Y', 'I', 'T', 'Z', 'L'};
    
    //Static UI class to display the board
    public static UI ui = new UI(horizontalGridSize, verticalGridSize, 50);

	/**
	 * Helper function which starts a basic search algorithm
	 */
    public static void search()
    {
        // Initialize an empty board
        int[][] field = new int[horizontalGridSize][verticalGridSize];

        for (int[] ints : field) {
            // -1 in the state matrix corresponds to empty square
            // Any positive number identifies the ID of the pentomino
            Arrays.fill(ints, -1);
        }
        //Start the basic search
        basicSearch(field);
    }
	
	/**
	 * Get as input the character representation of a pentomino and translate it into its corresponding numerical value (ID)
	 * @param character a character representing a pentomino
	 * @return	the corresponding ID (numerical value)
	 */
    private static int characterToID(char character) {
    	int pentID = -1; 
    	if (character == 'X') {
    		pentID = 0;
    	} else if (character == 'I') {
    		pentID = 1;
    	} else if (character == 'Z') {
    		pentID = 2;
    	} else if (character == 'T') {
    		pentID = 3;
    	} else if (character == 'U') {
    		pentID = 4;
     	} else if (character == 'V') {
     		pentID = 5;
     	} else if (character == 'W') {
     		pentID = 6;
     	} else if (character == 'Y') {
     		pentID = 7;
    	} else if (character == 'L') {
    		pentID = 8;
    	} else if (character == 'P') {
    		pentID = 9;
    	} else if (character == 'N') {
    		pentID = 10;
    	} else if (character == 'F') {
    		pentID = 11;
    	} 
    	return pentID;
    }
	
	/**
	 * Basic implementation of a search algorithm. It is not a brute force algorithms (it does not check all the possible combinations)
	 * but randomly takes possible combinations and positions to find a possible solution.
	 * The solution is not necessarily the most efficient one
	 * This algorithm can be very time-consuming
	 * @param field a matrix representing the board to be fulfilled with pentominoes
	 */
    private static void basicSearch(int[][] field){
    	Random random = new Random();
    	boolean solutionFound = false;

        int runLoops = 0;
    	
    	while (!solutionFound) {
            runLoops++;
            System.out.println("runLoops = " + runLoops);

    		solutionFound = true;
    		
    		//Empty board again to find a solution
            for (int[] ints : field) {
                Arrays.fill(ints, -1);
            }
    		
    		//Put all pentominoes with random rotation/flipping on a random position on the board
            for (char c : input) {

                //Choose a pentomino and randomly rotate/flip it
                int pentID = characterToID(c);
                int mutation = random.nextInt(PentominoDatabase.data[pentID].length);
                int[][] pieceToPlace = PentominoDatabase.data[pentID][mutation];

                //Randomly generate a position to put the pentomino on the board
                int x;
                int y;
                if (horizontalGridSize < pieceToPlace.length) {
                    //this particular rotation of the piece is too long for the field
                    x = -1;
                } else if (horizontalGridSize == pieceToPlace.length) {
                    //this particular rotation of the piece fits perfectly into the width of the field
                    x = 0;
                } else {
                    //there are multiple possibilities where to place the piece without leaving the field
                    x = random.nextInt(horizontalGridSize - pieceToPlace.length + 1);
                }

                if (verticalGridSize < pieceToPlace[0].length) {
                    //this particular rotation of the piece is too high for the field
                    y = -1;
                } else if (verticalGridSize == pieceToPlace[0].length) {
                    //this particular rotation of the piece fits perfectly into the height of the field
                    y = 0;
                } else {
                    //there are multiple possibilities where to place the piece without leaving the field
                    y = random.nextInt(verticalGridSize - pieceToPlace[0].length + 1);
                }

                //If there is a possibility to place the piece on the field, do it
                if (x >= 0 && y >= 0) {
                    addPiece(field, pieceToPlace, pentID, x, y);
                }
            }
    		//Check whether complete field is filled

            for (int[] row : field){
                for (int col : row){
                    if (col == -1){
                        solutionFound = false;
                        break;
                    }
                }
                if (solutionFound == false){
                    break;
                }
            }
    		

    		
    		if (solutionFound) {
    			//display the field
    			ui.setState(field); 
    			System.out.println("Solution found");
    			break;
    		}
    	}
    }



    private static void branchSearch() {
        // Initialize an empty board
        int[][] field = new int[horizontalGridSize][verticalGridSize];

        for (int[] ints : field) {
            // -1 in the state matrix corresponds to empty square
            // Any positive number identifies the ID of the pentomino
            Arrays.fill(ints, -1);
        }

        //Start the branch search
        recursiveSearch(field, 0);
    }

    private static boolean recursiveSearch(int[][] field, int index) {
        if(index == NumberOfPieces) {
            ui.setState(field);
            System.out.println("Solution found");
            return true;
        }
        int pentID = characterToID(input[index]);
        for (int mutation = 0; mutation < PentominoDatabase.data[pentID].length; mutation++) {
            int[][] piece = PentominoDatabase.data[pentID][mutation];
            for (int i = 0; i + piece.length < field.length; i++) {
                for (int j = 0; j + piece[0].length < field[i].length; j++) {
                    boolean validPlacement = true;
                    for (int x = 0; validPlacement && x < piece.length; x++)
                        for (int y = 0; y < piece[x].length; y++)
                            if (!(i + x < field.length) || !(j + y < field[i].length) || (piece[x][y] == 1 && field[i + x][j + y] != -1)) {
                                validPlacement = false;
                                break;
                            }
                    if (!validPlacement)
                        continue;
                    addPiece(field, piece, pentID, i, j);
                    if (recursiveSearch(field, index + 1))
                        return true;
                    removePiece(field, piece, pentID, i, j);
                }
            }
        }
        return false;
    }

    
	/**
	 * Adds a pentomino to the position on the field (overriding current board at that position)
	 * @param field a matrix representing the board to be fulfilled with pentominoes
	 * @param piece a matrix representing the pentomino to be placed in the board
	 * @param pieceID ID of the relevant pentomino
	 * @param x x position of the pentomino
	 * @param y y position of the pentomino
	 */
    public static void addPiece(int[][] field, int[][] piece, int pieceID, int x, int y)
    {
        for(int i = 0; i < piece.length; i++) // loop over x position of pentomino
        {
            for (int j = 0; j < piece[i].length; j++) // loop over y position of pentomino
            {
                if (piece[i][j] == 1)
                {
                    // Add the ID of the pentomino to the board if the pentomino occupies this square
					// i and j are offsets
                    field[x + i][y + j] = pieceID;
                }
            }
        }
    }

    public static void removePiece(int[][] field, int[][] piece, int pieceID, int x, int y)
    {
        for(int i = 0; i < piece.length; i++) // loop over x position of pentomino
        {
            for (int j = 0; j < piece[i].length; j++) // loop over y position of pentomino
            {
                if (piece[i][j] == 1)
                {
                    // remove the ID of the pentomino to the board if the pentomino occupies this square
                    // i and j are offsets
                    field[x + i][y + j] = -1;
                }
            }
        }
    }

	/**
	 * Main function. Needs to be executed to start the basic search algorithm
	 */
    public static void main(String[] args)
    {
        //search();
        branchSearch();
    }
}