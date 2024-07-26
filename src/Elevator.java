import com.oocourse.elevator2.TimableOutput;

import java.util.ArrayList;
import java.util.HashMap;

public class Elevator extends Thread {
    private int id;
    private int elePersonNum;
    private int eleMaxNum;
    private double moveTime;
    private int eleFloor;
    private boolean eleDirection;
    private RequestTable eleRequest;
    private Strategy strategy;

    //目的地key
    private HashMap<Integer, ArrayList<Person>> eleMap;
    private ArrayList<Person> elePersonArr;

    public Elevator(int id, RequestTable eleRequest) {
        this.eleMaxNum = 6;
        this.moveTime = 0.4;
        this.id = id;
        this.elePersonNum = 0;
        this.eleFloor = 1;
        this.eleDirection = true;
        this.eleMap = new HashMap<>();
        this.elePersonArr = new ArrayList<>();
        this.strategy = new Strategy(eleRequest);
        this.eleRequest = eleRequest;
    }

    @Override
    public void run() {
        while (true) {
            Advice advice = strategy.getAdvice(eleFloor, elePersonNum,
                    eleDirection, eleMap, eleMaxNum);
            if (advice == Advice.OVER) {
                break;
            } else if (advice == Advice.RESET) {
                reset();
            } else if (advice == Advice.MOVE) {
                move();
            } else if (advice == Advice.REVERSE) {
                reverse();
            } else if (advice == Advice.WAIT) {
                eleRequest.waitRequest();
            } else if (advice == Advice.OPEN) {
                openAndClose();
            }
        }
    }

    public void reset() {
        if (elePersonNum != 0) { //TO/DO*
            placeAll();
        }
        //TO/DO* close之后再改变电梯内人数
        elePersonNum = 0;
        TimableOutput.println(String.format("RESET_BEGIN-%d", id));
        Schedule.getMainRequestTable().backToMainRequest(eleRequest);


        eleMaxNum = eleRequest.getResetRequest().getCapacity();
        moveTime = eleRequest.getResetRequest().getSpeed();

        try {
            sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TimableOutput.println(String.format("RESET_END-%d", id));
        eleRequest.setResetRequest(null);

    }

    public void placeAll() {
        TimableOutput.println(String.format("OPEN-%d-%d", eleFloor, id));

        for (Integer i : eleMap.keySet()) {
            ArrayList<Person> outPerson = eleMap.get(i);
            for (Person person : outPerson) {
                elePersonArr.remove(person);
                TimableOutput.println(String.format("OUT-%d-%d-%d", person.getId(), eleFloor, id));
                if (person.getToFloor() != eleFloor) {
                    person.setFromFloor(eleFloor);
                    eleRequest.addRequest(person);
                }
            }
        }
        eleMap.clear();
        try {
            sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TimableOutput.println(String.format("CLOSE-%d-%d", eleFloor, id));
    }

    public void reverse() {
        eleDirection = !eleDirection;
    }

    public void move() {
        try {
            sleep((long) (1000 * moveTime));//TO/DO*
            Advice advice = strategy.getAdvice(eleFloor, elePersonNum,
                    eleDirection, eleMap, eleMaxNum);
            if (advice != Advice.MOVE) { //TO/DO* move or reset
                return;
            }
            if (eleDirection) {
                if (eleFloor < 11) {
                    eleFloor++;
                } else {
                    eleFloor--;
                    eleDirection = false;
                }
            } else {
                if (eleFloor > 1) {
                    eleFloor--;
                } else {
                    eleFloor++;
                    eleDirection = true;
                }
            }
            TimableOutput.println(String.format("ARRIVE-%d-%d", eleFloor, id));

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void openAndClose() {
        TimableOutput.println(String.format("OPEN-%d-%d", eleFloor, id));

        //先下后上好文明

        firstOut();

        try {
            sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //reverse？
        Advice advice = strategy.getAdvice(eleFloor, elePersonNum, eleDirection, eleMap, eleMaxNum);
        if (advice == Advice.REVERSE) {
            reverse();
        }
        secondIn();

        TimableOutput.println(String.format("CLOSE-%d-%d", eleFloor, id));

    }

    public void firstOut() {
        ArrayList<Person> outPersons = new ArrayList<>();
        if (eleMap.containsKey(eleFloor)) {
            outPersons = eleMap.get(eleFloor);
            for (Person person : outPersons) {
                TimableOutput.println(String.format("OUT-%d-%d-%d", person.getId(), eleFloor, id));
                elePersonArr.remove(person);
                elePersonNum--;
            }
        }
        eleMap.remove(eleFloor, outPersons);

    }

    public void secondIn() {
        ArrayList<Person> inPerson = eleRequest.delRequest(eleFloor,
                eleDirection, elePersonNum, eleMaxNum);
        for (Person person : inPerson) {
            TimableOutput.println(String.format("IN-%d-%d-%d", person.getId(), eleFloor, id));
            int toFloor = person.getToFloor();

            elePersonArr.add(person);
            if (eleMap.containsKey(toFloor)) {
                eleMap.get(toFloor).add(person);
            } else {
                ArrayList<Person> persons = new ArrayList<>();
                persons.add(person);
                eleMap.put(person.getToFloor(), persons);
            }

            elePersonNum++;
        }
    }
    ////////////////////

    public int getEleId() {
        return id;
    }

    public int getElePersonNum() {
        return elePersonNum;
    }

    public int getEleFloor() {
        return eleFloor;
    }

    public int getEleMaxNum() {
        return eleMaxNum;
    }

    public double getMoveTime() {
        return moveTime;
    }

    public RequestTable getEleRequest() {
        return eleRequest;
    }

    public ArrayList<Person> getElePersonArr() {
        return elePersonArr;
    }

    public boolean getEleDirection() {
        return eleDirection;
    }
}
