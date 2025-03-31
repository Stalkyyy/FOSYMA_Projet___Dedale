package eu.su.mas.dedaleEtu.mas.managers;

import eu.su.mas.dedaleEtu.mas.agents.MyAgent;
import eu.su.mas.dedaleEtu.mas.msgObjects.CharacteristicsMessage;
import eu.su.mas.dedaleEtu.mas.msgObjects.TopologyMessage;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class CommunicationManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private MyAgent agent;

    protected AtomicInteger messageIdCounter = new AtomicInteger();

    public CommunicationManager(MyAgent agent) {
        this.agent = agent;
    }

    /*
     * --- HISTORIQUE DES MESSAGES DE TOPOLOGIE ---
     */

    public void addTopologyMessageToHistory(TopologyMessage message) {
        agent.getTopologyMessageHistory().put(message.getMsgId(), message);
    }

    public TopologyMessage getTopologyMessage(int msgId) {
        return agent.getTopologyMessageHistory().get(msgId);
    }


    /*
     * --- HISTORIQUE DES MESSAGES DES CHARACTERISTIQUES ---
     */

    public void addCharacteristicsMessageToHistory(CharacteristicsMessage message) {
        agent.getCharacteristicsMessageHistory().put(message.getMsgId(), message);
    }

    public CharacteristicsMessage getCharacteristicsMessage(int msgId) {
        return agent.getCharacteristicsMessageHistory().get(msgId);
    }

    public int generateMessageId() {
        return messageIdCounter.incrementAndGet();
    }
}