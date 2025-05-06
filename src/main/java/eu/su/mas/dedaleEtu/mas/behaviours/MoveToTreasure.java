package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.List;
import java.util.Random;

import eu.su.mas.dedale.env.gs.GsLocation;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentBehaviourState;
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

        // Nous sommes arrivés au trésor.
        if (agent.getTargetNode() == null) {
            exitCode = 1;
            return;
        }

        List<String> accessibleNodes = agent.visionMgr.nodeAvailableList();

        agent.visionMgr.updateTreasure();

        // On se déplace.
        boolean moved = agent.moveTo(new GsLocation(agent.getTargetNode()));
        if (moved) {
            agent.resetFailedMoveCount();
            agent.setTargetNodeFromCurrentPath();
        } 

        else if (agent.getFailedMoveCount() > 2 && !accessibleNodes.isEmpty()){
            Random random = new Random();

            if (random.nextDouble() < 0.55) {
                String randomAccessibleNode = accessibleNodes.get(random.nextInt(accessibleNodes.size()));
                agent.setTargetNode(randomAccessibleNode);
                agent.clearCurrentPath();
                agent.moveTo(new GsLocation(agent.getTargetNode()));
                agent.doWait(agent.getBehaviourTimeoutMills());   
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
