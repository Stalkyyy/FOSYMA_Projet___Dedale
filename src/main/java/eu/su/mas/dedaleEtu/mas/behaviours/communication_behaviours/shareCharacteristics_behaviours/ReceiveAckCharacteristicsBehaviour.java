package eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.shareCharacteristics_behaviours;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.managers.CommunicationManager.COMMUNICATION_STEP;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveAckCharacteristicsBehaviour extends SimpleBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    private long startTime = -1;
    
    public ReceiveAckCharacteristicsBehaviour(final AbstractAgent myagent) {
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
            MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
            MessageTemplate.and(
                MessageTemplate.MatchProtocol("SHARE-CHARACTERISTICS"),
                MessageTemplate.MatchSender(new AID(targetAgent, AID.ISLOCALNAME))
            )
        );

        ACLMessage msg = agent.receive(template);
        if (msg == null) return;

        try {
            agent.otherKnowMgr.markSharedCharacteristicsTo(targetAgent);

            // Permet de passer au prochain step.
            COMMUNICATION_STEP nextStep = agent.comMgr.getNextStep();
            exitCode = nextStep == null ? 0 : nextStep.getExitCode();
            agent.comMgr.removeStep(nextStep);

        } catch (Exception e) {
            e.printStackTrace();
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
