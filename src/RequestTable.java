import com.oocourse.elevator2.ResetRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class RequestTable {
    private boolean isEnd;
    //出发地key
    private HashMap<Integer, ArrayList<Person>> reqMap;
    private ArrayList<Person> reqArr;
    private ResetRequest resetRequest;
    private ArrayList<ResetRequest> resetRequests;

    public RequestTable() {
        this.isEnd = false;
        this.reqMap = new HashMap<>();
        this.reqArr = new ArrayList<>();
        this.resetRequest = null;
        this.resetRequests = new ArrayList<>();
    }

    public synchronized boolean isAllFinished() {
        if (reqArr.isEmpty() && resetRequest == null) {
            return true;
        }
        return false;
    }

    public synchronized boolean isResetEmpty() {
        return resetRequests.isEmpty();
    }

    public synchronized void setResetRequest(ResetRequest resetRequest) {
        this.resetRequest = resetRequest;
        notifyAll();
    }

    public synchronized ResetRequest getOneReset() {
        if (resetRequests.isEmpty()) {
            return null;
        }
        ResetRequest request = resetRequests.get(0);
        resetRequests.remove(request);
        return request;
    }

    public synchronized ResetRequest getResetRequest() {
        return resetRequest;
    }

    public synchronized void addResetRequest(ResetRequest reset) {
        resetRequests.add(reset);
        this.notifyAll();
    }

    public synchronized void backToMainRequest(RequestTable requestTable) {
        //TO/DO *这个要加🔒？？？
        ArrayList<Person> persons = requestTable.getReqArr();
        if (persons.isEmpty()) {
            return;
        }
        for (Person person : persons) {
            reqArr.add(person);
            int fromFloor = person.getFromFloor();
            if (reqMap.containsKey(fromFloor)) {
                reqMap.get(fromFloor).add(person);
            } else {
                ArrayList<Person> newPerson = new ArrayList<>();
                newPerson.add(person);
                reqMap.put(fromFloor, newPerson);
            }
        }
        requestTable.clearAll();
        this.notifyAll();
    }

    public synchronized void clearAll() {
        reqMap.clear();
        reqArr.clear();
        //TO/DO * notify?????
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

    public synchronized Person getOneRequest() {
        if (reqArr.isEmpty() && !isEnd && resetRequests.isEmpty()) { //while?
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (reqArr.isEmpty() || !resetRequests.isEmpty()) {
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
