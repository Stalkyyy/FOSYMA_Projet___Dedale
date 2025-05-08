package eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.deadlock_communication_behaviours;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.NodeReservation;
import eu.su.mas.dedaleEtu.mas.msgObjects.DeadlockMessage;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SendDeadlockInfo extends OneShotBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    
    public SendDeadlockInfo(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }
    // Envoie un message d'interblocage à l'agent cible.
    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;  
        
        // Récupère l'agent cible pour la communication.
        String targetAgent = agent.comMgr.getTargetAgent();

        //Prépare le message ACL pour informer de l'interblocage.
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setProtocol("DEADLOCK");
        msg.setSender(agent.getAID());
        msg.addReceiver(new AID(targetAgent, AID.ISLOCALNAME));

        // On prépare l'objet du message.
        String currentNodeId = agent.getCurrentPosition().getLocationId();
        NodeReservation NR = agent.reserveMgr.createNodeReservation();
        DeadlockMessage msgObject = new DeadlockMessage(currentNodeId, NR);

        try {
            // On ajoute l'objet au message.
            msg.setContentObject(msgObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // On envoie le message à l'agent cible.
        agent.sendMessage(msg);
    }

    //Reinalise les attributs et retourne le code de sortie.
    @Override 
    public int onEnd() {
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        return exitCode;
    }
}