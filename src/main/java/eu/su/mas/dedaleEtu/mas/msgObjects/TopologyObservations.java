package eu.su.mas.dedaleEtu.mas.msgObjects;

import java.io.Serializable;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.knowledge.NodeObservations;

public class TopologyObservations implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private int msgId;
    private String receiverName;

    private SerializableSimpleGraph<String, MapAttribute> topology;
    private NodeObservations observations;

    public TopologyObservations(int msgId, String receiverName, SerializableSimpleGraph<String, MapAttribute> topology, NodeObservations observations) {
        this.msgId = msgId;
        this.receiverName = receiverName;
        this.topology = topology;
        this.observations = observations;
    }

    public int getMsgId() {
        return msgId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public SerializableSimpleGraph<String, MapAttribute> getTopology() {
        return topology;
    }

    public NodeObservations getObservations() {
        return observations;
    }

}