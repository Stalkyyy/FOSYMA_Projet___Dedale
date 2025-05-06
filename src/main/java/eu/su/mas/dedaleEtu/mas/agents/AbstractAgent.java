package eu.su.mas.dedaleEtu.mas.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.msgObjects.CharacteristicsMessage;
import eu.su.mas.dedaleEtu.mas.msgObjects.TopologyMessage;
import eu.su.mas.dedaleEtu.mas.msgObjects.TreasureMessage;
import eu.su.mas.dedaleEtu.mas.knowledge.OtherAgentsCharacteristics;
import eu.su.mas.dedaleEtu.mas.knowledge.TreasureObservations;
import eu.su.mas.dedaleEtu.mas.knowledge.OtherAgentsTopology;
import eu.su.mas.dedaleEtu.mas.knowledge.AgentsCoalition;
import eu.su.mas.dedaleEtu.mas.knowledge.FloodingState;
import eu.su.mas.dedaleEtu.mas.knowledge.MapMoreRepresentation;
import eu.su.mas.dedaleEtu.mas.managers.CoalitionManager;
import eu.su.mas.dedaleEtu.mas.managers.CommunicationManager;
import eu.su.mas.dedaleEtu.mas.managers.FloodingManager;
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

    protected AgentType agentType;
    protected AgentBehaviourState behaviourState = AgentBehaviourState.EXPLORATION;

    // --- INFORMATIONS SUR LES CAPACITES DE L'AGENT
    private int backPackTotalSpace;
    private int lockpick;
    private int strength;


    // --- MANAGERS ---
    public MovementManager moveMgr;
    public TopologyManager topoMgr;
    public VisionManager visionMgr;
    public TreasureManager treasureMgr;
    public CommunicationManager comMgr;
    public OtherAgentsKnowledgeManager otherKnowMgr;
    public FloodingManager floodMgr;
    public CoalitionManager  coalitionMgr;



    // --- ATTRIBUTS DE MEMOIRE SUR L'EXPLORATION
    protected MapMoreRepresentation myMap = null; 
    protected TreasureObservations myTreasures = new TreasureObservations();
    protected boolean exploCompleted = false;
    protected int failedMoveCount = 0;


    // --- ATTRIBUTS DE CHEMIN D'EXPLORATION ---
    protected List<String> currentPath = new ArrayList<>();
    protected String targetNode = null;


    // --- ATTRIBUT DE MISSION DE RAMASSAGE ---
    protected String focusedTreasureNodeId = null;


    // --- ATTRIBUTS DE COMMUNICATION ---
    protected Map<COMMUNICATION_STEP, Boolean> communicationSteps = new HashMap<>();
    protected String targetAgent = null;
    protected int behaviourTimeoutMills = 500;    


    // --- ATTRIBUTS D'HISTORIQUE DE MESSAGES ---
    protected Map<Integer, TopologyMessage> topologyMessageHistory = new HashMap<>();
    protected Map<Integer, TreasureMessage> treasureMessageHistory = new HashMap<>();
    protected Map<Integer, CharacteristicsMessage> characteristicsMessageHistory = new HashMap<>();


    // --- ATTRIBUTS DE POINT DE RENDEZ-VOUS ---
    // Ici, il est primordial que TOUS les agents ont les mêmes points, pour qu'ils aillent tous au même endroit.
    public final double distanceWeight = 0.7;
    public final double degreeWeight = 0.3;
    protected String meetingPointId = null;


    // --- ATTRIBUTS DES AUTRES AGENTS ---
    protected OtherAgentsCharacteristics otherAgentsCharacteristics = new OtherAgentsCharacteristics();
    protected OtherAgentsTopology otherAgentsTopology = new OtherAgentsTopology();


    // --- ATTRIBUTS POUR LE FLOODING PROTOCOL ---
    protected FloodingState floodingState = new FloodingState();

    // --- ATTRIBUTS DE COALITION ---
    protected AgentsCoalition coalitions;
    protected long startMissionMillis = -1;
    protected long collectTimeoutMillis = 1000 * 30;


    /*
     * --- ENUMERATION DES TYPES DE MODES ---
     */

    public enum AgentBehaviourState {

        // Mode d'exploration de l'environnement
        EXPLORATION(100),

        // Mode de recherche de point de rendez-vous
        MEETING_POINT(200),

        // Mode de flooding protocol
        FLOODING(300),
        
        // Mode lié à la collecte
        COLLECT_TREASURE(400),
        ON_TREASURE(500),

        // Mode d'exploration post-topologie
        RE_EXPLORATION(600);

        private int exitCode;
        AgentBehaviourState(int exitCode) {
            this.exitCode = exitCode;
        }

        public int getExitCode() {
            return this.exitCode;
        }
    }



    /*
     * --- TYPE DE L'AGENT ---
     */

    public enum AgentType {
        EXPLORER("EXPLORER", 0),
        COLLECTOR("COLLECTOR", 1),
        TANKER("TANKER", 2);

        private final String displayName;
        private final int id;
        AgentType(String displayName, int id) {
            this.displayName = displayName;
            this.id = id;
        }

        @Override
        public String toString() {
            return this.displayName;
        }

        public int getId() {
            return this.id;
        }

        public static AgentType fromString(String type) {
            for (AgentType agentType : AgentType.values()) {
                if (agentType.displayName.equalsIgnoreCase(type)) {
                    return agentType;
                }
            }
            throw new IllegalArgumentException("Type d'agent '" + type + "' invalide, vérifiez les arguments des entités dans Principal.java. Le premier paramètre doit être 'SILO' ou 'EXPLORER' ou 'TANKER'.");
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

            // On cherche le paramètre pour savoir quel type d'agent on est.
            try {
                this.agentType = AgentType.fromString((String) args[2]);
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
                System.exit(-1);
            }

            // On récupère le nom de nos confrères.
			int i = 3;
			while (i < args.length) {
                String agentName = (String)args[i];

                list_agentNames.add(agentName);
				i++;
			}
		}

        this.otherAgentsTopology = new OtherAgentsTopology(list_agentNames);

        this.otherAgentsCharacteristics = new OtherAgentsCharacteristics(list_agentNames);
        this.otherAgentsCharacteristics.updateCharacteristics(this.getLocalName(), this.agentType, this.getMyTreasureType(), this.backPackTotalSpace, this.lockpick, this.strength);

        this.coalitions = new AgentsCoalition(list_agentNames);



        /*
         * On récupère les capacités de l'agent. 
         */

        for (Couple<Observation, Integer> expertise : this.getMyExpertise()) {
            Observation type = expertise.getLeft();
            int level = expertise.getRight();

            if (type == Observation.LOCKPICKING)
                lockpick = level;
            else if (type == Observation.STRENGH)
                strength = level;
        }

        if (this.agentType == AgentType.TANKER) {
            backPackTotalSpace = Integer.MAX_VALUE;
        } else {
            for (Couple<Observation, Integer> backpack : this.getBackPackFreeSpace()) {
                Observation type = backpack.getLeft();
                int space = backpack.getRight();
    
                if (type == this.getMyTreasureType()) {
                    backPackTotalSpace = space;
                    break;
                }
            }
        }



        /*
         * Initialisation des managers.
         */
        moveMgr = new MovementManager(this);
        topoMgr = new TopologyManager(this);
        visionMgr = new VisionManager(this);
        treasureMgr = new TreasureManager(this);
        comMgr = new CommunicationManager(this);
        otherKnowMgr = new OtherAgentsKnowledgeManager(this);
        floodMgr = new FloodingManager(this);
        coalitionMgr = new CoalitionManager(this);

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

    public AgentBehaviourState getBehaviourState() {
        return this.behaviourState;
    }

    public void setBehaviourState(AgentBehaviourState behaviourState) {
        this.behaviourState = behaviourState;
    }

    public int freeSpace() {
        List<Couple<Observation, Integer>> freeSpaces = this.getBackPackFreeSpace();

        for (Couple<Observation, Integer> fs : freeSpaces) {
            Observation type = fs.getLeft();
            int value = fs.getRight();
            if (type == this.getMyTreasureType())
                return value;
        }

        return backPackTotalSpace;
    }



    /*
     * --- METHODES DE CARACTERISTIQUES --- 
     */

    

    public int getMyBackPackTotalSpace() {
        return backPackTotalSpace;
    }

    public int getMyLockpickLevel() {
        return lockpick;
    }

    public int getMyStrengthLevel() {
        return strength;
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
     * --- METHODES DE TRESORS ---
     */

    public TreasureObservations getMyTreasures() {
        return this.myTreasures;
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

    public Map<Integer, TreasureMessage> getTreasureMessageHistory() {
        return this.treasureMessageHistory;
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

    public void setBehaviourTimeoutMills(int ackTimeoutMills) {
        this.behaviourTimeoutMills = ackTimeoutMills;
    }

    public Map<COMMUNICATION_STEP, Boolean> getCommunicationSteps() {
        return this.communicationSteps;
    }

    public void setCommunicationSteps(Map<COMMUNICATION_STEP, Boolean> communicationSteps) {
        this.communicationSteps = communicationSteps;
    }


    /*
     * --- METHODES DE FLOODING PROTOCOL ---
     */ 

    public FloodingState getFloodingState() {
        return this.floodingState;
    }

    public void setFloodingState(FloodingState floodingState) {
        this.floodingState = floodingState;
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
     * --- GETTER DES COALITIONS ---
     */

    public AgentsCoalition getCoalitions() {
        return this.coalitions;
    }

    public void setCoalitions(AgentsCoalition coalitions) {
        this.coalitions = coalitions;
    }

    public long getStartMissionMillis() {
        return this.startMissionMillis;
    }

    public void startMissionMillis() {
        this.startMissionMillis = System.currentTimeMillis();
    }

    public long getCollectTimeoutMillis() {
        return this.collectTimeoutMillis;
    }



    /*
     * --- GETTER DU TYPE DE L'AGENT ---
     */

    public AgentType getAgentType() {
        return this.agentType;
    }
}
