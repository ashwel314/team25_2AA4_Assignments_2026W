import org.junit.*;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link RegularDice} and {@link MultiDice}.
 */
public class DiceTest {

    /**
     * Simple fixed dice implementation used to make {@link MultiDice}
     * behaviour deterministic in tests.
     */
    private static class FixedDice implements Dice {
        private final int value;

        FixedDice(int value) {
            this.value = value;
        }

        @Override
        public int roll() {
            return value;
        }
    }

    /**
     * RegularDice should always return a value in [1, sides].
     */
    @Test
    public void testRegularDiceRollWithinBounds() {
        int sides = 6;
        RegularDice dice = new RegularDice(sides);

        for (int i = 0; i < 1000; i++) {
            int roll = dice.roll();
            assertTrue("Roll should be >= 1", roll >= 1);
            assertTrue("Roll should be <= number of sides", roll <= sides);
        }
    }

    /**
     * MultiDice should sum the results of its component dice.
     */
    @Test
    public void testMultiDiceRollsSumOfComponents() {
        MultiDice multi = new MultiDice();
        multi.addDice(new FixedDice(3));
        multi.addDice(new FixedDice(4));

        int roll = multi.roll();
        assertEquals("MultiDice should sum component dice", 7, roll);
    }
}

