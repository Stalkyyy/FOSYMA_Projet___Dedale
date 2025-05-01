package eu.su.mas.dedaleEtu.mas.managers;

import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import dataStructures.tuple.Couple;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisionManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private AbstractAgent agent;

    public VisionManager(AbstractAgent agent) {
        this.agent = agent;
    }

    // ========================================================================

    // <LocationID, agentName>
    public Map<String, String> getAgentsNearby() {
        Map<String, String> neighbors = new HashMap<>();
        List<Couple<eu.su.mas.dedale.env.Location, List<Couple<Observation, String>>>> lobs = agent.observe();

        for (Couple<eu.su.mas.dedale.env.Location, List<Couple<Observation, String>>> obs : lobs) {
            String locationId = obs.getLeft().getLocationId();
            for (Couple<Observation, String> attribute : obs.getRight()) {
                if (attribute.getLeft() == Observation.AGENTNAME) {
                    neighbors.put(locationId, attribute.getRight());
                }
            }
        }
        return neighbors;
    }

    public boolean isAgentNearby(String agentName) {
        List<Couple<eu.su.mas.dedale.env.Location, List<Couple<Observation, String>>>> lobs = agent.observe();

        for (Couple<eu.su.mas.dedale.env.Location, List<Couple<Observation, String>>> obs : lobs) {
            for (Couple<Observation, String> attribute : obs.getRight()) {
                if ((attribute.getLeft() == Observation.AGENTNAME) && (agentName.compareTo(attribute.getRight()) == 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    // ========================================================================

    public List<String> nodeAvailableList() {
        List<String> listNodes = new ArrayList<>();

        List<Couple<eu.su.mas.dedale.env.Location, List<Couple<Observation, String>>>> lobs = agent.observe();

        for (Couple<eu.su.mas.dedale.env.Location, List<Couple<Observation, String>>> obs : lobs) {
            String locationId = obs.getLeft().getLocationId();

            boolean canMoveTo = true;
            for (Couple<Observation, String> attribute : obs.getRight()) {
                if (attribute.getLeft() == Observation.AGENTNAME) {
                    canMoveTo = false;
                    break;
                }
            }

            if (canMoveTo)
                listNodes.add(locationId);
        }
        
        return listNodes;
    }
}