/**
 * Represents a terrain tile on the Catan board.
 *
 * Tile IDs follow the spiral scheme from the assignment spec:
 *   Tile  0       : center
 *   Tiles 1 - 6  : inner ring, clockwise from bottom-right
 *   Tiles 7 - 18 : outer ring, clockwise from bottom-right
 */
public class Tile {

    /** The resource type produced by this tile. */
    private Resources resourceType;

    /** Unique tile ID (0 = center, 1-6 = inner ring, 7-18 = outer ring). */
    private int id;

    /** Dice roll number that activates this tile. 0 for DESERT (never activates). */
    private int numToken;

    /**
     * Constructor for Tile.
     * @param id           unique tile ID (0–18)
     * @param resourceType the resource this tile produces
     * @param numToken     the activation dice roll value (0 for DESERT)
     */
    public Tile(int id, Resources resourceType, int numToken) {
        this.id = id;
        this.resourceType = resourceType;
        this.numToken = numToken;
    }

    /**
     * Returns the resource type produced by this tile.
     * @return the Resources enum value
     */
    public Resources getResourceType() {
        return resourceType;
    }

    /**
     * Returns the dice roll token that activates this tile.
     * @return number token (0 for DESERT)
     */
    public int getNumberToken() {
        return numToken;
    }

    /** Returns this tile's unique ID. */
    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Tile[" + id + " " + resourceType + " roll=" + numToken + "]";
    }
}