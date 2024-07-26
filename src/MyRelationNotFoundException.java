import com.oocourse.spec2.exceptions.RelationNotFoundException;

import java.util.HashMap;

public class MyRelationNotFoundException extends RelationNotFoundException {
    private int id1;
    private int id2;
    private static int rnfNum = 0;
    private static HashMap<Integer, Integer> rnfMap = new HashMap<>();

    public MyRelationNotFoundException(int id1, int id2) {
        if (id1 < id2) {
            this.id1 = id1;
            this.id2 = id2;
        } else {
            this.id1 = id2;
            this.id2 = id1;
        }
        rnfNum++;
        if (rnfMap.containsKey(id1)) {
            int time = rnfMap.get(id1) + 1;
            rnfMap.put(id1, time);
        } else {
            rnfMap.put(id1, 1);
        }
        if (rnfMap.containsKey(id2)) {
            int time = rnfMap.get(id2) + 1;
            rnfMap.put(id2, time);
        } else {
            rnfMap.put(id2, 1);
        }
    }

    public void print() {
        System.out.printf("rnf-%d, %d-%d, %d-%d\n",
                rnfNum, id1, rnfMap.get(id1), id2, rnfMap.get(id2));
    }

}
