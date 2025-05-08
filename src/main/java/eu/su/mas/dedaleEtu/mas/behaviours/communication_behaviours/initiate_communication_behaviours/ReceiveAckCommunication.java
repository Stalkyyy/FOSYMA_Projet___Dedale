package eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.initiate_communication_behaviours;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveAckCommunication extends SimpleBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    private long startTime = -1;
    
    public ReceiveAckCommunication(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    // Gère la réception des messages d'acquitement pour initier une communication.
    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;
        if (startTime == -1)
            startTime = System.currentTimeMillis();

        //Définit le modède de message à recevoir.
        MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
            MessageTemplate.MatchProtocol("COMMUNICATION")
        );

        ACLMessage ackMsg;
        while ((ackMsg = agent.receive(template)) != null) {
            try {
                // Récupère le nom de l'expiditeur et vérifie si l'agent est dans le voisinage.
                String senderName = ackMsg.getSender().getLocalName();
                String nodeId = agent.visionMgr.isAgentNearby(senderName);

                if (nodeId == null)
                    continue;

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
        return (exitCode != -1) || (System.currentTimeMillis() - startTime > agent.getBehaviourTimeoutMills());
    }

    // Réinitialise les attributs et retourne le code de sortie.
    @Override 
    public int onEnd() {
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        startTime = -1;
        return exitCode;
    }
}
