/**
 * Interface for the Command Pattern.
 * Every game action must implement execute and undo for R3.1.
 */
public interface Command {
    /** Performs the game action. */
    void execute();
    
    /** Reverses the game action exactly. */
    void undo();
}