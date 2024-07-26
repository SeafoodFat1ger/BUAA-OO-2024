import com.oocourse.spec3.exceptions.EqualTagIdException;

import java.util.HashMap;

public class MyEqualTagIdException extends EqualTagIdException {
    private int id;
    private static int etiNum = 0;
    private static HashMap<Integer, Integer> etiMap = new HashMap<>();

    public MyEqualTagIdException(int id) {
        this.id = id;
        etiNum++;
        if (etiMap.containsKey(id)) {
            int time = etiMap.get(id) + 1;
            etiMap.put(id, time);
        } else {
            etiMap.put(id, 1);
        }
    }

    public void print() {
        System.out.printf("eti-%d, %d-%d\n", etiNum, id, etiMap.get(id));
    }

}