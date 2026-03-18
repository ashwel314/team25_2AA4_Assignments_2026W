import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Master JUnit test suite for the Catan simulator.
 *
 * Run this class to execute all unit tests added for Assignment 2 Task 1.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        AgentTest.class,
        BuildingCostTest.class,
        DiceTest.class,
        NodeEdgeTest.class,
        GameConfigTest.class,
        GameMapTest.class
})
public class AllGameTests {
    // Suite definition only – no implementation needed.
}

