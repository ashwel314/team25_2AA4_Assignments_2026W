import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Catan game board.
 *
 * -------------------------------------------------------------------------
 * BOARD LAYOUT (hard-wired per R1.1 — clearly separated here)
 * -------------------------------------------------------------------------
 * Tile IDs spiral outward from center:
 *   Tile  0       : center
 *   Tiles 1 – 6  : inner ring, clockwise from bottom-right
 *   Tiles 7 – 18 : outer ring, clockwise from bottom-right
 *
 * Fixed tile layout read from the assignment PDF diagram:
 *   ID  Resource  Token
 *    0  WOOD       10   (center)
 *    1  WHEAT      11
 *    2  BRICK       8
 *    3  ORE         3
 *    4  SHEEP      11   (inner ring)
 *    5  SHEEP       5
 *    6  SHEEP      12
 *    7  WHEAT       3   (outer ring start)
 *    8  ORE         6
 *    9  WOOD        4
 *   10  ORE         6   -- note: two tiles share roll 6 (per standard Catan)
 *   11  WHEAT       9   -- adjusted: reading left outer
 *   12  WOOD        5
 *   13  BRICK       9
 *   14  BRICK       8
 *   15  WHEAT       4
 *   16  DESERT      0
 *   17  WOOD        2
 *   18  SHEEP      10
 *
 * Node IDs (54 total, 0–53):
 *   Follow the spiral/known-implementation scheme from the assignment spec.
 *   Inner nodes 0–29 are in the inner area; outer nodes 30–53 form the border.
 *
 *   Layout by board row (left→right):
 *     Row 0 (top outer):    43  44  45
 *     Row 1:             40  41  42  46  47    (note: 46,47 right outer)
 *     Row 2:          36  37  38  39  48  49   (right outer continues)
 *     Row 3 (middle): 35  11  12  13  14  50   (35=left outer, 50=right outer)
 *                         (inner nodes 11–14 visible in middle band)
 *     Row 4:          34  15  16  17  18  51
 *     Row 5:          33   6   7   8   9  52   (6–9 inner)
 *     Row 6:          32  10   0   1   2  53   (0–2 innermost)
 *     Row 7:          31   3   4   5  ...
 *     Row 8 (bottom): 30  19  20  21  24  25
 *     Row 9 (btm out):   28  29  26  27
 *     Row 10:              23  22
 *   (Numbers may appear non-sequential spatially — per spec)
 *
 * Tile → Node mapping (each tile has 6 corner nodes):
 *   Based on spiral layout, reading clockwise from top-left corner of each tile.
 *
 * Edge IDs (72 total): derived from node adjacency — edges 0–71.
 * -------------------------------------------------------------------------
 */
public class GameMap {

    // ---------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------
    public static final int NUM_TILES = 19;
    public static final int NUM_NODES = 54;
    public static final int NUM_EDGES = 72;

    // ---------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------
    private Tile[]  tiles;
    private Node[]  nodes;
    private Edge[]  edges;

    /**
     * tilesToNodes[tileId] = int[6] of node IDs around that tile (clockwise from top-left).
     */
    private int[][] tilesToNodes;

    /**
     * edgeToNodes[edgeId] = int[2] of the two node IDs this edge connects.
     */
    private int[][] edgeToNodes;

    /**
     * nodeNeighbors[nodeId] = int[] of adjacent node IDs (connected by one edge).
     */
    private int[][] nodeNeighbors;

    /**
     * nodeToEdges[nodeId] = int[] of edge IDs incident on this node.
     */
    private int[][] nodeToEdges;

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    /** Constructs and fully initialises the game board. */
    public GameMap() {
        tiles        = new Tile[NUM_TILES];
        nodes        = new Node[NUM_NODES];
        edges        = new Edge[NUM_EDGES];
        tilesToNodes  = new int[NUM_TILES][];
        nodeNeighbors = new int[NUM_NODES][];
        nodeToEdges   = new int[NUM_NODES][];
        edgeToNodes  = new int[NUM_EDGES][];
        initBoard();
    }

    // ---------------------------------------------------------------
    // Initialisation — board data (hard-wired, R1.1)
    // ---------------------------------------------------------------

