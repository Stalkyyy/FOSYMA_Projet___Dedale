package eu.su.mas.dedaleEtu.mas.behaviours;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.GeneralAgent;
import eu.su.mas.dedaleEtu.mas.msgObjects.TopologyObservations;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveAckMapObsBehaviour extends OneShotBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = 0;

    private GeneralAgent agent;
    
    public ReceiveAckMapObsBehaviour(final AbstractDedaleAgent myagent) {
        super(myagent);
        this.agent = (GeneralAgent) myagent;
    }

    @Override
    public void action() {
        // Just added here to let you see what the agent is doing, otherwise he will be too quick.
        try {
            this.agent.doWait(500);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
            MessageTemplate.MatchProtocol("ACK")
        );

        ACLMessage ackMsg;
        while ((ackMsg = this.agent.receive(template)) != null) {
            try {
                int msgId = Integer.parseInt(ackMsg.getContent());

                TopologyObservations topo_obs = this.agent.getHist_TopologyObservations(msgId);                    

                String receiverName = topo_obs.getReceiverName();
                this.agent.getOtherAgentsTopology().mergeTopology(receiverName, topo_obs.getTopology());
                this.agent.getOtherAgentsObservations().mergeObservation(receiverName, topo_obs.getObservations());
                this.agent.getOtherAgentsTopology().resetLastUpdatesAgent(receiverName);

                if (topo_obs.getFinishedExplo()) {
                    this.agent.getOtherAgentsTopology().agentFinishedExplo(receiverName);
                }
            }

            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override 
    public int onEnd() {
        return this.agent.getExploFinished() ? 1 : 0;
    }
}