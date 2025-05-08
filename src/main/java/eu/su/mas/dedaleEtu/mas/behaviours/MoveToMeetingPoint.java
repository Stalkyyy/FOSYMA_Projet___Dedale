package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.List;

import eu.su.mas.dedale.env.gs.GsLocation;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentBehaviourState;
import jade.core.behaviours.OneShotBehaviour;

public class MoveToMeetingPoint extends OneShotBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    
    public MoveToMeetingPoint(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;

        this.agent.setBehaviourState(AgentBehaviourState.MEETING_POINT);

        if (agent.getMeetingPoint() == null) {
            String meetingPointId = agent.topoMgr.findMeetingPoint(agent.distanceWeight, agent.degreeWeight);
            agent.setMeetingPoint(meetingPointId);
        }

        List<String> path = agent.getCurrentPath();
        if (path.isEmpty() || !path.getLast().equals(agent.getMeetingPoint())) {
            agent.moveMgr.setCurrentPathTo(agent.getMeetingPoint());
        }

        // Nous sommes arrivés au meeting_point.
        if (agent.getTargetNode() == null) {
            agent.floodMgr.activateFlooding();
            exitCode = 1;
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
        } 
    }

    @Override 
    public int onEnd() {
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        return exitCode;
    }
}
