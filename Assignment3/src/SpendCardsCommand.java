public class SpendCardsCommand implements Command{
    private Agent agent;

    public SpendCardsCommand(Agent agent) {
        this.agent = agent;
    }

    @Override
    public void execute() { agent.halfHand(); }

    @Override
    public double getValue() { return 0.5; }

    @Override
    public String getDescription() { return "Spent cards"; }
}
