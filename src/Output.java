import com.oocourse.elevator3.TimableOutput;

public class Output {
    public static void printArrive(int type, int eleId, int eleFloor) {
        if (type == 0) {
            TimableOutput.println(String.format("ARRIVE-%d-%d", eleFloor, eleId));
        } else if (type == 1) {
            TimableOutput.println(String.format("ARRIVE-%d-%d-A", eleFloor, eleId));
        } else {
            TimableOutput.println(String.format("ARRIVE-%d-%d-B", eleFloor, eleId));
        }

    }

    public static void printReceive(int type, int eleId, int pid) {
        if (type == 0) {
            TimableOutput.println(String.format("RECEIVE-%d-%d", pid, eleId));
        } else if (type == 1) {
            TimableOutput.println(String.format("RECEIVE-%d-%d-A", pid, eleId));
        } else {
            TimableOutput.println(String.format("RECEIVE-%d-%d-B", pid, eleId));
        }

    }

    public static void printOpen(int type, int eleId, int eleFloor) {
        if (type == 0) {
            TimableOutput.println(String.format("OPEN-%d-%d", eleFloor, eleId));
        } else if (type == 1) {
            TimableOutput.println(String.format("OPEN-%d-%d-A", eleFloor, eleId));
        } else {
            TimableOutput.println(String.format("OPEN-%d-%d-B", eleFloor, eleId));
        }
    }

    public static void prinClose(int type, int eleId, int eleFloor) {
        if (type == 0) {
            TimableOutput.println(String.format("CLOSE-%d-%d", eleFloor, eleId));
        } else if (type == 1) {
            TimableOutput.println(String.format("CLOSE-%d-%d-A", eleFloor, eleId));
        } else {
            TimableOutput.println(String.format("CLOSE-%d-%d-B", eleFloor, eleId));
        }
    }

    public static void printIn(int type, int pid, int eleId, int eleFloor) {
        if (type == 0) {
            TimableOutput.println(String.format("IN-%d-%d-%d", pid, eleFloor, eleId));
        } else if (type == 1) {
            TimableOutput.println(String.format("IN-%d-%d-%d-A", pid, eleFloor, eleId));
        } else {
            TimableOutput.println(String.format("IN-%d-%d-%d-B", pid, eleFloor, eleId));
        }
    }

    public static void printOut(int type, int pid, int eleId, int eleFloor) {
        if (type == 0) {
            TimableOutput.println(String.format("OUT-%d-%d-%d", pid, eleFloor, eleId));
        } else if (type == 1) {
            TimableOutput.println(String.format("OUT-%d-%d-%d-A", pid, eleFloor, eleId));
        } else {
            TimableOutput.println(String.format("OUT-%d-%d-%d-B", pid, eleFloor, eleId));
        }
    }

}
