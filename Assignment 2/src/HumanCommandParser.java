import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles the parsing of huma player commands from the 
 * command line. Fulfills Requirement R2.1 using Regular 
 * Expressions for command validation. Developed for Assignment-2
*/

public class HumanCommandParser {
    
    private final Scanner scanner;

    /**
     * Initializes the parser with a standard system input scanner.
    */
    public Scanner getScanner(){
        return scanner;
    }

    public HumanCommandParser(){
        this.scanner = new Scanner(System.in);
    }

    /**
     * Entry point for a human player's turn. Continues to prompt
     * until 'Go' is entered.
     * @param agent   The humman agent currently acting. 
     *  @param map    The game board for validating and exxecuting moves.
    */
    public void handleTurn(Agent agent, GameMap map){
        System.out.println("\n--- Human Turn: Agent " + agent.getId() + " ---");
        System.out.println("Commands: Roll, List, Build [settlement|city|road], Go");

        boolean turnComplete = false;
        while(!turnComplete) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            turnComplete = parseCommand(input, agent, map);
        }
    } 

    /**
     * Uses Regular Expressions to identify and execute human commands.
     * @return true if the 'Go' command is received, ending the turn.
    */
   private boolean parseCommand(String input, Agent agent, GameMap map){
       //Regex Definitions
       if(input.matches("(?i)^roll$")){
          System.out.println("Dice rolled via command.");
          return false; 
       } else if (input.matches("(?i)^go$")) {
           return true;
       } else if (input.matches("(?i)^list$")){
           System.out.println("Current Hand: " + agent.getResourceMap());
           return false;
       }
       //Build Settlement: build settlement [nodeId]
       else if (input.matches("(?i)^build\\s+settlement\\s+(\\d+)$")) {
           executeBuild(input, agent, map, "settlement");
           return false;           
       }
       //Build Child: build city [nodeId]
       else if (input.matches("(?i)^build\\s+city\\s+(\\d+)$")) {
           executeBuild(input, agent, map, "city");
           return false;           
       }
       //Build Road: build road [nodeA, nodeB]
       else if (input.matches("(?i)^build\\s+road\\s+(\\d+),\\s*(\\d+)$")) {
           executeBuild(input, agent, map, "road");
           return false;           
       }

       System.out.println("Invalid command or format. Try again.");
       return false;
   }

   private void executeBuild(String input, Agent agent, GameMap map, String type){
       Pattern pattern = Pattern.compile("(\\d+)");
       Matcher matcher = pattern.matcher(input);

       if(type.equals("road")){
           //Logic for road build with two node inputs
           System.out.println("Processing road build..."); 
       }else {
           if(matcher.find()){
            int nodeId = Integer.parseInt(matcher.group(1));
            boolean success = type.equals("settlement") ? agent.buildSettlement(map) : agent.buildCity(map);
            System.out.println(type + "build on node " + nodeId + (success ? " successful." : " failed."));
           } 
       }
   }
} //ends class 
