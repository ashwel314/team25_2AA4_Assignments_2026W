/**
 * Represents a node (corner intersection between tiles) on the Catan board.
 *
 * There are 54 nodes total (IDs 0–53).
 * Node IDs follow the same spiral scheme as tiles: starting from center
 * outward. The numbering may appear non-sequential spatially — this is
 * intentional per the assignment spec ("comes from a known implementation").
 *
 * A node can hold at most one Building (Settlement or City).
 */
public class Node {

    /** Unique identifier for this node (0–53). */
    private int id;

    /** The building on this node, or null if empty (multiplicity 0..1). */
    private Building building;

    /**
     * Constructor for Node.
     * @param id unique node ID (0–53)
     */
    public Node(int id) {
        this.id = id;
        this.building = null;
    }

    /**
     * Returns this node's unique ID.
     * @return node ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the building on this node, or null if unoccupied.
     * @return Building or null
     */
    public Building getBuilding() {
        return building;
    }

    /**
     * Places a building on this node.
     * @param b the Building to place (Settlement or City)
     */
    public void setBuilding(Building b) {
        this.building = b;
    }

    /**
     * Returns whether this node has a building on it.
     * @return true if occupied
     */
    public boolean isOccupied() {
        return building != null;
    }

    @Override
    public String toString() {
        return "Node[" + id + (isOccupied() ? " OCCUPIED" : "") + "]";
    }
}