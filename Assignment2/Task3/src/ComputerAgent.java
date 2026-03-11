import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * AI-controlled agent that implements the original random-build logic.
 * This class is effectively the old concrete {@code Agent} from Task 3.
 */
public class ComputerAgent extends Agent {

    private final Random random = new Random();

    public ComputerAgent(int id, int points) {
        super(id, points);
    }

    @Override
    public String takeTurn(GameMap map, int round) {
        boolean forced = isSevenCards();

        List<String> actions = new ArrayList<>();
        if (checkRoadCost() && !map.validRoadEdges(this).isEmpty()) {
            actions.add("road");
        }
        if (checkSettlementCost() && !map.validSettlementNodes(this, false).isEmpty()) {
            actions.add("settlement");
        }
        if (checkCityCost() && !map.validCityNodes(this).isEmpty()) {
            actions.add("city");
        }

        if (actions.isEmpty()) {
            return forced
                    ? "No build possible despite 7+ cards"
                    : "No action taken (insufficient resources or no valid placements)";
        }

        String chosen = actions.get(random.nextInt(actions.size()));
        switch (chosen) {
            case "road":
                return buildRoad(map) ? "Built a road" : "Road build failed";
            case "settlement":
                return buildSettlement(map) ? "Built a settlement" : "Settlement build failed";
            case "city":
                return buildCity(map) ? "Built a city" : "City build failed";
            default:
                return "No action taken";
        }
    }

    @Override
    public String initialPlacement(GameMap map) {
        List<Integer> validNodes = map.validSettlementNodes(this, true);
        if (validNodes.isEmpty()) return "No valid initial settlement position found";

        int nodeId = validNodes.get(random.nextInt(validNodes.size()));
        map.placeSettlement(this, nodeId);
        decrementSettlementsRemaining();
        addPoints(1);

        List<Integer> adjEdges = new ArrayList<>();
        for (int e : map.getEdgesForNode(nodeId)) {
            if (!map.getEdge(e).isOccupied()) adjEdges.add(e);
        }
        String roadDesc = "";
        if (!adjEdges.isEmpty()) {
            int edgeId = adjEdges.get(random.nextInt(adjEdges.size()));
            map.placeRoad(this, edgeId);
            decrementRoadsRemaining();
            roadDesc = " and road on edge " + edgeId;
        }
        return "Placed settlement on node " + nodeId + roadDesc;
    }
}

