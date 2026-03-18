import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Abstract base class for all players (agents) in the Catan simulator.
 *
 * Encapsulates shared resource-hand and scoring logic. Concrete subclasses
 * such as {@link ComputerAgent} and {@link HumanAgent} implement turn-taking
 * and initial placement behaviour.
 */
public abstract class Agent {

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
    protected int id;

    /** Total victory points. */
    protected int totalPoints;

    /** Resource hand: list of resource cards held (each element is one card). */
    protected ArrayList<Resources> hand;

    /** Roads remaining to be placed (max 15 per standard rules). */
    protected int roadsRemaining;

    /** Settlements remaining to be placed (max 5). */
    protected int settlementsRemaining;

    /** Cities remaining to be placed (max 4). */
    protected int citiesRemaining;

    /** Shared random number generator for random behaviour. */
    protected Random random;

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
        this.hand               = new ArrayList<>();
    }

    // ---------------------------------------------------------------
    // Building helpers used by concrete agents
    // ---------------------------------------------------------------

    /** Attempts to place a road at a randomly chosen valid edge. */
    protected boolean buildRoad(GameMap map) {
        List<Integer> valid = map.validRoadEdges(this);
        if (valid.isEmpty() || !checkRoadCost()) return false;
        int edgeId = valid.get(random.nextInt(valid.size()));
        payResources(ROAD_COST);
        map.placeRoad(this, edgeId);
        roadsRemaining--;
        return true;
    }

    /** Attempts to place a settlement at a randomly chosen valid node. */
    protected boolean buildSettlement(GameMap map) {
        List<Integer> valid = map.validSettlementNodes(this, false);
        if (valid.isEmpty() || !checkSettlementCost()) return false;
        int nodeId = valid.get(random.nextInt(valid.size()));
        payResources(SETTLEMENT_COST);
        map.placeSettlement(this, nodeId);
        settlementsRemaining--;
        addPoints(1);
        return true;
    }

    /** Attempts to upgrade a randomly chosen settlement to a city. */
    protected boolean buildCity(GameMap map) {
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
    // Abstract turn and setup API
    // ---------------------------------------------------------------

    public abstract String takeTurn(GameMap map, int round);

    public abstract String initialPlacement(GameMap map);

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
        for (int i = 0; i < amount; i++) {
            hand.add(resource);
        }
    }

    /**
     * Removes the given amount of a resource from this agent's hand.
     * @param resource the resource type
     * @param amount   how many to remove
     */
    public void removeResource(Resources resource, int amount) {
        int removed = 0;
        for (int i = hand.size() - 1; i >= 0 && removed < amount; i--) {
            if (hand.get(i) == resource) {
                hand.remove(i);
                removed++;
            }
        }
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
        return hand.size();
    }

    /**
     * Returns a random resource card from the hand, or {@code null} if the hand is empty.
     */
    public Resources getRandomResource() {
        if (hand.isEmpty()) return null;
        return hand.get(random.nextInt(hand.size()));
    }

    /**
     * Returns true if the agent holds more than 7 resource cards (R1.8).
     */
    public boolean isSevenCards() {
        return handSize() > 7;
    }

    /**
     * Discards half of the hand (rounded down), randomly (R2.5).
     */
    public void halfHand() {
        int discardAmount = handSize() / 2;
        for (int i = 0; i < discardAmount; i++) {
            if (hand.isEmpty()) break;
            int idx = random.nextInt(hand.size());
            hand.remove(idx);
        }
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
            int count = 0;
            for (Resources r : hand) {
                if (r == entry.getKey()) count++;
            }
            if (count < entry.getValue()) return false;
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

    /** Returns a map of resource type to count for display (e.g. list command). */
    public Map<Resources, Integer> getResourceMap() {
        Map<Resources, Integer> map = new HashMap<>();
        for (Resources r : Resources.values()) {
            if (r != Resources.DESERT) map.put(r, 0);
        }
        for (Resources r : hand) {
            if (r != Resources.DESERT) map.merge(r, 1, Integer::sum);
        }
        return map;
    }

    /** Helper used for step-forward logic to distinguish AI from human players. */
    public boolean isComputer() {
        return this instanceof ComputerAgent;
    }

    /** Call when a settlement is placed (e.g. by human via parser) to update VP and piece count. */
    public void recordSettlementPlaced() {
        addPoints(1);
        decrementSettlementsRemaining();
    }

    /** Call when a road is placed (e.g. by human via parser) to update piece count. */
    public void recordRoadPlaced() {
        decrementRoadsRemaining();
    }

    /** Call when a city is placed (upgrade from settlement) to update piece count and VP. */
    public void recordCityPlaced() {
        citiesRemaining--;
        settlementsRemaining++;
        addPoints(1);
    }

    public List<Resources> getHand() {
        return hand;
    }

    public Random getRandom() {
        return random;
    }

    /** Deduct settlement cost from hand (for human build). */
    protected void payForSettlement() {
        payResources(SETTLEMENT_COST);
    }

    /** Deduct road cost from hand (for human build). */
    protected void payForRoad() {
        payResources(ROAD_COST);
    }

    /** Deduct city cost from hand (for human build). */
    protected void payForCity() {
        payResources(CITY_COST);
    }

    protected void decrementRoadsRemaining() {
        roadsRemaining--;
    }

    protected void decrementSettlementsRemaining() {
        settlementsRemaining--;
    }

    protected void decrementCitiesRemaining() {
        citiesRemaining--;
    }

    @Override
    public String toString() {
        return "Agent[" + id + " VP=" + totalPoints + " hand=" + handSize() + "]";
    }
}