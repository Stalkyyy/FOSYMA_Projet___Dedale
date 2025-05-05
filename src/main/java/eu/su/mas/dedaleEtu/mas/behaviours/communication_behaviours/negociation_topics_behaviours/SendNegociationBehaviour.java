package eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.negociation_topics_behaviours;

import java.io.IOException;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentBehaviourState;
import eu.su.mas.dedaleEtu.mas.managers.CommunicationManager.COMMUNICATION_STEP;
import eu.su.mas.dedaleEtu.mas.msgObjects.CommunicationStepMessage;
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

        CommunicationStepMessage msgObject = new CommunicationStepMessage();

        if (agent.otherKnowMgr.isTopologyShareable(targetAgent)) {
            agent.comMgr.addStep(COMMUNICATION_STEP.SHARE_TOPO);
            msgObject.addStep(COMMUNICATION_STEP.SHARE_TOPO);
        }

        // Le partager en avance permet de déposer les bouts de trésor pris durant l'exploration si besoin.
        if (agent.otherKnowMgr.isCharacteristicsShareable(targetAgent)) {
            agent.comMgr.addStep(COMMUNICATION_STEP.SHARE_CHARACTERISTICS);
            msgObject.addStep(COMMUNICATION_STEP.SHARE_CHARACTERISTICS);
        }



        if (agent.getBehaviourState() == AgentBehaviourState.FLOODING && !agent.floodMgr.hasContactedAgent(targetAgent)) {
            agent.comMgr.addStep(COMMUNICATION_STEP.ENTRY_FLOOD_SENT);
            msgObject.addStep(COMMUNICATION_STEP.ENTRY_FLOOD_RECEIVED);
        }        



        try {
            msg.setContentObject(msgObject);
        } catch (IOException e) {
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