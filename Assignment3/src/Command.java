/**
 * Interface for the Command Pattern.
 */
public interface Command {
    /** Performs the game action. */
    void execute();
    /** Gets the value for defined actions. */
    double getValue();
    /** Gets the description of chosen action. */
    String getDescription();
    /** Reverses the game action exactly. */
    void undo();
}
