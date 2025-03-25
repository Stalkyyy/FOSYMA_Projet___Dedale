package eu.su.mas.dedaleEtu.mas.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.gs.GsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.msgObjects.TopologyObservations;
import eu.su.mas.dedaleEtu.mas.knowledge.NodeObservations;
import eu.su.mas.dedaleEtu.mas.knowledge.OtherAgentsObservations;
import eu.su.mas.dedaleEtu.mas.knowledge.OtherAgentsTopology;



public class GeneralAgent extends AbstractDedaleAgent {
    
    protected static final long serialVersionUID = -7969469610241668140L;

    protected List<String> list_agentNames = new ArrayList<>();

    protected MapRepresentation myMap = null; 
    protected NodeObservations myObservations = new NodeObservations();

    protected OtherAgentsTopology otherAgentsTopology = new OtherAgentsTopology();
    protected OtherAgentsObservations otherAgentsObservations = new OtherAgentsObservations();

    // Historique des messages pour la TOPO/OBS de forme : Map<msgId, <receiverName, <Topology, NodeObservation>>>
    protected Map<Integer, TopologyObservations> sentMessagesHistory_TOPO_OBS = new HashMap<>();
    protected AtomicInteger messageIdCounter = new AtomicInteger();

    protected boolean exploFinished = false;

    //priorité de l'agent
    private int priority;

    private String nextNodeId;


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
