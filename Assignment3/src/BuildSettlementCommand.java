import java.util.HashMap;
import java.util.Map;

public class BuildSettlementCommand implements Command{
    
    private final Agent agent;
    private final GameMap map;
    private int nodeId;
    private Map<Resources, Integer> cost;

    public BuildSettlementCommand(Agent agent, GameMap map, int nodeId){
        this.agent = agent;
        this.map = map;
        this.nodeId = nodeId;

        // Settlement cost: 1 BRICK, 1 WOOD, 1 SHEEP, 1 WHEAT
        this.cost = new HashMap<>();
        this.cost.put(Resources.BRICK,1);
        this.cost.put(Resources.WOOD, 1);
        this.cost.put(Resources.SHEEP, 1);
        this.cost.put(Resources.WHEAT, 1);
    }
  
    public BuildSettlementCommand(Agent agent, GameMap map) {
        this.agent = agent;
        this.map = map;
    }

    @Override
    public double getValue() {
        return 1.0;
    }

    @Override
    public String getDescription() {
        return "Built a Settlement";
    }

    @Override
    public void execute() {
        for (Map.Entry<Resources, Integer> entry : cost.entrySet()) {
            agent.removeResource(entry.getKey(), entry.getValue());
        }
        map.placeSettlement(agent, nodeId);
        agent.recordSettlementPlaced();
    }

    @Override
    public void undo() {
        map.getNode(nodeId).setBuilding(null);         
        for (Map.Entry<Resources, Integer> entry : cost.entrySet()) {
            agent.addResource(entry.getKey(), entry.getValue());
        }
        agent.addPoints(-1);
        agent.settlementsRemaining++; 
    }

}
