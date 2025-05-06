package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.List;
import java.util.Random;

import eu.su.mas.dedale.env.gs.GsLocation;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import jade.core.behaviours.OneShotBehaviour;

public class RandomWalk extends OneShotBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    private long startTime = -1;
    
    public RandomWalk(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;
        if (startTime == -1)
            startTime = System.currentTimeMillis();

        Random random = new Random();

        if (agent.getTargetNode() == null)
            agent.moveMgr.setCurrentPathToRandomNode();

        if (random.nextDouble() < 0.05)
            agent.moveMgr.setCurrentPathToRandomNode();

        List<String> accessibleNodes = agent.visionMgr.nodeAvailableList();
        agent.visionMgr.updateTreasure();

        boolean moved = agent.moveTo(new GsLocation(agent.getTargetNode()));
        if (moved) {
            agent.resetFailedMoveCount();
            agent.setTargetNodeFromCurrentPath();
        } 

        else if (agent.getFailedMoveCount() > 2 && !accessibleNodes.isEmpty()) {

            // S'ils restent bloqués trop longtemps durant l'exploration, ils se partageront leur map au bout d'un certain nombre d'essaie. 
            // En théorie, ils se dispatcheront ensuite.
            agent.otherKnowMgr.incrementeLastUpdates_topology();

            if (random.nextDouble() < 0.90) {
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
