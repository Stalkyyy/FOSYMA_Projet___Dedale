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
    
    // Initalise les information de réservation pour un agent 
    public NodeReservation(String agentName, Set<String> reservedNodes, AgentBehaviourState state, boolean hasAccessibleNodes, long timestamp) {
        this.agentName = agentName;
        this.reservedNodes = reservedNodes;
        this.state = state;
        this.hasAccessibleNodes = hasAccessibleNodes;
        this.timestamp = System.currentTimeMillis();
    }

    // ====================================================================================

    // Retourne le nom de l'agent ayant réservé les nœuds.
    public String getAgentName() {
        return agentName;
    }

    // Définit le nom de l'agent ayant réservé les nœuds.
    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    // Retourne l'ensemble des nœuds réservés. 
    public Set<String> getReservedNodes() {
        return reservedNodes;
    }

    // Définit l'ensemble des nœuds réservés.
    public void setReservedNodes(Set<String> reservedNodes) {
        this.reservedNodes = reservedNodes;
    }

    // Retourne l'état actuel de l'agent.
    public AgentBehaviourState getState() {
        return state;
    }

    // Vérifie si l'agent a des nœuds accessibles.
    public boolean hasAccessibleNodes() {
        return hasAccessibleNodes;
    }

    // Définit si l'agent a des nœuds accessibles.
    public void setHasAccessibleNodes(boolean hasAccessibleNodes) {
        this.hasAccessibleNodes = hasAccessibleNodes;
    }

    // Définit l'état actuel de l'agent.
    public void setState(AgentBehaviourState state) {
        this.state = state;
    }

    // Retourne le timestamp de la réservation.
    public long getTimestamp() {
        return timestamp;
    }

    // Définit le timestamp de la réservation.
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
