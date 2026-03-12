import org.junit.*;
import static org.junit.Assert.*;

/**
 * JUnit tests for CommandParser.
 * Focus on syntactic + semantic correctness of parsed commands and IDs.
 */
public class CommandParserTest {

    private static final int DEFAULT_TIMEOUT = 2000;

    private CommandParser parser;

    @Before
    public void setUp() {
        parser = new CommandParser();
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testRollCommandParsedCorrectly() {
        CommandType type = parser.parser("roll");
        assertEquals("roll should be parsed as ROLL", CommandType.ROLL, type);
        assertEquals("nodeId should be reset", -1, parser.getNodeId());
        assertEquals("fromNodeId should be reset", -1, parser.getFromNodeId());
        assertEquals("toNodeId should be reset", -1, parser.getToNodeId());
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testGoCommandCaseInsensitive() {
        CommandType type = parser.parser("Go");
        assertEquals("Go should be parsed as GO", CommandType.GO, type);
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testBuildSettlementParsesPlainId() {
        CommandType type = parser.parser("build settlement 17");
        assertEquals("build settlement should be parsed as BUILD_SETTLEMENT",
                CommandType.BUILD_SETTLEMENT, type);
        assertEquals("node id should be 17", 17, parser.getNodeId());
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testBuildSettlementParsesBracketedId() {
        CommandType type = parser.parser("build settlement [23]");
        assertEquals("bracketed settlement should still be BUILD_SETTLEMENT",
                CommandType.BUILD_SETTLEMENT, type);
        assertEquals("node id should be 23", 23, parser.getNodeId());
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testBuildRoadParsesCommaSeparatedIds() {
        CommandType type = parser.parser("build road 9, 10");
        assertEquals("build road should be parsed as BUILD_ROAD",
                CommandType.BUILD_ROAD, type);
        assertEquals("from node should be 9", 9, parser.getFromNodeId());
        assertEquals("to node should be 10", 10, parser.getToNodeId());
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testBuildRoadParsesBracketedIds() {
        CommandType type = parser.parser("build road [9,10]");
        assertEquals("bracketed road should still be BUILD_ROAD",
                CommandType.BUILD_ROAD, type);
        assertEquals("from node should be 9", 9, parser.getFromNodeId());
        assertEquals("to node should be 10", 10, parser.getToNodeId());
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testInvalidCommandReturnsInvalidType() {
        CommandType type = parser.parser("foo bar baz");
        assertEquals("unknown commands should be INVALID",
                CommandType.INVALID, type);
        assertEquals(-1, parser.getNodeId());
        assertEquals(-1, parser.getFromNodeId());
        assertEquals(-1, parser.getToNodeId());
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testNullInputIsHandledSafely() {
        CommandType type = parser.parser(null);
        assertEquals("null input should be INVALID",
                CommandType.INVALID, type);
    }
}