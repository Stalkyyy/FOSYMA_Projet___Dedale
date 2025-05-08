package eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.topology_share_behaviors;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.given_knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.managers.CommunicationManager.COMMUNICATION_STEP;
import eu.su.mas.dedaleEtu.mas.msgObjects.TopologyMessage;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveAckTopo extends SimpleBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    private long startTime = System.currentTimeMillis();
    
    public ReceiveAckTopo(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }
    
    // Gère la réception des messages d'accusé de réception pour le partage de topologie.
    @Override
    public void action() {
        
        // On réinitialise les attributs si besoin.
        exitCode = -1;
        if (startTime == -1)
            startTime = System.currentTimeMillis();

        // Récupère l'agent cible pour le partage de topologie.
        String targetAgent = agent.comMgr.getTargetAgent();

        // Définit le modèle de message à recevoir.
        final MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
            MessageTemplate.and(
                MessageTemplate.MatchProtocol("SHARE-TOPOLOGY"),
                MessageTemplate.MatchSender(new AID(targetAgent, AID.ISLOCALNAME))
            )
        );

        ACLMessage ackMsg;
        while ((ackMsg = agent.receive(template)) != null) {
            try {
                // Récupère l'identifiant du message.
                int msgId = Integer.parseInt(ackMsg.getContent());

                // Récupère l'objet du message contenant la topologie partagée.
                TopologyMessage msgObject = agent.comMgr.getTopologyMessage(msgId);    

                // Récupère la topologie et l'état d'exploration.
                SerializableSimpleGraph<String, MapAttribute> topo_sent = msgObject.getTopology();
                boolean isExploFinished = msgObject.getExplorationComplete();

                // Fusionne la topologie reçue avec celle de l'agent.
                agent.otherKnowMgr.mergeTopologyOf(targetAgent, topo_sent);
                agent.otherKnowMgr.resetLastUpdateAgent_topology(targetAgent);
                
                // Si l'exploration est terminée, marque l'agent cible comme ayant terminé.
                if (isExploFinished) {
                    agent.otherKnowMgr.markExplorationComplete(targetAgent);
                    return;
                }
                // Définit le chemin en fonction de l'ordre alphabétique des noms des agents.
                if (agent.getName().compareTo(targetAgent) < 0)
                    agent.moveMgr.setCurrentPathToFarthestOpenNode();
                else
                    agent.moveMgr.setCurrentPathToClosestOpenNode();

                // Permet de passer au prochain step.
                COMMUNICATION_STEP nextStep = agent.comMgr.getNextStep();
                exitCode = nextStep == null ? 0 : nextStep.getExitCode();
                agent.comMgr.removeStep(nextStep);
                break;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // Vérifie si le comportement est terminé.
    @Override
    public boolean done() {
        return (exitCode != -1) || (System.currentTimeMillis() - startTime > agent.getBehaviourTimeoutMills());
    }
    // Réinitialise les attributs et retourne le code de sortie.
    @Override 
    public int onEnd() {
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        startTime = -1;
        return exitCode;
    }
}