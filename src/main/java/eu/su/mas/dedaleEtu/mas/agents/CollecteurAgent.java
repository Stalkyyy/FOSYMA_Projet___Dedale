package eu.su.mas.dedaleEtu.mas.agents;
import eu.su.mas.dedaleEtu.mas.knowledge.TreasureRepresentation;
import eu.su.mas.dedaleEtu.mas.managers.MovementManager;
import eu.su.mas.dedaleEtu.mas.managers.TopologyManager;

public class CollecteurAgent extends GeneralAgent {
    
    private static final long serialVersionUID = 1L;

    //Initialisation de l'agent collecteur qui recolte les trésors
    private int capaciteSac = 0; //Capacité du sac de l'agent
    private int force; //Force de l'agent
    private int serrurerie; //Serrurerie de l'agent
    private TreasureRepresentation treasure; //Trésor de l'agent

    public CollecteurAgent(String name, int capaciteSac, int force, int serrurerie) {
        super(); //Recupère le constructeur de la classe parent GeneralAgent
        this.capaciteSac = capaciteSac;
        this.force = force;
        this.serrurerie = serrurerie;
    }
    protected void setup(){
        super.setup(); //Appel au setup de la classe parente GeneralAgent

        moveMgr = new MovementManager(this);
        topoMgr = new TopologyManager(this);

    }

}
