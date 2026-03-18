public class BuildRoadCommand implements Command {
    private Agent agent;
    private GameMap map;

    public BuildRoadCommand(Agent agent, GameMap map) {
        this.agent = agent;
        this.map = map;
    }

    @Override
    public void execute() {
        agent.buildRoad(map);
    }

    @Override
    public double getValue() {
        return 0.8;
    }

    @Override
    public String getDescription() {
        return "Built a road";
    }
}
