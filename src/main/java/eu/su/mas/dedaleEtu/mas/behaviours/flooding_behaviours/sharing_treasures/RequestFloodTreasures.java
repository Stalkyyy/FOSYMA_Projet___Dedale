package eu.su.mas.dedaleEtu.mas.behaviours.flooding_behaviours.sharing_treasures;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.FloodingState.FLOODING_STEP;
import eu.su.mas.dedaleEtu.mas.msgObjects.TreasureFloodMessage;
import eu.su.mas.dedaleEtu.mas.msgObjects.TreasureMessage;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class RequestFloodTreasures extends OneShotBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;

    public RequestFloodTreasures(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

        @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1; 

        // ====================================================================================

        if (agent.floodMgr.isRoot() && agent.floodMgr.getStep() != FLOODING_STEP.SHARING_TREASURES)
            return;
        
        // L'agent root demande aux autres leurs caractéristiques.
        if (agent.floodMgr.isRoot()) {
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.setProtocol("TREASURE-FLOODING");
            msg.setSender(agent.getAID());
    
            for(String childName : agent.floodMgr.getChildrenAgents()) {
                msg.addReceiver(new AID(childName, AID.ISLOCALNAME));
            }
    
            agent.sendMessage(msg);
            
            exitCode = 1;
            return;
        }

        // ====================================================================================

        final MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
            MessageTemplate.MatchProtocol("TREASURE-FLOODING")
        );

        while (agent.receive(template) != null) {
            try {

                // Si l'agent est une feuille de l'arbre, alors il commence le processus d'envoi de caractéristique.
                if (agent.floodMgr.isLeaf()) {

                    TreasureFloodMessage msgObject = new TreasureFloodMessage();
                    msgObject.addTreasure(agent.getLocalName(), new TreasureMessage(agent.getMyTreasures()));

                    ACLMessage myChrMsg = new ACLMessage(ACLMessage.AGREE);
                    myChrMsg.setProtocol("TREASURE-FLOODING");
                    myChrMsg.setSender(agent.getAID());
                    myChrMsg.addReceiver(new AID(agent.floodMgr.getParentAgent(), AID.ISLOCALNAME));
                    myChrMsg.setContentObject(msgObject);
                    agent.sendMessage(myChrMsg);

                    exitCode = 2; // Il passe directement en attente de propagation.
                }

                // Sinon, on propage la requête.
                else {
                    ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
                    requestMsg.setProtocol("TREASURE-FLOODING");
                    requestMsg.setSender(agent.getAID());
            
                    for(String childName : agent.floodMgr.getChildrenAgents()) {
                        requestMsg.addReceiver(new AID(childName, AID.ISLOCALNAME));
                    }
            
                    agent.sendMessage(requestMsg);

                    exitCode = 1;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override 
    public int onEnd() {
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        return exitCode;
    }
}
