public class Person {
    private int id;
    private int fromFloor;
    private int toFloor;
    private boolean needChange;

    public Person(int id, int fromFloor, int toFloor) {
        this.id = id;
        this.fromFloor = fromFloor;
        this.toFloor = toFloor;
        this.needChange = false;
    }

    ///////////////////////////////
    public synchronized int getFromFloor() {
        return fromFloor;
    }

    public synchronized void setFromFloor(int fromFloor) {
        this.fromFloor = fromFloor;
    }

    public synchronized int getToFloor() {
        return toFloor;
    }

    public synchronized int getId() {
        return id;
    }

    public synchronized boolean isNeedChange() {
        return needChange;
    }

    public synchronized void setNeedChange(boolean needChange) {
        this.needChange = needChange;
    }
}
