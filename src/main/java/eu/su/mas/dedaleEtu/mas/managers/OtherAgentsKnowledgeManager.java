package eu.su.mas.dedaleEtu.mas.managers;

import java.io.Serializable;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedaleEtu.mas.agents.MyAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.NodeObservations;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;

public class OtherAgentsKnowledgeManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private MyAgent agent;

    public OtherAgentsKnowledgeManager(MyAgent agent) {
        this.agent = agent;
    }



    

    /*
     * Caractéristiques des autres agents à priori
     */

    

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
        return agent.getOtherAgentsTopology().isTopologyShareable(agentName);
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
