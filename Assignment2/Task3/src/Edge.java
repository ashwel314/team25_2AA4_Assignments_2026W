/**
 * Represents an edge (path segment between two adjacent nodes) on the Catan board.
 *
 * There are 72 edges total (IDs 0–71).
 * Each edge connects exactly two nodes and can hold at most one Road.
 */
public class Edge {

    /** Unique identifier for this edge (0–71). */
    private int id;

    /** The road on this edge, or null if empty (multiplicity 0..1). */
    private Road road;

    /**
     * Constructor for Edge.
     * @param id unique edge ID (0–71)
     */
    public Edge(int id) {
        this.id = id;
        this.road = null;
    }

    /**
     * Returns this edge's unique ID.
     * @return edge ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the road on this edge, or null if unoccupied.
     * @return Road or null
     */
    public Road getRoad() {
        return road;
    }

    /**
     * Places a road on this edge.
     * @param road the Road to place
     */
    public void setRoad(Road road) {
        this.road = road;
    }

    /**
     * Returns whether this edge has a road on it.
     * @return true if occupied
     */
    public boolean isOccupied() {
        return road != null;
    }

    @Override
    public String toString() {
        return "Edge[" + id + (isOccupied() ? " OCCUPIED" : "") + "]";
    }
}