package Assignment1;

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
        Resources r = victim.getRandomResourceFromHand(); //
        stealer.addResource(r);
        System.out.println(stealer.getName() + " stole from " + victim.getName());
    }

    public boolean blockResource(Tile tile){
        return this.location == tile;
    }
}
