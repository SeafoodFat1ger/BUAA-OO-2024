import java.util.ArrayList;

public class MainRequest {
    private boolean isEnd;
    private ArrayList<Person> reqArr;

    public MainRequest() {
        this.isEnd = false;
        this.reqArr = new ArrayList<>();
    }

    public synchronized void backToMainRequest(RequestTable requestTable) {
        //TO/DO *这个要加🔒？？？
        ArrayList<Person> persons = requestTable.getReqArr();
        //persons.addAll(requestTable.getWaitingPersons());//TO/DO buffer
        if (persons.isEmpty()) {
            return;
        }
        for (Person person : persons) {
            reqArr.add(person);
        }
        requestTable.clearAll();
        this.notifyAll();
    }

    public synchronized void addPersonRequest(Person person) {
        reqArr.add(person);
        this.notifyAll();
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
        return person;
    }

    public synchronized boolean isPersonEmpty() {
        return reqArr.isEmpty();
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

}
