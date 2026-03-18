
import java.util.Random;

public class Robber {

    // keeps track of what tile the robber is on
    private Tile location;

    /**
     *
     * @param tile initial tile it's on
     */
    public Robber(Tile tile){
        this.location = tile;
    }

    public Tile getLocation(){
        return this.location;
    }

    public void moveRobber(Tile tile){
        this.location = tile;
    }

    public void stealResource(Agent stealer, Agent victim){
        Random rand = new Random();

        Resources r = victim.getRandomResource();
        if(r != null){
            stealer.addResource(r, 1);
        }
    }

    /**
     * checks to see if the tile is blocked by the robber
     * @param tile the tile to be compared
     * @return true if blocked, false if not blocked
     */
    public boolean blockResource(Tile tile){
        return this.location == tile;
    }
}
