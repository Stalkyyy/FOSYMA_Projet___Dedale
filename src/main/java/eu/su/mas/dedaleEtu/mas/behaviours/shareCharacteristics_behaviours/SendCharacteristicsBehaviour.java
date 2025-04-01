package eu.su.mas.dedaleEtu.mas.behaviours.shareCharacteristics_behaviours;

import java.io.IOException;

import eu.su.mas.dedaleEtu.mas.agents.MyAgent;
import eu.su.mas.dedaleEtu.mas.msgObjects.CharacteristicsMessage;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SendCharacteristicsBehaviour extends OneShotBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private MyAgent agent;
    
    public SendCharacteristicsBehaviour(final MyAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    @Override
    public void action() {
        String targetAgent = agent.comMgr.getTargetAgent();

        // On construit le message.
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setProtocol("SHARE-CHARACTERISTICS");
        msg.setSender(agent.getAID());

        // On prépare l'objet à envoyer.
        int messageId = agent.comMgr.generateMessageId();
        msg.setConversationId(String.valueOf(messageId));
        CharacteristicsMessage newInfos = new CharacteristicsMessage(messageId, agent.getName(), targetAgent, agent.getMyExpertise(), agent.getMyTreasureType());

        msg.clearAllReceiver();
        try {					
            msg.setContentObject(newInfos);
        } catch (IOException e) {
            e.printStackTrace();
        } 

        // On envoie le message.
        agent.sendMessage(msg);
        agent.comMgr.addCharacteristicsMessageToHistory(newInfos);
    }

    @Override 
    public int onEnd() {
        return exitCode;
    }
}
