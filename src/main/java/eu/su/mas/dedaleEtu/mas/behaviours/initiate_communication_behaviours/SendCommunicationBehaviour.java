package eu.su.mas.dedaleEtu.mas.behaviours.initiate_communication_behaviours;

import java.util.Map;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SendCommunicationBehaviour extends OneShotBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    
    public SendCommunicationBehaviour(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    @Override
    public void action() {
        
        // On réinitialise les attributs si besoin.
        exitCode = -1;        

        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setProtocol("COMMUNICATION");
        msg.setSender(agent.getAID());

        Map<String, String> agentsNearby = agent.obsMgr.getAgentsNearby();
        
        for (String agentName : agentsNearby.values()) {
            if (!agent.otherKnowMgr.shouldInitiateCommunication(agentName))
                continue;

            msg.clearAllReceiver();
            msg.addReceiver(new AID(agentName, AID.ISLOCALNAME));

            // On envoie le message.
            agent.sendMessage(msg);

            exitCode = 1;
            break;
        }
    }

    @Override 
    public int onEnd() {
        if (agent.getLocalName().compareTo("Tim") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        return exitCode;
    }
}
