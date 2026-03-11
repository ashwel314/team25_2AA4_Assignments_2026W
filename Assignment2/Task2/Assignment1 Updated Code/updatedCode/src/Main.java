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

        // --- Read config (turns: int [1-8192]) ---
        String configPath = "config.txt";
        int maxRounds = 100;
        System.out.println("Catan Simulator starting. Max rounds: " + maxRounds);
        System.out.println("Game ends when a player reaches 10 VP or rounds are exhausted.\n");

        // --- Build board (hard-wired layout per spec) ---
        GameMap map = new GameMap();

        // --- Create 4 randomly-acting agents ---
        Agent[] agents = new Agent[]{
                new Agent(0, 0),
                new Agent(1, 0),
                new Agent(2, 0),
                new Agent(3, 0)
        };

        // --- Run game ---
        Game game = new Game(map, agents, maxRounds);
        game.run();
    }
}