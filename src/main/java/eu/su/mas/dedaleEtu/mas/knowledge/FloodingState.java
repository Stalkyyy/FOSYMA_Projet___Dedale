package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FloodingState implements Serializable {
    
    private static final long serialVersionUID = 1L;

    
    /**
     * Enumération des étapes du protocole de flooding.
     * Chaque étape est associée à un code de sortie.
     */
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

    // Vérifie si le protocole de flooding est actif.
    public boolean isFloodingActive() {
        return this.isFloodingActive;
    }

    // Active ou désactive le protocole de flooding.
    public void setFloodingActive(boolean b) {
        this.isFloodingActive = b;
    }

    //Retourne l'étape actuelle du protocole de flooding.
    public FLOODING_STEP getStep() {
        return this.step;
    }

    //Définit l'étape actuelle du protocole de flooding.
    public void setStep(FLOODING_STEP step) {
        this.step = step;
    }

    //Vérifie si l'agent est root dans le protocole de flooding.
    public boolean isRoot() {
        return this.isRoot;
    }

    // Définit si l'agent est root dans le protocole de flooding.
    public void setRoot(boolean b) {
        this.isRoot = b;
    }

    // Retourne les agents présents dans l'arbre de flooding.
    public Set<String> getAgentsInTree() {
        return this.agentsInTree;
    }

    // Définit les agents présents dans l'arbre de flooding.
    public void setAgentsInTree(Set<String> agentsInTree) {
        this.agentsInTree = agentsInTree;
    }

    // Ajoute un agent à l'arbre de flooding.
    public void addAgentsInTree(String agentName) {
        this.agentsInTree.add(agentName);
    }

    // Retourne le parent de l'agent dans l'arbre de flooding.
    public String getParentAgent() {
        return this.parentAgent;
    }

    // Définit le parent de l'agent dans l'arbre de flooding.
    public void setParentAgent(String agentName) {
        this.parentAgent = agentName;
    }

    // Retourne les agents déjà contactés.
    public Set<String> getContactedAgents() {
        return this.contactedAgents;
    }

    // Définit les agents déjà contactés.
    public void setContactedAgents(Set<String> contactedAgents) {
        this.contactedAgents = contactedAgents;
    }

    //Retourne la map des enfants et des agents accessibles via eux.
    public Map<String, Set<String>> getChildrenAgents() {
        return this.childrenAgents;
    }

    // Définit la map des enfants et des agents accessibles via eux.
    public void setChildrenAgents(Map<String, Set<String>> childrenAgents) {
        this.childrenAgents = childrenAgents;
    }
    
    // Vérifie si c'est le premier protocole de flooding. 
    public boolean isFirstFlooding() {
        return isFirstFlooding;
    }

    // ==================================================================

    // Active le protocole de flooding pour un agent root.
    public void activateFlooding() {
        this.isFloodingActive = true;
        this.step = FLOODING_STEP.WAITING_FOR_EVERYONE;
        this.isRoot = true;
        this.agentsInTree = new HashSet<>();
        this.parentAgent = null;
        this.contactedAgents = new HashSet<>();
        this.childrenAgents = new HashMap<>();
    }

    // Active le protocole de flooding pour un agent non-root.
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

    // Désactive le protocole de flooding et réinitialise les attributs.
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
