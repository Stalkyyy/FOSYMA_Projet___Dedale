package eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.initiate_communication_behaviours;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentBehaviourState;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveCommunication extends SimpleBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    private long startTime = -1;
    
    public ReceiveCommunication(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    // Gère la réception des demandes de communication.
    @Override
    public void action() {
        
        // On réinitialise les attributs si besoin.
        exitCode = -1;
        if (startTime == -1)
            startTime = System.currentTimeMillis();

        // Définit le mode de message à recevoir.
        final MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
            MessageTemplate.MatchProtocol("COMMUNICATION")
        );
            
        ACLMessage msg;
        while ((msg = agent.receive(template)) != null) {
            try {
                // Récupère le nom de l'expéditeur et vérifie si l'agent est dans le voisinage.
                String senderName = msg.getSender().getLocalName();
                String nodeId = agent.visionMgr.isAgentNearby(senderName);

                if (nodeId == null)
                    continue;

                // On confirme la demande.
                ACLMessage ackMsg = new ACLMessage(ACLMessage.CONFIRM);
                ackMsg.setProtocol("COMMUNICATION");
                ackMsg.setSender(agent.getAID());
                ackMsg.addReceiver(msg.getSender());
                agent.sendMessage(ackMsg);

                // Permet de passer en mode communication.
                agent.comMgr.setTargetAgent(senderName, nodeId);
                exitCode = 1;
                break;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // On vérifie si le comportement est terminé.
    @Override
    public boolean done() {
        boolean isDone = false;

        isDone = (exitCode != -1) || (System.currentTimeMillis() - startTime > agent.getBehaviourTimeoutMills());

        return isDone;
    }
    // Réinitialise les attributs et retourne le code de sortie.
    @Override 
    public int onEnd() {
        if (exitCode == -1) {
            // Définit le code de sortie en fonction de l'état de l'agent.
            if (agent.getBehaviourState() == AgentBehaviourState.FLOODING)
                exitCode = agent.getBehaviourState().getExitCode();
            else if (agent.comMgr.getLettingHimPass())
                exitCode = -2;
            else 
                exitCode = agent.getBehaviourState().getExitCode();
        }

        // Affiche les informations de débogage si necessaire.
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        startTime = -1;
        return exitCode;
    }
}
