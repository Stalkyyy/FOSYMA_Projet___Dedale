package eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.flooding_behaviours.sharing_plans;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import jade.core.behaviours.OneShotBehaviour;

public class SendCoalitions extends OneShotBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;

    public SendCoalitions(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

        @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1; 

        if (!agent.floodMgr.isRoot())
            return;

        // On calcule les meilleures coalitions d'après l'agent root.
        agent.coalitionMgr.calculateBestCoalitions();

        System.out.println(agent.getCoalitions().toString());
    }

    @Override 
    public int onEnd() {
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        return exitCode;
    }

}
