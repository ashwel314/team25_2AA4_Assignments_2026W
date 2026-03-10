/**
 *Student Name: Ankita Sharma
*/

package Assignment1;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * The GamePlay class serves as the central controller for the Catan simulation.
 * It manages the iterative game loop, coordinated resource production between the board
 * and agents, and monitors victory conditions. 
*/
public class GamePlay {
	
	private int roundNumber = 0;
    private int turnNumber = 0;
    private final List<Agent> agents;
    private final Board board;
    private final Dice dice;
    private final int maxRounds;
    private final Random random = new Random();
    /** Agent currently holding Longest Road (2 VPs). Null if no one has 5+ segments. */
    private Agent agentWithLongestRoad = null;

	/**
	 * Initializes the simulation controller with necessary game components.
	 * @param agents		The array of 4 agents participating agents.
	 * @param board			The game board contaning tiles, nodes, and edges.
	 * @param dice			The multi-dice component for rolling.
	 * @param maxRounds		The limit of rounds to simulate before termination.
	*/
	public GamePlay(List<Agent> agents, Board board, Dice dice, int maxRounds){
		this.agents = agents;
		this.board = board;
		this.dice = dice;
		this.maxRounds = maxRounds;
	}

	/** 
	 * Executes the main simulation loop. The process continues until an agent 
	 * reaches 10 victory points or the maximum round limit is reached.
	*/
	public void runSimulation() {
        System.out.println("Initial VPs: " + agents.get(0).getName() + " " + agents.get(0).getVictoryPoints() + ", "
            + agents.get(1).getName() + " " + agents.get(1).getVictoryPoints() + ", "
            + agents.get(2).getName() + " " + agents.get(2).getVictoryPoints() + ", "
            + agents.get(3).getName() + " " + agents.get(3).getVictoryPoints() + " (settlements placed).");
        boolean victoryAchieved = false;

        while (roundNumber < maxRounds && !victoryAchieved) {
            roundNumber++;
            for (Agent activeAgent : agents) {
                turnNumber++;
                executeTurn(activeAgent);

                if (activeAgent.getVictoryPoints() >= 10){
                    victoryAchieved = true;
                    break;
                }
            }
            printRoundSummary();
        }
    }

	/**
	 * Handles the logic for an individual agent's turn, including resource 
	 * production and mandatory building actions if resources exceed limits.
	 * @param agent	The agent currently taking their turn.
	 */
	private void executeTurn(Agent agent){
        int roll = dice.roll();
        String action = "Rolled " + roll;

        if (roll != 7) {
            distributeResources(roll);
        } else {
            handleSevenRoll();
        }

        if (agent.getHandSize() > 7) {
            String buildAction = performRandomBuildAction(agent);
            if (buildAction != null) {
                action = buildAction;
            } else {
                discardDownToSeven(agent);
                action = "Discarded down to 7 (no valid build)";
            }
        }
        System.out.println("[" + roundNumber + " / " + agent.getName() + "]: " + action + " - " + agent.getVictoryPoints() + " VPs");
    }

	/**
	 * Matches the current dice roll against tile values on the board and distributes 
	 * resources to agents with adjacent buildings.
	 * @param roll	The integer value resulting from the dice roll.
	 */
	private void distributeResources(int roll){
        for (int i = 0; i < board.getTiles().size(); i++){ // change back to 19
            Tile currentTile = board.getTile(i);

            if (currentTile.getRollValue() == roll){
                Resources producedResource = currentTile.getResource();

                if (producedResource == null || producedResource == Resources.NULL) continue; // desert
                for (Node adjacentNode : currentTile.getAdjacentNodes()) {
                    Building building = board.getBuildingAtNode(adjacentNode);

                    if (building != null){
                        Agent owner = building.getAgent();
                        int amount = (building instanceof City) ? 2 : 1;

                        for (int j = 0; j < amount; j++){
                            owner.addResource(producedResource);
                        }
                    }
                }
            } 
        } 
    }

	/**
	 * Tries to build one valid item when hand > 7: settlement, city, or road.
	 * Deducts resources, adds VPs, and returns a human-readable action string; null if no build.
	 */
	private String performRandomBuildAction(Agent agent){
        List<BuildOption> options = new ArrayList<>();

        if (agent.canAffordSettlement()) {
            for (Node node : board.getNodes()) {
                if (canPlaceSettlement(agent, node)) {
                    options.add(new BuildOption("Built Settlement at node " + node.getNodeNum(), () -> {
                        agent.deductSettlementCost();
                        board.placeSettlement(node, new Settlement(agent, node));
                        agent.addVictoryPoints(1);
                    }));
                }
            }
        }

        if (agent.canAffordCity()) {
            for (Node node : board.getNodes()) {
                if (canPlaceCity(agent, node)) {
                    options.add(new BuildOption("Built City at node " + node.getNodeNum(), () -> {
                        agent.deductCityCost();
                        board.placeCity(node, new City(agent, node), agent);
                        agent.addVictoryPoints(1); // net +1 (city 2 - settlement 1)
                    }));
                }
            }
        }

        if (agent.canAffordRoad()) {
            for (Edge edge : board.getEdges()) {
                if (canPlaceRoad(agent, edge)) {
                    options.add(new BuildOption("Built Road at edge (" + edge.getNodes()[0].getNodeNum() + "-" + edge.getNodes()[1].getNodeNum() + ")", () -> {
                        agent.deductRoadCost();
                        board.placeRoad(edge, new Road(agent, edge));
                        updateLongestRoad();
                    }));
                }
            }
        }

        if (options.isEmpty()) return null;
        BuildOption chosen = options.get(random.nextInt(options.size()));
        chosen.run.run();
        return chosen.action;
    }

