import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles regex-based command parsing for Human players.
 * Fulfills R2.1 (console input) and R2.4 (step-forward via "go" to end turn).
 */
public class HumanCommandParser {

    private final Scanner scanner;

    public HumanCommandParser() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Main per-turn loop for a human player.
     * Continues until the user types "go".
     *
     * Supported commands (case-insensitive):
     *  - roll
     *  - list
     *  - build settlement [nodeId]
     *  - build road [edgeId]
     *  - go
     */
    public void handleTurn(Agent agent, GameMap map) {
        // Default: main phase (not initial placement), roll value unknown
        handleTurn(agent, map, false, -1);
    }

    /**
     * Overload that allows callers to distinguish between initial placement
     * and main-phase turns, and optionally pass in the dice roll already
     * performed by the game loop.
     *
     * @param agent               the human agent
     * @param map                 the game map
     * @param isInitialPlacement  true during setup rounds
     * @param lastRoll            dice result already rolled by the Game loop
     */
    public void handleTurn(Agent agent, GameMap map,
                           boolean isInitialPlacement,
                           int lastRoll) {
        boolean turnOver = false;
        System.out.println("\n[Human Turn - Agent " + agent.getId() + "]");
        System.out.println("Available commands: 'roll', 'build settlement [id]', 'build road [fromId, toId]', 'list', 'go'");

        // In initial placement, the human is allowed exactly one settlement
        // and one adjacent road for this setup round.
        boolean placedSettlement = false;
        boolean placedRoad = false;

        while (!turnOver) {
            System.out.print("Agent " + agent.getId() + " > ");
            String input = scanner.nextLine().trim();

            if (input.matches("(?i)^roll$")) {
                if (isInitialPlacement) {
                    System.out.println("Dice are not rolled during initial placement.");
                } else {
                    // Dice are rolled by the Game loop; just report what happened.
                    if (lastRoll > 0) {
                        System.out.println("Dice have already been rolled for this turn: " + lastRoll);
                        System.out.println("Resources (and any robber effects) have already been applied.");
                    } else {
                        System.out.println("Dice are handled automatically by the simulator this turn.");
                    }
                }
            } else if (input.matches("(?i)^go$")) {
                if (isInitialPlacement && (!placedSettlement || !placedRoad)) {
                    System.out.println("You must place one settlement and one road before ending this setup turn.");
                } else {
                    turnOver = true;
                    System.out.println("Ending turn...");
                }
            } else if (input.matches("(?i)^list$")) {
                System.out.println("Your Resources: " + agent.getResourceMap());
                System.out.println("Victory Points: " + agent.getTotalPoints());
            } else if (input.matches("(?i)^build\\s+settlement\\s+\\[?(\\d+)\\]?$")) {
                if (isInitialPlacement && placedSettlement) {
                    System.out.println("You have already placed your settlement for this setup turn.");
                    continue;
                }
                boolean success = processBuildSettlement(input, agent, map, isInitialPlacement);
                if (success && isInitialPlacement) {
                    placedSettlement = true;
                }
            } else if (input.matches("(?i)^build\\s+road\\s*\\[\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\]$")) {
                if (isInitialPlacement && !placedSettlement) {
                    System.out.println("Place your settlement first, then place the adjacent road.");
                    continue;
                }
                if (isInitialPlacement && placedRoad) {
                    System.out.println("You have already placed your road for this setup turn.");
                    continue;
                }
                boolean success = processBuildRoad(input, agent, map);
                if (success && isInitialPlacement) {
                    placedRoad = true;
                    // Once both are placed in setup, end this initial-placement turn automatically.
                    if (placedSettlement) {
                        System.out.println("Initial placement for this round is complete.");
                        turnOver = true;
                    }
                }
            } else {
                System.out.println("Syntax Error: Command not recognized. Use 'build settlement [id]', 'build road [fromId, toId]', 'list', 'roll', or 'go'.");
            }
        }
    }

    private boolean processBuildSettlement(String input, Agent agent, GameMap map,
                                           boolean isInitialPlacement) {
        Pattern p = Pattern.compile("(\\d+)");
        Matcher m = p.matcher(input);
        if (m.find()) {
            int nodeId = Integer.parseInt(m.group(1));

            if (map.validSettlementNodes(agent, isInitialPlacement).contains(nodeId)) {
                map.placeSettlement(agent, nodeId);
                agent.recordSettlementPlaced();
                System.out.println("Success: Settlement built on node " + nodeId);
                return true;
            } else {
                System.out.println("Game Error: Cannot build settlement on node " + nodeId + ". Check resources or distance rule.");
            }
        }
        return false;
    }

    private boolean processBuildRoad(String input, Agent agent, GameMap map) {
        // Expect pattern: build road [fromNodeId, toNodeId]
        // We already validated syntax in handleTurn, but re-parse robustly here.
        Pattern p = Pattern.compile(".*?(\\d+)\\D+(\\d+).*");
        Matcher m = p.matcher(input);
        if (!m.matches()) {
            System.out.println("Game Error: Could not parse node ids. Use 'build road [fromId, toId]'.");
            return false;
        }
        int fromNodeId = Integer.parseInt(m.group(1));
        int toNodeId = Integer.parseInt(m.group(2));

        int edgeId = map.findEdgeBetweenNodes(fromNodeId, toNodeId);
        if (edgeId == -1) {
            System.out.println("Game Error: No road edge directly connects nodes " + fromNodeId + " and " + toNodeId + ".");
            return false;
        }

        if (map.validRoadEdges(agent).contains(edgeId)) {
            map.placeRoad(agent, edgeId);
            agent.recordRoadPlaced();
            System.out.println("Success: Road built between nodes " + fromNodeId + " and " + toNodeId + " (edge " + edgeId + ")");
            return true;
        } else {
            System.out.println("Game Error: Illegal road placement between nodes " + fromNodeId + " and " + toNodeId + ".");
            return false;
        }
    }
}

