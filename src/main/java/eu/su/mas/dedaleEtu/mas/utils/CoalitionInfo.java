package eu.su.mas.dedaleEtu.mas.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import eu.su.mas.dedale.env.Observation;

public class CoalitionInfo implements Serializable {
    private static final long serialVersionUID = 1L;

     /**
     * Enumération des rôles dans une coalition.
     * Chaque rôle est associé à une priorité pour la gestion des deadlocks.
     */
    public enum COALITION_ROLES {
        COLLECTOR(3),
        SILO(2),
        HELPER(1);

        private int deadlockPriority;
        COALITION_ROLES(int priority) {
            this.deadlockPriority = priority;
        }

        // Retourne la priorité associée au rôle.
        public int getPriority() {
            return this.deadlockPriority;
        }
    }

    // Les informations de base des trésors.
    private String nodeId;
    private Observation type;
    private int quantity;
    private boolean isLockOpen;

    // Le niveau d'expertise requis.
    private int requiredLockPick;
    private int requiredStrength;

    private Map<String, COALITION_ROLES> agentsRole;

    /**
     * Initialise les informations de la coalition à partir des détails du trésor.
     */
    public CoalitionInfo(String nodeId, Observation type, int quantity, boolean isLockOpen, int requiredLockPick, int requiredStrength) {
        this.nodeId = nodeId;
        this.type = type;
        this.quantity = quantity;
        this.isLockOpen = isLockOpen;

        this.requiredLockPick = requiredLockPick;
        this.requiredStrength = requiredStrength;

        this.agentsRole = new HashMap<>();
    }

    // Initialise les informations de la coalition à partir d'un objet TreasureInfo.
    public CoalitionInfo(TreasureInfo treasure) {
        this.nodeId = treasure.getNodeId();
        this.type = treasure.getType();
        this.quantity = treasure.getQuantity();
        this.isLockOpen = treasure.getIsLockOpen();

        this.requiredLockPick = treasure.getRequiredLockPick();
        this.requiredStrength = treasure.getRequiredStrength();

        this.agentsRole = new HashMap<>();
    }

    // =========================================================================================================
    
    // Retourne/Définit l'identifiant du nœud contenant le trésor.
    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    // Retourne/Définit le type de trésor.
    public Observation getType() { return type; }
    public void setType(Observation type) { this.type = type; }

    // Indique/Définit  si le trésor est accessible sans crochetage.
    public boolean isLockOpen() { return this.isLockOpen; }
    public void setIsLockOpen(boolean b) { this.isLockOpen = b; }

    // Retourne/Définit la quantité de trésor disponible.
    public int getQuantity() { return this.quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    // Retourne/Définit le niveau de crochetage requis pour accéder au trésor.
    public int getRequiredLockPick() { return requiredLockPick; }
    public void setRequiredLockPick(int requiredLockPick) { this.requiredLockPick = requiredLockPick; }

    // Retourne/Définit la force requise pour accéder au trésor.
    public int getRequiredStrength() { return requiredStrength; }
    public void getRequiredStrength(int requiredStrength) { this.requiredStrength = requiredStrength; }

    // =========================================================================================================

    // Vérifie si un agent fait partie de la coalition.
    public boolean hasAgent(String agentName) {
        return this.agentsRole.containsKey(agentName);
    }

    // Retourne la map des rôles des agents dans la coalition.
    public Map<String, COALITION_ROLES> getAgentsRole() {
        return this.agentsRole;
    }

    // Retourne le rôle d'un agent spécifique dans la coalition.
    public COALITION_ROLES getAgentRole(String agentName) {
        return  this.agentsRole.get(agentName);
    }

    // =========================================================================================================

    // Ajoute un agent avec le rôle de collecteur à la coalition.
    public void addCollector(String agentName) {
        this.agentsRole.put(agentName, COALITION_ROLES.COLLECTOR);
    }

    // Ajoute un agent avec le rôle de silo à la coalition.
    public void addSilo(String agentName) {
        this.agentsRole.put(agentName, COALITION_ROLES.SILO);
    }

    // Ajoute un agent avec le rôle de helper à la coalition.
    public void addHelper(String agentName) {
        this.agentsRole.put(agentName, COALITION_ROLES.HELPER);
    }
}
