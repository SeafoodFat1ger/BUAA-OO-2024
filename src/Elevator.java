import com.oocourse.elevator3.TimableOutput;
import com.oocourse.elevator3.DoubleCarResetRequest;

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

    private Floor floor;
    private int type;//1 is A/2 is B
    private int stopFloor;

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
        this.type = 0;
        this.stopFloor = 0;
        this.floor = null;
    }

    public Elevator(int id, int type, Floor floor,
                    int stopFloor, int eleMaxNum, double moveTime,
                    RequestTable eleRequest) {
        this.eleMaxNum = eleMaxNum;
        this.moveTime = moveTime;
        this.id = id;
        this.elePersonNum = 0;
        if (type == 1) {
            this.eleFloor = stopFloor - 1;
        } else {
            this.eleFloor = stopFloor + 1;
        }
        this.eleDirection = true;
        this.eleMap = new HashMap<>();
        this.elePersonArr = new ArrayList<>();
        this.strategy = new Strategy(eleRequest);
        this.eleRequest = eleRequest;
        this.type = type;
        this.stopFloor = stopFloor;
        this.floor = floor;
    }

    @Override
    public void run() {
        while (true) {
            Advice advice = strategy.getAdvice(eleFloor, elePersonNum,
                    eleDirection, eleMap, eleMaxNum,
                    type, stopFloor, elePersonArr);
            if (advice == Advice.OVER) {
                break;
            } else if (advice == Advice.RESET) {
                reset();
            } else if (advice == Advice.DC) {
                dcReset();
            } else if (advice == Advice.AWAY) {
                moveAway();
            } else if (advice == Advice.MOVE) {
                if (type == 0) {
                    move();
                } else {
                    moveSpecial();
                }
            } else if (advice == Advice.REVERSE) {
                reverse();
            } else if (advice == Advice.WAIT) {
                eleRequest.waitRequest();
            } else if (advice == Advice.OPEN) {
                openAndClose();
            }
        }
    }

    public void dcReset() {
        if (elePersonNum != 0) { //TO/DO*
            placeAll();
        }
        elePersonNum = 0;
        TimableOutput.println(String.format("RESET_BEGIN-%d", id));
        Schedule.getMainRequestTable().backToMainRequest(eleRequest);

        try {
            sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TimableOutput.println(String.format("RESET_END-%d", id));

        DoubleCarResetRequest dcRequest = eleRequest.getDcRequest();
        Schedule.creatGemini(dcRequest);
        eleRequest.setEnd(true);
        eleRequest.setDcRequest(null);
    }

    public void reset() {
        if (elePersonNum != 0) { //TO/DO*
            placeAll();
        }

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
        Output.printOpen(type, id, eleFloor);
        for (Integer i : eleMap.keySet()) {
            ArrayList<Person> outPerson = eleMap.get(i);
            for (Person person : outPerson) {
                elePersonArr.remove(person);
                Output.printOut(type, person.getId(), id, eleFloor);
                if (person.getToFloor() != eleFloor) {
                    person.setFromFloor(eleFloor);
                    eleRequest.addRequest(person);
                }
            }
        }
        eleMap.clear();
        elePersonNum = 0;
        try {
            sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Output.prinClose(type, id, eleFloor);
    }

    public void reverse() {
        eleDirection = !eleDirection;
    }

    public void move() {
        try {
            sleep((long) (1000 * moveTime));//TO/DO*
            Advice advice = strategy.getAdvice(eleFloor, elePersonNum,
                    eleDirection, eleMap, eleMaxNum,
                    type, stopFloor, elePersonArr);
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
            Output.printArrive(type, id, eleFloor);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void moveAway() {
        try {
            sleep((long) (1000 * moveTime));//TO/DO*
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (type == 1) {
            eleFloor--;
        } else if (type == 2) {
            eleFloor++;
        }
        //TimableOutput.println("妈妈我不是自愿的");//TO/DO
        Output.printArrive(type, id, eleFloor);
        eleRequest.setGoAway(false);
        floor.awayTransfer();
        //不用睡了
    }

    public void moveSpecial() {
        if ((type == 1 && eleFloor == stopFloor - 1 && eleDirection) ||
                (type == 2 && eleFloor == stopFloor + 1 && !eleDirection)) {
            while (floor.isOccupy()) {
                //TimableOutput.println("Im oocupied. Im:" + id + " type:" + type);//TO/DO
                floor.occupyOneAway(type);
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            synchronized (floor) {
                floor.setOccupy();
                try {
                    sleep((long) (1000 * moveTime));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                eleFloor = stopFloor;
                Output.printArrive(type, id, eleFloor);

                Advice advice = strategy.getAdvice(eleFloor, elePersonNum,
                        eleDirection, eleMap, eleMaxNum,
                        type, stopFloor, elePersonArr);
                if (advice == Advice.OPEN) {
                    openAndClose();
                }
            }
            return;
        } else if ((type == 1 && eleFloor == stopFloor && !eleDirection) ||
                (type == 2 && eleFloor == stopFloor && eleDirection)) {
            try {
                sleep((long) (1000 * moveTime));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (eleDirection) {
                eleFloor++;
            } else {
                eleFloor--;
            }
            Output.printArrive(type, id, eleFloor);
            //TimableOutput.println("我离开了没有占用");//TO/DO
            floor.awayTransfer();
            return;
        }
        //正常行动
        try {
            sleep((long) (1000 * moveTime));//TO/DO*
            if (eleDirection) {
                eleFloor++;
            } else {
                eleFloor--;
            }
            Output.printArrive(type, id, eleFloor);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void placeChange() {
        ArrayList<Person> changePersons = new ArrayList<>();
        for (Person person : elePersonArr) {
            if (person.isNeedChange()) {
                eleMap.get(person.getToFloor()).remove(person);
                //维护empty
                if (eleMap.get(person.getToFloor()).isEmpty()) {
                    eleMap.remove(person.getToFloor());
                }
                person.setFromFloor(stopFloor);
                person.setNeedChange(false);
                changePersons.add(person);
            }
        }
        for (Person person : changePersons) {
            elePersonArr.remove(person);
            Output.printOut(type, person.getId(), id, eleFloor);
            elePersonNum--;
        }
        for (Person person : changePersons) {
            Schedule.getMainRequestTable().addPersonRequest(person);
            Schedule.decreaceChangeMan();
        }
    }

    public void openAndClose() {
        Output.printOpen(type, id, eleFloor);

        //先下后上好文明
        firstOut();
        if (type != 0 && eleFloor == stopFloor) {
            placeChange();
        }
        //TO/DO 先后没影响
        try {
            sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //reverse？
        Advice advice = strategy.getAdvice(eleFloor, elePersonNum, eleDirection, eleMap, eleMaxNum,
                type, stopFloor, elePersonArr);
        if (advice == Advice.REVERSE) {
            reverse();
        }
        secondIn();

        Output.prinClose(type, id, eleFloor);

    }

    public void firstOut() {
        ArrayList<Person> outPersons = new ArrayList<>();
        if (eleMap.containsKey(eleFloor)) {
            outPersons = eleMap.get(eleFloor);
            for (Person person : outPersons) {
                Output.printOut(type, person.getId(), id, eleFloor);
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
            Output.printIn(type, person.getId(), id, eleFloor);
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

    public int getType() {
        return type;
    }

    public int getStopFloor() {
        return stopFloor;
    }
}
