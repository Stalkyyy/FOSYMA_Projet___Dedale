package eu.su.mas.dedaleEtu.mas.managers;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentBehaviourState;
import eu.su.mas.dedaleEtu.mas.knowledge.FloodingState.FLOODING_STEP;

public class FloodingManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private AbstractAgent agent;

    // Initialise le gestionnaire de flooding pour un agent donné.
    public FloodingManager(AbstractAgent agent) {
        this.agent = agent;
    }

    // ==================================================================

    // Active le protocole de flooding pour un agent root.
    public void activateFlooding() {
        agent.getFloodingState().activateFlooding();
        agent.getFloodingState().addAgentsInTree(agent.getLocalName());
        agent.setBehaviourState(AgentBehaviourState.FLOODING);

        agent.moveMgr.resetFailedMoveCount();
        agent.setDeadlockNodeSolution(null);
        agent.comMgr.setLettingHimPass(false);
    }

    // Active le protocole de flooding pour un agent non-root.
    public void activateFlooding(String parentAgent) {
        agent.getFloodingState().activateFlooding(parentAgent);
        agent.getFloodingState().addAgentsInTree(agent.getLocalName());
        agent.setBehaviourState(AgentBehaviourState.FLOODING);

        agent.moveMgr.resetFailedMoveCount();
        agent.setDeadlockNodeSolution(null);
        agent.comMgr.setLettingHimPass(false);
    }

    // Désactive le protocole de flooding.
    public void deactivateFlooding() {
        agent.getFloodingState().deactivateFlooding();
    }

    // Vérifie si le protocole de flooding est actif.
    public boolean isFloodingActive() {
        return agent.getFloodingState().isFloodingActive();
    }

    // Vérifie si c'est le premier protocole de flooding.
    public boolean isFirstFlooding() {
        return agent.getFloodingState().isFirstFlooding();
    }

    // ==================================================================

    // Vérifie si l'agent est le root du protocole de flooding.
    public boolean isRoot() {
        return agent.getFloodingState().isRoot();
    }

    // Ajoute un agent à l'arbre de flooding.
    public void addAgentsInTree(String agentName) {
        agent.getFloodingState().addAgentsInTree(agentName);
    }

    // Vérifie si tous les agents sont dans l'arbre de flooding.
    public boolean isEveryoneInTree() {
        Set<String> agentsInTree = agent.getFloodingState().getAgentsInTree();        
        return agentsInTree != null && agentsInTree.containsAll(agent.getListAgentNames());
    }

    // Vérifie si l'agent est une feuille dans l'arbre de flooding.
    public boolean isLeaf() {
        return agent.getFloodingState().getChildrenAgents().size() == 0;
    }

    // ==================================================================

    // Vérifie si un agent a déjà été contacté.
    public boolean hasContactedAgent(String agentName) {
        return agent.getFloodingState().getContactedAgents().contains(agentName);
    }

    // ==================================================================

    // Retourne le parent de l'agent dans l'arbre de flooding.
    public String getParentAgent() {
        return agent.getFloodingState().getParentAgent();
    }

    // Retourne les enfants de l'agent dans l'arbre de flooding.
    public Set<String> getChildrenAgents() {
        return agent.getFloodingState().getChildrenAgents().keySet();
    }

    // ==================================================================

    // Vérifie si un agent est un enfant de l'agent actuel.
    public boolean isChildren(String childName) {
        return agent.getFloodingState().getChildrenAgents().get(childName) != null;
    }

    // Ajoute un enfant à l'agent actuel dans l'arbre de flooding.
    public void addChildren(String childName) {
        agent.getFloodingState().getChildrenAgents().put(childName, new HashSet<>());
    }

    // Ajoute un agent accessible via un enfant.
    public void addAccessibleAgent(String childName, String newAgentName) {
        agent.getFloodingState().getChildrenAgents().get(childName).add(newAgentName);
    }

    // Ajoute un agent à la liste des agents contactés.
    public void addContacted(String agentName) {
        agent.getFloodingState().getContactedAgents().add(agentName);
    }

    // ==================================================================

    // Retourne l'étape actuelle du protocole de flooding.
    public FLOODING_STEP getStep() {
        return agent.getFloodingState().getStep();
    }

    // Définit l'étape actuelle du protocole de flooding.
    public void setStep(FLOODING_STEP step) {
        agent.getFloodingState().setStep(step);
    }
}
