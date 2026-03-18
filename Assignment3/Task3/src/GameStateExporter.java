import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class GameStateExporter {
    private static final Map<Integer, String> ID_TO_COLOUR = Map.of(
            0, "RED",
            1, "BLUE",
            2, "WHITE",
            3, "ORANGE"
    );

    private static String getColour(int id) {
        return ID_TO_COLOUR.getOrDefault(id, "UNKNOWN");
    }

    public static void export(GameMap map, Agent[] agents, String filename) {
        StringBuilder json = new StringBuilder("{\n");

        // ---------------- ROADS ----------------
        json.append("  \"roads\": [");
        boolean firstRoad = true;
        for (int i = 0; i < GameMap.NUM_EDGES; i++) {
            Edge e = map.getEdge(i);
            if (e.isOccupied()) {
                int[] endNodes = map.getNodesForEdge(i);
                // Prevent duplicates: only emit when a < b
                if (endNodes[0] < endNodes[1]) {
                    if (!firstRoad) json.append(",");
                    json.append(String.format(
                            "\n    { \"a\": %d, \"b\": %d, \"owner\": \"%s\" }",
                            endNodes[0],
                            endNodes[1],
                            getColour(e.getRoad().getOwner().getId())
                    ));
                    firstRoad = false;
                }
            }
        }
        json.append("\n  ],\n");

        // ---------------- BUILDINGS ----------------
        json.append("  \"buildings\": [");
        boolean firstBuilding = true;
        for (int i = 0; i < GameMap.NUM_NODES; i++) {
            Node n = map.getNode(i);
            if (n.isOccupied()) {
                if (!firstBuilding) json.append(",");
                json.append(String.format(
                        "\n    { \"node\": %d, \"owner\": \"%s\", \"type\": \"%s\" }",
                        i,
                        getColour(n.getBuilding().getAgent().getId()),
                        n.getBuilding().getClass().getSimpleName().toUpperCase()
                ));
                firstBuilding = false;
            }
        }
        json.append("\n  ]\n");
        json.append("}\n");

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(json.toString());
        } catch (IOException e) {
            System.err.println("Error exporting game state: " + e.getMessage());
        }
    }
}