package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.IOException;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import dataStructures.tuple.Couple;
import eu.su.mas.dedaleEtu.mas.agents.GeneralAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.NodeObservations;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.msgObjects.DeadlockInfo;
import eu.su.mas.dedaleEtu.mas.msgObjects.TopologyObservations;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveRequestDeadlockBehaviour extends OneShotBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = 0;

    private GeneralAgent agent;
    
    public ReceiveRequestDeadlockBehaviour(final AbstractDedaleAgent myagent) {
        super(myagent);
        this.agent = (GeneralAgent) myagent;
    }

    @Override
    public void action() {
        Location myPosition = this.agent.getCurrentPosition();
        if (myPosition == null || myPosition.getLocationId().isEmpty())
            return;

        // Just added here to let you see what the agent is doing, otherwise he will be too quick.
        try {
            this.agent.doWait(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
          
        MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
            MessageTemplate.MatchProtocol("DEADLOCK")
        );

        List<String> neighborsNodes = new ArrayList<>();
        neighborsNodes.addAll(this.agent.getNeighborAgents().keySet());
        boolean firstClear = true;

        ACLMessage msg;
        while ((msg = this.agent.receive(template)) != null) {
            try {
                // int msgId = Integer.parseInt(msg.getContent());

                //String agentSender = msg.getSender().getLocalName();
                DeadlockInfo info = (DeadlockInfo) msg.getContentObject();

                // En attendant d'avoir une priorité, on se base sur le nom.
                if (this.agent.getPriority() < info.getPriority() && (this.agent.getCurrentPath().isEmpty() || firstClear)) {
                    List<String> excludedNodes = info.getMyPath();
                    excludedNodes.addAll(neighborsNodes);
                    this.agent.setCurrentPathForDeadlock(excludedNodes);
                    firstClear = false;
                }
            }

            catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (this.agent.getTargetNode() == null) {
            System.out.println(this.agent.getLocalName() + " - Still can't move !! Transfering priority...");
        } else {
            System.out.println(this.agent.getLocalName() + " - Going to node " + this.agent.getTargetNode());
        }
    }

    @Override 
    public int onEnd() {
        return exitCode;
    }
}