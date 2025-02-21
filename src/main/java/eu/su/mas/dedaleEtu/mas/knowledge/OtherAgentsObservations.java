package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class OtherAgentsObservations implements Serializable {
    
    private static final long serialVersionUID = -1333959882640838272L;

    private Map<String, NodeObservations> otherAgentsObservations;



    public OtherAgentsObservations() {
        this.otherAgentsObservations = new HashMap<>();
    }

    public OtherAgentsObservations(List<String> list_agentNames) {
        this.otherAgentsObservations = new HashMap<>();
        for (String agentName : list_agentNames) {
            this.otherAgentsObservations.put(agentName, new NodeObservations());
        }
    }



    public void addObservations(String agentName, NodeObservations observations) {
        this.otherAgentsObservations.put(agentName, observations);
    }

    public NodeObservations getObservations(String agentName) {
        return this.otherAgentsObservations.get(agentName);
    }

    public boolean containsAgent(String agentName) {
        return this.otherAgentsObservations.containsKey(agentName);
    }

    public void removeObservations(String agentName) {
        this.otherAgentsObservations.remove(agentName);
    }

    public Map<String, NodeObservations> getAllObservations() {
        return this.otherAgentsObservations;
    }

    

    public void mergeObservation(String receiverName, NodeObservations obs) {
        this.otherAgentsObservations.get(receiverName).mergeObservations(obs);
    }
}
