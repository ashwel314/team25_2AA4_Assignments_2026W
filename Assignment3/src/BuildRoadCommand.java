import java.util.HashMap;
import java.util.Map;

public class BuildRoadCommand implements Command{
    
    private final Agent agent;
    private final GameMap map;
    private int edgeId;
    private Map<Resources, Integer> cost;

    public BuildRoadCommand(Agent agent, GameMap map, int edgeId){
        this.agent = agent;
        this.map = map;
        this.edgeId = edgeId;

        //Cost from Agent.java: 1 Brick, 1 Wood
        this.cost = new HashMap<>();
        this.cost.put(Resources.BRICK, 1);
        this.cost.put(Resources.WOOD, 1);
    }
  
    public BuildRoadCommand(Agent agent, GameMap map) {
        this.agent = agent;
        this.map = map;
    }

    @Override
    public double getValue() {
        return 0.8;
    }

    @Override
    public String getDescription() {
        return "Built a road";
    }

    @Override
    public void execute(){
        for (Map.Entry<Resources, Integer> entry : cost.entrySet()) {
            agent.removeResource(entry.getKey(), entry.getValue());
        }
        map.placeRoad(agent, edgeId);
        agent.recordRoadPlaced();
    }

    @Override 
    public void undo(){ 
        map.getEdge(edgeId).setRoad(null);         
        for (Map.Entry<Resources, Integer> entry : cost.entrySet()) {
            agent.addResource(entry.getKey(), entry.getValue());
        }
        agent.roadsRemaining++;
    }
}
