public class BuildCityCommand implements Command {
    private Agent agent;
    private GameMap map;
    public BuildCityCommand(Agent agent, GameMap map) {
        this.agent = agent;
        this.map = map;
    }

    @Override
    public void execute() {
        agent.buildCity(map);
    }

    @Override
    public double getValue() {
        return 1.0;
    }

    @Override
    public String getDescription() {
        return "Built a city";
    }
}
