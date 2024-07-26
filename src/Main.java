import com.oocourse.elevator3.TimableOutput;

public class Main {
    public static void main(String[] args) {
        TimableOutput.initStartTimestamp();
        Schedule schedule = new Schedule();
        InputThread inputThread = new InputThread(schedule);
        inputThread.start();
    }
}