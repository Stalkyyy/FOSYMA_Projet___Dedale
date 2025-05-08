package eu.su.mas.dedaleEtu.mas.behaviours.flooding_behaviours.sharing_plans;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.msgObjects.CoalitionsFloodMessage;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class PropagateFloodCoalitions extends OneShotBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;

    public PropagateFloodCoalitions(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;        

        final MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE),
            MessageTemplate.MatchProtocol("PLAN-FLOODING")
        );

        ACLMessage msg;
        while ((msg = agent.receive(template)) != null) {
            try {

                System.out.println(agent.getLocalName() + " a envoyé le plan");

                CoalitionsFloodMessage msgObject = (CoalitionsFloodMessage) msg.getContentObject();
                agent.setCoalitions(msgObject.getCoalitions());

                if (!agent.floodMgr.isLeaf()) {
                    ACLMessage myChrMsg = new ACLMessage(ACLMessage.PROPAGATE);
                    myChrMsg.setProtocol("PLAN-FLOODING");
                    myChrMsg.setSender(agent.getAID());
                    for (String childName : agent.floodMgr.getChildrenAgents())
                        myChrMsg.addReceiver(new AID(childName, AID.ISLOCALNAME));
                    myChrMsg.setContentObject(msgObject);
                    agent.sendMessage(myChrMsg);    
                }

                exitCode = 1;
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
