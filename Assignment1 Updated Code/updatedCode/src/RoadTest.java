import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for Road.resourcePayment.
 *
 * These tests use partition testing over different combinations of
 * BRICK and WOOD resources.
 */
public class RoadTest {

    private static final int DEFAULT_TIMEOUT = 2000;

    private Road newRoad() {
        // owner and edge are not needed for resourcePayment, so pass null
        return new Road(null, null);
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void test_resourcePayment_road_exactCost_true() {
        Road road = newRoad();
        Resources[] payment = { Resources.BRICK, Resources.WOOD };

        assertTrue("exact cost (1 BRICK, 1 WOOD) should be accepted",
                   road.resourcePayment(payment));
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void test_resourcePayment_road_missingBrick_false() {
        Road road = newRoad();
        Resources[] payment = { Resources.WOOD };

        assertFalse("missing BRICK should be rejected",
                    road.resourcePayment(payment));
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void test_resourcePayment_road_missingWood_false() {
        Road road = newRoad();
        Resources[] payment = { Resources.BRICK };

        assertFalse("missing WOOD should be rejected",
                    road.resourcePayment(payment));
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void test_resourcePayment_road_extraResources_true() {
        Road road = newRoad();
        Resources[] payment = {
                Resources.BRICK, Resources.BRICK,
                Resources.WOOD, Resources.SHEEP
        };

        assertTrue("extra resources plus required BRICK and WOOD should be accepted",
                   road.resourcePayment(payment));
    }
}

