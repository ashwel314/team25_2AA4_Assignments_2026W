/**
 * Interface for all dice in the Catan simulator.
 * Part of the composition pattern: RegularDice implements a single die,
 * MultiDice composes multiple Dice to roll them together.
 */
public interface Dice {
    /**
     * Rolls the die/dice and returns the total result.
     * @return the result of the roll
     */
    public int roll();
}