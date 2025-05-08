package eu.su.mas.dedaleEtu.mas.behaviours.flooding_behaviours.entry_in_flood;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.FloodingState.FLOODING_STEP;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveNotifyEntry extends OneShotBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;

    public ReceiveNotifyEntry(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }
    // Gère la réception des notifications d'entrée dans le protocole de flooding.
    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;        

        // Définit le modèle de message à recevoir.
        final MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.INFORM),
            MessageTemplate.MatchProtocol("ENTRY-FLOODING")
        );

        ACLMessage msg;
        while ((msg = agent.receive(template)) != null) {
            try {
                // Récupère le nom de l'enfant et le nouvel agent ajouté.
                String childName = msg.getSender().getLocalName();
                String newAgentName = msg.getContent();

                // Ajoute le nouvel agent à l'arbre de flooding.
                agent.floodMgr.addAgentsInTree(newAgentName);

                // Pour éviter les problèmes de désynchronisation.
                if (!agent.floodMgr.isChildren(childName))
                    agent.floodMgr.addChildren(childName);
                
                agent.floodMgr.addAccessibleAgent(childName, newAgentName);

                // Si, pour l'agent root, tout le monde est là, alors on passe à la suite. Sinon, il attend.
                if (agent.floodMgr.isRoot()) {
                    if (!agent.floodMgr.isEveryoneInTree())
                        return;

                    // if (agent.floodMgr.isFirstFlooding())
                    //     agent.floodMgr.setStep(FLOODING_STEP.SHARING_CHARACTERISTICS);
                    // else
                    //     agent.floodMgr.setStep(FLOODING_STEP.SHARING_TREASURES);

                    agent.floodMgr.setStep(FLOODING_STEP.SHARING_CHARACTERISTICS);
                } 

                // Les autres vont notifier leur parent.
                else {
                    ACLMessage notifyMsg = new ACLMessage(ACLMessage.INFORM);
                    notifyMsg.setProtocol("ENTRY-FLOODING");
                    notifyMsg.setSender(agent.getAID());
                    notifyMsg.addReceiver(new AID(agent.floodMgr.getParentAgent(), AID.ISLOCALNAME));
                    notifyMsg.setContent(newAgentName);
                    agent.sendMessage(notifyMsg);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    // Retourne le code de sortie. 
    @Override 
    public int onEnd() {
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        return exitCode;
    }
}