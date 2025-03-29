package eu.su.mas.dedaleEtu.mas.behaviours;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.GeneralAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.knowledge.NodeObservations;
import eu.su.mas.dedaleEtu.mas.msgObjects.TopologyObservations;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveMapObsBehaviour extends OneShotBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = 0;

    private GeneralAgent agent;
    
    public ReceiveMapObsBehaviour(final AbstractDedaleAgent myagent) {
        super(myagent);
        this.agent = (GeneralAgent) myagent;
    }

    @Override
    public void action() {
        final MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.INFORM),
            MessageTemplate.MatchProtocol("SHARE-TOPO-OBS")
        );

        ACLMessage msg;
        while ((msg = this.agent.receive(template)) != null) {
            try {
                
                TopologyObservations knowledge = (TopologyObservations) msg.getContentObject();
                SerializableSimpleGraph<String, MapAttribute> topology = knowledge.getTopology();
                NodeObservations nodeObs = knowledge.getObservations();
                boolean isExploFinished = knowledge.getFinishedExplo();
                int msgId = knowledge.getMsgId();

                // Mettre à jour la topologie et les observations de l'agent
                if (topology != null)
                    agent.topoMgr.merge(topology);

                if (!nodeObs.isEmpty())
                    agent.obsMgr.merge(nodeObs);

                // Mettre à jour les connaissances des autres agents
                String senderName = msg.getSender().getLocalName();
                agent.otherKnowMgr.updateTopology(senderName, topology);
                agent.otherKnowMgr.updateObservations(senderName, nodeObs);
                if (isExploFinished) {
                    this.agent.getOtherAgentsTopology().agentFinishedExplo(senderName);
                    agent.setExploFinished(true);
                }

                // Envoyer un ACK en réponse
                ACLMessage ackMsg = new ACLMessage(ACLMessage.CONFIRM);
                ackMsg.setProtocol("ACK");
                ackMsg.setSender(this.agent.getAID());
                ackMsg.addReceiver(msg.getSender());
                ackMsg.setContent(((Integer) msgId).toString());
                this.agent.sendMessage(ackMsg);

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