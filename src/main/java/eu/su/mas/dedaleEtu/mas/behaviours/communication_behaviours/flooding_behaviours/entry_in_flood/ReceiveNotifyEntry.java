package eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.flooding_behaviours.entry_in_flood;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.FloodingState.FLOODING_STEP;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveNotifyEntry extends OneShotBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;

    public ReceiveNotifyEntry(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

        @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;        

        final MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.INFORM),
            MessageTemplate.MatchProtocol("ENTRY-FLOODING")
        );

        ACLMessage msg;
        while ((msg = agent.receive(template)) != null) {
            try {

                String childName = msg.getSender().getLocalName();
                String newAgentName = msg.getContent();

                agent.floodMgr.addAgentsInTree(newAgentName);
                agent.floodMgr.addAccessibleAgent(childName, newAgentName);

                // Si, pour l'agent root, tout le monde est là, alors on passe à la suite. Sinon, il attend.
                if (agent.floodMgr.isRoot()) {
                    if (!agent.floodMgr.isEveryoneInTree())
                        return;

                    if (agent.floodMgr.isFirstFlooding())
                        agent.floodMgr.setStep(FLOODING_STEP.SHARING_CHARACTERISTICS);
                    else
                        agent.floodMgr.setStep(FLOODING_STEP.SHARING_TREASURES);
                } 

                // Les autres vont notifier leur parent.
                else {
                    ACLMessage notifyMsg = new ACLMessage(ACLMessage.INFORM);
                    notifyMsg.setProtocol("ENTRY-FLOODING");
                    notifyMsg.setSender(agent.getAID());
                    notifyMsg.addReceiver(new AID(agent.floodMgr.getParentAgent(), AID.ISLOCALNAME));
                    notifyMsg.setContent(newAgentName);
                    agent.sendMessage(notifyMsg);
                }

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