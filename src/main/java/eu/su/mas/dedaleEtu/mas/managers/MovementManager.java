package eu.su.mas.dedaleEtu.mas.managers;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;

import java.io.Serializable;
import java.util.List;

public class MovementManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private AbstractAgent agent;

    public MovementManager(AbstractAgent agent) {
        this.agent = agent;
    }

    public void setCurrentPathToClosestOpenNode() {
        String myNode = agent.getCurrentPosition().getLocationId();
        List<String> path = agent.getMyMap().getShortestPathToClosestOpenNode(myNode);
        agent.setCurrentPath(path);
        agent.setTargetNode(path.isEmpty() ? null : path.remove(0));
    }

    public void setCurrentPathTo(String nodeId) {
        String myNode = agent.getCurrentPosition().getLocationId();
        List<String> path = agent.getMyMap().getShortestPath(myNode, nodeId);
        agent.setCurrentPath(path);
        agent.setTargetNode(path.isEmpty() ? null : path.remove(0));
    }

    public void setCurrentPathToFarthestOpenNode() {
        String myNode = agent.getCurrentPosition().getLocationId();
        List<String> path = agent.getMyMap().getShortestPathToFarthestOpenNode(myNode);
        agent.setCurrentPath(path);
        agent.setTargetNode(path.isEmpty() ? null : path.remove(0));
    }

    public void setCurrentPathToRandomNode() {
        String nodeId = agent.getMyMap().getRandomNode();
        setCurrentPathTo(nodeId);
    }
}