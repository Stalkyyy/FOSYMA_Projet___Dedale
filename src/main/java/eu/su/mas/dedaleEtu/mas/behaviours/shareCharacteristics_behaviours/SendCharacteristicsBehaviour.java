package eu.su.mas.dedaleEtu.mas.behaviours.shareCharacteristics_behaviours;

import java.io.IOException;
import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.MyAgent;
import eu.su.mas.dedaleEtu.mas.msgObjects.CharacteristicsMessage;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SendCharacteristicsBehaviour extends OneShotBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = 0;

    private MyAgent agent;
    
    public SendCharacteristicsBehaviour(final MyAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    @Override
    public void action() {

        Location myPosition = agent.getCurrentPosition();
        if (myPosition == null || myPosition.getLocationId().isEmpty())
            return;


        // On construit le message.
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setProtocol("SHARE-CHARACTERISTICS");
        msg.setSender(agent.getAID());


        // On récupère les noms des agents à proximité.
        List<Couple<Location, List<Couple<Observation, String>>>> lobs = agent.observe();
        for (Couple<Location, List<Couple<Observation, String>>> obs : lobs) {

            List<Couple<Observation, String>> attributes = obs.getRight();
            
            // Si on observe un agent, on lui envoie le message.
            for (Couple<Observation, String> observationNode : attributes) {

                // On vérifie si l'observation faite est sur un agent et qu'on lui a pas déjà donné nos charactéristiques.
                boolean isAgentObserved = (observationNode.getLeft() == Observation.AGENTNAME);
                String agentName = observationNode.getRight();
                if (!isAgentObserved || !agent.getListAgentNames().contains(agentName) || !agent.otherKnowMgr.isCharacteristicsShareable(agentName))
                    continue;

                // Générer un ID unique pour le message
                int messageId = agent.comMgr.generateMessageId();
                msg.setConversationId(String.valueOf(messageId));
            
                // On prépare l'objet à envoyer.
                CharacteristicsMessage newInfos = new CharacteristicsMessage(messageId, agent.getName(), agentName, agent.getMyExpertise(), agent.getMyTreasureType());

                // On remplie le reste du message. On l'enverra en broadcast, étant donné que le message ne change pas en fonction de l'agent receiver.
                msg.clearAllReceiver();
                try {					
                    msg.setContentObject(newInfos);
                } catch (IOException e) {
                    e.printStackTrace();
                } 

                // On envoie le message.
                agent.sendMessage(msg);

                // Ajouter le message à l'historique
                agent.comMgr.addCharacteristicsMessageToHistory(newInfos);

                return;
            }
        }
    }

    @Override 
    public int onEnd() {
        return exitCode;
    }
}