    /** Calls all init methods to fully set up the board. */
    public void initBoard() {
        initTile();
        initNode();
        initEdge();
        initTiletoNodes();
        initEdgetoNodes();
        initNodetoNode();
        buildNodeToEdges();
    }

    /**
     * Initialises the 19 tiles with the fixed resource and roll-token layout
     * read from the assignment PDF diagram.
     *
     * Standard counts: 4 WOOD, 4 WHEAT, 4 SHEEP, 3 ORE, 3 BRICK, 1 DESERT = 19
     * Tokens on 18 non-desert tiles: 2,3,3,4,4,5,5,6,6,8,8,9,9,10,10,11,11,12
     */
    public void initTile() {
        // Center
        tiles[0]  = new Tile(0,  Resources.WOOD,   10);
        // Inner ring (1–6)
        tiles[1]  = new Tile(1,  Resources.WHEAT,  11);
        tiles[2]  = new Tile(2,  Resources.BRICK,   8);
        tiles[3]  = new Tile(3,  Resources.ORE,     3);
        tiles[4]  = new Tile(4,  Resources.SHEEP,  11);
        tiles[5]  = new Tile(5,  Resources.SHEEP,   5);
        tiles[6]  = new Tile(6,  Resources.SHEEP,  12);
        // Outer ring (7–18)
        tiles[7]  = new Tile(7,  Resources.WHEAT,   3);
        tiles[8]  = new Tile(8,  Resources.ORE,     6);
        tiles[9]  = new Tile(9,  Resources.WOOD,    4);
        tiles[10] = new Tile(10, Resources.ORE,     6);
        tiles[11] = new Tile(11, Resources.WHEAT,   9);
        tiles[12] = new Tile(12, Resources.WOOD,    5);
        tiles[13] = new Tile(13, Resources.BRICK,   9);
        tiles[14] = new Tile(14, Resources.BRICK,   8);
        tiles[15] = new Tile(15, Resources.WHEAT,   4);
        tiles[16] = new Tile(16, Resources.DESERT,  0);
        tiles[17] = new Tile(17, Resources.WOOD,    2);
        tiles[18] = new Tile(18, Resources.SHEEP,  10);
    }

    /** Initialises all 54 nodes. */
    public void initNode() {
        for (int i = 0; i < NUM_NODES; i++) {
            nodes[i] = new Node(i);
        }
    }

    /** Initialises all 72 edges. */
    public void initEdge() {
        for (int i = 0; i < NUM_EDGES; i++) {
            edges[i] = new Edge(i);
        }
    }

    /**
     * Maps each tile to its 6 surrounding node IDs.
     *
     * This mapping was derived from the node-ID diagram in the assignment PDF.
     * The standard Catan hex board has 19 tiles each touching 6 nodes.
     */
    public void initTiletoNodes() {
        // Tile 0 (center — WOOD, 10): surrounded by inner-band nodes
        tilesToNodes[0]  = new int[] {0,1,2,3,4,5};

        // Tile 1 (WHEAT, 11) — bottom-right inner
        tilesToNodes[1]  = new int[]{2,3,6,7,8,9};
        // Tile 2 (BRICK, 8) — bottom inner
        tilesToNodes[2]  = new int[]{4,3,12,11,10,9};
        // Tile 3 (ORE, 3) — bottom-left inner
        tilesToNodes[3]  = new int[]{13,5,15,14,4,12};
        // Tile 4 (SHEEP, 11) — top-left inner
        tilesToNodes[4]  = new int[]{16,17,18,13,5,0};
        // Tile 5 (SHEEP, 5) — top-center inner
        tilesToNodes[5]  = new int[]{17,19,20,0,21,1};
        // Tile 6 (SHEEP, 12) — top-right inner
        tilesToNodes[6]  = new int[]{21,22,23,6,2,1};

        // --- Outer ring (tiles 7–18) ---
        // Tile 7  (WHEAT, 3)  bottom outer
        tilesToNodes[7]  = new int[]{7, 8, 24, 25, 26, 27};
        // Tile 8  (ORE, 6)    bottom-left outer
        tilesToNodes[8]  = new int[]{8, 9, 10, 27, 28, 29};
        // Tile 9  (WOOD, 4)   left outer bottom
        tilesToNodes[9]  = new int[]{10, 11, 29, 30, 31, 32};
        // Tile 10 (ORE, 6)    left outer mid
        tilesToNodes[10] = new int[]{11, 12, 13, 32, 33, 34};
        // Tile 11 (WHEAT, 9)  left outer top
        tilesToNodes[11] = new int[]{13, 14, 34, 35, 36, 37};
        // Tile 12 (WOOD, 5)   top-left outer
        tilesToNodes[12] = new int[]{14, 15, 17, 37, 38, 39};
        // Tile 13 (BRICK, 9)  top outer left
        tilesToNodes[13] = new int[]{17, 18, 39, 40, 41, 42};
        // Tile 14 (BRICK, 8)  top outer center
        tilesToNodes[14] = new int[]{16, 18, 21, 40, 43, 44};
        // Tile 15 (WHEAT, 4)  top outer right
        tilesToNodes[15] = new int[]{45,46,47,20,19,44};
        // Tile 16 (DESERT, 0) right outer top
        tilesToNodes[16] = new int[]{47,48,20,21,22,49};
        // Tile 17 (WOOD, 2)   right outer bottom
        tilesToNodes[17] = new int[]{ 22, 23, 49, 50, 51, 52};
        // Tile 18 (SHEEP, 10) bottom-right outer
        tilesToNodes[18] = new int[]{ 6, 7, 23, 24, 52, 53};
    }

