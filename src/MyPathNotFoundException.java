import com.oocourse.spec2.exceptions.PathNotFoundException;

import java.util.HashMap;

public class MyPathNotFoundException extends PathNotFoundException {
    private int id1;
    private int id2;
    private static int pnfNum = 0;
    private static HashMap<Integer, Integer> pnfMap = new HashMap<>();

    public MyPathNotFoundException(int id1, int id2) {
        if (id1 < id2) {
            this.id1 = id1;
            this.id2 = id2;
        } else {
            this.id1 = id2;
            this.id2 = id1;
        }
        pnfNum++;
        if (pnfMap.containsKey(id1)) {
            int time = pnfMap.get(id1) + 1;
            pnfMap.put(id1, time);
        } else {
            pnfMap.put(id1, 1);
        }
        if (pnfMap.containsKey(id2)) {
            int time = pnfMap.get(id2) + 1;
            pnfMap.put(id2, time);
        } else {
            pnfMap.put(id2, 1);
        }
    }

    public void print() {
        System.out.printf("pnf-%d, %d-%d, %d-%d\n",
                pnfNum, id1, pnfMap.get(id1), id2, pnfMap.get(id2));
    }

}