import com.oocourse.elevator2.TimableOutput;

public class Main {
    public static void main(String[] args) {
        TimableOutput.initStartTimestamp();
        Schedule schedule = new Schedule();
        InputThread inputThread = new InputThread(schedule);
        inputThread.start();
    }
}