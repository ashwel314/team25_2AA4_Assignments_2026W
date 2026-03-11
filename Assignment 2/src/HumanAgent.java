/**
 * Concrete implementation for a Human Player.
 * Fulfills Requireemnt R2.1 by delegating turn logic to a CLI Parser.
*/

public class HumanAgent extends Agent {
    
    private HumanCommandParser parser;

    /**
     * Constructor for HumanAgent.
     * @param id        Unique ID
     * @param points    Starting points
     * @param parser    The shared Regex command parser.
    */
    public HumanAgent(int id, int points, HumanCommandParser parser) {
        super(id, points);
        this.parser = parser;
    }

    /**
     * Requirement R2.1: Executes turn via mnual command line input.
    */
    @Override
    public String takeTurn(GameMap map, int round){
        parser.handleTurn(this, map);
        return "Human turn completed.";
    }
    
    /**
     * Requirements R1.1: Allows manual setup for the human player.
    */
    @Override
    public String initialPlacement(GameMap map){
        System.out.println("SETUP / " + id + ": Human turn for initial placement.");
        parser.handleTurn(this,map);
        return "Manual setup finished.";
    }
    
}//ends class
