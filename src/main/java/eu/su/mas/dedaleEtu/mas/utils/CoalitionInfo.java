package eu.su.mas.dedaleEtu.mas.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import eu.su.mas.dedale.env.Observation;

public class CoalitionInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum COALITION_ROLES {
        COLLECTOR(3),
        SILO(2),
        HELPER(1);

        private int deadlockPriority;
        COALITION_ROLES(int priority) {
            this.deadlockPriority = priority;
        }

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
     * Initialise l'objet.
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

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public Observation getType() { return type; }
    public void setType(Observation type) { this.type = type; }

    public boolean isLockOpen() { return this.isLockOpen; }
    public void setIsLockOpen(boolean b) { this.isLockOpen = b; }

    public int getQuantity() { return this.quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getRequiredLockPick() { return requiredLockPick; }
    public void setRequiredLockPick(int requiredLockPick) { this.requiredLockPick = requiredLockPick; }

    public int getRequiredStrength() { return requiredStrength; }
    public void getRequiredStrength(int requiredStrength) { this.requiredStrength = requiredStrength; }

    // =========================================================================================================

    public boolean hasAgent(String agentName) {
        return this.agentsRole.containsKey(agentName);
    }

    public Map<String, COALITION_ROLES> getAgentsRole() {
        return this.agentsRole;
    }

    public COALITION_ROLES getAgentRole(String agentName) {
        return  this.agentsRole.get(agentName);
    }

    // =========================================================================================================

    public void addCollector(String agentName) {
        this.agentsRole.put(agentName, COALITION_ROLES.COLLECTOR);
    }

    public void addSilo(String agentName) {
        this.agentsRole.put(agentName, COALITION_ROLES.SILO);
    }

    public void addHelper(String agentName) {
        this.agentsRole.put(agentName, COALITION_ROLES.HELPER);
    }
}
