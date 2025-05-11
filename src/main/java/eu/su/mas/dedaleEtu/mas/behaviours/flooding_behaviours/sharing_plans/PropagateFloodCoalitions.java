package eu.su.mas.dedaleEtu.mas.behaviours.flooding_behaviours.sharing_plans;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.msgObjects.CoalitionsFloodMessage;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class PropagateFloodCoalitions extends OneShotBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;

    public PropagateFloodCoalitions(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    // Gère la propagation des coalitions dans le protocole de flooding.
    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;        

        // Définit le modèle de message à recevoir.
        final MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE),
            MessageTemplate.MatchProtocol("PLAN-FLOODING")
        );

        ACLMessage msg;
        while ((msg = agent.receive(template)) != null) {
            try {

                // Récupère l'objet contenant les coalitions propagées.
                CoalitionsFloodMessage msgObject = (CoalitionsFloodMessage) msg.getContentObject();
                
                // Met à jour les coalitions dans les connaissances de l'agent.
                agent.setCoalitions(msgObject.getCoalitions());

                // Si l'agent n'est pas une feuille, il propage les coalitions à ses enfants.
                if (!agent.floodMgr.isLeaf()) {
                    ACLMessage myChrMsg = new ACLMessage(ACLMessage.PROPAGATE);
                    myChrMsg.setProtocol("PLAN-FLOODING");
                    myChrMsg.setSender(agent.getAID());
                    for (String childName : agent.floodMgr.getChildrenAgents())
                        myChrMsg.addReceiver(new AID(childName, AID.ISLOCALNAME));
                    myChrMsg.setContentObject(msgObject);
                    agent.sendMessage(myChrMsg);    
                }

                exitCode = 1;
                break;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // Retourne le code de sortie. 
    @Override 
    public int onEnd() {
        // Affiche des informations de débogage si nécessaire.
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        return exitCode;
    }
}
