package eu.su.mas.dedaleEtu.mas.agents;


import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.*;

import eu.su.mas.dedaleEtu.mas.behaviours.Exploration;
import eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.characteristics_share_behaviours.ReceiveAckCharacteristics;
import eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.characteristics_share_behaviours.ReceiveCharacteristics;
import eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.characteristics_share_behaviours.SendCharacteristics;
import eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.initiate_communication_behaviours.ReceiveAckCommunication;
import eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.initiate_communication_behaviours.ReceiveCommunication;
import eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.initiate_communication_behaviours.SendCommunication;
import eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.negociation_topics_behaviours.ReceiveAckNegociation;
import eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.negociation_topics_behaviours.ReceiveNegociation;
import eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.negociation_topics_behaviours.SendNegociation;
import eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.stop_communication_behaviours.StopCommunication;
import eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.topology_share_behaviors.ReceiveAckTopo;
import eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.topology_share_behaviors.ReceiveTopo;
import eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.topology_share_behaviors.SendTopo;
import eu.su.mas.dedaleEtu.mas.behaviours.flooding_behaviours.EndFlood;
import eu.su.mas.dedaleEtu.mas.behaviours.flooding_behaviours.entry_in_flood.PropagateEveryoneIsHere;
import eu.su.mas.dedaleEtu.mas.behaviours.flooding_behaviours.entry_in_flood.ReceiveAckRequestFloodingEntry;
import eu.su.mas.dedaleEtu.mas.behaviours.flooding_behaviours.entry_in_flood.ReceiveNotifyEntry;
import eu.su.mas.dedaleEtu.mas.behaviours.flooding_behaviours.entry_in_flood.ReceiveRequestFloodingEntry;
import eu.su.mas.dedaleEtu.mas.behaviours.flooding_behaviours.entry_in_flood.SendRequestFloodingEntry;
import eu.su.mas.dedaleEtu.mas.behaviours.flooding_behaviours.sharing_characteristics.PropagateFloodCharacteristics;
import eu.su.mas.dedaleEtu.mas.behaviours.flooding_behaviours.sharing_characteristics.ReceiveFloodCharacteristics;
import eu.su.mas.dedaleEtu.mas.behaviours.flooding_behaviours.sharing_characteristics.RequestFloodCharacteristics;
import eu.su.mas.dedaleEtu.mas.behaviours.flooding_behaviours.sharing_plans.PropagateFloodCoalitions;
import eu.su.mas.dedaleEtu.mas.behaviours.flooding_behaviours.sharing_plans.SendFloodCoalitions;
import eu.su.mas.dedaleEtu.mas.behaviours.flooding_behaviours.sharing_treasures.PropagateFloodTreasures;
import eu.su.mas.dedaleEtu.mas.behaviours.flooding_behaviours.sharing_treasures.ReceiveFloodTreasures;
import eu.su.mas.dedaleEtu.mas.behaviours.flooding_behaviours.sharing_treasures.RequestFloodTreasures;
import eu.su.mas.dedaleEtu.mas.knowledge.FloodingState.FLOODING_STEP;
import eu.su.mas.dedaleEtu.mas.behaviours.End;
import eu.su.mas.dedaleEtu.mas.behaviours.MoveToMeetingPoint;
import eu.su.mas.dedaleEtu.mas.behaviours.MoveToTreasure;
import eu.su.mas.dedaleEtu.mas.behaviours.OnTreasure;
import eu.su.mas.dedaleEtu.mas.behaviours.RandomWalk;
import eu.su.mas.dedaleEtu.mas.managers.CommunicationManager.COMMUNICATION_STEP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;


public class MyAgent extends AbstractAgent {

    private static final long serialVersionUID = -7969469610241668140L;

    // His behaviour (FSM)
    private FSMBehaviour fsm;

