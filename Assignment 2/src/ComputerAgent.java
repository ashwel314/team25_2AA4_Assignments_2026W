import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Concrte implementation for an AI player.
 * Fulfills Requirement R1.2 by performing randomized actions.
*/
public class ComputerAgent extends Agent {
    private Random random = new Random();

    /**
     * Constructor for ComputerAgent.
     * @param id Unique identifier (0-3).
     * @param points Starting victory points.
     */
    public ComputerAgent(int id, int points){
        super(id, points);
    }

    /**
     * Requirement R1.2: Executes turn via randomized logic.
     * Picks randomly between building a road, settlement, or city if possible.
    */
    @Override
    public String takeTurn(GameMap map, int round){

        // AI logic: collect all currently valid moves
        List<String> possibleActions = new ArrayList<>();
        
        List<Integer> validRoads = map.validRoadEdges(this);
        List<Integer> validSettlements = map.validSettlementNodes(this, false);
        List<Integer> validCities = map.validCityNodes(this);

        if (!validRoads.isEmpty()) possibleActions.add("ROAD");
        if (!validSettlements.isEmpty()) possibleActions.add("SETTLEMENT");
        if (!validCities.isEmpty()) possibleActions.add("CITY");

        if (possibleActions.isEmpty()) {
            return "No valid actions possible.";
        }

        // Pick a random action type
        String chosenAction = possibleActions.get(random.nextInt(possibleActions.size()));

        switch (chosenAction) {
            case "ROAD":
                int edgeId = validRoads.get(random.nextInt(validRoads.size()));
                map.placeRoad(this, edgeId);
                return "Built a road on edge " + edgeId;
            case "SETTLEMENT":
                int nodeId = validSettlements.get(random.nextInt(validSettlements.size()));
                map.placeSettlement(this, nodeId);
                addPoints(1);
                return "Built a settlement on node " + nodeId;
            case "CITY":
                int cityNodeId = validCities.get(random.nextInt(validCities.size()));
                map.placeCity(this, cityNodeId);
                addPoints(1); // Net increase from settlement
                return "Upgraded settlement to city on node " + cityNodeId;
            default:
                return "No action taken.";
        }
   
    }

    /**
     * Requirement R1.1: Automated initial placement logic.
     * Places one settlement and one adjacent road randomly.
     * @param map The game map.
     * @return Description of the placement for console trace.
    */
    @Override
    public String initialPlacement(GameMap map){
        
        // 1. Find all valid settlement nodes (ignoring road connectivity for setup)
        List<Integer> validNodes = map.validSettlementNodes(this, true);
        if (validNodes.isEmpty()) return "Error: No valid placement found.";

        // Pick one at random
        int chosenNode = validNodes.get(random.nextInt(validNodes.size()));
        map.placeSettlement(this, chosenNode);
        addPoints(1);

        // 2. Find all adjacent edges to that node to place the mandatory road
        List<Integer> adjEdges = new ArrayList<>();
        for (int edgeId : map.getEdgesForNode(chosenNode)) {
            if (!map.getEdge(edgeId).isOccupied()) {
                adjEdges.add(edgeId);
            }
        }

        if (!adjEdges.isEmpty()) {
            int chosenEdge = adjEdges.get(random.nextInt(adjEdges.size()));
            map.placeRoad(this, chosenEdge);
            return "Placed initial settlement on node " + chosenNode + " and road on edge " + chosenEdge;
        }

        return "Placed initial settlement on node " + chosenNode + " (no adjacent edges found).";
    }

    /**
     * Helper to track points within the agent.
     */
    private void addPoints(int p) {
        this.totalPoints += p;
    }

}//ends class
