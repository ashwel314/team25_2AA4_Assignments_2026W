import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * Top-level game controller for the Catan simulator.
 * Updated for Assignment 2 to support Human-in-the-loop gameplay.
 *
 * Manages the full game loop including:
 *   - Initial placement phase (R1.1, R1.6)
 *   - Round-by-round simulation (R1.3, R1.4)
 *   - Termination when 10 VP reached or max rounds elapsed (R1.4, R1.5)
 *   - Console output in the specified format (R1.7)
 */
public class Game {

    // Constants
    public static final int WIN_VP       = 10;
    public static final int MAX_ROUNDS   = 8192;
    public static final int NUM_AGENTS   = 4;

    // Fields
    private GameMap  map;
    private Agent[]  agents;
    private int      round;
    private int      maxRounds;
    private MultiDice dice;
    private HumanCommandParser humanParser; //Member 3: Added for R2.1

    /**
     * Creates a new Game instance. 
     * @param map       the fully initialised game map
     * @param agents    the 4 agents (at least one should be human)
     * @param maxRounds maximum rounds to simulate (from config, R1.4)
     */
    public Game(GameMap map, Agent[] agents, int maxRounds) {
        this.map       = map;
        this.agents    = agents;
        this.round     = 0;
        this.maxRounds = Math.min(maxRounds, MAX_ROUNDS);
        this.humanParser = new HumanCommandParser(); //Added for R2.1

        // Dice composition pattern (Fixes Assignment 1 Feedback) 
        this.dice = new MultiDice();
        this.dice.addDice(new RegularDice(6));
        this.dice.addDice(new RegularDice(6));
    }


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

    private void handlePlacement(Agent agent){
        if(agent.isComputer()){
            String action = agent.initialPlacement(map);
            System.out.println("SETUP / " + agent.getId() + ": " + action);
        } else{
            //R2.1 Call your parser for the human's setup turn
            humanParser.handleTurn(agent, map);
        }
        //R2.3 Update state after every placement for the visualizer
        GameStateExporter.export(map, agents, "game_state.json");
    }

    /**
     * Runs a single round: all agents take one turn each.
     * Dice are rolled once per agent turn; resources distributed before action.
     * Per R1.7, each agent action is printed as: [RoundNumber] / [PlayerID]: [Action]
     * Victory points are printed once at the end of the round.
     */
    //This method has been updated for Assignment 2
    public void runRound() {
        round++;
        for (Agent agent : agents) {
            //Step-Forward mechanism
            if (agent.isComputer()){
                System.out.println("\n[PAUSED] Type 'go' to see Agent " + agent.getId() + "'s turn.");
                waitForGoCommand();
                
                int roll = dice.roll();
                map.distributeResources(roll, agents);
                String action = agent.takeTurn(map, round);
                System.out.println(round + " / " + agent.getId() + ": " + action);
            }else{
                // R2.1: Human Player Turn
                int roll = dice.roll();
                System.out.println("\n" + round + " / " + agent.getId() + ": Rolled a " + roll);
                map.distributeResources(roll, agents);
                humanParser.handleTurn(agent, map);
            }
            //R2.3: Export state after every individual turn
            GameStateExporter.export(map, agents, "game_state.json");
        }
        printVictoryPoints();
    }

    /**
     * Requirements R2.4: Blocks execution until "go" is enetered.
    */
    private void waitForGoCommand() {
        Scanner sc = new Scanner(System.in);
        while(true){
            String input = sc.nextLine().trim();
            if(input.equalsIgnoreCase("go")){
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
            if (a.getTotalPoints() > winner.getTotalPoints()) winner = a;
            System.out.println("  Agent " + a.getId() + ": " + a.getTotalPoints() + " VP");
        }
        System.out.println("Winner: Agent " + winner.getId() + " with " + winner.getTotalPoints() + " VP");
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

} //ends class Game