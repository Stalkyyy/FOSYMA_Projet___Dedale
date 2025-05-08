package eu.su.mas.dedaleEtu.mas.agents.dummies.explo;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.*;
import eu.su.mas.dedaleEtu.mas.behaviours.given_behaviours.ExploCoopBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.given_knowledge.MapRepresentation;
import jade.core.behaviours.Behaviour;

/**
 * <pre>
 * ExploreCoop agent. 
 * Basic example of how to "collaboratively" explore the map
 *  - It explore the map using a DFS algorithm and blindly tries to share the topology with the agents within reach.
 *  - The shortestPath computation is not optimized
 *  - Agents do not coordinate themselves on the node(s) to visit, thus progressively creating a single file. It's bad.
 *  - The agent sends all its map, periodically, forever. Its bad x3.
 *  - You should give him the list of agents'name to send its map to in parameter when creating the agent.
 *   Object [] entityParameters={"Name1","Name2};
 *   ag=createNewDedaleAgent(c, agentName, ExploreCoopAgent.class.getName(), entityParameters);
 *  
 * It stops when all nodes have been visited.
 * 
 * 
 *  </pre>
 *  
 * @author hc
 *
 */


public class ExploreCoopAgent extends AbstractDedaleAgent {

	private static final long serialVersionUID = -7969469610241668140L;
	private MapRepresentation myMap;
	

	/**
	 * This method is automatically called when "agent".start() is executed.
	 * Consider that Agent is launched for the first time. 
	 * 			1) set the agent attributes 
	 *	 		2) add the behaviours
	 *          
	 */
	protected void setup(){

		super.setup();
		
		//get the parameters added to the agent at creation (if any)
		final Object[] args = getArguments();
		
		List<String> list_agentNames=new ArrayList<String>();
		
		// Check if the required parameters are provided
		if(args.length==0){
			System.err.println("Error while creating the agent, names of agent to contact expected");
			System.exit(-1);
		}else{
			 // Parse the agent names from the arguments
			int i=2;// WARNING YOU SHOULD ALWAYS START AT 2. This will be corrected in the next release.
			while (i<args.length) {
				list_agentNames.add((String)args[i]);
				i++;
			}
		}

		// Create a list of behaviors for the agent
		List<Behaviour> lb=new ArrayList<Behaviour>();
		
		/************************************************
		 * 
		 * ADD the behaviours of the Dummy Moving Agent
		 * 
		 ************************************************/
		
		lb.add(new ExploCoopBehaviour(this,this.myMap,list_agentNames));

		
		
		/***
		 * MANDATORY TO ALLOW YOUR AGENT TO BE DEPLOYED CORRECTLY
		 */
		addBehaviour(new StartMyBehaviours(this,lb));
		
		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}
	
	
	/**
	 * This method is automatically called after doDelete()
	 * Cleans up resources and performs any necessary shutdown tasks.
	 */
	protected void takeDown(){
		super.takeDown();
	}


	/**
     * This method is called before the agent migrates to another container.
     * - Can be used to save the agent's state before migration.
     */
	protected void beforeMove(){
		super.beforeMove();
		//System.out.println("I migrate");
	}


	/**
     * This method is called after the agent migrates to another container.
     * - Can be used to restore the agent's state after migration.
     */
	protected void afterMove(){
		super.afterMove();
		//System.out.println("I migrated");
	}

}
