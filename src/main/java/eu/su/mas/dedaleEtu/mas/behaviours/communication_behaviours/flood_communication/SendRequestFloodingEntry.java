package eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.flood_communication;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SendRequestFloodingEntry extends OneShotBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    
    public SendRequestFloodingEntry(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }
    // Envoie une demande d'entrée dans le protocole de flooding à un agent cible.
    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;        

        // Récupère l'agent cible pour la communication.
        String targetAgent = agent.comMgr.getTargetAgent();

        // Prépare un message ACL pour initier la demande d'entrée dans le flooding.
        ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
        msg.setProtocol("ENTRY-FLOODING");
        msg.setSender(agent.getAID());
        msg.addReceiver(new AID(targetAgent, AID.ISLOCALNAME));

        // Envoie le message à l'agent cible.
        agent.sendMessage(msg);
    }

    // Retourne le code de sortie.
    @Override 
    public int onEnd() {
        // Affiche des informations de débug si necessaire.
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);
        return exitCode;
    }
}