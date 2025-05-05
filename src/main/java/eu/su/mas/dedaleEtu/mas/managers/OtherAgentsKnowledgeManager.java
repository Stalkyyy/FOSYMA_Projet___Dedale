package eu.su.mas.dedaleEtu.mas.managers;

import java.io.Serializable;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentBehaviourState;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentType;
import eu.su.mas.dedaleEtu.mas.knowledge.given_knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.msgObjects.CharacteristicsFloodMessage;

public class OtherAgentsKnowledgeManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private AbstractAgent agent;

    public OtherAgentsKnowledgeManager(AbstractAgent agent) {
        this.agent = agent;
    }



    public boolean shouldInitiateCommunication(String agentName) {

        if (agent.getBehaviourState() == AgentBehaviourState.EXPLORATION) {
            return isTopologyShareable(agentName) || isCharacteristicsShareable(agentName);
        }

        else if (agent.getBehaviourState() == AgentBehaviourState.FLOODING) {
            return !agent.floodMgr.hasContactedAgent(agentName);
        }

        else {
            return false;
        }
    }



    /*
     * Caractéristiques des autres agents à priori
     */

    public void updateCharacteristics(String agentName, AgentType type, Observation treasureType, int space, int lockpick, int strength) {
        agent.getOtherAgentsCharacteristics().updateCharacteristics(agentName, type, treasureType, space, lockpick, strength);
    }

    public void updateCharacteristics(CharacteristicsFloodMessage CFM) {
        agent.getOtherAgentsCharacteristics().updateCharacteristics(CFM);
    }

    public AgentType getAgentType(String agentName) {
        return agent.getOtherAgentsCharacteristics().getAgentType(agentName);
    }

    public Observation getTreasureType(String agentName) {
        return agent.getOtherAgentsCharacteristics().getTreasureType(agentName);
    }

    public int getSpace(String agentName) {
        return agent.getOtherAgentsCharacteristics().getSpace(agentName);
    }

    public int getLockpick(String agentName) {
        return agent.getOtherAgentsCharacteristics().getLockpick(agentName);
    }

    public int getStrength(String agentName) {
        return agent.getOtherAgentsCharacteristics().getStrength(agentName);
    }

    public boolean isCharacteristicsShareable(String agentName) {
        return !agent.getOtherAgentsCharacteristics().hasSharedCharacteristicsTo(agentName);
    }

    public void markSharedCharacteristicsTo(String agentName) {
        agent.getOtherAgentsCharacteristics().markSharedCharacteristicsTo(agentName);
    }

    

    /*
     * Topologies des autres agents à priori (+ partage !)
     */

    public void incrementeLastUpdates_topology() {
        agent.getOtherAgentsTopology().incrementeLastUpdates();
    }

    public void updateTopology(String agentName, SerializableSimpleGraph<String, MapAttribute> topology) {
        agent.getOtherAgentsTopology().updateTopology(agentName, topology);
    }

    public SerializableSimpleGraph<String, MapAttribute> getTopology(String agentName) {
        return agent.getOtherAgentsTopology().getTopology(agentName);
    }

    public SerializableSimpleGraph<String, MapAttribute> getTopologyDifferenceWith(String agentName) {
        SerializableSimpleGraph<String, MapAttribute> tp1 = agent.getMyMap().getSerializableGraph();
        SerializableSimpleGraph<String, MapAttribute> tp2 = getTopology(agentName);
        return agent.topoMgr.diffTopology(tp1, tp2);
    }

    public void mergeTopologyOf(String agentName, SerializableSimpleGraph<String, MapAttribute> tp2) {
        SerializableSimpleGraph<String, MapAttribute> mergedTopo = agent.topoMgr.mergeTopologies(getTopology(agentName), tp2);
        updateTopology(agentName, mergedTopo);
    }

    public boolean isTopologyShareable(String agentName) {
        int minUpdatesToShare = agent.getOtherAgentsTopology().getMinUpdatesToShare();
        int pendingUpdatesCount = agent.getOtherAgentsTopology().getPendingUpdatesCountOf(agentName);
        boolean hasFinishedExplo = agent.getOtherAgentsTopology().hasFinishedExplo(agentName);

        boolean cond_1 = !agent.getExplorationComplete() && pendingUpdatesCount >= minUpdatesToShare && !hasFinishedExplo;
        boolean cond_2 = agent.getExplorationComplete() && !hasFinishedExplo;

        return cond_1 || cond_2;
    }

    public void resetLastUpdateAgent_topology(String agentName) {
        agent.getOtherAgentsTopology().resetLastUpdatesAgent(agentName);
    }

    public void markExplorationComplete(String agentName) {
        agent.getOtherAgentsTopology().markExplorationComplete(agentName);
    }
}
