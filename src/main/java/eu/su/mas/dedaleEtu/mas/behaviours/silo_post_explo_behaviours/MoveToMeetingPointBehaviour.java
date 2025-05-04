package eu.su.mas.dedaleEtu.mas.behaviours.silo_post_explo_behaviours;

import java.util.List;
import java.util.Random;

import eu.su.mas.dedale.env.gs.GsLocation;
import eu.su.mas.dedaleEtu.mas.agents.SiloAgent;
import jade.core.behaviours.OneShotBehaviour;

public class MoveToMeetingPointBehaviour extends OneShotBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private SiloAgent agent;
    
    public MoveToMeetingPointBehaviour(final SiloAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    @Override
    public void action() {

        if (agent.getMeetingPoint() == null) {
            String meetingPointId = agent.topoMgr.findMeetingPoint(agent.distanceWeight, agent.degreeWeight);
            agent.setMeetingPoint(meetingPointId);
        }

        List<String> path = agent.getCurrentPath();
        if (path.isEmpty() || !path.getLast().equals(agent.getMeetingPoint())) {
            agent.moveMgr.setCurrentPathTo(agent.getMeetingPoint());
        }

        if (agent.getTargetNode() == null) {
            exitCode = 2;
            return;
        }

        List<String> accessibleNodes = agent.visionMgr.nodeAvailableList();

        // On se déplace.
        boolean moved = agent.moveTo(new GsLocation(agent.getTargetNode()));
        if (moved) {
            agent.resetFailedMoveCount();
            agent.setTargetNodeFromCurrentPath();
        } 

        else if (agent.getFailedMoveCount() > 2 && !accessibleNodes.isEmpty()){
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