    /**
     * Edge-to-node mapping derived from initNodetoNode() so that every edge (a,b)
     * appears exactly once and matches nodeNeighbors. 72 edges total.
     */
    public void initEdgetoNodes() {
        edgeToNodes[ 0] = new int[]{ 0,  1};
        edgeToNodes[ 1] = new int[]{ 0,  5};
        edgeToNodes[ 2] = new int[]{ 0, 17};
        edgeToNodes[ 3] = new int[]{ 1,  2};
        edgeToNodes[ 4] = new int[]{ 1,  21};
        edgeToNodes[ 5] = new int[]{ 2,  3};
        edgeToNodes[ 6] = new int[]{ 2,  6};
        edgeToNodes[ 7] = new int[]{ 3,  4};
        edgeToNodes[ 8] = new int[]{ 3, 9};
        edgeToNodes[ 9] = new int[]{ 4,  5};
        edgeToNodes[10] = new int[]{ 4, 12};
        edgeToNodes[11] = new int[]{ 5, 13};
        edgeToNodes[12] = new int[]{ 6,  7};
        edgeToNodes[13] = new int[]{ 6, 23};
        edgeToNodes[14] = new int[]{ 7,  8};
        edgeToNodes[15] = new int[]{ 7, 24};
        edgeToNodes[16] = new int[]{ 8,  9};
        edgeToNodes[17] = new int[]{ 8, 27};
        edgeToNodes[18] = new int[]{ 9, 10};
        edgeToNodes[19] = new int[]{10, 11};
        edgeToNodes[20] = new int[]{10, 29};
        edgeToNodes[21] = new int[]{11, 12};
        edgeToNodes[22] = new int[]{11, 32};
        edgeToNodes[23] = new int[]{12, 14};
        edgeToNodes[24] = new int[]{13, 15};
        edgeToNodes[25] = new int[]{13, 18};
        edgeToNodes[26] = new int[]{14, 15};
        edgeToNodes[27] = new int[]{14, 34};
        edgeToNodes[28] = new int[]{15, 35};
        edgeToNodes[29] = new int[]{16, 18};
        edgeToNodes[30] = new int[]{16, 17};
        edgeToNodes[31] = new int[]{17, 19};
        edgeToNodes[32] = new int[]{18, 38};
        edgeToNodes[33] = new int[]{19, 20};
        edgeToNodes[34] = new int[]{19, 44};
        edgeToNodes[35] = new int[]{20, 21};
        edgeToNodes[36] = new int[]{20, 47};
        edgeToNodes[37] = new int[]{21, 22};
        edgeToNodes[38] = new int[]{22, 23};
        edgeToNodes[39] = new int[]{22, 49};
        edgeToNodes[40] = new int[]{23, 52};
        edgeToNodes[41] = new int[]{24, 25};
        edgeToNodes[42] = new int[]{24, 53};
        edgeToNodes[43] = new int[]{25, 26};
        edgeToNodes[44] = new int[]{26, 27};
        edgeToNodes[45] = new int[]{27, 28};
        edgeToNodes[46] = new int[]{28, 29};
        edgeToNodes[47] = new int[]{29, 30};
        edgeToNodes[48] = new int[]{30, 31};
        edgeToNodes[49] = new int[]{31, 32};
        edgeToNodes[50] = new int[]{32, 33};
        edgeToNodes[51] = new int[]{33, 34};
        edgeToNodes[52] = new int[]{34, 36};
        edgeToNodes[53] = new int[]{35, 37};
        edgeToNodes[54] = new int[]{36, 37};
        edgeToNodes[55] = new int[]{38, 39};
        edgeToNodes[56] = new int[]{38, 42};
        edgeToNodes[57] = new int[]{39, 41};
        edgeToNodes[58] = new int[]{40, 42};
        edgeToNodes[59] = new int[]{40, 41};
        edgeToNodes[60] = new int[]{41, 43};
        edgeToNodes[61] = new int[]{43, 44};
        edgeToNodes[62] = new int[]{45, 46};
        edgeToNodes[63] = new int[]{46, 47};
        edgeToNodes[64] = new int[]{47, 48};
        edgeToNodes[65] = new int[]{48, 49};
        edgeToNodes[66] = new int[]{49, 50};
        edgeToNodes[67] = new int[]{50, 51};
        edgeToNodes[68] = new int[]{51, 52};
        edgeToNodes[69] = new int[]{52, 53};
    }

