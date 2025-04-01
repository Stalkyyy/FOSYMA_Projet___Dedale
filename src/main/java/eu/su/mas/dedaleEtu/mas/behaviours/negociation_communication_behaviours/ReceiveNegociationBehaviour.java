package eu.su.mas.dedaleEtu.mas.behaviours.negociation_communication_behaviours;

import eu.su.mas.dedaleEtu.mas.agents.MyAgent;
import eu.su.mas.dedaleEtu.mas.managers.CommunicationManager.COMMUNICATION_STEP;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveNegociationBehaviour extends SimpleBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private MyAgent agent;
    private long startTime = -1;
    
    public ReceiveNegociationBehaviour(final MyAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    @Override
    public void action() {
        if (startTime == -1)
            startTime = System.currentTimeMillis();

        String targetAgent = agent.comMgr.getTargetAgent();

        final MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.INFORM),
            MessageTemplate.and(
                MessageTemplate.MatchProtocol("NEGOCIATING"), 
                MessageTemplate.MatchSender(new AID(targetAgent, AID.ISLOCALNAME))
            )
        );
            
        ACLMessage msg;
        while ((msg = agent.receive(template)) != null) {
            try {
                String msgContent = msg.getContent();
                String[] stepsArray = msgContent.split(";");

                for (String stepStr : stepsArray) {
                    COMMUNICATION_STEP step = COMMUNICATION_STEP.valueOf(stepStr);
                    agent.comMgr.addStep(step);
                }

                // On confirme le reçu.
                ACLMessage ackMsg = new ACLMessage(ACLMessage.CONFIRM);
                ackMsg.setProtocol("NEGOCIATING");
                ackMsg.setSender(agent.getAID());
                ackMsg.addReceiver(msg.getSender());
                agent.sendMessage(ackMsg);

                // Permet de passer au prochain step.
                COMMUNICATION_STEP nextStep = agent.comMgr.getStep();
                exitCode = nextStep == null ? 0 : nextStep.getExitCode();
                break;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean done() {
        return (exitCode != -1) || (System.currentTimeMillis() - startTime > agent.getBehaviourTimeoutMills());
    }

    @Override 
    public int onEnd() {
        startTime = -1;
        return exitCode;
    }
}
