package eu.su.mas.dedaleEtu.mas.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.msgObjects.TopologyMessage;
import eu.su.mas.dedaleEtu.mas.knowledge.OtherAgentsCharacteristics;
import eu.su.mas.dedaleEtu.mas.knowledge.TreasureObservations;
import eu.su.mas.dedaleEtu.mas.knowledge.OtherAgentsTopology;
import eu.su.mas.dedaleEtu.mas.knowledge.AgentsCoalition;
import eu.su.mas.dedaleEtu.mas.knowledge.FloodingState;
import eu.su.mas.dedaleEtu.mas.knowledge.MapMoreRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.NodeReservation;
import eu.su.mas.dedaleEtu.mas.managers.CoalitionManager;
import eu.su.mas.dedaleEtu.mas.managers.CommunicationManager;
import eu.su.mas.dedaleEtu.mas.managers.FloodingManager;
import eu.su.mas.dedaleEtu.mas.managers.MovementManager;
import eu.su.mas.dedaleEtu.mas.managers.NodeReservationManager;
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
    public NodeReservationManager reserveMgr;



    // --- ATTRIBUTS DE MEMOIRE SUR L'EXPLORATION
    protected MapMoreRepresentation myMap = null; 
    protected TreasureObservations myTreasures = new TreasureObservations();
    protected boolean exploCompleted = false;


    // --- ATTRIBUTS DE CHEMIN D'EXPLORATION ---
    protected List<String> currentPath = new ArrayList<>();
    protected String targetNode = null;


    // --- ATTRIBUT DE MISSION DE RAMASSAGE ---
    protected String focusedTreasureNodeId = null;


    // --- ATTRIBUTS DE COMMUNICATION ---
    protected Map<COMMUNICATION_STEP, Boolean> communicationSteps = new HashMap<>();
    protected String targetAgent = null;
    protected String targetAgentNode = null;
    protected int behaviourTimeoutMills = 400;    


    // --- ATTRIBUTS D'HISTORIQUE DE MESSAGES ---
    protected Map<Integer, TopologyMessage> topologyMessageHistory = new HashMap<>();


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
    protected long collectTimeoutMillis = 120000;

    // --- ATTRIBUTS D'INTERBLOCAGE ---
    protected String deadlockNodeSolution = null;
    protected NodeReservation nodeReservation = null;
    protected long deadlockTimeoutMillis = 1000 * 5;


    /*
     * --- ENUMERATION DES TYPES DE MODES ---
     */

    public enum AgentBehaviourState {

        // Mode d'exploration de l'environnement
        EXPLORATION(100, 1),

        // Mode de recherche de point de rendez-vous
        MEETING_POINT(200, 4),

        // Mode de flooding protocol
        FLOODING(300, 5),
        
        // Mode lié à la collecte
        COLLECT_TREASURE(400, 3),

        // Mode d'exploration post-topologie
        RE_EXPLORATION(600, 2);

        private int exitCode;
        private int deadlockPriority;
        AgentBehaviourState(int exitCode, int deadlockPriority) {
            this.exitCode = exitCode;
            this.deadlockPriority = deadlockPriority;
        }

        public int getExitCode() {
            return this.exitCode;
        }

        public int getPriority() {
            return deadlockPriority;
        }
    }



    /*
     * --- TYPE DE L'AGENT ---
     */

    public enum AgentType {
        EXPLORER("EXPLORER", 0),
        COLLECTOR("COLLECTOR", 1),
        TANKER("TANKER", 2),
        ENNEMI("WUMPUS", 3);

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
                if (agentType.displayName.compareTo(type) == 0) {
                    return agentType;
                }
            }
            return ENNEMI;
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
        // Initialisation des connaissances sur les autres agents
        this.otherAgentsTopology = new OtherAgentsTopology(list_agentNames);

        this.otherAgentsCharacteristics = new OtherAgentsCharacteristics(list_agentNames);
        this.otherAgentsCharacteristics.updateCharacteristics(this.getLocalName(), this.agentType, this.getMyTreasureType(), this.backPackTotalSpace, this.lockpick, this.strength);
        // Initialisation des coalitions
        this.coalitions = new AgentsCoalition(list_agentNames);



        //On récupère les capacités de l'agent. 

        for (Couple<Observation, Integer> expertise : this.getMyExpertise()) {
            Observation type = expertise.getLeft();
            int level = expertise.getRight();

            if (type == Observation.LOCKPICKING)
                lockpick = level;
            else if (type == Observation.STRENGH)
                strength = level;
        }

        // Détermine la capacité du sac à dos
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
        reserveMgr = new NodeReservationManager(this);

    }

    /**
     * Méthode appelée lors de la suppression de l'agent.
     * Permet de nettoyer les ressources.
     */
	protected void takeDown(){
		super.takeDown();
	}

    /**
     * Méthode appelée avant la migration de l'agent.
     * Permet de sauvegarder l'état de l'agent.
     */
	protected void beforeMove(){
		super.beforeMove();
	}

    //Méthode appelée après la migration de l'agent.
	protected void afterMove(){
		super.afterMove();
	}


    //Récupère la liste des noms des agents connus.
    public List<String> getListAgentNames() {
        return this.list_agentNames;
    }
    
    //Récupère l'état comportemental actuel de l'agent.
    public AgentBehaviourState getBehaviourState() {
        return this.behaviourState;
    }

    //Définit l'état comportemental de l'agent.
    public void setBehaviourState(AgentBehaviourState behaviourState) {
        this.behaviourState = behaviourState;
    }

    //Calcule l'espace libre dans le sac à dos pour le type de trésor de l'agent.
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

    
    //Récupère la capacité totale du sac à dos de l'agent.
    public int getMyBackPackTotalSpace() {
        return backPackTotalSpace;
    }

    //Récupère le niveau de crochetage de l'agent.
    public int getMyLockpickLevel() {
        return lockpick;
    }

    //Récupère le niveau de force de l'agent.
    public int getMyStrengthLevel() {
        return strength;
    }


    /*
     * --- METHODES D'EXPLORATION --- 
     */

     
    //Initialise la représentation de la carte.
    public void initMapRepresentation() {
        this.myMap = new MapMoreRepresentation();
    }

     //Vérifie si l'exploration est terminée.
    public boolean getExplorationComplete() {
        return this.exploCompleted;
    }

    //Définit si l'exploration est terminée.
    public void setExplorationComplete(boolean b) {
        this.exploCompleted = b;
    }

    //Marque l'exploration comme terminée.
    public void markExplorationComplete() {
        this.exploCompleted = true;
    }

    // ---

    
    //Récupère le chemin actuel de l'agent.
    public List<String> getCurrentPath() {
        return this.currentPath;
    }

    //Définit le chemin actuel de l'agent.
    public void setCurrentPath(List<String> path) {
        this.currentPath = path;
    }

    //Efface le chemin actuel de l'agent.
    public void clearCurrentPath() {
        this.currentPath.clear();
    }

    // ---
    
    //Récupère le nœud cible actuel de l'agent.
    public String getTargetNode() {
        return this.targetNode;
    }
    
    // Définit le nœud cible de l'agent.
    public void setTargetNode(String nodeId) {
        this.targetNode = nodeId;
    }

    //Définit le nœud cible à partir du chemin actuel.
    public void setTargetNodeFromCurrentPath() {
        this.targetNode = this.currentPath.isEmpty() ? null : this.currentPath.remove(0);
    }



    /*
     * --- METHODES DE TOPOLOGIE ---
     */

     //Récupère la représentation de la carte de l'agent.
    public MapMoreRepresentation getMyMap() {
        return this.myMap;
    }

   //Récupère les informations topologiques des autres agents.
    public OtherAgentsTopology getOtherAgentsTopology() {
        return this.otherAgentsTopology;
    }



    /*
     * --- METHODES DE TRESORS ---
     */

     //Récupère les observations de trésors de l'agent.
    public TreasureObservations getMyTreasures() {
        return this.myTreasures;
    }



    /*
     * --- METHODES DE CHARACTERISTIQUES ---
     */

     //Récupère les caractéristiques des autres agents.
    public OtherAgentsCharacteristics getOtherAgentsCharacteristics() {
        return this.otherAgentsCharacteristics;
    }



    /*
     * --- METHODES DE COMMUNICATION ---
     */


    //Récupère l'historique des messages de topologie.
    public Map<Integer, TopologyMessage> getTopologyMessageHistory() {
        return this.topologyMessageHistory;
    }



    //Récupère l'agent cible pour la communication.
    public String getTargetAgent() {
        return this.targetAgent;
    }

    //Définit l'agent cible pour la communication.
    public void setTargetAgent(String agentName) {
        this.targetAgent = agentName;
    }

    //Récupère le nœud cible de l'agent cible.
    public String getTargetAgentNode() {
        return this.targetAgentNode;
    }

     //Définit le nœud cible de l'agent cible.
    public void setTargetAgentNode(String nodeId) {
        this.targetAgentNode = nodeId;
    }
    //Récupère le délai d'attente pour les comportements.
    public int getBehaviourTimeoutMills() {
        return this.behaviourTimeoutMills;
    }
    // Définit le délai d'attente pour les comportements.
    public void setBehaviourTimeoutMills(int ackTimeoutMills) {
        this.behaviourTimeoutMills = ackTimeoutMills;
    }
    //Récupère les étapes de communication.
    public Map<COMMUNICATION_STEP, Boolean> getCommunicationSteps() {
        return this.communicationSteps;
    }
    //Définit les étapes de communication.
    public void setCommunicationSteps(Map<COMMUNICATION_STEP, Boolean> communicationSteps) {
        this.communicationSteps = communicationSteps;
    }


    /*
     * --- METHODES DE FLOODING PROTOCOL ---
     */ 
    //Récupère l'état de flooding de l'agent.
    public FloodingState getFloodingState() {
        return this.floodingState;
    }
    //Définit l'état de flooding de l'agent.
    public void setFloodingState(FloodingState floodingState) {
        this.floodingState = floodingState;
    }



    /*
     * --- METHODES DE POINT DE RENDEZ-VOUS ---
     */
    //Récupère le point de rendez-vous actuel.
    public String getMeetingPoint() {
        return this.meetingPointId;
    }
    //Définit le point de rendez-vous actuel.
    public void setMeetingPoint(String meetingPointId) {
        this.meetingPointId = meetingPointId;
    }



    /*
     * --- METHODES DES COALITIONS ---
     */
    //Récupère les coalitions des agents.
    public AgentsCoalition getCoalitions() {
        return this.coalitions;
    }
    //Définit les coalitions des agents.
    public void setCoalitions(AgentsCoalition coalitions) {
        this.coalitions = coalitions;
    }
    //Récupère le temps de début de la mission.
    public long getStartMissionMillis() {
        return this.startMissionMillis;
    }
    //Définit le temps de début de la mission.
    public void startMissionMillis() {
        this.startMissionMillis = System.currentTimeMillis();
    }
    //Récupère le délai d'expiration pour la collecte.
    public long getCollectTimeoutMillis() {
        return this.collectTimeoutMillis;
    }



    /*
     * --- METHODES DE DEADLOCK ---
     */
    //Récupère la solution de nœud d'interblocage.
    public String getDeadlockNodeSolution() {
        return this.deadlockNodeSolution;
    }
    //Définit la solution de nœud d'interblocage.
    public void setDeadlockNodeSolution(String nodeId) {
        this.deadlockNodeSolution = nodeId;
    }
    //Récupère la réservation de nœud actuelle.
    public NodeReservation getNodeReservation() {
        return this.nodeReservation;
    }
    //Définit la réservation de nœud actuelle.
    public void setNodeReservation(NodeReservation nodeReservation) {
        this.nodeReservation = nodeReservation;
    }
    //Récupère le délai d'expiration pour l'interblocage.
    public long getDeadlockTimeoutMillis() {
        return this.deadlockTimeoutMillis;
    }



    /*
     * --- GETTER DU TYPE DE L'AGENT ---
     */
    //Récupère le type d'agent.
    public AgentType getAgentType() {
        return this.agentType;
    }
}
