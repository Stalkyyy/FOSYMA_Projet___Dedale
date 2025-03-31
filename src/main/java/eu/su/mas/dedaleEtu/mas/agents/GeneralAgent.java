package eu.su.mas.dedaleEtu.mas.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.gs.GsLocation;
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



abstract class GeneralAgent extends AbstractDedaleAgent {

    // --- ATTRIBUTS GENERAUX ---
    protected static final long serialVersionUID = -7969469610241668140L;
    protected List<String> list_agentNames = new ArrayList<>();
    protected int priority = 0;


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
    protected Map<Integer, TopologyMessage> topologyMessageHistory = new HashMap<>();
    protected Map<Integer, CharacteristicsMessage> characteristicsMessageHistory = new HashMap<>();
    

    // --- ATTRIBUTS DES AUTRES AGENTS ---
    protected OtherAgentsCharacteristics otherAgentsCharacteristics = new OtherAgentsCharacteristics();
    protected OtherAgentsObservations otherAgentsObservations = new OtherAgentsObservations();
    protected OtherAgentsTopology otherAgentsTopology = new OtherAgentsTopology();

    protected Map<String, Integer> pendingUpdatesCount = new HashMap<>() ;
    protected int minUpdatesToShare = 15;


    //priorité de l'agent
    private int priority;

    private String nextNodeId;

    /*
     * --- METHODES GENERALES ---
     */

    protected void setup() {
        super.setup();
        this.priority = generatePriority();
    }


    private int generatePriority () {
        return this.getLocalName().hashCode();
    }

    private int getPriority() {
        return this.priority;
    }

    public String getNextNodeId() {
        return this.nextNodeId;
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


    public void handleDeadLock(List<GeneralAgent> agents) {
        GeneralAgent myPriority = this;
        for (GeneralAgent agent : agents) {
            if (agent.getPriority() > myPriority.getPriority()) {
                this.doWait(1000);
            }
        }   if(myPriority == this) {
               Location current_Pos = this.getCurrentPosition();
               if (nextNodeId == null) {
                List<String> path = this.myMap.getShortestPathToClosestOpenNode(current_Pos.getLocationId()); 
                //Si pas de noeud ouvert disponible
                if (path.isEmpty()) {
                    return;
                }
                //envoyer le chemin de l'agent qui a + de priorité a l'autre agent :agent.merge(path,this.myObservations); 
                // Le 2eme agent change de chemin en fonction du chemin de l'agent qui a + de priorité : 
                // Le 1er agent va vers le noeud ou il devait aller

                nextNodeId = path.get(0);

                for (GeneralAgent agent : agents) {
                    this.myMap.merge(path, this.myObservations);
            }
             this.moveTo(new GsLocation(nextNodeId));
            } else {
                Location current_Pos = this.getCurrentPosition();
                List <String> path = this.myMap.getShortestPathToClosestOpenNode(current_Pos.getLocationId());
                if (path.isEmpty())
                    return;
            }
            nextNodeId = path.get(0);
        
        // Vérifier que le prochain nœud n'est pas le même que celui des autres agents
        for (GeneralAgent agent : agents){
            String agentNextNodeId = agent.getNextNodeId();
            if (nextNodeId.equals(agentNextNodeId)) {
                 // Si le prochain nœud est le même, attendre
                this.doWait(1000);
            }
        }
        
        this.moveTo(new GsLocation(nextNodeId));

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
