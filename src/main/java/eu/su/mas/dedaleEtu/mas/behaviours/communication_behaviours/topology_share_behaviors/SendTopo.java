package eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.topology_share_behaviors;

import java.io.IOException;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.given_knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.msgObjects.TopologyMessage;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class SendTopo extends OneShotBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    
    public SendTopo(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    // Envoie un message contenant des informations de topologie à l'agent cible.
    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;        

        // Récupère l'agent cible pour le partage de topologie.
        String targetAgent = agent.comMgr.getTargetAgent();

        // On construit le message.
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setProtocol("SHARE-TOPOLOGY");
        msg.setSender(agent.getAID());

        // On récupère le bout de map que l'autre ne possède pas à priori (nouveautés et modifications).
        SerializableSimpleGraph<String, MapAttribute> mapToSend = agent.otherKnowMgr.getTopologyDifferenceWith(targetAgent);
        boolean isExploFinished = agent.getExplorationComplete();

        // On prépare l'objet à envoyer.
        // Générer un ID unique pour le message
        int messageId = agent.comMgr.generateMessageId();
        msg.setConversationId(String.valueOf(messageId));
        TopologyMessage newInfos = new TopologyMessage(messageId, targetAgent, mapToSend, isExploFinished);

        msg.addReceiver(new AID(targetAgent, AID.ISLOCALNAME));			
        try {
            // Ajoute l'objet de topologie au contenu du message.
            msg.setContentObject(newInfos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Envoie le message à l'agent cible.
        agent.sendMessage(msg);

        // Ajoute le message de topologie à l'historique des messages envoyés.
        agent.comMgr.addTopologyMessageToHistory(newInfos);
    }

    // Réinitialise les attributs et retourne le code de sortie.
    @Override 
    public int onEnd() {
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        return exitCode;
    }
}