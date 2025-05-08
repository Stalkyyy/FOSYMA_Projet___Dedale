package eu.su.mas.dedaleEtu.mas.managers;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovementManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private AbstractAgent agent;

    protected Map<String, Integer> timeUntilNextMoveRequest;  // Temps restant avant la prochaine requête de mouvement pour chaque agent.
    public final int minTime = 15;  // Temps minimum avant une nouvelle requête.

    public final int maxFailedMoveCount = 3;
    private int failedMoveCount = 0;

    // Initialise le gestionnaire de mouvement pour un agent donné.
    public MovementManager(AbstractAgent agent) {
        this.agent = agent;

        timeUntilNextMoveRequest = new HashMap<>();
        for(String agentName : agent.getListAgentNames())
            timeUntilNextMoveRequest.put(agentName, minTime + 1);
    }

    // ==================================================================================

    // Définit le chemin actuel vers le nœud ouvert le plus proche.
    public void setCurrentPathToClosestOpenNode() {
        String myNode = agent.getCurrentPosition().getLocationId();
        List<String> path = agent.getMyMap().getShortestPathToClosestOpenNode(myNode);
        agent.setCurrentPath(path);
        agent.setTargetNode(path.isEmpty() ? null : path.remove(0));
    }

    // Définit le chemin actuel vers un nœud spécifique.
    public void setCurrentPathTo(String nodeId) {
        String myNode = agent.getCurrentPosition().getLocationId();
        List<String> path = agent.getMyMap().getShortestPath(myNode, nodeId);
        agent.setCurrentPath(path);
        agent.setTargetNode(path.isEmpty() ? null : path.remove(0));
    }

    // Définit le chemin actuel vers un nœud spécifique en excluant certains nœuds.
    public void setCurrentPathToExcluding(String nodeId) {
        String myNode = agent.getCurrentPosition().getLocationId();
        List<String> path = agent.getMyMap().getShortestPath(myNode, nodeId);
        agent.setCurrentPath(path);
        agent.setTargetNode(path.isEmpty() ? null : path.remove(0));
    }


    // Définit le chemin actuel vers le nœud ouvert le plus éloigné.
    public void setCurrentPathToFarthestOpenNode() {
        String myNode = agent.getCurrentPosition().getLocationId();
        List<String> path = agent.getMyMap().getShortestPathToFarthestOpenNode(myNode);
        agent.setCurrentPath(path);
        agent.setTargetNode(path.isEmpty() ? null : path.remove(0));
    }

    // Définit le chemin actuel à partir d'une liste de nœuds.
    public void setCurrentPathTo(List<String> path) {
        agent.setCurrentPath(path);
        agent.setTargetNode(path.isEmpty() ? null : path.remove(0));
    }


    // Définit le chemin actuel vers un nœud aléatoire.
    public void setCurrentPathToRandomNode() {
        String nodeId = agent.getMyMap().getRandomNode(agent.getCurrentPosition().getLocationId());
        setCurrentPathTo(nodeId);
    }

    // ==================================================================================

    // Vérifie si un deadlock doit être initié.
    public boolean shouldInitiateDeadlock(String agentName, String agentPosition) {
        if (agentPosition == null || agentName == null || agent.getTargetNode() == null)
            return false;

        boolean iSeeMyPosition = agentPosition.compareTo(agent.getTargetNode()) == 0;
        boolean confirmationDeadlock = this.failedMoveCount > this.maxFailedMoveCount;

        return iSeeMyPosition && confirmationDeadlock;
    }

    // Incrémente le compteur de mouvements échoués.
    public void incrementFailedMoveCount() {
        this.failedMoveCount++;
    }

    // Réinitialise le compteur de mouvements échoués.
    public void resetFailedMoveCount() {
        this.failedMoveCount = 0;
    }

    // Retourne le compteur de mouvements échoués.
    public int getFailedMoveCount() {
        return this.failedMoveCount;
    }

    // Incrémente le temps d'attente pour tous les agents en cas de deadlock.
    public void incrementeTimeDeadlock() {
        for (Map.Entry<String, Integer> entry : this.timeUntilNextMoveRequest.entrySet()) {
            String agentName = entry.getKey();
            int time = entry.getValue();
            this.timeUntilNextMoveRequest.put(agentName, time + 1);
        }
    }

    // Réinitialise le temps d'attente pour un agent spécifique.
    public void resetTimeDeadlock(String agentName) {
        this.timeUntilNextMoveRequest.put(agentName, 0);
    }

    // Retourne le temps d'attente restant pour un agent spécifique.
    public int getTimeToWait(String agentName) {
        return this.timeUntilNextMoveRequest.get(agentName);
    }
}