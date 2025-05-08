package eu.su.mas.dedaleEtu.mas.behaviours.flooding_behaviours.sharing_treasures;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.FloodingState.FLOODING_STEP;
import eu.su.mas.dedaleEtu.mas.msgObjects.TreasureMessage;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class PropagateFloodTreasures extends OneShotBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;

    public PropagateFloodTreasures(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    // Gère la propagation des informations sur les trésors dans le protocole de flooding.
    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;        

        // Définit le modèle de message à recevoir.
        final MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE),
            MessageTemplate.MatchProtocol("TREASURE-FLOODING")
        );

        ACLMessage msg;
        while ((msg = agent.receive(template)) != null) {
            try {

                // Récupère l'objet contenant les informations sur les trésors propagées.
                TreasureMessage msgObject = (TreasureMessage) msg.getContentObject();

                // Met à jour les informations sur les trésors dans les connaissances de l'agent.
                agent.treasureMgr.merge(msgObject.getTreasures());

                // Si l'agent n'est pas une feuille, il propage les informations sur les trésors à ses enfants.
                if (!agent.floodMgr.isLeaf()) {
                    ACLMessage myChrMsg = new ACLMessage(ACLMessage.PROPAGATE);
                    myChrMsg.setProtocol("TREASURE-FLOODING");
                    myChrMsg.setSender(agent.getAID());
                    for (String childName : agent.floodMgr.getChildrenAgents())
                        myChrMsg.addReceiver(new AID(childName, AID.ISLOCALNAME));
                    myChrMsg.setContentObject(msgObject);
                    agent.sendMessage(myChrMsg);    
                }

                // Passe à l'étape suivante du flooding.
                agent.floodMgr.setStep(FLOODING_STEP.SHARING_PLANS);

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
        // Affiche des informations de debug si nécessaire.
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        return exitCode;
    }
}
