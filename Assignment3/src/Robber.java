/**
 * Represents the Robber piece and its simplified behaviour (R2.5).
 */
public class Robber {

    private Tile location;

    public Robber(Tile initialTile) {
        this.location = initialTile;
    }

    public Tile getLocation() {
        return location;
    }

    public void moveRobber(Tile tile) {
        this.location = tile;
    }

    /**
     * Steals one random resource card from the victim (if any) and gives it
     * to the stealing agent.
     */
    public void stealResource(Agent stealer, Agent victim) {
        Resources r = victim.getRandomResource();
        if (r != null) {
            victim.removeResource(r, 1);
            stealer.addResource(r, 1);
        }
    }

    /**
     * Returns true if this tile is currently blocked by the robber.
     */
    public boolean blockResource(Tile tile) {
        return this.location == tile;
    }
}

