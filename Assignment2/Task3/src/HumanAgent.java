/**
 * Concrete implementation for a human-controlled player.
 * Uses a shared {@link HumanCommandParser} to read commands from stdin (R2.1).
 */
public class HumanAgent extends Agent {

    private final HumanCommandParser parser;

    public HumanAgent(int id, int points, HumanCommandParser parser) {
        super(id, points);
        this.parser = parser;
    }

    @Override
    public String takeTurn(GameMap map, int round) {
        parser.handleTurn(this, map);
        return "Human turn completed.";
    }

    @Override
    public String initialPlacement(GameMap map) {
        System.out.println("SETUP / " + id + ": Human turn for initial placement.");
        parser.handleTurn(this, map);
        return "Manual setup finished.";
    }
}

