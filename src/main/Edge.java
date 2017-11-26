package main;

/**
 * Represents a directed edge in a graph
 */
public class Edge {
    private int head;
    private int tail;

    public Edge(int tail, int head) {
        this.head = head;
        this.tail = tail;
    }

    public int getHead() {
        return head;
    }

    public void setHead(int head) {
        this.head = head;
    }

    public int getTail() {
        return tail;
    }

    public void setTail(int tail) {
        this.tail = tail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        return head == edge.head && tail == edge.tail;
    }

    @Override
    public int hashCode() {
        int result = head;
        result = 31 * result + tail;
        return result;
    }
}
