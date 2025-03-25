package eu.su.mas.dedaleEtu.mas.msgObjects;

import java.io.Serializable;
import java.util.List;

public class DeadlockInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> myPath;
    private int priority;

    public DeadlockInfo(List<String> myPath, int priority) {
        this.myPath = myPath;
        this.priority = priority;
    }

    public List<String> getMyPath() {
        return myPath;
    }

    public int getPriority() {
        return priority;
    }
}
