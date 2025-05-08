package eu.su.mas.dedaleEtu.mas.msgObjects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentType;

public class CharacteristicsFloodMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Map<String, CharacteristicsMessage> characteristics;

    // Initialise un message de flooding contenant les caractéristiques des agents.
    public CharacteristicsFloodMessage() {
        this.characteristics = new HashMap<>();
    }

    // Retourne les caractéristiques des agents contenues dans le message.
    public Map<String, CharacteristicsMessage> getCharacteristics() {
        return characteristics;
    }

    // Ajoute les caractéristiques d'un agent au message.
    public void addCharacteristics(String agentName, AgentType type, Observation treasureType, int space, int lockpick, int strength) {
        this.characteristics.put(agentName, new CharacteristicsMessage(type, treasureType, space, lockpick, strength));
    }

    // Fusionne les caractéristiques d'un autre message de flooding avec celles du message actuel.
    public void addCharacteristics(CharacteristicsFloodMessage CFM) {
        this.characteristics.putAll(CFM.getCharacteristics());
    }
}