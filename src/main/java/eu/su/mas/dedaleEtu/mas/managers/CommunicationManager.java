package eu.su.mas.dedaleEtu.mas.managers;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.msgObjects.DeadlockMessage;
import eu.su.mas.dedaleEtu.mas.msgObjects.TopologyMessage;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CommunicationManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private AbstractAgent agent;

    protected AtomicInteger messageIdCounter = new AtomicInteger();

    public enum COMMUNICATION_STEP {
        SHARE_TREASURES(20),
        SHARE_TOPO(30),
        ENTRY_FLOOD_SENT(40),
        ENTRY_FLOOD_RECEIVED(41),
        DEADLOCK(50);

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
        COMMUNICATION_STEP.SHARE_TREASURES,
        COMMUNICATION_STEP.SHARE_TOPO,
        COMMUNICATION_STEP.ENTRY_FLOOD_SENT,
        COMMUNICATION_STEP.ENTRY_FLOOD_RECEIVED,
        COMMUNICATION_STEP.DEADLOCK
    );

    private String temporaryAgentNodeId = null;
    private DeadlockMessage temporaryDeadlockMessage = null;
    private boolean lettingHimPass = false;


    public CommunicationManager(AbstractAgent agent) {
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

    public void setTargetAgent(String agentName, String nodeId) {
        agent.setTargetAgent(agentName);
        agent.setTargetAgentNode(nodeId);
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
        agent.setTargetAgent(null);
        agent.setTargetAgentNode(null);
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
     * --- Temporary variable for deadlock message ---
     */

    public String getTemporaryAgentNodeId() {
        return this.temporaryAgentNodeId;
    }

    public void setTemporaryAgentNodeId(String s) {
        this.temporaryAgentNodeId = s;
    }

    public DeadlockMessage getTemporaryDeadlockMessage() {
        return this.temporaryDeadlockMessage;
    }

    public void addTemporaryDeadlockMessage(DeadlockMessage DM) {
        this.temporaryDeadlockMessage = DM;
    }

    public void removeTemporaryDeadlockMessage() {
        this.temporaryDeadlockMessage = null;
    }

    public boolean getLettingHimPass() {
        return this.lettingHimPass;
    }

    public void setLettingHimPass(boolean b) {
        this.lettingHimPass = b;
    }



    /*
     * --- GENERATION D'ID DE MESSAGES ---
     */

    public int generateMessageId() {
        return messageIdCounter.incrementAndGet();
    }
}