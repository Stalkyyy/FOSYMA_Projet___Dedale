package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.su.mas.dedaleEtu.mas.utils.CoalitionInfo;
import eu.su.mas.dedaleEtu.mas.utils.CoalitionInfo.COALITION_ROLES;

public class AgentsCoalition implements Serializable {
    
    private static final long serialVersionUID = 1L;

    // Map associant chaque agent à sa coalition.
    private Map<String, CoalitionInfo> agentsCoalition;
    
    // Initialise une map vide pour les coalitions des agents.
    public AgentsCoalition() {
        this.agentsCoalition = new HashMap<>();
    }

    // Initialise une map avec les noms des agents, sans coalition.
    public AgentsCoalition(List<String> list_agentNames) {
        this.agentsCoalition = new HashMap<>();
        for (String agentName : list_agentNames)
            this.agentsCoalition.put(agentName, null);
    }

    // ==========================================================================
    
    // Retourne la map des coalitions des agents.
    public Map<String, CoalitionInfo> GetAgentsCoalition() {
        return this.agentsCoalition;
    }

    //Définit la map des coalitions des agents.
    public void setAgentsCoalition(Map<String, CoalitionInfo> agentsCoalition) {
        this.agentsCoalition = agentsCoalition;
    }

    // ==========================================================================

    // On suppose que tout le monde a bien été initialisé ici.
    public void updateCoalition(CoalitionInfo coalition) {
        Map<String, COALITION_ROLES> roles = coalition.getAgentsRole();
        for (String agentName : roles.keySet())
            this.agentsCoalition.put(agentName, coalition);
    }

    // ==========================================================================

    // Supprime toutes les associations entre agents et coalitions.
    public void reset() {
        for (String agentName : agentsCoalition.keySet())
            this.agentsCoalition.put(agentName, null);
    }

    // ==========================================================================

    // Affiche les informations sur chaque agent et sa coalition.
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AgentsCoalition {\n");
        
        if (agentsCoalition.isEmpty()) {
            sb.append("  Aucune coalition définie.\n");
        } else {
            for (Map.Entry<String, CoalitionInfo> entry : agentsCoalition.entrySet()) {
                String agentName = entry.getKey();
                CoalitionInfo coalition = entry.getValue();
                
                sb.append("  Agent: ").append(agentName).append(" -> ");
                
                if (coalition == null) {
                    sb.append("Aucune coalition assignée\n");
                } else {
                    sb.append("Coalition pour le trésor au noeud : ").append(coalition.getNodeId())
                    .append(" (Type: ").append(coalition.getType()).append(")\n");
                    
                    // Afficher tous les membres de cette coalition avec leurs rôles
                    sb.append("    Membres de la coalition :\n");
                    for (Map.Entry<String, COALITION_ROLES> roleEntry : coalition.getAgentsRole().entrySet()) {
                        sb.append("      - ").append(roleEntry.getKey())
                        .append(" comme ").append(roleEntry.getValue()).append("\n");
                    }
                    
                    // Afficher les exigences du trésor
                    sb.append("    Exigences du trésor :\n")
                    .append("      - LockPick: ").append(coalition.getRequiredLockPick())
                    .append(coalition.isLockOpen() ? " (déjà ouvert)" : "").append("\n")
                    .append("      - Strength: ").append(coalition.getRequiredStrength()).append("\n");
                }
            }
        }
        
        sb.append("}");
        return sb.toString();
    }
}
