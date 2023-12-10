package pentris.game;

import java.util.ArrayList;
import java.util.Random;

public class Board {
    public Cell[][] field; // Creates a new field matrix
    public Pentomino currentPent; // The current pentomino being moved around
    private int numPlacedShapes; // The number of shapes that were placed in the board
    private ArrayList<Integer> batch; // Batch of pentominoes to be placed, is randomly regenerated when needed
    private boolean removedARow; // Should be true at start of a move, helps terminate update method
    private String moves; // String containing the character codes for move sequences
    private String boardState; // String containing the current game state of the board
    private final int NORMAL_TIMER = Game.wantedFps/2, SOFT_TIMER = Game.wantedFps/5; // Timers for dropping pentominoes
    private int t, timer; // Timer variables for dropping pentominoes
    private boolean softDrop; // Boolean to check whether the soft drop input is being held
    private boolean updateCondition; // Boolean to check whether the board is alowed to proceed with updating (used for animation delays)
    private Board checkpoint; // Stores a previous board state as a new board object
    private int numRemovedRows; // The total number of removed rows in the board
    private boolean fixedBatch; // Defines whether the batch stays the same or is randomly generated
    private int fixedPos; // Position of the pentomino selected in the fixed batch

    public static final char 
    MOVE_RIGHT = 'D',
    MOVE_LEFT = 'A',
    ROTATE_RIGHT = 'E',
    ROTATE_LEFT = 'Q',
    SOFT_DROP = 'S',
    HARD_DROP = ' ';
    
    /**
     * Constructor.
     * Creates a new board.
     */
    public Board() {
        fixedBatch = false;
        field = new Cell[18][5];
        numPlacedShapes = 0;
        batch = new ArrayList<Integer>();
        removedARow = true;
        moves = "";
        boardState = "init move";
        timer = NORMAL_TIMER;
        t = 0;
        softDrop = false;
        updateCondition = true;
        numRemovedRows = 0;
    }
    
    /**
     * Class used for storing board spaces.
     */
    class Cell {
        int xx, yy;
        int pentID, shapeID;
    }

    /**
     * Class for keeping track of the current active pentomino
     */
    class Pentomino {
        int currentRotation;
        Shape[] shapeRotations = new Shape[4];
        int pentID;
       
        /**
         * Creates a pentomino 
         * @param id The ID of the pentomino 
         */
        Pentomino(int id) {
            this.pentID = id;
            createShapes();
        }

        /**
         * Creates the shape rotations (all 4) by rotating the original shape matrix from the pentomino database in Main.
         * Uses the Main.rotateSquareMatrix() method to rotate the database matrices
         */
        void createShapes() {
            for(int k = 0; k<4 ; k++){
                Shape shape = new Shape(new Cell[0]);
                int[][] current = Main.rotateSquareMatrix(Main.database[pentID],k);
                for (int i = 0; i < current.length; i++) {
                    for (int j = 0; j < current[0].length; j++) {
                        if(current[i][j] != 0){
                            Cell cell = new Cell();
                            cell.pentID = pentID;
                            cell.xx = j;
                            cell.yy = i;
                            shape.addCell(cell);
                             
                        }   
                    }  
                }
                shapeRotations[k] = shape;
            }   
        }

        /**
         * Gets the Shape of the current rotation of the pentomino
         * @return The shape
         */
        Shape getCurrentRotation() {
            /*if(currentRotation > 3){
                currentRotation %= 4;
            }*/
            return shapeRotations[currentRotation];
        }

        /**
         * Moves the pentomino by a certain amount.
         * Does so by moving all the shape rotations
         * @param dx The amount to move on the x-axis
         * @param dy The amount to move on the y-axis
         */
        void move(int dx, int dy) {
            for(int i = 0; i < 4; i++){
                shapeRotations[i].move(dx, dy);
            }
        }

        /**
         * Rotates the pentomino to the right.
         * Does so by switching between the shapeRotations.
         */
        void rotateRight() {
            currentRotation += 3;
            currentRotation %= 4;
        }

