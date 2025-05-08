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

    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;

        agent.visionMgr.updateTreasure();
        agent.moveMgr.incrementeTimeDeadlock();

        String treasureId = agent.coalitionMgr.getCoalition().getNodeId();
        if (treasureId == null) {
            agent.setBehaviourState(AgentBehaviourState.MEETING_POINT);
            exitCode = agent.getBehaviourState().getExitCode();
            return;
        }


        List<String> path = agent.getCurrentPath();
        if (path.isEmpty() || !path.getLast().equals(treasureId)) {
            agent.moveMgr.setCurrentPathTo(treasureId);
        }



        // Nous ne sommes toujours pas arrivé au trésor.
        if (agent.getTargetNode() != null) {
            // On se déplace.
            boolean moved = agent.moveTo(new GsLocation(agent.getTargetNode()));
            if (moved) {
                agent.moveMgr.resetFailedMoveCount();
                agent.setTargetNodeFromCurrentPath();
            } 

            else {
                agent.moveMgr.incrementFailedMoveCount();
            }
        }



        else {
            // Si le coffre a été **refermé** depuis la dernière observation, ou disparu, ou récupéré, on part.
            boolean isTreasureStillHere = agent.treasureMgr.getCurrentTreasure() != null;
            boolean isLockOpenCoalition = agent.coalitionMgr.getCoalition().isLockOpen();

            boolean isActuallyOpen = isTreasureStillHere ? agent.treasureMgr.getCurrentTreasure().getIsLockOpen() : false;
            boolean beenClosed = isTreasureStillHere ? isLockOpenCoalition && !isActuallyOpen : false;

            if (!isTreasureStillHere || beenClosed) {
                agent.setBehaviourState(AgentBehaviourState.RE_EXPLORATION);
                exitCode = agent.getBehaviourState().getExitCode();
                return;
            }

            Observation type = agent.coalitionMgr.getCoalition().getType();

            if (!isActuallyOpen) 
                agent.openLock(type);

            agent.pick();

            // Si l'agent qu'on voit est un Silo, on tente de lui donner les ressources que l'on a.
            Map<String, String> agentsNearby = agent.visionMgr.getAgentsNearby();
            for (String agentName : agentsNearby.values()) {
                if (agent.freeSpace() < agent.getMyBackPackTotalSpace() && agent.getAgentType() == AgentType.COLLECTOR && agent.otherKnowMgr.getAgentType(agentName) == AgentType.TANKER) {
                    agent.emptyMyBackPack(agentName);
                }
            }
        }

        if (System.currentTimeMillis() - agent.getStartMissionMillis() > agent.getCollectTimeoutMillis())
            exitCode = 1;
    }

    @Override 
    public int onEnd() {
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        return exitCode;
    }
}
