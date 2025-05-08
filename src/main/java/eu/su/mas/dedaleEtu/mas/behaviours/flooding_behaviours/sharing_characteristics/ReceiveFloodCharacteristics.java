package eu.su.mas.dedaleEtu.mas.behaviours.flooding_behaviours.sharing_characteristics;

import java.util.HashMap;
import java.util.Map;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.FloodingState.FLOODING_STEP;
import eu.su.mas.dedaleEtu.mas.msgObjects.CharacteristicsFloodMessage;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveFloodCharacteristics extends OneShotBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private Map<String, CharacteristicsFloodMessage> hasEveryChild;

    private AbstractAgent agent;

    public ReceiveFloodCharacteristics(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    // Gère la réception des caractéristiques dans le protocole de flooding.
    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;

        // Initialise la structure pour suivre les caractéristiques reçues de chaque enfant.
        if (hasEveryChild == null) {
            this.hasEveryChild = new HashMap<>();
            for (String childName : agent.floodMgr.getChildrenAgents())
                this.hasEveryChild.put(childName, null);    
        }

        // Définit le modèle de message à recevoir.
        final MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.AGREE),
            MessageTemplate.MatchProtocol("CHR-FLOODING")
        );
        

        ACLMessage msg;
        while ((msg = agent.receive(template)) != null) {
            try {
                // Récupère le nom de l'enfant et les caractéristiques reçues.
                String child = msg.getSender().getLocalName();
                CharacteristicsFloodMessage msgObject = (CharacteristicsFloodMessage) msg.getContentObject();
                hasEveryChild.put(child, msgObject);

                // Si on a pas reçu ceux de tous nos enfants, on boucle.
                if (hasEveryChild.values().stream().anyMatch(value -> value == null))
                    return;

                // A la fin, on envoie l'addition de tout + nos caractéristiques.
                CharacteristicsFloodMessage finishedObject = new CharacteristicsFloodMessage();
                for (CharacteristicsFloodMessage CFM : hasEveryChild.values())
                    finishedObject.addCharacteristics(CFM);
                finishedObject.addCharacteristics(agent.getLocalName(), agent.getAgentType(), agent.getMyTreasureType(), agent.getMyBackPackTotalSpace(), agent.getMyLockpickLevel(), agent.getMyStrengthLevel());


                // ====================================================================================

                // On incorpore toutes ces connaissances et on les renvoie.
                if (agent.floodMgr.isRoot()) {
                    agent.otherKnowMgr.updateCharacteristics(finishedObject);

                    ACLMessage myChrMsg = new ACLMessage(ACLMessage.PROPAGATE);
                    myChrMsg.setProtocol("CHR-FLOODING");
                    myChrMsg.setSender(agent.getAID());
                    for (String childName : agent.floodMgr.getChildrenAgents())
                        myChrMsg.addReceiver(new AID(childName, AID.ISLOCALNAME));
                    myChrMsg.setContentObject(msgObject);
                    agent.sendMessage(myChrMsg);

                    // Passe à l'étape suivante du flooding.
                    agent.floodMgr.setStep(FLOODING_STEP.SHARING_TREASURES);
                    exitCode = 2; // Il saute l'étape d'attendre la propagation.
                    return;
                }

                // ====================================================================================


                // Sinon, on remonte l'objet.
                else {
                    ACLMessage myChrMsg = new ACLMessage(ACLMessage.AGREE);
                    myChrMsg.setProtocol("CHR-FLOODING");
                    myChrMsg.setSender(agent.getAID());
                    myChrMsg.addReceiver(new AID(agent.floodMgr.getParentAgent(), AID.ISLOCALNAME));
                    myChrMsg.setContentObject(finishedObject);
                    agent.sendMessage(myChrMsg);
                }

                exitCode = 1;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
