package eu.su.mas.dedaleEtu.mas.msgObjects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TreasureFloodMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, TreasureMessage> treasures;

    public TreasureFloodMessage() {
        this.treasures = new HashMap<>();
    }

    public Map<String, TreasureMessage> getTreasures() {
        return treasures;
    }

    public void addTreasure(String agentName, TreasureMessage treasures) {
        this.treasures.put(agentName, treasures);
    }

    public void addTreasure(TreasureFloodMessage TFM) {
        this.treasures.putAll(TFM.getTreasures());
    }
}
