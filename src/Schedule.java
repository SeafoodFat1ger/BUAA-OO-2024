import com.oocourse.elevator3.DoubleCarResetRequest;
import com.oocourse.elevator3.NormalResetRequest;

import java.util.ArrayList;
import java.util.Random;

public class Schedule extends Thread {

    private static MainRequest mainRequestTable;
    private final ArrayList<RequestTable> eleRequestTables;
    private ArrayList<Elevator> elevators;
    private static ArrayList<Gemini> geminis;
    private static ArrayList<RequestTable> geminiAReqs;
    private static ArrayList<RequestTable> geminiBReqs;

    private static int changeMan;

    public Schedule() {
        mainRequestTable = new MainRequest();
        this.eleRequestTables = new ArrayList<>();
        this.elevators = new ArrayList<>();
        geminis = new ArrayList<>();
        geminiAReqs = new ArrayList<>();
        geminiBReqs = new ArrayList<>();
        changeMan = 0;
        for (int i = 0; i < 6; i++) {
            RequestTable eleRequest = new RequestTable();
            eleRequestTables.add(eleRequest);
            Elevator elevator = new Elevator(i + 1, eleRequest);
            elevators.add(elevator);
            elevator.start();
        }
    }

    public boolean isFinished() {
        synchronized (eleRequestTables) {
            for (RequestTable requestTable : eleRequestTables) {
                if (!requestTable.isAllFinished()) {
                    return false;
                }
            }
        }
        synchronized (geminiAReqs) {
            for (RequestTable requestTable : geminiAReqs) {
                if (!requestTable.isAllFinished()) {
                    return false;
                }
            }
        }
        synchronized (geminiBReqs) {
            for (RequestTable requestTable : geminiBReqs) {
                if (!requestTable.isAllFinished()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void run() {
        while (true) {
            if (mainRequestTable.isPersonEmpty() && mainRequestTable.isEnd()) {
                if (changeMan == 0 && isFinished()) {
                    for (RequestTable requestTable : eleRequestTables) {
                        requestTable.setEnd(true);
                    }
                    for (RequestTable requestTable : geminiAReqs) {
                        requestTable.setEnd(true);
                    }
                    for (RequestTable requestTable : geminiBReqs) {
                        requestTable.setEnd(true);
                    }
                    //TimableOutput.println("哥们罢工了");
                    return;
                } else {
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            Person person = mainRequestTable.getOneRequest();
            //TimableOutput.println("GET ONE:null"+changeMan);
            if (person == null) {
                continue;
            }
            while (true) {
                int flag = randomDispatch(person);
                if (flag == -1) {
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }
        }
    }

    public synchronized void resetElevator(NormalResetRequest resetRequest) {
        //System.out.println("get reset ele ***********"+id);
        int id = resetRequest.getElevatorId();
        synchronized (eleRequestTables) {
            RequestTable eleRequest = eleRequestTables.get(id - 1);
            eleRequest.setResetRequest(resetRequest);
        }
    }

    public synchronized void dcElevator(DoubleCarResetRequest dcRequest) {
        //System.out.println("get reset ele ***********"+id);
        int id = dcRequest.getElevatorId();
        synchronized (eleRequestTables) {
            RequestTable eleRequest = eleRequestTables.get(id - 1);
            eleRequest.setDcRequest(dcRequest);
        }
    }

    public static synchronized void creatGemini(DoubleCarResetRequest dcRequest) {
        int id = dcRequest.getElevatorId();
        int stopFloor = dcRequest.getTransferFloor();
        int eleMaxNum = dcRequest.getCapacity();
        double moveTime = dcRequest.getSpeed();
        RequestTable reqA = new RequestTable();
        geminiAReqs.add(reqA);
        RequestTable reqB = new RequestTable();
        geminiBReqs.add(reqB);
        Gemini gemini = new Gemini(id, stopFloor, eleMaxNum, reqA, reqB, moveTime);
        geminis.add(gemini);
    }

    public synchronized int randomDispatch(Person person) {
        Random random = new Random();
        int id = random.nextInt(6) + 1;
        synchronized (eleRequestTables) {
            synchronized (geminiAReqs) {
                synchronized (geminiBReqs) {
                    if (eleRequestTables.get(id - 1).isEnd()) {
                        int index = 0;
                        for (int i = 0; i < geminis.size(); i++) {
                            if (geminis.get(i).getId() == id) {
                                index = i;
                                break;
                            }
                        }
                        int stopFloor = geminis.get(index).getStopFloor();
                        boolean isA = isAElevator(person, stopFloor);
                        boolean isChange = isChange(person, stopFloor);
                        if (isChange) {
                            changeMan++;
                            person.setNeedChange(true);
                        }
                        if (isA) {
                            Output.printReceive(1,geminis.get(index).getId(),person.getId());
                            geminiAReqs.get(index).addRequest(person);
                        } else {
                            Output.printReceive(2,geminis.get(index).getId(),person.getId());
                            geminiBReqs.get(index).addRequest(person);
                        }
                    } else {
                        if (eleRequestTables.get(id - 1).getResetRequest() != null
                                || eleRequestTables.get(id - 1).getDcRequest() != null) {
                            return -1;
                        } else {
                            Output.printReceive(0,id,person.getId());
                            eleRequestTables.get(id - 1).addRequest(person);
                        }
                    }

                }
            }
        }
        return 0;
    }

    //sb中的sb 改了一天还有问题
    public synchronized double calc(Person person, Gemini gemini, boolean isA, boolean isChange) {
        Shadow shuang;
        double time = 0;
        Person personNew = new Person(person.getId(), gemini.getStopFloor(), person.getToFloor());
        Person personChange = new Person(person.getId(),
                person.getFromFloor(), person.getToFloor());
        personChange.setNeedChange(true);
        Shadow shuangA = new Shadow(gemini.getElevatorA());
        Shadow shuangB = new Shadow(gemini.getElevatorB());
        if (isA) {
            shuang = shuangA;
        } else {
            shuang = shuangB;
        }
        if (isChange) {
            if (isA) {
                shuangB.addPerson(personNew);
            } else {
                shuangA.addPerson(personNew);
            }
            shuang.addPerson(personChange);
        } else {
            shuang.addPerson(person);
        }
        shuangA.run();
        shuangB.run();
        if (shuangA.isTle() || shuangB.isTle()) {
            return -1;
        }
        if (isChange) {
            time = shuangB.getTime() + shuangB.getTime();
        } else {
            time = shuang.getTime();
        }
        return time;
    }

    public synchronized boolean isAElevator(Person person, int stopFloor) {
        if (stopFloor > person.getFromFloor()) {
            return true;
        } else if (stopFloor < person.getFromFloor()) {
            return false;
        } else {
            if (person.getToFloor() < person.getFromFloor()) {
                return true;
            } else {
                return false;
            }
        }
    }

    public synchronized boolean isChange(Person person, int stopFloor) {
        return (person.getToFloor() > stopFloor
                && person.getFromFloor() < stopFloor) ||
                (person.getFromFloor() > stopFloor
                        && person.getToFloor() < stopFloor);
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
        if (canAcEleNum == 0) {
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

    public static synchronized void decreaceChangeMan() {
        //TimableOutput.println("CHANGE --");
        changeMan--;
    }

    public static MainRequest getMainRequestTable() {
        return mainRequestTable;
    }
}
