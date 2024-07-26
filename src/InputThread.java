import com.oocourse.elevator3.DoubleCarResetRequest;
import com.oocourse.elevator3.ElevatorInput;
import com.oocourse.elevator3.NormalResetRequest;
import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.Request;

import java.io.IOException;

public class InputThread extends Thread {

    private MainRequest mainRequest;
    private Schedule schedule;

    public InputThread(Schedule schedule) {
        this.mainRequest = Schedule.getMainRequestTable();
        this.schedule = schedule;
        this.schedule.start();
    }

    @Override
    public void run() {
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        while (true) {
            Request request = elevatorInput.nextRequest();
            if (request == null) {
                mainRequest.setEnd(true);
                break;
            } else {
                if (request instanceof PersonRequest) {
                    PersonRequest personRequest = (PersonRequest) request;
                    Person person = new Person(personRequest.getPersonId(),
                            personRequest.getFromFloor(), personRequest.getToFloor());
                    mainRequest.addPersonRequest(person);

                } else if (request instanceof NormalResetRequest) {
                    NormalResetRequest resetRequest = (NormalResetRequest) request;
                    //TimableOutput.println("RESET: id:" + resetRequest.getElevatorId());
                    //mainRequest.addResetRequest(resetRequest);
                    schedule.resetElevator(resetRequest);
                } else if (request instanceof DoubleCarResetRequest) {
                    DoubleCarResetRequest dcRequest = (DoubleCarResetRequest) request;
                    //mainRequest.addDcRequest(dcRequest);
                    schedule.dcElevator(dcRequest);
                }

            }
        }
        try {
            elevatorInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
