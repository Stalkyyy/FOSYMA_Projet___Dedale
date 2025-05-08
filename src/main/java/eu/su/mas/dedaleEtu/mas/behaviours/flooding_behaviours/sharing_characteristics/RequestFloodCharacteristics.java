package eu.su.mas.dedaleEtu.mas.behaviours.flooding_behaviours.sharing_characteristics;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.FloodingState.FLOODING_STEP;
import eu.su.mas.dedaleEtu.mas.msgObjects.CharacteristicsFloodMessage;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class RequestFloodCharacteristics extends OneShotBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;

    public RequestFloodCharacteristics(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }
    // Gère la demande et la propagation de caractéristiques dans le protocole de flooding.
    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1; 

        // ====================================================================================
        // Si l'agent root n'est pas dans l'étape de partage des caractéristiques, il ne fait rien.
        if (agent.floodMgr.isRoot() && agent.floodMgr.getStep() != FLOODING_STEP.SHARING_CHARACTERISTICS)
            return;
        
        // L'agent root demande aux autres leurs caractéristiques.
        if (agent.floodMgr.isRoot()) {
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.setProtocol("CHR-FLOODING");
            msg.setSender(agent.getAID());
            
            // Ajoute les enfants comme destinataires du message.
            for(String childName : agent.floodMgr.getChildrenAgents()) {
                msg.addReceiver(new AID(childName, AID.ISLOCALNAME));
            }
            
            // Envoie le message.
            agent.sendMessage(msg);
            
            exitCode = 1;
            return;
        }

        // ====================================================================================
        // Si l'agent n'est pas root, il attend une requête de caractéristiques.
        final MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
            MessageTemplate.MatchProtocol("CHR-FLOODING")
        );

        while (agent.receive(template) != null) {
            try {

                // Si l'agent est une feuille de l'arbre, alors il commence le processus d'envoi de caractéristique.
                if (agent.floodMgr.isLeaf()) {

                    CharacteristicsFloodMessage msgObject = new CharacteristicsFloodMessage();
                    msgObject.addCharacteristics(agent.getLocalName(), agent.getAgentType(), agent.getMyTreasureType(), agent.getMyBackPackTotalSpace(), agent.getMyLockpickLevel(), agent.getMyStrengthLevel());

                    ACLMessage myChrMsg = new ACLMessage(ACLMessage.AGREE);
                    myChrMsg.setProtocol("CHR-FLOODING");
                    myChrMsg.setSender(agent.getAID());
                    myChrMsg.addReceiver(new AID(agent.floodMgr.getParentAgent(), AID.ISLOCALNAME));
                    myChrMsg.setContentObject(msgObject);
                    agent.sendMessage(myChrMsg);

                    exitCode = 2; // Il passe directement en attente de propagation.
                }

                // Sinon, on propage la requête.
                else {
                    ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
                    requestMsg.setProtocol("CHR-FLOODING");
                    requestMsg.setSender(agent.getAID());
            
                    for(String childName : agent.floodMgr.getChildrenAgents()) {
                        requestMsg.addReceiver(new AID(childName, AID.ISLOCALNAME));
                    }
                    
                    // Envoi le message
                    agent.sendMessage(requestMsg);

                    exitCode = 1;
                }

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
