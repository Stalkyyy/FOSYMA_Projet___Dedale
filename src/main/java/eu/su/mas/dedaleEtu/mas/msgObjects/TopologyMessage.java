package eu.su.mas.dedaleEtu.mas.msgObjects;

import java.io.Serializable;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedaleEtu.mas.knowledge.given_knowledge.MapRepresentation.MapAttribute;

public class TopologyMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private int msgId;
    private String receiverName;

    private SerializableSimpleGraph<String, MapAttribute> topology;
    private boolean isExploCompleted;

    // Initialise un message contenant des informations sur la topologie.
    public TopologyMessage(int msgId, String receiverName, SerializableSimpleGraph<String, MapAttribute> topology, boolean isExploCompleted) {
        this.msgId = msgId;
        this.receiverName = receiverName;
        this.topology = topology;
        this.isExploCompleted = isExploCompleted;
    }

    // Retourne l'identifiant du message.
    public int getMsgId() {
        return msgId;
    }

    // Retourne le nom de l'agent destinataire.
    public String getReceiverName() {
        return receiverName;
    }

    // Retourne la topologie incluse dans le message.
    public SerializableSimpleGraph<String, MapAttribute> getTopology() {
        return topology;
    }

    // Indique si l'exploration est terminée.
    public boolean getExplorationComplete() {
        return isExploCompleted;
    }

}