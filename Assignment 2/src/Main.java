/**
 * Main entry point for the Catan Simulator.
 */
public class Main {

    public static void main(String[] args) {
        // --- Setup Map ---
        GameMap map = new GameMap();
        HumanCommandParser parser = new HumanCommandParser();

        // --- Create Agents (R2.1) ---
        //One human player (Agent 0) and three computer agents (Agents 1-3)
        Agent[] agents = new Agent[]{
                new HumanAgent(0, 0, parser), //Human Player
                new ComputerAgent(1, 0), //Computer
                new ComputerAgent(2, 0), //Computer
                new ComputerAgent(3, 0) //Computer
        };
        
        Game game = new Game(map, agents, 100);
        game.run();
    }
}