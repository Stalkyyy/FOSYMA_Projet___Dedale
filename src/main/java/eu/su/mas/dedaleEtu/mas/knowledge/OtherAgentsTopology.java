package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dataStructures.serializableGraph.SerializableNode;
import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;

public class OtherAgentsTopology implements Serializable {
    
    private static final long serialVersionUID = -1333959882640838272L;

    private Map<String, SerializableSimpleGraph<String, MapAttribute>> otherAgentsTopologies;
    private Map<String, Integer> updatesSinceLastCommunication;
    private Map<String, Boolean> finishedExplo;
    private int minUpdatesBeforeCommunication = 25;


    public OtherAgentsTopology() {
        this.otherAgentsTopologies = new HashMap<>();
        this.updatesSinceLastCommunication = new HashMap<>();
        this.finishedExplo = new HashMap<>();
    }

    public OtherAgentsTopology(List<String> list_agentNames) {
        this.otherAgentsTopologies = new HashMap<>();
        this.updatesSinceLastCommunication = new HashMap<>();
        this.finishedExplo = new HashMap<>();
        for (String agentName : list_agentNames) {
            this.otherAgentsTopologies.put(agentName, new SerializableSimpleGraph<String, MapAttribute>());
            this.updatesSinceLastCommunication.put(agentName, 0);
            this.finishedExplo.put(agentName, false);
        }
    }


    public boolean canSendInfoToAgent(String agentName) {
        return (this.updatesSinceLastCommunication.get(agentName) >= this.minUpdatesBeforeCommunication)
            && !this.finishedExplo.get(agentName);
    }

    public void resetLastUpdatesAgent(String agentName) {
        this.updatesSinceLastCommunication.put(agentName, 0);
    }

    public void incrementeLastUpdates() {
        for (String agentName : this.updatesSinceLastCommunication.keySet()) {
            int lastValue = this.updatesSinceLastCommunication.get(agentName);
            this.updatesSinceLastCommunication.put(agentName, lastValue + 1);
        }
    }

    public void agentFinishedExplo(String agentName) {
        this.finishedExplo.put(agentName, true);
    }



    public void addTopology(String agentName, SerializableSimpleGraph<String, MapAttribute> topology) {
        this.otherAgentsTopologies.put(agentName, topology);
    }

    public SerializableSimpleGraph<String, MapAttribute> getTopology(String agentName) {
        return this.otherAgentsTopologies.get(agentName);
    }

    public boolean containsAgent(String agentName) {
        return this.otherAgentsTopologies.containsKey(agentName);
    }


    public void removeTopology(String agentName) {
        this.otherAgentsTopologies.remove(agentName);
    }

    public Map<String, SerializableSimpleGraph<String, MapAttribute>> getAllTopologies() {
        return this.otherAgentsTopologies;
    }


    public SerializableSimpleGraph<String, MapAttribute> diffTopology(String agentName, SerializableSimpleGraph<String, MapAttribute> mainTopology) {
        SerializableSimpleGraph<String, MapAttribute> agentTopology = this.otherAgentsTopologies.get(agentName);
        SerializableSimpleGraph<String, MapAttribute> diffTopology = new SerializableSimpleGraph<>();

        if (agentTopology == null)
            return mainTopology; // Si l'agent n'a pas de topologie, retourner la topologie principale
    
        // Parcourir les nœuds de la topologie principale
        for (SerializableNode<String, MapAttribute> node : mainTopology.getAllNodes()) {
            String nodeId = node.getNodeId();
            MapAttribute attribute = node.getNodeContent();

            // Si le nœud n'existe pas dans la topologie de l'agent, l'ajouter à la topologie de différence
            if (agentTopology.getNode(nodeId) == null) {
                diffTopology.addNode(nodeId, attribute);
            } else {
                // Si le nœud existe mais avec un attribut différent, mettre à jour l'attribut
                MapAttribute agentAttribute = agentTopology.getNode(nodeId).getNodeContent();
                if (!agentAttribute.equals(attribute)) {
                    diffTopology.addNode(nodeId, attribute);
                }
            }
        }

        // Parcourir les arêtes de la topologie principale
        for (SerializableNode<String, MapAttribute> node : mainTopology.getAllNodes()) {
            String nodeId = node.getNodeId();
            Set<String> mainEdges = mainTopology.getEdges(nodeId);
            Set<String> agentEdges = null;

            if (agentTopology.getAllNodes().contains(node))
                agentEdges = agentTopology.getEdges(nodeId);

            if (agentEdges == null) {
                for (String edge : mainEdges)
                    diffTopology.addEdge(edge, nodeId, edge);
            }

            else {
                for (String edge : mainEdges) {
                    if (!agentEdges.contains(edge))
                        diffTopology.addEdge(edge, nodeId, edge);
                }
            }
        }

        return diffTopology;
    }


    public void mergeTopology(String agentName, SerializableSimpleGraph<String, MapAttribute> newTopology) {
        SerializableSimpleGraph<String, MapAttribute> existingTopology = this.otherAgentsTopologies.get(agentName);
        if (existingTopology == null) {
            this.otherAgentsTopologies.put(agentName, newTopology);
            return;
        }


        for (SerializableNode<String, MapAttribute> node : newTopology.getAllNodes()) {
            String nodeId = node.getNodeId();
            MapAttribute attribute = node.getNodeContent();

            if (existingTopology.getNode(nodeId) == null) {
                existingTopology.addNode(nodeId, attribute);
            } else {
                // Fusionner les attributs des noeuds si nécessaire
                MapAttribute existingAttribute = existingTopology.getNode(nodeId).getNodeContent();
                if (existingAttribute != MapAttribute.closed && attribute == MapAttribute.closed) {
                    existingTopology.getNode(nodeId).setContent(MapAttribute.closed);
                }
            }
        }

        for (SerializableNode<String, MapAttribute> node : newTopology.getAllNodes()) {
            String nodeId = node.getNodeId();
            Set<String> edges = newTopology.getEdges(nodeId);

            for (String edge : edges) {
                existingTopology.addEdge(edge, nodeId, edge);
            }
        }

        this.otherAgentsTopologies.put(agentName, existingTopology);
    }
}
