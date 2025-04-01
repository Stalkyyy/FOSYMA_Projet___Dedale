package eu.su.mas.dedaleEtu.mas.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dataStructures.tuple.Couple;
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
//Rajout Import Adel Treasure
import eu.su.mas.dedaleEtu.mas.knowledge.TreasureRepresentation;
//Un peu de mal avec l'utilisation des Managers, je te laisse adapter ce que j'ai fait avec les managers. Desolé vraiment
import eu.su.mas.dedale.env.Observation;
import jade.core.AID;
import jade.lang.acl.ACLMessage;




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
    protected int minUpdatesToShare = 25;



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


    //Rajout de Adel : Collection des Trésors (Essai)
    /*public void CollectTreasure(TreasureRepresentation treasure) {
        //Si l'agent (collecteur) a de la place dans son sac, et qu'il a les compétences (serrurerie et/ou force) pour ouvrir le trésor
        if (((AbstractDedaleAgent)this.myAgent).getBackPackFreeSpace() > 0 && (this.serrurerie >= treasure.getSerrurerie() && this.force >= treasure.getForce())) {
        }
    }*/

    
    public int getForce(){
        Set<Couple<Observation, Integer>> expertise = this.getMyExpertise();
        for (Couple<Observation, Integer> expert : expertise){
            if(expert.getLeft() == Observation.STRENGH){
                return expert.getRight(); //Retourne le niveau de force de l'agent
            }
        }
        return 0; // Le niveau de force de l'agent est a 0 
    }

    public int getSerrurerie(){
        Set<Couple<Observation, Integer>> expertise = this.getMyExpertise();
        for (Couple<Observation, Integer> expert : expertise){
            if(expert.getLeft() == Observation.LOCKPICKING){
                return expert.getRight(); //Retourne le niveau de serrurerie de l'agent
            }
        }
        return 0; // Le niveau de serrurerie de l'agent est a 0 
    }



    private void requestHelpForStrength(TreasureRepresentation treasure){
        //Logique pour demander de l'aide a un agent suffisament fort pour prendre le trésor
        this.comMgr.sendMessageToAgents("Besoin d'aide pour un trésor necessitant plus de force.",treasure);
        //Methode appelé n'existe pas, je la met la pour avoir l'idée et l'implémenter plus tard !
    }
    private void requestHelpForLockPicking(TreasureRepresentation treasure){
        //Logique pour demander de l'aide a un agent suffisament fort pour prendre le trésor
        this.comMgr.sendMessageToAgents("Besoin d'aide pour un trésor necessitant plus de serrurerie.",treasure);
        //Methode appelé n'existe pas, je la met la pour avoir l'idée et l'implémenter plus tard !
    }
    private void requestHelpForStrengthAndLockPicking(TreasureRepresentation treasure){
        //Logique pour demander de l'aide a un agent suffisament fort pour prendre le trésor
        this.comMgr.sendMessageToAgents("Besoin d'aide pour un trésor necessitant plus de force ET de serrurerie.",treasure);
        //Methode appelé n'existe pas, je la met la pour avoir l'idée et l'implémenter plus tard !
    }

    public void collectTreasure(TreasureRepresentation treasure){
        System.out.println(this.getLocalName() + "Je vais ouvrir un trésor de type :" + treasure.getType()); //Erreur normalement, il faudrait utiliser la fonction getMytreasureType

        //Récupère l'espace libre dans le sac à dos 
        List<Couple<Observation, Integer>> freeSpace = getBackPackFreeSpace();
        int availableGoldSpace = 0;
        int availableDiamondSpace = 0;

        for (Couple<Observation, Integer> space : freeSpace){
            if (space.getLeft() == Observation.GOLD){
                availableGoldSpace = space.getRight();
            } else if (space.getLeft() == Observation.DIAMOND){
                availableDiamondSpace = space.getRight();
            }
        }

        //Verifier si l'agent a assez de place et également les bonne capacité pour débloquer ou/et ouvrir le coffre 
        if  (this.getForce() >= treasure.getForce() && this.getSerrurerie() >= treasure.getSerrurerie()){
            int quantity_collect = 0; 
            //Vérifie le type de trésor et récupère le trésor selon l'espace du collecteur
            if(treasure.getType() == Observation.GOLD && availableGoldSpace > 0){
                quantity_collect = Math.min(availableGoldSpace, treasure.getQuantity());
                this.updateBackPack(Observation.GOLD,quantity_collect); //n'existe pas encore comme méthode je l'ai juste mise avec de la facon dont je l'ai imaginé
                treasure.setQuantity(treasure.getQuantity() - quantity_collect);
                System.out.println(this.getLocalName() + " - J'ai collecté " + quantity_collect + " bloc d'or.");
            } else if(treasure.getType() == Observation.DIAMOND && availableDiamondSpace > 0){
                quantity_collect = Math.min(availableDiamondSpace, treasure.getQuantity());
                this.updateBackPack(Observation.DIAMOND,quantity_collect);
                treasure.setQuantity(treasure.getQuantity() - quantity_collect);
                System.out.println(this.getLocalName() + " - J'ai collecté " + quantity_collect + " bloc de diamants.");
        }
    }
    else if(this.getSerrurerie() >= treasure.getSerrurerie() && this.getForce() < treasure.getForce()){
        System.out.println(this.getLocalName() + "Je n'ai pas assez de force pour prendre le trésor. Besoin d'aide.");
        this.requestHelpForStrength(treasure);
    } else if(this.getSerrurerie() < treasure.getSerrurerie() && this.getForce() >= treasure.getForce()){
        System.out.println(this.getLocalName() + "Je n'ai pas assez de serrurerie pour ouvrir le trésor. Besoin d'aide.");
        this.requestHelpForLockPicking(treasure);
    } else {
        System.out.println(this.getLocalName() + "Je n'ai pas assez de force et de serrurerie pour m'occuper du trésor. Besoin d'aide.");
        this.requestHelpForStrengthAndLockPicking(treasure);
    }
}