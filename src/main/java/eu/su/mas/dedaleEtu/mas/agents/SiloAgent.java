package eu.su.mas.dedaleEtu.mas.agents;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.StartMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.EndBehaviour;
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
import eu.su.mas.dedaleEtu.mas.behaviours.silo_post_explo_behaviours.MoveToMeetingPointBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.stop_communication_behaviours.StopCommunicationBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.topology_communication_behaviors.ReceiveAckMapObsBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.topology_communication_behaviors.ReceiveMapObsBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.topology_communication_behaviors.SendMapObsBehaviour;
import eu.su.mas.dedaleEtu.mas.managers.CommunicationManager;
import eu.su.mas.dedaleEtu.mas.managers.CommunicationManager.COMMUNICATION_STEP;
import eu.su.mas.dedaleEtu.mas.managers.MovementManager;
import eu.su.mas.dedaleEtu.mas.managers.ObservationManager;
import eu.su.mas.dedaleEtu.mas.managers.OtherAgentsKnowledgeManager;
import eu.su.mas.dedaleEtu.mas.managers.TopologyManager;


import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;

public class SiloAgent extends AbstractAgent {
    private static final long serialVersionUID = -7969469610241668140L;

    // His behaviour (FSM)
    private FSMBehaviour fsm;


    protected void setup(){

		super.setup();



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
        this.fsm.registerState(new SendCharacteristicsBehaviour(this), "SendChr");
        this.fsm.registerState(new ReceiveCharacteristicsBehaviour(this), "ReceiveChr");
        this.fsm.registerState(new ReceiveAckCharacteristicsBehaviour(this), "ReceiveAckChr");

        // Behaviours pour que les agents s'échangent leurs topologies et observations.
        this.fsm.registerState(new SendMapObsBehaviour(this), "SendMap");
        this.fsm.registerState(new ReceiveMapObsBehaviour(this), "ReceiveMap");
        this.fsm.registerState(new ReceiveAckMapObsBehaviour(this), "ReceiveAckMap");

        // Behaviour de fin de communication
        this.fsm.registerState(new StopCommunicationBehaviour(this), "StopCommunication");

        // Behaviour direction le point de rendez-vous
        this.fsm.registerState(new MoveToMeetingPointBehaviour(this), "MoveToMeetingPoint");

        // Behaviour temporaire de fin d'exploration.
        this.fsm.registerLastState(new EndBehaviour(this), "End");


        // --- TRANSITIONS ---

        // On essaye d'avoir une conversation avec quelqu'un.
        this.fsm.registerDefaultTransition("Exploration", "SendCommunication");
        this.fsm.registerDefaultTransition("SendCommunication", "ReceiveCommunication");
        this.fsm.registerDefaultTransition("ReceiveAckCommunication", "ReceiveCommunication");
        this.fsm.registerDefaultTransition("ReceiveCommunication", "Exploration");

        this.fsm.registerDefaultTransition("SendNegociation", "ReceiveNegociation");
        this.fsm.registerDefaultTransition("ReceiveNegociation", "StopCommunication");
        this.fsm.registerDefaultTransition("ReceiveAckNegociation", "StopCommunication");

        this.fsm.registerDefaultTransition("SendChr", "ReceiveChr");
        this.fsm.registerDefaultTransition("ReceiveChr", "StopCommunication");
        this.fsm.registerDefaultTransition("ReceiveAckChr", "StopCommunication");

        this.fsm.registerDefaultTransition("SendMap", "ReceiveMap");
        this.fsm.registerDefaultTransition("ReceiveMap", "StopCommunication");
        this.fsm.registerDefaultTransition("ReceiveAckMap", "StopCommunication");

        this.fsm.registerDefaultTransition("StopCommunication", "SendCommunication");

        this.fsm.registerDefaultTransition("MoveToMeetingPoint", "SendCommunication");


        
        // ======================

        this.fsm.registerTransition("Exploration", "MoveToMeetingPoint", 2);
        this.fsm.registerTransition("MoveToMeetingPoint", "End", 2);

        this.fsm.registerTransition("SendCommunication", "ReceiveAckCommunication", 1);
        this.fsm.registerTransition("ReceiveAckCommunication", "SendNegociation", 1);
        this.fsm.registerTransition("ReceiveCommunication", "SendNegociation", 1);

        this.fsm.registerTransition("ReceiveCommunication", "MoveToMeetingPoint", 2);


        this.fsm.registerTransition("ReceiveNegociation", "ReceiveAckNegociation", 1);
        this.fsm.registerTransition("ReceiveMap", "ReceiveAckMap", 1);
        this.fsm.registerTransition("ReceiveChr", "ReceiveAckChr", 1);


        this.fsm.registerTransition("ReceiveAckNegociation", "SendChr", COMMUNICATION_STEP.SHARE_CHARACTERISTICS.getExitCode());
        this.fsm.registerTransition("ReceiveAckNegociation", "SendMap", COMMUNICATION_STEP.SHARE_TOPO.getExitCode());

        this.fsm.registerTransition("ReceiveAckChr", "SendChr", COMMUNICATION_STEP.SHARE_CHARACTERISTICS.getExitCode());
        this.fsm.registerTransition("ReceiveAckChr", "SendMap", COMMUNICATION_STEP.SHARE_TOPO.getExitCode());

        this.fsm.registerTransition("ReceiveAckMap", "SendChr", COMMUNICATION_STEP.SHARE_CHARACTERISTICS.getExitCode());
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
