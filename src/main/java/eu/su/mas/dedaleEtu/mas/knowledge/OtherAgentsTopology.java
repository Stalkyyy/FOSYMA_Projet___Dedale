package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;

public class OtherAgentsTopology implements Serializable {
    
    private static final long serialVersionUID = -1333959882640838272L;

    private Map<String, SerializableSimpleGraph<String, MapAttribute>> otherAgentsTopologies;
    private Map<String, Integer> updatesSinceLastCommunication;
    private Map<String, Boolean> finishedExplo;
    private int minUpdatesBeforeCommunication = 25;


    public OtherAgentsTopology() {
        this.otherAgentsTopologies = new HashMap<>();
        this.updatesSinceLastCommunication = new HashMap<>();
        this.finishedExplo = new HashMap<>();
    }

    public OtherAgentsTopology(List<String> list_agentNames) {
        this.otherAgentsTopologies = new HashMap<>();
        this.updatesSinceLastCommunication = new HashMap<>();
        this.finishedExplo = new HashMap<>();
        for (String agentName : list_agentNames) {
            this.otherAgentsTopologies.put(agentName, new SerializableSimpleGraph<String, MapAttribute>());
            this.updatesSinceLastCommunication.put(agentName, 0);
            this.finishedExplo.put(agentName, false);
        }
    }


    public boolean isInfoShareable(String agentName) {
        return (this.updatesSinceLastCommunication.get(agentName) >= this.minUpdatesBeforeCommunication)
            && !this.finishedExplo.get(agentName);
    }

    public void resetLastUpdatesAgent(String agentName) {
        this.updatesSinceLastCommunication.put(agentName, 0);
    }

    public void incrementeLastUpdates() {
        for (String agentName : this.updatesSinceLastCommunication.keySet()) {
            int lastValue = this.updatesSinceLastCommunication.get(agentName);
            this.updatesSinceLastCommunication.put(agentName, lastValue + 1);
        }
    }

    public void agentFinishedExplo(String agentName) {
        this.finishedExplo.put(agentName, true);
    }



    public void addTopology(String agentName, SerializableSimpleGraph<String, MapAttribute> topology) {
        this.otherAgentsTopologies.put(agentName, topology);
    }

    public SerializableSimpleGraph<String, MapAttribute> getTopology(String agentName) {
        return this.otherAgentsTopologies.get(agentName);
    }

    public boolean containsAgent(String agentName) {
        return this.otherAgentsTopologies.containsKey(agentName);
    }


    public void removeTopology(String agentName) {
        this.otherAgentsTopologies.remove(agentName);
    }

    public Map<String, SerializableSimpleGraph<String, MapAttribute>> getAllTopologies() {
        return this.otherAgentsTopologies;
    }
}
