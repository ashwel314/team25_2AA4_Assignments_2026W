import java.util.HashMap;
import java.util.Map;

/**
 * Abstract base class representing a generic player int he Catan simulator.
 * Fulfills the "Information Expert" pattern by managing resources and points.
 * Addressess Assignment 1 Feedback: Replaces boolean flags with OO Inheritance.
 */
public abstract class Agent {

    protected int id;
    protected int totalPoints;
    protected Map<Resources, Integer> resources;

    /**
     * Constructor for the Agent base class.
     * @param id      Unique identifier for the agent (0-3).
     * @param points  Initial victory points (usually 0). 
    */
    public Agent(int id, int points){
        this.id = id;
        this.totalPoints = points;
        this.resources = new HashMap<>();

        //Initialize hand with 0 for all resource type
        for (Resources r : Resources.values()){
            if(r != Resources.DESERT){
                resources.put(r, 0);
            }
        }
    }//ends constructor

    /**
     * Requirement R2.1: Abstract method for executing a turn.
     * Overriden by subclasses to provide either AI or Human Logic.
     * @param map     The game board for more validation.
     * @param round   The current round number for logging.
     * @return String description of the action taken   
    */
    public abstract String takeTurn(GameMap map, int round);

    /**
     * Requirement R1.3: Shared resource management logic.
     * @param res   The resource type to add.
     * @param qty   The amount to add.
    */
    public void addResource(Resources res, int qty){
        if (res != Resources.DESERT){
            resources.merge(res, qty, Integer::sum);
        } 
    }

    /**
     * Requirements R1.8: Checks total cards in hand.
     * @return total count of all resource cards.
    */
    public int handSize(){
        return resources.values().stream().mapToInt(Integer::intValue).sum();
    }

    //Accessors
    public int getId() { return id; }
    public int getTotalPoints(){ return totalPoints; }
    public Map<Resources, Integer> getResourceMap() { return resources; }

    /**
     * Requirements R2.4: Helper to identify computer agents for step-forward.
     * @return true if subclass is ComputerAgent
    */
    public boolean isComputer() {
        return this instanceof ComputerAgent;
    }

    //Placeholder for intitial placement logic
    public abstract String initialPlacement(GameMap map);
    
}//ends class Agent