package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.List;
import java.io.IOException;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import dataStructures.tuple.Couple;
import eu.su.mas.dedaleEtu.mas.agents.GeneralAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.NodeObservations;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.msgObjects.TopologyObservations;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class SendMapObsBehaviour extends OneShotBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = 0;

    private GeneralAgent agent;
    
    public SendMapObsBehaviour(final AbstractDedaleAgent myagent) {
        super(myagent);
        this.agent = (GeneralAgent) myagent;
    }

    @Override
    public void action() {
        Location myPosition = this.agent.getCurrentPosition();
        if (myPosition == null || myPosition.getLocationId().isEmpty())
            return;

        // On construit le message.
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setProtocol("SHARE-TOPO-OBS");
        msg.setSender(this.agent.getAID());

        // Générer un ID unique pour le message
        int messageId = this.agent.generateMessageId();
        msg.setConversationId(String.valueOf(messageId));

        // On récupère les noms des agents à proximité.
        List<Couple<Location, List<Couple<Observation, String>>>> lobs = this.agent.observe();
        for (Couple<Location, List<Couple<Observation, String>>> obs : lobs) {
            List<Couple<Observation, String>> attributes = obs.getRight();

            // Si on observe un agent, on lui envoie le message.
            for (Couple<Observation, String> observationNode : attributes) {

                // On vérifie si l'observation faite est sur un agent.
                boolean isAgentObserved = (observationNode.getLeft() == Observation.AGENTNAME);
                String agentName = observationNode.getRight();
                if (!isAgentObserved || !this.agent.getListAgentNames().contains(agentName) || !this.agent.getOtherAgentsTopology().canSendInfoToAgent(agentName))
                    continue;

                // On récupère le bout de map que l'autre ne possède pas à priori (nouveautés et modifications).
                SerializableSimpleGraph<String, MapAttribute> mapToSend = this.agent.getOtherAgentsTopology().diffTopology(agentName, this.agent.getMyMap().getSerializableGraph());
                NodeObservations obsToSend = this.agent.getMyObservations().getUniqueObservations(this.agent.getOtherAgentsObservations().getObservations(agentName));

                if (mapToSend == null && obsToSend.isEmpty())
                    continue;

                // On prépare l'objet à envoyer.
                TopologyObservations newInfos = new TopologyObservations(0, agentName, mapToSend, obsToSend);

                // On remplie le reste du message. On l'enverra spécifiquement pour un agent.
                msg.clearAllReceiver();
                msg.addReceiver(new AID(agentName, AID.ISLOCALNAME));
                try {					
                    msg.setContentObject(newInfos);
                } catch (IOException e) {
                    e.printStackTrace();
                } 

                // On envoie le message.
                this.agent.sendMessage(msg);
                //System.out.println(this.agent.getLocalName() + " a envoyé sa carte à " + agentName);

                // Ajouter le message à l'historique
                this.agent.addSentMessageToHistory(newInfos);
            }
        }
    }

    @Override 
    public int onEnd() {
        return exitCode;
    }
}