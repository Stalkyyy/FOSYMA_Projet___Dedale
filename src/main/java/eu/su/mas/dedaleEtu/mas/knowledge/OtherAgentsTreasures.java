package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Cette classe stocke et gère les observations A PRIORI faites par les autres agents que soi-même.
 * 
 * @author PIHNO FERNANDES Enzo - BEN SALAH Adel
 */
public class OtherAgentsTreasures implements Serializable {
    
    private static final long serialVersionUID = -1333959882640838272L;

    private Map<String, TreasureObservations> otherAgentsTreasures;


    
    /**
     * Initialise l'objet, sans nom d'agents.
     */
    public OtherAgentsTreasures() {
        this.otherAgentsTreasures = new HashMap<>();
    }

    /**
     * Initialise l'objet, avec nom d'agents.
     */
    public OtherAgentsTreasures(List<String> list_agentNames) {
        this.otherAgentsTreasures = new HashMap<>();
        for (String agentName : list_agentNames) {
            this.otherAgentsTreasures.put(agentName, new TreasureObservations());
        }
    }


    // DANS CE PROJET, on a pas d'entrée et de sortie d'agents, donc on va prédéfinir la liste des agents en avance.
    // - D'où le manque de addAgentName...


    /**
     * Met à jour l'observation à priori d'un agent donné.
     * 
     * @param agentName
     * @param observations
     */
    public void updateObservations(String agentName, TreasureObservations observations) {
        this.otherAgentsTreasures.put(agentName, observations);
    }

    /**
     * Récupère l'observation à priori d'un agent donné.
     * 
     * @param agentName
     * @return l'objet TreasureObservations représentant l'observation à priori de `agentName`
     */
    public TreasureObservations getObservations(String agentName) {
        return this.otherAgentsTreasures.get(agentName);
    }

    /**
     * Récupère toutes les observations à priori des autres agents, que l'agent actuel possède.
     * 
     * @return une instance Map<agentName, TreasureObservations> 
     */
    public Map<String, TreasureObservations> getAllObservations() {
        return this.otherAgentsTreasures;
    }

    
    /**
     * Fusionne l'observation à priori d'un agent actuel, et une nouvelle observation.
     * On prend en compte le timestamp de l'observation pour choisir quoi garder ou non.
     * 
     * @param receiverName
     * @param obs
     */
    public void mergeObservation(String receiverName, TreasureObservations obs) {
        this.otherAgentsTreasures.get(receiverName).mergeObservations(obs);
    }
}
