package eu.su.mas.dedaleEtu.mas.managers;

import java.io.Serializable;

import eu.su.mas.dedaleEtu.mas.agents.SiloAgent;

public class AssignmentManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private SiloAgent agent;

    public AssignmentManager(SiloAgent agent) {
        this.agent = agent;
    }

    // ====================================================================

    
}
