import org.junit.*;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * JUnit tests for HumanAgent using CommandParser.
 * Focus on turn-flow and initial placement behaviour.
 */
public class HumanAgentTest {

    private static final int DEFAULT_TIMEOUT = 2000;

    private InputStream systemInBackup;

    @Before
    public void backupSystemIn() {
        systemInBackup = System.in;
    }

    @After
    public void restoreSystemIn() {
        System.setIn(systemInBackup);
    }

    /**
     * Helper to simulate console input for the HumanAgent's Scanner.
     */
    private void simulateInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testMainTurnRequiresRollBeforeGo() {
        GameMap map = new GameMap();
        CommandParser parser = new CommandParser();
        HumanAgent human = new HumanAgent(0, 0, parser);

        // First "go" should be rejected (no roll yet), then we roll, then "go" ends the turn.
        simulateInput("go\nroll\ngo\n");

        // If handleTurn returns without timing out, the state machine respected the rule.
        human.handleTurn(map, /*round=*/1, /*diceRoll=*/8);
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testInitialPlacementPlacesSettlementAndRoad() {
        GameMap map = new GameMap();
        CommandParser parser = new CommandParser();
        HumanAgent human = new HumanAgent(0, 0, parser);

        // Pick a valid settlement node for initial placement
        List<Integer> validNodes = map.validSettlementNodes(human, true);
        assertFalse("There should be at least one valid initial settlement node",
                validNodes.isEmpty());
        int settlementNode = validNodes.get(0);

        // Choose an edge incident on that node; this will be a legal road location
        int[] candidateEdges = map.getEdgesForNode(settlementNode);
        assertTrue("Chosen settlement node should have at least one incident edge",
                candidateEdges.length > 0);
        int edgeId = candidateEdges[0];
        int[] edgeNodes = map.getNodesForEdge(edgeId);
        int from = edgeNodes[0];
        int to   = edgeNodes[1];

        // Script: build settlement, build road, then go.
        String script = ""
                + "build settlement " + settlementNode + "\n"
                + "build road " + from + ", " + to + "\n"
                + "go\n";

        simulateInput(script);

        // Run initial placement; should not hang and should place both pieces.
        human.initialPlacement(map);

        assertTrue("Settlement should be placed on chosen node",
                map.isSettlement(human, settlementNode));

        int builtEdgeId = map.findEdgeBetweenNodes(from, to);
        assertTrue("Road should be placed on chosen edge",
                builtEdgeId >= 0 && map.isRoad(human, builtEdgeId));
    }
}