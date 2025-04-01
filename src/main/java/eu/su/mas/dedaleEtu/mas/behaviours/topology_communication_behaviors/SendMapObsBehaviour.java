package eu.su.mas.dedaleEtu.mas.behaviours.topology_communication_behaviors;

import java.io.IOException;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedaleEtu.mas.agents.MyAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.NodeObservations;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.msgObjects.TopologyMessage;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class SendMapObsBehaviour extends OneShotBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private MyAgent agent;
    
    public SendMapObsBehaviour(final MyAgent myagent) {
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
        msg.setProtocol("SHARE-TOPOLOGY");
        msg.setSender(agent.getAID());

        // On récupère le bout de map que l'autre ne possède pas à priori (nouveautés et modifications).
        SerializableSimpleGraph<String, MapAttribute> mapToSend = agent.otherKnowMgr.getTopologyDifferenceWith(targetAgent);
        NodeObservations obsToSend = agent.otherKnowMgr.getObservationsDifferenceWith(targetAgent);
        boolean isExploFinished = agent.getExplorationComplete();

        // On prépare l'objet à envoyer.
        // Générer un ID unique pour le message
        int messageId = agent.comMgr.generateMessageId();
        msg.setConversationId(String.valueOf(messageId));        
        TopologyMessage newInfos = new TopologyMessage(messageId, targetAgent, mapToSend, obsToSend, isExploFinished);

        msg.addReceiver(new AID(targetAgent, AID.ISLOCALNAME));			
        try {
            msg.setContentObject(newInfos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        agent.sendMessage(msg);
        agent.comMgr.addTopologyMessageToHistory(newInfos);
    }

    @Override 
    public int onEnd() {
        if (agent.getLocalName().compareTo("Tim") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        return exitCode;
    }
}