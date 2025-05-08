package eu.su.mas.dedaleEtu.mas.utils;

import java.io.Serializable;

import eu.su.mas.dedale.env.Observation;

/**
 * Cette classe représente toutes les informations disponibles pour un trésor donné.
 * 
 * @author PIHNO FERNANDES Enzo - BEN SALAH Adel
 */
public class TreasureInfo implements Serializable {
    
    private static final long serialVersionUID = 1L;

    // Les informations de base des trésors.
    private String nodeId;
    private Observation type;
    private int quantity;
    private boolean IsLockOpen;

    // Le niveau d'expertise requis.
    private int requiredLockPick;
    private int requiredStrength;


    /**
     * Initialise l'objet.
     */
    public TreasureInfo(String nodeId, Observation type, int quantity, boolean IsLockOpen, int requiredLockPick, int requiredStrength) {
        this.nodeId = nodeId;
        this.type = type;
        this.quantity = quantity;
        this.IsLockOpen = IsLockOpen;
        this.requiredLockPick = requiredLockPick;
        this.requiredStrength = requiredStrength;
    }


    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public Observation getType() { return type; }
    public void setType(Observation type) { this.type = type; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public boolean getIsLockOpen() { return IsLockOpen; }
    public void setIsLockOpen(boolean isLockOpen) { this.IsLockOpen = isLockOpen; }

    public int getRequiredLockPick() { return requiredLockPick; }
    public void setRequiredLockPick(int requiredLockPick) { this.requiredLockPick = requiredLockPick; }

    public int getRequiredStrength() { return requiredStrength; }
    public void getRequiredStrength(int requiredStrength) { this.requiredStrength = requiredStrength; }

}
