package eu.su.mas.dedaleEtu.mas.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.msgObjects.TopologyObservations;
import eu.su.mas.dedaleEtu.mas.knowledge.NodeObservations;
import eu.su.mas.dedaleEtu.mas.knowledge.OtherAgentsCharacteristics;
import eu.su.mas.dedaleEtu.mas.knowledge.OtherAgentsObservations;
import eu.su.mas.dedaleEtu.mas.knowledge.OtherAgentsTopology;



public class GeneralAgent extends AbstractDedaleAgent {
    
    protected static final long serialVersionUID = -7969469610241668140L;

    // Liste des noms d'agents
    protected List<String> list_agentNames = new ArrayList<>();

    // Mes connaissances sur la topologie
    protected MapRepresentation myMap = null; 
    protected NodeObservations myObservations = new NodeObservations();
    protected boolean exploFinished = false;

    // Connaissances sur les autres agents
    protected OtherAgentsCharacteristics otherAgentsCharacteristics = new OtherAgentsCharacteristics();
    protected OtherAgentsTopology otherAgentsTopology = new OtherAgentsTopology();
    protected OtherAgentsObservations otherAgentsObservations = new OtherAgentsObservations();

    // Historique des messages pour la TOPO/OBS de forme : Map<msgId, <receiverName, <Topology, NodeObservation>>>
    protected Map<Integer, TopologyObservations> sentMessagesHistory_TOPO_OBS = new HashMap<>();
    protected AtomicInteger messageIdCounter = new AtomicInteger();

    // Exploration
    protected int failedMoveCount = 0;
    protected int priority = 0;

    protected List<String> currentPath = new ArrayList<>();
    protected String targetNode = null;


    protected void setup() {
        super.setup();
    }



    public void initMapRepresentation() {
        this.myMap = new MapRepresentation();
    }

    public void setExploFinished(boolean b) {
        this.exploFinished = b;
    }

    public boolean getExploFinished() {
        return this.exploFinished;
    }



    public List<String> getListAgentNames() {
        return this.list_agentNames;
    }

    public MapRepresentation getMyMap() {
        return this.myMap;
    }

    public OtherAgentsTopology getOtherAgentsTopology() {
        return this.otherAgentsTopology;
    }

    public NodeObservations getMyObservations() {
        return this.myObservations;
    }

    public OtherAgentsObservations getOtherAgentsObservations() {
        return this.otherAgentsObservations;
    }

    public TopologyObservations getHist_TopologyObservations(int msgId) {
        return this.sentMessagesHistory_TOPO_OBS.get(msgId);
    }


    public void addSentMessageToHistory(TopologyObservations Topo_Obs) {
        this.sentMessagesHistory_TOPO_OBS.put(Topo_Obs.getMsgId(), Topo_Obs);
    }

    public int generateMessageId() {
        return this.messageIdCounter.incrementAndGet();
    }


    public void incrementFailedMoveCount() {
        this.failedMoveCount++;
    }

    public void resetFailedMoveCount() {
        this.failedMoveCount = 0;
    }

    public int getFailedMoveCount() {
        return this.failedMoveCount;
    }


    public List<String> getCurrentPath() {
        return this.currentPath;
    }

    public void setCurrentPath(List<String> path) {
        this.currentPath = path;
    }

    public void clearCurrentPath() {
        this.currentPath.clear();
    }


    public String getTargetNode() {
        return this.targetNode;
    }

    public void setTargetNode(String nodeId) {
        this.targetNode = nodeId;
    }

    public void setTargetNodeFromCurrentPath() {
        this.targetNode = this.currentPath.isEmpty() ? null : this.currentPath.remove(0);
    }


    public void setCurrentPathToClosestOpenNode() {
        String myNode = this.getCurrentPosition().getLocationId();
        this.currentPath = this.myMap.getShortestPathToClosestOpenNode(myNode);
        this.targetNode = currentPath.remove(0);   
    }


    public void setCurrentPathForDeadlock(List<String> nodesToDodge) {
        String myNode = this.getCurrentPosition().getLocationId();
        this.currentPath = this.myMap.getShortestPathToClosestNodeExclude(myNode, nodesToDodge);
        this.targetNode = this.currentPath.isEmpty() ? null : currentPath.remove(0);   
    }


    public Map<String, String> getNeighborAgents() {
        Map<String, String> neighbors = new HashMap<>();

        List<Couple<Location,List<Couple<Observation,String>>>> lobs = this.observe();
        for (Couple<Location, List<Couple<Observation, String>>> obs : lobs) {
            Location location = obs.getLeft();
            List<Couple<Observation, String>> attributes = obs.getRight();

            for (Couple<Observation, String> observationNode : attributes) {
                if (observationNode.getLeft() == Observation.AGENTNAME)
                    neighbors.put(location.getLocationId(), observationNode.getRight());
            }
        }

        return neighbors;
    }


    public int getPriority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void increasePriority() {
        this.priority++;
    }
    
    public void decreasePriority() {
        this.priority = Math.max(0, this.priority - 1);
    }


    public boolean canMove() {
        List<Couple<Location,List<Couple<Observation,String>>>> lobs = this.observe();
        for (Couple<Location, List<Couple<Observation, String>>> obs : lobs) {
            List<Couple<Observation, String>> attributes = obs.getRight();

            for (Couple<Observation, String> observationNode : attributes) {
                if (observationNode.getLeft() != Observation.AGENTNAME)
                    return true;
            }
        }

        return false;
    }



    	/**
	 * This method is automatically called after doDelete()
	 */
	protected void takeDown(){
		super.takeDown();
	}

	protected void beforeMove(){
		super.beforeMove();
		//System.out.println("I migrate");
	}

	protected void afterMove(){
		super.afterMove();
		//System.out.println("I migrated");
	}
}
