import java.util.ArrayList;
import java.util.TreeSet;

public class Piece {
    int id;
    int rotation;
    int NumberOfRotations;
    TreeSet<Cords> occupiedSpaces = new TreeSet<>();
    Piece(int id, int rotation) {
        this.id = id;
        this.rotation = rotation;
        this.NumberOfRotations = PentominoDatabase.data[id].length;
    }

    void rotate() {
        this.rotation = (this.rotation + 1) % NumberOfRotations;
    }
}
