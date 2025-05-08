package eu.su.mas.dedaleEtu.mas.behaviours.flooding_behaviours.end_flood;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentBehaviourState;
import jade.core.behaviours.OneShotBehaviour;

public class EndFlood extends OneShotBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;

    public EndFlood(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    // Termine le protocole de flooding et détermine l'état suivant de l'agent.
    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;        

        // Si l'agent est root, affiche les coalitions calculées pour debug.
        if (agent.floodMgr.isRoot())
            System.out.println("\n" + agent.coalitionMgr.toString() + "\n");

        // Désactive le protocole de flooding.
        agent.floodMgr.deactivateFlooding();

        // Détermine l'état suivant de l'agent en fonction des coalitions.
        if (agent.coalitionMgr.getCoalition() != null)
            agent.setBehaviourState(AgentBehaviourState.COLLECT_TREASURE); // Passe à la collecte des trésors.
        else
            agent.setBehaviourState(AgentBehaviourState.RE_EXPLORATION); // Reprend l'exploration.

        // Démarre le chronomètre pour la mission.
        agent.startMissionMillis();

        // Définit le code de sortie en fonction de l'état actuel de l'agent.
        exitCode = agent.getBehaviourState().getExitCode(); 

        System.out.println(agent.getLocalName() + " finished the flood ! -> " + agent.getBehaviourState());
    }

    // Retourne le code de sortie.
    @Override 
    public int onEnd() {
        // Affiche des informations de debug si nécessaire.
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        return exitCode;
    }
}
