import com.oocourse.elevator2.TimableOutput;

import com.oocourse.elevator2.ResetRequest;

import java.util.ArrayList;

public class Schedule extends Thread {

    private static RequestTable mainRequestTable;
    private final ArrayList<RequestTable> eleRequestTables;
    private ArrayList<Elevator> elevators;

    public Schedule() {
        this.mainRequestTable = new RequestTable();
        this.eleRequestTables = new ArrayList<>();
        this.elevators = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            RequestTable eleRequest = new RequestTable();
            eleRequestTables.add(eleRequest);
            Elevator elevator = new Elevator(i + 1, eleRequest);
            elevators.add(elevator);
            elevator.start();
        }
    }

    public boolean isFinished() {
        for (RequestTable requestTable : eleRequestTables) {
            if (!requestTable.isAllFinished()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void run() {
        while (true) {
            if (mainRequestTable.isEmpty() && mainRequestTable.isResetEmpty()
                    && mainRequestTable.isEnd()) {
                if (isFinished()) {
                    for (RequestTable requestTable : eleRequestTables) {
                        requestTable.setEnd(true);
                    }
                    return;
                } else {
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            ResetRequest resetRequest = mainRequestTable.getOneReset();
            if (resetRequest != null) {
                resetElevator(resetRequest);
                //TimableOutput.println("调度器 RESET");
            }

            Person person = mainRequestTable.getOneRequest();
            if (person != null) {
                while (true) {
                    int eleId = dispatch(person);//TO/DO* 分配策略
                    if (eleId != -1) {
                        TimableOutput.println(String.format("RECEIVE-%d-%d",
                                person.getId(), eleId));
                        eleRequestTables.get(eleId - 1).addRequest(person);
                        break;
                    }
                    try {
                        sleep(1200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }


            //TimableOutput.println("Schedule personId:"+person.getId());
            //TimableOutput.println(mainRequestTable.isEmpty());
            //TimableOutput.println(eleRequestTables.get(1).isEmpty());
        }
    }

    public synchronized void resetElevator(ResetRequest resetRequest) {
        //System.out.println("get reset ele ***********"+id);
        int id = resetRequest.getElevatorId();
        synchronized (eleRequestTables) {
            RequestTable eleRequest = eleRequestTables.get(id - 1);
            eleRequest.setResetRequest(resetRequest);
        }
    }

    public int fewest() {
        int min = 114514;
        int id = 1;
        for (int i = 0; i < 6; i++) {
            int tmp = elevators.get(i).getElePersonNum();
            if (tmp < min) {
                id = i + 1;
                min = tmp;
            }
        }
        return id;
    }

    public synchronized int dispatch(Person person) {
        int eleId = -1;
        ArrayList<Integer> canIndex = new ArrayList<>();
        int canAcEleNum = 0;
        synchronized (eleRequestTables) {
            for (int i = 0; i < 6; i++) {
                if (eleRequestTables.get(i).getResetRequest() == null) {
                    canIndex.add(i);
                    canAcEleNum++;
                }
            }
        }
        if (canAcEleNum <= 3) {
            return eleId;
        }
        double min = 114514;
        ArrayList<Double> eleBeforeTime = new ArrayList<>(canAcEleNum);
        for (int i = 0; i < canAcEleNum; i++) {
            Shadow before = new Shadow(elevators.get(canIndex.get(i)));
            before.run();
            //TimableOutput.println("Dispatch before elevater over (ID-1): " + canIndex.get(i));
            eleBeforeTime.add(before.getTime());
        }

        //TimableOutput.println("Begin Shadow add Person");
        for (int i = 0; i < canAcEleNum; i++) {
            Shadow after = new Shadow(elevators.get(canIndex.get(i)));
            after.addPerson(person);
            after.run();
            //TimableOutput.println("Dispatch after elevater over (ID-1): " + canIndex.get(i));

            double max = 0;
            for (int j = 0; j < canAcEleNum; j++) {
                if (i == j) {
                    if (after.getTime() > max) {
                        max = after.getTime();
                    }
                } else {
                    if (eleBeforeTime.get(j) > max) {
                        max = eleBeforeTime.get(j);
                    }
                }
            }

            if (max < min) {
                min = max;
                eleId = canIndex.get(i) + 1;
            } else if (max == min) {
                int beforePersonNum = elevators.get(eleId - 1).getElePersonNum();
                int afterPersonNum = elevators.get(canIndex.get(i)).getElePersonNum();
                if (afterPersonNum < beforePersonNum) {
                    eleId = canIndex.get(i) + 1;
                }
            }

        }
        //TimableOutput.println("Dispatch personId: "+person.getId()+" elevator: "+eleId);
        return eleId;
    }

    public static RequestTable getMainRequestTable() {
        return mainRequestTable;
    }
}
