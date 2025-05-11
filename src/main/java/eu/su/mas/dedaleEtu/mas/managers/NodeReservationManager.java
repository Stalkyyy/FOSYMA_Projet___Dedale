package eu.su.mas.dedaleEtu.mas.managers;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentBehaviourState;
import eu.su.mas.dedaleEtu.mas.knowledge.NodeReservation;
import eu.su.mas.dedaleEtu.mas.utils.CoalitionInfo.COALITION_ROLES;

public class NodeReservationManager implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private AbstractAgent agent;

    // Initialise le gestionnaire de réservation de nœuds pour un agent donné.
    public NodeReservationManager(AbstractAgent agent) {
        this.agent = agent;
    }

    // =====================================================================

     /**
     * Crée une nouvelle réservation de nœuds pour l'agent.
     * Si une réservation existe déjà, elle est fusionnée avec les nouveaux nœuds.
     */
    public NodeReservation createNodeReservation() {
        NodeReservation currentNodeReservation = agent.getNodeReservation();

        if (currentNodeReservation == null) {
            return new NodeReservation(
                agent.getLocalName(), 
                new HashSet<>(agent.getCurrentPath()), 
                agent.getBehaviourState(),
                true,
                System.currentTimeMillis()
            );
        }

        else {
            Set<String> newNodes = new HashSet<>();
            newNodes.addAll(agent.getCurrentPath());
            newNodes.addAll(currentNodeReservation.getReservedNodes());

            return new NodeReservation(
                currentNodeReservation.getAgentName(), 
                newNodes, 
                currentNodeReservation.getState(), 
                currentNodeReservation.hasAccessibleNodes(),
                System.currentTimeMillis()
            );
        }
    }

    // Fusionne une réservation de nœuds reçue avec celle de l'agent.
    public void mergeNodeReservation(NodeReservation NR) {
        if (agent.getNodeReservation() == null) {
            agent.setNodeReservation(NR);  // Si aucune réservation n'existe, on utilise directement celle reçue.
            return;
        }

        // Fusionne les nœuds réservés existants avec ceux de la réservation reçue.
        Set<String> reserved = new HashSet<>(NR.getReservedNodes());
        boolean hasAccessibleNodes = true;
        if (agent.getNodeReservation() != null) {
            reserved.addAll(agent.getNodeReservation().getReservedNodes());
            hasAccessibleNodes = agent.getNodeReservation().hasAccessibleNodes();
        }

        agent.setNodeReservation(
                new NodeReservation(
                NR.getAgentName(),
                reserved,
                NR.getState(),
                NR.hasAccessibleNodes() && hasAccessibleNodes,
                System.currentTimeMillis()
            )
        );
    }

    // =====================================================================

    // Vérifie si l'agent actuel a une priorité plus élevée qu'une autre réservation.
    public boolean hasPriorityOver(NodeReservation otherReservation) {

        // On regarde lequel des deux a des mouvements disponibles.
        boolean currentPossibility = agent.visionMgr.hasNodeAvailable();
        boolean otherPossibility = otherReservation.hasAccessibleNodes();
        
        if (currentPossibility != otherPossibility)
            return !currentPossibility;

        // On compare les états de chaque agent.
        int currentPriority = agent.getBehaviourState().getPriority();
        int otherPriority = otherReservation.getState().getPriority();

        if (currentPriority != otherPriority)
            return currentPriority > otherPriority;


        // Si les priorités sont égales et qu'on est en mode collecte, on va comparer leur coalition.
        if (agent.getBehaviourState() == AgentBehaviourState.COLLECT_TREASURE)
            return hasPriorityOver_COLLECT(otherReservation);

        // Sinon, on compare leur nom.
        return agent.getLocalName().compareTo(otherReservation.getAgentName()) > 0;
    }

    // Vérifie la priorité entre deux agents en mode collecte.
    private boolean hasPriorityOver_COLLECT(NodeReservation otherReservation) {
        String otherAgentName = otherReservation.getAgentName();

        // Si les agents ne sont pas de la même coalition, on compare la quantité de leurs trésors. Si c'est égal, l'id du noeud du trésor.
        if (!agent.coalitionMgr.hasAgentInCoalition(otherAgentName)) {
            int currentQuantity = agent.coalitionMgr.getQuantity();
            int otherQuantity = agent.coalitionMgr.getQuantity(otherAgentName);

            if (currentQuantity != otherQuantity)
                return currentQuantity > otherQuantity;

            String currentTreasureNode = agent.coalitionMgr.getTreasureId();
            String otherTreasureNode = agent.coalitionMgr.getTreasureId(otherAgentName);

            return currentTreasureNode.compareTo(otherTreasureNode) > 0;
        }

        // Sinon, on compare leur rôle : CoalitionCollector > CoalitionSilo > CoalitionHelper
        COALITION_ROLES currentRole = agent.coalitionMgr.getRole();
        COALITION_ROLES otherRole = agent.coalitionMgr.getRole(otherAgentName);

        if (currentRole.getPriority() != otherRole.getPriority())
            return currentRole.getPriority() > otherRole.getPriority();

        // S'ils ont le même rôle, on compare leur nom.
        return agent.getLocalName().compareTo(otherAgentName) > 0;
    }
}
