package eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.topology_share_behaviors;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.given_knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.msgObjects.TopologyMessage;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveTopo extends SimpleBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    private long startTime = System.currentTimeMillis();
    
    public ReceiveTopo(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;
        if (startTime == -1)
            startTime = System.currentTimeMillis();

        String targetAgent = agent.comMgr.getTargetAgent();

        final MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.INFORM),
            MessageTemplate.and(
                MessageTemplate.MatchProtocol("SHARE-TOPOLOGY"),
                MessageTemplate.MatchSender(new AID(targetAgent, AID.ISLOCALNAME))
            )
        );

        ACLMessage msg;
        while ((msg = agent.receive(template)) != null) {
            try {
                
                TopologyMessage knowledge = (TopologyMessage) msg.getContentObject();
                SerializableSimpleGraph<String, MapAttribute> topology = knowledge.getTopology();
                boolean isExploFinished = knowledge.getExplorationComplete();
                int msgId = knowledge.getMsgId();

                // Mettre à jour la topologie et les observations de l'agent
                if (topology != null)
                    agent.topoMgr.merge(topology);

                // Mettre à jour les connaissances des autres agents
                agent.otherKnowMgr.updateTopology(targetAgent, topology);
                if (isExploFinished) {
                    agent.otherKnowMgr.markExplorationComplete(targetAgent);
                    agent.markExplorationComplete();
                }

                if (!agent.getExplorationComplete()) {
                    if (agent.getName().compareTo(targetAgent) < 0)
                        agent.moveMgr.setCurrentPathToFarthestOpenNode();
                    else
                        agent.moveMgr.setCurrentPathToClosestOpenNode();
                }

                // Envoyer un ACK en réponse
                ACLMessage ackMsg = new ACLMessage(ACLMessage.CONFIRM);
                ackMsg.setProtocol("SHARE-TOPOLOGY");
                ackMsg.setSender(agent.getAID());
                ackMsg.addReceiver(msg.getSender());
                ackMsg.setContent(((Integer) msgId).toString());
                agent.sendMessage(ackMsg);

                // Permet de passer au prochain step.
                exitCode = 1;
                break;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean done() {
        return (exitCode != -1) || (System.currentTimeMillis() - startTime > agent.getBehaviourTimeoutMills());
    }

    @Override 
    public int onEnd() {
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        startTime = -1;
        return exitCode;
    }
}