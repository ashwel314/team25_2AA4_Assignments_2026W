import java.util.ArrayList;
import java.util.List;

public class SpendCardsCommand implements Command {
    private Agent agent;
    private List<Resources> discarded = new ArrayList<>(); // stores what was removed

    public SpendCardsCommand(Agent agent) {
        this.agent = agent;
    }

    @Override
    public void execute() {
        // Store discarded cards before removing them
        List<Resources> hand = agent.getHand(); // need to add getHand() to Agent
        int discardAmount = hand.size() / 2;
        for (int i = 0; i < discardAmount; i++) {
            Resources removed = hand.get(agent.getRandom().nextInt(hand.size()));
            discarded.add(removed); // remember what was removed
            agent.removeResource(removed, 1);
        }
    }

    @Override
    public void undo() {
        // Give back every card that was discarded
        for (Resources r : discarded) {
            agent.addResource(r, 1);
        }
        discarded.clear();
    }

    @Override
    public double getValue() { return 0.5; }

    @Override
    public String getDescription() { return "Spent cards"; }
}
