package eu.su.mas.dedaleEtu.mas.managers;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.TreasureObservations;
import eu.su.mas.dedaleEtu.mas.msgObjects.TreasureFloodMessage;
import eu.su.mas.dedaleEtu.mas.utils.TreasureInfo;

public class TreasureManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private AbstractAgent agent;

    public TreasureManager(AbstractAgent agent) {
        this.agent = agent;
    }

    // ========================================================================

    public void update(String nodeId, List<Couple<Observation, String>> attributes) {
        if (attributes == null) {
            agent.getMyTreasures().updateObservations(nodeId, null);
            return;
        }

        Observation type = null;
        int quantity = 0;
        boolean isLockOpen = false;
        int requiredLockPick = 0;
        int requiredStrength = 0;

        for (Couple<Observation, String> attribute : attributes) {
            Observation obs = attribute.getLeft();
            String info = attribute.getRight();

            if (obs == Observation.GOLD || obs == Observation.DIAMOND) {
                type = obs;
                quantity = Integer.parseInt(info);
            }

            else if (obs == Observation.LOCKSTATUS) {
                isLockOpen = info.equals("1");
            }

            else if (obs == Observation.LOCKPICKING) {
                requiredLockPick = Integer.parseInt(info);
            }

            else if (obs == Observation.STRENGH) {
                requiredStrength = Integer.parseInt(info);
            }
        }

        TreasureInfo treasure = (type == null || quantity <= 0) ? null : new TreasureInfo(nodeId, type, quantity, isLockOpen, requiredLockPick, requiredStrength);
        agent.getMyTreasures().updateObservations(nodeId, treasure);
    }

    public void updateTimestamp(String nodeId) {
        this.agent.getMyTreasures().updateTimestamp(nodeId);
    }

    // ========================================================================

    public Map<String, TreasureInfo> getTreasures() {
        return agent.getMyTreasures().getTreasures();
    }

    public TreasureInfo treasureInNode(String nodeId) {
        return agent.getMyTreasures().getTreasures().get(nodeId);
    }

    // ========================================================================

    public void merge(TreasureObservations obs) {
        agent.getMyTreasures().mergeObservations(obs);
    }

    public void merge(TreasureFloodMessage TFM) {
        agent.getMyTreasures().mergeObservations(TFM);
    }

    public TreasureObservations merge(TreasureObservations obs1, TreasureObservations obs2) {
        TreasureObservations merged = obs1.copy();
        merged.mergeObservations(obs2);
        return merged;
    }
}
