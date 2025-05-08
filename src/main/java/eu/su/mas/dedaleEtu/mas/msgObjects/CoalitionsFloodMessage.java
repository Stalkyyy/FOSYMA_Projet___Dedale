package eu.su.mas.dedaleEtu.mas.msgObjects;

import java.io.Serializable;

import eu.su.mas.dedaleEtu.mas.knowledge.AgentsCoalition;

public class CoalitionsFloodMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private AgentsCoalition agentsCoalition;

    public CoalitionsFloodMessage(AgentsCoalition agentsCoalition) {
        this.agentsCoalition = agentsCoalition;
    }

    public AgentsCoalition getCoalitions() {
        return agentsCoalition;
    }    
}
