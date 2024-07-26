import java.util.ArrayList;

public class Schedule extends Thread {

    private RequestTable mainRequestTable;
    private ArrayList<RequestTable> verticalRequestTables;

    public Schedule(RequestTable mainRequestTable,
                    ArrayList<RequestTable> verticalRequestTables) {
        this.mainRequestTable = mainRequestTable;
        this.verticalRequestTables = verticalRequestTables;

    }

    @Override
    public void run() {
        while (true) {
            if (mainRequestTable.isEmpty() && mainRequestTable.isEnd()) {
                for (RequestTable requestTable : verticalRequestTables) {
                    requestTable.setEnd(true);
                }
                return;
            }
            Person person = mainRequestTable.getOneRequest();
            if (person == null) {
                continue;
            }
            int eleId = person.getBeginElevator();
            verticalRequestTables.get(eleId - 1).addRequest(person);
        }
    }

}
