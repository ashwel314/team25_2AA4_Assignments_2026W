/**
 * Main entry point for the Catan Simulator.
 *
 * Reads turn count from config.txt, builds the board,
 * creates 4 agents, and runs the full simulation.
 *
 * Output format: [RoundNumber] / [PlayerID]: [Action]
 * Victory points are printed at the end of every round.
 */
public class Main {

    public static void main(String[] args) {
        // --- Setup Map ---
        GameMap map = new GameMap();

        // --- Create Agents (R2.1) ---
        //One human player (Agent 0) and three computer agents (Agents 1-3)
        Agent[] agents = new Agent[]{
                new Agent(0, 0, false), //Human Player
                new Agent(1, 0, true), //Computer
                new Agent(2, 0, true), //Computer
                new Agent(3, 0, true) //Computer
        };

        // --- Configuration and Execution ---
        int maxRounds = Game.readConfig("config.txt");
        Game game = new Game(map, agents, maxRounds);
        System.out.println("Starting SFWRENG 2AA4 Catan Simulator");
        System.out.println("Agent 0 id the Human Player. Use 'roll', 'build', and 'go' commands.");
        
        game.run();
    }
}