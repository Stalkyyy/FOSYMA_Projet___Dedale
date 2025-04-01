package eu.su.mas.dedaleEtu.mas.managers;

import eu.su.mas.dedaleEtu.mas.agents.MyAgent;
import eu.su.mas.dedaleEtu.mas.msgObjects.CharacteristicsMessage;
import eu.su.mas.dedaleEtu.mas.msgObjects.TopologyMessage;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CommunicationManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private MyAgent agent;

    protected AtomicInteger messageIdCounter = new AtomicInteger();

    public enum COMMUNICATION_STEP {
        SHARE_TOPO(1),
        SHARE_CHARACTERISTICS(2);

        private int exitCode;
        COMMUNICATION_STEP(int exitCode) {
            this.exitCode = exitCode;
        }

        public int getExitCode() {
            return this.exitCode;
        }
    }

    // Force l'ordre des étapes de communications.
    private final static List<COMMUNICATION_STEP> OrderSteps = List.of(
        COMMUNICATION_STEP.SHARE_CHARACTERISTICS,
        COMMUNICATION_STEP.SHARE_TOPO
    );


    public CommunicationManager(MyAgent agent) {
        this.agent = agent;

        for (COMMUNICATION_STEP step : COMMUNICATION_STEP.values())
            agent.getCommunicationSteps().put(step, false);
    }



    /*
     * --- GERE LA COMMUNICATION ACTUELLE ---
     */

    public String getTargetAgent() {
        return agent.getTargetAgent();
    }

    public void setTargetAgent(String agentName) {
        agent.setTargetAgent(agentName);
    }


    public void addStep(COMMUNICATION_STEP step) {
        agent.getCommunicationSteps().put(step, true);
    }

    public void removeStep(COMMUNICATION_STEP step) {
        agent.getCommunicationSteps().put(step, false);
    }

    public COMMUNICATION_STEP getNextStep() {
        for (COMMUNICATION_STEP step : CommunicationManager.OrderSteps) {
            if (agent.getCommunicationSteps().get(step))
                return step;
        }
        return null;
    }

    public void clearSteps() {
        for (COMMUNICATION_STEP step : COMMUNICATION_STEP.values())
            removeStep(step);
    }

    public void stopCommunication() {
        setTargetAgent(null);
        clearSteps();
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