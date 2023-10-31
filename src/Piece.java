public class Piece {
    int id;
    int rotation;
    int NumberOfRotations;
    int[][] grid;
    Piece(int id, int rotation) {
        this.id = id;
        this.rotation = rotation;
        this.grid = PentominoDatabase.data[id][rotation];
        this.NumberOfRotations = PentominoDatabase.data[id].length;
    }

    void rotate() {
        this.rotation = (this.rotation + 1) % NumberOfRotations;
        this.grid = PentominoDatabase.data[this.id][this.rotation];
    }
}
