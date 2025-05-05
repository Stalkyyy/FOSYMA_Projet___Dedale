package eu.su.mas.dedaleEtu.mas.msgObjects;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import eu.su.mas.dedaleEtu.mas.managers.CommunicationManager.COMMUNICATION_STEP;

public class CommunicationStepMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;

    Set<COMMUNICATION_STEP> setOfSteps;

    public CommunicationStepMessage() {
        this.setOfSteps = new HashSet<>();
    }

    // ==============================================================================

    public Set<COMMUNICATION_STEP> getSteps() {
        return setOfSteps;
    }

    public void addStep(COMMUNICATION_STEP step) {
        setOfSteps.add(step);
    }
}
