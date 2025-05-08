package eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.negociation_topics_behaviours;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentBehaviourState;
import eu.su.mas.dedaleEtu.mas.managers.CommunicationManager.COMMUNICATION_STEP;
import eu.su.mas.dedaleEtu.mas.msgObjects.CommunicationStepMessage;

import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveNegociation extends SimpleBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    private long startTime = -1;
    
    public ReceiveNegociation(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    // Gère la réception des messages de négociation.
    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;
        if (startTime == -1)
            startTime = System.currentTimeMillis();

        // Récupère l'agent cible pour la négociation.
        String targetAgent = agent.comMgr.getTargetAgent();

        // Définit le mode de message à recevoir.
        final MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.INFORM),
            MessageTemplate.and(
                MessageTemplate.MatchProtocol("NEGOCIATING"), 
                MessageTemplate.MatchSender(new AID(targetAgent, AID.ISLOCALNAME))
            )
        );
            
        ACLMessage msg;
        while ((msg = agent.receive(template)) != null) {
            try {
                // Récupère les étapes de communication envoyées par l'agent cible.
                CommunicationStepMessage steps = (CommunicationStepMessage)msg.getContentObject();

                // Ajoute les étapes reçues à la liste des étapes de communication.
                for (COMMUNICATION_STEP step : steps.getSteps()) {
                    agent.comMgr.addStep(step);                        
                }

                // Si une étape spécifique est reçue ou si l'agent est en mode "flooding", on retire une étape 
                if (steps.getSteps().contains(COMMUNICATION_STEP.ENTRY_FLOOD_RECEIVED) || agent.getBehaviourState() == AgentBehaviourState.FLOODING)
                    agent.comMgr.removeStep(COMMUNICATION_STEP.DEADLOCK);

                // On confirme le reçu.
                ACLMessage ackMsg = new ACLMessage(ACLMessage.CONFIRM);
                ackMsg.setProtocol("NEGOCIATING");
                ackMsg.setSender(agent.getAID());
                ackMsg.addReceiver(msg.getSender());
                agent.sendMessage(ackMsg);

                // Permet de passer au prochain step.
                exitCode = 1;
                break;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Vérifie si le comportement est terminé.
    @Override
    public boolean done() {
        return (exitCode != -1) || (System.currentTimeMillis() - startTime > agent.getBehaviourTimeoutMills());
    }

    // Reintialise les attributs et renvoie le code de sortie.
    @Override 
    public int onEnd() {
        if (exitCode == -1) {
            if (agent.comMgr.getLettingHimPass())
                exitCode = -2;
            else 
                exitCode = agent.getBehaviourState().getExitCode();
        }
        // Affiche des informations de debubgage si necessaire.
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        startTime = -1;
        return exitCode;
    }
}
