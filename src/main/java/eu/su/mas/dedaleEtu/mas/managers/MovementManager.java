package eu.su.mas.dedaleEtu.mas.managers;

import eu.su.mas.dedaleEtu.mas.agents.MyAgent;

import java.io.Serializable;
import java.util.List;

public class MovementManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private MyAgent agent;

    public MovementManager(MyAgent agent) {
        this.agent = agent;
    }

    public void setCurrentPathToClosestOpenNode() {
        String myNode = agent.getCurrentPosition().getLocationId();
        List<String> path = agent.getMyMap().getShortestPathToClosestOpenNode(myNode);
        agent.setCurrentPath(path);
        agent.setTargetNode(path.isEmpty() ? null : path.remove(0));
    }

    public void setCurrentPathToFarthestOpenNode() {
        String myNode = agent.getCurrentPosition().getLocationId();
        List<String> path = agent.getMyMap().getShortestPathToFarthestOpenNode(myNode);
        agent.setCurrentPath(path);
        agent.setTargetNode(path.isEmpty() ? null : path.remove(0));

    }

    public void handleDeadlock(List<String> nodesToAvoid) {
        String myNode = agent.getCurrentPosition().getLocationId();
        List<String> path = agent.getMyMap().getShortestPathToClosestNodeExclude(myNode, nodesToAvoid);
        agent.setCurrentPath(path);
        agent.setTargetNode(path.isEmpty() ? null : path.remove(0));
    }
}