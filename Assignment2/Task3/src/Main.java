/**
 * Demonstrator entry point for the Catan Simulator (R2.6).
 *
 * Builds the board, creates one human and three computer agents,
 * and runs a single demonstrative simulation.
 */
public class Main {

    public static void main(String[] args) {

        int maxRounds = 100;
        System.out.println("Catan Simulator starting. Max rounds: " + maxRounds);
        System.out.println("Game ends when a player reaches 10 VP or rounds are exhausted.\n");

        // --- Build board (hard-wired layout per spec) ---
        GameMap map = new GameMap();

        // --- Create 1 human + 3 computer agents ---
        HumanCommandParser parser = new HumanCommandParser();
        Agent[] agents = new Agent[]{
                new HumanAgent(0, 0, parser),
                new ComputerAgent(1, 0),
                new ComputerAgent(2, 0),
                new ComputerAgent(3, 0)
        };

        // --- Run game ---
        Game game = new Game(map, agents, maxRounds);
        game.run();
    }
}