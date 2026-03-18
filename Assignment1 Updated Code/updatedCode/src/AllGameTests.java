import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite that runs all unit tests for the Catan simulator.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        AgentResourceTest.class,
        RoadTest.class,
        SettlementTest.class,
        CityTest.class
})
public class AllGameTests {
    // no code needed; annotations define the suite
}

