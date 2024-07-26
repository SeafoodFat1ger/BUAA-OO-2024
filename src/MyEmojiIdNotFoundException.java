import com.oocourse.spec3.exceptions.EmojiIdNotFoundException;

import java.util.HashMap;

public class MyEmojiIdNotFoundException extends EmojiIdNotFoundException {
    private int id;
    private static int einfNum = 0;
    private static HashMap<Integer, Integer> einfMap = new HashMap<>();

    public MyEmojiIdNotFoundException(int id) {
        this.id = id;
        einfNum++;
        if (einfMap.containsKey(id)) {
            int time = einfMap.get(id) + 1;
            einfMap.put(id, time);
        } else {
            einfMap.put(id, 1);
        }
    }

    public void print() {
        System.out.printf("einf-%d, %d-%d\n", einfNum, id, einfMap.get(id));
    }
}