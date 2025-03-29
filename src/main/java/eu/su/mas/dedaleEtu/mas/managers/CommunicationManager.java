package eu.su.mas.dedaleEtu.mas.managers;

import eu.su.mas.dedaleEtu.mas.agents.GeneralAgent;
import eu.su.mas.dedaleEtu.mas.msgObjects.TopologyObservations;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class CommunicationManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private GeneralAgent agent;

    protected AtomicInteger messageIdCounter = new AtomicInteger();

    public CommunicationManager(GeneralAgent agent) {
        this.agent = agent;
    }

    public void addMessageToHistory(TopologyObservations message) {
        agent.getSentMessagesHistory().put(message.getMsgId(), message);
    }

    public TopologyObservations getMessageFromHistory(int msgId) {
        return agent.getSentMessagesHistory().get(msgId);
    }

    public int generateMessageId() {
        return messageIdCounter.incrementAndGet();
    }
}