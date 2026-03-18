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
        if (isSevenCards()) {
            while (isSevenCards()) {
                List<Command> forced = new ArrayList<>();

                if (checkCityCost() && !map.validCityNodes(this).isEmpty())
                    forced.add(new BuildCityCommand(this, map));

                if (checkSettlementCost() && !map.validSettlementNodes(this, false).isEmpty())
                    forced.add(new BuildSettlementCommand(this, map));

                if (checkRoadCost() && !map.validRoadEdges(this).isEmpty())
                    forced.add(new BuildRoadCommand(this, map));

                if (forced.isEmpty()) {
                    // Can't afford anything to reduce hand further
                    return "Forced: must spend cards but cannot afford anything, hand size: " + handSize();
                }

                Command best = selectBest(forced);
                best.execute();
            }
            return "Forced: spent cards down to " + handSize() + " (had 7+ cards)";
        }

        List<Command> available = new ArrayList<>();

        if (checkCityCost() && !map.validCityNodes(this).isEmpty())
            available.add(new BuildCityCommand(this, map));

        if (checkSettlementCost() && !map.validSettlementNodes(this, false).isEmpty())
            available.add(new BuildSettlementCommand(this, map));

        if (checkRoadCost() && !map.validRoadEdges(this).isEmpty())
            available.add(new BuildRoadCommand(this, map));

        if (handSize() > 5)
            available.add(new SpendCardsCommand(this));

        if (available.isEmpty())
            return "No action taken (insufficient resources or no valid placements)";

        Command best = selectBest(available);
        best.execute();
        return best.getDescription();
    }

    /**
     * Selects the highest value command from the list.
     * Breaks ties randomly as required by R3.2.
     */
    private Command selectBest(List<Command> commands) {
        double maxValue = 0;
        for (Command c : commands) {
            if (c.getValue() > maxValue) maxValue = c.getValue();
        }

        List<Command> tied = new ArrayList<>();
        for (Command c : commands) {
            if (c.getValue() == maxValue) tied.add(c);
        }

        return tied.get(random.nextInt(tied.size()));
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

