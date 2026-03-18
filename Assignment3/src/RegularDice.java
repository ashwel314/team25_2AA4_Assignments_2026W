import java.util.Random;

/**
 * Represents a single standard die with a configurable number of sides.
 * Implements the Dice interface.
 */
public class RegularDice implements Dice {

    private static final Random random = new Random();

    /** Number of sides on this die. */
    private final int sides;

    /**
     * Constructor for RegularDice.
     * @param sides number of sides (e.g. 6 for a standard die)
     */
    public RegularDice(int sides) {
        this.sides = sides;
    }

    /**
     * Rolls this die, returning a value from 1 to sides inclusive.
     * @return roll result
     */
    @Override
    public int roll() {
        return random.nextInt(sides) + 1;
    }
}