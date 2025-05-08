package eu.su.mas.dedaleEtu.mas.msgObjects;

import java.io.Serializable;

import eu.su.mas.dedaleEtu.mas.knowledge.TreasureObservations;

public class TreasureMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private int msgId;
    private String receiverName;

    private TreasureObservations treasures; // Observations sur les trésors incluses dans le message.

    // Initialise un message contenant des informations sur les trésors.
    public TreasureMessage(int msgId, String receiverName, TreasureObservations treasures) {
        this.msgId = msgId;
        this.receiverName = receiverName;
        this.treasures = treasures;
    }

    // Initialise un message contenant uniquement des observations sur les trésors.
    public TreasureMessage(TreasureObservations treasures) {
        this.msgId = -1;
        this.receiverName = null;
        this.treasures = treasures;
    }

    // Retourne l'identifiant du message.
    public int getMsgId() {
        return msgId;
    }

    // Retourne le nom de l'agent destinataire.
    public String getReceiverName() {
        return receiverName;
    }

    // Retourne les observations sur les trésors incluses dans le message.
    public TreasureObservations getTreasures() {
        return treasures;
    }
}
