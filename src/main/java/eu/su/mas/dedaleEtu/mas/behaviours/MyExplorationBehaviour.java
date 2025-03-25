package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.GsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.agents.GeneralAgent;
import jade.core.behaviours.OneShotBehaviour;

public class MyExplorationBehaviour extends OneShotBehaviour {
    
    private static final long serialVersionUID = -3912044501329336080L;

    /**
     * 0 est la sortie de base.
     * 1 est en détection d'interblocage.
     * 2 est si l'exploration est terminée.
     */
    private int exitCode = 0;
    private GeneralAgent agent;



    public MyExplorationBehaviour(final AbstractDedaleAgent myagent) {
        super(myagent);
        this.agent = (GeneralAgent) myagent;
    }

	@Override
    public void action() {

        if (this.agent.getMyMap() == null)
            this.agent.initMapRepresentation();

        // Retrieve the current position.
        Location myPosition = this.agent.getCurrentPosition();
        if (myPosition == null)
            return;

        // List of observable from the agent's current position + update.
        List<Couple<Location,List<Couple<Observation,String>>>> lobs = this.agent.observe();

        // Just added here to let you see what the agent is doing, otherwise he will be too quick.
        try {
            this.agent.doWait(750);
        } catch (Exception e) {
            e.printStackTrace();
        }

        
        this.agent.getMyMap().addNode(myPosition.getLocationId(), MapAttribute.closed);
        this.agent.getOtherAgentsTopology().incrementeLastUpdates();

        // Get the surrounding nodes and, if not in closedNodes, add them to open nodes + update observations.
        List<String> accessiblesNodes = new ArrayList<>();
        Iterator<Couple<Location, List<Couple<Observation, String>>>> iter = lobs.iterator();

        while(iter.hasNext()){
            Couple<Location, List<Couple<Observation, String>>> elemIter = iter.next();

            String accessibleNodeId = elemIter.getLeft().getLocationId();
            List<Couple<Observation,String>> attributes = elemIter.getRight();

            // Update de la topologie
            boolean isNewNode = this.agent.getMyMap().addNewNode(accessibleNodeId);
            if (isNewNode)
                this.agent.getOtherAgentsTopology().incrementeLastUpdates();
            
            // The node may exist, but not necessarily the edge
            if (myPosition.getLocationId() != accessibleNodeId) {
                this.agent.getMyMap().addEdge(myPosition.getLocationId(), accessibleNodeId);
                if (this.agent.getTargetNode() == null && isNewNode) {
                    this.agent.setTargetNode(accessibleNodeId);
                    this.agent.clearCurrentPath();
                }

                boolean hasAgentObservation = attributes.stream()
                    .anyMatch(observation -> observation.getLeft() == Observation.AGENTNAME);
                if (!hasAgentObservation) 
                    accessiblesNodes.add(accessibleNodeId);
            }     
            

            // Update des observations
            this.agent.getMyObservations().updateObservation(accessibleNodeId, attributes);
        }


        // S'il n'y a plus de noeud ouvert, alors l'agent a terminé son exploration.
        if (!this.agent.getMyMap().hasOpenNode()) {
            this.exitCode = 2;
            this.agent.setExploFinished(true);
            System.out.println(this.agent.getLocalName()+" - Exploration successufully done.");
            return;
        }

        // S'il y a pas de noeud ouvert directement accessible, on en choisit un et on fait le plus court chemin pour y aller.
        if (this.agent.getTargetNode() == null) {
            this.agent.setCurrentPathToClosestOpenNode();
        }

        // On s'y déplace.
        boolean moved = this.agent.moveTo(new GsLocation(this.agent.getTargetNode()));
        if (moved) {
            this.agent.resetFailedMoveCount();
            this.agent.setTargetNodeFromCurrentPath();
        } else {
            this.agent.incrementFailedMoveCount();
            if (this.agent.getFailedMoveCount() > 5) {
                // System.out.println(this.agent.getLocalName() + " - Potentiel interblocage détecté !");
                // Aller dans la transition pour gérer les deadlocks.
                // this.exitCode = 1;

                if (!accessiblesNodes.isEmpty()) {
                    String randomAccessibleNode = accessiblesNodes.get((new Random()).nextInt(accessiblesNodes.size()));
                    this.agent.setTargetNode(randomAccessibleNode);
                    this.agent.clearCurrentPath();
                    this.agent.moveTo(new GsLocation(this.agent.getTargetNode()));
                }

            }
        }
    }


    @Override 
    public int onEnd() {
        return exitCode;
    }
}
