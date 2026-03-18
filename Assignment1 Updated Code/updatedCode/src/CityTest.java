import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for City.resourcePayment and related behavior.
 *
 * These tests include partition and boundary-style cases for wheat/ore counts.
 */
public class CityTest {

    private static final int DEFAULT_TIMEOUT = 2000;

    private City newCity() {
        // owner and location are not needed for resourcePayment here, so pass nulls
        return new City(null, null);
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void test_resourcePayment_city_exactCost_true() {
        City city = newCity();
        Resources[] payment = {
                Resources.WHEAT, Resources.WHEAT,
                Resources.ORE, Resources.ORE, Resources.ORE
        };

        assertTrue("exact city cost (2 WHEAT, 3 ORE) should be accepted",
                   city.resourcePayment(payment));
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void test_resourcePayment_city_insufficientWheat_false() {
        City city = newCity();
        Resources[] payment = {
                Resources.WHEAT,
                Resources.ORE, Resources.ORE, Resources.ORE
        };

        assertFalse("only 1 WHEAT should be rejected for city",
                    city.resourcePayment(payment));
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void test_resourcePayment_city_insufficientOre_false() {
        City city = newCity();
        Resources[] payment = {
                Resources.WHEAT, Resources.WHEAT,
                Resources.ORE, Resources.ORE
        };

        assertFalse("only 2 ORE should be rejected for city",
                    city.resourcePayment(payment));
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void test_getResourceMultiplier_and_getPoints() {
        City city = newCity();

        assertEquals("city resource multiplier should be 2",
                     2, city.getResourceAmount());
        assertEquals("city should grant 2 victory points",
                     2, city.getPoints());
    }
}

