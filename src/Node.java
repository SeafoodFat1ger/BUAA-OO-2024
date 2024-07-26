public class Node implements Comparable<Node> {
    private final int key;
    private final int value;

    public Node(int key, int value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public int compareTo(Node o) {
        return this.value - o.value;
    }

    public int getKey() {
        return key;
    }

}