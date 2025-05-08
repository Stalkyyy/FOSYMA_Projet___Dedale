package eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.deadlock_communication_behaviours;

import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.NodeReservation;
import eu.su.mas.dedaleEtu.mas.msgObjects.DeadlockMessage;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveDeadlockInfo extends SimpleBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    private long startTime = -1;
    
    public ReceiveDeadlockInfo(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    // Gère la réception des messages d'interblocage.
    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;
        if (startTime == -1)
            startTime = System.currentTimeMillis();

        // Récupère l'agent cible pour la communication.
        String targetAgent = agent.comMgr.getTargetAgent();

        // Définit le modède de message à recevoir.
        final MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.INFORM),
            MessageTemplate.and(
                MessageTemplate.MatchProtocol("DEADLOCK"), 
                MessageTemplate.MatchSender(new AID(targetAgent, AID.ISLOCALNAME))
            )
        );
            
        ACLMessage msg;
        while ((msg = agent.receive(template)) != null) {
            try {
                // Récupère le message d'interblocage et l''ajoute à la liste des messages temporaires.
                DeadlockMessage DM = (DeadlockMessage)msg.getContentObject();
                agent.comMgr.addTemporaryDeadlockMessage(DM);

                // Récupère la réservation de noeud de l'agent cible.
                NodeReservation NR = DM.getNodeReservation();
                boolean hasPriority = agent.reserveMgr.hasPriorityOver(NR);

                Couple<String, List<String>> solution = agent.topoMgr.getPathToClosestFreeNodeExcluding(agent.getCurrentPosition().getLocationId(), NR.getReservedNodes(), DM.getCurrentNodeId());

                ACLMessage ackMsg;
                if (hasPriority) {
                    // Propose une solution à l'agent cible.
                    ackMsg = new ACLMessage(ACLMessage.PROPOSE);
                }
                else if (!hasPriority && solution != null) {
                    // Il accepte si une solution est trouvé et qu'il n'a pas la priorité.
                    agent.reserveMgr.mergeNodeReservation(NR);
                    agent.setDeadlockNodeSolution(solution.getLeft());
                    agent.moveMgr.setCurrentPathTo(solution.getRight());

                    agent.comMgr.setLettingHimPass(true);
                    ackMsg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                }
                else {               
                    // Sinon, il refuse la proposition.
                    String nodeSolution = agent.topoMgr.findIntersectionAndAdjacentNode(agent.getCurrentPosition().getLocationId());

                    NodeReservation new_NR = agent.reserveMgr.createNodeReservation();
                    agent.moveMgr.setCurrentPathTo(nodeSolution);
                    agent.setDeadlockNodeSolution(nodeSolution);
                    new_NR.setAgentName(NR.getAgentName());
                    new_NR.setState(NR.getState());
                    new_NR.setHasAccessibleNodes(false);

                    DM = new DeadlockMessage(agent.getCurrentPosition().getLocationId(), NR);
                    ackMsg = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
                    ackMsg.setContentObject(DM);
                }

                // Envoie le message d'accusé de réception à l'agent cible.
                ackMsg.setProtocol("DEADLOCK");
                ackMsg.setSender(agent.getAID());
                ackMsg.addReceiver(new AID(targetAgent, AID.ISLOCALNAME));    
                agent.sendMessage(ackMsg);

                // Permet de passer au prochain step.
                exitCode = 1;
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

    // Reinitialise les attributs et retourne le code de sortie.
    @Override 
    public int onEnd() {
        if (exitCode == -1) {
            if (agent.comMgr.getLettingHimPass())
                exitCode = -2;
            else 
                exitCode = agent.getBehaviourState().getExitCode();
        }

        
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        startTime = -1;
        return exitCode;
    }
}

