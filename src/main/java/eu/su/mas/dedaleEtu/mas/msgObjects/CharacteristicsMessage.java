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

    // Initialise un message avec toutes les informations nécessaires.
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

    // Initialise un message sans informations sur l'expéditeur et le destinataire.
    public CharacteristicsMessage(AgentType agentType, Observation treasureType, int space, int lockpick, int strength) {
        this.msgId = -1;
        this.senderName = null;
        this.receiverName = null;
        this.agentType = agentType;
        this.treasureType = treasureType;
        this.space = space;
        this.lockpick = lockpick;
        this.strength = strength;
    }

    // Retourne l'identifiant du message.
    public int getMsgId() {
        return msgId;
    }

    // Retourne le nom de l'agent expéditeur.
    public String getSenderName() {
        return senderName;
    }

    // Retourne le nom de l'agent destinataire.
    public String getReceiverName() {
        return receiverName;
    }

    // Retourne le type de l'agent.
    public AgentType getType() {
        return agentType;
    }

    // Retourne le type de trésor manipulé par l'agent.
    public Observation getTreasureType() {
        return treasureType;
    }

    // Retourne l'espace disponible dans le sac à dos de l'agent.
    public int getSpace() {
        return space;
    }

    // Retourne le niveau de crochetage de l'agent.
    public int getLockpick() {
        return lockpick;
    }

    //Retourne la force de l'agent.
    public int getStrength() {
        return strength;
    }

    @Override
    public String toString() {
        return "<" + agentType + ", " + treasureType + ", " + space + ", " + lockpick + ", " + strength + ">";
    }
}
