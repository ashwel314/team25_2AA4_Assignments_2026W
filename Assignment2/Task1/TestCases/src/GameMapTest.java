import org.junit.*;
import static org.junit.Assert.*;

import java.util.List;

/**
 * Unit tests targeting {@link GameMap} behaviour.
 *
 * Focuses on:
 * - Resource distribution from a single activated tile.
 * - Valid settlement node detection in the initial placement phase.
 */
public class GameMapTest {

    /**
     * When a tile with a given number token is rolled, agents with
     * settlements and cities adjacent to that tile should receive
     * resources in a 1:2 ratio.
     *
     * This test uses tile 0 (center, WOOD, token 10) whose surrounding
     * node IDs are defined in {@link GameMap#initTiletoNodes()}.
     */
    @Test
    public void testDistributeResourcesForSingleTile() {
        GameMap map = new GameMap();

        // Two agents: one with a settlement, one with a city on the same tile.
        Agent a0 = new Agent(0, 0);
        Agent a1 = new Agent(1, 0);
        Agent[] agents = { a0, a1 };

        int[] nodesAroundTile0 = map.getNodesForTile(0); // WOOD, token 10
        assertTrue("Tile 0 should have 6 surrounding nodes", nodesAroundTile0.length == 6);

        int settlementNodeId = nodesAroundTile0[0];
        int cityNodeId = nodesAroundTile0[1];

        // Place a settlement for agent 0 and a city for agent 1.
        map.placeSettlement(a0, settlementNodeId);
        map.placeCity(a1, cityNodeId);

        // Roll 10 to activate tile 0 (WOOD).
        map.distributeResources(10, agents);

        int woodForA0 = a0.getResourceMap().get(Resources.WOOD);
        int woodForA1 = a1.getResourceMap().get(Resources.WOOD);

        assertEquals("Settlement should receive 1 wood from activated tile", 1, woodForA0);
        assertEquals("City should receive 2 wood from activated tile", 2, woodForA1);
    }

    /**
     * In the initial placement phase, many nodes should be valid settlement
     * positions; once a settlement is placed, adjacent nodes should become
     * invalid due to the distance rule (no neighbouring settlements).
     */
    @Test
    public void testValidSettlementNodesInitialPlacementRespectsDistanceRule() {
        GameMap map = new GameMap();
        Agent agent = new Agent(0, 0);

        // Initially, there should be multiple valid nodes.
        List<Integer> initialValid = map.validSettlementNodes(agent, true);
        assertFalse("There should be some valid initial settlement nodes", initialValid.isEmpty());

        int chosenNode = initialValid.get(0);
        map.placeSettlement(agent, chosenNode);

        List<Integer> afterPlacementValid = map.validSettlementNodes(agent, true);

        // The chosen node should no longer be valid.
        assertFalse("Placed node should not remain valid",
                afterPlacementValid.contains(chosenNode));

        // None of the direct neighbour nodes should be valid anymore either.
        for (int neighbour : map.getNeighborNodes(chosenNode)) {
            assertFalse("Neighbour node " + neighbour + " should not be valid " +
                            "after settlement placement at " + chosenNode,
                    afterPlacementValid.contains(neighbour));
        }
    }
}

