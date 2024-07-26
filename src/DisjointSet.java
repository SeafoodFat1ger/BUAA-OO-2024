import java.util.HashMap;

public class DisjointSet {
    private HashMap<Integer, Integer> father;
    private HashMap<Integer, Integer> rank;
    private boolean dirty;

    public DisjointSet() {
        this.father = new HashMap<>();
        this.rank = new HashMap<>();
        dirty = false;
    }

    public void add(int id) {
        //if (!father.containsKey(id)) {
        father.put(id, id);
        rank.put(id, 1);
        //}
    }

    public int find(int id) {
        int root = id;
        while (root != father.get(root)) {
            root = father.get(root);
        }

        //不能递归
        int now = id;
        int tmp;
        while (now != root) {
            tmp = father.get(now);
            father.put(now, root);
            now = tmp;
        }
        return root;
    }

    //按秩合并????
    public int merge(int id1, int id2) {
        int root1 = find(id1);
        int root2 = find(id2);
        if (root1 == root2) {
            return -1;
        }
        int rank1 = rank.get(root1);
        int rank2 = rank.get(root2);
        if (rank1 == rank2) {
            rank.put(root2, rank2 + 1);
            father.put(root1, root2);
        } else if (rank1 > rank2) {
            father.put(root2, root1);
        } else {
            father.put(root1, root2);
        }
        return 0;
    }

    public void modify(int id1, int id2) {
        father.put(id1, id2);
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void clearAll() {
        father.clear();
        rank.clear();
    }
}
