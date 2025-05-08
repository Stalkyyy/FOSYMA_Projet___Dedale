package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FloodingState implements Serializable {
    
    private static final long serialVersionUID = 1L;

    public enum FLOODING_STEP {
        WAITING_FOR_EVERYONE(1000),
        SHARING_CHARACTERISTICS(2000),
        SHARING_TREASURES(3000),
        SHARING_PLANS(4000);

        private int exitCode;
        FLOODING_STEP(int exitCode) {
            this.exitCode = exitCode;
        }

        public int getExitCode() {
            return this.exitCode;
        }

    }

    private boolean isFloodingActive;
    private boolean isFirstFlooding;
    private FLOODING_STEP step;

    // Attributs pour l'agent root.
    private boolean isRoot;
    private Set<String> agentsInTree;

    private String parentAgent;
    private Set<String> contactedAgents; // Pour éviter les re-contacte inutile.

    // On garde en mémoire nos enfants, mais également les liens qu'il peut avoir avec les autres.
    // Par exemple, pour {"A" : {"B", "C", "D"}} - On sait que depuis C est accessible depuis A qui est notre enfant.
    private Map<String, Set<String>> childrenAgents; 

    public FloodingState() {
        this.isFloodingActive = false;
        this.isFirstFlooding = true;
        this.step = null;
        this.isRoot = false;
        this.agentsInTree = null;
        this.parentAgent = null;
        this.contactedAgents = null;
        this.childrenAgents = null;
    }

    // ==================================================================

    public boolean isFloodingActive() {
        return this.isFloodingActive;
    }

    public void setFloodingActive(boolean b) {
        this.isFloodingActive = b;
    }

    public FLOODING_STEP getStep() {
        return this.step;
    }

    public void setStep(FLOODING_STEP step) {
        this.step = step;
    }

    public boolean isRoot() {
        return this.isRoot;
    }

    public void setRoot(boolean b) {
        this.isRoot = b;
    }

    public Set<String> getAgentsInTree() {
        return this.agentsInTree;
    }

    public void setAgentsInTree(Set<String> agentsInTree) {
        this.agentsInTree = agentsInTree;
    }

    public void addAgentsInTree(String agentName) {
        this.agentsInTree.add(agentName);
    }

    public String getParentAgent() {
        return this.parentAgent;
    }

    public void setParentAgent(String agentName) {
        this.parentAgent = agentName;
    }

    public Set<String> getContactedAgents() {
        return this.contactedAgents;
    }

    public void setContactedAgents(Set<String> contactedAgents) {
        this.contactedAgents = contactedAgents;
    }

    public Map<String, Set<String>> getChildrenAgents() {
        return this.childrenAgents;
    }

    public void setChildrenAgents(Map<String, Set<String>> childrenAgents) {
        this.childrenAgents = childrenAgents;
    }

    public boolean isFirstFlooding() {
        return isFirstFlooding;
    }

    // ==================================================================

    public void activateFlooding() {
        this.isFloodingActive = true;
        this.step = FLOODING_STEP.WAITING_FOR_EVERYONE;
        this.isRoot = true;
        this.agentsInTree = new HashSet<>();
        this.parentAgent = null;
        this.contactedAgents = new HashSet<>();
        this.childrenAgents = new HashMap<>();
    }

    public void activateFlooding(String parentAgent) {
        this.isFloodingActive = true;
        this.step = FLOODING_STEP.WAITING_FOR_EVERYONE;
        this.isRoot = false;
        this.agentsInTree = new HashSet<>();
        this.parentAgent = parentAgent;
        this.contactedAgents = new HashSet<>();
        this.childrenAgents = new HashMap<>();
    }

    // ==================================================================

    public void deactivateFlooding() {
        this.isFloodingActive = false;
        this.isFirstFlooding = false;
        this.step = null;
        this.isRoot = false;
        this.agentsInTree = null;
        this.parentAgent = null;
        this.contactedAgents = null;
        this.childrenAgents = null;
    }
} 
