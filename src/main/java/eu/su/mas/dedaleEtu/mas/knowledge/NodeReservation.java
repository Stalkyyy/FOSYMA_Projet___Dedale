package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.Set;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentBehaviourState;

public class NodeReservation implements Serializable {
    
    private static final long serialVersionUID = -1333959882640838272L;

    private String agentName;
    private Set<String> reservedNodes;
    private AgentBehaviourState state;
    private boolean hasAccessibleNodes;
    private long timestamp;

    public NodeReservation(String agentName, Set<String> reservedNodes, AgentBehaviourState state, boolean hasAccessibleNodes, long timestamp) {
        this.agentName = agentName;
        this.reservedNodes = reservedNodes;
        this.state = state;
        this.hasAccessibleNodes = hasAccessibleNodes;
        this.timestamp = System.currentTimeMillis();
    }

    // ====================================================================================

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public Set<String> getReservedNodes() {
        return reservedNodes;
    }

    public void setReservedNodes(Set<String> reservedNodes) {
        this.reservedNodes = reservedNodes;
    }

    public AgentBehaviourState getState() {
        return state;
    }

    public boolean hasAccessibleNodes() {
        return hasAccessibleNodes;
    }

    public void setHasAccessibleNodes(boolean hasAccessibleNodes) {
        this.hasAccessibleNodes = hasAccessibleNodes;
    }

    public void setState(AgentBehaviourState state) {
        this.state = state;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
