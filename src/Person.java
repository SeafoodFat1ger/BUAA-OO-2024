public class Person {
    private int id;
    private int fromFloor;
    private int toFloor;
    private int beginElevator;

    public Person(int id, int fromFloor, int toFloor, int beginElevator) {
        this.id = id;
        this.fromFloor = fromFloor;
        this.toFloor = toFloor;
        this.beginElevator = beginElevator;
    }

    ///////////////////////////////
    public int getFromFloor() {
        return fromFloor;
    }

    public int getToFloor() {
        return toFloor;
    }

    public int getId() {
        return id;
    }

    public int getBeginElevator() {
        return beginElevator;
    }
}
