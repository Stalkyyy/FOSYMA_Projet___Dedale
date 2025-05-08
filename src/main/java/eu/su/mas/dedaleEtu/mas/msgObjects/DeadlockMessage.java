package eu.su.mas.dedaleEtu.mas.msgObjects;

import java.io.Serializable;

import eu.su.mas.dedaleEtu.mas.knowledge.NodeReservation;

public class DeadlockMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private String currentNodeId;
    private NodeReservation nodeReservation;

    public DeadlockMessage(String currentNodeId, NodeReservation nodeReservation) {
        this.currentNodeId = currentNodeId;
        this.nodeReservation = nodeReservation;
    }

    public String getCurrentNodeId() {
        return currentNodeId;
    }

    public NodeReservation getNodeReservation() {
        return nodeReservation;
    }
}
