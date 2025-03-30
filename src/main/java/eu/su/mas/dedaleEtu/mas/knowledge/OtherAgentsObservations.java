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
public class OtherAgentsObservations implements Serializable {
    
    private static final long serialVersionUID = -1333959882640838272L;

    private Map<String, NodeObservations> otherAgentsObservations;

    /**
     * Initialise l'objet, sans nom d'agents.
     */
    public OtherAgentsObservations() {
        this.otherAgentsObservations = new HashMap<>();
    }

    /**
     * Initialise l'objet, avec nom d'agents.
     */
    public OtherAgentsObservations(List<String> list_agentNames) {
        this.otherAgentsObservations = new HashMap<>();
        for (String agentName : list_agentNames) {
            this.otherAgentsObservations.put(agentName, new NodeObservations());
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
    public void updateObservations(String agentName, NodeObservations observations) {
        this.otherAgentsObservations.put(agentName, observations);
    }

    /**
     * Récupère l'observation à priori d'un agent donné.
     * 
     * @param agentName
     * @return l'objet NodeObservations représentant l'observation à priori de `agentName`
     */
    public NodeObservations getObservations(String agentName) {
        return this.otherAgentsObservations.get(agentName);
    }

    /**
     * Vérifie si l'agent donné est déjà initialisé dans la mémoire.
     * 
     * @param agentName
     * @return True si c'est le cas, sinon False.
     */
    public boolean containsAgent(String agentName) {
        return this.otherAgentsObservations.containsKey(agentName);
    }

    /**
     * Retire l'observation à priori de l'agent donné.
     * 
     * @param agentName
     */
    public void removeObservations(String agentName) {
        this.otherAgentsObservations.remove(agentName);
    }

    /**
     * Récupère toutes les observations à priori des autres agents, que l'agent actuel possède.
     * 
     * @return une instance Map<agentName, NodeObservations> 
     */
    public Map<String, NodeObservations> getAllObservations() {
        return this.otherAgentsObservations;
    }

    
    /**
     * Fusionne l'observation à priori d'un agent actuel, et une nouvelle observation.
     * On prend en compte le timestamp de l'observation pour choisir quoi garder ou non.
     * 
     * @param receiverName
     * @param obs
     */
    public void mergeObservation(String receiverName, NodeObservations obs) {
        this.otherAgentsObservations.get(receiverName).mergeObservations(obs);
    }
}
