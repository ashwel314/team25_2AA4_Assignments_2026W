/**
 * Interface for the Command Pattern.
 */
public interface Command {
    /** Performs the game action. */
    void execute();
    double getValue();
    String getDescription();
    
    /** Reverses the game action exactly. */
    void undo();
}
