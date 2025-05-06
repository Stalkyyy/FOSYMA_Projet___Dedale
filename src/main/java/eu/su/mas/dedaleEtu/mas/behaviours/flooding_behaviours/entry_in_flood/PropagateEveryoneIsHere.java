package eu.su.mas.dedaleEtu.mas.behaviours.flooding_behaviours.entry_in_flood;

import java.io.IOException;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.FloodingState.FLOODING_STEP;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class PropagateEveryoneIsHere extends OneShotBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;

    public PropagateEveryoneIsHere(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

        @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;      
        
        // ====================================================================================
        

        // L'agent root dit aux autres qu'il y a tout le monde, et dans quel mode on passe.
        if (agent.floodMgr.isRoot() && agent.floodMgr.getStep() != FLOODING_STEP.WAITING_FOR_EVERYONE) {
            ACLMessage msg = new ACLMessage(ACLMessage.PROPAGATE);
            msg.setProtocol("ENTRY-FLOODING");
            msg.setSender(agent.getAID());
    
            for(String childName : agent.floodMgr.getChildrenAgents()) {
                msg.addReceiver(new AID(childName, AID.ISLOCALNAME));
            }
    
            try {
                msg.setContentObject(agent.floodMgr.getStep());
                agent.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            exitCode = agent.floodMgr.getStep().getExitCode();
            return;
        }

        else if (agent.floodMgr.isRoot())
            return;


        // ====================================================================================


        final MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE),
            MessageTemplate.MatchProtocol("ENTRY-FLOODING")
        );

        ACLMessage msg;
        while ((msg = agent.receive(template)) != null) {
            try {

                FLOODING_STEP step = (FLOODING_STEP) msg.getContentObject();

                ACLMessage propagateMsg = new ACLMessage(ACLMessage.PROPAGATE);
                propagateMsg.setProtocol("ENTRY-FLOODING");
                propagateMsg.setSender(agent.getAID());
        
                for(String childName : agent.floodMgr.getChildrenAgents()) {
                    propagateMsg.addReceiver(new AID(childName, AID.ISLOCALNAME));
                }
        
                propagateMsg.setContentObject(step);
                agent.sendMessage(propagateMsg);
                
                exitCode = step.getExitCode();
                break;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override 
    public int onEnd() {
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        return exitCode;
    }
}
