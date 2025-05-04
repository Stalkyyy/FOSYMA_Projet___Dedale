package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentType;



public class OtherAgentsCharacteristics implements Serializable {
    
    private static final long serialVersionUID = -1333959882640838272L;

    private Map<String, AgentType> otherAgentsType;

    private Map<String, Observation> otherAgentsTreasureType;
    private Map<String, Integer> otherAgentsSpace;
    private Map<String, Integer> otherAgentsLockpick;
    private Map<String, Integer> otherAgentsStrength;
    private Map<String, Boolean> hasSharedCharacteristics;



    public OtherAgentsCharacteristics() {
        this.otherAgentsType = new HashMap<>();
        this.otherAgentsTreasureType = new HashMap<>();
        this.otherAgentsSpace = new HashMap<>();
        this.otherAgentsLockpick = new HashMap<>();
        this.otherAgentsStrength = new HashMap<>();
        this.hasSharedCharacteristics = new HashMap<>();
    }

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

    

    public boolean hasSharedCharacteristicsTo(String agentName) {
        return this.hasSharedCharacteristics.get(agentName);
    }

    public void markSharedCharacteristicsTo(String agentName) {
        this.hasSharedCharacteristics.put(agentName, true);
    }

    public void updateCharacteristics(String agentName, AgentType type, Observation treasureType, int space, int lockpick, int strength) {
        this.otherAgentsType.put(agentName, type);
        this.otherAgentsTreasureType.put(agentName, treasureType);
        this.otherAgentsSpace.put(agentName, space);
        this.otherAgentsLockpick.put(agentName, lockpick);
        this.otherAgentsStrength.put(agentName, strength);
    }

    public AgentType getAgentType(String agentName) {
        return this.otherAgentsType.get(agentName);
    }

    public Map<String, AgentType> getAllTypes() {
        return this.otherAgentsType;
    }

    public Observation getTreasureType(String agentName) {
        return this.otherAgentsTreasureType.get(agentName);
    }

    public Map<String, Observation> getAllTreasureType() {
        return this.otherAgentsTreasureType;
    }

    public int getSpace(String agentName) {
        return this.otherAgentsSpace.get(agentName);
    }

    public Map<String, Integer> getAllSpace() {
        return this.otherAgentsSpace;
    }

    public int getLockpick(String agentName) {
        return this.otherAgentsLockpick.get(agentName);
    }

    public Map<String, Integer> getAllLockpick() {
        return this.otherAgentsLockpick;
    }

    public int getStrength(String agentName) {
        return this.otherAgentsStrength.get(agentName);
    }

    public Map<String, Integer> getAllStrength() {
        return this.otherAgentsStrength;
    }
}
