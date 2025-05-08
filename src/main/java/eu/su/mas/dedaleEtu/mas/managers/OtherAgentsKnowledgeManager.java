package eu.su.mas.dedaleEtu.mas.managers;

import java.io.Serializable;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentBehaviourState;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentType;
import eu.su.mas.dedaleEtu.mas.knowledge.given_knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.msgObjects.CharacteristicsFloodMessage;

public class OtherAgentsKnowledgeManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private AbstractAgent agent;

    // Initialise le gestionnaire de connaissances sur les autres agents pour un agent donné.
    public OtherAgentsKnowledgeManager(AbstractAgent agent) {
        this.agent = agent;
    }


    // Vérifie si une communication doit être initiée avec un autre agent.
    public boolean shouldInitiateCommunication(String agentName, String agentPosition) {

        if (agent.getBehaviourState() == AgentBehaviourState.EXPLORATION) {
            return isTopologyShareable(agentName) || shouldInitiateDeadlock(agentName, agentPosition);
        }

        else if (agent.getBehaviourState() == AgentBehaviourState.FLOODING) {
            return !agent.floodMgr.hasContactedAgent(agentName);
        }

        else if (agent.getBehaviourState() == AgentBehaviourState.COLLECT_TREASURE) {
            return shouldInitiateDeadlock(agentName, agentPosition) && shouldIPushMySuperior(agentName, agentPosition);
        }

        else {
            return shouldInitiateDeadlock(agentName, agentPosition);
        }
    }



    /*
     * Caractéristiques des autres agents à priori
     */

    // Met à jour les caractéristiques d'un agent donné.
    public void updateCharacteristics(String agentName, AgentType type, Observation treasureType, int space, int lockpick, int strength) {
        agent.getOtherAgentsCharacteristics().updateCharacteristics(agentName, type, treasureType, space, lockpick, strength);
    }

    // Met à jour les caractéristiques des agents à partir d'un message de flooding.
    public void updateCharacteristics(CharacteristicsFloodMessage CFM) {
        agent.getOtherAgentsCharacteristics().updateCharacteristics(CFM);
    }

    // Retourne le type d'un agent donné.
    public AgentType getAgentType(String agentName) {
        return agent.getOtherAgentsCharacteristics().getAgentType(agentName);
    }

    // Retourne le type de trésor manipulé par un agent donné.
    public Observation getTreasureType(String agentName) {
        return agent.getOtherAgentsCharacteristics().getTreasureType(agentName);
    }

    // Retourne l'espace disponible dans le sac à dos d'un agent donné. 
    public int getSpace(String agentName) {
        return agent.getOtherAgentsCharacteristics().getSpace(agentName);
    }

    // Retourne le niveau de crochetage d'un agent donné.
    public int getLockpick(String agentName) {
        return agent.getOtherAgentsCharacteristics().getLockpick(agentName);
    }

    // Retourne la force d'un agent donné.
    public int getStrength(String agentName) {
        return agent.getOtherAgentsCharacteristics().getStrength(agentName);
    }

    

    /*
     * Topologies des autres agents à priori (+ partage !)
     */

    // Incrémente le compteur de mises à jour de topologie.
    public void incrementeLastUpdates_topology() {
        agent.getOtherAgentsTopology().incrementeLastUpdates();
    }

    // Met à jour la topologie d'un agent donné.
    public void updateTopology(String agentName, SerializableSimpleGraph<String, MapAttribute> topology) {
        agent.getOtherAgentsTopology().updateTopology(agentName, topology);
    }

    // Retourne la topologie d'un agent donné.
    public SerializableSimpleGraph<String, MapAttribute> getTopology(String agentName) {
        return agent.getOtherAgentsTopology().getTopology(agentName);
    }

    // Calcule la différence entre la topologie locale et celle d'un agent donné.
    public SerializableSimpleGraph<String, MapAttribute> getTopologyDifferenceWith(String agentName) {
        SerializableSimpleGraph<String, MapAttribute> tp1 = agent.getMyMap().getSerializableGraph();
        SerializableSimpleGraph<String, MapAttribute> tp2 = getTopology(agentName);
        return agent.topoMgr.diffTopology(tp1, tp2);
    }

    // Fusionne la topologie locale avec celle d'un agent donné.
    public void mergeTopologyOf(String agentName, SerializableSimpleGraph<String, MapAttribute> tp2) {
        SerializableSimpleGraph<String, MapAttribute> mergedTopo = agent.topoMgr.mergeTopologies(getTopology(agentName), tp2);
        updateTopology(agentName, mergedTopo);
    }

    // Vérifie si la topologie peut être partagée avec un agent donné.
    public boolean isTopologyShareable(String agentName) {
        int minUpdatesToShare = agent.getOtherAgentsTopology().getMinUpdatesToShare();
        int pendingUpdatesCount = agent.getOtherAgentsTopology().getPendingUpdatesCountOf(agentName);
        boolean hasFinishedExplo = agent.getOtherAgentsTopology().hasFinishedExplo(agentName);

        boolean cond_1 = !agent.getExplorationComplete() && pendingUpdatesCount >= minUpdatesToShare && !hasFinishedExplo;
        boolean cond_2 = agent.getExplorationComplete() && !hasFinishedExplo;

        return cond_1 || cond_2;
    }

    // Réinitialise le compteur de mises à jour pour un agent donné.
    public void resetLastUpdateAgent_topology(String agentName) {
        agent.getOtherAgentsTopology().resetLastUpdatesAgent(agentName);
    }

    // Marque l'exploration comme terminée pour un agent donné.
    public void markExplorationComplete(String agentName) {
        agent.getOtherAgentsTopology().markExplorationComplete(agentName);
    }



    /*
     * Deadlocks
     */

    // Vérifie si un deadlock doit être initié avec un agent donné.
    public boolean shouldInitiateDeadlock(String agentName, String agentPosition) {
        return agent.moveMgr.shouldInitiateDeadlock(agentName, agentPosition);
    }

    // Vérifie si l'agent actuel doit pousser un agent supérieur dans un deadlock.
    public boolean shouldIPushMySuperior(String agentName, String agentPosition) {
        boolean isInMyCoalition = agent.coalitionMgr.hasAgentInCoalition(agentName);
        if (!isInMyCoalition) 
            return true;

        boolean isMySuperior = agent.coalitionMgr.getRole(agentName).getPriority() > agent.coalitionMgr.getRole().getPriority();
        boolean inDeadlockWithMorePowerfulThanMe = agent.getNodeReservation() != null;

        return !isMySuperior || inDeadlockWithMorePowerfulThanMe;
    }
}
