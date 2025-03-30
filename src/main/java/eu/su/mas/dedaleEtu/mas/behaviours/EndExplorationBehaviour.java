package eu.su.mas.dedaleEtu.mas.behaviours;

import eu.su.mas.dedaleEtu.mas.agents.MyAgent;
import jade.core.behaviours.OneShotBehaviour;

public class EndExplorationBehaviour extends OneShotBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;

    // private GeneralAgent agent;
    
    public EndExplorationBehaviour(final MyAgent myagent) {
        super(myagent);
        // this.agent = myagent;
    }

    @Override
    public void action() {
        System.out.println("Finito !");
        
    }
}