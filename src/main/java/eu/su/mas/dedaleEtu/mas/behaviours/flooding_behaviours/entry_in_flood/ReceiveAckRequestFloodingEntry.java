package eu.su.mas.dedaleEtu.mas.behaviours.flooding_behaviours.entry_in_flood;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.FloodingState.FLOODING_STEP;
import eu.su.mas.dedaleEtu.mas.managers.CommunicationManager.COMMUNICATION_STEP;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveAckRequestFloodingEntry extends SimpleBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    private long startTime = System.currentTimeMillis();
    
    public ReceiveAckRequestFloodingEntry(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }
    // Gère la réception des accusés de réception pour les demandes d'entrée dans le protocole de flooding.
    @Override
    public void action() {
        
        // On réinitialise les attributs si besoin.
        exitCode = -1;
        if (startTime == -1)
            startTime = System.currentTimeMillis();

        // Récupère l'agent cible pour la communication.
        String targetAgent = agent.comMgr.getTargetAgent();

        // Définit le modèle de message à recevoir.
        final MessageTemplate template = MessageTemplate.and(
            MessageTemplate.or(
                MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL),
                MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL)
            ),
            MessageTemplate.and(
                MessageTemplate.MatchProtocol("ENTRY-FLOODING"),
                MessageTemplate.MatchSender(new AID(targetAgent, AID.ISLOCALNAME))
            )
        );

        ACLMessage ackMsg;
        while ((ackMsg = agent.receive(template)) != null) {
            try {
                // Ajoute l'agent cible à la liste des agents contactés.
                agent.floodMgr.addContacted(targetAgent);

                if (ackMsg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                     // Ajoute l'agent cible comme enfant et dans l'arbre de flooding.
                    agent.floodMgr.addChildren(targetAgent);
                    agent.floodMgr.addAgentsInTree(targetAgent);

                    // Si l'agent est le root, alors on vérifie s'il nous manque quelqu'un ou non.
                    if (agent.floodMgr.isRoot()) {
                        if (!agent.floodMgr.isEveryoneInTree())
                            return;
                        
                        // Définit l'étape suivante en fonction du premier flooding ou non.
                        if (agent.floodMgr.isFirstFlooding())
                            agent.floodMgr.setStep(FLOODING_STEP.SHARING_CHARACTERISTICS);
                        else
                            agent.floodMgr.setStep(FLOODING_STEP.SHARING_TREASURES);
                    }

                    // On va notifier le root de l'addition du nouveau agent.
                    else {
                        ACLMessage notifyMsg = new ACLMessage(ACLMessage.INFORM);
                        notifyMsg.setProtocol("ENTRY-FLOODING");
                        notifyMsg.setSender(agent.getAID());
                        notifyMsg.addReceiver(new AID(agent.floodMgr.getParentAgent(), AID.ISLOCALNAME));
                        notifyMsg.setContent(targetAgent);
                        agent.sendMessage(notifyMsg);
                    }
                }

                // Permet de passer au prochain step.
                COMMUNICATION_STEP nextStep = agent.comMgr.getNextStep();
                exitCode = nextStep == null ? 0 : nextStep.getExitCode();
                agent.comMgr.removeStep(nextStep);
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
    // Réinitialise les attributs et renvoie le code de sortie.
    @Override 
    public int onEnd() {
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        startTime = -1;
        return exitCode;
    }
}
