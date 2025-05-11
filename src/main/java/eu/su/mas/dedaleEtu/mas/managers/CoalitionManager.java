package eu.su.mas.dedaleEtu.mas.managers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent;
import eu.su.mas.dedaleEtu.mas.agents.AbstractAgent.AgentType;
import eu.su.mas.dedaleEtu.mas.utils.CoalitionInfo;
import eu.su.mas.dedaleEtu.mas.utils.CoalitionInfo.COALITION_ROLES;
import eu.su.mas.dedaleEtu.mas.utils.TreasureInfo;

public class CoalitionManager implements Serializable {
        
    private static final long serialVersionUID = 1L;

    private AbstractAgent agent;


    // Initialise le gestionnaire de coalition avec l'agent donné.
    public CoalitionManager(AbstractAgent agent) {
        this.agent = agent;
    }

    // =================================================================================

    // Retourne la coalition de l'agent actuel.
    public CoalitionInfo getCoalition() {
        return agent.getCoalitions().GetAgentsCoalition().get(agent.getLocalName());
    }

    // Retourne la coalition d'un agent donné.
    public CoalitionInfo getCoalition(String agentName) {
        return agent.getCoalitions().GetAgentsCoalition().get(agentName);
    }
    
    // Vérifie si un agent donné fait partie de la coalition de l'agent actuel.
    public boolean hasAgentInCoalition(String agentName) {
        return agent.getCoalitions().GetAgentsCoalition().get(agent.getLocalName()).hasAgent(agentName);
    }

    // =================================================================================

     // Retourne l'identifiant du trésor de la coalition de l'agent actuel.
     public String getTreasureId() {
        return getCoalition().getNodeId();
    }
    // Retourne l'identifiant du trésor de la coalition d'un agent donné.
    public String getTreasureId(String agentName) {
        return getCoalition(agentName).getNodeId();
    }

    public Set<String> getTreasures() {
        Set<String> nodes = new HashSet<>();
        
        for (CoalitionInfo coalition : agent.getCoalitions().GetAgentsCoalition().values()) {
            if (coalition != null && coalition.getNodeId() != null) {
                nodes.add(coalition.getNodeId());
            }
        }
        
        return nodes;
    }

    public boolean shouldVisitTreasures() {
        if (getCoalition() != null) {
            return false;
        }
        
        for (String agentName : agent.getListAgentNames()) {
            if (getCoalition(agentName) == null && agent.getLocalName().compareTo(agentName) > 0) {
                return false;
            }
        }
        return true;
    }

    // =================================================================================

    // Retourne le rôle de l'agent actuel dans sa coalition.
    public COALITION_ROLES getRole() {
        return getCoalition().getAgentRole(agent.getLocalName());
    }

    // Retourne le rôle d'un agent donné dans sa coalition.
    public COALITION_ROLES getRole(String agentName) {
        return getCoalition(agentName).getAgentRole(agentName);
    }

    // =================================================================================

    // Retourne la quantité de trésor associée à la coalition de l'agent actuel.
    public int getQuantity() {
        return getCoalition().getQuantity();
    }

    // Retourne la quantité de trésor associée à la coalition d'un agent donné.
    public int getQuantity(String agentName) {
        return getCoalition(agentName).getQuantity();
    }

    // =================================================================================

    // Met à jour la coalition avec de nouvelles informations.
    public void updateCoalition(CoalitionInfo coalition) {
        agent.getCoalitions().updateCoalition(coalition);
    }

    // =================================================================================

    // Réinitialise toutes les coalitions.
    public void reset() {
        agent.getCoalitions().reset();
    }

    // =================================================================================

