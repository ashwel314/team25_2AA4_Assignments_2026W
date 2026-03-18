import java.util.Stack;

/**
 * Manages the undo and redo stacks for the game session.
 */
public class CommandManager {
    private final Stack<Command> undoStack = new Stack<>();
    private final Stack<Command> redoStack = new Stack<>();
    
    /** Executes a command and pushes it to the undo stack. */
    public void executeCommand(Command cmd){
        cmd.execute();
        undoStack.push(cmd);
        redoStack.clear(); 
    }

    /** Pops from undo stack and moves it to redo stack. */
    public void undo(){
        if(!undoStack.isEmpty()){
            Command cmd = undoStack.pop();
            cmd.undo();
            redoStack.push(cmd);
        } else{
            System.out.println("Nothing to UNDO");
        }
    }

    /** Pops from redo stack and re-executes. */
    public void redo(){
        if(!redoStack.isEmpty()) {
            Command cmd = redoStack.pop();
            cmd.execute();
            undoStack.push(cmd);
        } else{
            System.out.println("Nothing to REDO");
        }
    }
}
