import com.oocourse.spec3.exceptions.AcquaintanceNotFoundException;

import java.util.HashMap;

public class MyAcquaintanceNotFoundException extends AcquaintanceNotFoundException {

    private int id;
    private static int anfNum = 0;
    private static HashMap<Integer, Integer> anfMap = new HashMap<>();

    public MyAcquaintanceNotFoundException(int id) {
        this.id = id;
        anfNum++;
        if (anfMap.containsKey(id)) {
            int time = anfMap.get(id) + 1;
            anfMap.put(id, time);
        } else {
            anfMap.put(id, 1);
        }
    }

    public void print() {
        System.out.printf("anf-%d, %d-%d\n", anfNum, id, anfMap.get(id));
    }
}