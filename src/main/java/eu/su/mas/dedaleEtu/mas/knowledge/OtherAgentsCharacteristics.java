package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentType;



public class OtherAgentsCharacteristics implements Serializable {
    
    private static final long serialVersionUID = -1333959882640838272L;

    private Map<String, AgentType> otherAgentsType;
    private Map<String, Set<Couple<Observation, Integer>>> otherAgentsExpertise;
    private Map<String, Observation> otherAgentsTreasureType;
    private Map<String, Boolean> hasSharedCharacteristics;





    public OtherAgentsCharacteristics() {
        this.otherAgentsType = new HashMap<>();
        this.otherAgentsExpertise = new HashMap<>();
        this.otherAgentsTreasureType = new HashMap<>();
        this.hasSharedCharacteristics = new HashMap<>();
    }

    public OtherAgentsCharacteristics(List<String> list_agentNames) {
        this.otherAgentsType = new HashMap<>();
        this.otherAgentsExpertise = new HashMap<>();
        this.otherAgentsTreasureType = new HashMap<>();
        this.hasSharedCharacteristics = new HashMap<>();
        for (String agentName : list_agentNames) {
            this.otherAgentsType.put(agentName, null);
            this.otherAgentsExpertise.put(agentName, null);
            this.otherAgentsTreasureType.put(agentName, null);
            this.hasSharedCharacteristics.put(agentName, false);
        }
    }

    

    public boolean hasSharedCharacteristicsTo(String agentName) {
        return this.hasSharedCharacteristics.get(agentName);
    }

    public void markSharedCharacteristicsTo(String agentName) {
        this.hasSharedCharacteristics.put(agentName, true);
    }

    public void updateCharacteristics(String agentName, AgentType type, Set<Couple<Observation, Integer>> expertise, Observation treasureType) {
        this.otherAgentsType.put(agentName, type);
        this.otherAgentsExpertise.put(agentName, expertise);
        this.otherAgentsTreasureType.put(agentName, treasureType);
    }

    public AgentType getType(String agentName) {
        return this.otherAgentsType.get(agentName);
    }

    public Map<String, AgentType> getAllTypes() {
        return this.otherAgentsType;
    }

    public Set<Couple<Observation, Integer>> getExpertise(String agentName) {
        return this.otherAgentsExpertise.get(agentName);
    }

    public Map<String, Set<Couple<Observation, Integer>>> getAllExpertise() {
        return this.otherAgentsExpertise;
    }

    public Observation getTreasureType(String agentName) {
        return this.otherAgentsTreasureType.get(agentName);
    }

    public Map<String, Observation> getAllTreasureType() {
        return this.otherAgentsTreasureType;
    }
}
