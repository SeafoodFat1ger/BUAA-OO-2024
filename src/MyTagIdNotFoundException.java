import com.oocourse.spec2.exceptions.TagIdNotFoundException;

import java.util.HashMap;

public class MyTagIdNotFoundException extends TagIdNotFoundException {
    private int id;
    private static int tinfNum = 0;
    private static HashMap<Integer, Integer> tinfMap = new HashMap<>();

    public MyTagIdNotFoundException(int id) {
        this.id = id;
        tinfNum++;
        if (tinfMap.containsKey(id)) {
            int time = tinfMap.get(id) + 1;
            tinfMap.put(id, time);
        } else {
            tinfMap.put(id, 1);
        }
    }

    public void print() {
        System.out.printf("tinf-%d, %d-%d\n", tinfNum, id, tinfMap.get(id));
    }

}