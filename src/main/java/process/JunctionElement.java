package process;

import java.io.Serializable;
import java.util.List;

public class JunctionElement implements Serializable {

    private String junctionId;
    List<String> incLanes;

    public String getJunctionId() {
        return junctionId;
    }

    public void setJunctionId(String junctionId) {
        this.junctionId = junctionId;
    }

    public List<String> getIncLanes() {
        return incLanes;
    }

    public void setIncLanes(List<String> incLanes) {
        this.incLanes = incLanes;
    }

    public JunctionElement(String junctionId, List<String> incLanes) {
        this.junctionId = junctionId;
        this.incLanes = incLanes;
    }
}
