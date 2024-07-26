import com.oocourse.elevator2.ElevatorInput;
import com.oocourse.elevator2.PersonRequest;
import com.oocourse.elevator2.Request;
import com.oocourse.elevator2.ResetRequest;

import java.io.IOException;

public class InputThread extends Thread {

    private RequestTable mainRequest;
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
                    mainRequest.addRequest(person);

                } else if (request instanceof ResetRequest) {
                    ResetRequest resetRequest = (ResetRequest) request;
                    //TimableOutput.println("RESET: id:" + resetRequest.getElevatorId());
                    mainRequest.addResetRequest(resetRequest);
                    //schedule.resetElevator(resetRequest);
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
