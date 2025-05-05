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

    public FloodingManager(AbstractAgent agent) {
        this.agent = agent;
    }

    // ==================================================================

    public void activateFlooding() {
        agent.getFloodingState().activateFlooding();
        agent.setBehaviourState(AgentBehaviourState.FLOODING);
    }

    public void activateFlooding(String parentAgent) {
        agent.getFloodingState().activateFlooding(parentAgent);
        agent.setBehaviourState(AgentBehaviourState.FLOODING);
    }

    public void deactivateFlooding() {
        agent.getFloodingState().deactivateFlooding();
        agent.setBehaviourState(AgentBehaviourState.COLLECT_TREASURE);
    }

    public boolean isFloodingActive() {
        return agent.getFloodingState().isFloodingActive();
    }

    public boolean isFirstFlooding() {
        return agent.getFloodingState().isFirstFlooding();
    }

    // ==================================================================

    public boolean isRoot() {
        return agent.getFloodingState().isRoot();
    }

    public void addAgentsInTree(String agentName) {
        agent.getFloodingState().getAgentsInTree().add(agentName);
    }

    public boolean isEveryoneInTree() {
        Set<String> agentsInTree = agent.getFloodingState().getAgentsInTree();        
        return agentsInTree != null && agentsInTree.containsAll(agent.getListAgentNames());
    }

    public boolean isLeaf() {
        return agent.getFloodingState().getChildrenAgents().size() == 0;
    }

    // ==================================================================

    public boolean hasContactedAgent(String agentName) {
        return agent.getFloodingState().getContactedAgents().contains(agentName);
    }

    // ==================================================================

    public String getParentAgent() {
        return agent.getFloodingState().getParentAgent();
    }

    public Set<String> getChildrenAgents() {
        return agent.getFloodingState().getChildrenAgents().keySet();
    }

    // ==================================================================

    public void addChildren(String childName) {
        agent.getFloodingState().getChildrenAgents().put(childName, new HashSet<>());
    }

    public void addAccessibleAgent(String childName, String newAgentName) {
        agent.getFloodingState().getChildrenAgents().get(childName).add(newAgentName);
    }

    public void addContacted(String agentName) {
        agent.getFloodingState().getContactedAgents().add(agentName);
    }

    // ==================================================================

    public FLOODING_STEP getStep() {
        return agent.getFloodingState().getStep();
    }

    public void setStep(FLOODING_STEP step) {
        agent.getFloodingState().setStep(step);
    }
}
