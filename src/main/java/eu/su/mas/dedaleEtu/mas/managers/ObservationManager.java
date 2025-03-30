package eu.su.mas.dedaleEtu.mas.managers;

import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.MyAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.NodeObservations;
import dataStructures.tuple.Couple;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObservationManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private MyAgent agent;

    public ObservationManager(MyAgent agent) {
        this.agent = agent;
    }

    public void update(String nodeId, List<Couple<Observation, String>> attributes) {
        agent.getMyObservations().updateObservations(nodeId, attributes);
    }

    public Map<String, String> getNeighboringAgents() {
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

    // ========================================================================

    public NodeObservations difference(NodeObservations obs) {
        return agent.getMyObservations().diffObservations(obs);
    }

    public NodeObservations diffObservations(NodeObservations obs1, NodeObservations obs2) {

        NodeObservations diffObservations = new NodeObservations();

        // Si obs2 est null, copier toutes les observations de obs1
        if (obs2 == null) {
            diffObservations.getObservations().putAll(new HashMap<>(obs1.getObservations()));
            diffObservations.getTimestamps().putAll(new HashMap<>(obs1.getTimestamps()));
            return diffObservations;
        }

        // Parcourir les observations de obs1
        for (String nodeId : obs1.getObservations().keySet()) {
            List<Couple<Observation, String>> obs1_Obs = obs1.getObservations().get(nodeId);
            Long obs1_tsp = obs1.getTimestamps().get(nodeId);

            List<Couple<Observation, String>> obs2_Obs = obs2.getObservations().get(nodeId);
            Long obs2_tsp = obs2.getTimestamps().get(nodeId);


            // Si le nœud n'existe pas dans obs2 ou si les observations diffèrent (en prenant compte du timestamp), ajouter à diffObservations
            if (obs2_Obs == null || (!obs1_Obs.equals(obs2_Obs) && obs1_tsp > obs2_tsp)) {
                diffObservations.getObservations().put(nodeId, obs1_Obs);
                diffObservations.getTimestamps().put(nodeId, obs1.getTimestamps().get(nodeId));
            }
        }

        return diffObservations;
    }

    // ========================================================================

    public void merge(NodeObservations obs) {
        agent.getMyObservations().mergeObservations(obs);
    }


    public NodeObservations mergeObservations(NodeObservations obs1, NodeObservations obs2) {
        NodeObservations mergedObservations = obs1.copy();
        mergedObservations.mergeObservations(obs2);
        return mergedObservations;
    }
}