        /**
         * Rotates the pentomino to the left.
         * Does so by switching between the shapeRotations
         */
        void rotateLeft() {
            currentRotation += 1;
            currentRotation %= 4;
        }
    }
    
    /**
     * Class for clustering a pieces of the board into a shape.
     * Also used for controlling the pentomino-
     */
    class Shape {
        Cell[] cells = new Cell[field.length*field[0].length];
        int cellNum = 0;
        int shapeID;
        /**
         * Creates a Shape containing a given list of cells.
         * Sets the shapeID to numPlacedShapes.
         * Has to set all of those cell shapeIDs to it's own shapeID
         * Adds 1 to the board numPlacedShapes
         * @param cellList The list of cells
         */
        Shape(Cell[] cellList) {
            
            this.shapeID = numPlacedShapes;
            numPlacedShapes +=1;
            for (int i = 0; i < cellList.length; i++) {
                addCell(cellList[i]);
            }
            
        }


        /**
         * Moves the shape by a given amount.
         * Does so by moving all the Cell objects (xx and yy coordinates)
         * @param dx The amount to move it on the x-axis
         * @param dy The amount to move it on the y-axis
         */
        void move(int dx, int dy) {
            for(int j = 0; j < this.size(); j++){
                if(dx != 0){
                    this.cells[j].xx += dx;
                }
                if(dy != 0){
                    this.cells[j].yy += dy;
                }
            }
        }

        /**
         * Adds a cell to the shapes cells list
         * @param cell The new cell to add
         */
        void addCell(Cell cell) {
            cell.shapeID = shapeID;
            cells[cellNum] = cell;
            cellNum++;
        }

        /**
         * Returns the size of the shape (How many cells it contains)
         * @return The size
         */
        int size() {
            return cellNum;
        }
    }

    /**
     * Checks if a shape can be placed into the current field 
     * Does so by checking if there is a Cell placed into the field that matches the xx, yy coordinate of one of the shapes cells or if the xx, yy is out of bounds
     * @param shape The shape to check
     */
    public boolean isPlaceable(Shape shape) {
        for (int i = 0; i < shape.size(); i++) {
            if (shape.cells[i].xx < field[0].length && shape.cells[i].xx >= 0 && shape.cells[i].yy < field.length && shape.cells[i].yy >= 0) {
                if (field[shape.cells[i].yy][shape.cells[i].xx] != null) {
                    return false;
                }
            }
            else {
                return false;
            }
        }
        return true;
    }

    /**
     * Places a shape into the field.
     * Sets field positions found in the xx, yy coordinates of the shapes cells to the corresponding cells
     * @param shape The shape to place
     */
    public void placeShape(Shape shape) {
        if(isPlaceable(shape)){
            for (int i = 0; i < shape.size(); i++) {
                field[shape.cells[i].yy][shape.cells[i].xx] = shape.cells[i];
            }
        }

    }

    /**
     * Removes a shape from the current field.
     * Does so by Finding all the xx, yy positions of cells in the shape, and removes those positions from the field
     * @param shape
     */
    public void removeShape(Shape shape) {
        for (int i = 0; i < shape.size(); i++) {
                field[shape.cells[i].yy][shape.cells[i].xx] = null;
        }

    }

