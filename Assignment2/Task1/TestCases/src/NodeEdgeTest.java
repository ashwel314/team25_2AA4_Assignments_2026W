import org.junit.*;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link Node} and {@link Edge} occupancy behaviour.
 */
public class NodeEdgeTest {

    @Test
    public void testNodeOccupancy() {
        Node node = new Node(10);
        assertFalse("New node should be unoccupied", node.isOccupied());
        assertNull("New node should have no building", node.getBuilding());
        assertEquals("Node id should be preserved", 10, node.getId());

        Agent agent = new Agent(0, 0);
        Settlement settlement = new Settlement(agent, node);
        node.setBuilding(settlement);

        assertTrue("Node with building should be occupied", node.isOccupied());
        assertEquals("getBuilding should return the placed building",
                settlement, node.getBuilding());
    }

    @Test
    public void testEdgeOccupancy() {
        Edge edge = new Edge(3);
        assertFalse("New edge should be unoccupied", edge.isOccupied());
        assertNull("New edge should have no road", edge.getRoad());
        assertEquals("Edge id should be preserved", 3, edge.getId());

        Agent agent = new Agent(1, 0);
        Road road = new Road(agent, edge);
        edge.setRoad(road);

        assertTrue("Edge with road should be occupied", edge.isOccupied());
        assertEquals("getRoad should return the placed road", road, edge.getRoad());
        assertEquals("Road should expose the same edge id", 3, road.getEdgeId());
    }
}

