package eu.su.mas.dedaleEtu.mas.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.msgObjects.CharacteristicsMessage;
import eu.su.mas.dedaleEtu.mas.msgObjects.TopologyMessage;
import eu.su.mas.dedaleEtu.mas.knowledge.NodeObservations;
import eu.su.mas.dedaleEtu.mas.knowledge.OtherAgentsCharacteristics;
import eu.su.mas.dedaleEtu.mas.knowledge.OtherAgentsObservations;
import eu.su.mas.dedaleEtu.mas.knowledge.OtherAgentsTopology;
import eu.su.mas.dedaleEtu.mas.managers.CommunicationManager;
import eu.su.mas.dedaleEtu.mas.managers.MovementManager;
import eu.su.mas.dedaleEtu.mas.managers.ObservationManager;
import eu.su.mas.dedaleEtu.mas.managers.OtherAgentsKnowledgeManager;
import eu.su.mas.dedaleEtu.mas.managers.TopologyManager;
import eu.su.mas.dedaleEtu.mas.managers.CommunicationManager.COMMUNICATION_STEP;



abstract class GeneralAgent extends AbstractDedaleAgent {

    // --- ATTRIBUTS GENERAUX ---
    protected static final long serialVersionUID = -7969469610241668140L;
    protected List<String> list_agentNames = new ArrayList<>();
    protected String actualMode = "EXPLORATION";


    // --- MANAGERS ---
    public MovementManager moveMgr;
    public TopologyManager topoMgr;
    public ObservationManager obsMgr;
    public CommunicationManager comMgr;
    public OtherAgentsKnowledgeManager otherKnowMgr;


    // --- ATTRIBUTS D'EXPLORATION
    protected MapRepresentation myMap = null; 
    protected NodeObservations myObservations = new NodeObservations();

    protected List<String> currentPath = new ArrayList<>();
    protected String targetNode = null;

    protected boolean exploCompleted = false;
    protected int failedMoveCount = 0;


    // --- ATTRIBUTS DE COMMUNICATION ---
    protected Map<COMMUNICATION_STEP, Boolean> communicationSteps = new HashMap<>();
    protected String targetAgent = null;
    protected int behaviourTimeoutMills = 100;    

    // --- ATTRIBUTS D'HISTORIQUE DE MESSAGES ---
    protected Map<Integer, TopologyMessage> topologyMessageHistory = new HashMap<>();
    protected Map<Integer, CharacteristicsMessage> characteristicsMessageHistory = new HashMap<>();



    // --- ATTRIBUTS DES AUTRES AGENTS ---
    protected OtherAgentsCharacteristics otherAgentsCharacteristics = new OtherAgentsCharacteristics();
    protected OtherAgentsObservations otherAgentsObservations = new OtherAgentsObservations();
    protected OtherAgentsTopology otherAgentsTopology = new OtherAgentsTopology();

    protected Map<String, Integer> pendingUpdatesCount = new HashMap<>() ;
    protected int minUpdatesToShare = 7;

    protected int priority = 0;




    /*
     * --- METHODES GENERALES ---
     */

    protected void setup() {
        super.setup();
    }

	protected void takeDown(){
		super.takeDown();
	}

	protected void beforeMove(){
		super.beforeMove();
	}

	protected void afterMove(){
		super.afterMove();
	}

    public List<String> getListAgentNames() {
        return this.list_agentNames;
    }



    /*
     * --- METHODES D'EXPLORATION --- 
     */

    public void initMapRepresentation() {
        this.myMap = new MapRepresentation();
    }

    public boolean getExplorationComplete() {
        return this.exploCompleted;
    }

    public void setExplorationComplete(boolean b) {
        this.exploCompleted = b;
    }

    public void markExplorationComplete() {
        this.exploCompleted = true;
    }

    

    // ---

    public void incrementFailedMoveCount() {
        this.failedMoveCount++;
    }

    public void resetFailedMoveCount() {
        this.failedMoveCount = 0;
    }

    public int getFailedMoveCount() {
        return this.failedMoveCount;
    }

    // ---

    public List<String> getCurrentPath() {
        return this.currentPath;
    }

    public void setCurrentPath(List<String> path) {
        this.currentPath = path;
    }

    public void clearCurrentPath() {
        this.currentPath.clear();
    }

    // ---

    public String getTargetNode() {
        return this.targetNode;
    }

    public void setTargetNode(String nodeId) {
        this.targetNode = nodeId;
    }

    public void setTargetNodeFromCurrentPath() {
        this.targetNode = this.currentPath.isEmpty() ? null : this.currentPath.remove(0);
    }



    /*
     * --- METHODES DE TOPOLOGIE ---
     */

    public MapRepresentation getMyMap() {
        return this.myMap;
    }

    public OtherAgentsTopology getOtherAgentsTopology() {
        return this.otherAgentsTopology;
    }



    /*
     * --- METHODES D'OBSERVATIONS ---
     */

    public NodeObservations getMyObservations() {
        return this.myObservations;
    }

    public OtherAgentsObservations getOtherAgentsObservations() {
        return this.otherAgentsObservations;
    }



    /*
     * --- METHODES DE CHARACTERISTIQUES ---
     */

    public OtherAgentsCharacteristics getOtherAgentsCharacteristics() {
        return this.otherAgentsCharacteristics;
    }



    /*
     * --- METHODES DE COMMUNICATION ---
     */

    public Map<Integer, TopologyMessage> getTopologyMessageHistory() {
        return this.topologyMessageHistory;
    }

    public Map<Integer, CharacteristicsMessage> getCharacteristicsMessageHistory() {
        return this.characteristicsMessageHistory;
    }

    public String getTargetAgent() {
        return this.targetAgent;
    }

    public void setTargetAgent(String agentName) {
        this.targetAgent = agentName;
    }

    public int getBehaviourTimeoutMills() {
        return this.behaviourTimeoutMills;
    }

    public void setbehaviourTimeoutMills(int ackTimeoutMills) {
        this.behaviourTimeoutMills = ackTimeoutMills;
    }

    public Map<COMMUNICATION_STEP, Boolean> getCommunicationSteps() {
        return this.communicationSteps;
    }

    public void setCommunicationSteps(Map<COMMUNICATION_STEP, Boolean> communicationSteps) {
        this.communicationSteps = communicationSteps;
    }


    /*
     * --- METHODES DE PRIORITE ---
     */ 

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
}