    /**
     * Creates a temporary checkpoint board.
     * Useful for the model in the bot, to restore moves.
     */
    public void storeCheckpoint() {
        Board prevCheckpoint = checkpoint;
        checkpoint = new Board();
        checkpoint.checkpoint = prevCheckpoint;
        checkpoint.currentPent = new Pentomino(currentPent.pentID);
        for (int i=0; i<currentPent.shapeRotations.length; i++) {
            Shape s1 = new Shape(new Cell[0]);
            Shape s2 = currentPent.shapeRotations[i];
            for (int j=0; j<s2.size(); j++) {
                Cell c = new Cell();
                c.xx = s2.cells[j].xx;
                c.yy = s2.cells[j].yy;
                c.pentID = s2.cells[j].pentID;
                c.shapeID = s2.cells[j].shapeID;
                s1.addCell(c);
            }
            checkpoint.currentPent.shapeRotations[i] = s1;
        }
        checkpoint.currentPent.currentRotation = currentPent.currentRotation;
        checkpoint.boardState = boardState;
        Cell[][] newField = new Cell[field.length][field[0].length];
        for (int i=0; i<newField.length; i++) {
            for (int j=0; j<newField[i].length; j++) {
                if (field[i][j] != null) {
                    newField[i][j] = new Cell();
                    newField[i][j].xx = field[i][j].xx;
                    newField[i][j].yy = field[i][j].yy;
                    newField[i][j].pentID = field[i][j].pentID;
                    newField[i][j].shapeID = field[i][j].shapeID;
                }
            }
        }
        checkpoint.numPlacedShapes = numPlacedShapes;
        checkpoint.numRemovedRows = numRemovedRows;
        checkpoint.field = newField;
        checkpoint.removeShape(checkpoint.currentPent.getCurrentRotation());
        checkpoint.placeShape(checkpoint.currentPent.getCurrentRotation());
    }

    /**
     * Returns the last stored position of the board.
     * Useful for the bot model to reset moves.
     */
    public Board getLastCheckpoint() {
        return checkpoint;
    }

    /**
     * Return the state of the board as an array of 0 and 1 where 1=filled and 0=empty
     * @return The state array
     */
    public int[][] getState() {
        int[][] state = new int[field.length][field[0].length];
        for (int i=0; i<state.length; i++) {
            for (int j=0; j<state[i].length; j++) {
                if (field[i][j] != null) {
                    state[i][j] = 1;
                }
            }
        }
        return state;
    }

    /**
     * Gets the number of removed rows since the start of existance of this object
     * @return The number of removed rows
     */
    public int getNumRemovedRows() {
        return numRemovedRows;
    }

    /**
     * Updates the board.
     * Does a cascade row update.
     * Deletes all filled rows.
     * Finds the connected Shapes and drops them.
     */
    public void update() {
        int[] targetRows = new int[field.length];
        int numRows = 0;
        int count = 0; //Counts the filled cells in the row
        int i = 0;
        int j = 0;
        removedARow = false;
        while(true){
            if(j==field[0].length){
                count = 0;
                i++;
                j=0;
                if(i == field.length){
                    break;
                }
            }     
            if(field[i][j] != null){ 
                count +=1;
            }
            if(count == field[0].length){
                targetRows[numRows] = i;
                numRows++;
            }
            j++;
        }
        if(numRows > 0){
            removedARow = true;
            for(i = 0; i<numRows; i++){
                numRemovedRows++;
                for(j =0; j<field[0].length;j++){
                    field[targetRows[i]][j] = null; 
                }
            }
        }
        Shape[] ShapeList = new Shape[field.length*field[0].length]; // This can fit all shapes even if the entire board is filled with shapes of 1 block
        int shapeNum = 0;
        for(i = 0; i < field.length;i++){
            for(j=0;j<field[i].length;j++){
                if(!(field[i][j]==null)){
                    int counter = 0;
                    for(int k = 0; k < shapeNum; k++){
                        for(int l = 0; l< ShapeList[k].size(); l++){
                            if(field[i][j].shapeID == ShapeList[k].cells[l].shapeID) {
                                counter +=1;
                                break;
                            }
                        }
                    }
                    if(counter == 0){
                        ShapeList[shapeNum] = findShapeInBoard(new Shape(new Cell[0]), j, i);
                        shapeNum++;
                    }
                }
            }
        }
        while(true){
            int param = 0;
            for(i = 0; i < shapeNum; i++){
                Shape shape = ShapeList[i];
                removeShape(shape);
                shape.move(0,1);
                if(isPlaceable(shape)){
                    param +=1;
                } else {
                    shape.move(0,-1);
                }
                placeShape(shape);
            }

            if(param == 0){
                break;
            }
        } 
    } 

