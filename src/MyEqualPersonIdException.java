import com.oocourse.spec1.exceptions.EqualPersonIdException;

import java.util.HashMap;

public class MyEqualPersonIdException extends EqualPersonIdException {
    private int id;
    private static int eqiNum = 0;
    private static HashMap<Integer, Integer> eqiMap = new HashMap<>();

    public MyEqualPersonIdException(int id) {
        this.id = id;
        eqiNum++;
        if (eqiMap.containsKey(id)) {
            int time = eqiMap.get(id) + 1;
            eqiMap.put(id, time);
        } else {
            eqiMap.put(id, 1);
        }
    }

    public void print() {
        System.out.printf("epi-%d, %d-%d\n", eqiNum, id, eqiMap.get(id));
    }
}
