import org.junit.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Unit tests for {@link Game#readConfig(String)}.
 *
 * Covers:
 * - Normal values within range,
 * - Boundary values below 1 and above MAX_ROUNDS,
 * - Invalid configuration content.
 */
public class GameConfigTest {

    /**
     * Helper to create a temporary config file with the given contents.
     */
    private File writeConfigFile(String content) throws IOException {
        File temp = File.createTempFile("game-config", ".txt");
        try (FileWriter writer = new FileWriter(temp)) {
            writer.write(content);
        }
        temp.deleteOnExit();
        return temp;
    }

    @Test
    public void testReadConfigWithinBounds() throws IOException {
        File config = writeConfigFile("turns: 123\n");
        int turns = Game.readConfig(config.getAbsolutePath());
        assertEquals("Config value within bounds should be read as-is",
                123, turns);
    }

    @Test
    public void testReadConfigBelowMinimumIsClampedToOne() throws IOException {
        File config = writeConfigFile("turns: 0\n");
        int turns = Game.readConfig(config.getAbsolutePath());
        assertEquals("turns < 1 should be clamped to 1",
                1, turns);
    }

    @Test
    public void testReadConfigAboveMaximumIsClamped() throws IOException {
        File config = writeConfigFile("turns: " + (Game.MAX_ROUNDS + 100) + "\n");
        int turns = Game.readConfig(config.getAbsolutePath());
        assertEquals("turns > MAX_ROUNDS should be clamped to MAX_ROUNDS",
                Game.MAX_ROUNDS, turns);
    }

    @Test
    public void testReadConfigInvalidFileFallsBackToDefault() throws IOException {
        File config = writeConfigFile("not a valid config line\n");
        int turns = Game.readConfig(config.getAbsolutePath());
        assertEquals("Invalid config should fall back to MAX_ROUNDS",
                Game.MAX_ROUNDS, turns);
    }
}

