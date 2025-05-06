package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.Map;

import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentBehaviourState;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentType;
import jade.core.behaviours.SimpleBehaviour;

public class OnTreasure extends SimpleBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    private long startTime = -1;
    
    public OnTreasure(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;
        if (startTime == -1)
            startTime = System.currentTimeMillis();
        


        agent.visionMgr.updateTreasure();


        // Si le coffre a été **refermé** depuis la dernière observation, ou disparu, ou récupéré, on part.
        boolean isTreasureStillHere = agent.treasureMgr.getCurrentTreasure() != null;
        boolean isLockOpenCoalition = agent.coalitionMgr.getCoalition().isLockOpen();

        boolean isActuallyOpen = isTreasureStillHere ? agent.treasureMgr.getCurrentTreasure().getIsLockOpen() : false;
        boolean beenClosed = isTreasureStillHere ? isLockOpenCoalition && !isActuallyOpen : false;

        if (!isTreasureStillHere || beenClosed) {
            agent.setBehaviourState(AgentBehaviourState.MEETING_POINT);
            exitCode = agent.getBehaviourState().getExitCode();
        }

        Observation type = agent.coalitionMgr.getCoalition().getType();

        if (!isActuallyOpen) 
            agent.openLock(type);

        // Si l'agent qu'on voit est un Silo, on tente de lui donner les ressources que l'on a.
        Map<String, String> agentsNearby = agent.visionMgr.getAgentsNearby();
        for (String agentName : agentsNearby.values()) {
            if (agent.freeSpace() < agent.getMyBackPackTotalSpace() && agent.otherKnowMgr.getAgentType(agentName) == AgentType.TANKER)
                agent.emptyMyBackPack(agentName);
        }
    }

    @Override
    public boolean done() {
        boolean isDone = false;

        isDone = (exitCode != -1) || (System.currentTimeMillis() - startTime > agent.getCollectTimeoutMillis());

        return isDone;
    }


    @Override 
    public int onEnd() {
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        agent.setBehaviourState(AgentBehaviourState.MEETING_POINT);
        exitCode = agent.getBehaviourState().getExitCode();

        return exitCode;
    }
}
