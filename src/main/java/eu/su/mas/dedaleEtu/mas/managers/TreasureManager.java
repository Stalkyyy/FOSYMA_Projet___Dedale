package eu.su.mas.dedaleEtu.mas.managers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.TreasureObservations;
import eu.su.mas.dedaleEtu.mas.utils.TreasureInfo;

public class TreasureManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private AbstractAgent agent;

    public TreasureManager(AbstractAgent agent) {
        this.agent = agent;
    }

    // ========================================================================

    public void update(String nodeId, List<Couple<Observation, String>> attributes) {
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

        TreasureInfo treasure = type == null ? null : new TreasureInfo(nodeId, type, quantity, isLockOpen, requiredLockPick, requiredStrength);
        agent.getMyTreasures().updateObservations(nodeId, treasure);
    }

    // ========================================================================

    public TreasureObservations difference(TreasureObservations obs) {
        return agent.getMyTreasures().diffObservations(obs);
    }

    public TreasureObservations difference(TreasureObservations obs1, TreasureObservations obs2) {

        TreasureObservations difference = new TreasureObservations();

        if (obs2 == null) {
            difference.getTreasures().putAll(new HashMap<>(obs1.getTreasures()));
            difference.getTimestamps().putAll(new HashMap<>(obs1.getTimestamps()));
            return difference;
        }

        for (String nodeId : obs1.getTreasures().keySet()) {
            TreasureInfo currentTreasure = obs1.getTreasures().get(nodeId);
            TreasureInfo otherTreasure = obs2.getTreasures().get(nodeId);

            Long currentTimestamp = obs1.getTimestamps().get(nodeId);
            Long otherTimestamp = obs2.getTimestamps().get(nodeId);

            if (otherTreasure == null || currentTimestamp > otherTimestamp) {
                difference.getTreasures().put(nodeId, currentTreasure);
                difference.getTimestamps().put(nodeId, currentTimestamp);
            }
        }

        return difference;
    }

    // ========================================================================

    public void merge(TreasureObservations obs) {
        agent.getMyTreasures().mergeObservations(obs);
    }

    public TreasureObservations merge(TreasureObservations obs1, TreasureObservations obs2) {
        TreasureObservations merged = obs1.copy();
        merged.mergeObservations(obs2);
        return merged;
    }
}
