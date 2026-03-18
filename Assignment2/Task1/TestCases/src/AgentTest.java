import org.junit.*;
import static org.junit.Assert.*;

/**
 * Unit tests for the {@link Agent} class.
 *
 * Includes:
 * - Boundary testing for the "7 cards" rule (R1.8).
 * - Partition testing for resource affordability checks.
 */
public class AgentTest {

    /**
     * Boundary test for the 7-card rule.
     * Hand size == 7 should NOT trigger isSevenCards,
     * hand size == 8 should.
     */
    @Test
    public void testIsSevenCardsBoundary() {
        Agent agent = new Agent(0, 0);

        // Hand size 7 -> isSevenCards should be false
        for (int i = 0; i < 7; i++) {
            agent.addResource(Resources.BRICK, 1);
        }
        assertEquals("Hand size should be 7", 7, agent.handSize());
        assertFalse("Boundary hand size 7 should not trigger forced build", agent.isSevenCards());

        // Hand size 8 -> isSevenCards should be true
        agent.addResource(Resources.WOOD, 1);
        assertEquals("Hand size should be 8", 8, agent.handSize());
        assertTrue("Hand size > 7 should trigger forced build", agent.isSevenCards());
    }

    /**
     * Partition test for road affordability.
     *
     * Partitions:
     *  - exactly enough resources,
     *  - missing one required resource,
     *  - strictly more than required resources.
     */
    @Test
    public void testCheckRoadCostPartition() {
        // Exactly enough resources
        Agent agentExact = new Agent(1, 0);
        agentExact.addResource(Resources.BRICK, 1);
        agentExact.addResource(Resources.WOOD, 1);
        assertTrue("Exactly 1 BRICK + 1 WOOD should be affordable",
                agentExact.checkRoadCost());

        // Missing one required resource (no WOOD)
        Agent agentMissing = new Agent(2, 0);
        agentMissing.addResource(Resources.BRICK, 1);
        assertFalse("Missing WOOD should not be affordable",
                agentMissing.checkRoadCost());

        // More than required resources
        Agent agentExtra = new Agent(3, 0);
        agentExtra.addResource(Resources.BRICK, 2);
        agentExtra.addResource(Resources.WOOD, 2);
        agentExtra.addResource(Resources.SHEEP, 5);
        assertTrue("Having extra resources should still be affordable",
                agentExtra.checkRoadCost());
    }

    /**
     * Basic check that city costs are enforced:
     * needs at least 2 WHEAT and 3 ORE.
     */
    @Test
    public void testCheckCityCost() {
        Agent rich = new Agent(4, 0);
        rich.addResource(Resources.WHEAT, 2);
        rich.addResource(Resources.ORE, 3);
        assertTrue("2 WHEAT + 3 ORE should be affordable for a city",
                rich.checkCityCost());

        Agent poor = new Agent(5, 0);
        poor.addResource(Resources.WHEAT, 2);
        poor.addResource(Resources.ORE, 2);
        assertFalse("Only 2 ORE should not be enough for a city",
                poor.checkCityCost());
    }
}

