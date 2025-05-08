package eu.su.mas.dedaleEtu.mas.behaviours.communication_behaviours.stop_communication_behaviours;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentBehaviourState;
import jade.core.behaviours.OneShotBehaviour;

public class StopCommunication extends OneShotBehaviour {
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    
    public StopCommunication(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }
    // Arrête la communication en cours.
    @Override
    public void action() {
        agent.comMgr.stopCommunication();
    }

    // Détermine le code de sortie en fonction de l'état comportemental de l'agent.
    @Override 
    public int onEnd() {

        // Si l'agent est en mode "flooding", utilise le code de sortie correspondant.
        if (agent.getBehaviourState() == AgentBehaviourState.FLOODING)
            exitCode = agent.getBehaviourState().getExitCode();
        // Si l'agent laisse passer un autre agent, retourne un code spécifique.
        else if (agent.comMgr.getLettingHimPass())
            exitCode = -2;
        // Sinon, utilise le code de sortie par défaut de l'état comportemental.
        else 
            exitCode = agent.getBehaviourState().getExitCode();

        // Affiche des informations de débogage si nécessaire.
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        return exitCode;
    }
}
