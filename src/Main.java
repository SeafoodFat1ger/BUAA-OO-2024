import com.oocourse.elevator1.TimableOutput;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        TimableOutput.initStartTimestamp();
        RequestTable mainRequest = new RequestTable();
        ArrayList<RequestTable> verticalRequestTables = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            RequestTable eleRequest = new RequestTable();
            verticalRequestTables.add(eleRequest);
            Thread elevator = new Elevator(i + 1, eleRequest);
            elevator.start();
        }

        Thread inputThread = new InputThread(mainRequest);
        inputThread.start();

        Thread schedule = new Schedule(mainRequest, verticalRequestTables);
        schedule.start();
    }
}