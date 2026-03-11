import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Represents a player (agent) in the Catan simulator.
 *
 * Agents act randomly per R1.2. On each turn they:
 *   1. Collect resources from the dice roll (handled by GameMap).
 *   2. If holding more than 7 cards, try to build something (R1.8).
 *   3. Otherwise, attempt to build randomly (road → settlement → city).
 *
 * Building costs (standard Catan rules, R1.3):
 *   Road:       1 BRICK + 1 WOOD
 *   Settlement: 1 BRICK + 1 WOOD + 1 SHEEP + 1 WHEAT
 *   City:       2 WHEAT + 3 ORE
 */
public class Agent {

    // ---------------------------------------------------------------
    // Constants — building costs
    // ---------------------------------------------------------------
    private static final Map<Resources, Integer> ROAD_COST       = new HashMap<>();
    private static final Map<Resources, Integer> SETTLEMENT_COST = new HashMap<>();
    private static final Map<Resources, Integer> CITY_COST       = new HashMap<>();

    static {
        ROAD_COST.put(Resources.BRICK, 1);
        ROAD_COST.put(Resources.WOOD,  1);

        SETTLEMENT_COST.put(Resources.BRICK, 1);
        SETTLEMENT_COST.put(Resources.WOOD,  1);
        SETTLEMENT_COST.put(Resources.SHEEP, 1);
        SETTLEMENT_COST.put(Resources.WHEAT, 1);

        CITY_COST.put(Resources.WHEAT, 2);
        CITY_COST.put(Resources.ORE,   3);
    }

    // ---------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------

    /** Unique agent ID. */
    private int id;

    /** Total victory points. */
    private int totalPoints;

    /** Resource hand: maps each resource type to the count held. */
    private Map<Resources, Integer> resources;

    /** Roads remaining to be placed (max 15 per standard rules). */
    private int roadsRemaining;

    /** Settlements remaining to be placed (max 5). */
    private int settlementsRemaining;

    /** Cities remaining to be placed (max 4). */
    private int citiesRemaining;

    /** Shared random number generator for random agent behaviour (R1.2). */
    private Random random;

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    /**
     * Constructor for Agent.
     * @param id     unique agent ID
     * @param points starting victory points (normally 0)
     */
    public Agent(int id, int points) {
        this.id                 = id;
        this.totalPoints        = points;
        this.roadsRemaining     = 15;
        this.settlementsRemaining = 5;
        this.citiesRemaining    = 4;
        this.random             = new Random();
        this.resources          = new HashMap<>();
        for (Resources r : Resources.values()) {
            if (r != Resources.DESERT) resources.put(r, 0);
        }
    }

    // ---------------------------------------------------------------
    // Turn logic
    // ---------------------------------------------------------------

    /**
     * Executes this agent's turn on the given map.
     *
     * Per R1.8: if hand size > 7, the agent must attempt to build.
     * Otherwise the agent tries to build something randomly (R1.2).
     *
     * @param map   the game map
     * @param round current round number (for console output)
     * @return a human-readable description of the action taken (R1.7)
     */
    public String takeTurn(GameMap map, int round) {
        boolean forced = isSevenCards();

        // Collect all currently affordable actions
        List<String> actions = new ArrayList<>();
        if (checkRoadCost()       && !map.validRoadEdges(this).isEmpty())           actions.add("road");
        if (checkSettlementCost() && !map.validSettlementNodes(this, false).isEmpty()) actions.add("settlement");
        if (checkCityCost()       && !map.validCityNodes(this).isEmpty())           actions.add("city");

        if (actions.isEmpty()) {
            return "No action taken (insufficient resources or no valid placements)";
        }

        // Pick randomly among valid actions (R1.2; R1.8 — forced but same logic)
        String chosen = actions.get(random.nextInt(actions.size()));

        switch (chosen) {
            case "road":       return buildRoad(map)       ? "Built a road"       : "Road build failed";
            case "settlement": return buildSettlement(map) ? "Built a settlement" : "Settlement build failed";
            case "city":       return buildCity(map)       ? "Built a city"       : "City build failed";
            default:           return "No action taken";
        }
    }

    /**
     * Attempts to place a road at a randomly chosen valid edge.
     * @param map the game map
     * @return action description
     */
    public boolean buildRoad(GameMap map) {
        List<Integer> valid = map.validRoadEdges(this);
        if (valid.isEmpty() || !checkRoadCost()) return false;
        int edgeId = valid.get(random.nextInt(valid.size()));
        payResources(ROAD_COST);
        map.placeRoad(this, edgeId);
        roadsRemaining--;
        return true;
    }

    // private helper that returns a String for logging
    private String buildRoad(GameMap map, boolean log) {
        List<Integer> valid = map.validRoadEdges(this);
        if (valid.isEmpty() || !checkRoadCost()) return "Road build failed";
        int edgeId = valid.get(random.nextInt(valid.size()));
        payResources(ROAD_COST);
        map.placeRoad(this, edgeId);
        roadsRemaining--;
        return "Built road on edge " + edgeId;
    }

