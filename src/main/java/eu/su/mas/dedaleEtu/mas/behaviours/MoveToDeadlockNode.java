package eu.su.mas.dedaleEtu.mas.behaviours;

import eu.su.mas.dedale.env.gs.GsLocation;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentType;
import eu.su.mas.dedaleEtu.mas.utils.TreasureInfo;
import jade.core.behaviours.OneShotBehaviour;

public class MoveToDeadlockNode extends OneShotBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    
    public MoveToDeadlockNode(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    // Gère le déplacement de l'agent vers un nœud d'interblocage.
    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;

        // Met à jour les informations sur les trésors visibles.
        agent.visionMgr.updateTreasure();

        // Si on a un trésor sous nos pieds, on essaye de l'ouvrir et d'en ramasser le plus possible.
        TreasureInfo treasure = agent.treasureMgr.treasureInNode(agent.getCurrentPosition().getLocationId());
        if (treasure != null) {
            if (!treasure.getIsLockOpen())
                treasure.setIsLockOpen(agent.openLock(treasure.getType()));

            if (treasure.getIsLockOpen() && agent.getMyTreasureType() == treasure.getType() && agent.getAgentType() == AgentType.COLLECTOR)
                agent.pick();
        }


        // Nous sommes arrivés au point de deadlock.
        if (agent.getTargetNode() == null) {

            // Si l'agent fait partie d'une coalition, vérifie les priorités des rôles.
            if (agent.coalitionMgr.getCoalition() != null && agent.coalitionMgr.hasAgentInCoalition(agent.getNodeReservation().getAgentName())) {
                if (agent.coalitionMgr.getRole(agent.getNodeReservation().getAgentName()).getPriority() > agent.coalitionMgr.getRole().getPriority())
                    agent.doWait(600); // Attend si un autre agent a une priorité plus élevée.
            }    

            // Libère la réservation du nœud et réinitialise les paramètres de communication.
            agent.setNodeReservation(null);
            agent.comMgr.setLettingHimPass(false);

            return;
        }

        // Incrémente le compteur de temps passé après un deadlock.
        // agent.moveMgr.incrementeTimeDeadlock();

        // On se déplace.
        boolean moved = agent.moveTo(new GsLocation(agent.getTargetNode()));
        if (moved) {
            // Si le déplacement est réussi, réinitialise le compteur d'échecs et met à jour le chemin.
            agent.moveMgr.resetFailedMoveCount();
            agent.setTargetNodeFromCurrentPath();
        } 

        else {
            // Si le déplacement échoue, incrémente le compteur d'échecs et met à jour la topologie.
            agent.moveMgr.incrementFailedMoveCount();
            agent.topoMgr.incrementUpdateCount();

            // On reset pour éviter les boucles infinies d'interblocage.
            if (agent.moveMgr.getFailedMoveCount() > 20) {
                agent.setNodeReservation(null);
                agent.comMgr.setLettingHimPass(false);    
            }
        } 
    }

    // Retourne le code de sortie. 
    @Override 
    public int onEnd() {
        // Affiche des informations de debug si nécessaire.
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        return exitCode;
    }
}
