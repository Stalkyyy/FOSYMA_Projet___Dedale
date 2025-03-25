package eu.su.mas.dedaleEtu.mas.behaviours;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.GeneralAgent;
import jade.core.behaviours.OneShotBehaviour;

public class EndExplorationBehaviour extends OneShotBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;

    private GeneralAgent agent;
    
    public EndExplorationBehaviour(final AbstractDedaleAgent myagent) {
        super(myagent);
        this.agent = (GeneralAgent) myagent;
    }

    @Override
    public void action() {
        System.out.println("Finito !");
        
    }
}