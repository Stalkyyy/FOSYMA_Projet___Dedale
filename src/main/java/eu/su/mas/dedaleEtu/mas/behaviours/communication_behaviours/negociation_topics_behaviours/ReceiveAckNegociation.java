package eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.negociation_topics_behaviours;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.managers.CommunicationManager.COMMUNICATION_STEP;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveAckNegociation extends SimpleBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    private long startTime = -1;
    
    public ReceiveAckNegociation(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }
    // Gère la réception des message d'accusé de réception de négociation.
    @Override
    public void action() {
        // On réinitialise les attributs si besoin.
        exitCode = -1;
        if (startTime == -1)
            startTime = System.currentTimeMillis();

        // Récupère l'agent cible pour la négociation.
        String targetAgent = agent.comMgr.getTargetAgent();

        // Définit le mode de message à recevoir.
        MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
            MessageTemplate.and(
                MessageTemplate.MatchProtocol("NEGOCIATING"),
                MessageTemplate.MatchSender(new AID(targetAgent, AID.ISLOCALNAME))
            )
        );
        
        // Boucle pour traiter les messages reçus.
        while (agent.receive(template) != null) {
            try {
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
    
    // Réinitialise les attributs et retourne le code de sortie.
    @Override 
    public int onEnd() {
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        startTime = -1;
        return exitCode;
    }
}
