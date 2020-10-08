public class CycleFoundException extends Exception {
    private static final long serialVersionUID = -5655260614897085843L;
    public CycleFoundException() {
        super("A topological sort cannot be found for a graph containing cycles.");
    }
}