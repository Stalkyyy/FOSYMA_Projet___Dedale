package eu.su.mas.dedaleEtu.mas.msgObjects;

import java.io.Serializable;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentType;

public class CharacteristicsMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private int msgId;
    private String senderName;
    private String receiverName;

    private AgentType agentType;
    private Observation treasureType;
    private int space;
    private int lockpick;
    private int strength;

    public CharacteristicsMessage(int msgId, String senderName, String receiverName, AgentType agentType, Observation treasureType, int space, int lockpick, int strength) {
        this.msgId = msgId;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.agentType = agentType;
        this.treasureType = treasureType;
        this.space = space;
        this.lockpick = lockpick;
        this.strength = strength;
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
        return agentType;
    }

    public Observation getTreasureType() {
        return treasureType;
    }

    public int getSpace() {
        return space;
    }

    public int getLockpick() {
        return lockpick;
    }

    public int getStrength() {
        return strength;
    }
}
