import com.oocourse.spec2.exceptions.EqualRelationException;

import java.util.HashMap;

public class MyEqualRelationException extends EqualRelationException {
    private int id1;
    private int id2;
    private static int erNum = 0;
    private static HashMap<Integer, Integer> erMap = new HashMap<>();

    public MyEqualRelationException(int id1, int id2) {
        if (id1 < id2) {
            this.id1 = id1;
            this.id2 = id2;
        } else {
            this.id1 = id2;
            this.id2 = id1;
        }
        erNum++;
        if (erMap.containsKey(id1)) {
            int time = erMap.get(id1) + 1;
            erMap.put(id1, time);
        } else {
            erMap.put(id1, 1);
        }
        if (id1 != id2) {
            if (erMap.containsKey(id2)) {
                int time = erMap.get(id2) + 1;
                erMap.put(id2, time);
            } else {
                erMap.put(id2, 1);
            }
        }
    }

    public void print() {
        System.out.printf("er-%d, %d-%d, %d-%d\n",
                erNum, id1, erMap.get(id1), id2, erMap.get(id2));
    }

}
