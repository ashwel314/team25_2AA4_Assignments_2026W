public class BuildSettlementCommand implements Command {
    private Agent agent;
    private GameMap map;

    public BuildSettlementCommand(Agent agent, GameMap map) {
        this.agent = agent;
        this.map = map;
    }

    @Override
    public void execute() {
        agent.buildSettlement(map);
    }

    @Override
    public double getValue() {
        return 1.0;
    }

    @Override
    public String getDescription() {
        return "Built a Settlement";
    }
}
