package eu.su.mas.dedaleEtu.mas.msgObjects;

import java.io.Serializable;

import eu.su.mas.dedaleEtu.mas.knowledge.NodeReservation;

public class DeadlockMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private String currentNodeId;  // Identifiant du nœud actuel.
    private NodeReservation nodeReservation;  // Réservation de nœud associée au message.

    // Initialise un message de deadlock avec le nœud actuel et la réservation associée.
    public DeadlockMessage(String currentNodeId, NodeReservation nodeReservation) {
        this.currentNodeId = currentNodeId;
        this.nodeReservation = nodeReservation;
    }

    // Retourne l'identifiant du nœud actuel.
    public String getCurrentNodeId() {
        return currentNodeId;
    }

    // Retourne la réservation de nœud associée au message.
    public NodeReservation getNodeReservation() {
        return nodeReservation;
    }
}
