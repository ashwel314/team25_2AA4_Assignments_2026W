import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for Settlement.resourcePayment and related behavior.
 */
public class SettlementTest {

    private static final int DEFAULT_TIMEOUT = 2000;

    private Settlement newSettlement() {
        // owner and location are not needed for resourcePayment here, so pass nulls
        return new Settlement(null, null);
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void test_resourcePayment_settlement_exactCost_true() {
        Settlement settlement = newSettlement();
        Resources[] payment = {
                Resources.BRICK, Resources.WOOD,
                Resources.SHEEP, Resources.WHEAT
        };

        assertTrue("exact settlement cost should be accepted",
                   settlement.resourcePayment(payment));
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void test_resourcePayment_settlement_missingSheep_false() {
        Settlement settlement = newSettlement();
        Resources[] payment = {
                Resources.BRICK, Resources.WOOD,
                Resources.WHEAT
        };

        assertFalse("missing SHEEP should be rejected for settlement",
                    settlement.resourcePayment(payment));
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void test_resourcePayment_settlement_missingMultiple_false() {
        Settlement settlement = newSettlement();
        Resources[] payment = {
                Resources.BRICK, Resources.SHEEP
        };

        assertFalse("missing WOOD and WHEAT should be rejected",
                    settlement.resourcePayment(payment));
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void test_getResourceMultiplier_and_getPoints() {
        Settlement settlement = newSettlement();

        assertEquals("settlement resource multiplier should be 1",
                     1, settlement.getResourceMultiplier());
        assertEquals("settlement should grant 1 victory point",
                     1, settlement.getPoints());
    }
}

