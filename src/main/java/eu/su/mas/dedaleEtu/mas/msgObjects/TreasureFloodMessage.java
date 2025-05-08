package eu.su.mas.dedaleEtu.mas.msgObjects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TreasureFloodMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, TreasureMessage> treasures;  // Map contenant les trésors associés aux agents.

    // Initialise un message de flooding contenant les informations sur les trésors.
    public TreasureFloodMessage() {
        this.treasures = new HashMap<>();
    }

    // Retourne les trésors inclus dans le message.
    public Map<String, TreasureMessage> getTreasures() {
        return treasures;
    }

    // Ajoute un trésor au message pour un agent spécifique.
    public void addTreasure(String agentName, TreasureMessage treasures) {
        this.treasures.put(agentName, treasures);
    }

    // Fusionne les trésors d'un autre message de flooding avec ceux du message actuel.
    public void addTreasure(TreasureFloodMessage TFM) {
        this.treasures.putAll(TFM.getTreasures());
    }
}
