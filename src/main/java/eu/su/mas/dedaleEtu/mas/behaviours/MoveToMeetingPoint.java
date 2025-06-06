package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.List;

import eu.su.mas.dedale.env.gs.GsLocation;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentBehaviourState;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentType;
import eu.su.mas.dedaleEtu.mas.utils.TreasureInfo;
import jade.core.behaviours.OneShotBehaviour;

public class MoveToMeetingPoint extends OneShotBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    
    public MoveToMeetingPoint(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }
    // Gère le déplacement de l'agent vers le point de rencontre.
    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;

        // Définit l'état de l'agent comme étant en déplacement vers le point de rencontre.
        this.agent.setBehaviourState(AgentBehaviourState.MEETING_POINT);

        // Si on a un trésor sous nos pieds, on essaye de l'ouvrir et d'en ramasser le plus possible.
        TreasureInfo treasure = agent.treasureMgr.treasureInNode(agent.getCurrentPosition().getLocationId());
        if (treasure != null) {
            if (!treasure.getIsLockOpen())
                treasure.setIsLockOpen(agent.openLock(treasure.getType()));

            if (treasure.getIsLockOpen() && agent.getMyTreasureType() == treasure.getType() && agent.getAgentType() == AgentType.COLLECTOR)
                agent.pick();
        }

        

        // Si le point de rencontre n'est pas encore défini, le calcule et le définit.
        if (agent.getMeetingPoint() == null) {
            String meetingPointId = agent.topoMgr.findMeetingPoint(agent.distanceWeight, agent.degreeWeight);
            agent.setMeetingPoint(meetingPointId);
        }

         // Vérifie si le chemin actuel est vide ou si le point de rencontre a changé.
        List<String> path = agent.getCurrentPath();
        if (path.isEmpty() || path.getLast().equals(agent.getMeetingPoint())) {
            agent.moveMgr.setCurrentPathTo(agent.getMeetingPoint());
        }

        // Nous sommes arrivés au meeting_point.
        if (agent.getTargetNode() == null) {
            agent.floodMgr.activateFlooding();
            System.out.println(agent.getLocalName() + " - racine du flood");
            exitCode = 1;
            return;
        }

         // Met à jour les informations sur les trésors visibles.
        agent.visionMgr.updateTreasure();

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
            // Si le déplacement échoue, incrémente le compteur d'échecs.
            agent.moveMgr.incrementFailedMoveCount();

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
