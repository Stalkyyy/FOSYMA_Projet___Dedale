package eu.su.mas.dedaleEtu.mas.behaviours.shareMap_behaviours;

import java.util.List;
import java.io.IOException;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import dataStructures.tuple.Couple;
import eu.su.mas.dedaleEtu.mas.agents.MyAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.NodeObservations;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.msgObjects.TopologyMessage;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class SendMapObsBehaviour extends OneShotBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = 0;

    private MyAgent agent;
    
    public SendMapObsBehaviour(final MyAgent myagent) {
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
        msg.setProtocol("SHARE-TOPO-OBS");
        msg.setSender(agent.getAID());


        // On récupère les noms des agents à proximité.
        List<Couple<Location, List<Couple<Observation, String>>>> lobs = agent.observe();
        for (Couple<Location, List<Couple<Observation, String>>> obs : lobs) {

            List<Couple<Observation, String>> attributes = obs.getRight();
            
            // Si on observe un agent, on lui envoie le message.
            for (Couple<Observation, String> observationNode : attributes) {

                // On vérifie si l'observation faite est sur un agent.
                boolean isAgentObserved = (observationNode.getLeft() == Observation.AGENTNAME);
                String agentName = observationNode.getRight();
                if (!isAgentObserved || !agent.getListAgentNames().contains(agentName) || !agent.otherKnowMgr.isTopologyShareable(agentName))
                    continue;

                // On récupère le bout de map que l'autre ne possède pas à priori (nouveautés et modifications).
                SerializableSimpleGraph<String, MapAttribute> mapToSend = agent.otherKnowMgr.getTopologyDifferenceWith(agentName);
                NodeObservations obsToSend = agent.otherKnowMgr.getObservationsDifferenceWith(agentName);
                boolean isExploFinished = agent.getExplorationComplete();

                if (mapToSend == null && obsToSend.isEmpty())
                    continue;

                // Générer un ID unique pour le message
                int messageId = agent.comMgr.generateMessageId();
                msg.setConversationId(String.valueOf(messageId));
            
                // On prépare l'objet à envoyer.
                TopologyMessage newInfos = new TopologyMessage(messageId, agentName, mapToSend, obsToSend, isExploFinished);

                // On remplie le reste du message. On l'enverra spécifiquement pour un agent.
                msg.clearAllReceiver();
                msg.addReceiver(new AID(agentName, AID.ISLOCALNAME));
                try {					
                    msg.setContentObject(newInfos);
                } catch (IOException e) {
                    e.printStackTrace();
                } 

                // On envoie le message.
                agent.sendMessage(msg);

                // Ajouter le message à l'historique
                agent.comMgr.addTopologyMessageToHistory(newInfos);
            }
        }
    }

    @Override 
    public int onEnd() {
        return exitCode;
    }
}