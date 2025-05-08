package eu.su.mas.dedaleEtu.mas.behaviours;

import eu.su.mas.dedale.env.gs.GsLocation;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentBehaviourState;
import jade.core.behaviours.OneShotBehaviour;

public class MoveToDeadlockNode extends OneShotBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    
    public MoveToDeadlockNode(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;

        agent.doWait(250);

        // Nous sommes arrivés au point de deadlock.
        if (agent.getTargetNode() == null) {

            if (agent.coalitionMgr.getCoalition() != null && agent.coalitionMgr.hasAgentInCoalition(agent.getNodeReservation().getAgentName())) {
                if (agent.coalitionMgr.getRole(agent.getNodeReservation().getAgentName()).getPriority() > agent.coalitionMgr.getRole().getPriority())
                    agent.doWait(250);
            }    

            agent.setNodeReservation(null);
            agent.comMgr.setLettingHimPass(false);

            return;
        }

        agent.visionMgr.updateTreasure();
        agent.moveMgr.incrementeTimeDeadlock();

        // On se déplace.
        boolean moved = agent.moveTo(new GsLocation(agent.getTargetNode()));
        if (moved) {
            agent.moveMgr.resetFailedMoveCount();
            agent.setTargetNodeFromCurrentPath();
        } 

        else {
            agent.moveMgr.incrementFailedMoveCount();
            agent.topoMgr.incrementUpdateCount();
        } 

        if (agent.getBehaviourState() != AgentBehaviourState.EXPLORATION && System.currentTimeMillis() - agent.getStartMissionMillis() > agent.getCollectTimeoutMillis())
            exitCode = 1;
    }

    @Override 
    public int onEnd() {
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        return exitCode;
    }
}
