package eu.su.mas.dedaleEtu.mas.msgObjects;

import java.io.Serializable;
import java.util.Set;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentType;

public class CharacteristicsMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private int msgId;
    private String senderName;
    private String receiverName;

    private AgentType type;
    private Set<Couple<Observation, Integer>> expertise;
    private Observation treasureType;

    public CharacteristicsMessage(int msgId, String senderName, String receiverName, AgentType type, Set<Couple<Observation, Integer>> expertise, Observation treasureType) {
        this.msgId = msgId;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.type = type;
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

    public AgentType getType() {
        return type;
    }

    public Set<Couple<Observation, Integer>> getExpertise() {
        return expertise;
    }

    public Observation getTreasureType() {
        return treasureType;
    }
}