    /**
     * Finds a Shape by finding all connected cells that have the same shapeID
     * @param xx The starting x position in the field to check
     * @param yy The starting y position in the field to check
     * @return The shape found at the given position
     */
    public Shape findShapeInBoard(Shape shape, int xx, int yy) {  
        
        int previous_shapeID = field[yy][xx].shapeID;
        
        shape.addCell(field[yy][xx]);
        

        if(xx > 0){ // To prevent error out of bounds.
            if(field[yy][xx-1] != null){
                if(previous_shapeID == field[yy][xx-1].shapeID){
                    findShapeInBoard(shape, xx-1, yy);
                }
            }
        }
    
        if(xx<field[0].length-1){ // To prevent error out of bounds.
            if(field[yy][xx+1] != null){
                if(previous_shapeID == field[yy][xx+1].shapeID){
                    findShapeInBoard(shape, xx+1, yy);
                }
            }
            
        }

        if(yy>0){ // To prevent error out of bounds.
            if(field[yy-1][xx] != null){
                if(previous_shapeID == field[yy-1][xx].shapeID){
                    findShapeInBoard(shape, xx, yy-1);
                }
            }

        }
        
        if(yy <field.length-1){ // To prevent error out of bounds.
            if(field[yy+1][xx] != null){
                if(previous_shapeID == field[yy+1][xx].shapeID){
                    findShapeInBoard(shape, xx, yy+1);
                }
            }
        } 
        
        return shape;
    }

    /**
     * Sets a fixed batch for the pentominoes. 
     * @param pentIDs The array of pentominoe IDs in the batch
     */
    public void setFixedBatch(int[] pentIDs) {
        fixedBatch = true;
        fixedPos = 0;
        batch = new ArrayList<Integer>();
        for (int i=0; i<pentIDs.length; i++) {
            batch.add(pentIDs[i]);
        }
    }

    /**
     * Creates a new pentomino from the randomly generated batch
     */
     public void createNewPentomino() {
        if (fixedBatch) {
            currentPent = new Pentomino(batch.get(fixedPos));
            fixedPos++;
            fixedPos %= batch.size();
        } else {
            createBatch();
            currentPent =  new Pentomino(batch.get(0));
            batch.remove(0);
        }
        moveShapeUp();
    }

    //Creates the batch of 18 pentominos
    public void createBatch(){
        Random random = new Random();
        if(batch.size() == 0 || batch.size() == 1){
            while (batch.size() < Main.database.length) {
                int n = random.nextInt(Main.database.length);
                while (batch.indexOf(n) != -1) {
                    n = random.nextInt(Main.database.length);
                }
                batch.add(n);
            }
        }
    }

    /**
     * Finds the highest y position of a cell in the current pentomino
     * @return The highest y position
     */
    public int scanShapeForY() {
        int yySmallest = 100;
        for (int j = 0; j < currentPent.getCurrentRotation().size(); j++){
                if(currentPent.getCurrentRotation().cells[j].yy < yySmallest){
                    yySmallest = currentPent.getCurrentRotation().cells[j].yy;
                }     
            }
        return yySmallest;
    }

    /**
     * Moves the current pentomino to the top of the board
     */
    public void moveShapeUp() {
            currentPent.move(0,-scanShapeForY());
    }

    /**
     * Moves the current pentomino right
     */
    public void moveRight() {
        currentPent.move(1,0);
        if (!isPlaceable(currentPent.getCurrentRotation())) {
            currentPent.move(-1,0);
        }
    }

    /**
     * Moves the current pentomino left
     */
    public void moveLeft() {
        currentPent.move(-1,0);
        if (!isPlaceable(currentPent.getCurrentRotation())) {
            currentPent.move(1,0);
        }
    }

