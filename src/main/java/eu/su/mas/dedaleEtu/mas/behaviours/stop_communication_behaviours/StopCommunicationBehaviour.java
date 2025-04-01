package eu.su.mas.dedaleEtu.mas.behaviours.stop_communication_behaviours;

import eu.su.mas.dedaleEtu.mas.agents.MyAgent;
import jade.core.behaviours.OneShotBehaviour;

public class StopCommunicationBehaviour extends OneShotBehaviour {
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private MyAgent agent;
    
    public StopCommunicationBehaviour(final MyAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    @Override
    public void action() {
        agent.comMgr.stopCommunication();
    }

    @Override 
    public int onEnd() {
        return exitCode;
    }
}
