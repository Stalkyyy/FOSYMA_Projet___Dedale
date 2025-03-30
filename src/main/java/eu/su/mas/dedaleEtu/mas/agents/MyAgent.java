package eu.su.mas.dedaleEtu.mas.agents;


import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.*;

import eu.su.mas.dedaleEtu.mas.behaviours.MyExplorationBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ReceiveAckMapObsBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ReceiveMapObsBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.SendMapObsBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.EndExplorationBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.OtherAgentsObservations;
import eu.su.mas.dedaleEtu.mas.knowledge.OtherAgentsTopology;
import eu.su.mas.dedaleEtu.mas.managers.CommunicationManager;
import eu.su.mas.dedaleEtu.mas.managers.MovementManager;
import eu.su.mas.dedaleEtu.mas.managers.ObservationManager;
import eu.su.mas.dedaleEtu.mas.managers.OtherAgentsKnowledgeManager;
import eu.su.mas.dedaleEtu.mas.managers.TopologyManager;

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
        this.fsm = new FSMBehaviour(this);

        this.fsm.registerFirstState(new MyExplorationBehaviour(this), "Explo");
        this.fsm.registerState(new SendMapObsBehaviour(this), "SendMap");
        this.fsm.registerState(new ReceiveAckMapObsBehaviour(this), "ReceiveAckMap");
        this.fsm.registerState(new ReceiveMapObsBehaviour(this), "ReceiveMap");
        this.fsm.registerLastState(new EndExplorationBehaviour(this), "EndExplo");

        this.fsm.registerDefaultTransition("Explo", "SendMap");
        this.fsm.registerDefaultTransition("SendMap", "ReceiveMap");
        this.fsm.registerDefaultTransition("ReceiveMap", "ReceiveAckMap");
        this.fsm.registerDefaultTransition("ReceiveAckMap", "Explo");

        this.fsm.registerTransition("ReceiveAckMap", "SendMap", 1);
        this.fsm.registerTransition("Explo", "EndExplo", 2);

		List<Behaviour> lb = new ArrayList<Behaviour>();
	    lb.add(fsm);
		

        /*
         * Démarrage du FSMBehaviour.
         */
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
