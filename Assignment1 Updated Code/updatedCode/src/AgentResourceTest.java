import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Map;

/**
 * Unit tests for the Agent resource hand.
 *
 * These tests are written so that they pass both before and after
 * refactoring the Agent's internal hand representation (Map vs ArrayList).
 */
public class AgentResourceTest {

    private static final int DEFAULT_TIMEOUT = 2000;

    private Agent newAgent() {
        return new Agent(1, 0);
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void test_addResource_increasesHandAndMap() {
        Agent agent = newAgent();

        assertEquals("initial hand size should be 0", 0, agent.handSize());
        Map<Resources, Integer> before = agent.getResourceMap();
        assertEquals("initial WOOD count should be 0",
                     Integer.valueOf(0), before.get(Resources.WOOD));

        agent.addResource(Resources.WOOD, 3);

        assertEquals("hand size after adding 3 WOOD", 3, agent.handSize());
        Map<Resources, Integer> after = agent.getResourceMap();
        assertEquals("WOOD count after adding 3",
                     Integer.valueOf(3), after.get(Resources.WOOD));
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void test_addResource_ignoresDesert() {
        Agent agent = newAgent();

        agent.addResource(Resources.DESERT, 5);

        assertEquals("DESERT should not be added to hand", 0, agent.handSize());
        Map<Resources, Integer> map = agent.getResourceMap();
        assertNull("DESERT should not appear in resource map",
                   map.get(Resources.DESERT));
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void test_removeResource_reducesCount() {
        Agent agent = newAgent();
        agent.addResource(Resources.ORE, 2);

        agent.removeResource(Resources.ORE, 1);

        assertEquals("hand size after adding 2 ORE then removing 1", 1, agent.handSize());
        Map<Resources, Integer> map = agent.getResourceMap();
        assertEquals("ORE count after removing 1 from 2",
                     Integer.valueOf(1), map.get(Resources.ORE));
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void test_isSevenCards_boundaryAt7And8() {
        Agent agent = newAgent();

        agent.addResource(Resources.WOOD, 7);
        assertFalse("exactly 7 cards should NOT trigger isSevenCards", agent.isSevenCards());

        agent.addResource(Resources.WOOD, 1);
        assertTrue("more than 7 cards should trigger isSevenCards", agent.isSevenCards());
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void test_checkRoadCost_exactCost_true() {
        Agent agent = newAgent();
        agent.addResource(Resources.BRICK, 1);
        agent.addResource(Resources.WOOD, 1);

        assertTrue("agent with 1 BRICK and 1 WOOD can afford a road",
                   agent.checkRoadCost());
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void test_checkRoadCost_missingBrick_false() {
        Agent agent = newAgent();
        agent.addResource(Resources.WOOD, 1);

        assertFalse("agent missing BRICK cannot afford a road",
                    agent.checkRoadCost());
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void test_checkRoadCost_missingWood_false() {
        Agent agent = newAgent();
        agent.addResource(Resources.BRICK, 1);

        assertFalse("agent missing WOOD cannot afford a road",
                    agent.checkRoadCost());
    }
}

