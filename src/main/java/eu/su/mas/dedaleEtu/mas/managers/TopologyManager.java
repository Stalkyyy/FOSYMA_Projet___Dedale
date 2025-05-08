package eu.su.mas.dedaleEtu.mas.managers;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import dataStructures.serializableGraph.SerializableNode;
import dataStructures.serializableGraph.SerializableSimpleGraph;
import dataStructures.tuple.Couple;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.given_knowledge.MapRepresentation.MapAttribute;

public class TopologyManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private AbstractAgent agent;
    
    // Initialise le gestionnaire de topologie pour un agent donné.
    public TopologyManager(AbstractAgent agent) {
        this.agent = agent;
    }

    // ========================================================================

    // Ajoute un nœud à la carte avec un attribut spécifique.
    public void addNode(String nodeId,  MapAttribute mapAttribute) {
        agent.getMyMap().addNode(nodeId, mapAttribute);
    }

    // Ajoute un nouveau nœud à la carte.
    public boolean addNewNode(String nodeId) {
        return agent.getMyMap().addNewNode(nodeId);
    }

    // Ajoute une arête entre deux nœuds.
    public void addEdge(String node1, String node2) {
        agent.getMyMap().addEdge(node1, node2);
    }

    // Vérifie si la carte contient des nœuds ouverts.
    public boolean hasOpenNodes() {
        return agent.getMyMap().hasOpenNode();
    }

    // Incrémente le compteur de mises à jour de topologie.
    public void incrementUpdateCount() {
        agent.getOtherAgentsTopology().incrementeLastUpdates();
    }

    // ========================================================================

    // Calcule la différence entre la topologie locale et une autre topologie.
    public SerializableSimpleGraph<String, MapAttribute> difference(SerializableSimpleGraph<String, MapAttribute> tp) {
        return diffTopology(agent.getMyMap().getSerializableGraph(), tp);
    }

    // Calcule la différence entre deux topologies.
    public SerializableSimpleGraph<String, MapAttribute> diffTopology(SerializableSimpleGraph<String, MapAttribute> tp1, SerializableSimpleGraph<String, MapAttribute> tp2) {
        SerializableSimpleGraph<String, MapAttribute> diffTopology = new SerializableSimpleGraph<>();

        if (tp2 == null) 
            return tp1;

        // Parcourir les nœuds de la topologie principale
        for (SerializableNode<String, MapAttribute> node : tp1.getAllNodes()) {
            String nodeId = node.getNodeId();
            MapAttribute attribute = node.getNodeContent();

            // Si le nœud n'existe pas dans la topologie de l'agent, l'ajouter à la topologie de différence
            if (tp2.getNode(nodeId) == null) {
                diffTopology.addNode(nodeId, attribute);
            } else {
                // Si le nœud existe mais avec un attribut différent, mettre à jour l'attribut
                MapAttribute tp2_Attribute = tp2.getNode(nodeId).getNodeContent();
                if (!tp2_Attribute.equals(attribute)) {
                    diffTopology.addNode(nodeId, attribute);
                }
            }
        }

        // Parcourir les arêtes de la topologie principale
        for (SerializableNode<String, MapAttribute> node : tp1.getAllNodes()) {
            String nodeId = node.getNodeId();
            Set<String> tp1_EdgesEdges = tp1.getEdges(nodeId);
            Set<String> tp2_Edges = null;

            if (tp2.getAllNodes().contains(node))
                tp2_Edges = tp2.getEdges(nodeId);

            if (tp2_Edges == null) {
                for (String edge : tp1_EdgesEdges)
                    diffTopology.addEdge(edge, nodeId, edge);
            }

            else {
                for (String edge : tp1_EdgesEdges) {
                    if (!tp2_Edges.contains(edge))
                        diffTopology.addEdge(edge, nodeId, edge);
                }
            }
        }

        return diffTopology;
    }

    // ========================================================================

    // Fusionne une topologie avec la carte locale.
    public void merge(SerializableSimpleGraph<String, MapAttribute> tp1) {
        agent.getMyMap().mergeMap(tp1);
    }

    // Fusionne deux topologies en une seule.
    public SerializableSimpleGraph<String, MapAttribute> mergeTopologies(SerializableSimpleGraph<String, MapAttribute> tp1, SerializableSimpleGraph<String, MapAttribute> tp2) {
        
        SerializableSimpleGraph<String, MapAttribute> mergedTopology = new SerializableSimpleGraph<>();

        // On ajoute tous les noeuds de la topologie principale tp1
        for (SerializableNode<String, MapAttribute> node : tp1.getAllNodes()) {
            String nodeId = node.getNodeId();
            MapAttribute attribute = node.getNodeContent();
            mergedTopology.addNode(nodeId, attribute);
        }

        // On ajoute toutes les arêtes de la topologie principale tp1
        for (SerializableNode<String, MapAttribute> node : tp1.getAllNodes()) {
            String nodeId = node.getNodeId();
            Set<String> edges = tp1.getEdges(nodeId);

            for (String edge : edges) {
                mergedTopology.addEdge(edge, nodeId, edge);
            }
        }



        // On ajoute ou fusionne les noeuds de la deuxième topologie tp2
        for (SerializableNode<String, MapAttribute> node : tp2.getAllNodes()) {
            String nodeId = node.getNodeId();
            MapAttribute attribute = node.getNodeContent();

            if (mergedTopology.getNode(nodeId) == null) {
                mergedTopology.addNode(nodeId, attribute);
            } else {
                // Fusionner les attributs des noeuds si nécessaire
                MapAttribute existingAttribute = mergedTopology.getNode(nodeId).getNodeContent();
                if (existingAttribute != MapAttribute.closed && attribute == MapAttribute.closed) {
                    mergedTopology.getNode(nodeId).setContent(MapAttribute.closed);
                }
            }
        }

        // On ajoute ou fusionne les arêtes de la deuxième topologie tp2
        for (SerializableNode<String, MapAttribute> node : tp2.getAllNodes()) {
            String nodeId = node.getNodeId();
            Set<String> edges = tp2.getEdges(nodeId);

            for (String edge : edges) {
                mergedTopology.addEdge(edge, nodeId, edge);
            }
        }

        return mergedTopology;
    }

    // ========================================================================

    // Trouve un point de rencontre optimal en fonction des poids de distance et de degré.
    public String findMeetingPoint(double distanceWeight, double degreeWeight) {
        return agent.getMyMap().findMeetingPoint(distanceWeight, degreeWeight);
    }

    // ========================================================================

    // Trouve le chemin vers le nœud libre le plus proche en excluant certains nœuds.
    public Couple<String, List<String>> getPathToClosestFreeNodeExcluding(String myPosition, Set<String> reservedNodes) {
        return agent.getMyMap().getPathToClosestFreeNodeExcluding(myPosition, reservedNodes);
    } 

    // Trouve le chemin vers le nœud libre le plus proche en excluant certains nœuds et un nœud interdit.
    public Couple<String, List<String>> getPathToClosestFreeNodeExcluding(String myPosition, Set<String> reservedNodes, String forbiddenNode) {
        return agent.getMyMap().getPathToClosestFreeNodeExcluding(myPosition, reservedNodes, forbiddenNode);
    } 

    // ========================================================================

    // Trouve un nœud d'intersection et un nœud adjacent à partir de la position actuelle.
    public String findIntersectionAndAdjacentNode(String myPosition) {
        return agent.getMyMap().findIntersectionAndAdjacentNode(myPosition);
    }

    // Trouve un nœud d'intersection et un nœud adjacent en excluant certains nœuds.
    public String findIntersectionAndAdjacentNode(String myPosition, Set<String> reservedNodes) {
        return agent.getMyMap().findIntersectionAndAdjacentNode(myPosition, reservedNodes);
    }
}