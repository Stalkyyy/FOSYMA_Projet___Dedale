package eu.su.mas.dedaleEtu.mas.knowledge;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Node;

import dataStructures.tuple.Couple;
import eu.su.mas.dedaleEtu.mas.knowledge.given_knowledge.MapRepresentation;

public class MapMoreRepresentation extends MapRepresentation {
    
    private static final long serialVersionUID = 1L;

    public MapMoreRepresentation() {
        super();
    }

    // ======================================================================

    private Map<String, Map<String, Integer>> getAllNodeDistances() {
        Map<String, Map<String, Integer>> distanceMatrix = new HashMap<>();

        List<String> allNodes = this.g.nodes()
            .map(Node::getId)
            .collect(Collectors.toList());

        for (String sourceNodeId : allNodes) {
            Map<String, Integer> distances = new HashMap<>();

            Dijkstra dijkstra = new Dijkstra();
            dijkstra.init(g);
            dijkstra.setSource(g.getNode(sourceNodeId));
            dijkstra.compute();

            for (String targetNodeId : allNodes) {
                if (sourceNodeId.equals(targetNodeId)) {
                    distances.put(targetNodeId, 0);
                    continue;
                }

                try {
                    List<Node> path = dijkstra.getPath(g.getNode(targetNodeId)).getNodePath();;
                    distances.put(targetNodeId, path.size() - 1);
                } catch (Exception e) {
                    distances.put(targetNodeId, Integer.MAX_VALUE);
                }
            }

            dijkstra.clear();
            distanceMatrix.put(sourceNodeId, distances);
        }

        return distanceMatrix;
    }

    // ======================================================================

    public List<String> getShortestPathToFarthestOpenNode(String myPosition) {
		//1) Get all openNodes
		List<String> opennodes=getOpenNodes();

		//2) select the farthest one
		List<Couple<String,Integer>> lc=
				opennodes.stream()
				.map(on -> (getShortestPath(myPosition,on)!=null)? new Couple<String, Integer>(on,getShortestPath(myPosition,on).size()): new Couple<String, Integer>(on,Integer.MIN_VALUE))//some nodes my be unreachable if the agents do not share at least one common node.
				.collect(Collectors.toList());

		Optional<Couple<String,Integer>> farthest=lc.stream().max(Comparator.comparing(Couple::getRight));

		//3) Compute shorterPath
		if (farthest.isPresent() && farthest.get().getRight() != Integer.MAX_VALUE) {
			return getShortestPath(myPosition, farthest.get().getLeft());
		}
		
		return new ArrayList<String>(); // Aucun nœud accessible
	}


    public List<String> getShortestPathToClosestNodeExclude(String myPosition, List<String> excludedNodes) {
		//1) get all nodes
		List<String> allNodes = this.g.nodes()
			.map(Node::getId)
			.collect(Collectors.toList());

		//2) Filtrage des noeuds.
		List<String> filteredNodes = allNodes.stream()
			.filter(node -> !excludedNodes.contains(node))
			.collect(Collectors.toList());

		//3) On trouve le noeud le plus proche
		List<Couple<String, Integer>> lc = filteredNodes.stream()
		.map(node -> (getShortestPath(myPosition, node) != null)
				? new Couple<>(node, getShortestPath(myPosition, node).size())
				: new Couple<>(node, Integer.MAX_VALUE)) // Certains nœuds peuvent être inaccessibles
		.collect(Collectors.toList());

		Optional<Couple<String, Integer>> closest = lc.stream().min(Comparator.comparing(Couple::getRight));

		//4) Calculer le chemin le plus court vers ce nœud
		if (closest.isPresent() && closest.get().getRight() != Integer.MAX_VALUE) {
			return getShortestPath(myPosition, closest.get().getLeft());
		}

		// Si aucun nœud accessible n'est trouvé, retourner null
		return null;
	}

    // ======================================================================

    public String findMeetingPoint(double distanceWeight, double degreeWeight) {

        Map<String, Map<String, Integer>> distances = getAllNodeDistances();
        
        Map<String, Integer> totalDistances = new HashMap<>();
        Map<String, Integer> degrees = new HashMap<>();
        
        // Calcul des distances totales et des degrés
        for (String nodeId : distances.keySet()) {
            int totalDistance = distances.get(nodeId).values().stream()
                .filter(d -> d != Integer.MAX_VALUE)
                .mapToInt(Integer::intValue)
                .sum();
            totalDistances.put(nodeId, totalDistance);
            
            int degree = this.g.getNode(nodeId).getDegree();
            degrees.put(nodeId, degree);
        }
        
        // On trouve les valeurs min et max pour normalisation
        int minDistance = totalDistances.values().stream().min(Integer::compare).orElse(0);
        int maxDistance = totalDistances.values().stream().max(Integer::compare).orElse(1);
        int distanceRange = maxDistance - minDistance;
        
        int minDegree = degrees.values().stream().min(Integer::compare).orElse(0);
        int maxDegree = degrees.values().stream().max(Integer::compare).orElse(1);
        int degreeRange = maxDegree - minDegree;
        
        // On calcule les scores et trouver le meilleur nœud
        String bestMeetingPoint = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        
        for (String nodeId : distances.keySet()) {

            // Normalise la distance (un score plus petit est meilleur, donc on inverse)
            double distanceScore = distanceRange == 0 ? 1.0 : 
                1.0 - ((double)(totalDistances.get(nodeId) - minDistance) / distanceRange);
            
            // Normalise le degré (un score plus grand est meilleur)
            double degreeScore = degreeRange == 0 ? 0.0 : 
                (double)(degrees.get(nodeId) - minDegree) / degreeRange;
            
            // Calcule le score combiné
            double score = (distanceWeight * distanceScore) + (degreeWeight * degreeScore);
            
            // Mise à jour du meilleur point si nécessaire
            if (score > bestScore) {
                bestScore = score;
                bestMeetingPoint = nodeId;
            }

            // En cas d'égalité, prendre le nœud avec l'ID lexicographiquement plus petit
            else if (score == bestScore && bestMeetingPoint != null && nodeId.compareTo(bestMeetingPoint) < 0) {
                bestMeetingPoint = nodeId;
            }
        }
        
        return bestMeetingPoint;
    }
}
