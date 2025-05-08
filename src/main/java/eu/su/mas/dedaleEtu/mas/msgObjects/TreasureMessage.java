package eu.su.mas.dedaleEtu.mas.msgObjects;

import java.io.Serializable;

import eu.su.mas.dedaleEtu.mas.knowledge.TreasureObservations;

public class TreasureMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private int msgId;
    private String receiverName;

    private TreasureObservations treasures;

    public TreasureMessage(int msgId, String receiverName, TreasureObservations treasures) {
        this.msgId = msgId;
        this.receiverName = receiverName;
        this.treasures = treasures;
    }

    public TreasureMessage(TreasureObservations treasures) {
        this.msgId = -1;
        this.receiverName = null;
        this.treasures = treasures;
    }

    public int getMsgId() {
        return msgId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public TreasureObservations getTreasures() {
        return treasures;
    }
}
