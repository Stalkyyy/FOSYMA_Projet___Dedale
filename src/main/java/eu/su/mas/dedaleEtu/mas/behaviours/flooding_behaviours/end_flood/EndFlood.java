package eu.su.mas.dedaleEtu.mas.behaviours.flooding_behaviours.end_flood;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentBehaviourState;
import jade.core.behaviours.OneShotBehaviour;

public class EndFlood extends OneShotBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;

    public EndFlood(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;        

        if (agent.floodMgr.isRoot())
            System.out.println("\n" + agent.coalitionMgr.toString() + "\n");

        agent.floodMgr.deactivateFlooding();

        if (agent.coalitionMgr.getCoalition() != null)
            agent.setBehaviourState(AgentBehaviourState.COLLECT_TREASURE);
        else
            agent.setBehaviourState(AgentBehaviourState.RE_EXPLORATION);

        agent.startMissionMillis();
        exitCode = agent.getBehaviourState().getExitCode(); 

        System.out.println(agent.getLocalName() + " finished the flood ! -> " + agent.getBehaviourState());
    }

    @Override 
    public int onEnd() {
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        return exitCode;
    }
}
