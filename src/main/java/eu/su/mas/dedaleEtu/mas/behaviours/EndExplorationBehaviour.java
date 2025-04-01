package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.List;
import java.util.Random;

import eu.su.mas.dedale.env.gs.GsLocation;
import eu.su.mas.dedaleEtu.mas.agents.MyAgent;
import jade.core.behaviours.OneShotBehaviour;

public class EndExplorationBehaviour extends OneShotBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;

    private MyAgent agent;
    
    public EndExplorationBehaviour(final MyAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    @Override
    public void action() {
        List<String> availableNodes = agent.obsMgr.nodeAvailableList();
        if (availableNodes == null || availableNodes.isEmpty())
            return;

        Random random = new Random();
        String randomNode = availableNodes.get(random.nextInt(availableNodes.size()));
        agent.moveTo(new GsLocation(randomNode));
    }
}