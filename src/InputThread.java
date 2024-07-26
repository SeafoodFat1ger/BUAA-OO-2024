import com.oocourse.elevator1.ElevatorInput;
import com.oocourse.elevator1.PersonRequest;

import java.io.IOException;

public class InputThread extends Thread {

    private RequestTable mainRequest;

    public InputThread(RequestTable mainRequest) {
        this.mainRequest = mainRequest;
    }

    @Override
    public void run() {
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        while (true) {
            PersonRequest request = elevatorInput.nextPersonRequest();
            if (request == null) {
                mainRequest.setEnd(true);
                //                System.out.println(mainRequest.isEnd());
                //                System.out.println(mainRequest.isEmpty());
                break;
            } else {
                Person person = new Person(request.getPersonId(), request.getFromFloor(),
                        request.getToFloor(), request.getElevatorId());
                mainRequest.addRequest(person);
            }
        }
        try {
            elevatorInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
