import org.junit.*;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link Settlement}, {@link City}, and {@link Road}
 * focusing on their resource cost logic and simple getters.
 */
public class BuildingCostTest {

    @Test
    public void testSettlementResourcePaymentExactCost() {
        Agent agent = new Agent(0, 0);
        Node node = new Node(10);
        Settlement settlement = new Settlement(agent, node);

        Resources[] payment = {
                Resources.BRICK,
                Resources.WOOD,
                Resources.SHEEP,
                Resources.WHEAT
        };

        assertTrue("Exact settlement cost should be accepted",
                settlement.resourcePayment(payment));
    }

    @Test
    public void testSettlementResourcePaymentMissingResource() {
        Agent agent = new Agent(0, 0);
        Node node = new Node(11);
        Settlement settlement = new Settlement(agent, node);

        Resources[] payment = {
                Resources.BRICK,
                Resources.WOOD,
                Resources.WHEAT   // missing SHEEP
        };

        assertFalse("Missing SHEEP should not satisfy settlement cost",
                settlement.resourcePayment(payment));
    }

    @Test
    public void testCityResourcePayment() {
        Agent agent = new Agent(0, 0);
        Node node = new Node(12);
        City city = new City(agent, node);

        Resources[] enough = {
                Resources.WHEAT, Resources.WHEAT,
                Resources.ORE, Resources.ORE, Resources.ORE
        };
        assertTrue("2 WHEAT + 3 ORE should be accepted for a city",
                city.resourcePayment(enough));

        Resources[] notEnoughOre = {
                Resources.WHEAT, Resources.WHEAT,
                Resources.ORE, Resources.ORE
        };
        assertFalse("Only 2 ORE should not be enough for a city",
                city.resourcePayment(notEnoughOre));
    }

    @Test
    public void testRoadResourcePayment() {
        Agent agent = new Agent(0, 0);
        Edge edge = new Edge(5);
        Road road = new Road(agent, edge);

        Resources[] payment = { Resources.BRICK, Resources.WOOD };
        assertTrue("1 BRICK + 1 WOOD should be accepted for a road",
                road.resourcePayment(payment));

        Resources[] missingWood = { Resources.BRICK };
        assertFalse("Missing WOOD should not satisfy road cost",
                road.resourcePayment(missingWood));
    }

    @Test
    public void testSettlementAndCityPointsAndMultipliers() {
        Agent agent = new Agent(1, 0);

        Node node1 = new Node(1);
        Settlement settlement = new Settlement(agent, node1);
        assertEquals("Settlement gives 1 point", 1, settlement.getPoints());
        assertEquals("Settlement multiplier is 1", 1, settlement.getResourceMultiplier());

        Node node2 = new Node(2);
        City city = new City(agent, node2);
        assertEquals("City gives 2 points", 2, city.getPoints());
        assertEquals("City resource amount is 2", 2, city.getResourceAmount());
    }
}

