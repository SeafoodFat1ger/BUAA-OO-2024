import com.oocourse.spec3.exceptions.EqualEmojiIdException;

import java.util.HashMap;

public class MyEqualEmojiIdException extends EqualEmojiIdException {
    private int id;
    private static int eeiNum = 0;
    private static HashMap<Integer, Integer> eeiMap = new HashMap<>();

    public MyEqualEmojiIdException(int id) {
        this.id = id;
        eeiNum++;
        if (eeiMap.containsKey(id)) {
            int time = eeiMap.get(id) + 1;
            eeiMap.put(id, time);
        } else {
            eeiMap.put(id, 1);
        }
    }

    public void print() {
        System.out.printf("eei-%d, %d-%d\n", eeiNum, id, eeiMap.get(id));
    }

}