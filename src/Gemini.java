public class Gemini {
    private Elevator elevatorA;
    private Elevator elevatorB;
    private int id;
    private int stopFloor;
    private RequestTable requestA;
    private RequestTable requestB;
    private Floor floor;

    public Gemini(int id, int stopFloor, int eleMaxNum,
                  RequestTable requestA, RequestTable requestB, double moveTime) {
        this.id = id;
        this.requestA = requestA;
        this.requestB = requestB;
        this.stopFloor = stopFloor;
        this.floor = new Floor(requestA, requestB);
        this.elevatorA = new Elevator(id, 1, floor, stopFloor, eleMaxNum, moveTime, requestA);
        this.elevatorB = new Elevator(id, 2, floor, stopFloor, eleMaxNum, moveTime, requestB);
        this.elevatorA.start();
        this.elevatorB.start();
    }

    public int getId() {
        return id;
    }

    public int getStopFloor() {
        return stopFloor;
    }

    public Elevator getElevatorA() {
        return elevatorA;
    }

    public Elevator getElevatorB() {
        return elevatorB;
    }
}
