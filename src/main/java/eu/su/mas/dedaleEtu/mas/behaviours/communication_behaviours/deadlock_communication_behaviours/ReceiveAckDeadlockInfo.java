package eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.deadlock_communication_behaviours;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.NodeReservation;
import eu.su.mas.dedaleEtu.mas.managers.CommunicationManager.COMMUNICATION_STEP;
import eu.su.mas.dedaleEtu.mas.msgObjects.DeadlockMessage;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveAckDeadlockInfo extends SimpleBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    private long startTime = -1;
    
    public ReceiveAckDeadlockInfo(final AbstractAgent myagent) {
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

        MessageTemplate template = MessageTemplate.and(
            MessageTemplate.or(
                MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
                MessageTemplate.or(
                    MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL),
                    MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL)
                ) 
            ),
            MessageTemplate.and(
                MessageTemplate.MatchProtocol("DEADLOCK"),
                MessageTemplate.MatchSender(new AID(targetAgent, AID.ISLOCALNAME))
            )
        );

        ACLMessage ackMsg;
        while ((ackMsg = agent.receive(template)) != null) {
            try {

                // Si l'autre agent a refusé malgré le fait d'avoir eu la priorité, alors on laisse passer.
                if (ackMsg.getPerformative() == ACLMessage.REJECT_PROPOSAL) {  
                    DeadlockMessage DM = (DeadlockMessage) ackMsg.getContentObject();
                    NodeReservation NR = DM.getNodeReservation();

                    String solution = agent.topoMgr.findIntersectionAndAdjacentNode(agent.getCurrentPosition().getLocationId());

                    agent.reserveMgr.mergeNodeReservation(NR);
                    agent.setDeadlockNodeSolution(solution);
                    agent.moveMgr.setCurrentPathTo(solution);

                    agent.comMgr.setLettingHimPass(true);
                }

                agent.moveMgr.resetFailedMoveCount();
                agent.moveMgr.resetTimeDeadlock(targetAgent);

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