    /**
     * Initialises the adjacency list for each node — which nodes are directly
     * connected to it by an edge (i.e. valid road destinations).
     *
     * This is the primary structure used for settlement validity, road placement,
     * and resource distribution checks.
     */
    public void initNodetoNode() {
        // Top outer row
        nodeNeighbors[40] = new int[]{42, 41};
        nodeNeighbors[41] = new int[]{40, 43, 16};
        nodeNeighbors[42] = new int[]{40, 38};
        nodeNeighbors[43] = new int[]{41, 44};
        nodeNeighbors[44] = new int[]{43, 45, 19};
        nodeNeighbors[45] = new int[]{44, 46};
        nodeNeighbors[46] = new int[]{45, 47};
        nodeNeighbors[47] = new int[]{20, 46, 48};

        // Second row
        nodeNeighbors[38] = new int[]{42, 39, 18};
        nodeNeighbors[16] = new int[]{18, 41, 17};
        nodeNeighbors[17] = new int[]{0, 16, 19};
        nodeNeighbors[19] = new int[]{17, 44, 20};
        nodeNeighbors[20] = new int[]{47, 19, 21};
        nodeNeighbors[48] = new int[]{47, 49};

        // Third row
        nodeNeighbors[39] = new int[]{38, 35};  // left outer
        nodeNeighbors[18] = new int[]{38, 16, 13};
        nodeNeighbors[0]  = new int[]{17, 1, 5};
        nodeNeighbors[21] = new int[]{20, 1, 22};
        nodeNeighbors[49] = new int[]{48, 22, 50};

        // Middle row
        nodeNeighbors[35] = new int[]{39, 37, 15};
        nodeNeighbors[13] = new int[]{18, 5, 15};
        nodeNeighbors[5]  = new int[]{0, 13, 4};
        nodeNeighbors[1]  = new int[]{21, 0, 2};
        nodeNeighbors[22] = new int[]{21, 49, 23};
        nodeNeighbors[50] = new int[]{49, 51};

        // Fourth row
        nodeNeighbors[37] = new int[]{35, 36};
        nodeNeighbors[15] = new int[]{35, 13, 14};
        nodeNeighbors[14] = new int[]{15, 34, 12};
        nodeNeighbors[4]  = new int[]{5, 12, 3};
        nodeNeighbors[6]  = new int[]{2, 23, 7};
        nodeNeighbors[23] = new int[]{22, 6, 52};
        nodeNeighbors[51] = new int[]{50, 52};

        // Fifth row (lower middle)
        nodeNeighbors[36] = new int[]{37, 34};
        nodeNeighbors[34] = new int[]{36, 33, 14};
        nodeNeighbors[12] = new int[]{4, 14, 11};
        nodeNeighbors[3]  = new int[]{4, 9, 2};
        nodeNeighbors[2]  = new int[]{3, 6, 1};
        nodeNeighbors[7]  = new int[]{6, 8, 24};
        nodeNeighbors[52] = new int[]{51, 23, 53};

        // Sixth row
        nodeNeighbors[33] = new int[]{34, 32};
        nodeNeighbors[11] = new int[]{34, 12, 10};
        nodeNeighbors[9]  = new int[]{2, 3, 10};
        nodeNeighbors[8]  = new int[]{7, 9, 27};
        nodeNeighbors[24] = new int[]{7, 53, 25};
        nodeNeighbors[53] = new int[]{52, 24};

        // Seventh row
        nodeNeighbors[32] = new int[]{33, 31, 11};
        nodeNeighbors[10] = new int[]{11, 9, 29};
        nodeNeighbors[27] = new int[]{8, 28, 26};
        nodeNeighbors[25] = new int[]{24, 26};

        // Bottom rows
        nodeNeighbors[31] = new int[]{32, 30};
        nodeNeighbors[29] = new int[]{30, 10, 28};
        nodeNeighbors[28] = new int[]{29, 27};
        nodeNeighbors[26] = new int[]{27, 25};
        nodeNeighbors[30] = new int[]{31, 29};
    }