    // Calcule les meilleures coalitions possibles pour les trésors disponibles.
    public void calculateBestCoalitions() {
        reset();

        // On fait un tri décroissant des trésors par quantité
        List<TreasureInfo> sortedTreasures = agent.treasureMgr.sortTreasuresByQuantity();
        
        // On gardera en mémoire chaque agent, trié par leur type
        Set<String> availableCollectors = new HashSet<>();
        Set<String> availableSilos = new HashSet<>();
        Set<String> availableExplorers = new HashSet<>();

        for (String agentName : agent.getListAgentNames()) {

            AgentType type = agent.otherKnowMgr.getAgentType(agentName);
            
            switch (type) {
                case AgentType.COLLECTOR:
                    availableCollectors.add(agentName);
                    break;
                case AgentType.TANKER:
                    availableSilos.add(agentName);
                    break;
                default:
                    availableExplorers.add(agentName);
                    break;
            }
        }


        // On va traiter chaque trésor du plus grand au plus petit, et tenter de lui trouver la meilleure coalition possible, avec "le moins de perte" d'expertise.
        for (TreasureInfo treasure : sortedTreasures) {
            Observation treasureType = treasure.getType();
            
            // Si on n'a plus de silo ou de collecteur du bon type, on s'arrête
            if (availableSilos.isEmpty()) {
                break;
            }
            
            // Chercher le meilleur collecteur pour ce type de trésor
            String bestCollector = null;
            for (String collector : availableCollectors) {
                if (agent.otherKnowMgr.getTreasureType(collector) == treasureType) {
                    bestCollector = collector;
                    break;
                }
            }
            
            // Si aucun collecteur correspondant, passer au trésor suivant
            if (bestCollector == null) {
                continue;
            }
            
            // On essaye de trouver la coalition la plus optimale.
            CoalitionInfo bestCoalition = findOptimCoalition(treasure, bestCollector, availableCollectors, availableSilos, availableExplorers);
            
            if (bestCoalition != null) {
                agent.coalitionMgr.updateCoalition(bestCoalition);
                
                // On met à jour les listes des agents disponibles
                for (Map.Entry<String, COALITION_ROLES> entry : bestCoalition.getAgentsRole().entrySet()) {
                    String agentName = entry.getKey();
                    COALITION_ROLES role = entry.getValue();
                    
                    switch (role) {
                        case COLLECTOR:
                            availableCollectors.remove(agentName);
                            break;
                        case SILO:
                            availableSilos.remove(agentName);
                            break;
                        case HELPER:
                            availableExplorers.remove(agentName);
                            break;
                    }
                }
            }
        }
    }


    // Trouve la coalition optimale pour un trésor donné.
    private CoalitionInfo findOptimCoalition(TreasureInfo treasure, String collector, Set<String> availableCollectors, Set<String> availableSilos, Set<String> availableExplorers) {
        int minSurplus = Integer.MAX_VALUE;
        CoalitionInfo bestCoalition = null;

        // Pour chaque silo, on va calculer qui va le mieux avec le collecteur.
        for (String silo : availableSilos) {
            
            // On calcule les expertises de base avec ce collecteur et ce silo
            int baseLockpick = agent.otherKnowMgr.getLockpick(collector) + agent.otherKnowMgr.getLockpick(silo);
            int baseStrength = agent.otherKnowMgr.getStrength(collector) + agent.otherKnowMgr.getStrength(silo);
            
            // On calcule l'expertise manquante. Si le coffre est ouvert, pas besoin.
            int needLockpick = treasure.getIsLockOpen() ? 0 : Math.max(0, treasure.getRequiredLockPick() - baseLockpick);
            int needStrength = treasure.getIsLockOpen() ? 0 : Math.max(0, treasure.getRequiredStrength() - baseStrength);
            
            // Si ce duo suffit déjà, alors on arrête.
            if (needLockpick == 0 && needStrength == 0) {
                int surplus = (baseLockpick - (treasure.getIsLockOpen() ? 0 : treasure.getRequiredLockPick())) +
                            (baseStrength - (treasure.getIsLockOpen() ? 0 : treasure.getRequiredStrength()));
                
                if (surplus < minSurplus) {
                    minSurplus = surplus;
                    bestCoalition = new CoalitionInfo(treasure);
                    bestCoalition.addCollector(collector);
                    bestCoalition.addSilo(silo);
                }

                continue; // Pas besoin de chercher des explorateurs
            }
            
            // On cherche les meilleurs explorateurs pour compléter si besoin
            Set<String> bestHelpers = findBestHelpers(availableExplorers, availableSilos, availableCollectors, needLockpick, needStrength);
            
            if (bestHelpers != null) {

                int totalLockpick = baseLockpick;
                int totalStrength = baseStrength;
                
                for (String helper : bestHelpers) {
                    totalLockpick += agent.otherKnowMgr.getLockpick(helper);
                    totalStrength += agent.otherKnowMgr.getStrength(helper);
                }
                
                // Vérifier que les expertises demandées sont satisfaits et on calcule le surplus
                if (totalLockpick >= (treasure.getIsLockOpen() ? 0 : treasure.getRequiredLockPick()) && 
                    totalStrength >= (treasure.getIsLockOpen() ? 0 : treasure.getRequiredStrength())) {
                    
                    int surplus = (totalLockpick - (treasure.getIsLockOpen() ? 0 : treasure.getRequiredLockPick())) +
                                (totalStrength - (treasure.getIsLockOpen() ? 0 : treasure.getRequiredStrength()));
                    
                    if (surplus < minSurplus) {
                        minSurplus = surplus;
                        bestCoalition = new CoalitionInfo(treasure);
                        bestCoalition.addCollector(collector);
                        bestCoalition.addSilo(silo);
                        
                        for (String helper : bestHelpers) {
                            bestCoalition.addHelper(helper);
                        }
                    }
                }
            }

            // Si les explorateurs ne suffisent pas, alors là on s'autorise à utiliser plus d'un silo et collecteur. 
            // On prendra toujours le type le plus "libre".
            else {
                Set<String> allHelpers = new HashSet<>(availableExplorers);
                Set<String> copyCollect = new HashSet<>(availableCollectors);
                Set<String> copySilo = new HashSet<>(availableSilos);
                copyCollect.remove(collector);
                copySilo.remove(silo);
    
                int totalLockpick = baseLockpick;
                int totalStrength = baseStrength;
    
                for (String helper : allHelpers) {
                    totalLockpick += agent.otherKnowMgr.getLockpick(helper);
                    totalStrength += agent.otherKnowMgr.getStrength(helper);
                }

                int remainingLockpick = (treasure.getIsLockOpen() ? 0 : treasure.getRequiredLockPick()) - totalLockpick;
                int remainingStrength = (treasure.getIsLockOpen() ? 0 : treasure.getRequiredStrength()) - totalStrength;

                while (remainingLockpick > 0 || remainingStrength > 0) {
                    if (copyCollect.isEmpty() && copySilo.isEmpty())
                    break;

                    String new_agent;
                    if (copyCollect.size() > copySilo.size()) {
                        Iterator<String> it = copyCollect.iterator();
                        new_agent = it.next();
                        it.remove();
                    } else {
                        Iterator<String> it = copySilo.iterator();
                        new_agent = it.next();
                        it.remove();
                    }

                    allHelpers.add(new_agent);
                    remainingLockpick -= agent.otherKnowMgr.getLockpick(new_agent);
                    remainingStrength -= agent.otherKnowMgr.getStrength(new_agent);


                }

                if (remainingLockpick <= 0 && remainingStrength <= 0) {
                    bestCoalition = new CoalitionInfo(treasure);
                    bestCoalition.addCollector(collector);
                    bestCoalition.addSilo(silo);
                    
                    for (String helper : allHelpers) {
                        bestCoalition.addHelper(helper);
                    }
                } 
    
            }
        }
        
        return bestCoalition;
    }

