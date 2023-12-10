package pentris.game;

import java.util.ArrayList;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

//import GameEngine.*;

public class Main {
    
    public static final int numFlippable = 12; // How many flippable pentominoes there are
    public static final int[][][] database = {
        //
        //  Flippable
        //

        // P1
        {{0,0,0,0},
         {0,1,1,0},
         {0,1,1,1},
         {0,0,0,0}},

        // P2
        {{0,0,0,0},
         {0,1,1,1},
         {0,1,1,0},
         {0,0,0,0}},

        // F1
        {{0,0,0,0,0},
         {0,0,1,1,0},
         {0,1,1,0,0},
         {0,0,1,0,0},
         {0,0,0,0,0}},

        // F2
        {{0,0,0,0,0},
         {0,1,1,0,0},
         {0,0,1,1,0},
         {0,0,1,0,0},
         {0,0,0,0,0}},

        // Y1
        {{0,0,0,0},
         {0,0,1,0},
         {1,1,1,1},
         {0,0,0,0}},

        // Y2
        {{0,0,0,0},
         {1,1,1,1},
         {0,0,1,0},
         {0,0,0,0}},

        // Z1
        {{0,0,0,0,0},
         {0,1,1,0,0},
         {0,0,1,0,0},
         {0,0,1,1,0},
         {0,0,0,0,0}},

        // Z2
        {{0,0,0,0,0},
         {0,0,1,1,0},
         {0,0,1,0,0},
         {0,1,1,0,0},
         {0,0,0,0,0}},

        // N1
        {{0,0,0,0},
         {0,1,1,1},
         {1,1,0,0},
         {0,0,0,0}},
        
        // N2
        {{0,0,0,0},
         {1,1,0,0},
         {0,1,1,1},
         {0,0,0,0}},

        // L1
        {{0,0,0,0},
         {1,1,1,1},
         {1,0,0,0},
         {0,0,0,0}},
         
        // L2
        {{0,0,0,0},
         {1,0,0,0},
         {1,1,1,1},
         {0,0,0,0}},

        //
        // Non flippable
        // 

        // X
        {{0,0,0,0,0},
         {0,0,1,0,0},
         {0,1,1,1,0},
         {0,0,1,0,0},
         {0,0,0,0,0}},
        
        // V
        {{0,0,0,0,0},
         {0,1,0,0,0},
         {0,1,0,0,0},
         {0,1,1,1,0},
         {0,0,0,0,0}},

        // W
        {{0,0,0,0,0},
         {0,1,0,0,0},
         {0,1,1,0,0},
         {0,0,1,1,0},
         {0,0,0,0,0}},

        // I
        {{0,0,0,0,0},
         {0,0,0,0,0},
         {1,1,1,1,1},
         {0,0,0,0,0},
         {0,0,0,0,0}},

        // T
        {{0,0,0,0,0},
         {0,1,1,1,0},
         {0,0,1,0,0},
         {0,0,1,0,0},
         {0,0,0,0,0}},

        // U
        {{0,0,0,0,0},
         {0,1,1,1,0},
         {0,1,0,1,0},
         {0,0,0,0,0},
         {0,0,0,0,0}}
    };
    public static final int cellSize = Game.WIDTH/10; // The size of the grid blocks in pixels
    public static final int boardX = 0; // The x position of the top left corner of the grid
    public static final int boardY = 0; // The y position of the top left corner of the grid
    private Board board; // The board object the Main will use to run the game
    private Bot bot; // The bot to be used if the bot is currently playing the game
    private ArrayList<Block> blocks; // Arraylist of animated block pieces
    

    /**
     * Constructor of the game
     */
    public Main() {
        blocks = new ArrayList<Block>();
        board = new Board();
    }

    /**
     * Allows the game to run with a bot
     */
    public void enableBot() {
        bot = new Bot(new int[] {Bot.MAX_HEIGHT, Bot.CENTER_HEIGHT, Bot.ROW_TRANSITIONS, Bot.COLUMN_TRANSITIONS, Bot.NUM_HOLES, Bot.DEEPEST_WELL},
                          new double[] {2.2112334071595248,-4.730949602485206,-3.9557429442760115,1.6151794034934457,-5.0,-3.6366230443930787});
    }

    /**
     * Gets the current score of the game
     * @return The score
     */
    public int getScore() {
        return board.getNumRemovedRows();
    }

    /**
     * Object used for animating pentomino pieces
     */
    class Block {
        double x, y, xDraw, yDraw;
        Board.Cell cell;
        int pentID;
        double vspd, grav;
        boolean isVisible, stoppedAnimating;

        /**
         * Constructor
         * @param pentID The pentID of the cell
         * @param cell The cell connected to the block
         */
        public Block(int pentID, Board.Cell cell) {
            this.pentID = pentID;
            this.cell = cell;
            vspd = 0;
            grav = 1;
            x = cell.xx*Main.cellSize + Main.boardX;
            y = cell.yy*Main.cellSize + Main.boardY;
            xDraw = x;
            yDraw = y;
            isVisible = false;
        }

