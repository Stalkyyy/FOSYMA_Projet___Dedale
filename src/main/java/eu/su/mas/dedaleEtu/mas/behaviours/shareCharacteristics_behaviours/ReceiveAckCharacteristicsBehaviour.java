package eu.su.mas.dedaleEtu.mas.behaviours.shareCharacteristics_behaviours;

import eu.su.mas.dedaleEtu.mas.agents.MyAgent;
import eu.su.mas.dedaleEtu.mas.msgObjects.CharacteristicsMessage;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveAckCharacteristicsBehaviour extends OneShotBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = 0;

    private MyAgent agent;
    
    public ReceiveAckCharacteristicsBehaviour(final MyAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    @Override
    public void action() {
        // Just added here to let you see what the agent is doing, otherwise he will be too quick.
        try {
            agent.doWait(500);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
            MessageTemplate.MatchProtocol("ACK-CHARACTERISTICS")
        );

        ACLMessage ackMsg;
        while ((ackMsg = agent.receive(template)) != null) {
            try {
                int msgId = Integer.parseInt(ackMsg.getContent());

                CharacteristicsMessage msgObject = agent.comMgr.getCharacteristicsMessage(msgId);
                String receiverName = msgObject.getReceiverName();

                agent.otherKnowMgr.markSharedCharacteristicsTo(receiverName);
            }

            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override 
    public int onEnd() {
        return exitCode;
    }
}
