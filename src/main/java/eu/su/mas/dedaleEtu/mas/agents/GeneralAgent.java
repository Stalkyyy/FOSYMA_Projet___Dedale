package eu.su.mas.dedaleEtu.mas.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
