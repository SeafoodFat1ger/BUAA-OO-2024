import com.oocourse.elevator3.DoubleCarResetRequest;
import com.oocourse.elevator3.NormalResetRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class RequestTable {
    private boolean isEnd;
    //出发地key
    private HashMap<Integer, ArrayList<Person>> reqMap;
    private ArrayList<Person> reqArr;
    private NormalResetRequest resetRequest;
    private DoubleCarResetRequest dcRequest;

    private boolean goAway;
    private ArrayList<Person> waitingPersons;

    public RequestTable() {
        this.isEnd = false;
        this.reqMap = new HashMap<>();
        this.reqArr = new ArrayList<>();
        this.resetRequest = null;
        this.goAway = false;
        this.waitingPersons = new ArrayList<>();
    }

    public synchronized void setGoAway(boolean goAway) {
        this.goAway = goAway;
        notifyAll();
    }

    public synchronized boolean isGoAway() {
        return goAway;
    }

    public synchronized boolean isAllFinished() {
        if (reqArr.isEmpty() && resetRequest == null && dcRequest == null) {
            return true;
        }
        return false;
    }

    public synchronized void setResetRequest(NormalResetRequest resetRequest) {
        this.resetRequest = resetRequest;
        notifyAll();
    }

    public synchronized NormalResetRequest getResetRequest() {
        return resetRequest;
    }

    public synchronized void setDcRequest(DoubleCarResetRequest dcRequest) {
        this.dcRequest = dcRequest;
        notifyAll();
    }

    public synchronized DoubleCarResetRequest getDcRequest() {
        return dcRequest;
    }

    public synchronized void clearAll() {
        reqMap.clear();
        reqArr.clear();
        //waitingPersons.clear();
        //TO/DO * notify????? && waitingList
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

    public synchronized void addRequestClone(Person person) {
        if (reqMap.containsKey(person.getFromFloor())) {
            reqMap.get(person.getFromFloor()).add(person);
        } else {
            ArrayList<Person> persons = new ArrayList<>();
            persons.add(person);
            reqMap.put(person.getFromFloor(), persons);
        }
        reqArr.add(person);
    }

    //起始地，方向，最多上几个人
    public synchronized ArrayList<Person> delRequest(int fromFloor, boolean eleDirection,
                                                     int elePersonNum, int eleMaxNum) {

        ArrayList<Person> inPerson = new ArrayList<>();
        if (elePersonNum >= eleMaxNum) {
            return inPerson;
        }
        //TimableOutput.println("**********");
        //TimableOutput.println("Direction:" + eleDirection);

        int canNum = eleMaxNum - elePersonNum;
        if (reqMap.containsKey(fromFloor)) {
            ArrayList<Person> persons = reqMap.get(fromFloor);

            Iterator<Person> iterator = persons.iterator();
            while (iterator.hasNext()) {
                Person person = iterator.next();
                if (canNum > 0) {
                    if ((person.getToFloor() > fromFloor && eleDirection)
                            || (person.getToFloor() < fromFloor && !eleDirection)) {
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

    public synchronized ArrayList<Person> getReqArr() {
        return reqArr;
    }

    public synchronized HashMap<Integer, ArrayList<Person>> getReqMap() {
        return reqMap;
    }

}