        /**
         * Called every game step
         */
        public void tick() {
            // Check if visible
            isVisible = false;
            for (int i=0; i<board.field.length; i++) {
                for (int j=0; j<board.field[i].length; j++) {
                    if (cell == board.field[i][j]) {
                        isVisible = true;
                        break;
                    }
                }
                if (isVisible) {
                    break;
                }
            }
            // Update position
            x = cell.xx*Main.cellSize + Main.boardX;
            y = cell.yy*Main.cellSize + Main.boardY;
            xDraw += (x-xDraw)/2;
            if (Math.abs(x-xDraw) < 0.001) {
                xDraw = x;
            }
            vspd += grav;
            yDraw += vspd;
            if (yDraw >= y) {
                yDraw = y;
                vspd = 0;
            }
            // Check if the block stopped animating
            stoppedAnimating = false;
            if (y == yDraw && x == xDraw) {
                stoppedAnimating = true;
            }
            // Delete if not used
            if (!isVisible) {
                boolean delete = true;
                for (int i=0; i<board.currentPent.shapeRotations.length; i++) {
                    Board.Shape s = board.currentPent.shapeRotations[i];
                    for (int j=0; j<s.size(); j++) {
                        if (cell.equals(s.cells[j])) {
                            delete = false;
                            break;
                        }
                    }
                    if (!delete) {
                        break;
                    }
                }
                if (delete) {
                    blocks.remove(this);
                }
            }  
        }
    }

    /**
     * Checks if the current game is over
     * @return {@code true} if over and {@code false} otherwise
     */
    public boolean isOver() {
        return board.getBoardState().equals("lose") && blocksStoppedAnimating();
    }

    /**
     * Enables the game to run with an infinite batch
     */
    public void enableBestBatch() {
        board.setFixedBatch(new int[] {0,2,4,6,13,11,14,17,15,12,9,16});
    }

