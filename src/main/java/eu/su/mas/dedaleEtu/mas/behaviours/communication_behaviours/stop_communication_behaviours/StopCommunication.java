package eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.stop_communication_behaviours;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentBehaviourState;
import jade.core.behaviours.OneShotBehaviour;

public class StopCommunication extends OneShotBehaviour {
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    
    public StopCommunication(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    @Override
    public void action() {
        agent.comMgr.stopCommunication();
    }

    @Override 
    public int onEnd() {

        if (agent.getBehaviourState() == AgentBehaviourState.FLOODING)
            exitCode = agent.getBehaviourState().getExitCode();
        else if (agent.comMgr.getLettingHimPass())
            exitCode = -2;
        else 
            exitCode = agent.getBehaviourState().getExitCode();


        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        return exitCode;
    }
}
