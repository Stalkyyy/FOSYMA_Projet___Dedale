package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.Random;
// import java.util.Random;
import java.util.Set;

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

        Random random = new Random();

        Set<String> treasuresToVerify = agent.getTreasuresToVerify();

        if (treasuresToVerify.contains(agent.getCurrentPosition().getLocationId())) {
            treasuresToVerify.remove(agent.getCurrentPosition().getLocationId());
        }

        // Génère un déplacement aléatoire.
        if (agent.getTargetNode() == null) {
            if (!treasuresToVerify.isEmpty()) {
                String treasureId = treasuresToVerify.iterator().next();
                agent.moveMgr.setCurrentPathTo(treasureId);
            } else {
                agent.moveMgr.setCurrentPathToRandomNode();
            }
        }
        
        if (!treasuresToVerify.isEmpty() && random.nextDouble() < 0.20) {
            agent.moveMgr.setCurrentPathToRandomNode();
        }

        // Met à jour les informations sur les trésors visibles.
        agent.visionMgr.updateTreasure();

        // Incrémente le compteur de temps passé après un deadlock.
        // agent.moveMgr.incrementeTimeDeadlock();

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

            // On reset pour éviter les boucles infinies d'interblocage.
            if (agent.moveMgr.getFailedMoveCount() > 20) {
                agent.setNodeReservation(null);
                agent.comMgr.setLettingHimPass(false);
            }
            
        }
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
