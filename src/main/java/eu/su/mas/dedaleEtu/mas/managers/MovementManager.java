package eu.su.mas.dedaleEtu.mas.managers;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovementManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private AbstractAgent agent;

    protected Map<String, Integer> timeUntilNextMoveRequest;
    public final int minTime = 15;

    public final int maxFailedMoveCount = 3;
    private int failedMoveCount = 0;

    public MovementManager(AbstractAgent agent) {
        this.agent = agent;

        timeUntilNextMoveRequest = new HashMap<>();
        for(String agentName : agent.getListAgentNames())
            timeUntilNextMoveRequest.put(agentName, minTime + 1);
    }

    // ==================================================================================

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

    public void setCurrentPathToExcluding(String nodeId) {
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


    public void setCurrentPathTo(List<String> path) {
        agent.setCurrentPath(path);
        agent.setTargetNode(path.isEmpty() ? null : path.remove(0));
    }

    public void setCurrentPathToRandomNode() {
        String nodeId = agent.getMyMap().getRandomNode(agent.getCurrentPosition().getLocationId());
        setCurrentPathTo(nodeId);
    }

    // ==================================================================================

    public boolean shouldInitiateDeadlock(String agentName, String agentPosition) {
        if (agentPosition == null || agentName == null || agent.getTargetNode() == null)
            return false;

        boolean iSeeMyPosition = agentPosition.compareTo(agent.getTargetNode()) == 0;
        boolean confirmationDeadlock = this.failedMoveCount > this.maxFailedMoveCount;

        return iSeeMyPosition && confirmationDeadlock;
    }

    public void incrementFailedMoveCount() {
        this.failedMoveCount++;
    }

    public void resetFailedMoveCount() {
        this.failedMoveCount = 0;
    }

    public int getFailedMoveCount() {
        return this.failedMoveCount;
    }

    public void incrementeTimeDeadlock() {
        for (Map.Entry<String, Integer> entry : this.timeUntilNextMoveRequest.entrySet()) {
            String agentName = entry.getKey();
            int time = entry.getValue();
            this.timeUntilNextMoveRequest.put(agentName, time + 1);
        }
    }

    public void resetTimeDeadlock(String agentName) {
        this.timeUntilNextMoveRequest.put(agentName, 0);
    }

    public int getTimeToWait(String agentName) {
        return this.timeUntilNextMoveRequest.get(agentName);
    }
}