package eu.su.mas.dedaleEtu.mas.behaviours;

import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import jade.core.behaviours.SimpleBehaviour;

public class End extends SimpleBehaviour {
    
    private static final long serialVersionUID = -568863390879327961L;

    private AbstractAgent agent;
    private int exitCode = -1;
    
    public End(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }

    // Ne fait rien, mais peut être utilisée pour signaler la fin d'un processus.
    @Override
    public void action() {
        // System.out.println(agent.getLocalName() + " a fini !~");
        return;
    }

    // Retourne le code de sortie
    @Override 
    public int onEnd() {
        // Affiche des informations de debug si nécessaire.
        if (agent.getLocalName().compareTo("DEBUG_AGENT") == 0)
            System.out.println(this.getClass().getSimpleName() + " -> " + exitCode);

        return exitCode;
    }

    // Verifie si le comportement est terminé.
    @Override
    public boolean done() {
        return false;
    }
}