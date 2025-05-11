package eu.su.mas.dedaleEtu.mas.behaviours.flooding_behaviours.sharing_plans;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.msgObjects.CoalitionsFloodMessage;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SendFloodCoalitions extends OneShotBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;

    public SendFloodCoalitions(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }
    // Gère l'envoi des coalitions calculées par l'agent root aux autres agents.
    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1; 

        // Si l'agent n'est pas root, il ne fait rien.
        if (!agent.floodMgr.isRoot())
            return;

        // On calcule les meilleures coalitions d'après l'agent root.
        agent.coalitionMgr.calculateBestCoalitions();
        CoalitionsFloodMessage msgObject = new CoalitionsFloodMessage(agent.getCoalitions());

        System.out.println(agent.getCoalitions());

        // On envoie les coalitions aux autres.
        ACLMessage msg = new ACLMessage(ACLMessage.PROPAGATE);
        msg.setProtocol("PLAN-FLOODING");
        msg.setSender(agent.getAID());
        for(String childName : agent.floodMgr.getChildrenAgents()) {
            msg.addReceiver(new AID(childName, AID.ISLOCALNAME));
        }

        try {
            // Ajoute l'objet des coalitions au contenu du message et l'envoie.
            msg.setContentObject(msgObject);
            agent.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        exitCode = 1;
    }

    // Retourne le code de sortie.
    @Override 
    public int onEnd() {
         // Affiche des informations de debug si nécessaire.
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        return exitCode;
    }
}
