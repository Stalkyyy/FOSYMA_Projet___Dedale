package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.Random;

import eu.su.mas.dedale.env.gs.GsLocation;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import jade.core.behaviours.OneShotBehaviour;

public class RandomWalk extends OneShotBehaviour {

    private static final long serialVersionUID = -568863390879327961L;
    private int exitCode = -1;

    private AbstractAgent agent;
    
    public RandomWalk(final AbstractAgent myagent) {
        super(myagent);
        this.agent = myagent;
    }
    // Gère le déplacement aléatoire de l'agent dans l'environnement.
    @Override
    public void action() {

        // On réinitialise les attributs si besoin.
        exitCode = -1;

        // Génère un déplacement aléatoire.
        Random random = new Random();
        if (agent.getTargetNode() == null || random.nextDouble() < 0.25) {
            agent.moveMgr.setCurrentPathToRandomNode();
        }

        // Met à jour les informations sur les trésors visibles.
        agent.visionMgr.updateTreasure();

        // Incrémente le compteur de temps passé en deadlock.
        agent.moveMgr.incrementeTimeDeadlock();

        // Tente de se déplacer vers le nœud cible.
        boolean moved = agent.moveTo(new GsLocation(agent.getTargetNode()));
        if (moved) {
            // Si le déplacement est réussi, réinitialise le compteur d'échecs et met à jour le chemin.
            agent.moveMgr.resetFailedMoveCount();
            agent.setTargetNodeFromCurrentPath();
        } 

        else {
            // Si le déplacement échoue, incrémente le compteur d'échecs.
            agent.moveMgr.incrementFailedMoveCount();
        }

        // Vérifie si le temps alloué à la mission est écoulé.
        if (System.currentTimeMillis() - agent.getStartMissionMillis() > agent.getCollectTimeoutMillis())
            exitCode = 1;
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
