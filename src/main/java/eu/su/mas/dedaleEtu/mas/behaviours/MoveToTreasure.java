package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.List;
import java.util.Map;

import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.GsLocation;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentBehaviourState;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentType;
import jade.core.behaviours.OneShotBehaviour;

public class MoveToTreasure extends OneShotBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    
    public MoveToTreasure(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }
    // Gère le déplacement de l'agent vers le trésor.
    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;

        agent.visionMgr.updateTreasure();



        // Incrémente le compteur de temps passé après un deadlock.
        // agent.moveMgr.incrementeTimeDeadlock();

        // Récupère l'identifiant du trésor cible.
        String treasureId = agent.coalitionMgr.getCoalition().getNodeId();
        if (treasureId == null) {
            // Si aucun trésor n'est défini et que l'on a déjà tout déposé dans le silo, passe à l'état "RE_EXPLORATION".
            agent.setBehaviourState(AgentBehaviourState.RE_EXPLORATION);
            exitCode = agent.getBehaviourState().getExitCode();
            return;
        }

        // Vérifie si le chemin actuel est vide ou si le trésor cible a changé.
        List<String> path = agent.getCurrentPath();
        if (path.isEmpty() || path.getLast().equals(treasureId)) {
            agent.moveMgr.setCurrentPathTo(treasureId);
        }



        // Nous ne sommes toujours pas arrivé au trésor.
        if (agent.getTargetNode() != null) {
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



        else {
            // Si le coffre a été **refermé** depuis la dernière observation, ou disparu, ou récupéré, on part.
            boolean isTreasureStillHere = agent.treasureMgr.getCurrentTreasure() != null;
            boolean isLockOpenCoalition = agent.coalitionMgr.getCoalition().isLockOpen();

            boolean isActuallyOpen = isTreasureStillHere ? agent.treasureMgr.getCurrentTreasure().getIsLockOpen() : false;
            boolean beenClosed = isTreasureStillHere ? isLockOpenCoalition && !isActuallyOpen : false;

            if (!isTreasureStillHere || beenClosed) {
                // Si le trésor n'est plus là ou si le coffre a été refermé, passe à l'état "RE_EXPLORATION".
                agent.setBehaviourState(AgentBehaviourState.RE_EXPLORATION);
                exitCode = agent.getBehaviourState().getExitCode();
                return;
            }

            // Tente d'ouvrir le coffre si nécessaire.
            Observation type = agent.coalitionMgr.getCoalition().getType();

            if (!isActuallyOpen) 
                agent.openLock(type);

            // Collecte le trésor.
            if (agent.getAgentType() == AgentType.COLLECTOR) {
                agent.pick();            

                // Si l'agent qu'on voit est un Silo, on tente de lui donner les ressources que l'on a.
                Map<String, String> agentsNearby = agent.visionMgr.getAgentsNearby();
                for (String agentName : agentsNearby.values()) {
                    if (agent.freeSpace() < agent.getMyBackPackTotalSpace() && agent.otherKnowMgr.getAgentType(agentName) == AgentType.TANKER) {
                        agent.emptyMyBackPack(agentName);
                    }
                }
            }
        }

        // Met à jour les informations sur les trésors visibles.
        agent.visionMgr.updateTreasure();
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
