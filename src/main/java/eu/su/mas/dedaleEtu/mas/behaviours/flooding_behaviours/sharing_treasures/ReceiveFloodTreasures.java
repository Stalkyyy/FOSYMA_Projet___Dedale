package eu.su.mas.dedaleEtu.mas.behaviours.flooding_behaviours.sharing_treasures;

import java.util.HashMap;
import java.util.Map;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.FloodingState.FLOODING_STEP;
import eu.su.mas.dedaleEtu.mas.msgObjects.TreasureFloodMessage;
import eu.su.mas.dedaleEtu.mas.msgObjects.TreasureMessage;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveFloodTreasures extends OneShotBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private Map<String, TreasureFloodMessage> hasEveryChild;

    private AbstractAgent agent;

    public ReceiveFloodTreasures(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

        @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;

        if (hasEveryChild == null) {
            this.hasEveryChild = new HashMap<>();
            for (String childName : agent.floodMgr.getChildrenAgents())
                this.hasEveryChild.put(childName, null);    
        }

        final MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.AGREE),
            MessageTemplate.MatchProtocol("TREASURE-FLOODING")
        );

        ACLMessage msg;
        while ((msg = agent.receive(template)) != null) {
            try {

                String child = msg.getSender().getLocalName();
                TreasureFloodMessage msgObject = (TreasureFloodMessage) msg.getContentObject();
                hasEveryChild.put(child, msgObject);

                // Si on a pas reçu ceux de tous nos enfants, on boucle.
                if (hasEveryChild.values().stream().anyMatch(value -> value == null))
                    return;

                // A la fin, on envoie l'addition de tout + nos trésors.
                TreasureFloodMessage finishedObject = new TreasureFloodMessage();
                for (TreasureFloodMessage TFM : hasEveryChild.values())
                    finishedObject.addTreasure(TFM);
                finishedObject.addTreasure(agent.getLocalName(), new TreasureMessage(agent.getMyTreasures()));


                // ====================================================================================

                // On incorpore toutes ces trésors et on les renvoie.
                if (agent.floodMgr.isRoot()) {
                    agent.treasureMgr.merge(finishedObject);

                    TreasureMessage myTreasures = new TreasureMessage(agent.getMyTreasures()); // Pas besoin d'envoyer toutes les autres observations, on a déjà ceux qu'on garde.
                    ACLMessage myChrMsg = new ACLMessage(ACLMessage.PROPAGATE);
                    myChrMsg.setProtocol("TREASURE-FLOODING");
                    myChrMsg.setSender(agent.getAID());
                    for (String childName : agent.floodMgr.getChildrenAgents())
                        myChrMsg.addReceiver(new AID(childName, AID.ISLOCALNAME));
                    myChrMsg.setContentObject(myTreasures);
                    agent.sendMessage(myChrMsg);

                    agent.floodMgr.setStep(FLOODING_STEP.SHARING_TREASURES);
                    exitCode = 2; // Il saute l'étape d'attendre la propagation.

                    return;
                }

                // ====================================================================================


                // Sinon, on remonte l'objet.
                else {
                    ACLMessage myChrMsg = new ACLMessage(ACLMessage.AGREE);
                    myChrMsg.setProtocol("TREASURE-FLOODING");
                    myChrMsg.setSender(agent.getAID());
                    myChrMsg.addReceiver(new AID(agent.floodMgr.getParentAgent(), AID.ISLOCALNAME));
                    myChrMsg.setContentObject(msgObject);
                    agent.sendMessage(myChrMsg);
                }

                exitCode = 1;

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
