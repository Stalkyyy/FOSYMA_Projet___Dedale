package eu.su.mas.dedaleEtu.mas.behaviours.shareCharacteristics_behaviours;

import java.util.Set;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.MyAgent;
import eu.su.mas.dedaleEtu.mas.msgObjects.CharacteristicsMessage;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveCharacteristicsBehaviour extends OneShotBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = 0;

    private MyAgent agent;
    
    public ReceiveCharacteristicsBehaviour(final MyAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    @Override
    public void action() {
        final MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.INFORM),
            MessageTemplate.MatchProtocol("SHARE-CHARACTERISTICS")
        );

        ACLMessage msg;
        while ((msg = agent.receive(template)) != null) {
            try {
                
                CharacteristicsMessage knowledge = (CharacteristicsMessage) msg.getContentObject();
                Set<Couple<Observation, Integer>>  expertise = knowledge.getExpertise();
                Observation treasureType = knowledge.getTreasureType();
                int msgId = knowledge.getMsgId();


                // Mettre à jour les connaissances des autres agents
                String senderName = msg.getSender().getLocalName();
                agent.otherKnowMgr.updateCharacteristics(senderName, expertise, treasureType);

                // Envoyer un ACK en réponse
                ACLMessage ackMsg = new ACLMessage(ACLMessage.CONFIRM);
                ackMsg.setProtocol("ACK-CHARACTERISTICS");
                ackMsg.setSender(agent.getAID());
                ackMsg.addReceiver(msg.getSender());
                ackMsg.setContent(((Integer) msgId).toString());
                agent.sendMessage(ackMsg);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override 
    public int onEnd() {
        return exitCode;
    }
}
