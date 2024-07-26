public class Floor {
    private boolean isOccupy;
    private RequestTable reqA;
    private RequestTable reqB;

    public Floor(RequestTable reqA, RequestTable reqB) {
        this.reqA = reqA;
        this.reqB = reqB;
        this.isOccupy = false;
    }

    public synchronized boolean isOccupy() {
        return isOccupy;
    }

    public synchronized void occupyOneAway(int comeType) {
        if (comeType == 1) {
            reqB.setGoAway(true);
        } else if (comeType == 2) {
            reqA.setGoAway(true);
        }
    }

    public synchronized void awayTransfer() {
        isOccupy = false;
    }

    public synchronized void setOccupy() {
        isOccupy = true;
    }
}
