import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class RequestTable {
    private boolean isEnd;

    //出发地key
    private HashMap<Integer, ArrayList<Person>> reqMap;
    private ArrayList<Person> reqArr;

    public RequestTable() {
        this.isEnd = false;
        this.reqMap = new HashMap<>();
        this.reqArr = new ArrayList<>();
    }

    public synchronized void addRequest(Person person) {
        if (reqMap.containsKey(person.getFromFloor())) {
            reqMap.get(person.getFromFloor()).add(person);
        } else {
            ArrayList<Person> persons = new ArrayList<>();
            persons.add(person);
            reqMap.put(person.getFromFloor(), persons);
        }
        reqArr.add(person);
        this.notifyAll();
    }

    //起始地，方向，最多上几个人
    public synchronized ArrayList<Person> delRequest(int fromFloor,
                                                     boolean eleDirection, int elePersonNum) {

        ArrayList<Person> inPerson = new ArrayList<>();
        if (elePersonNum >= 6) {
            return inPerson;
        }
        //        if (reqMap.isEmpty()) {
        //            return inPerson;
        //        }

        int canNum = 6 - elePersonNum;
        if (reqMap.containsKey(fromFloor)) {
            ArrayList<Person> persons = reqMap.get(fromFloor);

            Iterator<Person> iterator = persons.iterator();
            while (iterator.hasNext()) {
                Person person = iterator.next();
                if (canNum > 0) {
                    if ((person.getToFloor() > fromFloor && eleDirection) ||
                            (person.getToFloor() < fromFloor && !eleDirection)) {
                        inPerson.add(person);

                        reqArr.remove(person);

                        iterator.remove();
                        canNum--;
                    }
                } else {
                    break;
                }
            }
            //维护empty...
            if (persons.isEmpty()) {
                reqMap.remove(fromFloor, persons);
            }
        }
        return inPerson;
    }

    public synchronized void waitRequest() {
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized Person getOneRequest() {
        if (reqArr.isEmpty() && !isEnd) { //while?
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (reqArr.isEmpty()) {
            return null;
        }
        Person person = reqArr.get(0);
        reqArr.remove(person);
        int perFloor = person.getFromFloor();
        reqMap.get(perFloor).remove(person);

        //维护empty
        if (reqMap.get(perFloor).isEmpty()) {
            reqMap.remove(perFloor);
        }

        return person;
    }

    public synchronized boolean isEmpty() {
        return reqMap.isEmpty();
    }

    public synchronized boolean isEnd() {
        return isEnd;
    }

    public synchronized void setEnd(boolean end) {
        isEnd = end;
        this.notifyAll();
    }

    public synchronized HashMap<Integer, ArrayList<Person>> getReqMap() {
        return reqMap;
    }

}