    /**
     * Attempts to place a settlement at a randomly chosen valid node.
     * @param map the game map
     * @return true if successful
     */
    public boolean buildSettlement(GameMap map) {
        List<Integer> valid = map.validSettlementNodes(this, false);
        if (valid.isEmpty() || !checkSettlementCost()) return false;
        int nodeId = valid.get(random.nextInt(valid.size()));
        payResources(SETTLEMENT_COST);
        map.placeSettlement(this, nodeId);
        settlementsRemaining--;
        addPoints(1);
        return true;
    }

    /**
     * Attempts to upgrade a randomly chosen settlement to a city.
     * @param map the game map
     * @return true if successful
     */
    public boolean buildCity(GameMap map) {
        List<Integer> valid = map.validCityNodes(this);
        if (valid.isEmpty() || !checkCityCost()) return false;
        int nodeId = valid.get(random.nextInt(valid.size()));
        payResources(CITY_COST);
        map.placeCity(this, nodeId);
        citiesRemaining--;
        settlementsRemaining++; // settlement piece returned
        addPoints(1); // city gives +1 (net, since settlement already gave 1)
        return true;
    }

    // ---------------------------------------------------------------
    // Initial placement (R1.1 / setup phase)
    // ---------------------------------------------------------------

    /**
     * Places one settlement and one road during the initial setup phase.
     * No resource cost during initial placement.
     * @param map the game map
     * @return action description
     */
    public String initialPlacement(GameMap map) {
        List<Integer> validNodes = map.validSettlementNodes(this, true);
        if (validNodes.isEmpty()) return "No valid initial settlement position found";

        int nodeId = validNodes.get(random.nextInt(validNodes.size()));
        map.placeSettlement(this, nodeId);
        settlementsRemaining--;
        addPoints(1);

        // Place adjacent road
        List<Integer> adjEdges = new ArrayList<>();
        for (int e : map.getEdgesForNode(nodeId)) {
            if (!map.getEdge(e).isOccupied()) adjEdges.add(e);
        }
        String roadDesc = "";
        if (!adjEdges.isEmpty()) {
            int edgeId = adjEdges.get(random.nextInt(adjEdges.size()));
            map.placeRoad(this, edgeId);
            roadsRemaining--;
            roadDesc = " and road on edge " + edgeId;
        }
        return "Placed settlement on node " + nodeId + roadDesc;
    }

    // ---------------------------------------------------------------
    // Resource management
    // ---------------------------------------------------------------

    /**
     * Adds the given amount of a resource to this agent's hand.
     * @param resource the resource type
     * @param amount   how many to add
     */
    public void addResource(Resources resource, int amount) {
        if (resource == Resources.DESERT) return;
        resources.merge(resource, amount, Integer::sum);
    }

    /**
     * Removes the given amount of a resource from this agent's hand.
     * @param resource the resource type
     * @param amount   how many to remove
     */
    public void removeResource(Resources resource, int amount) {
        resources.merge(resource, -amount, Integer::sum);
    }

    /**
     * Deducts a cost map from this agent's resource hand.
     * @param cost map of resource → quantity required
     */
    private void payResources(Map<Resources, Integer> cost) {
        for (Map.Entry<Resources, Integer> entry : cost.entrySet()) {
            removeResource(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Returns the total number of resource cards in this agent's hand.
     * @return hand size
     */
    public int handSize() {
        return resources.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Returns true if the agent holds more than 7 resource cards (R1.8).
     */
    public boolean isSevenCards() {
        return handSize() > 7;
    }

    /**
     * Returns true if this agent can afford a road (1 BRICK + 1 WOOD).
     */
    public boolean checkRoadCost() {
        return canAfford(ROAD_COST);
    }

    /**
     * Returns true if this agent can afford a settlement.
     */
    public boolean checkSettlementCost() {
        return canAfford(SETTLEMENT_COST);
    }

    /**
     * Returns true if this agent can afford a city.
     */
    public boolean checkCityCost() {
        return canAfford(CITY_COST);
    }

    private boolean canAfford(Map<Resources, Integer> cost) {
        for (Map.Entry<Resources, Integer> entry : cost.entrySet()) {
            if (resources.getOrDefault(entry.getKey(), 0) < entry.getValue()) return false;
        }
        return true;
    }

    // ---------------------------------------------------------------
    // Points
    // ---------------------------------------------------------------

    /** Adds victory points to this agent's total. */
    public void addPoints(int points) {
        this.totalPoints += points;
    }

    // ---------------------------------------------------------------
    // Accessors
    // ---------------------------------------------------------------

    public int getId()          { return id; }
    public int getTotalPoints() { return totalPoints; }
    public int getRoadsRemaining()       { return roadsRemaining; }
    public int getSettlementsRemaining() { return settlementsRemaining; }
    public int getCitiesRemaining()      { return citiesRemaining; }

    public Map<Resources, Integer> getResourceMap() {
        return resources;
    }

    @Override
    public String toString() {
        return "Agent[" + id + " VP=" + totalPoints + " hand=" + handSize() + "]";
    }
}