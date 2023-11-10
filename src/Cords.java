public class Cords implements Comparable<Cords> {
    int x;
    int y;

    Cords(int x, int y) {
        this.x = x;
        this.y = y;
    }


    @Override
    public int compareTo(Cords otherCord) {
        if(this.x != otherCord.x)
            return Integer.compare(x, otherCord.x);
        else
            return Integer.compare(y, otherCord.y);
    }

    public Cords add(int dx, int dy) {
        return new Cords(x + dx, y + dy);
    }
}
