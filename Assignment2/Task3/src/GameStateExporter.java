import java.io.FileWriter;
import java.io.IOException;

/**
 * Exports the current game state to a JSON file for the visualizer (R2.2, R2.3).
 */
public class GameStateExporter {

    public static void export(GameMap map, Agent[] agents, String filename) {
        StringBuilder json = new StringBuilder("{\n");

        // Agents
        json.append("  \"agents\": [");
        for (int i = 0; i < agents.length; i++) {
            Agent a = agents[i];
            json.append(String.format(
                    "\n    {\"id\": %d, \"points\": %d, \"hand_size\": %d}",
                    a.getId(), a.getTotalPoints(), a.handSize()
            ));
            if (i < agents.length - 1) {
                json.append(",");
            }
        }
        json.append("\n  ],\n");

        // Board nodes (enough for visualizer to reconstruct state)
        json.append("  \"board\": {\n");
        json.append("    \"nodes\": [");
        for (int i = 0; i < GameMap.NUM_NODES; i++) {
            Node n = map.getNode(i);
            String buildingType = n.isOccupied() ? n.getBuilding().getClass().getSimpleName() : "none";
            int ownerId = n.isOccupied() ? n.getBuilding().getAgent().getId() : -1;
            json.append(String.format(
                    "\n      {\"id\": %d, \"occupied\": %b, \"type\": \"%s\", \"owner\": %d}",
                    i, n.isOccupied(), buildingType, ownerId
            ));
            if (i < GameMap.NUM_NODES - 1) {
                json.append(",");
            }
        }
        json.append("\n    ]\n");
        json.append("  }\n");
        json.append("}\n");

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(json.toString());
        } catch (IOException e) {
            System.err.println("Error exporting game state: " + e.getMessage());
        }
    }
}

