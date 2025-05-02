package eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.treasures_share_behaviours;

import java.io.IOException;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.TreasureObservations;
import eu.su.mas.dedaleEtu.mas.msgObjects.TreasureMessage;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SendTreasureBehaviour extends OneShotBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    
    public SendTreasureBehaviour(final AbstractAgent myagent) {
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
        msg.setProtocol("SHARE-TREASURE");
        msg.setSender(agent.getAID());

        // On récupère les trésors que l'autre ne possède pas à priori (nouveautés et modifications).
        TreasureObservations treasuresToSend = agent.otherKnowMgr.getTreasuresDifferenceWith(targetAgent);

        // On prépare l'objet à envoyer.
        int messageId = agent.comMgr.generateMessageId();
        msg.setConversationId(String.valueOf(messageId));
        TreasureMessage newInfos = new TreasureMessage(messageId, targetAgent, treasuresToSend);

        msg.addReceiver(new AID(targetAgent, AID.ISLOCALNAME));			
        try {
            msg.setContentObject(newInfos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        agent.sendMessage(msg);
        agent.comMgr.addTreasureMessageToHistory(newInfos);
    }

    @Override 
    public int onEnd() {
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        return exitCode;
    }
}
