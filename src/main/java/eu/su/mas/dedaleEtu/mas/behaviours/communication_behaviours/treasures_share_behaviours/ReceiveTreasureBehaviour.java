package eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.treasures_share_behaviours;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.TreasureObservations;
import eu.su.mas.dedaleEtu.mas.msgObjects.TreasureMessage;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveTreasureBehaviour extends SimpleBehaviour {
        private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    private long startTime = System.currentTimeMillis();
    
    public ReceiveTreasureBehaviour(final AbstractAgent myagent) {
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
            MessageTemplate.MatchPerformative(ACLMessage.INFORM),
            MessageTemplate.and(
                MessageTemplate.MatchProtocol("SHARE-TREASURE"),
                MessageTemplate.MatchSender(new AID(targetAgent, AID.ISLOCALNAME))
            )
        );

        ACLMessage msg;
        while ((msg = agent.receive(template)) != null) {
            try {
                
                TreasureMessage knowledge = (TreasureMessage) msg.getContentObject();
                TreasureObservations treasures = knowledge.getTreasures();
                int msgId = knowledge.getMsgId();

                // Mettre à jour les trésors de l'agent
                if (treasures != null)
                    agent.treasureMgr.merge(treasures);

                // Mettre à jour les connaissances des autres agents
                agent.otherKnowMgr.updateTreasures(targetAgent, treasures);

                // Envoyer un ACK en réponse
                ACLMessage ackMsg = new ACLMessage(ACLMessage.CONFIRM);
                ackMsg.setProtocol("SHARE-TREASURE");
                ackMsg.setSender(agent.getAID());
                ackMsg.addReceiver(msg.getSender());
                ackMsg.setContent(((Integer) msgId).toString());
                agent.sendMessage(ackMsg);

                // Permet de passer au prochain step.
                exitCode = 1;
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
