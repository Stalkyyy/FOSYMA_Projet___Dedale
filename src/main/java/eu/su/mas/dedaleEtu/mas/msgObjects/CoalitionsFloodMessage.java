package eu.su.mas.dedaleEtu.mas.msgObjects;

import java.io.Serializable;

import eu.su.mas.dedaleEtu.mas.knowledge.AgentsCoalition;

public class CoalitionsFloodMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private AgentsCoalition agentsCoalition; // Coalition des agents incluse dans le message.

    // Initialise un message de flooding contenant les informations sur les coalitions des agents.
    public CoalitionsFloodMessage(AgentsCoalition agentsCoalition) {
        this.agentsCoalition = agentsCoalition;
    }

    // Retourne les coalitions des agents contenues dans le message.
    public AgentsCoalition getCoalitions() {
        return agentsCoalition;
    }    
}
