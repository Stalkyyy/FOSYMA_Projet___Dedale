package eu.su.mas.dedaleEtu.mas.managers;

import java.io.Serializable;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentType;
import eu.su.mas.dedaleEtu.mas.knowledge.TreasureObservations;
import eu.su.mas.dedaleEtu.mas.knowledge.given_knowledge.MapRepresentation.MapAttribute;

public class OtherAgentsKnowledgeManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private AbstractAgent agent;

    public OtherAgentsKnowledgeManager(AbstractAgent agent) {
        this.agent = agent;
    }



    public boolean shouldInitiateCommunication(String agentName) {
        return isCharacteristicsShareable(agentName) || isTopologyShareable(agentName) || isTreasureShareable(agentName);
    }



    /*
     * Caractéristiques des autres agents à priori
     */

    public void updateCharacteristics(String agentName, AgentType type, Observation treasureType, int space, int lockpick, int strength) {
        agent.getOtherAgentsCharacteristics().updateCharacteristics(agentName, type, treasureType, space, lockpick, strength);
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



    /*
    * Trésors des autres agents à priori
    */

    public boolean isTreasureShareable(String agentName) {
        int minUpdatesToShare = agent.getOtherAgentsTreasures().getMinUpdatesToShare();
        int pendingUpdatesCount = agent.getOtherAgentsTreasures().getPendingUpdatesCountOf(agentName);

        return pendingUpdatesCount >= minUpdatesToShare;
    }

    public void incrementeLastUpdates_treasure() {
        agent.getOtherAgentsTreasures().incrementeLastUpdates();
    }

    public void resetLastUpdateAgent_treasure(String agentName) {
        agent.getOtherAgentsTreasures().resetLastUpdatesAgent(agentName);
    }

    public void updateTreasures(String agentName, TreasureObservations obs) {
        agent.getOtherAgentsTreasures().updateTreasures(agentName, obs);
    }
      
    public TreasureObservations getTreasures(String agentName) {
        return agent.getOtherAgentsTreasures().getTreasures(agentName);
    }

    public TreasureObservations getTreasuresDifferenceWith(String agentName) {
        TreasureObservations obs1 = agent.getMyTreasures();
        TreasureObservations obs2 = getTreasures(agentName);
        return agent.treasureMgr.difference(obs1, obs2);
    }

    public void mergeTreasuresOf(String agentName, TreasureObservations obs2) {
        TreasureObservations mergedObs = agent.treasureMgr.merge(getTreasures(agentName), obs2);
        updateTreasures(agentName, mergedObs);
    }
}
