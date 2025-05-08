package eu.su.mas.dedaleEtu.mas.msgObjects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentType;

public class CharacteristicsFloodMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Map<String, CharacteristicsMessage> characteristics;

    public CharacteristicsFloodMessage() {
        this.characteristics = new HashMap<>();
    }

    public Map<String, CharacteristicsMessage> getCharacteristics() {
        return characteristics;
    }

    public void addCharacteristics(String agentName, AgentType type, Observation treasureType, int space, int lockpick, int strength) {
        this.characteristics.put(agentName, new CharacteristicsMessage(type, treasureType, space, lockpick, strength));
    }

    public void addCharacteristics(CharacteristicsFloodMessage CFM) {
        this.characteristics.putAll(CFM.getCharacteristics());
    }
}