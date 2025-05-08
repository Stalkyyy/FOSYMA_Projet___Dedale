package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedaleEtu.mas.knowledge.given_knowledge.MapRepresentation.MapAttribute;

/**
 * Cette classe stocke et gère les topologies A PRIORI des autres agents que soi-même.
 * 
 * @author PIHNO FERNANDES Enzo - BEN SALAH Adel
 */
public class OtherAgentsTopology implements Serializable {
    
    private static final long serialVersionUID = -1333959882640838272L;

    private Map<String, SerializableSimpleGraph<String, MapAttribute>> otherAgentsTopologies;
    private Map<String, Integer> pendingUpdatesCount;
    private Map<String, Boolean> finishedExplo;
    private int minUpdatesToShare = 12;


    /**
     * Initialise l'objet, avec nom d'agents.
     */
    public OtherAgentsTopology() {
        this.otherAgentsTopologies = new HashMap<>();
        this.pendingUpdatesCount = new HashMap<>();
        this.finishedExplo = new HashMap<>();
    }

    
    /**
     * Initialise l'objet, avec nom d'agents.
     */
    public OtherAgentsTopology(List<String> list_agentNames) {
        this.otherAgentsTopologies = new HashMap<>();
        this.pendingUpdatesCount = new HashMap<>();
        this.finishedExplo = new HashMap<>();
        for (String agentName : list_agentNames) {
            this.otherAgentsTopologies.put(agentName, new SerializableSimpleGraph<String, MapAttribute>());
            this.pendingUpdatesCount.put(agentName, 0);
            this.finishedExplo.put(agentName, false);
        }
    }


    // DANS CE PROJET, on a pas d'entrée et de sortie d'agents, donc on va prédéfinir la liste des agents en avance.
    // - D'où le manque de addAgentName...

    // Retourne le nombre de mises à jour en attente pour un agent donné.
    public int getPendingUpdatesCountOf(String agentName) {
        return this.pendingUpdatesCount.get(agentName);
    }

    // Retourne le nombre minimum de mises à jour avant de partager.
    public int getMinUpdatesToShare() {
        return this.minUpdatesToShare;
    }



    /**
     * Remet à 0 le nombre de mise à jour faites depuis le dernier partage avec l'agent donné.
     * 
     * @param agentName
     */
    public void resetLastUpdatesAgent(String agentName) {
        this.pendingUpdatesCount.put(agentName, 0);
    }

    /**
     * le nombre de mise à jour faites depuis le dernier partage avec l'agent donné.
     */
    public void incrementeLastUpdates() {
        for (String agentName : this.pendingUpdatesCount.keySet()) {
            int lastValue = this.pendingUpdatesCount.get(agentName);
            this.pendingUpdatesCount.put(agentName, lastValue + 1);
        }
    }

    /**
     * Marque l'exploration de l'agent donné comme terminé.
     * 
     * @param agentName
     */
    public void markExplorationComplete(String agentName) {
        this.finishedExplo.put(agentName, true);
    }

    /**
     * Vérifie si l'agent donné a terminé son exploration à priori.
     * 
     * @param agentName
     * @param topology
     */
    public boolean hasFinishedExplo(String agentName) {
        return this.finishedExplo.get(agentName);
    }

    /**
     * Met à jour la topologie à priori de l'agent donné.
     * 
     * @param agentName
     * @param topology
     */
    public void updateTopology(String agentName, SerializableSimpleGraph<String, MapAttribute> topology) {
        this.otherAgentsTopologies.put(agentName, topology);
    }

    /**
     * Récupère la topologie à priori de l'agent donné.
     * 
     * @param agentName
     * @return la topologie sérialisée de l'agent donné.
     */
    public SerializableSimpleGraph<String, MapAttribute> getTopology(String agentName) {
        return this.otherAgentsTopologies.get(agentName);
    }

    /**
     * Récupère toutes les topologies à priori des autres agents, que l'agent actuel possède.
     * 
     * @return une instance Map<agentName, topologie_sérialisée>
     */
    public Map<String, SerializableSimpleGraph<String, MapAttribute>> getAllTopologies() {
        return this.otherAgentsTopologies;
    }
}
