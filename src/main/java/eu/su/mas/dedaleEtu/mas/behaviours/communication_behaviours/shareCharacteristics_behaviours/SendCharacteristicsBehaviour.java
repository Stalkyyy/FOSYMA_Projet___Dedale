package eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.shareCharacteristics_behaviours;

import java.io.IOException;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.msgObjects.CharacteristicsMessage;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SendCharacteristicsBehaviour extends OneShotBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    
    public SendCharacteristicsBehaviour(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;        
        
        String targetAgent = agent.comMgr.getTargetAgent();

        // On construit le message.
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setProtocol("SHARE-CHARACTERISTICS");
        msg.setSender(agent.getAID());
        msg.addReceiver(new AID(targetAgent, AID.ISLOCALNAME));


        // On prépare l'objet à envoyer.
        int messageId = agent.comMgr.generateMessageId();
        msg.setConversationId(String.valueOf(messageId));
        CharacteristicsMessage newInfos = new CharacteristicsMessage(messageId, agent.getName(), targetAgent, agent.getAgentType(), agent.getMyExpertise(), agent.getMyTreasureType());

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
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        return exitCode;
    }
}
