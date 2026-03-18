import java.util.HashMap;
import java.util.Map;

public class BuildCityCommand implements Command {
    
    private final Agent agent;
    private final GameMap map;
    private final int nodeId;
    private final Map<Resources, Integer> cost;

    public BuildCityCommand(Agent agent, GameMap map, int nodeId) {
        this.agent = agent;
        this.map = map;
        this.nodeId = nodeId;
        this.cost = new HashMap<>();
        this.cost.put(Resources.WHEAT, 2);
        this.cost.put(Resources.ORE, 3);
    }
  
    public BuildCityCommand(Agent agent, GameMap map) {
        this.agent = agent;
        this.map = map;

    @Override
    public void execute() {
        for (Map.Entry<Resources, Integer> entry : cost.entrySet()) {
            agent.removeResource(entry.getKey(), entry.getValue());
        }
        map.placeCity(agent, nodeId);
        agent.recordCityPlaced(); 
    }
   
    @Override
    public double getValue() {
        return 1.0;
    }

    @Override
    public String getDescription() {
        return "Built a city";
    }

    @Override
    public void undo() {
        map.getNode(nodeId).setBuilding(new Settlement(agent, map.getNode(nodeId)));
        
        for (Map.Entry<Resources, Integer> entry : cost.entrySet()) {
            agent.addResource(entry.getKey(), entry.getValue());
        }
        
        agent.addPoints(-1); 
        agent.citiesRemaining++; 
        agent.settlementsRemaining--; 
    }
}

