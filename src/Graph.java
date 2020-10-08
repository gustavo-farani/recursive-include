import java.util.Vector;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

class Graph<T> {
    enum VertexState {
        UNVISITED, PRE_VISITED, POST_VISITED
    }
    private int n;
    private Map<T, Integer> index;
    private Vector<T> sequence;
    private Vector<Vector<Integer>> adj;
    private Vector<VertexState> state;
    public Graph () {
        this.n = 0;
        this.index = new HashMap<T, Integer>();
        this.sequence = new Vector<T>();
        this.adj = new Vector<Vector<Integer>>();
        this.state = new Vector<VertexState>();
    }
    public int indexOf (T x) {
        if (!index.containsKey(x)) {
            int i = n++;
            index.put(x, i);
            sequence.addElement(x);
            adj.addElement(new Vector<Integer>());
            state.addElement(VertexState.UNVISITED);
            return i;
        } else {
            return index.get(x);
        }
    }
    public void addArc (T from, T to) {
        adj.get(this.indexOf(from))
           .addElement(this.indexOf(to));
    }
    private void printGraph () {
        for (int i = 0; i < n; i++) {
            for (int j : adj.get(i)) {
                System.err.printf("%d %d\n", i, j);
            }
        }
    }
    public Vector<T> topologicalSort () throws CycleFoundException {
        Vector<T> order = new Vector<T>();
        for (int i = 0; i < n; i++) {
            if (state.get(i) == VertexState.UNVISITED) {
                this.dfs(i, order);
            }
        }
        Collections.reverse(order);
        return order;
    }
    private void dfs (int from, Vector<T> order) throws CycleFoundException {
        state.set(from, VertexState.PRE_VISITED);
        for (int to : adj.get(from)) {
            if (state.get(to) == VertexState.UNVISITED) {
                dfs(to, order);
            } else if (state.get(to) == VertexState.PRE_VISITED) {
                this.printGraph();
                throw new CycleFoundException();
            }
        }
        order.addElement(sequence.get(from));
        state.set(from, VertexState.POST_VISITED);
    }
}