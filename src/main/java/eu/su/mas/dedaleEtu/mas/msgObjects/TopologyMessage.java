package eu.su.mas.dedaleEtu.mas.msgObjects;

import java.io.Serializable;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.knowledge.NodeObservations;

public class TopologyMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private int msgId;
    private String receiverName;

    private SerializableSimpleGraph<String, MapAttribute> topology;
    private NodeObservations observations;
    private boolean isExploCompleted;

    public TopologyMessage(int msgId, String receiverName, SerializableSimpleGraph<String, MapAttribute> topology, NodeObservations observations, boolean isExploCompleted) {
        this.msgId = msgId;
        this.receiverName = receiverName;
        this.topology = topology;
        this.observations = observations;
        this.isExploCompleted = isExploCompleted;
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

    public boolean getExplorationComplete() {
        return isExploCompleted;
    }

}