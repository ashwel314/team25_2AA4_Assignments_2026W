/**
 * Represents a mapping entry used by GameMap to associate
 * a tile or edge ID with a set of node IDs.
 * Used internally by GameMap for the tilesToNodes and edgeToNodes lookups.
 */
public class Map {

    /** The source ID (tile ID or edge ID). */
    private int id;

    /** The associated node IDs. */
    private int[] nodeIds;

    /**
     * Constructor for Map.
     * @param id      the source ID
     * @param nodeIds the associated node IDs
     */
    public Map(int id, int[] nodeIds) {
        this.id = id;
        this.nodeIds = nodeIds;
    }

    /** Returns the source ID of this entry. */
    public int getId() { return id; }

    /** Returns the node IDs associated with this entry. */
    public int[] getNodeIds() { return nodeIds; }
}