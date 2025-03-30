package eu.su.mas.dedaleEtu.mas.behaviours;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedaleEtu.mas.agents.MyAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.NodeObservations;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.msgObjects.TopologyMessage;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveAckMapObsBehaviour extends OneShotBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    // private int exitCode = 0;

    private MyAgent agent;
    
    public ReceiveAckMapObsBehaviour(final MyAgent myagent) {
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
            MessageTemplate.MatchProtocol("ACK")
        );

        ACLMessage ackMsg;
        while ((ackMsg = agent.receive(template)) != null) {
            try {
                int msgId = Integer.parseInt(ackMsg.getContent());

                TopologyMessage msgObject = agent.comMgr.getTopologyMessageToHistory(msgId);    

                String receiverName = msgObject.getReceiverName();
                SerializableSimpleGraph<String, MapAttribute> topo_sent = msgObject.getTopology();
                NodeObservations obs_sent = msgObject.getObservations();
                boolean isExploFinished = msgObject.getExplorationComplete();

                agent.otherKnowMgr.mergeTopologyOf(receiverName, topo_sent);
                agent.otherKnowMgr.mergeObservationOf(receiverName, obs_sent);
                agent.otherKnowMgr.resetLastUpdateAgent(receiverName);

                if (isExploFinished) {
                    agent.otherKnowMgr.markExplorationComplete(receiverName);
                }
            }

            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override 
    public int onEnd() {
        return agent.getExplorationComplete() ? 1 : 0;
    }
}