    /**
     * Rotates the current pentomino right
     */
    public void rotateRight() {
        currentPent.rotateRight();
        if (!isPlaceable(currentPent.getCurrentRotation())) {
            // Try moving it around
            boolean placed = false;
            for (int j=0; j<=1; j++) {
                for (int i=0; i<=1; i++) {
                    currentPent.move(i,j);
                    if (!isPlaceable(currentPent.getCurrentRotation())) {
                        currentPent.move(-i,-j);
                    } else {
                        placed = true;
                        break;
                    }
                    currentPent.move(-i,j);
                    if (!isPlaceable(currentPent.getCurrentRotation())) {
                        currentPent.move(i,-j);
                    } else {
                        placed = true;
                        break;
                    }
                }
                if (placed) {
                    break;
                }
            }
            if (!placed) {
                currentPent.rotateLeft();
            } 
        } 
    }

    /**
     * Rotates the current pentomino left
     */
    public void rotateLeft() {
        currentPent.rotateLeft();
        if (!isPlaceable(currentPent.getCurrentRotation())) {
            // Try moving it around
            boolean placed = false;
            for (int j=0; j<=1; j++) {
                for (int i=0; i<=1; i++) {
                    currentPent.move(i,j);
                    if (!isPlaceable(currentPent.getCurrentRotation())) {
                        currentPent.move(-i,-j);
                    } else {
                        placed = true;
                        break;
                    }
                    currentPent.move(-i,j);
                    if (!isPlaceable(currentPent.getCurrentRotation())) {
                        currentPent.move(i,-j);
                    } else {
                        placed = true;
                        break;
                    }
                }
                if (placed) {
                    break;
                }
            }
            if (!placed) {
                currentPent.rotateRight();
            } 
        }
    }

    /**
     * Check if the game is over
     */
    public boolean gameOver() {
        for (int i=0; i<3; i++) {
            for (int j=0; j<field[i].length; j++) {
                if (field[i][j] != null) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Hard drops the current pentomino
     */
    public void hardDrop() {
        while (isPlaceable(currentPent.getCurrentRotation())) {
            currentPent.move(0,1);
        }
        currentPent.move(0,-1);
    }

    /**
     * Adds a move to the list of moves that have to be performed
     * @param move
     */
    public void addMove(char move) {
        if (move == Board.SOFT_DROP) {
            softDrop = true;
            return;
        }
        moves += move;
    }

    /**
     * Sets the update condition of the board
     * @param val True if the condition is met and false otherwise
     */
    public void setUpdateCondition(boolean val) {
        updateCondition = val;
    }

    /**
     * Gets the state of the board
     * @return The state
     */
    public String getBoardState() {
        return boardState;
    }

    /**
     * Does a single tick of the board
     */
    public void tick() {
        if (boardState.equals("move")) {
            removeShape(currentPent.getCurrentRotation());
            for (int i=0; i<moves.length(); i++) {
                char move = moves.charAt(i);
                if (move == Board.MOVE_RIGHT) {
                    moveRight();
                }
                if (move == Board.MOVE_LEFT) {
                    moveLeft();
                }
                if (move == Board.ROTATE_RIGHT) {
                    rotateRight();
                }
                if (move == Board.ROTATE_LEFT) {
                    rotateLeft();
                }
                if (move == Board.HARD_DROP) {
                    hardDrop();
                    boardState = "update";
                    break;
                }
            }
            moves = "";
            if (softDrop) {
                timer = SOFT_TIMER;
            } else {
                timer = NORMAL_TIMER;
            }
            softDrop = false;
            // Tick
            if (!boardState.equals("update")) {
                t++;
                if (t >= timer) {
                    currentPent.move(0,1);
                    if (!isPlaceable(currentPent.getCurrentRotation())) {
                        currentPent.move(0,-1);
                        boardState = "update";
                    }
                    t = 0;
                }
            }
            placeShape(currentPent.getCurrentRotation());
        } else if (boardState.equals("update")) {
            // Check for game over
            if (gameOver()) {
                boardState = "lose";
            } else {
                if (updateCondition) {
                    update();
                    if (!removedARow) {
                        boardState = "init move";
                    }
                }
            }
        } else if (boardState.equals("init move")) {
            createNewPentomino();
            boardState = "move";
            moves = "";
            t = 0;
            removedARow = true;
        }
    }
}
