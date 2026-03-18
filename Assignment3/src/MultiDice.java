import java.util.ArrayList;
import java.util.List;

/**
 * Composes multiple Dice objects and returns their combined total.
 * Implements the composition pattern for the Dice interface.
 *
 * Usage: add two RegularDice(6) instances to simulate standard Catan two-dice rolls.
 */
public class MultiDice implements Dice {

    /** The list of composed dice. */
    private List<Dice> diceList;

    /** Default constructor. */
    public MultiDice() {
        this.diceList = new ArrayList<>();
    }

    /**
     * Adds a die to this multi-dice.
     * @param d the Dice to add
     */
    public void addDice(Dice d) {
        diceList.add(d);
    }

    /**
     * Rolls all composed dice and returns the sum.
     * @return combined roll total
     */
    @Override
    public int roll() {
        int total = 0;
        for (Dice d : diceList) {
            total += d.roll();
        }
        return total;
    }
}