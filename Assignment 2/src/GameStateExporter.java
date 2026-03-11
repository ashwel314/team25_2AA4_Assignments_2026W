import java.io.FileWriter;
import java.io.IOException;

/**
 * Utility class to fulfill Requirement R2.3
 * Exports the current GameMap and Agent states into a JSON format. 
*/
public class GameStateExporter {
    
    /**
     * Exports the current game state to a JSON file
     * @param map       The current game board.
     * @param agents    The list of players.
     * @param filename  The output path (e.g., "game_state.json")
    */
    public static void export(GameMap map, Agent[] agents, String filename){
        StringBuilder json = new StringBuilder("{\n");
        
        //1. Export Agent Data
        json.append(" \"agents\": [");        
        for (int i = 0; i < agents.length; i++){
            json.append(String.format("\n   {\"id\": %d, \"points\": %d, \"hand_size\": %d}", agents[i].getId(), agents[i].getTotalPoints(), agents[i].handSize()));
            if (i < agents.length - 1) {
                json.append(",");
            }
        }

        json.append("\n   ],\n"); 

        // 2. Export Board Data (Nodes & Edges)
        json.append("  \"board\": {\n   \"nodes\": [");
        for (int i = 0; i < GameMap.NUM_NODES; i++){
            Node n = map.getNode(i);
            String buildingType = n.isOccupied() ? n.getBuilding().getClass().getSimpleName() : "none";
            int ownerId = n.isOccupied() ? n.getBuilding().getAgent().getId() : -1;
            
            json.append(String.format("\n   {\"id\": %d, \"occupied\": %b, \"type\": \"%s\", \"owner\": %d}", i, n.isOccupied(), buildingType, ownerId));
            if (i < GameMap.NUM_NODES - 1) json.append(",");
        }
        json.append("\n   ]\n }\n}");
        
        //3. Write to File
        try(FileWriter writer = new FileWriter(filename)){
            writer.write(json.toString());
        } catch (IOException e){
            System.err.println("Error exporting game state: " + e.getMessage());
        }
    }
} //ends class
