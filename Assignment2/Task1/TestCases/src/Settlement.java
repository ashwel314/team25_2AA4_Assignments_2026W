/**
 * Represents a Settlement on the Catan board.
 * Extends the abstract Building class (which implements Location).
 *
 * Victory points: 1 VP when placed.
 * Resource multiplier: 1 (receives 1 resource card per adjacent activated tile).
 * Build cost: 1 BRICK + 1 WOOD + 1 SHEEP + 1 WHEAT.
 */
public class Settlement extends Building {

    /**
     * Resources received per adjacent activated tile (1 for settlement, 2 for city).
     */
    private int resourceMultiplier;

    /**
     * Constructor for Settlement.
     * @param owner    the Agent who owns this settlement
     * @param location the Node where this settlement is placed
     */
    public Settlement(Agent owner, Node location) {
        super(owner, location);
        this.resourceMultiplier = 1;
    }

    /**
     * Validates that the given resources satisfy the settlement build cost.
     * Cost: 1 BRICK, 1 WOOD, 1 SHEEP, 1 WHEAT.
     * @param resources resources offered for payment
     * @return true if cost is met
     */
    @Override
    public boolean resourcePayment(Resources[] resources) {
        int brick = 0, wood = 0, sheep = 0, wheat = 0;
        for (Resources r : resources) {
            switch (r) {
                case BRICK: brick++; break;
                case WOOD:  wood++;  break;
                case SHEEP: sheep++; break;
                case WHEAT: wheat++; break;
                default: break;
            }
        }
        return brick >= 1 && wood >= 1 && sheep >= 1 && wheat >= 1;
    }

    /**
     * Returns the resource multiplier (1 for a settlement).
     * @return 1
     */
    public int getResourceMultiplier() {
        return resourceMultiplier;
    }

    /**
     * Returns the node ID of this settlement's location.
     * @return node ID
     */
    public int getNodeId() {
        return ((Node) getLocation()).getId();
    }

    /**
     * Returns the victory points contributed by this settlement (1 VP).
     * @return 1
     */
    public int getPoints() {
        return 1;
    }


    @Override
    public String toString() {
        return "Settlement[owner=" + getAgent().getId() + " node=" + getNodeId() + "]";
    }
}