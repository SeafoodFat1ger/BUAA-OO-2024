import com.oocourse.spec3.exceptions.MessageIdNotFoundException;

import java.util.HashMap;

public class MyMessageIdNotFoundException extends MessageIdNotFoundException {
    private int id;
    private static int minfNum = 0;
    private static HashMap<Integer, Integer> minfMap = new HashMap<>();

    public MyMessageIdNotFoundException(int id) {
        this.id = id;
        minfNum++;
        if (minfMap.containsKey(id)) {
            int time = minfMap.get(id) + 1;
            minfMap.put(id, time);
        } else {
            minfMap.put(id, 1);
        }
    }

    public void print() {
        System.out.printf("minf-%d, %d-%d\n", minfNum, id, minfMap.get(id));
    }

}