import java.util.ArrayList;
import java.util.HashMap;

public class Shadow {
    private double time;
    private int id;
    private int elePersonNum;
    private int eleMaxNum;
    private double moveTime;
    private int eleFloor;
    private boolean eleDirection;
    private RequestTable eleRequest;
    private HashMap<Integer, ArrayList<Person>> eleMap;

    public Shadow(Elevator elevator) {
        this.id = elevator.getEleId();
        this.elePersonNum = elevator.getElePersonNum();
        //TimableOutput.println("elePersonNum: "+elePersonNum);
        this.eleMaxNum = elevator.getEleMaxNum();
        this.moveTime = elevator.getMoveTime();
        this.eleFloor = elevator.getEleFloor();
        this.eleDirection = elevator.getEleDirection();
        this.time = 0;
        ArrayList<Person> reqArr = new ArrayList<>();
        reqArr.addAll(elevator.getEleRequest().getReqArr());
        this.eleRequest = new RequestTable();
        for (Person person : reqArr) {
            eleRequest.addRequestClone(person);
        }
        this.eleMap = new HashMap<>();
        ArrayList<Person> elePerson = new ArrayList<>();
        elePerson.addAll(elevator.getElePersonArr());
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

    }

    public double getTime() {
        return time;
    }

    public void run() {
        while (true) {

            if (openForOut(eleFloor, eleMap)
                    || openForIn(eleFloor, elePersonNum, eleDirection, eleMaxNum)) {
                openAndClose();
                continue;
            }
            //电梯是否有人->继续原方向
            if (elePersonNum != 0) {
                move();
                continue;
            }

            //请求队列是否空
            if (eleRequest.isEmpty()) {
                break;
            } else {
                if (reqSameDirection(eleFloor, eleDirection)) {
                    move();
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

    public void openAndClose() {

        //TimableOutput.println(id+": floor "+eleFloor+" direction: "+eleDirection);

        //先下后上好文明

        firstOut();

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

    public void addPerson(Person person) {
        eleRequest.addRequestClone(person);
    }
}
