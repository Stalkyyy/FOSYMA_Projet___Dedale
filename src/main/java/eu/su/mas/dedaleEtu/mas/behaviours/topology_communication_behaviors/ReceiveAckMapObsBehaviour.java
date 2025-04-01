package eu.su.mas.dedaleEtu.mas.behaviours.topology_communication_behaviors;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedaleEtu.mas.agents.MyAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.NodeObservations;
import eu.su.mas.dedaleEtu.mas.managers.CommunicationManager.COMMUNICATION_STEP;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.msgObjects.TopologyMessage;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveAckMapObsBehaviour extends SimpleBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private MyAgent agent;
    private long startTime = System.currentTimeMillis();
    
    public ReceiveAckMapObsBehaviour(final MyAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    @Override
    public void action() {
        if (startTime == -1)
            startTime = System.currentTimeMillis();

        String targetAgent = agent.comMgr.getTargetAgent();

        final MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
            MessageTemplate.and(
                MessageTemplate.MatchProtocol("SHARE-TOPOLOGY"),
                MessageTemplate.MatchSender(new AID(targetAgent, AID.ISLOCALNAME))
            )
        );

        ACLMessage ackMsg;
        while ((ackMsg = agent.receive(template)) != null) {
            try {
                int msgId = Integer.parseInt(ackMsg.getContent());

                TopologyMessage msgObject = agent.comMgr.getTopologyMessage(msgId);    

                SerializableSimpleGraph<String, MapAttribute> topo_sent = msgObject.getTopology();
                NodeObservations obs_sent = msgObject.getObservations();
                boolean isExploFinished = msgObject.getExplorationComplete();

                agent.otherKnowMgr.mergeTopologyOf(targetAgent, topo_sent);
                agent.otherKnowMgr.mergeObservationOf(targetAgent, obs_sent);
                agent.otherKnowMgr.resetLastUpdateAgent(targetAgent);

                if (isExploFinished) {
                    agent.otherKnowMgr.markExplorationComplete(targetAgent);
                    return;
                }

                if (agent.getName().compareTo(targetAgent) < 0)
                    agent.moveMgr.setCurrentPathToFarthestOpenNode();
                else
                    agent.moveMgr.setCurrentPathToClosestOpenNode();

                // Permet de passer au prochain step.
                COMMUNICATION_STEP nextStep = agent.comMgr.getStep();
                exitCode = nextStep == null ? 0 : nextStep.getExitCode();
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
        startTime = -1;
        return exitCode;
    }
}