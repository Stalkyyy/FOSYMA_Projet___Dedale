package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AssignmentGiven implements Serializable {

    private static final long serialVersionUID = 1L;

    enum TYPE_ASSIGNMENT {
        LOCKPICK_AGENTS,
        STRENGTH_AGENTS,
        PICKER_AGENTS;
    }

    enum STATUS_ASSIGNMENT {
        ONGOING_LOCKPICK,
        ONGOING_PICKUP,
        FINISHED;
    }

    private Map<String, STATUS_ASSIGNMENT> status;

    // Map<AgentName, <nodeId_lockpick, nodeId_strength>>
    private Map<String, Map<TYPE_ASSIGNMENT, String>> isAgentAlreadyAssigned;

    private Map<String, Set<String>> lockpickAgents;
    private Map<String, Set<String>> strengthAgents;
    private Map<String, Set<String>> pickerAgents;


    // Niveau cumulé des compétences dans les coalitions.
    private Map<String, Integer> totalLockpick;
    private Map<String, Integer> totalStrength;
    
    public AssignmentGiven(List<String> list_agentNames) {
        this.status = new HashMap<>();

        this.isAgentAlreadyAssigned = new HashMap<>();
        for (String agentName : list_agentNames) {
            Map<TYPE_ASSIGNMENT, String> map = new HashMap<>();
            map.put(TYPE_ASSIGNMENT.LOCKPICK_AGENTS, null);
            map.put(TYPE_ASSIGNMENT.STRENGTH_AGENTS, null);
            map.put(TYPE_ASSIGNMENT.PICKER_AGENTS, null);

            isAgentAlreadyAssigned.put(agentName, map);
        }

        this.lockpickAgents = new HashMap<>();
        this.strengthAgents = new HashMap<>();
        this.pickerAgents = new HashMap<>();

        this.totalLockpick = new HashMap<>();
        this.totalStrength = new HashMap<>();
    }

    // ===================================================================

    public STATUS_ASSIGNMENT getStatus(String nodeId) {
        return status.get(nodeId);
    }

    public void setStatus(String nodeId, STATUS_ASSIGNMENT statusAssignment) {
        status.put(nodeId, statusAssignment);
    }

    // ===================================================================

    public String isAgentAlreadyAssigned(String nodeId, TYPE_ASSIGNMENT type) {
        return isAgentAlreadyAssigned.get(nodeId).get(type);
    }

    public void assignAgentTo(String agentName, String nodeId, Set<TYPE_ASSIGNMENT> types) {
        for(TYPE_ASSIGNMENT type : types) {
            switch (type) {
                case TYPE_ASSIGNMENT.LOCKPICK_AGENTS: lockpickAgents.computeIfAbsent(nodeId, k -> new HashSet<>()).add(agentName);
                case TYPE_ASSIGNMENT.STRENGTH_AGENTS: strengthAgents.computeIfAbsent(nodeId, k -> new HashSet<>()).add(agentName);
                case TYPE_ASSIGNMENT.PICKER_AGENTS  : pickerAgents.computeIfAbsent(nodeId, k -> new HashSet<>()).add(agentName);
            }

            isAgentAlreadyAssigned.get(agentName).put(type, nodeId);
        }
    }

    public void unassignAgent(String agentName, Set<TYPE_ASSIGNMENT> types) {
        for(TYPE_ASSIGNMENT type : types) {
            String nodeId = isAgentAlreadyAssigned.get(agentName).get(type);

            switch (type) {
                case TYPE_ASSIGNMENT.LOCKPICK_AGENTS: lockpickAgents.get(nodeId).remove(agentName);
                case TYPE_ASSIGNMENT.STRENGTH_AGENTS: strengthAgents.get(nodeId).remove(agentName);
                case TYPE_ASSIGNMENT.PICKER_AGENTS  : pickerAgents.get(nodeId).remove(agentName);
            }

            isAgentAlreadyAssigned.get(agentName).put(type, null);
        }
    }

    // ===================================================================

    public Map<String, Set<String>> getLockpickAgents(String nodeId) {
        return lockpickAgents;
    }

    public void setLockpickAgents(String nodeId, Set<String> agentNames) {
        lockpickAgents.put(nodeId, agentNames);
    }

    // ===================================================================

    public Map<String, Set<String>> getStrengthAgents(String nodeId) {
        return strengthAgents;
    }

    public void setStrengthAgents(String nodeId, Set<String> agentNames) {
        strengthAgents.put(nodeId, agentNames);
    }

    // ===================================================================

    public Map<String, Set<String>> getPickerAgents(String nodeId) {
        return pickerAgents;
    }

    public void setPickerAgents(String nodeId, Set<String> agentNames) {
        pickerAgents.put(nodeId, agentNames);
    }

    // ===================================================================

    public int getTotalLockpick(String nodeId) {
        return totalLockpick.get(nodeId);
    }

    public void setTotalLockpick(String nodeId, int value) {
        totalLockpick.put(nodeId, value);
    }

    // ===================================================================

    public int getTotalStrength(String nodeId) {
        return totalStrength.get(nodeId);
    }

    public void setTotalStrength(String nodeId, int value) {
        totalStrength.put(nodeId, value);
    }

    // ===================================================================
}
