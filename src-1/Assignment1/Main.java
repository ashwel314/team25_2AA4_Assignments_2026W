package Assignment1;

import java.util.ArrayList;
import java.util.List;

/**
 * The Main class serves as the entry point for the Catan simulator.
 * It initializes the game board, agents, and dice, then passes them
 * to the GamePlay controller to run the simulation.
 */
public class Main {
    
    /**
     * Entry point of the application.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // 1. Initialize the Dice
        // Using the Composite Pattern via MultiDice to hold two 6-sided dice.
        MultiDice gameDice = new MultiDice();
        gameDice.addDice(new RegularDice(6));
        gameDice.addDice(new RegularDice(6));

        // 2. Initialize the 4 Agents (Requirement R1.2)
        List<Agent> agents = new ArrayList<>();
        agents.add(new Agent(0, "Agent_Alpha"));
        agents.add(new Agent(0, "Agent_Beta"));
        agents.add(new Agent(0, "Agent_Gamma"));
        agents.add(new Agent(0, "Agent_Delta"));

        // 3. Setup the Board (Requirement R1.1) – 19 tiles, 54 nodes, 72 edges per spec
        Board catanBoard = MapSetup.createBoard();

        // 4. Place one initial settlement per agent (distance ≥ 2 between players)
        // Nodes 0, 15, 28, 43 are pairwise non-adjacent so they satisfy the distance rule.
        catanBoard.placeSettlement(catanBoard.getNode(0), new Settlement(agents.get(0), catanBoard.getNode(0)));
        agents.get(0).addVictoryPoints(1);
        catanBoard.placeSettlement(catanBoard.getNode(15), new Settlement(agents.get(1), catanBoard.getNode(15)));
        agents.get(1).addVictoryPoints(1);
        catanBoard.placeSettlement(catanBoard.getNode(28), new Settlement(agents.get(2), catanBoard.getNode(28)));
        agents.get(2).addVictoryPoints(1);
        catanBoard.placeSettlement(catanBoard.getNode(43), new Settlement(agents.get(3), catanBoard.getNode(43)));
        agents.get(3).addVictoryPoints(1);
        // Give each agent a small starting hand so they can build once they exceed 7 cards (avoids being stuck with only one resource type)
        for (Agent a : agents) {
            a.addResource(Resources.LUMBER);
            a.addResource(Resources.BRICK);
            a.addResource(Resources.WHEAT);
            a.addResource(Resources.WOOL);
        }
        System.out.println("Initial settlements placed (nodes 0, 15, 28, 43).");

        // 5. Initialize the GamePlay Controller
        int maxRounds = 8192;
        GamePlay controller = new GamePlay(agents, catanBoard, gameDice, maxRounds);

        // 6. Run the Simulation
        System.out.println("--- Starting Catan Simulation ---");
        controller.runSimulation();

        // 7. Final Status Output
        System.out.println("--- Simulation Complete ---");
        System.out.println("Total Turns Elapsed: " + controller.getTurnNumber());
    }
}//ends class main
