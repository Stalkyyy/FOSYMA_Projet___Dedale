package eu.su.mas.dedaleEtu.mas.knowledge;
//Egalement aussi utilisation d'observation alors que normalement y'a le manager, je te laisse gerer ca également désolé...
import eu.su.mas.dedale.env.Observation;
public class TreasureRepresentation {
    private Observation type; // Type du trésor (Gold, Diamond)
    private int quantity; // Quantité de trésor
    private int serrurerie; // Serrurerie du trésor  
    private int force; // Force necessaire pour ouvrir le trésor

    public TreasureRepresentation(Observation type, int quantity, int serrurerie, int force) {
        this.type = type;
        this.quantity = quantity;
        this.serrurerie = serrurerie;
        this.force = force;
    }
    public Observation getType() {
        return type;
    }
    public void setType(Observation type) {
        this.type = type;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public int getSerrurerie() {
        return serrurerie;
    }
    public void setSerrurerie(int serrurerie) {
        this.serrurerie = serrurerie;
    }
    public int getForce() {
        return force;
    }
    public void setForce(int force) {
        this.force = force;
    }
}