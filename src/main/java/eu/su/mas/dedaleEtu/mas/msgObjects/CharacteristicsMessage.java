package eu.su.mas.dedaleEtu.mas.msgObjects;

import java.io.Serializable;
import java.util.Set;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;

public class CharacteristicsMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private int msgId;
    private String senderName;
    private String receiverName;

    private Set<Couple<Observation, Integer>> expertise;
    private Observation treasureType;

    public CharacteristicsMessage(int msgId, String senderName, String receiverName, Set<Couple<Observation, Integer>> expertise, Observation treasureType) {
        this.msgId = msgId;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.expertise = expertise;
        this.treasureType = treasureType;
    }

    public int getMsgId() {
        return msgId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public Set<Couple<Observation, Integer>> getExpertise() {
        return expertise;
    }

    public Observation getTreasureType() {
        return treasureType;
    }
}
