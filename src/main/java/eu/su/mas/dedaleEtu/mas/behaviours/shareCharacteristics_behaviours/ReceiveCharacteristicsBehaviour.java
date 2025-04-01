package eu.su.mas.dedaleEtu.mas.behaviours.shareCharacteristics_behaviours;

import java.util.Set;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.MyAgent;
import eu.su.mas.dedaleEtu.mas.msgObjects.CharacteristicsMessage;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveCharacteristicsBehaviour extends SimpleBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private MyAgent agent;
    private long startTime = -1;
    
    public ReceiveCharacteristicsBehaviour(final MyAgent myagent) {
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
                MessageTemplate.MatchProtocol("SHARE-CHARACTERISTICS"),
                MessageTemplate.MatchSender(new AID(targetAgent, AID.ISLOCALNAME))
            )
        );

        ACLMessage msg = agent.receive(template);
        if (msg == null) return;

        try {
            CharacteristicsMessage knowledge = (CharacteristicsMessage) msg.getContentObject();
            Set<Couple<Observation, Integer>> expertise = knowledge.getExpertise();
            Observation treasureType = knowledge.getTreasureType();
            int msgId = knowledge.getMsgId();

            // Mettre à jour les connaissances des autres agents
            agent.otherKnowMgr.updateCharacteristics(targetAgent, expertise, treasureType);

            // Envoyer un ACK en réponse
            ACLMessage ackMsg = new ACLMessage(ACLMessage.CONFIRM);
            ackMsg.setProtocol("SHARE-CHARACTERISTICS");
            ackMsg.setSender(agent.getAID());
            ackMsg.addReceiver(msg.getSender());
            ackMsg.setContent(((Integer) msgId).toString());
            agent.sendMessage(ackMsg);

            // Permet de passer au prochain step.
            exitCode = 1;

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
        if (agent.getLocalName().compareTo("Tim") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        startTime = -1;
        return exitCode;
    }
}
