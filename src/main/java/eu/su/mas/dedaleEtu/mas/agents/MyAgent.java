package eu.su.mas.dedaleEtu.mas.agents;


import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.*;

import eu.su.mas.dedaleEtu.mas.behaviours.MyExplorationBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.initiate_communication_behaviours.ReceiveAckCommunicationBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.initiate_communication_behaviours.ReceiveCommunicationBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.initiate_communication_behaviours.SendCommunicationBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.negociation_communication_behaviours.ReceiveAckNegociationBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.negociation_communication_behaviours.ReceiveNegociationBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.negociation_communication_behaviours.SendNegociationBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.shareCharacteristics_behaviours.ReceiveAckCharacteristicsBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.shareCharacteristics_behaviours.ReceiveCharacteristicsBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.shareCharacteristics_behaviours.SendCharacteristicsBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.stop_communication_behaviours.StopCommunicationBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.topology_communication_behaviors.ReceiveAckMapObsBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.topology_communication_behaviors.ReceiveMapObsBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.topology_communication_behaviors.SendMapObsBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.EndExplorationBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.OtherAgentsCharacteristics;
import eu.su.mas.dedaleEtu.mas.knowledge.OtherAgentsObservations;
import eu.su.mas.dedaleEtu.mas.knowledge.OtherAgentsTopology;
import eu.su.mas.dedaleEtu.mas.managers.CommunicationManager;
import eu.su.mas.dedaleEtu.mas.managers.MovementManager;
import eu.su.mas.dedaleEtu.mas.managers.ObservationManager;
import eu.su.mas.dedaleEtu.mas.managers.OtherAgentsKnowledgeManager;
import eu.su.mas.dedaleEtu.mas.managers.TopologyManager;
import eu.su.mas.dedaleEtu.mas.managers.CommunicationManager.COMMUNICATION_STEP;

import java.util.ArrayList;
import java.util.List;


import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;


public class MyAgent extends GeneralAgent {

    private static final long serialVersionUID = -7969469610241668140L;

    // His behaviour (FSM)
    private FSMBehaviour fsm;


