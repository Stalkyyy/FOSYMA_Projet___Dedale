package eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.initiate_communication_behaviours;

import java.util.Map;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentType;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SendCommunication extends OneShotBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    
    public SendCommunication(final AbstractAgent myagent) {
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

        Map<String, String> agentsNearby = agent.visionMgr.getAgentsNearby();
        
        for (String agentName : agentsNearby.values()) {

            // Si l'agent qu'on croise est un Silo, on tente de lui donner les ressources que l'on a.
            if (agent.freeSpace() < agent.getMyBackPackTotalSpace() && agent.getAgentType() == AgentType.COLLECTOR && agent.otherKnowMgr.getAgentType(agentName) == AgentType.TANKER) {
                agent.emptyMyBackPack(agentName);
            }

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
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        return exitCode;
    }
}