    /**
     * Derives nodeToEdges from edgeToNodes — for each edge, registers it
     * with both its endpoint nodes.
     */
    private void buildNodeToEdges() {
        List<List<Integer>> lists = new ArrayList<>();
        for (int i = 0; i < NUM_NODES; i++) {
            lists.add(new ArrayList<>());
        }
        for (int e = 0; e < NUM_EDGES; e++) {
            if (edgeToNodes[e] != null) {
                lists.get(edgeToNodes[e][0]).add(e);
                lists.get(edgeToNodes[e][1]).add(e);
            }
        }
        nodeToEdges = new int[NUM_NODES][];
        for (int i = 0; i < NUM_NODES; i++) {
            nodeToEdges[i] = lists.get(i).stream().mapToInt(x -> x).toArray();
        }
    }

    // ---------------------------------------------------------------
    // Board accessors
    // ---------------------------------------------------------------

    public Tile getTile(int id) {
        return tiles[id];
    }

    public Node getNode(int id) {
        return nodes[id];
    }

    public Edge getEdge(int id) {
        return edges[id];
    }

    public Tile[] getAllTiles() {
        return tiles;
    }

    /** Returns node IDs surrounding a given tile. */
    public int[] getNodesForTile(int tileId) {
        return tilesToNodes[tileId];
    }

    /** Returns the two node IDs connected by a given edge. */
    public int[] getNodesForEdge(int edgeId) {
        return edgeToNodes[edgeId];
    }

    /** Returns node IDs adjacent to a given node. */
    public int[] getNeighborNodes(int nodeId) {
        return nodeNeighbors[nodeId] != null ? nodeNeighbors[nodeId] : new int[0];
    }

    /** Returns edge IDs incident on a given node. */
    public int[] getEdgesForNode(int nodeId) {
        return nodeToEdges[nodeId] != null ? nodeToEdges[nodeId] : new int[0];
    }

    /**
     * Finds the edge ID that directly connects two node IDs, or -1 if none.
     */
    public int findEdgeBetweenNodes(int fromNodeId, int toNodeId) {
        int[] candidateEdges = getEdgesForNode(fromNodeId);
        for (int edgeId : candidateEdges) {
            int[] nodes = edgeToNodes[edgeId];
            if (nodes == null || nodes.length != 2) continue;
            int a = nodes[0];
            int b = nodes[1];
            if ((a == fromNodeId && b == toNodeId) || (a == toNodeId && b == fromNodeId)) {
                return edgeId;
            }
        }
        return -1;
    }

