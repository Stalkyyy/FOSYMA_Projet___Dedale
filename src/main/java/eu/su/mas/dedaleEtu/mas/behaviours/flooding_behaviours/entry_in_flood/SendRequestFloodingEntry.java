package eu.su.mas.dedaleEtu.mas.behaviours.flooding_behaviours.entry_in_flood;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SendRequestFloodingEntry extends OneShotBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    
    public SendRequestFloodingEntry(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;        

        String targetAgent = agent.comMgr.getTargetAgent();

        ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
        msg.setProtocol("ENTRY-FLOODING");
        msg.setSender(agent.getAID());
        msg.addReceiver(new AID(targetAgent, AID.ISLOCALNAME));
        agent.sendMessage(msg);
    }

    @Override 
    public int onEnd() {
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);
        return exitCode;
    }
}