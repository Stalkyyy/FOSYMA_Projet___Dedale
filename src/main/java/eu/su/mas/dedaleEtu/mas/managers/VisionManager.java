package eu.su.mas.dedaleEtu.mas.managers;

import eu.su.mas.dedale.env.Location;
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

    // Initialise le gestionnaire de vision pour un agent donné.
    public VisionManager(AbstractAgent agent) {
        this.agent = agent;
    }

    // ========================================================================

    // <LocationID, agentName>
    // Retourne une map des agents visibles à proximité.
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

    // Vérifie si un agent spécifique est visible à proximité.
    public String isAgentNearby(String agentName) {
        List<Couple<eu.su.mas.dedale.env.Location, List<Couple<Observation, String>>>> lobs = agent.observe();

        for (Couple<eu.su.mas.dedale.env.Location, List<Couple<Observation, String>>> obs : lobs) {
            String locationId = obs.getLeft().getLocationId();
            for (Couple<Observation, String> attribute : obs.getRight()) {
                if ((attribute.getLeft() == Observation.AGENTNAME) && (agentName.compareTo(attribute.getRight()) == 0)) {
                    return locationId;
                }
            }
        }
        return null;
    }

    // ========================================================================

    // Retourne une liste des nœuds accessibles (sans agents présents).
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
    
    // Vérifie si des nœuds accessibles sont disponibles.
    public boolean hasNodeAvailable() {
        return nodeAvailableList().size() > 0;
    }

    // ========================================================================

    // Met à jour les informations sur les trésors observés à proximité.
    public void updateTreasure() {
        String currentNodeId = agent.getCurrentPosition().getLocationId();
        List<Couple<eu.su.mas.dedale.env.Location, List<Couple<Observation, String>>>> lobs = agent.observe();

        for (Couple<Location, List<Couple<Observation, String>>> observation : lobs) {
            String observedNodeId = observation.getLeft().getLocationId();
            List<Couple<Observation, String>> attributes = observation.getRight();

            // On update la liste des trésors si c'est le noeud actuel.
            if (currentNodeId.equals(observedNodeId)) {
                agent.treasureMgr.update(currentNodeId, attributes);
            }
        }
    }
}