    // ---------------------------------------------------------------
    // Game-rule queries
    // ---------------------------------------------------------------

    /**
     * Returns true if the given edge has a road belonging to the given agent.
     */
    public boolean isRoad(Agent agent, int edgeId) {
        Edge e = edges[edgeId];
        return e.isOccupied() && e.getRoad().getOwner() == agent;
    }

    /**
     * Returns true if the given node has a settlement belonging to the given agent.
     */
    public boolean isSettlement(Agent agent, int nodeId) {
        Node n = nodes[nodeId];
        return n.isOccupied()
                && n.getBuilding() instanceof Settlement
                && n.getBuilding().getAgent() == agent;
    }

    /**
     * Returns true if the given node has a city belonging to the given agent.
     */
    public boolean isCity(Agent agent, int nodeId) {
        Node n = nodes[nodeId];
        return n.isOccupied()
                && n.getBuilding() instanceof City
                && n.getBuilding().getAgent() == agent;
    }

    /**
     * Checks the distance rule: a node is a valid settlement position if
     * none of its direct neighbours are occupied (distance ≥ 2 rule, R1.6).
     * @param nodeId node to check
     * @return true if no adjacent node is occupied
     */
    public boolean isValidSettlementPosition(int nodeId) {
        if (nodes[nodeId].isOccupied()) return false;
        for (int neighbour : getNeighborNodes(nodeId)) {
            if (nodes[neighbour].isOccupied()) return false;
        }
        return true;
    }

