/**
 * Represents a City on the Catan board.
 * Extends the abstract Building class (which implements Location).
 * Cities replace existing settlements.
 *
 * Victory points: 2 VP (net +1 over the replaced settlement).
 * Resource multiplier: 2 (receives 2 resource cards per adjacent activated tile).
 * Build cost: 2 WHEAT + 3 ORE.
 */
public class City extends Building {

    /**
     * Resources received per adjacent activated tile (2 for city).
     */
    private int resourceMultiplier;

    /**
     * Constructor for City.
     * @param owner    the Agent who owns this city
     * @param location the Node where this city is placed (replacing a settlement)
     */
    public City(Agent owner, Node location) {
        super(owner, location);
        this.resourceMultiplier = 2;
    }

    /**
     * Validates that the given resources satisfy the city build cost.
     * Cost: 2 WHEAT, 3 ORE.
     * @param resources resources offered for payment
     * @return true if cost is met
     */
    @Override
    public boolean resourcePayment(Resources[] resources) {
        int wheat = 0, ore = 0;
        for (Resources r : resources) {
            switch (r) {
                case WHEAT: wheat++; break;
                case ORE:   ore++;   break;
                default: break;
            }
        }
        return wheat >= 2 && ore >= 3;
    }

    /**
     * Returns the node ID of this city's location.
     * @return node ID
     */
    public int getNodeId() {
        return ((Node) getLocation()).getId();
    }

    /**
     * Returns the victory points contributed by this city (2 VP).
     * @return 2
     */
    public int getPoints() {
        return 2;
    }

    /**
     * Returns the resource amount produced per adjacent tile activation.
     * @return 2
     */
    public int getResourceAmount() {
        return resourceMultiplier;
    }

    @Override
    public String toString() {
        return "City[owner=" + getAgent().getId() + " node=" + getNodeId() + "]";
    }
}