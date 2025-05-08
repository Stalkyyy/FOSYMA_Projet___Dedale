package eu.su.mas.dedaleEtu.mas.knowledge;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Random;
import java.util.Set;

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

    // ======================================================================

    public String getRandomNode(String myPosition) {
        List<String> allNodes = this.g.nodes()
            .map(Node::getId)
            .collect(Collectors.toList())
            .stream()
            .filter(node -> node.compareTo(myPosition) != 0)
            .collect(Collectors.toList());
            
        Random random = new Random();
        return allNodes.get(random.nextInt(allNodes.size()));
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

    // ======================================================================

    public Couple<String, List<String>> getPathToClosestFreeNodeExcluding(String myPosition, Set<String> reservedNodes) {
        // 1) get all nodes
        List<String> allNodes = this.g.nodes()
        .map(Node::getId)
        .collect(Collectors.toList());
    
        // 2) Filtrer pour exclure les nœuds réservés
        List<String> availableNodes = allNodes.stream()
            .filter(node -> !reservedNodes.contains(node))
            .filter(node -> !node.equals(myPosition))
            .collect(Collectors.toList());
    
        // 3) Trouver le noeud libre le plus proche
        List<Couple<String, List<String>>> candidateNodes = new ArrayList<>();
    
        for (String node : availableNodes) {
            List<String> path = getShortestPath(myPosition, node);
            
            // Vérifier si un chemin existe
            if (path != null && !path.isEmpty()) {
                candidateNodes.add(new Couple<>(node, path));
            }
        }
    
        // 4) Trouver le nœud avec le chemin le plus court parmi les candidats
        Optional<Couple<String, List<String>>> closest = candidateNodes.stream()
            .min(Comparator.comparing(c -> c.getRight().size()));
    
        // 5) Retourner le nœud le plus proche ou null si aucun n'est trouvé
        return closest.isPresent() ? closest.get() : null;
    }


    public Couple<String, List<String>> getPathToClosestFreeNodeExcluding(String myPosition, Set<String> reservedNodes, String forbiddenNode) {
        // 1) get all nodes
        List<String> allNodes = this.g.nodes()
        .map(Node::getId)
        .collect(Collectors.toList());

        // 2) Filtrer pour exclure les nœuds réservés
        List<String> availableNodes = allNodes.stream()
            .filter(node -> !reservedNodes.contains(node))
            .filter(node -> !node.equals(myPosition))
            .collect(Collectors.toList());

        // 3) Trouver le nœud le plus proche dont le chemin n'inclut pas forbiddenNode
        List<Couple<String, List<String>>> candidateNodes = new ArrayList<>();

        for (String node : availableNodes) {
            List<String> path = getShortestPath(myPosition, node);
            
            // Vérifier si un chemin existe et qu'il ne contient pas le nœud interdit
            if (path != null && !path.isEmpty() && !path.contains(forbiddenNode)) {
                candidateNodes.add(new Couple<>(node, path));
            }
        }

        // 4) Trouver le nœud avec le chemin le plus court parmi les candidats
        Optional<Couple<String, List<String>>> closest = candidateNodes.stream()
            .min(Comparator.comparing(c -> c.getRight().size()));

        // 5) Retourner le nœud le plus proche ou null si aucun n'est trouvé
        return closest.isPresent() ? closest.get() : null;
    }

    // ======================================================================

    public String findIntersectionAndAdjacentNode(String myPosition) {
        // 1) Récupérer tous les nœuds du graphe
        List<String> allNodes = this.g.nodes()
            .map(Node::getId)
            .collect(Collectors.toList());
    
        // 2) Identifier les intersections (nœuds avec degré > 2)
        List<String> intersections = allNodes.stream()
            .filter(node -> this.g.getNode(node).getDegree() > 2) // Condition d'intersection
            .collect(Collectors.toList());
        
        // Si aucune intersection n'est trouvée, retourner null
        if (intersections.isEmpty()) {
            return null;
        }
        
        // 3) Trouver l'intersection la plus proche
        String closestIntersection = null;
        int minDistance = Integer.MAX_VALUE;
        
        for (String intersection : intersections) {
            List<String> path = getShortestPath(myPosition, intersection);
            if (path != null && !path.isEmpty() && path.size() < minDistance) {
                minDistance = path.size();
                closestIntersection = intersection;
            }
        }
        
        // Si aucune intersection accessible n'est trouvée, retourner null
        if (closestIntersection == null) {
            return null;
        }
        
        // 4) Trouver un nœud adjacent à cette intersection
        List<String> adjacentNodes = this.g.getNode(closestIntersection).neighborNodes()
            .map(Node::getId)
            .collect(Collectors.toList());
        
        // Choisir le premier nœud adjacent différent de ma position
        String adjacentNode = adjacentNodes.stream()
            .filter(node -> !node.equals(myPosition))
            .findFirst()
            .orElse(adjacentNodes.get(0));
        
        return adjacentNode;
    }

    public String findIntersectionAndAdjacentNode(String myPosition, Set<String> reservedNodes) {

        List<String> allNodes = this.g.nodes()
            .map(Node::getId)
            .collect(Collectors.toList());
    
        List<String> validIntersections = new ArrayList<>();
        
        for (String nodeId : allNodes) {
            if (this.g.getNode(nodeId).getDegree() > 2) {
                boolean hasNonReservedAdjacent = this.g.getNode(nodeId).neighborNodes()
                    .map(Node::getId)
                    .anyMatch(neighbor -> !reservedNodes.contains(neighbor) && !neighbor.equals(myPosition));
                    
                if (hasNonReservedAdjacent) {
                    validIntersections.add(nodeId);
                }
            }
        }
        
        if (validIntersections.isEmpty()) {
            return null;
        }
        
        String closestIntersection = null;
        int minDistance = Integer.MAX_VALUE;
        
        for (String intersection : validIntersections) {
            List<String> path = getShortestPath(myPosition, intersection);
            if (path != null && !path.isEmpty() && path.size() < minDistance) {
                minDistance = path.size();
                closestIntersection = intersection;
            }
        }
        
        if (closestIntersection == null) {
            return null;
        }
        
        String adjacentNode = this.g.getNode(closestIntersection).neighborNodes()
            .map(Node::getId)
            .filter(node -> !reservedNodes.contains(node))
            .filter(node -> !node.equals(myPosition))
            .findFirst()
            .orElse(null);
        
        return adjacentNode;
    }
}
