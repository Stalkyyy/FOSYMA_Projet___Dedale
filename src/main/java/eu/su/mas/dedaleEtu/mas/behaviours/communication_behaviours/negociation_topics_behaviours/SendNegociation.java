package eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.negociation_topics_behaviours;

import java.io.IOException;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentBehaviourState;
import eu.su.mas.dedaleEtu.mas.knowledge.FloodingState.FLOODING_STEP;
import eu.su.mas.dedaleEtu.mas.managers.CommunicationManager.COMMUNICATION_STEP;
import eu.su.mas.dedaleEtu.mas.msgObjects.CommunicationStepMessage;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SendNegociation extends OneShotBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    
    public SendNegociation(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    // Envoie un message de négociation à l'agent cible.
    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;        

        // Récupère l'agent cible pour la négociation.
        String targetAgent = agent.comMgr.getTargetAgent();

        // Prépare un message ACL pour initier la négociation.
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setProtocol("NEGOCIATING");
        msg.setSender(agent.getAID());
        msg.addReceiver(new AID(targetAgent, AID.ISLOCALNAME));

        // Crée un objet pour stocker les étapes de communication.
        CommunicationStepMessage msgObject = new CommunicationStepMessage();

        // Vérifie si le partage de topologie est possible avec l'agent cible.
        if (agent.otherKnowMgr.isTopologyShareable(targetAgent)) {
            agent.comMgr.addStep(COMMUNICATION_STEP.SHARE_TOPO);
            msgObject.addStep(COMMUNICATION_STEP.SHARE_TOPO);
        }


        // Vérifie si l'agent est en mode "flooding" et n'a pas encore contacté l'agent cible.
        if (agent.getBehaviourState() == AgentBehaviourState.FLOODING && agent.floodMgr.getStep() == FLOODING_STEP.WAITING_FOR_EVERYONE && !agent.floodMgr.hasContactedAgent(targetAgent) && !targetAgent.equals(agent.floodMgr.getParentAgent())) {
            agent.comMgr.addStep(COMMUNICATION_STEP.ENTRY_FLOOD_SENT);
            msgObject.addStep(COMMUNICATION_STEP.ENTRY_FLOOD_RECEIVED);
        }        


        // Vérifie si une négociation d'interblocage doit être initiée.
        if (agent.moveMgr.shouldInitiateDeadlock(agent.getTargetAgent(), agent.getTargetAgentNode()) && agent.getBehaviourState() != AgentBehaviourState.FLOODING) {
            agent.comMgr.addStep(COMMUNICATION_STEP.DEADLOCK);
            msgObject.addStep(COMMUNICATION_STEP.DEADLOCK);
        }



        try {
            // Ajoute l'objet des étapes de communication au contenu du message.
            msg.setContentObject(msgObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Envoie le message à l'agent cible.
        agent.sendMessage(msg);
    }
    // Reinitialise les attributs et retourne le code de sortie.
    @Override 
    public int onEnd() {
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);
        return exitCode;
    }
}