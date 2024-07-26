import com.oocourse.spec3.exceptions.EqualMessageIdException;

import java.util.HashMap;

public class MyEqualMessageIdException extends EqualMessageIdException {
    private int id;
    private static int emiNum = 0;
    private static HashMap<Integer, Integer> emiMap = new HashMap<>();

    public MyEqualMessageIdException(int id) {
        this.id = id;
        emiNum++;
        if (emiMap.containsKey(id)) {
            int time = emiMap.get(id) + 1;
            emiMap.put(id, time);
        } else {
            emiMap.put(id, 1);
        }
    }

    public void print() {
        System.out.printf("emi-%d, %d-%d\n", emiNum, id, emiMap.get(id));
    }

}