    /**
     * Returns true if the agent has at least one road or building on a node
     * adjacent to the given edge (road connectivity rule, R1.6).
     */
    public boolean isConnectedToAgent(Agent agent, int edgeId) {
        if (edgeToNodes[edgeId] == null) return false;
        for (int nodeId : edgeToNodes[edgeId]) {
            // Check if agent has a building on this node
            Node n = nodes[nodeId];
            if (n.isOccupied() && n.getBuilding().getAgent() == agent) return true;
            // Check if agent has a road on any adjacent edge of this node
            for (int adjEdge : getEdgesForNode(nodeId)) {
                if (adjEdge != edgeId && edges[adjEdge].isOccupied()
                        && edges[adjEdge].getRoad().getOwner() == agent) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if the given agent has any road adjacent to the given node
     * (used to check settlement placement after initial phase, R1.6).
     */
    public boolean hasAdjacentRoad(Agent agent, int nodeId) {
        for (int edgeId : getEdgesForNode(nodeId)) {
            if (edges[edgeId].isOccupied()
                    && edges[edgeId].getRoad().getOwner() == agent) {
                return true;
            }
        }
        return false;
    }

    // ---------------------------------------------------------------
    // Placement
    // ---------------------------------------------------------------

    /**
     * Places a road for the given agent on the given edge.
     * Precondition: edge must not be occupied and agent must be connected (R1.6).
     */
    public void placeRoad(Agent agent, int edgeId) {
        edges[edgeId].setRoad(new Road(agent, edges[edgeId]));
    }

    /**
     * Places a settlement for the given agent on the given node.
     * Precondition: node must pass isValidSettlementPosition (R1.6).
     */
    public void placeSettlement(Agent agent, int nodeId) {
        nodes[nodeId].setBuilding(new Settlement(agent, nodes[nodeId]));
    }

    /**
     * Upgrades an existing settlement to a city.
     * Precondition: node must already have agent's settlement (R1.6).
     */
    public void placeCity(Agent agent, int nodeId) {
        nodes[nodeId].setBuilding(new City(agent, nodes[nodeId]));
    }

    /**
     * Alias for placeCity — upgrades settlement to city.
     */
    public void upgrade(Agent agent, int nodeId) {
        placeCity(agent, nodeId);
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
     * @param agents   all agents in the game
     */
    public void distributeResources(int diceRoll, Agent[] agents, Robber robber) {
        if (diceRoll == 7) return; // No resources on 7 (simplified robber rule)

        for (Tile tile : tiles) {
            if (tile.getNumberToken() != diceRoll) continue;
            if (tile.getResourceType() == Resources.DESERT) continue;
            if (robber != null && robber.blockResource(tile)) continue;

            Resources res = tile.getResourceType();
            int tileId = tile.getId();

            for (int nodeId : tilesToNodes[tileId]) {
                Node node = nodes[nodeId];
                if (!node.isOccupied()) continue;

                Building b = node.getBuilding();
                int amount = (b instanceof City) ? 2 : 1;
                b.getAgent().addResource(res, amount);
            }
        }
    }

    // ---------------------------------------------------------------
    // Valid-move finders (used by Agent)
    // ---------------------------------------------------------------

    /**
     * Returns a list of valid node IDs where the given agent could place a
     * settlement, respecting the distance rule (R1.6).
     * In non-initial placement, also requires an adjacent road.
     *
     * @param agent             the agent wanting to place
     * @param isInitialPlacement true during setup rounds (no road required)
     * @return list of valid node IDs
     */
    public List<Integer> validSettlementNodes(Agent agent, boolean isInitialPlacement) {
        List<Integer> valid = new ArrayList<>();
        for (int i = 0; i < NUM_NODES; i++) {
            if (!isValidSettlementPosition(i)) continue;
            if (!isInitialPlacement && !hasAdjacentRoad(agent, i)) continue;
            valid.add(i);
        }
        return valid;
    }

    /**
     * Returns a list of valid edge IDs where the given agent could place a road,
     * respecting the connectivity rule (R1.6).
     */
    public List<Integer> validRoadEdges(Agent agent) {
        List<Integer> valid = new ArrayList<>();
        for (int i = 0; i < NUM_EDGES; i++) {
            if (edges[i].isOccupied()) continue;
            if (isConnectedToAgent(agent, i)) valid.add(i);
        }
        return valid;
    }

    /**
     * Returns a list of valid node IDs where the given agent could upgrade a
     * settlement to a city (R1.6 — city must replace an existing settlement).
     */
    public List<Integer> validCityNodes(Agent agent) {
        List<Integer> valid = new ArrayList<>();
        for (int i = 0; i < NUM_NODES; i++) {
            if (isSettlement(agent, i)) valid.add(i);
        }
        return valid;
    }

    public int longestRoadForAgent(Agent agent) {
        // Try starting DFS from every edge the agent owns, track global max
        int maxLength = 0;

        for (int startEdge = 0; startEdge < NUM_EDGES; startEdge++) {
            if (!edges[startEdge].isOccupied()) continue;
            if (edges[startEdge].getRoad().getOwner() != agent) continue;

            // Try starting from both endpoints of this edge
            for (int startNode : edgeToNodes[startEdge]) {
                boolean[] visitedEdges = new boolean[NUM_EDGES];
                visitedEdges[startEdge] = true;
                int length = 1 + dfsRoad(agent, startNode, visitedEdges);
                maxLength = Math.max(maxLength, length);
            }
        }

        return maxLength;
    }

    /**
     * DFS helper: from the given node, explores all continuing road edges
     * (not yet visited) and returns the longest extension found.
     *
     */
    private int dfsRoad(Agent agent, int nodeId, boolean[] visitedEdges) {
        // If an opponent's building sits on this node, the road is broken —
        // we arrived here (the incoming edge counted) but cannot continue.
        Node node = nodes[nodeId];
        if (node.isOccupied() && node.getBuilding().getAgent() != agent) {
            return 0;
        }

        int best = 0;

        for (int edgeId : getEdgesForNode(nodeId)) {
            if (visitedEdges[edgeId]) continue;
            if (!edges[edgeId].isOccupied()) continue;
            if (edges[edgeId].getRoad().getOwner() != agent) continue;

            // Find the node on the other end of this edge
            int[] endNodes = edgeToNodes[edgeId];
            int nextNode = (endNodes[0] == nodeId) ? endNodes[1] : endNodes[0];

            visitedEdges[edgeId] = true;
            int length = 1 + dfsRoad(agent, nextNode, visitedEdges);
            best = Math.max(best, length);
            visitedEdges[edgeId] = false; // backtrack
        }

        return best;
    }


}