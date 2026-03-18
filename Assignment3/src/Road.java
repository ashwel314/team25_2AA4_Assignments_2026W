/**
 * Represents a Road placed on an edge of the Catan board.
 * Implements the Location interface (shared with Building).
 *
 * Build cost: 1 BRICK + 1 WOOD.
 * Roads must be connected to an existing road or settlement (R1.6).
 */
public class Road implements Location {

    /** The agent who owns this road. */
    private Agent owner;

    /** The edge on which this road is placed. */
    private Edge edge;

    /**
     * Constructor for Road.
     * @param owner the Agent who owns this road
     * @param edge  the Edge where this road is placed
     */
    public Road(Agent owner, Edge edge) {
        this.owner = owner;
        this.edge  = edge;
    }

    /**
     * Returns the edge location of this road (implements Location).
     * @return the Edge this road occupies
     */
    @Override
    public Object getLocation() {
        return edge;
    }

    /**
     * Returns the agent who owns this road.
     * @return owning Agent
     */
    public Agent getOwner() {
        return owner;
    }

    /**
     * Returns the edge ID of this road's placement.
     * @return edge ID
     */
    public int getEdgeId() {
        return edge.getId();
    }

    /**
     * Returns whether this road has been placed on the board.
     * @return true (a Road object always represents a placed road)
     */
    public boolean isPlaced() {
        return true;
    }

    /**
     * Validates that the given resources satisfy the road build cost.
     * Cost: 1 BRICK, 1 WOOD.
     * @param resources resources offered for payment
     * @return true if cost is met
     */
    public boolean resourcePayment(Resources[] resources) {
        int brick = 0, wood = 0;
        for (Resources r : resources) {
            switch (r) {
                case BRICK: brick++; break;
                case WOOD:  wood++;  break;
                default: break;
            }
        }
        return brick >= 1 && wood >= 1;
    }

    @Override
    public String toString() {
        return "Road[owner=" + owner.getId() + " edge=" + edge.getId() + "]";
    }
}