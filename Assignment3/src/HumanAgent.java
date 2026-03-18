import java.util.Scanner;

/**
 * Human-controlled player using CommandParser and CommandType (R2.1).
 */
public class HumanAgent extends Agent {

    private final CommandParser parser;

    public HumanAgent(int id, int points, CommandParser parser) {
        super(id, points);
        this.parser = parser;
    }

    @Override
    public String takeTurn(GameMap map, int round) {
        // Game calls handleTurn(map, round, roll) instead when it's the human's turn
        return "Human turn completed.";
    }

    /**
     * Main-phase turn loop: read commands until GO. Dice roll already applied by Game.
     */
    public void handleTurn(GameMap map, int round, int diceRoll, CommandManager cmdManager) {
        Scanner scanner = new Scanner(System.in);
        boolean hasRolledThisTurn = false;

        System.out.println("\n[Human Turn - Agent " + id + "]");
        System.out.println("Commands: roll, list, build settlement [id], build road [from, to], undo, redo, go");

        while (true) {
            System.out.print("Agent " + id + " > ");
            String input = scanner.nextLine();
            CommandType type = parser.parser(input);

            switch (type) {
                case UNDO:
                    cmdManager.undo();
                    System.out.println("Action undone.");
                    break;
                case REDO:
                    cmdManager.redo();
                    System.out.println("Action redone.");
                    break;
                case BUILD_SETTLEMENT:
                    if (settlementsRemaining > 0 && (hasRolledThisTurn || round == 0)) {
                        int nodeId = parser.getNodeId();
                        cmdManager.executeCommand(new BuildSettlementCommand(this, map, nodeId));
                    }
                break;
                case BUILD_ROAD:
                    if (roadsRemaining > 0 && (hasRolledThisTurn || round == 0)) {
                        int from = parser.getFromNodeId();
                        int to = parser.getToNodeId();
                        int edgeId = map.findEdgeBetweenNodes(from, to);
                        cmdManager.executeCommand(new BuildRoadCommand(this, map, edgeId));
                    }
                    break;
                case GO:
                    if (hasRolledThisTurn) return;
                    System.out.println("Roll first!");
                    break;
                case ROLL:
                    hasRolledThisTurn = true;
                    System.out.println("Rolled: " + diceRoll);
                    break;
                default:
                    System.out.println("Invalid command.");
            }
        }
    }

    /**
     * Initial placement: one settlement then one road, then end.
     */
    public void handleInitialPlacement(GameMap map) {
        Scanner scanner = new Scanner(System.in);
        boolean placedSettlement = false;
        boolean placedRoad = false;

        System.out.println("\n[Human Turn - Agent " + id + "] Initial placement: place one settlement, then one road.");
        System.out.println("Commands: build settlement [id], build road [from, to], list, go");

        while (true) {
            System.out.print("Agent " + id + " > ");
            String input = scanner.nextLine();
            CommandType type = parser.parser(input);

            if (type == CommandType.LIST) {
                System.out.println("Your hand: " + getResourceMap());
                System.out.println("Victory Points: " + getTotalPoints());
            } else if (type == CommandType.GO) {
                if (!placedSettlement || !placedRoad) {
                    System.out.println("You must place one settlement and one road before ending.");
                    continue;
                }
                System.out.println("Initial placement complete.");
                return;
            } else if (type == CommandType.BUILD_SETTLEMENT) {
                if (placedSettlement) {
                    System.out.println("You have already placed your settlement for this round.");
                    continue;
                }
                if (tryBuildSettlement(map, true)) {
                    placedSettlement = true;
                    System.out.println("Successfully built settlement on node " + parser.getNodeId());
                } else {
                    System.out.println("Failed to build settlement on node " + parser.getNodeId());
                }
            } else if (type == CommandType.BUILD_ROAD) {
                if (!placedSettlement) {
                    System.out.println("Place your settlement first.");
                    continue;
                }
                if (placedRoad) {
                    System.out.println("You have already placed your road for this round.");
                    continue;
                }
                if (tryBuildRoad(map, true)) {
                    placedRoad = true;
                    System.out.println("Successfully built road from node " + parser.getFromNodeId() + " to " + parser.getToNodeId());
                } else {
                    System.out.println("Failed to build road.");
                }
            } else if (type == CommandType.ROLL || type == CommandType.BUILD_CITY) {
                System.out.println("Not available during initial placement.");
            } else if (type == CommandType.INVALID) {
                System.out.println("Invalid command.");
            }
        }
    }

    @Override
    public String initialPlacement(GameMap map) {
        handleInitialPlacement(map);
        return "Manual setup finished.";
    }

    private boolean tryBuildSettlement(GameMap map, boolean isInitialPlacement) {
        if (settlementsRemaining <= 0) return false;
        if (!isInitialPlacement && !checkSettlementCost()) return false;
        int nodeId = parser.getNodeId();
        if (nodeId < 0) return false;
        if (!map.validSettlementNodes(this, isInitialPlacement).contains(nodeId)) return false;
        if (!isInitialPlacement) payForSettlement();
        map.placeSettlement(this, nodeId);
        recordSettlementPlaced();
        return true;
    }

    private boolean tryBuildRoad(GameMap map, boolean isInitialPlacement) {
        if (roadsRemaining <= 0) return false;
        int from = parser.getFromNodeId();
        int to = parser.getToNodeId();
        if (from < 0 || to < 0) return false;
        int edgeId = map.findEdgeBetweenNodes(from, to);
        if (edgeId < 0) return false;
        if (!map.validRoadEdges(this).contains(edgeId)) return false;
        if (!isInitialPlacement) payForRoad();
        map.placeRoad(this, edgeId);
        recordRoadPlaced();
        return true;
    }

    private boolean tryBuildCity(GameMap map) {
        if (citiesRemaining <= 0) return false;
        if (!checkCityCost()) return false;
        int nodeId = parser.getNodeId();
        if (nodeId < 0) return false;
        if (!map.validCityNodes(this).contains(nodeId)) return false;
        payForCity();
        map.placeCity(this, nodeId);
        recordCityPlaced();
        return true;
    }
}
