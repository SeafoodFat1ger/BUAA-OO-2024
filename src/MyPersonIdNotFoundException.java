import com.oocourse.spec3.exceptions.PersonIdNotFoundException;

import java.util.HashMap;

public class MyPersonIdNotFoundException extends PersonIdNotFoundException {
    private int id;
    private static int pinfNum = 0;
    private static HashMap<Integer, Integer> pinfMap = new HashMap<>();

    public MyPersonIdNotFoundException(int id) {
        this.id = id;
        pinfNum++;
        if (pinfMap.containsKey(id)) {
            int time = pinfMap.get(id) + 1;
            pinfMap.put(id, time);
        } else {
            pinfMap.put(id, 1);
        }
    }

    public void print() {
        System.out.printf("pinf-%d, %d-%d\n", pinfNum, id, pinfMap.get(id));
    }
}