    protected void setup(){

		super.setup();


        /*
         * Initialisation du FSMBehaviour.
         */

        // --- STATES ---

        this.fsm = new FSMBehaviour(this);

        // Behaviour d'exploration.
        this.fsm.registerFirstState(new Exploration(this), "Exploration");
        
        // Behaviours pour demander et accepter une communication.
        this.fsm.registerState(new SendCommunication(this), "SendCommunication");
        this.fsm.registerState(new ReceiveCommunication(this), "ReceiveCommunication");
        this.fsm.registerState(new ReceiveAckCommunication(this), "ReceiveAckCommunication");

        // Behaviours pour qu'ils s'organisent sur quoi s'envoyer et quoi attendre.
        this.fsm.registerState(new SendNegociation(this), "SendNegociation");
        this.fsm.registerState(new ReceiveNegociation(this), "ReceiveNegociation");
        this.fsm.registerState(new ReceiveAckNegociation(this), "ReceiveAckNegociation");


        // Behaviours pour que les agents s'échangent leurs caractéristiques.
        this.fsm.registerState(new SendCharacteristics(this), "SendChr");
        this.fsm.registerState(new ReceiveCharacteristics(this), "ReceiveChr");
        this.fsm.registerState(new ReceiveAckCharacteristics(this), "ReceiveAckChr");

        // Behaviours pour que les agents s'échangent leurs topologies et observations.
        this.fsm.registerState(new SendTopo(this), "SendMap");
        this.fsm.registerState(new ReceiveTopo(this), "ReceiveMap");
        this.fsm.registerState(new ReceiveAckTopo(this), "ReceiveAckMap");

        // Behaviour de fin de communication
        this.fsm.registerState(new StopCommunication(this), "StopCommunication");

        // Behaviour direction le point de rendez-vous
        this.fsm.registerState(new MoveToMeetingPoint(this), "MoveToMeetingPoint");

        // Behaviours pour la communication de demande d'entrée de flood.
        this.fsm.registerState(new SendRequestFloodingEntry(this), "SendEntryFlood");
        this.fsm.registerState(new ReceiveRequestFloodingEntry(this), "ReceiveEntryFlood");
        this.fsm.registerState(new ReceiveAckRequestFloodingEntry(this), "ReceiveAckEntryFlood");
        this.fsm.registerState(new ReceiveNotifyEntry(this), "NotifyEntryFlood");
        this.fsm.registerState(new PropagateEveryoneIsHere(this), "PropagateEveryoneIsHere");


        // Behaviours pour la communication de caractéristique dans un flood.
        this.fsm.registerState(new RequestFloodCharacteristics(this), "RequestChrFlood");
        this.fsm.registerState(new ReceiveFloodCharacteristics(this), "ReceiveChrFlood");
        this.fsm.registerState(new PropagateFloodCharacteristics(this), "PropagateChrFlood");

        // Behaviours pour la communication de trésors dans un flood.
        this.fsm.registerState(new RequestFloodTreasures(this), "RequestTreasureFlood");
        this.fsm.registerState(new ReceiveFloodTreasures(this), "ReceiveTreasureFlood");
        this.fsm.registerState(new PropagateFloodTreasures(this), "PropagateTreasureFlood");

        // Behaviours pour la communication de plan dans un flood.
        this.fsm.registerState(new SendFloodCoalitions(this), "SendCoalitions");
        this.fsm.registerState(new PropagateFloodCoalitions(this), "PropagateCoalitions");
        this.fsm.registerState(new EndFlood(this), "EndFlood");

        // Behaviours pour aller au trésor et le ramasser
        this.fsm.registerState(new MoveToTreasure(this), "MoveToTreasure");
        this.fsm.registerState(new OnTreasure(this), "OnTreasure");

        this.fsm.registerState(new RandomWalk(this), "RandomWalk");

        // Behaviour temporaire de fin.
        this.fsm.registerLastState(new End(this), "End");



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

        this.fsm.registerDefaultTransition("SendEntryFlood", "ReceiveAckEntryFlood");
        this.fsm.registerDefaultTransition("ReceiveEntryFlood", "StopCommunication");
        this.fsm.registerDefaultTransition("ReceiveAckEntryFlood", "StopCommunication");
        this.fsm.registerDefaultTransition("NotifyEntryFlood", "PropagateEveryoneIsHere");
        this.fsm.registerDefaultTransition("PropagateEveryoneIsHere", "SendCommunication");

        this.fsm.registerDefaultTransition("RequestChrFlood", "RequestChrFlood");
        this.fsm.registerDefaultTransition("ReceiveChrFlood", "ReceiveChrFlood");
        this.fsm.registerDefaultTransition("PropagateChrFlood", "RequestTreasureFlood");

        this.fsm.registerDefaultTransition("RequestTreasureFlood", "RequestTreasureFlood");
        this.fsm.registerDefaultTransition("ReceiveTreasureFlood", "ReceiveTreasureFlood");
        this.fsm.registerDefaultTransition("PropagateTreasureFlood", "SendCoalitions");

        this.fsm.registerDefaultTransition("SendCoalitions", "PropagateCoalitions");
        this.fsm.registerDefaultTransition("PropagateCoalitions", "PropagateCoalitions");

        this.fsm.registerDefaultTransition("EndFlood", "SendCommunication");

        this.fsm.registerDefaultTransition("MoveToTreasure", "SendCommunication");
        this.fsm.registerDefaultTransition("OnTreasure", "SendCommunication");

        this.fsm.registerDefaultTransition("RandomWalk", "SendCommunication");

        

        // ======================

        this.fsm.registerTransition("Exploration", "MoveToMeetingPoint", 2);
        this.fsm.registerTransition("MoveToMeetingPoint", "NotifyEntryFlood", 1);

        this.fsm.registerTransition("SendCommunication", "ReceiveAckCommunication", 1);
        this.fsm.registerTransition("ReceiveAckCommunication", "SendNegociation", 1);
        this.fsm.registerTransition("ReceiveCommunication", "SendNegociation", 1);


        this.fsm.registerTransition("ReceiveCommunication", "Exploration", AgentBehaviourState.EXPLORATION.getExitCode());
        this.fsm.registerTransition("ReceiveCommunication", "MoveToMeetingPoint", AgentBehaviourState.MEETING_POINT.getExitCode());
        this.fsm.registerTransition("ReceiveCommunication", "NotifyEntryFlood", AgentBehaviourState.FLOODING.getExitCode());
        this.fsm.registerTransition("ReceiveCommunication", "MoveToTreasure", AgentBehaviourState.COLLECT_TREASURE.getExitCode());
        this.fsm.registerTransition("ReceiveCommunication", "RandomWalk", AgentBehaviourState.RE_EXPLORATION.getExitCode());


        this.fsm.registerTransition("ReceiveNegociation", "ReceiveAckNegociation", 1);
        this.fsm.registerTransition("ReceiveMap", "ReceiveAckMap", 1);
        this.fsm.registerTransition("ReceiveChr", "ReceiveAckChr", 1);

        this.fsm.registerTransition("StopCommunication", "Exploration", AgentBehaviourState.EXPLORATION.getExitCode());
        this.fsm.registerTransition("StopCommunication", "MoveToMeetingPoint", AgentBehaviourState.MEETING_POINT.getExitCode());
        this.fsm.registerTransition("StopCommunication", "NotifyEntryFlood", AgentBehaviourState.FLOODING.getExitCode());
        this.fsm.registerTransition("StopCommunication", "MoveToTreasure", AgentBehaviourState.COLLECT_TREASURE.getExitCode());
        this.fsm.registerTransition("StopCommunication", "OnTreasure", AgentBehaviourState.ON_TREASURE.getExitCode());
        this.fsm.registerTransition("StopCommunication", "RandomWalk", AgentBehaviourState.RE_EXPLORATION.getExitCode());




        this.fsm.registerTransition("PropagateEveryoneIsHere", "RequestChrFlood", FLOODING_STEP.SHARING_CHARACTERISTICS.getExitCode());

        this.fsm.registerTransition("RequestChrFlood", "ReceiveChrFlood", 1);
        this.fsm.registerTransition("RequestChrFlood", "PropagateChrFlood", 2);
        this.fsm.registerTransition("ReceiveChrFlood", "PropagateChrFlood", 1);
        this.fsm.registerTransition("ReceiveChrFlood", "RequestTreasureFlood", 2);

        this.fsm.registerTransition("RequestTreasureFlood", "ReceiveTreasureFlood", 1);
        this.fsm.registerTransition("RequestTreasureFlood", "PropagateTreasureFlood", 2);
        this.fsm.registerTransition("ReceiveTreasureFlood", "PropagateTreasureFlood", 1);
        this.fsm.registerTransition("ReceiveTreasureFlood", "SendCoalitions", 2);

        this.fsm.registerTransition("PropagateCoalitions", "EndFlood", 1);

        this.fsm.registerTransition("MoveToTreasure", "OnTreasure", 1);
        this.fsm.registerTransition("RandomWalk", "MoveToMeetingPoint", 1);



        // --- TRANSITIONS DE TYPE DE COMMUNICATION (sauf Flood) ---

        Set<String> sources = new HashSet<>();
        sources.add("ReceiveAckNegociation");
        sources.add("ReceiveAckChr");
        sources.add("ReceiveAckMap");
        sources.add("ReceiveEntryFlood");
        sources.add("ReceiveAckEntryFlood");

        Map<String, Integer> destinations = new HashMap<>();
        destinations.put("SendChr", COMMUNICATION_STEP.SHARE_CHARACTERISTICS.getExitCode());
        destinations.put("SendMap", COMMUNICATION_STEP.SHARE_TOPO.getExitCode());
        destinations.put("SendEntryFlood", COMMUNICATION_STEP.ENTRY_FLOOD_SENT.getExitCode());
        destinations.put("ReceiveEntryFlood", COMMUNICATION_STEP.ENTRY_FLOOD_RECEIVED.getExitCode());

        for (String src : sources) {
            for (Map.Entry<String, Integer> dest : destinations.entrySet()) {
                this.fsm.registerTransition(src, dest.getKey(), dest.getValue());
            }
        }



        /*
         * Démarrage du FSMBehaviour.
         */

		List<Behaviour> lb = new ArrayList<Behaviour>();
	    lb.add(fsm);

		addBehaviour(new StartMyBehaviours(this,lb));
		System.out.println("The agent " + this.getLocalName() +  " is started");
	}


    public void beforeMove() {
        super.beforeMove();
    }

    public void afterMove() {
        super.afterMove();
    }
}
