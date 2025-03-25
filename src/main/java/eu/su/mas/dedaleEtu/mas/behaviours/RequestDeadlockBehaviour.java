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

public class RequestDeadlockBehaviour extends OneShotBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = 0;

    private GeneralAgent agent;
    
    public RequestDeadlockBehaviour(final AbstractDedaleAgent myagent) {
        super(myagent);
        this.agent = (GeneralAgent) myagent;
    }

    @Override
    public void action() {
        Location myPosition = this.agent.getCurrentPosition();
        if (myPosition == null || myPosition.getLocationId().isEmpty())
            return;
          
        Map<String, String> neighbors = this.agent.getNeighborAgents();
        if (!neighbors.containsKey(myPosition.getLocationId())) {
            return;
        }

        // On construit le message.
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setProtocol("DEADLOCK");
        msg.setSender(this.agent.getAID());
        msg.addReceiver(new AID(neighbors.get(myPosition.getLocationId()), AID.ISLOCALNAME));

        List<String> myPath = new ArrayList<>(this.agent.getCurrentPath());
        myPath.add(0, this.agent.getTargetNode());
        if (!this.agent.canMove()) {
            this.agent.setPriority(1);
        } else {
            this.agent.setPriority(0);
        }

        DeadlockInfo deadlockInfo = new DeadlockInfo(myPath, this.agent.getPriority());

        try {
            msg.setContentObject(deadlockInfo);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Générer un ID unique pour le message
        int messageId = this.agent.generateMessageId();
        msg.setConversationId(String.valueOf(messageId));

        this.agent.sendMessage(msg);
    }

    @Override 
    public int onEnd() {
        return exitCode;
    }
}