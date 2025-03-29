package eu.su.mas.dedaleEtu.mas.knowledge;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import eu.su.mas.dedale.env.Observation;
import dataStructures.tuple.Couple;
import jade.util.leap.Serializable;



public class NodeObservations implements Serializable {

    private static final long serialVersionUID = -1333959882640838272L;
    
    private Map<String, List<Couple<Observation, String>>> observations;
    private Map<String, Long> timestamps;

    public NodeObservations() {
        this.observations = new HashMap<>();
        this.timestamps = new HashMap<>();
    }


    public Map<String, List<Couple<Observation, String>>> getObservations() {
        return this.observations;
    }

    public Map<String, Long> getTimestamps() {
        return this.timestamps;
    }



    /**
     * L'agent met à jour son observation d'un noeud.
     * 
     * @param nodeId
     * @param attributes
     */
    public void updateObservations(String nodeId, List<Couple<Observation, String>> attributes) {
        this.observations.put(nodeId, attributes);
        this.timestamps.put(nodeId, System.currentTimeMillis());
    }

    /**
     * agent met à jour son observation d'un noeud.
     * 
     * @param nodeId
     * @param attributes
     * @param timestamp
     */
    public void updateObservations(String nodeId, List<Couple<Observation, String>> attributes, Long timestamp) {
        this.observations.put(nodeId, attributes);
        this.timestamps.put(nodeId, timestamp);
    }


    /**
     * L'agent va mettre à jour son observation d'un noeud suivant les observations reçues d'un autre agent.
     * Si son observation est plus récente, il gardera la sienne. Sinon, il gardera celle de l'autre.
     * 
     * @param nodeId
     * @param attributes
     * @param timestamp
     */
    public void mergeObservations(NodeObservations other) {
        for (String nodeId : other.observations.keySet()) {
            List<Couple<Observation, String>> currentObservations = this.observations.get(nodeId);
            List<Couple<Observation, String>> otherObservations = other.observations.get(nodeId);

            Long currentTimestamp = other.timestamps.get(nodeId);
            Long otherTimestamp = other.timestamps.get(nodeId);

            if (currentObservations == null || (otherTimestamp > currentTimestamp)) {
                this.observations.put(nodeId, otherObservations);
                this.timestamps.put(nodeId, otherTimestamp);
            }
        }
    }
 


    public NodeObservations diffObservations(NodeObservations other) {
        NodeObservations uniqueObservations = new NodeObservations();

        // Si l'argument other est null, renvoyer une copie des observations actuelles
        if (other == null) {
            uniqueObservations.observations.putAll(new HashMap<>(this.observations));
            uniqueObservations.timestamps.putAll(new HashMap<>(this.timestamps));
            return uniqueObservations;
        }

        for (String nodeId : this.observations.keySet()) {
            List<Couple<Observation, String>> currentObservations = this.observations.get(nodeId);
            List<Couple<Observation, String>> otherObservations = other.observations.get(nodeId);


            
            if (otherObservations == null || !currentObservations.equals(otherObservations)) {
                if (otherObservations != null) 
                    // System.out.println(otherObservations.toString() + " - " + currentObservations.toString());
                uniqueObservations.observations.put(nodeId, currentObservations);
                uniqueObservations.timestamps.put(nodeId, this.timestamps.get(nodeId));
            }
        }

        return uniqueObservations;
    }


    /**
     * Crée une copie de l'objet actuel.
     * 
     * @return une nouvelle instance de NodeObservations avec les mêmes données.
     */
    public NodeObservations copy() {
        NodeObservations copy = new NodeObservations();
        copy.observations.putAll(new HashMap<>(this.observations));
        copy.timestamps.putAll(new HashMap<>(this.timestamps));
        return copy;
    }


    public boolean isEmpty() {
        return this.observations.isEmpty() && this.timestamps.isEmpty();
    }
}