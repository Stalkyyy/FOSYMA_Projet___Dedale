package eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.initiate_communication_behaviours;

import java.util.Map;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentType;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SendCommunication extends OneShotBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    
    public SendCommunication(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    // Envoie une demande de communication aux agents à proximité.
    @Override
    public void action() {
        
        // On réinitialise les attributs si besoin.
        exitCode = -1;   
         
        // Prépare le message ACL pour informer de la communication.
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setProtocol("COMMUNICATION");
        msg.setSender(agent.getAID());

        // Récupère les agents à proximité
        Map<String, String> agentsNearby = agent.visionMgr.getAgentsNearby();
        
        for (Map.Entry<String, String> entry : agentsNearby.entrySet()) {
            String locationId = entry.getKey();
            String agentName = entry.getValue();



            // C'est un Wumpus.
            if (!agent.getListAgentNames().contains(agentName)) {
                // Si trop de tentatives échouées, on change de chemin aléatoirement.
                if (agent.moveMgr.getFailedMoveCount() > agent.moveMgr.maxFailedMoveCount) {
                    agent.moveMgr.setCurrentPathToRandomNode();
                    return;
                }
                
                continue;
            }

            // Si l'agent qu'on croise est un Silo, on tente de lui donner les ressources que l'on a.
            if (agent.freeSpace() < agent.getMyBackPackTotalSpace() && agent.otherKnowMgr.getAgentType(agentName) == AgentType.TANKER) {
                agent.emptyMyBackPack(agentName);
            }
            // Verifie si une communication est possible avec l'agent.
            if (!agent.otherKnowMgr.shouldInitiateCommunication(agentName, locationId))
                continue;
        
            // Configure le message pour l'agent cible.
            msg.clearAllReceiver();
            msg.addReceiver(new AID(agentName, AID.ISLOCALNAME));

            // On envoie le message.
            agent.sendMessage(msg);

            exitCode = 1;
            break;
        }
    }
    // Reinalise les attributs et retourne le code de sortie.
    @Override 
    public int onEnd() {
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        return exitCode;
    }
}