    // Trouve les meilleurs explorateurs pour compléter une coalition.
    private Set<String> findBestHelpers(Set<String> availableExplorers, Set<String> availableSilos, Set<String> availableCollectors, int needLockpick, int needStrength) {
        if (needLockpick == 0 && needStrength == 0) {
            return new HashSet<>(); // Pas besoin d'explorateurs
        }
        
        Set<String> bestHelpers = null;
        int minSurplus = Integer.MAX_VALUE;
        
        int maxHelpers = availableExplorers.size();
        
        for (int size = 1; size <= maxHelpers; size++) {
            List<Set<String>> combinations = generateCombinations(availableExplorers, size);
            
            for (Set<String> combination : combinations) {
                int totalLockpick = 0;
                int totalStrength = 0;
                
                for (String explorer : combination) {
                    totalLockpick += agent.otherKnowMgr.getLockpick(explorer);
                    totalStrength += agent.otherKnowMgr.getStrength(explorer);
                }
                
                if (totalLockpick >= needLockpick && totalStrength >= needStrength) {
                    int surplus = (totalLockpick - needLockpick) + (totalStrength - needStrength);
                    
                    if (surplus < minSurplus) {
                        minSurplus = surplus;
                        bestHelpers = new HashSet<>(combination);
                    }
                }
            }
            
            if (bestHelpers != null) {
                break;
            }
        }
        
        return bestHelpers;
    }
    
    // Génère toutes les combinaisons possibles d'un ensemble donné.
    private List<Set<String>> generateCombinations(Set<String> set, int k) {
        List<Set<String>> result = new ArrayList<>();
        generateCombinationsHelper(new ArrayList<>(set), k, 0, new HashSet<>(), result);
        return result;
    }

    // Génère toutes les combinaisons possibles d'un ensemble donné de manière récursive.
    private void generateCombinationsHelper(List<String> elements, int k, int startIndex, Set<String> currentCombination, List<Set<String>> result) {
        if (currentCombination.size() == k) {
            result.add(new HashSet<>(currentCombination));
            return;
        }

        for (int i = startIndex; i < elements.size(); i++) {
            currentCombination.add(elements.get(i));
            generateCombinationsHelper(elements, k, i + 1, currentCombination, result);
            currentCombination.remove(elements.get(i));
        }
    }
}
