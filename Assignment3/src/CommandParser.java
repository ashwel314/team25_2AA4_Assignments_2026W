import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses human console input into CommandType and extracts node/edge ids (R2.1).
 */
public class CommandParser {

    private int toNodeId;
    private int fromNodeId;
    private int nodeId;
    private CommandType commandType;

    public CommandParser() {
        commandType = CommandType.INVALID;
        toNodeId = -1;
        fromNodeId = -1;
        nodeId = -1;
    }

    /**
     * Parses input and returns the command type; node/from/to ids are stored for retrieval.
     */
    public CommandType parser(String input) {
        commandType = CommandType.INVALID;
        toNodeId = -1;
        fromNodeId = -1;
        nodeId = -1;

        if (input == null) {
            return CommandType.INVALID;
        }
        input = input.trim();

        if (Pattern.compile("^roll$", Pattern.CASE_INSENSITIVE).matcher(input).matches()) {
            commandType = CommandType.ROLL;
            return commandType;
        }
        if (Pattern.compile("^go$", Pattern.CASE_INSENSITIVE).matcher(input).matches()) {
            commandType = CommandType.GO;
            return commandType;
        }
        if (Pattern.compile("^list$", Pattern.CASE_INSENSITIVE).matcher(input).matches()) {
            commandType = CommandType.LIST;
            return commandType;
        }
        
        /*Addition for Assignment 3*/
        if (Pattern.compile("^undo$", Pattern.CASE_INSENSITIVE).matcher(input).matches()) {
            commandType = CommandType.UNDO;
            return commandType; 
        }
        if (Pattern.compile("^redo$", Pattern.CASE_INSENSITIVE).matcher(input).matches()) {
            commandType = CommandType.REDO;
            return commandType;
        }

        // build settlement <id> or build settlement [id]
        Matcher settlementMatcher = Pattern.compile("^build\\s+settlement\\s+\\[?(\\d+)\\]?$", Pattern.CASE_INSENSITIVE).matcher(input);
        if (settlementMatcher.matches()) {
            commandType = CommandType.BUILD_SETTLEMENT;
            nodeId = Integer.parseInt(settlementMatcher.group(1));
            return commandType;
        }

        // build city <id> or build city [id]
        Matcher cityMatcher = Pattern.compile("^build\\s+city\\s+\\[?(\\d+)\\]?$", Pattern.CASE_INSENSITIVE).matcher(input);
        if (cityMatcher.matches()) {
            commandType = CommandType.BUILD_CITY;
            nodeId = Integer.parseInt(cityMatcher.group(1));
            return commandType;
        }

        // build road <from>, <to> or build road [from, to]
        Matcher roadMatcher = Pattern.compile("^build\\s+road\\s*\\[?\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\]?$", Pattern.CASE_INSENSITIVE).matcher(input);
        if (roadMatcher.matches()) {
            commandType = CommandType.BUILD_ROAD;
            fromNodeId = Integer.parseInt(roadMatcher.group(1));
            toNodeId = Integer.parseInt(roadMatcher.group(2));
            return commandType;
        }

        return CommandType.INVALID;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public int getNodeId() {
        return nodeId;
    }

    public int getFromNodeId() {
        return fromNodeId;
    }

    public int getToNodeId() {
        return toNodeId;
    }
}
