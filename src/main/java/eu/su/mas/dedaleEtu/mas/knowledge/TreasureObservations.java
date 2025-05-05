package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import eu.su.mas.dedaleEtu.mas.msgObjects.TreasureFloodMessage;
import eu.su.mas.dedaleEtu.mas.msgObjects.TreasureMessage;
import eu.su.mas.dedaleEtu.mas.utils.TreasureInfo;

/**
 * Cette classe stocke et gère les observations des trésors faites par les agents sur une topologie.
 * 
 * @author PINHO FERNANDES Enzo - BEN SALAH Adel
 */
public class TreasureObservations implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Map<String, TreasureInfo> treasures;
    private Map<String, Long> timestamps;


    /**
     * Initialise l'objet.
     */
    public TreasureObservations() {
        this.treasures = new HashMap<>();
        this.timestamps = new HashMap<>();
    }

    /**
     * Retourne les observations de trésors faites sur les noeuds visités.
     * 
     * @return Les observations sous forme de Map<NodeId, TreasureInfo>
     */
    public Map<String, TreasureInfo> getTreasures() {
        return this.treasures;
    }

    /**
     * Retourne les timestamps associés à chaque trésor.
     * 
     * @return les timestamps sous forme de Map<NodeId, timestamp>
     */
    public Map<String, Long> getTimestamps() {
        return this.timestamps;
    }



    /**
     * L'agent met à jour le trésor d'un noeud. Le timestamp sera calculé à l'appel.
     * 
     * @param nodeId
     * @param attributes
     */
    public void updateObservations(String nodeId, TreasureInfo treasure) {
        this.treasures.put(nodeId, treasure);
        this.timestamps.put(nodeId, System.currentTimeMillis());
    }

    /**
     * L'agent met à jour le trésor d'un noeud. Le timestamp est fourni par l'utilisateur.
     * 
     * @param nodeId
     * @param attributes
     * @param timestamp
     */
    public void updateObservations(String nodeId, TreasureInfo treasure, Long timestamp) {
        this.treasures.put(nodeId, treasure);
        this.timestamps.put(nodeId, timestamp);
    }



    /**
     * L'agent va mettre à jour ses observations des trésor d'un noeud suivant les observations reçues d'un autre agent.
     * Si son observation est plus récente, il gardera la sienne. Sinon, il gardera celle de l'autre.
     * 
     * @param other 
     */
    public void mergeObservations(TreasureObservations other) {
        for (String nodeId : other.treasures.keySet()) {
            // TreasureInfo currentTreasure = this.treasures.get(nodeId);
            TreasureInfo otherTreasure = other.treasures.get(nodeId);

            Long currentTimestamp = this.timestamps.get(nodeId);
            Long otherTimestamp = other.timestamps.get(nodeId);

            if ((currentTimestamp == null) || (otherTimestamp > currentTimestamp)) {
                this.treasures.put(nodeId, otherTreasure);
                this.timestamps.put(nodeId, otherTimestamp);
            }
        }
    }

    public void mergeObservations(TreasureFloodMessage TFM) {
        for (Map.Entry<String, TreasureMessage> entry : TFM.getTreasures().entrySet()) {
            TreasureMessage TM = entry.getValue();
            mergeObservations(TM.getTreasures());
        }
    }



    /**
     * Renvoie un nouveau TreasureObservations où l'on fait la différence des trésors entre l'objet actuel et l'objet en paramètre.
     * 
     * @param other
     * @return une nouvelle instance de TreasureObservations contenant que les différences entre l'objet actuel et l'objet `other`.
     */
    public TreasureObservations diffObservations(TreasureObservations other) {
        TreasureObservations uniqueObs = new TreasureObservations();

        if (other == null) {
            uniqueObs.treasures.putAll(new HashMap<>(this.treasures));
            uniqueObs.timestamps.putAll(new HashMap<>(this.timestamps));
            return uniqueObs;
        }

        for (String nodeId : this.treasures.keySet()) {
            TreasureInfo currentTreasure = this.treasures.get(nodeId);
            // TreasureInfo otherTreasure = other.treasures.get(nodeId);

            Long currentTimestamp = this.timestamps.get(nodeId);
            Long otherTimestamp = other.timestamps.get(nodeId);

            if (otherTimestamp == null || currentTimestamp > otherTimestamp) {
                uniqueObs.treasures.put(nodeId, currentTreasure);
                uniqueObs.timestamps.put(nodeId, currentTimestamp);
            }
        }

        return uniqueObs;
    }


    /**
     * Crée une copie de l'objet actuel.
     * 
     * @return une nouvelle instance de NodeObservations avec les mêmes données.
     */
    public TreasureObservations copy() {
        TreasureObservations copy = new TreasureObservations();
        copy.treasures.putAll(new HashMap<>(this.treasures));
        copy.timestamps.putAll(new HashMap<>(this.timestamps));
        return copy;
    }
}
