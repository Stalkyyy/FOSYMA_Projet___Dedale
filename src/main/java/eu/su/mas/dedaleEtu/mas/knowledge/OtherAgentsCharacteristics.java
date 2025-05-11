package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentType;
import eu.su.mas.dedaleEtu.mas.msgObjects.CharacteristicsFloodMessage;
import eu.su.mas.dedaleEtu.mas.msgObjects.CharacteristicsMessage;



public class OtherAgentsCharacteristics implements Serializable {
    
    private static final long serialVersionUID = -1333959882640838272L;

    // Map associant chaque agent à ses caractéristiques.
    private Map<String, AgentType> otherAgentsType;

    private Map<String, Observation> otherAgentsTreasureType;
    private Map<String, Integer> otherAgentsSpace;
    private Map<String, Integer> otherAgentsLockpick;
    private Map<String, Integer> otherAgentsStrength;
    private Map<String, Boolean> hasSharedCharacteristics;


    // Initialise les maps pour stocker les caractéristiques des agents.
    public OtherAgentsCharacteristics() {
        this.otherAgentsType = new HashMap<>();
        this.otherAgentsTreasureType = new HashMap<>();
        this.otherAgentsSpace = new HashMap<>();
        this.otherAgentsLockpick = new HashMap<>();
        this.otherAgentsStrength = new HashMap<>();
        this.hasSharedCharacteristics = new HashMap<>();
    }

    // Initialise les maps avec les noms des agents et des valeurs par défaut.
    public OtherAgentsCharacteristics(List<String> list_agentNames) {
        this.otherAgentsType = new HashMap<>();
        this.otherAgentsTreasureType = new HashMap<>();
        this.otherAgentsSpace = new HashMap<>();
        this.otherAgentsLockpick = new HashMap<>();
        this.otherAgentsStrength = new HashMap<>();
        this.hasSharedCharacteristics = new HashMap<>();

        for (String agentName : list_agentNames) {
            this.otherAgentsType.put(agentName, null);
            this.otherAgentsTreasureType.put(agentName, null);
            this.otherAgentsSpace.put(agentName, null);
            this.otherAgentsLockpick.put(agentName, null);
            this.otherAgentsStrength.put(agentName, null);
            this.hasSharedCharacteristics.put(agentName, false);
        }
    }

    

    // Vérifie si les caractéristiques ont été partagées avec un agent donné.
    public boolean hasSharedCharacteristicsTo(String agentName) {
        return this.hasSharedCharacteristics.get(agentName);
    }

    // Marque les caractéristiques comme partagées avec un agent donné.
    public void markSharedCharacteristicsTo(String agentName) {
        this.hasSharedCharacteristics.put(agentName, true);
    }

    // Met à jour les caractéristiques d'un agent.
    public void updateCharacteristics(String agentName, AgentType type, Observation treasureType, int space, int lockpick, int strength) {
        this.otherAgentsType.put(agentName, type);
        this.otherAgentsTreasureType.put(agentName, treasureType);
        this.otherAgentsSpace.put(agentName, space);
        this.otherAgentsLockpick.put(agentName, lockpick);
        this.otherAgentsStrength.put(agentName, strength);
    }

    // Met à jour les caractéristiques des agents à partir d'un message de flooding.
    public void updateCharacteristics(CharacteristicsFloodMessage CFM) {
        for (Map.Entry<String, CharacteristicsMessage> entry : CFM.getCharacteristics().entrySet()) {
            String agentName = entry.getKey();
            CharacteristicsMessage CM = entry.getValue();

            updateCharacteristics(agentName, CM.getType(), CM.getTreasureType(), CM.getSpace(), CM.getLockpick(), CM.getStrength());
        }
    }

    // Retourne le type d'un agent donné.
    public AgentType getAgentType(String agentName) {
        return this.otherAgentsType.get(agentName);
    }

    //Retourne une map contenant les types de tous les agents.
    public Map<String, AgentType> getAllTypes() {
        return this.otherAgentsType;
    }

    // Retourne le type de trésor manipulé par un agent donné.
    public Observation getTreasureType(String agentName) {
        return this.otherAgentsTreasureType.get(agentName);
    }

    // Retourne une map contenant les types de trésors manipulés par tous les agents.
    public Map<String, Observation> getAllTreasureType() {
        return this.otherAgentsTreasureType;
    }

    // Retourne l'espace disponible dans le sac à dos d'un agent donné.
    public int getSpace(String agentName) {
        return this.otherAgentsSpace.get(agentName);
    }
    // Retourne une map contenant l'espace disponible pour tous les agents.
    public Map<String, Integer> getAllSpace() {
        return this.otherAgentsSpace;
    }

    // Retourne le niveau de crochetage d'un agent donné.
    public int getLockpick(String agentName) {
        return this.otherAgentsLockpick.get(agentName);
    }

    // Retourne une map contenant les niveaux de crochetage de tous les agents.
    public Map<String, Integer> getAllLockpick() {
        return this.otherAgentsLockpick;
    }

    // Retourne la force d'un agent donné.
    public int getStrength(String agentName) {
        return this.otherAgentsStrength.get(agentName);
    }

    // Retourne une map contenant les forces de tous les agents.
    public Map<String, Integer> getAllStrength() {
        return this.otherAgentsStrength;
    }
}
