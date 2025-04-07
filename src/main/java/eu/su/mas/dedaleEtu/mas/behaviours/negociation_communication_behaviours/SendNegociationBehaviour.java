package eu.su.mas.dedaleEtu.mas.behaviours.negociation_communication_behaviours;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.managers.CommunicationManager.COMMUNICATION_STEP;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SendNegociationBehaviour extends OneShotBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    
    public SendNegociationBehaviour(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;        

        String targetAgent = agent.comMgr.getTargetAgent();

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setProtocol("NEGOCIATING");
        msg.setSender(agent.getAID());
        msg.addReceiver(new AID(targetAgent, AID.ISLOCALNAME));

        StringBuilder msgObject = new StringBuilder();

        if (agent.otherKnowMgr.isTopologyShareable(targetAgent)) {
            agent.comMgr.addStep(COMMUNICATION_STEP.SHARE_TOPO);
            msgObject = msgObject.append(COMMUNICATION_STEP.SHARE_TOPO.toString()).append(";");
        }

        if (agent.otherKnowMgr.isCharacteristicsShareable(targetAgent)) {
            agent.comMgr.addStep(COMMUNICATION_STEP.SHARE_CHARACTERISTICS);
            msgObject = msgObject.append(COMMUNICATION_STEP.SHARE_CHARACTERISTICS.toString()).append(";");
        }

        msg.setContent(msgObject.toString());
        agent.send(msg);
    }

    @Override 
    public int onEnd() {
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);
        return exitCode;
    }
}