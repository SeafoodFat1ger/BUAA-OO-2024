import java.util.ArrayList;
import java.util.HashMap;

public class Shadow {
    private double time;
    private boolean tle;
    private int id;
    private int elePersonNum;
    private int eleMaxNum;
    private double moveTime;
    private int eleFloor;
    private boolean eleDirection;
    private RequestTable eleRequest;
    private HashMap<Integer, ArrayList<Person>> eleMap;

    private int type;
    private int stopFloor;
    private boolean isReset;
    private ArrayList<Person> elePersonArr;

    public Shadow(Elevator elevator) {
        synchronized (elevator.getEleRequest()) {
            this.tle = false;
            this.stopFloor = elevator.getStopFloor();
            this.type = elevator.getType();
            this.id = elevator.getEleId();
            this.elePersonNum = elevator.getElePersonNum();
            this.eleMaxNum = elevator.getEleMaxNum();
            this.moveTime = elevator.getMoveTime();
            this.eleFloor = elevator.getEleFloor();
            this.eleDirection = elevator.getEleDirection();
            this.time = 0;
            ArrayList<Person> reqArr = new ArrayList<>();
            reqArr.addAll(elevator.getEleRequest().getReqArr());
            //reqArr.addAll(elevator.getEleRequest().getWaitingPersons());
            this.eleRequest = new RequestTable();
            for (Person person : reqArr) {
                eleRequest.addRequestClone(person);
            }
            //你一被我深克隆的东西怎么还会被外界改？？反思
            this.eleMap = new HashMap<>();
            ArrayList<Person> elePerson = new ArrayList<>();
            elePerson.addAll(elevator.getElePersonArr());
            this.elePersonArr = elePerson;
            for (Person person : elePerson) {
                int toFloor = person.getToFloor();
                if (eleMap.containsKey(toFloor)) {
                    eleMap.get(toFloor).add(person);
                } else {
                    ArrayList<Person> persons = new ArrayList<>();
                    persons.add(person);
                    eleMap.put(person.getToFloor(), persons);
                }
            }
            //锁了你还有空指针是吧 nb
            if (elevator.getEleRequest().getDcRequest() != null) {
                this.isReset = true;
                this.eleMaxNum = elevator.getEleRequest().getDcRequest().getCapacity();
                this.moveTime = elevator.getEleRequest().getDcRequest().getSpeed();
            } else if (elevator.getEleRequest().getResetRequest() != null) {
                this.isReset = true;
                this.eleMaxNum = elevator.getEleRequest().getResetRequest().getCapacity();
                this.moveTime = elevator.getEleRequest().getResetRequest().getSpeed();
            } else {
                this.isReset = false;
            }
        }
    }

    public double getTime() {
        return time;
    }

    public boolean isTle() {
        return tle;
    }

    public void run() {
        int flag = 0;
        while (true) {
            flag++;
            if (flag > 200) {
                tle = true;
                break;
            }
            if (isReset) {
                time += 1.2;
                isReset = false;
                continue;
            }
            if (openForOut(eleFloor, eleMap)
                    || openForIn(eleFloor, elePersonNum, eleDirection, eleMaxNum)
                    || openForChange(type, stopFloor, elePersonArr, eleFloor)) {
                openAndClose();
                continue;
            }
            //电梯是否有人->继续原方向
            if (elePersonNum != 0) {
                if (type == 0) {
                    move();
                } else {
                    moveSpecial();
                }
                continue;
            }

            //请求队列是否空
            if (eleRequest.isEmpty()) {
                break;
            } else {
                if (reqSameDirection(eleFloor, eleDirection)) {
                    if (type == 0) {
                        move();
                    } else {
                        moveSpecial();
                    }
                    continue;
                } else {
                    reverse();
                    continue;
                }
            }
        }

    }

    public void reverse() {
        eleDirection = !eleDirection;
    }

    public void moveSpecial() {
        if ((type == 1 && eleFloor == stopFloor - 1 && eleDirection) ||
                (type == 2 && eleFloor == stopFloor + 1 && !eleDirection)) {
            time += moveTime;
            time += 0.4;
            eleFloor = stopFloor;
            time += moveTime;
            return;
        }
        //正常行动
        time += moveTime;
        if (eleDirection) {
            eleFloor++;
        } else {
            eleFloor--;
        }
    }

    public void move() {
        time += moveTime;
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
                changePersons.add(person);
            }
        }
        for (Person person : changePersons) {
            elePersonArr.remove(person);
            elePersonNum--;
        }
    }

    public void openAndClose() {

        //TimableOutput.println(id+": floor "+eleFloor+" direction: "+eleDirection);

        //先下后上好文明

        firstOut();
        if (type != 0 && eleFloor == stopFloor) {
            placeChange();
        }
        time += 0.4;

        //reverse？
        if (elePersonNum == 0 && !eleRequest.isEmpty() &&
                !openForIn(eleFloor, elePersonNum, eleDirection, eleMaxNum) &&
                !reqSameDirection(eleFloor, eleDirection)) {
            reverse();
        }

        //TimableOutput.println(id+": floor "+eleFloor+" direction: "+eleDirection);

        secondIn();

    }

    public void firstOut() {
        ArrayList<Person> outPersons = new ArrayList<>();

        if (eleMap.containsKey(eleFloor)) {
            outPersons = eleMap.get(eleFloor);
            int size = outPersons.size();
            for (Person person : outPersons) {
                elePersonArr.remove(person);
            }
            elePersonNum -= size;
        }
        eleMap.remove(eleFloor, outPersons);
    }

    public void secondIn() {
        ArrayList<Person> inPerson = eleRequest.delRequest(eleFloor,
                eleDirection, elePersonNum, eleMaxNum);
        //TimableOutput.println(inPerson.isEmpty());

        for (Person person : inPerson) {
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

    public boolean openForIn(int eleFloor, int elePersonNum, boolean eleDirection, int eleMaxNum) {
        //如果满人了  先下后上 不下绝对不可能上....
        if (elePersonNum >= eleMaxNum) {
            return false;
        }
        if (!eleRequest.isEmpty()) {
            if (eleRequest.getReqMap().containsKey(eleFloor)) {
                ArrayList<Person> persons = eleRequest.getReqMap().get(eleFloor);
                for (Person person : persons) {
                    if ((person.getToFloor() > eleFloor && eleDirection) ||
                            (person.getToFloor() < eleFloor && !eleDirection)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean openForOut(int eleFloor, HashMap<Integer, ArrayList<Person>> eleMap) {

        if (eleMap.containsKey(eleFloor)) {
            ArrayList<Person> persons = eleMap.get(eleFloor);
            if (!persons.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public boolean reqSameDirection(int eleFloor, boolean eleDirection) {
        //请求队列非空已判断
        for (Integer i : eleRequest.getReqMap().keySet()) {
            if ((i > eleFloor && eleDirection) ||
                    (i < eleFloor && !eleDirection)) {
                return true;
            }
        }

        return false;
    }

    public boolean openForChange(int type, int stopFloor,
                                 ArrayList<Person> elePersonArr, int eleFloor) {
        if (type == 0 || eleFloor != stopFloor) {
            return false;
        }
        for (Person person : elePersonArr) {
            if (person.isNeedChange()) {
                return true;
            }
        }
        return false;
    }

    public void addPerson(Person person) {
        eleRequest.addRequestClone(person);
    }
}
