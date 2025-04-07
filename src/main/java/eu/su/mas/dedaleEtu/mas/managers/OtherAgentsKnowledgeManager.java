package eu.su.mas.dedaleEtu.mas.managers;

import java.io.Serializable;
import java.util.Set;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.NodeObservations;
import eu.su.mas.dedaleEtu.mas.knowledge.given_knowledge.MapRepresentation.MapAttribute;

public class OtherAgentsKnowledgeManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private AbstractAgent agent;

    public OtherAgentsKnowledgeManager(AbstractAgent agent) {
        this.agent = agent;
    }



    public boolean shouldInitiateCommunication(String agentName) {
        return isCharacteristicsShareable(agentName) || isTopologyShareable(agentName);
    }

    /*
     * Caractéristiques des autres agents à priori
     */

    public void updateCharacteristics(String agentName, Set<Couple<Observation, Integer>> expertise, Observation treasureType) {
        agent.getOtherAgentsCharacteristics().updateCharacteristics(agentName, expertise, treasureType);
    }

    public Set<Couple<Observation, Integer>> getExpertise(String agentName) {
        return agent.getOtherAgentsCharacteristics().getExpertise(agentName);
    }

    public Observation getTreasureType(String agentName) {
        return agent.getOtherAgentsCharacteristics().getTreasureType(agentName);
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

    public void resetLastUpdateAgent(String agentName) {
        agent.getOtherAgentsTopology().resetLastUpdatesAgent(agentName);
    }

    public void markExplorationComplete(String agentName) {
        agent.getOtherAgentsTopology().markExplorationComplete(agentName);
    }



    /*
    * Observations des autres agents à priori
    */

    public void updateObservations(String agentName, NodeObservations obs) {
        agent.getOtherAgentsObservations().updateObservations(agentName, obs);
    }
      
    public NodeObservations getObservations(String agentName) {
        return agent.getOtherAgentsObservations().getObservations(agentName);
    }

    public NodeObservations getObservationsDifferenceWith(String agentName) {
        NodeObservations obs1 = agent.getMyObservations();
        NodeObservations obs2 = getObservations(agentName);
        return agent.obsMgr.diffObservations(obs1, obs2);
    }

    public void mergeObservationOf(String agentName, NodeObservations obs2) {
        NodeObservations mergedObs = agent.obsMgr.mergeObservations(getObservations(agentName), obs2);
        updateObservations(agentName, mergedObs);
    }
}
