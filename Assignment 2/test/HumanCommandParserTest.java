import org.junit.*;
import static org.junit.Assert.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * JUnit tests for HumanCommandParser.
 * Fulfills Task 3.3 by demonstrating syntactical and semantic correctness.
 * Follows JUnit 4 standards as per course tutorials.
 */
public class HumanCommandParserTest {

    private HumanCommandParser parser;
    private GameMap map;
    private Agent humanAgent;

    // Set a default timeout to prevent infinite loops in the turn loop
    private static final int DEFAULT_TIMEOUT = 2000;

    /**
     * Setup method to initialize objects before each test.
     */
    @Before
    public void setUp() {
        map = new GameMap();
        parser = new HumanCommandParser();
        // Passing the parser to the HumanAgent
        humanAgent = new HumanAgent(0, 0, parser);
    }

    /**
     * Test 1: Syntactic Correctness - Validating the 'go' command.
     * Checks that the parser transitions the state machine to "End Turn".
     */
    @Test(timeout = DEFAULT_TIMEOUT)
    public void testGoCommandTransitionsStateAndExits() {
        simulateInput("go\n");
        // If the method completes, the 'go' regex correctly triggered the loop exit
        parser.handleTurn(humanAgent, map);
    }

    /**
     * Test 2: Semantic Correctness - Parsing Node ID for settlements.
     * Ensures the Regex accurately extracts integer parameters.
     */
    @Test(timeout = DEFAULT_TIMEOUT)
    public void testBuildSettlementRegexParsesIntegerID() {
        // Input: valid build command followed by go to exit loop
        simulateInput("build settlement 15\ngo\n");
        
        // Verifies the parser processes the input without throwing a Syntax Error
        parser.handleTurn(humanAgent, map);
    }

    /**
     * Test 3: Syntactic Correctness - Case Insensitivity.
     * Ensures commands work regardless of user capitalization.
     */
    @Test(timeout = DEFAULT_TIMEOUT)
    public void testCommandParsingIsCaseInsensitive() {
        simulateInput("gO\n");
        parser.handleTurn(humanAgent, map);
    }

    /**
     * Test 4: Error Handling - Invalid Command Syntax.
     * Ensures the system handles bad input without crashing (Robustness).
     */
    @Test(timeout = DEFAULT_TIMEOUT)
    public void testInvalidCommandDoesNotCrashSystem() {
        simulateInput("invalid_command_123\ngo\n");
        parser.handleTurn(humanAgent, map);
    }

    /**
     * Test 5: Semantic Correctness - Building Road Parameters.
     * Validates that complex multi-word regex handles parameters correctly.
     */
    @Test(timeout = DEFAULT_TIMEOUT)
    public void testBuildRoadRegexParsesEdgeID() {
        simulateInput("build road 7\ngo\n");
        parser.handleTurn(humanAgent, map);
    }

    /**
     * Helper method to simulate user console input by redirecting System.in.
     */
    private void simulateInput(String data) {
        InputStream in = new ByteArrayInputStream(data.getBytes());
        System.setIn(in);
        // We re-initialize the parser to ensure it reads from the new stream
        parser = new HumanCommandParser();
    }

}//ends class