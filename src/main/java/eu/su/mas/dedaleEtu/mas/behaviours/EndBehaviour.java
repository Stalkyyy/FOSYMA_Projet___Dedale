package eu.su.mas.dedaleEtu.mas.behaviours;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import jade.core.behaviours.SimpleBehaviour;

public class EndBehaviour extends SimpleBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;

    private AbstractAgent agent;
    private int exitCode = -1;
    
    public EndBehaviour(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    @Override
    public void action() {
        // System.out.println(agent.getLocalName() + " a fini !~");
        return;
    }

    @Override 
    public int onEnd() {
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        return exitCode;
    }

    @Override
    public boolean done() {
        return false;
    }
}