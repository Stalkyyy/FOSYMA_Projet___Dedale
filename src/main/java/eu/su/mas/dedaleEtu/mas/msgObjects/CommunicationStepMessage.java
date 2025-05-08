package eu.su.mas.dedaleEtu.mas.msgObjects;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import eu.su.mas.dedaleEtu.mas.managers.CommunicationManager.COMMUNICATION_STEP;

public class CommunicationStepMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;

    Set<COMMUNICATION_STEP> setOfSteps; // Ensemble des étapes de communication incluses dans le message.

    // Initialise un message contenant un ensemble vide d'étapes de communication
    public CommunicationStepMessage() {
        this.setOfSteps = new HashSet<>();
    }

    // ==============================================================================

    // Retourne l'ensemble des étapes de communication contenues dans le message.
    public Set<COMMUNICATION_STEP> getSteps() {
        return setOfSteps;
    }

    // Ajoute une étape de communication au message.
    public void addStep(COMMUNICATION_STEP step) {
        setOfSteps.add(step);
    }
}