    /**
     * Rotates a square matrix (without changing the inputed matrix) numRotations times to the left
     * @param matrix The matrix to rotate
     * @param numRotations The number of times to rotate the matrix to the left
     * @return The rotated matrix
     */
    public static int[][] rotateSquareMatrix(int[][] matrix, int numRotations) {
        int [][] temporaryMatrix = new int [matrix.length][matrix[0].length];
        int [][] temporaryMatrix2 = new int [temporaryMatrix.length][temporaryMatrix[0].length];
        for (int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[i].length; j++){
                temporaryMatrix[i][j] = matrix[i][j];
                }
            }
            for(int z = 0; z < numRotations; z++){
                    for(int i = 0; i < temporaryMatrix.length; i++){
                        for(int j = 0; j < temporaryMatrix[0].length; j++){
                            if(temporaryMatrix.length == 2){
                                temporaryMatrix2 [i][j] = temporaryMatrix[j][Math.abs(i-1)];

                            }
                            if(temporaryMatrix.length == 3){
                                temporaryMatrix2 [i][j] = temporaryMatrix[j][Math.abs(i-2)];

                            }
                            if(temporaryMatrix.length == 4){
                                temporaryMatrix2 [i][j] = temporaryMatrix[j][Math.abs(i-3)];

                            }
                            if(temporaryMatrix.length == 5){
                                temporaryMatrix2 [i][j] = temporaryMatrix[j][Math.abs(i-4)];

                            }
                        
                        }
                    }

                for (int i = 0; i < temporaryMatrix.length; i++){
                    for (int j = 0; j <temporaryMatrix[0].length; j++){
                        temporaryMatrix[i][j] = temporaryMatrix2[i][j]; 
                    }

                }
            }
        return temporaryMatrix;
    }

    /**
     * Creates Block objects that can be animated
     */
    public void createNewBlocks() {
        // Loop through the pentomino animations to create blocks
        for (int i=0; i<board.currentPent.shapeRotations.length; i++) {
            Board.Shape shape = board.currentPent.shapeRotations[i];
            for (int j=0; j<shape.size(); j++) {
                Block block = new Block(board.currentPent.pentID, shape.cells[j]);
                blocks.add(block);
            } 
        }
    }

    /**
     * Checks if all the blocks have stopped animating
     * @return True if they stopped animating and false otherwise
     */
    public boolean blocksStoppedAnimating() {
        for (int i=0; i<blocks.size(); i++) {
            if (!blocks.get(i).stoppedAnimating) {
                return false;
            }
        }
        return true;
    }
    

    /**
     * Main loop of the game object.
     * Runs main input and game logic steps.
     */
    public void step() {
        for (int i=0; i<blocks.size(); i++) {
            blocks.get(i).tick();
        }
        // Run the main game logic
        if (!board.getBoardState().equals("lose")) {
            if (bot == null && board.getBoardState().equals("move")) {
                // Retrieve player input
                if (Game.getInstance().checkKeyPressed(Input.D)) {
                    board.addMove(Board.MOVE_RIGHT);
                }
                if (Game.getInstance().checkKeyPressed(Input.A)) {
                    board.addMove(Board.MOVE_LEFT);
                }
                if (Game.getInstance().checkKeyPressed(Input.E)) {
                    board.addMove(Board.ROTATE_RIGHT);
                }
                if (Game.getInstance().checkKeyPressed(Input.Q)) {
                    board.addMove(Board.ROTATE_LEFT);
                }
                if (Game.getInstance().checkKeyPressed(Input.SPACE)) {
                    board.addMove(Board.HARD_DROP);
                    for (int i=0; i<blocks.size(); i++) {
                        blocks.get(i).vspd = 0;
                    }
                }
                if (Game.getInstance().checkKeyDown(Input.S)) {
                    board.addMove(Board.SOFT_DROP);
                }
            } 
            boolean updateCondition = true;
            for (int i=0; i<blocks.size(); i++) {
                if (!blocks.get(i).stoppedAnimating){
                    updateCondition = false;
                    break;
                }
            }
            board.setUpdateCondition(updateCondition);
            String state1 = board.getBoardState();
            board.tick();
            String state2 = board.getBoardState();
            // Creating a new pentomino
            if (state1.equals("init move") && state2.equals("move")) {
                createNewBlocks();
                // Retrieve the bot input
                if (bot != null) {
                    bot.chooseMoves(board);
                    for (int i=0; i<bot.botMove.length(); i++) {
                        board.addMove(bot.botMove.charAt(i));
                        if (bot.botMove.charAt(i) == Board.HARD_DROP) {
                            for (int j=0; j<blocks.size(); j++) {
                                blocks.get(j).vspd = 0;
                            }
                        }
                    }
                    bot.botMove = "";
                }
            }
        }
    }

    /**
     * Draws the game board and score
     * @param g2 The {@code Graphics2D} object to draw to
     */
    public void render(Graphics2D g2) {
        // Draw the grid
        int off = Main.cellSize/15;
        for (int i = 0; i < board.field.length; i++) {
            for (int j = 0; j < board.field[0].length; j++) {
                if (i >= 3) {
                    g2.setColor(new Color(200, 200, 200));
                } else {
                    g2.setColor(new Color(150, 150, 150));
                }
                g2.fillRect(Main.boardX+j*Main.cellSize+off, Main.boardY+i*Main.cellSize+off, Main.cellSize-off, Main.cellSize-off);
            }        
        }
        // Draw the animated blocks
        for (int i=0; i<blocks.size(); i++) {
            Block block = blocks.get(i);
            if (block.isVisible && block.cell.xx >= 0 && block.cell.xx < board.field[0].length) {
                // Check neighbouring cells
                g2.setColor(Color.BLACK);
                g2.fillRect((int) block.xDraw+off, (int) block.yDraw+off, Main.cellSize-off, Main.cellSize-off);
                if (block.cell.yy >= 3) {
                    g2.setColor(new Color(block.pentID*10, 100, 100));
                    g2.fillRect((int) block.xDraw, (int) block.yDraw, Main.cellSize-off, Main.cellSize-off);
                }
                if (block.cell.xx+1 < board.field[0].length && board.field[block.cell.yy][block.cell.xx+1] != null && board.field[block.cell.yy][block.cell.xx+1].shapeID == block.cell.shapeID) {
                    g2.setColor(Color.BLACK);
                    g2.fillRect((int) block.xDraw+Main.cellSize, (int) block.yDraw+off, off, Main.cellSize-off);
                    if (block.cell.yy >= 3) {
                        g2.setColor(new Color(block.pentID*10, 100, 100));
                        g2.fillRect((int) block.xDraw+Main.cellSize-off, (int) block.yDraw, off, Main.cellSize-off);
                    }
                }
                if (block.cell.yy+1 < board.field.length && board.field[block.cell.yy+1][block.cell.xx] != null && board.field[block.cell.yy+1][block.cell.xx].shapeID == block.cell.shapeID) {
                    g2.setColor(Color.BLACK);
                    g2.fillRect((int) block.xDraw+off, (int) block.yDraw+Main.cellSize, Main.cellSize-off, off);
                    if (block.cell.yy >= 3) {
                        g2.setColor(new Color(block.pentID*10, 100, 100));
                        g2.fillRect((int) block.xDraw, (int) block.yDraw+Main.cellSize-off, Main.cellSize-off, off);
                    }
                }
            }
        }
        // Draw the score
        g2.setColor(new Color(42, 232, 12));;
        g2.setFont(new Font("TimesRoman", Font.HANGING_BASELINE, 25));
        g2.drawString("SCORE:", Main.boardX+Main.cellSize*5+5, Main.boardY + Main.cellSize);
        g2.setFont(new Font("TimesRoman", Font.PLAIN, 25));
        g2.drawString(board.getNumRemovedRows()+"", Main.boardX+Main.cellSize*5+5, Main.boardY + Main.cellSize*2);
    }
}
