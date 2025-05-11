package eu.su.mas.dedaleEtu.mas.behaviours.flooding_behaviours.entry_in_flood;

import java.io.IOException;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.FloodingState.FLOODING_STEP;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class PropagateEveryoneIsHere extends OneShotBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;

    public PropagateEveryoneIsHere(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    // Gère la propagation de l'information "tout le monde est là" dans le protocole de flooding.
    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;      
        
        // ====================================================================================
        
        // L'agent root dit aux autres qu'il y a tout le monde, et dans quel mode on passe.
        if (agent.floodMgr.isRoot() && agent.floodMgr.getStep() != FLOODING_STEP.WAITING_FOR_EVERYONE) {
            ACLMessage msg = new ACLMessage(ACLMessage.PROPAGATE);
            msg.setProtocol("ENTRY-FLOODING");
            msg.setSender(agent.getAID());
            
            // Ajoute les enfants comme destinataires du message.
            for(String childName : agent.floodMgr.getChildrenAgents()) {
                msg.addReceiver(new AID(childName, AID.ISLOCALNAME));
            }
    
            try {
                // Ajoute l'étape actuelle du flooding au contenu du message.
                msg.setContentObject(agent.floodMgr.getStep());
                agent.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Définit le code de sortie en fonction de l'étape actuelle.
            exitCode = agent.floodMgr.getStep().getExitCode();
            return;
        }

        else if (agent.floodMgr.isRoot())
            return;


        // ====================================================================================
        // Si l'agent n'est pas root, il attend un message de propagation.

        final MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE),
            MessageTemplate.MatchProtocol("ENTRY-FLOODING")
        );

        ACLMessage msg;
        while ((msg = agent.receive(template)) != null) {
            try {
                // Récupère l'étape de flooding envoyée par le parent.
                FLOODING_STEP step = (FLOODING_STEP) msg.getContentObject();

                agent.floodMgr.setStep(step);

                // Prépare un message pour propager l'information aux enfants.
                ACLMessage propagateMsg = new ACLMessage(ACLMessage.PROPAGATE);
                propagateMsg.setProtocol("ENTRY-FLOODING");
                propagateMsg.setSender(agent.getAID());
                
                // Ajoute les enfants comme destinataires du message.
                for(String childName : agent.floodMgr.getChildrenAgents()) {
                    propagateMsg.addReceiver(new AID(childName, AID.ISLOCALNAME));
                }
                
                // Ajoute l'étape de flooding au contenu du message.
                propagateMsg.setContentObject(step);
                agent.sendMessage(propagateMsg);
                
                // Définit le code de sortie en fonction de l'étape reçue.
                exitCode = step.getExitCode();
                break;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // Retourne le code de sortie.
    @Override 
    public int onEnd() {
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        return exitCode;
    }
}