    private static class BuildOption {
        final String action;
        final Runnable run;
        BuildOption(String action, Runnable run) { this.action = action; this.run = run; }
    }

	/**
	 *Enforces the Distance Rule: a settlement may only be built if all 3 adjacent   
	 *intersections are vacant
	*/
	private boolean canPlaceSettlement(Agent agent, Node node){
        if (board.getBuildingAtNode(node) != null) return false;
        for (Node neighbor : node.getAdjacentNodes()) {
            if (board.getBuildingAtNode(neighbor) != null) return false;
        }
        return true;
    }

	/** Road can be placed if edge is free and adjacent to agent's settlement/city or to agent's existing road (R1.6). */
	private boolean canPlaceRoad(Agent agent, Edge edge) {
		if (board.getRoadAtEdge(edge) != null) return false;
		for (Node node : edge.getNodes()) {
			Building b = board.getBuildingAtNode(node);
			if (b != null && b.getAgent().equals(agent)) return true;
			for (Edge other : edgesAtNode(node)) {
				Road r = board.getRoadAtEdge(other);
				if (r != null && r.getAgent().equals(agent)) return true;
			}
		}
		return false;
	}

	private List<Edge> edgesAtNode(Node node) {
		List<Edge> out = new ArrayList<>();
		for (Edge e : board.getEdges()) {
			for (Node n : e.getNodes()) if (n == node) { out.add(e); break; }
		}
		return out;
	}

	/**
     * Enforces the upgrade rule: cities must replace existing settlements
     */
	private boolean canPlaceCity(Agent agent, Node node) {
        Building b = board.getBuildingAtNode(node);
        return b instanceof Settlement && b.getAgent().equals(agent);
    }

	/**
     * Implements the discard rule for a 7 roll: every player with more than 7 
     * cards must discard half (rounded down)
     */
    /** Longest Road: 5+ connected road segments award 2 VPs. Recompute after each road build; only change VPs when holder changes. */
    private void updateLongestRoad() {
        int requiredLength = 5;
        Agent bestAgent = null;
        int bestLength = requiredLength - 1;
        for (Agent a : agents) {
            int len = computeLongestRoadLength(a);
            if (len >= requiredLength && len > bestLength) {
                bestLength = len;
                bestAgent = a;
            }
        }
        if (bestAgent == agentWithLongestRoad) return; // no change
        if (agentWithLongestRoad != null) {
            agentWithLongestRoad.addVictoryPoints(-2);
            System.out.println("  -> " + agentWithLongestRoad.getName() + " loses Longest Road (-2 VPs)");
        }
        agentWithLongestRoad = bestAgent;
        if (bestAgent != null) {
            bestAgent.addVictoryPoints(2);
            System.out.println("  -> " + bestAgent.getName() + " gains Longest Road (+2 VPs)");
        }
    }

    /** Longest path (in edges) in the graph of this agent's roads. */
    private int computeLongestRoadLength(Agent agent) {
        Map<Node, List<Node>> adj = new HashMap<>();
        Set<Edge> agentRoads = new HashSet<>();
        for (Map.Entry<Edge, Road> e : board.getEdgeRoads().entrySet()) {
            if (e.getValue().getAgent().equals(agent)) {
                agentRoads.add(e.getKey());
                Node a = e.getKey().getNodes()[0], b = e.getKey().getNodes()[1];
                adj.computeIfAbsent(a, k -> new ArrayList<>()).add(b);
                adj.computeIfAbsent(b, k -> new ArrayList<>()).add(a);
            }
        }
        if (agentRoads.isEmpty()) return 0;
        int maxPath = 0;
        for (Node start : adj.keySet()) {
            Set<Node> visited = new HashSet<>();
            int len = longestPathFrom(start, adj, visited);
            if (len > maxPath) maxPath = len;
        }
        return maxPath;
    }

    private int longestPathFrom(Node node, Map<Node, List<Node>> adj, Set<Node> visited) {
        visited.add(node);
        int maxChild = 0;
        for (Node next : adj.getOrDefault(node, new ArrayList<>())) {
            if (!visited.contains(next)) {
                int d = 1 + longestPathFrom(next, adj, visited);
                if (d > maxChild) maxChild = d;
            }
        }
        visited.remove(node);
        return maxChild;
    }

    /** When hand > 7 and no build is possible, discard randomly until hand size is 7 (so mix can change over time). */
    private void discardDownToSeven(Agent agent) {
        while (agent.getHandSize() > 7) {
            Resources r = agent.getRandomResourceFromHand();
            if (r != null) agent.removeResource(r);
        }
    }

    private void handleSevenRoll() {
        for (Agent a : agents) {
            int handSize = a.getHandSize();
            if (handSize > 7) {
                int discardCount = handSize / 2; 
                for (int i = 0; i < discardCount; i++) {
                    Resources r = a.getRandomResourceFromHand();
                    a.removeResource(r);
                }
            }
        }
    }

	/** Outputs current victory points at end of each round (single line so progression is clear). */
    private void printRoundSummary() {
        System.out.println("--- End of Round " + roundNumber + " --- VPs: "
            + agents.get(0).getName() + " " + agents.get(0).getVictoryPoints() + ", "
            + agents.get(1).getName() + " " + agents.get(1).getVictoryPoints() + ", "
            + agents.get(2).getName() + " " + agents.get(2).getVictoryPoints() + ", "
            + agents.get(3).getName() + " " + agents.get(3).getVictoryPoints());
    }

	/**
     * Returns the total number of turns elapsed.
     * @return current turnNumber
     */
	public int getTurnNumber() {
        return this.turnNumber;
    }

}//ends class GamePlay
