package eu.su.mas.dedaleEtu.mas.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.msgObjects.CharacteristicsMessage;
import eu.su.mas.dedaleEtu.mas.msgObjects.TopologyMessage;
import eu.su.mas.dedaleEtu.mas.knowledge.OtherAgentsCharacteristics;
import eu.su.mas.dedaleEtu.mas.knowledge.OtherAgentsTreasures;
import eu.su.mas.dedaleEtu.mas.knowledge.TreasureObservations;
import eu.su.mas.dedaleEtu.mas.knowledge.OtherAgentsTopology;
import eu.su.mas.dedaleEtu.mas.knowledge.MapMoreRepresentation;
import eu.su.mas.dedaleEtu.mas.managers.CommunicationManager;
import eu.su.mas.dedaleEtu.mas.managers.MovementManager;
import eu.su.mas.dedaleEtu.mas.managers.VisionManager;
import eu.su.mas.dedaleEtu.mas.managers.OtherAgentsKnowledgeManager;
import eu.su.mas.dedaleEtu.mas.managers.TopologyManager;
import eu.su.mas.dedaleEtu.mas.managers.TreasureManager;
import eu.su.mas.dedaleEtu.mas.managers.CommunicationManager.COMMUNICATION_STEP;



public abstract class AbstractAgent extends AbstractDedaleAgent {

    // --- ATTRIBUTS GENERAUX ---
    protected static final long serialVersionUID = -7969469610241668140L;
    protected List<String> list_agentNames = new ArrayList<>();
    protected AgentBehaviorState behaviorState = AgentBehaviorState.EXPLORATION;


    // --- MANAGERS ---
    public MovementManager moveMgr;
    public TopologyManager topoMgr;
    public VisionManager visionMgr;
    public TreasureManager treasureMgr;
    public CommunicationManager comMgr;
    public OtherAgentsKnowledgeManager otherKnowMgr;


    // --- ATTRIBUTS D'EXPLORATION
    protected MapMoreRepresentation myMap = null; 
    protected TreasureObservations myTreasures = new TreasureObservations();

    protected List<String> currentPath = new ArrayList<>();
    protected String targetNode = null;

    protected boolean exploCompleted = false;
    protected int failedMoveCount = 0;


    // --- ATTRIBUTS DE COMMUNICATION ---
    protected Map<COMMUNICATION_STEP, Boolean> communicationSteps = new HashMap<>();
    protected String targetAgent = null;
    protected int behaviourTimeoutMills = 1000;    


    // --- ATTRIBUTS D'HISTORIQUE DE MESSAGES ---
    protected Map<Integer, TopologyMessage> topologyMessageHistory = new HashMap<>();
    protected Map<Integer, CharacteristicsMessage> characteristicsMessageHistory = new HashMap<>();


    // --- ATTRIBUTS DE POINT DE RENDEZ-VOUS ---
    // Ici, il est primordial que TOUS les agents ont les mêmes points, pour qu'ils aillent tous au même endroit.
    public final double distanceWeight = 0.7;
    public final double degreeWeight = 0.3;

    protected String meetingPointId = null;




    // --- ATTRIBUTS DES AUTRES AGENTS ---
    protected OtherAgentsCharacteristics otherAgentsCharacteristics = new OtherAgentsCharacteristics();
    protected OtherAgentsTreasures otherAgentsTreasures = new OtherAgentsTreasures();
    protected OtherAgentsTopology otherAgentsTopology = new OtherAgentsTopology();

    protected Map<String, Integer> pendingUpdatesCount = new HashMap<>() ;
    protected int minUpdatesToShare = 7;

    protected int priority = 0;



    /*
     * --- ENUMERATION DES TYPES DE MODES ---
     */

    public enum AgentBehaviorState {
        // Mode d'exploration de l'environnement
        EXPLORATION("Exploration"),

        // Mode de recherche de point de rendez-vous, et de synchronisation
        SILO("Silo"),
        
        // Modes liés au déplacement et rendez-vous
        COLLECT("Collect");

        private final String displayName;
        
        AgentBehaviorState(String displayName) {
            this.displayName = displayName;
        }
        
        @Override
        public String toString() {
            return this.displayName;
        }
    }



    /*
     * --- TYPE DE L'AGENT ---
     */

    public enum AgentType {
        OTHER("Other"),
        COLLECTOR("Collector"),
        SILO("Silo");

        private final String displayName;
        AgentType(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return this.displayName;
        }
    }

    

    /*
     * --- METHODES GENERALES ---
     */

    protected void setup() {
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
                if (agentName.equals(getLocalName()))
                    continue;

				list_agentNames.add(agentName);
                pendingUpdatesCount.put(agentName, 0);
				i++;
			}
		}

        this.otherAgentsTopology = new OtherAgentsTopology(list_agentNames);
        this.otherAgentsTreasures = new OtherAgentsTreasures(list_agentNames);
        this.otherAgentsCharacteristics = new OtherAgentsCharacteristics(list_agentNames);


        /*
         * Initialisation des managers.
         */
        moveMgr = new MovementManager(this);
        topoMgr = new TopologyManager(this);
        visionMgr = new VisionManager(this);
        treasureMgr = new TreasureManager(this);
        comMgr = new CommunicationManager(this);
        otherKnowMgr = new OtherAgentsKnowledgeManager(this);

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

    public AgentBehaviorState getBehaviorState() {
        return this.behaviorState;
    }

    public void setBehaviorState(AgentBehaviorState behaviorState) {
        this.behaviorState = behaviorState;
    }



    /*
     * --- METHODES D'EXPLORATION --- 
     */

    public void initMapRepresentation() {
        this.myMap = new MapMoreRepresentation();
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

    public MapMoreRepresentation getMyMap() {
        return this.myMap;
    }

    public OtherAgentsTopology getOtherAgentsTopology() {
        return this.otherAgentsTopology;
    }



    /*
     * --- METHODES D'OBSERVATIONS ---
     */

    public TreasureObservations getMyTreasures() {
        return this.myTreasures;
    }

    public OtherAgentsTreasures getOtherAgentsTreasures() {
        return this.otherAgentsTreasures;
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



    /*
     * --- METHODES DE POINT DE RENDEZ-VOUS ---
     */

    public String getMeetingPoint() {
        return this.meetingPointId;
    }

    public void setMeetingPoint(String meetingPointId) {
        this.meetingPointId = meetingPointId;
    }



    /*
     * --- GETTER DU TYPE DE L'AGENT ---
     */

    public AgentType getAgentType() {
        if (this instanceof SiloAgent)
            return AgentType.SILO;
        else if (this instanceof CollectorAgent)
            return AgentType.COLLECTOR;
        else
            return AgentType.OTHER;
    }
}
