package simulation;

import it.polito.appeal.traci.Edge;

/**
 * Created by szezso on 2017.02.03..
 */
public class SumoVehicle {

    private Edge src;
    private Edge dst;
    private String id;

    public String getId() {
        return id;
    }

    public Edge getSrc() {
        return src;
    }

    public void setSrc(Edge src) {
        this.src = src;
    }

    public Edge getDst() {
        return dst;
    }

    public void setDst(Edge dst) {
        this.dst = dst;
    }

    public SumoVehicle(Edge src, Edge dst, String id) {
        this.src = src;
        this.dst = dst;
        this.id = id;
    }

    public void changeSrcDst(){
        Edge tmp = src;
        src = dst;
        dst = tmp;
    }
}
