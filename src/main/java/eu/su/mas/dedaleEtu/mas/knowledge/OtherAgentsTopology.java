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



    public OtherAgentsTopology() {
        this.otherAgentsTopologies = new HashMap<>();
    }

    public OtherAgentsTopology(List<String> list_agentNames) {
        this.otherAgentsTopologies = new HashMap<>();
        for (String agentName : list_agentNames) {
            this.otherAgentsTopologies.put(agentName, new SerializableSimpleGraph<String, MapAttribute>());
        }
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


            Set<String> edges = newTopology.getEdges(nodeId);
            for (String edge : edges) {
                String targetNode = edge.split("-")[1];
                existingTopology.addEdge(targetNode, nodeId, targetNode);
            }
        }


        this.otherAgentsTopologies.put(agentName, existingTopology);
    }
}
