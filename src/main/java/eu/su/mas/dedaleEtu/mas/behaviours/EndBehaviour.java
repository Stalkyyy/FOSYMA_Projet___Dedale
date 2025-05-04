package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.List;
import java.util.Random;

import eu.su.mas.dedale.env.gs.GsLocation;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentType;
import jade.core.behaviours.SimpleBehaviour;

public class EndBehaviour extends SimpleBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;

    private AbstractAgent agent;
    private int exitCode = -1;
    
    public EndBehaviour(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    @Override
    public void action() {
        if (agent.getAgentType() == AgentType.SILO)
            return;

        List<String> availableNodes = agent.visionMgr.nodeAvailableList();
        if (availableNodes == null || availableNodes.isEmpty())
            return;

        Random random = new Random();
        String randomNode = availableNodes.get(random.nextInt(availableNodes.size()));

        agent.doWait(1000);

        agent.moveTo(new GsLocation(randomNode));
    }

    @Override 
    public int onEnd() {
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            agent.getMyTreasures().getTreasures().forEach((key, value) -> System.out.println(key + " " + value));

        return exitCode;
    }

    @Override
    public boolean done() {
        return false;
    }
}