/**
 * Abstract base class for all buildings placed on the Catan board.
 * Implements the Location interface (shared with Road).
 *
 * Subclasses: Settlement, City.
 */
public abstract class Building implements Location {

    /** The agent who owns this building. */
    private Agent owner;

    /** The node on which this building is placed. */
    private Node location;

    /**
     * Constructor for Building.
     * @param owner    the Agent who owns this building
     * @param location the Node where this building is placed
     */
    public Building(Agent owner, Node location) {
        this.owner    = owner;
        this.location = location;
    }

    /**
     * Returns the agent who owns this building.
     * @return owning Agent
     */
    public Agent getAgent() {
        return owner;
    }

    /**
     * Returns the node location of this building (implements Location).
     * @return the Node this building occupies
     */
    @Override
    public Object getLocation() {
        return location;
    }

    /**
     * Abstract: validates and processes resource payment to build this structure.
     * @param resource resources offered
     * @return true if cost is satisfied
     */
    public abstract boolean resourcePayment(Resources[] resource);
}