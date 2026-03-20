import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ComputerAgent extends Agent {

    private final Random random = new Random();

    public ComputerAgent(int id, int points) {
        super(id, points);
    }

    @Override
    public String takeTurn(GameMap map, int round, int diceRoll) {
        // exact same body as your existing takeTurn(GameMap map, int round)
        // diceRoll is unused but satisfies the contract
        if (isSevenCards()) {
            while (isSevenCards()) {
                List<Command> forced = new ArrayList<>();

                if (checkCityCost() && !map.validCityNodes(this).isEmpty())
                    forced.add(new BuildCityCommand(this, map));

                if (checkSettlementCost() && !map.validSettlementNodes(this, false).isEmpty())
                    forced.add(new BuildSettlementCommand(this, map));

                if (checkRoadCost() && !map.validRoadEdges(this).isEmpty())
                    forced.add(new BuildRoadCommand(this, map));

                if (forced.isEmpty())
                    return "Forced: must spend cards but cannot afford anything, hand size: " + handSize();

                Command best = selectBest(forced);
                best.execute();
            }
            return "Forced: spent cards down to " + handSize() + " (had 7+ cards)";
        }

        if (checkRoadCost()) {
            int connectingEdge = findConnectingRoadEdge(map);
            if (connectingEdge != -1) {
                map.placeRoad(this, connectingEdge);
                decrementRoadsRemaining();
                payForRoad();
                return "Built connecting road on edge " + connectingEdge + " (spatial constraint)";
            }
        }

        if (checkRoadCost()) {
            int myLongest = map.longestRoadForAgent(this);
            boolean threatened = isThreatenedByOpponent(map, myLongest);
            if (threatened) {
                List<Integer> validEdges = map.validRoadEdges(this);
                if (!validEdges.isEmpty()) {
                    int bestEdge = getBestExtensionEdge(map, validEdges);
                    map.placeRoad(this, bestEdge);
                    decrementRoadsRemaining();
                    payForRoad();
                    return "Built defensive road on edge " + bestEdge + " (competitive constraint)";
                }
            }
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

    @Override
    public String executeInitialPlacement(GameMap map) {
        // exact same body as your existing initialPlacement(GameMap map)
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

    
// Finds an unoccupied edge that would connect two separate clusters
// of this agent's roads that are exactly 1 edge apart. "< 2 units away" means the gap between the two nearest endpoints
// of the two clusters is bridgeable by a single road.

    private int findConnectingRoadEdge(GameMap map) {
        // Collect all edge-endpoint nodes that are "dangling" — on this
        // agent's road network but not internal (i.e. they touch < 2 of
        // this agent's roads, making them open ends).
        List<Integer> openEnds = new ArrayList<>();
        for (int e = 0; e < GameMap.NUM_EDGES; e++) {
            if (!map.getEdge(e).isOccupied()) continue;
            if (map.getEdge(e).getRoad().getOwner() != this) continue;
            for (int nodeId : map.getNodesForEdge(e)) {
                int ownedCount = 0;
                for (int adj : map.getEdgesForNode(nodeId)) {
                    if (map.getEdge(adj).isOccupied()
                            && map.getEdge(adj).getRoad().getOwner() == this) {
                        ownedCount++;
                    }
                }
                // An open end touches exactly 1 of our roads at this node
                if (ownedCount == 1 && !openEnds.contains(nodeId)) {
                    openEnds.add(nodeId);
                }
            }
        }

        // For every valid (unoccupied, connected) road edge, check whether
        // it would bridge two open ends that belong to different road clusters.
        for (int candidate : map.validRoadEdges(this)) {
            int[] endNodes = map.getNodesForEdge(candidate);
            boolean node0isOpen = openEnds.contains(endNodes[0]);
            boolean node1isOpen = openEnds.contains(endNodes[1]);
            // Bridging: one end connects to one open cluster, other end to another.
            // We verify they aren't already in the same connected component.
            if (node0isOpen && node1isOpen
                    && !sameRoadComponent(map, endNodes[0], endNodes[1])) {
                return candidate;
            }
        }
        return -1; // No connecting gap found
    }

// BFS/flood-fill to check whether two nodes are already in the same road-connected component for this agent.

    private boolean sameRoadComponent(GameMap map, int startNode, int targetNode) {
        if (startNode == targetNode) return true;
        boolean[] visited = new boolean[GameMap.NUM_NODES];
        java.util.Queue<Integer> queue = new java.util.LinkedList<>();
        queue.add(startNode);
        visited[startNode] = true;

        while (!queue.isEmpty()) {
            int current = queue.poll();
            for (int edgeId : map.getEdgesForNode(current)) {
                if (!map.getEdge(edgeId).isOccupied()) continue;
                if (map.getEdge(edgeId).getRoad().getOwner() != this) continue;
                int[] ends = map.getNodesForEdge(edgeId);
                int next = (ends[0] == current) ? ends[1] : ends[0];
                if (next == targetNode) return true;
                if (!visited[next]) {
                    visited[next] = true;
                    queue.add(next);
                }
            }
        }
        return false;
    }
  
// Returns true if any opponent's longest road is within 1 segment
// of this agent's longest road length.

    private boolean isThreatenedByOpponent(GameMap map, int myLongest) {
        // Walk every edge looking for roads owned by other agents
        for (int e = 0; e < GameMap.NUM_EDGES; e++) {
            if (!map.getEdge(e).isOccupied()) continue;
            Agent owner = map.getEdge(e).getRoad().getOwner();
            if (owner == this) continue;
            int theirLongest = map.longestRoadForAgent(owner);
            // "Within one segment" means they only need 1 more road to tie/beat us
            if (theirLongest >= myLongest - 1) {
                return true;
            }
        }
        return false;
    }

    
// From the list of valid edges, picks the one whose placement
// results in the greatest increase to this agent's longest road.
  
    private int getBestExtensionEdge(GameMap map, List<Integer> validEdges) {
        int bestEdge = validEdges.get(0);
        int bestLength = -1;

        for (int edgeId : validEdges) {
            // Temporarily place the road to measure the new longest road
            map.placeRoad(this, edgeId);
            int length = map.longestRoadForAgent(this);
            // Undo the temporary placement
            map.getEdge(edgeId).setRoad(null);

            if (length > bestLength) {
                bestLength = length;
                bestEdge = edgeId;
            }
        }
        return bestEdge;
    }

}

