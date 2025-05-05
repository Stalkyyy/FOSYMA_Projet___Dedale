package eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.flooding_behaviours.entry_in_flood;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.managers.CommunicationManager.COMMUNICATION_STEP;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveRequestFloodingEntry extends SimpleBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    private long startTime = System.currentTimeMillis();
    
    public ReceiveRequestFloodingEntry(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;
        if (startTime == -1)
            startTime = System.currentTimeMillis();  

        String targetAgent = agent.comMgr.getTargetAgent();

        final MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
            MessageTemplate.and(
                MessageTemplate.MatchProtocol("ENTRY-FLOODING"),
                MessageTemplate.MatchSender(new AID(targetAgent, AID.ISLOCALNAME))
            )
        );

        ACLMessage msg;
        while ((msg = agent.receive(template)) != null) {
            try {

                int perf;

                // S'il est déjà dans le flood, alors il refuse.
                if (agent.floodMgr.isFloodingActive())
                    perf = ACLMessage.REJECT_PROPOSAL;

                // Sinon, il l'accepte.
                else {
                    perf = ACLMessage.ACCEPT_PROPOSAL;
                    agent.floodMgr.activateFlooding(targetAgent);
                }

                // Envoyer un ACK en réponse
                ACLMessage ackMsg = new ACLMessage(perf);
                ackMsg.setProtocol("ENTRY-FLOODING");
                ackMsg.setSender(agent.getAID());
                ackMsg.addReceiver(msg.getSender());
                agent.sendMessage(ackMsg);

                // Permet de passer au prochain step.
                COMMUNICATION_STEP nextStep = agent.comMgr.getNextStep();
                exitCode = nextStep == null ? 0 : nextStep.getExitCode();
                agent.comMgr.removeStep(nextStep);
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
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        startTime = -1;
        return exitCode;
    }
}
