package eu.su.mas.dedaleEtu.mas.managers;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.msgObjects.CharacteristicsMessage;
import eu.su.mas.dedaleEtu.mas.msgObjects.DeadlockMessage;
import eu.su.mas.dedaleEtu.mas.msgObjects.TopologyMessage;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CommunicationManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private AbstractAgent agent;

    protected AtomicInteger messageIdCounter = new AtomicInteger();

    /**
     * Enumération des étapes de communication.
     * Chaque étape est associée à un code de sortie.
     */
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

    // Constructeur de la classe.
    public CommunicationManager(AbstractAgent agent) {
        this.agent = agent;

        for (COMMUNICATION_STEP step : COMMUNICATION_STEP.values())
            agent.getCommunicationSteps().put(step, false);
    }



    /*
     * --- GERE LA COMMUNICATION ACTUELLE ---
     */

    // Retourne l'agent cible de la communication.
    public String getTargetAgent() {
        return agent.getTargetAgent();
    }

    // Définit l'agent cible et son nœud.
    public void setTargetAgent(String agentName, String nodeId) {
        agent.setTargetAgent(agentName);
        agent.setTargetAgentNode(nodeId);
    }

    // Ajoute une étape de communication à la liste des étapes actives.
    public void addStep(COMMUNICATION_STEP step) {
        agent.getCommunicationSteps().put(step, true);
    }

    // Supprime une étape de communication de la liste des étapes actives.
    public void removeStep(COMMUNICATION_STEP step) {
        agent.getCommunicationSteps().put(step, false);
    }

    // Retourne la prochaine étape de communication active.
    public COMMUNICATION_STEP getNextStep() {
        for (COMMUNICATION_STEP step : CommunicationManager.OrderSteps) {
            if (agent.getCommunicationSteps().get(step))
                return step;
        }
        return null;
    }

    // Réinitialise toutes les étapes de communication.
    public void clearSteps() {
        for (COMMUNICATION_STEP step : COMMUNICATION_STEP.values())
            removeStep(step);
    }

    /**
     * Arrête la communication en cours.
     * Réinitialise l'agent cible et les étapes de communication.
     */
    public void stopCommunication() {
        agent.setTargetAgent(null);
        agent.setTargetAgentNode(null);
        clearSteps();
    }
    

    /*
     * --- HISTORIQUE DES MESSAGES DE TOPOLOGIE ---
     */

    // Ajoute un message de topologie à l'historique.
    public void addTopologyMessageToHistory(TopologyMessage message) {
        agent.getTopologyMessageHistory().put(message.getMsgId(), message);
    }

    // Récupère un message de topologie depuis l'historique.
    public TopologyMessage getTopologyMessage(int msgId) {
        return agent.getTopologyMessageHistory().get(msgId);
    }


    /*
     * --- HISTORIQUE DES MESSAGES DES CHARACTERISTIQUES ---
     */

    // Ajoute un message de caractéristiques à l'historique.
    public void addCharacteristicsMessageToHistory(CharacteristicsMessage message) {
        agent.getCharacteristicsMessageHistory().put(message.getMsgId(), message);
    }

    // Récupère un message de caractéristiques depuis l'historique.
    public CharacteristicsMessage getCharacteristicsMessage(int msgId) {
        return agent.getCharacteristicsMessageHistory().get(msgId);
    }



    /*
     * --- Temporary variable for deadlock message ---
     */

    // Retourne l'identifiant temporaire du nœud de l'agent.
    public String getTemporaryAgentNodeId() {
        return this.temporaryAgentNodeId;
    }

    // Définit l'identifiant temporaire du nœud de l'agent.
    public void setTemporaryAgentNodeId(String s) {
        this.temporaryAgentNodeId = s;
    }

    // Retourne le message de deadlock temporaire.
    public DeadlockMessage getTemporaryDeadlockMessage() {
        return this.temporaryDeadlockMessage;
    }

    // Ajoute un message de deadlock temporaire.
    public void addTemporaryDeadlockMessage(DeadlockMessage DM) {
        this.temporaryDeadlockMessage = DM;
    }

    // Supprime le message de deadlock temporaire.
    public void removeTemporaryDeadlockMessage() {
        this.temporaryDeadlockMessage = null;
    }

    // Vérifie si l'agent laisse passer un autre agent.
    public boolean getLettingHimPass() {
        return this.lettingHimPass;
    }

    // Définit si l'agent laisse passer un autre agent.
    public void setLettingHimPass(boolean b) {
        this.lettingHimPass = b;
    }



    /*
     * --- GENERATION D'ID DE MESSAGES ---
     */

    // Génère un nouvel identifiant unique pour un message.
    public int generateMessageId() {
        return messageIdCounter.incrementAndGet();
    }
}