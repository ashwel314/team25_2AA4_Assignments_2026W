import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Top-level game controller for the Catan simulator.
 *
 * Updated for Assignment 2 to support:
 *  - Human-in-the-loop gameplay via CLI (R2.1)
 *  - JSON state export for the visualizer (R2.2, R2.3)
 *  - Step-forward "go" command between turns (R2.4)
 *  - Robber mechanics on roll 7 (R2.5)
 *
 * Output format per spec: [RoundNumber] / [PlayerID]: [Action]
 */
public class Game {

    // ---------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------
    public static final int WIN_VP       = 10;
    public static final int MAX_ROUNDS   = 8192;
    public static final int NUM_AGENTS   = 4;

    // ---------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------
    private GameMap  map;
    private Agent[]  agents;
    private int      round;
    private int      maxRounds;
    private MultiDice dice;
    private Robber robber;

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    /**
     * Creates a new Game instance.
     * @param map       the fully initialised game map
     * @param agents    the 4 agents (at least one may be human)
     * @param maxRounds maximum rounds to simulate (from config, R1.4)
     */
    public Game(GameMap map, Agent[] agents, int maxRounds) {
        this.map       = map;
        this.agents    = agents;
        this.round     = 0;
        this.maxRounds = Math.min(maxRounds, MAX_ROUNDS);
        // Robber starts on the desert tile (id 16 in this layout)
        this.robber = new Robber(map.getAllTiles()[16]);

        // Compose two 6-sided dice using the composition pattern
        this.dice = new MultiDice();
        this.dice.addDice(new RegularDice(6));
        this.dice.addDice(new RegularDice(6));
    }

    // ---------------------------------------------------------------
    // Game flow
    // ---------------------------------------------------------------

    /**
     * Runs the initial placement phase.
     * Supports both AI and Human placement.
     */
    public void initialRound() {
        System.out.println("=== INITIAL PLACEMENT ===");
        // Forward pass
        for (Agent agent : agents) {
            handlePlacement(agent);
        }
        // Reverse pass (snake draft)
        for (int i = agents.length - 1; i >= 0; i--) {
            handlePlacement(agents[i]);
        }
        System.out.println("=== GAME START ===");
    }

    private void handlePlacement(Agent agent) {
        if (agent.isComputer()) {
            String action = agent.initialPlacement(map);
            System.out.println("SETUP / " + agent.getId() + ": " + action);
        } else {
            ((HumanAgent) agent).handleInitialPlacement(map);
        }
        GameStateExporter.export(map, agents, "game_state.json");
    }

    /**
     * Runs a single round: all agents take one turn each.
     * Dice are rolled once per agent turn; resources distributed before action.
     * Per R1.7, each agent action is printed as: [RoundNumber] / [PlayerID]: [Action]
     * Victory points are printed once at the end of the round.
     */
    public void runRound() {
        round++;
        Random random = new Random();
        for (Agent agent : agents) {
            // Step-forward between turns so the human can follow (R2.4)
            if (agent.isComputer()) {
                System.out.println("\n[PAUSED] Type 'go' to see Agent " + agent.getId() + "'s turn.");
                waitForGoCommand();
            }

            int roll = dice.roll();

            if (roll == 7) {
                // Half-hand discard for all players with >7 cards
                for (Agent a : agents) {
                    if (a.isSevenCards()) {
                        a.halfHand();
                    }
                }

                // Move robber to a random tile
                Tile[] tiles = map.getAllTiles();
                Tile newRobberLocation = tiles[random.nextInt(tiles.length)];
                robber.moveRobber(newRobberLocation);

                // Collect potential victims (players with a city/settlement adjacent)
                int[] nodesOnRobberTile = map.getNodesForTile(newRobberLocation.getId());
                List<Agent> potentialVictims = new ArrayList<>();
                for (Agent a : agents) {
                    for (int node : nodesOnRobberTile) {
                        if (map.isSettlement(a, node) || map.isCity(a, node)) {
                            potentialVictims.add(a);
                            break;
                        }
                    }
                }

                if (!potentialVictims.isEmpty()) {
                    Agent victim = potentialVictims.get(random.nextInt(potentialVictims.size()));
                    robber.stealResource(agent, victim);
                }
            } else {
                // Distribute resources, respecting the robber block (R2.5)
                map.distributeResources(roll, agents, robber);
            }

            String action;
            if (agent.isComputer()) {
                action = agent.takeTurn(map, round);
                System.out.println(round + " / " + agent.getId() + ": " + action);
            } else {
                ((HumanAgent) agent).handleTurn(map, round, roll);
                action = "Human turn completed.";
            }

            GameStateExporter.export(map, agents, "game_state.json");
        }

        // Print VP summary once at end of round (R1.7)
        printVictoryPoints();
    }

    /**
     * Blocks execution until the user types "go" (R2.4).
     */
    private void waitForGoCommand() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("go")) {
                break;
            }
            System.out.println("Invalid command. Type 'go' to step forward.");
        }
    }

    /**
     * Returns true if the game should terminate (R1.4, R1.5):
     * any agent has reached WIN_VP, or maxRounds has been reached.
     */
    public boolean gameOver() {
        if (round >= maxRounds) return true;
        for (Agent agent : agents) {
            if (agent.getTotalPoints() >= WIN_VP) return true;
        }
        return false;
    }

    /**
     * Prints victory points for all agents at the end of each round (R1.7).
     * Format: End of Round [N] - VP: Player 0: X | Player 1: X | Player 2: X | Player 3: X
     */
    private void printVictoryPoints() {
        StringBuilder sb = new StringBuilder("End of Round " + round + " - VP: ");
        for (int i = 0; i < agents.length; i++) {
            sb.append("Player ").append(agents[i].getId())
                    .append(": ").append(agents[i].getTotalPoints());
            if (i < agents.length - 1) sb.append(" | ");
        }
        System.out.println(sb.toString().trim());
    }

    /**
     * Runs the complete game: initial placement then rounds until termination.
     */
    public void run() {
        initialRound();
        while (!gameOver()) {
            runRound();
        }
        stats();
    }

    /**
     * Prints final game statistics including winner and round count (R1.7).
     */
    public void stats() {
        System.out.println("\n=== GAME OVER after " + round + " round(s) ===");
        Agent winner = agents[0];
        for (Agent a : agents) {
            if (a.getTotalPoints() > winner.getTotalPoints()) winner = a; // add the tie condition as well 
            System.out.println("  Agent " + a.getId() + ": " + a.getTotalPoints() + " VP");
        }
        System.out.println("Winner: Agent " + winner.getId()
                + " with " + winner.getTotalPoints() + " VP");
    }

    // ---------------------------------------------------------------
    // Config file reader (R1.4)
    // ---------------------------------------------------------------

    /**
     * Reads the number of turns from the configuration file.
     * Config format: "turns: int [1-8192]"
     * @param configPath path to the config file
     * @return number of turns, or MAX_ROUNDS if file cannot be read
     */
    public static int readConfig(String configPath) {
        try (BufferedReader br = new BufferedReader(new FileReader(configPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("turns:")) {
                    int val = Integer.parseInt(line.split(":")[1].trim());
                    return Math.max(1, Math.min(val, MAX_ROUNDS));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Config read error: " + e.getMessage() + " — using default " + MAX_ROUNDS);
        }
        return MAX_ROUNDS;
    }

    // ---------------------------------------------------------------
    // Accessors
    // ---------------------------------------------------------------

    public int     getRound()     { return round; }
    public GameMap getMap()       { return map; }
    public Agent[] getAgents()    { return agents; }
}