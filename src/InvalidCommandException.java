public class InvalidCommandException extends Exception {
    private static final long serialVersionUID = -4144885971958219488L;
    public InvalidCommandException() {
        super("Command is invalid");
    }
}