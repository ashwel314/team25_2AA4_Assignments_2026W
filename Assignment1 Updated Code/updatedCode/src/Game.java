import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Top-level game controller for the Catan simulator.
 *
 * Manages the full game loop including:
 *   - Initial placement phase (R1.1, R1.6)
 *   - Round-by-round simulation (R1.3, R1.4)
 *   - Termination when 10 VP reached or max rounds elapsed (R1.4, R1.5)
 *   - Console output in the specified format (R1.7)
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
     * Creates a new Game.
     * @param map       the fully initialised game map
     * @param agents    the 4 agents
     * @param maxRounds maximum rounds to simulate (from config, R1.4)
     */
    public Game(GameMap map, Agent[] agents, int maxRounds) {
        this.map       = map;
        this.agents    = agents;
        this.round     = 0;
        this.maxRounds = Math.min(maxRounds, MAX_ROUNDS);
        this.robber = new Robber(map.getAllTiles()[16]); // Robber always starts on the desert tile

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
     * Each agent places one settlement + one road in order, then in reverse
     * order (standard Catan snake draft), per R1.3.
     */
    public void initialRound() {
        System.out.println("=== INITIAL PLACEMENT ===");
        // Forward pass
        for (Agent agent : agents) {
            String action = agent.initialPlacement(map);
            System.out.println("SETUP / " + agent.getId() + ": " + action);
        }
        // Reverse pass (snake draft)
        for (int i = agents.length - 1; i >= 0; i--) {
            String action = agents[i].initialPlacement(map);
            System.out.println("SETUP / " + agents[i].getId() + ": " + action);
        }
        System.out.println("=== GAME START ===");
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
            // 1. Roll dice
            int roll = dice.roll();

            // seven roll logic
            if(roll == 7){
                for(Agent a : agents){
                    if(a.isSevenCards()){
                        a.halfHand();
                    }
                }

                // new robber location
                Tile newRobberLocation = map.getAllTiles()[random.nextInt(19)];
                robber.moveRobber(newRobberLocation);

                // getting the victim for who gets a resource taken from them
                int[] nodesOnRobberTile = map.getNodesForTile(newRobberLocation.getId());
                List<Agent> potentialVictims = new ArrayList<>();

                for(Agent a : agents){ // checking to see who has a city or settlement on the tiles nodes
                    for(int node : nodesOnRobberTile){
                        if(map.isSettlement(a, node) || map.isCity(a, node)){
                            potentialVictims.add(a);
                            break;
                        }
                    }
                }

                // only steals resources if players are on the tile
                if(!(potentialVictims.isEmpty())){
                    Agent victim = potentialVictims.get(random.nextInt(potentialVictims.size()));
                    robber.stealResource(agent, victim);
                }
            } else{
                // 2. Distribute resources to all agents based on roll
                distributeResources(roll);
            }
            // 3. Agent takes their action
            String action = agent.takeTurn(map, round);

            // 4. Print in exact spec format: [RoundNumber] / [PlayerID]: [Action]
            System.out.println(round + " / " + agent.getId() + ": " + action);
        }

        // Print VP summary once at end of round (R1.7)
        printVictoryPoints();
    }

    // ---------------------------------------------------------------
    // Resource distribution
    // ---------------------------------------------------------------

    /**
     * Distributes resources to all agents whose buildings are on tiles
     * matching the given dice roll value (R1.3).
     *
     * Rule: Each settlement adjacent to an activated tile earns 1 of that resource.
     *       Each city adjacent to an activated tile earns 2 of that resource.
     *       Roll of 7 produces no resources (robber rule, simplified per spec).
     *
     * @param diceRoll the result of the two-dice roll
     */
    private void distributeResources(int diceRoll) {
        if (diceRoll == 7) return; // No resources on 7 (simplified robber rule)

        for (Tile tile : map.getAllTiles()) {
            if (tile.getNumberToken() != diceRoll) continue;
            if (tile.getResourceType() == Resources.DESERT) continue;
            if (robber.blockResource(tile)){
                continue;
            }

            Resources res = tile.getResourceType();
            int tileId = tile.getId();

            for (int nodeId : map.getNodesForTile(tileId)) {
                Node node = map.getNode(nodeId);
                if (!node.isOccupied()) continue;

                Building b = node.getBuilding();
                int amount = (b instanceof City) ? 2 : 1;
                b.getAgent().addResource(res, amount);
            }
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