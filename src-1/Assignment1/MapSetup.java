package Assignment1;

import java.util.*;

/**
 * Hard-wired Catan map setup per specification (R1.1).
 * Tiles: 0 = center, 1-6 = inner ring, 7-18 = outer ring.
 * 19 tiles, 54 nodes (0-53), 72 edges.
 * Resource and number token layout from the provided board specification.
 */
public class MapSetup {

    /** For each tile (0-18), the 6 node IDs in clockwise order (top-right going around). */
    private static final int[][] TILE_NODES = {
        { 0, 1, 2, 3, 4, 5 },       // 0 center
        { 0, 1, 6, 7, 8, 9 },       // 1
        { 1, 2, 9, 10, 11, 12 },    // 2
        { 2, 3, 12, 13, 14, 15 },   // 3
        { 3, 4, 15, 16, 17, 18 },   // 4
        { 4, 5, 18, 19, 20, 21 },   // 5
        { 5, 0, 21, 22, 23, 24 },   // 6
        { 6, 7, 25, 26, 27, 28 },   // 7
        { 7, 8, 28, 29, 30, 31 },   // 8
        { 8, 9, 31, 32, 33, 34 },   // 9
        { 9, 10, 34, 35, 36, 37 },  // 10
        { 10, 11, 37, 38, 39, 40 }, // 11
        { 11, 12, 40, 41, 42, 43 }, // 12
        { 12, 13, 43, 44, 45, 46 }, // 13
        { 13, 14, 46, 47, 48, 49 }, // 14
        { 14, 15, 48, 49, 50, 51 }, // 15
        { 15, 16, 51, 52, 53, 25 }, // 16 desert (node 25 shared with tile 7)
        { 16, 17, 53, 26, 27, 22 }, // 17
        { 17, 18, 21, 22, 23, 24 }, // 18
    };

    /** Tile resources per spec: 0=WOOD, 1=WHEAT, 2=BRICK, 3=ORE, 4=SHEEP(WOOL), 5=SHEEP, 6=SHEEP, 7=WHEAT, 8=ORE, 9=WOOD, 10=ORE, 11=WHEAT, 12=WOOD, 13=BRICK, 14=BRICK, 15=WHEAT, 16=DESERT(NULL), 17=WOOD, 18=SHEEP. */
    private static final Resources[] TILE_RESOURCES = {
        Resources.LUMBER, Resources.WHEAT, Resources.BRICK, Resources.ORE, Resources.WOOL, Resources.WOOL, Resources.WOOL,
        Resources.WHEAT, Resources.ORE, Resources.LUMBER, Resources.ORE, Resources.WHEAT, Resources.LUMBER, Resources.BRICK, Resources.BRICK, Resources.WHEAT, Resources.NULL, Resources.LUMBER, Resources.WOOL
    };

    /** Production numbers per spec. Desert = 0 (no production). */
    private static final int[] TILE_NUMBERS = {
        10, 11, 8, 6, 4, 5, 12,
        3, 6, 4, 3, 9, 5, 9, 8, 4, 0, 2, 10
    };

    /**
     * Builds the full board: 54 nodes, 72 edges, 19 tiles.
     * Node and tile identification follow the specification.
     */
    public static Board createBoard() {
        int numNodes = 54;
        int numTiles = 19;

        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < numNodes; i++) {
            nodes.add(new Node(i));
        }

        // Build unique edges from tile definitions: each tile gives 6 edges (node[i], node[i+1 mod 6])
        Set<AbstractMap.SimpleEntry<Integer, Integer>> edgeSet = new HashSet<>();
        for (int t = 0; t < numTiles; t++) {
            int[] n = TILE_NODES[t];
            for (int i = 0; i < 6; i++) {
                int a = n[i];
                int b = n[(i + 1) % 6];
                edgeSet.add(new AbstractMap.SimpleEntry<>(Math.min(a, b), Math.max(a, b)));
            }
        }

        List<Edge> edges = new ArrayList<>();
        Map<String, Edge> edgeByPair = new HashMap<>();
        for (AbstractMap.SimpleEntry<Integer, Integer> p : edgeSet) {
            int a = p.getKey();
            int b = p.getValue();
            Node na = nodes.get(a);
            Node nb = nodes.get(b);
            Edge e = new Edge(na, nb);
            edges.add(e);
            edgeByPair.put(a + "," + b, e);
            na.addEdge(e);
            nb.addEdge(e);
        }

        // Build tiles with their edges (each tile's 6 edges in order)
        List<Tile> tiles = new ArrayList<>();
        for (int t = 0; t < numTiles; t++) {
            int[] n = TILE_NODES[t];
            List<Edge> tileEdges = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                int a = n[i];
                int b = n[(i + 1) % 6];
                String key = Math.min(a, b) + "," + Math.max(a, b);
                Edge e = edgeByPair.get(key);
                if (e != null) tileEdges.add(e);
            }
            Tile tile = new Tile(TILE_RESOURCES[t], TILE_NUMBERS[t], t, tileEdges);
            tiles.add(tile);
        }

        return new Board(tiles, edges, nodes);
    }
}
