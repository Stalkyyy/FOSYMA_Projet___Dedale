package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class OtherAgentsCharacteristics implements Serializable {
    
    private static final long serialVersionUID = -1333959882640838272L;

    private Map<String, NodeObservations> otherAgentsCharacteristics;



    public OtherAgentsCharacteristics() {
        this.otherAgentsCharacteristics = new HashMap<>();
    }

    public OtherAgentsCharacteristics(List<String> list_agentNames) {
        this.otherAgentsCharacteristics = new HashMap<>();
        for (String agentName : list_agentNames) {
            this.otherAgentsCharacteristics.put(agentName, new NodeObservations());
        }
    }


}
