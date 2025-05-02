package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.GsLocation;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentBehaviorState;
import eu.su.mas.dedaleEtu.mas.knowledge.given_knowledge.MapRepresentation.MapAttribute;
import jade.core.behaviours.OneShotBehaviour;

public class MyExplorationBehaviour extends OneShotBehaviour {
    
    private static final long serialVersionUID = -3912044501329336080L;

    /**
     * 0 est la sortie de base.
     * 1 est en détection d'interblocage.
     * 2 est si l'exploration est terminée.
     */
    private int exitCode = -1;
    private AbstractAgent agent;



    public MyExplorationBehaviour(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

	@Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;

        // On actualise l'état de l'agent.
        agent.setBehaviorState(AgentBehaviorState.EXPLORATION);

        // Initialisation de la carte
        if (agent.getMyMap() == null)
            agent.initMapRepresentation();

            
        // Récupération de la position actuelle.
        Location myPosition = agent.getCurrentPosition();
        if (myPosition == null) return;
        
        // Mise à jour de la carte avec le nœud actuel
        String currentNodeId = myPosition.getLocationId();
        agent.topoMgr.addNode(currentNodeId, MapAttribute.closed);


        // Liste des nœuds accessibles sans agents
        List<String> accessibleNodes = new ArrayList<>();

        // Observation des noeuds accessibles.
        List<Couple<Location,List<Couple<Observation,String>>>> observations = agent.observe();

        for (Couple<Location, List<Couple<Observation, String>>> observation : observations) {
            String observedNodeId = observation.getLeft().getLocationId();
            List<Couple<Observation, String>> attributes = observation.getRight();

            // Ajout du nœud observé à la carte
            boolean isNewNode = agent.getMyMap().addNewNode(observedNodeId);
            if (isNewNode) {
                agent.getOtherAgentsTopology().incrementeLastUpdates();
            }

            // On update la liste des trésors si c'est le noeud actuel.
            if (currentNodeId.equals(observedNodeId)) {
                agent.treasureMgr.update(currentNodeId, attributes);
                continue;
            }

            // Ajout d'une arête entre le nœud actuel et le nœud observé
            agent.getMyMap().addEdge(currentNodeId, observedNodeId);

            // Mise à jour du nœud cible si aucun nœud cible n'est défini
            if (agent.getTargetNode() == null && isNewNode) {
                agent.setTargetNode(observedNodeId);
                agent.clearCurrentPath();
            }

            // Vérification de la présence d'un agent sur le nœud observé
            boolean hasAgent = attributes.stream()
                .anyMatch(attr -> attr.getLeft() == Observation.AGENTNAME);

            // Ajout du nœud à la liste des nœuds accessibles s'il n'y a pas d'agent
            if (!hasAgent) {
                accessibleNodes.add(observedNodeId);
            }
        }


        // S'il n'y a plus de noeud ouvert, alors l'agent a terminé son exploration.
        if (!agent.getMyMap().hasOpenNode()) {
            exitCode = 2;
            agent.markExplorationComplete();
            System.out.println(this.agent.getLocalName()+" - Exploration successufully done.");
            return;
        }


        // S'il y a pas de noeud ouvert directement accessible, on en choisit un et on fait le plus court chemin pour y aller.
        if (agent.getTargetNode() == null) {
            agent.moveMgr.setCurrentPathToClosestOpenNode();
        }

        // On s'y déplace.
        boolean moved = agent.moveTo(new GsLocation(agent.getTargetNode()));
        if (moved) {
            agent.resetFailedMoveCount();
            agent.setTargetNodeFromCurrentPath();
        } 
        
        else if (agent.getFailedMoveCount() > 3 && !accessibleNodes.isEmpty()){
            Random random = new Random();

            if (random.nextDouble() < 0.33) {
                String randomAccessibleNode = accessibleNodes.get(random.nextInt(accessibleNodes.size()));
                agent.setTargetNode(randomAccessibleNode);
                agent.clearCurrentPath();
                agent.moveTo(new GsLocation(agent.getTargetNode()));
            }
        } 
        
        else {
            agent.incrementFailedMoveCount();
        }
    }


    @Override 
    public int onEnd() {
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        return exitCode;
    }
}
