import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles Regex-based command parsing for Human players.
 * Fulfills Requirement R2.1 and R2.4 for Assignment 2.
 * This class serves as the interface between the user's text input
 * and the formal game logic in the GameMap and Agent classes.
 */
public class HumanCommandParser {
    
    private final Scanner scanner;
    
    public HumanCommandParser(){
        this.scanner = new Scanner(System.in);
    }

    /**
     * Requirement R2.1 & R2.4: The "Turn Loop" for a human player.
     * Continues until the 'go' command (State transition to End turn) is received.
     * @param agent  The humman agent. 
     * @param map    The game board for validating and executing moves.
    */
    public void handleTurn(Agent agent, GameMap map){
        boolean turnOver = false;
        System.out.println("\n[Human Turn - Agent " + agent.getId() + "]");
        System.out.println("Available commands: 'roll', 'build settlement [id]', 'build road [id]', 'list', 'go'");
        
        while (!turnOver) {
            System.out.print("Agent " + agent.getId() + " > ");
            String input = scanner.nextLine().trim();

            // R2.1 Regex-based branching for command recognition
            if (input.matches("(?i)^roll$")) {
                // In R2.4, the Game loop rolls for the player, so we just acknowledge it here.
                System.out.println("Dice have already been cast for this turn.");
            } 
            else if (input.matches("(?i)^go$")) {
                // Task 2 Automaton: Transition from Main Phase to End Turn
                turnOver = true; 
                System.out.println("Ending turn...");
            } 
            else if (input.matches("(?i)^list$")) {
                // Helper command to show hand state
                System.out.println("Your Resources: " + agent.getResourceMap());
                System.out.println("Victory Points: " + agent.getTotalPoints());
            } 
            else if (input.matches("(?i)^build\\s+settlement\\s+(\\d+)$")) {
                processBuildSettlement(input, agent, map);
            } 
            else if (input.matches("(?i)^build\\s+road\\s+(\\d+)$")) {
                processBuildRoad(input, agent, map);
            }
            else {
                // Error handling for R2.1
                System.out.println("Syntax Error: Command not recognized. Use 'build settlement [id]' or 'go'.");
            }
        }
    } 

    /**
     * Parses the node ID from the command and executes the build logic.
     */
    private void processBuildSettlement(String input, Agent agent, GameMap map) {
        Pattern p = Pattern.compile("(\\d+)");
        Matcher m = p.matcher(input);
        if (m.find()) {
            int nodeId = Integer.parseInt(m.group(1));
            
            // Check game logic (Semantic Correctness)
            if (map.validSettlementNodes(agent, false).contains(nodeId)) {
                map.placeSettlement(agent, nodeId);
                System.out.println("Success: Settlement built on node " + nodeId);
            } else {
                System.out.println("Game Error: Cannot build settlement on node " + nodeId + ". Check resources or distance rule.");
            }
        }
    }

    /**
     * Parses the edge ID from the command and executes the road logic.
     */
    private void processBuildRoad(String input, Agent agent, GameMap map) {
        Pattern p = Pattern.compile("(\\d+)");
        Matcher m = p.matcher(input);
        if (m.find()) {
            int edgeId = Integer.parseInt(m.group(1));
            
            if (map.validRoadEdges(agent).contains(edgeId)) {
                map.placeRoad(agent, edgeId);
                System.out.println("Success: Road built on edge " + edgeId);
            } else {
                System.out.println("Game Error: Illegal road placement on edge " + edgeId);
            }
        }
    }
    

} //ends class 