    protected void setup(){

		super.setup();

        /*
         * Initialisation de la liste des agents, et rajout de ces noms dans les objets appropriés.
         */
        final Object[] args = getArguments();

        if(args.length==0){
			System.err.println("Error while creating the agent, names of agent to contact expected");
			System.exit(-1);
		} else {
			int i=2; // WARNING YOU SHOULD ALWAYS START AT 2. This will be corrected in the next release.
			while (i < args.length) {
                String agentName = (String)args[i];

				list_agentNames.add(agentName);
                pendingUpdatesCount.put(agentName, 0);
				i++;
			}
		}

        this.otherAgentsTopology = new OtherAgentsTopology(list_agentNames);
        this.otherAgentsObservations = new OtherAgentsObservations(list_agentNames);
        this.otherAgentsCharacteristics = new OtherAgentsCharacteristics(list_agentNames);



        /*
         * Initialisation des managers.
         */
        moveMgr = new MovementManager(this);
        topoMgr = new TopologyManager(this);
        obsMgr = new ObservationManager(this);
        comMgr = new CommunicationManager(this);
        otherKnowMgr = new OtherAgentsKnowledgeManager(this);



        /*
         * Initialisation du FSMBehaviour.
         */

        // --- STATES ---

        this.fsm = new FSMBehaviour(this);

        // Behaviour d'exploration.
        this.fsm.registerFirstState(new MyExplorationBehaviour(this), "Exploration");
        
        // Behaviours pour demander et accepter une communication.
        this.fsm.registerState(new SendCommunicationBehaviour(this), "SendCommunication");
        this.fsm.registerState(new ReceiveCommunicationBehaviour(this), "ReceiveCommunication");
        this.fsm.registerState(new ReceiveAckCommunicationBehaviour(this), "ReceiveAckCommunication");

        // Behaviours pour qu'ils s'organisent sur quoi s'envoyer et quoi attendre.
        this.fsm.registerState(new SendNegociationBehaviour(this), "SendNegociation");
        this.fsm.registerState(new ReceiveNegociationBehaviour(this), "ReceiveNegociation");
        this.fsm.registerState(new ReceiveAckNegociationBehaviour(this), "ReceiveAckNegociation");


        // Behaviours pour que les agents s'échangent leurs caractéristiques.
        // this.fsm.registerState(new SendCharacteristicsBehaviour(this), "SendChr");
        // this.fsm.registerState(new ReceiveAckCharacteristicsBehaviour(this), "ReceiveAckChr");
        // this.fsm.registerState(new ReceiveCharacteristicsBehaviour(this), "ReceiveChr");

        // Behaviours pour que les agents s'échangent leurs topologies et observations.
        this.fsm.registerState(new SendMapObsBehaviour(this), "SendMap");
        this.fsm.registerState(new ReceiveMapObsBehaviour(this), "ReceiveMap");
        this.fsm.registerState(new ReceiveAckMapObsBehaviour(this), "ReceiveAckMap");

        // Behaviour de fin de communication
        this.fsm.registerState(new StopCommunicationBehaviour(this), "StopCommunication");

        // Behaviour temporaire de fin d'exploration.
        this.fsm.registerLastState(new EndExplorationBehaviour(this), "EndExploration");


        // --- TRANSITIONS ---

        // On essaye d'avoir une conversation avec quelqu'un.
        this.fsm.registerDefaultTransition("Exploration", "SendCommunication");
        this.fsm.registerDefaultTransition("SendCommunication", "ReceiveCommunication");
        this.fsm.registerDefaultTransition("ReceiveAckCommunication", "ReceiveCommunication");
        this.fsm.registerDefaultTransition("ReceiveCommunication", "Exploration");

        this.fsm.registerDefaultTransition("SendNegociation", "ReceiveNegociation");
        this.fsm.registerDefaultTransition("ReceiveNegociation", "ReceiveAckNegociation");
        this.fsm.registerDefaultTransition("ReceiveAckNegociation", "StopCommunication");

        // this.fsm.registerDefaultTransition("SendChr", "ReceiveChr");
        // this.fsm.registerDefaultTransition("ReceiveChr", "ReceiveAckChr");
        // this.fsm.registerDefaultTransition("ReceiveAckChr", "StopCommunication");

        this.fsm.registerDefaultTransition("SendMap", "ReceiveMap");
        this.fsm.registerDefaultTransition("ReceiveMap", "ReceiveAckMap");
        this.fsm.registerDefaultTransition("ReceiveAckMap", "StopCommunication");

        this.fsm.registerDefaultTransition("StopCommunication", "Exploration");

        // ======================

        this.fsm.registerTransition("Exploration", "EndExploration", 2);

        this.fsm.registerTransition("SendCommunication", "ReceiveAckCommunication", 1);
        this.fsm.registerTransition("ReceiveAckCommunication", "SendNegociation", 1);

        // this.fsm.registerTransition("ReceiveNegociation", "SendSendChr", COMMUNICATION_STEP.SHARE_CHARACTERISTICS.getExitCode());
        this.fsm.registerTransition("ReceiveNegociation", "SendMap", COMMUNICATION_STEP.SHARE_TOPO.getExitCode());

        // this.fsm.registerTransition("ReceiveAckNegociation", "SendSendChr", COMMUNICATION_STEP.SHARE_CHARACTERISTICS.getExitCode());
        this.fsm.registerTransition("ReceiveAckNegociation", "SendMap", COMMUNICATION_STEP.SHARE_TOPO.getExitCode());

        // this.fsm.registerTransition("ReceiveChr", "SendChr", COMMUNICATION_STEP.SHARE_CHARACTERISTICS.getExitCode());
        // this.fsm.registerTransition("ReceiveChr", "SendMap", COMMUNICATION_STEP.SHARE_TOPO.getExitCode());

        // this.fsm.registerTransition("ReceiveAckChr", "SendSendChr", COMMUNICATION_STEP.SHARE_CHARACTERISTICS.getExitCode());
        this.fsm.registerTransition("ReceiveAckChr", "SendMap", COMMUNICATION_STEP.SHARE_TOPO.getExitCode());

        // this.fsm.registerTransition("ReceiveMap", "SendChr", COMMUNICATION_STEP.SHARE_CHARACTERISTICS.getExitCode());
        this.fsm.registerTransition("ReceiveMap", "SendMap", COMMUNICATION_STEP.SHARE_TOPO.getExitCode());

        // this.fsm.registerTransition("ReceiveAckMap", "SendSendChr", COMMUNICATION_STEP.SHARE_CHARACTERISTICS.getExitCode());
        this.fsm.registerTransition("ReceiveAckMap", "SendMap", COMMUNICATION_STEP.SHARE_TOPO.getExitCode());



        /*
         * Démarrage du FSMBehaviour.
         */

		List<Behaviour> lb = new ArrayList<Behaviour>();
	    lb.add(fsm);

		addBehaviour(new StartMyBehaviours(this,lb));
		System.out.println("the  agent "+this.getLocalName()+ " is started");
	}


    public void beforeMove() {
        super.beforeMove();
    }

    public void afterMove() {
        super.afterMove();
    }
}
