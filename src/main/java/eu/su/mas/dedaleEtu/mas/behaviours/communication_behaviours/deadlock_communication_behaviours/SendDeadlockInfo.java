package eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.deadlock_communication_behaviours;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.NodeReservation;
import eu.su.mas.dedaleEtu.mas.msgObjects.DeadlockMessage;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SendDeadlockInfo extends OneShotBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    
    public SendDeadlockInfo(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;  
        
        String targetAgent = agent.comMgr.getTargetAgent();

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setProtocol("DEADLOCK");
        msg.setSender(agent.getAID());
        msg.addReceiver(new AID(targetAgent, AID.ISLOCALNAME));

        // On prépare l'objet du message
        String currentNodeId = agent.getCurrentPosition().getLocationId();
        NodeReservation NR = agent.reserveMgr.createNodeReservation();
        DeadlockMessage msgObject = new DeadlockMessage(currentNodeId, NR);

        try {
            msg.setContentObject(msgObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        agent.sendMessage(msg);
    }

    @Override 
    public int onEnd() {
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        return exitCode;
    }
}