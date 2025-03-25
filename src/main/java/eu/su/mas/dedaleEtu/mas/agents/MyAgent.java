package eu.su.mas.dedaleEtu.mas.agents;


import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.*;

import eu.su.mas.dedaleEtu.mas.behaviours.MyExplorationBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ReceiveAckMapObsBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ReceiveMapObsBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.SendMapObsBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.endExplorationBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.OtherAgentsObservations;
import eu.su.mas.dedaleEtu.mas.knowledge.OtherAgentsTopology;

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


        // Initialisation des noms des agents
        final Object[] args = getArguments();

        if(args.length==0){
			System.err.println("Error while creating the agent, names of agent to contact expected");
			System.exit(-1);
		} else {
			int i=2; // WARNING YOU SHOULD ALWAYS START AT 2. This will be corrected in the next release.
			while (i < args.length) {
				list_agentNames.add((String)args[i]);
				i++;
			}
		}

        this.otherAgentsTopology = new OtherAgentsTopology(list_agentNames);
        this.otherAgentsObservations = new OtherAgentsObservations(list_agentNames);





        // Création du FSMBehaviour
        this.fsm = new FSMBehaviour(this);

        this.fsm.registerFirstState(new MyExplorationBehaviour(this), "MyExplorationBehaviour");
        this.fsm.registerState(new SendMapObsBehaviour(this), "SendMapObsBehaviour");
        this.fsm.registerState(new ReceiveAckMapObsBehaviour(this), "ReceiveAckMapObsBehaviour");
        this.fsm.registerState(new ReceiveMapObsBehaviour(this), "ReceiveMapObsBehaviour");
        this.fsm.registerLastState(new endExplorationBehaviour(this), "endExplorationBehaviour");

        this.fsm.registerDefaultTransition("MyExplorationBehaviour", "SendMapObsBehaviour");
        this.fsm.registerDefaultTransition("SendMapObsBehaviour", "ReceiveMapObsBehaviour");
        this.fsm.registerDefaultTransition("ReceiveMapObsBehaviour", "ReceiveAckMapObsBehaviour");
        this.fsm.registerDefaultTransition("ReceiveAckMapObsBehaviour", "MyExplorationBehaviour");
        this.fsm.registerTransition("ReceiveAckMapObsBehaviour", "SendMapObsBehaviour", 1);
        this.fsm.registerTransition("MyExplorationBehaviour", "endExplorationBehaviour", 1);


        // Ajout du FSMBehabiour
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
