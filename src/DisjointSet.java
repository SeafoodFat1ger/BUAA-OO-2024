import java.util.HashMap;
import java.util.HashSet;

public class DisjointSet {
    private HashMap<Integer, Integer> father;
    private HashMap<Integer, HashSet<Integer>> ancestors;
    private HashMap<Integer, Integer> rank;

    public DisjointSet() {
        this.father = new HashMap<>();
        this.rank = new HashMap<>();
        this.ancestors = new HashMap<>();
    }

    public void add(int id) {
        if (!father.containsKey(id)) {
            father.put(id, id);
            rank.put(id, 1);
        }
    }

    public int find(int id) {
        int root = id;
        while (root != father.get(root)) {
            root = father.get(root);
        }
        HashSet<Integer> rootSet = new HashSet<>();
        if (ancestors.containsKey(root)) {
            rootSet = ancestors.get(root);
        }

        //不能递归
        int now = id;
        int tmp;
        while (now != root) {
            tmp = father.get(now);
            father.put(now, root);
            rootSet.add(now);
            now = tmp;
        }
        return root;
    }

    //按秩合并????
    public void merge(int id1, int id2) {
        int root1 = find(id1);
        int root2 = find(id2);
        father.put(root1, root2);
        putAll(root1, root2);
        //        if (root1 == root2) {
        //            return -1;
        //        }
        //        int rank1 = rank.get(root1);
        //        int rank2 = rank.get(root2);
        //        if (rank1 == rank2) {
        //            rank.put(root2, rank2 + 1);
        //            father.put(root1, root2);
        //        } else if (rank1 > rank2) {
        //            father.put(root2, root1);
        //        } else {
        //            father.put(root1, root2);
        //        }
        //        return 0;
    }

    public void putAll(int id1, int id2) {
        HashSet<Integer> set2 = new HashSet<>();
        if (ancestors.containsKey(id2)) {
            set2 = ancestors.get(id2);
        }
        if (ancestors.containsKey(id1)) {
            HashSet<Integer> set1 = ancestors.get(id1);
            for (Integer id : set1) {
                father.put(id, id2);
                set2.add(id);
            }
        }
        set2.add(id1);
        ancestors.put(id2, set2);
    }

    public void modify(int id1, int id2) {
        father.put(id1, id2);
        rank.put(id1, 1);
    }

    public void printAll() {
        for (Integer i : father.keySet()) {
            Integer j = father.get(i);
            System.out.println(i + " " + j + " " + "*******");
        }
    }

}