package process;

import net.sf.jsi.Point;
import net.sf.jsi.Rectangle;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapData implements Serializable {

    private static final long serialVersionUID = -7604766932017737115L;
    private static MapData mapDataInstance = new MapData();

    private MyRTree rTree;
    private Map<String, EdgeElement> edgeMap;
    private Map<String, JunctionElement> junctionMap;

    public MyRTree getRTree() {
        return rTree;
    }

    public Map<String, EdgeElement> getEdgeMap(){
        return edgeMap;
    }

    public static MapData getInstance() {
        return mapDataInstance;
    }

    public MyRTree getrTree() {
        return rTree;
    }

    public void setrTree(MyRTree rTree) {
        this.rTree = rTree;
    }

    public void setEdgeMap(Map<String, EdgeElement> edgeMap) {
        this.edgeMap = edgeMap;
    }

    public Map<String, JunctionElement> getJunctionMap() {
        return junctionMap;
    }

    public void setJunctionMap(Map<String, JunctionElement> junctionMap) {
        this.junctionMap = junctionMap;
    }

    private MapData() {
        rTree = new MyRTree();
        edgeMap = new ConcurrentHashMap<>();
        junctionMap = new ConcurrentHashMap<>();
    }

    /**
     * https://www.journaldev.com/1377/java-singleton-design-pattern-best-practices-examples#serialization-and-singleton
     * https://stackoverflow.com/questions/35632581/serialization-with-singleton-design-pattern
     * @return
     */
    protected Object readResolve() {
        mapDataInstance.setEdgeMap(getEdgeMap());
        mapDataInstance.setrTree(getrTree());
        return mapDataInstance;
    }

    public synchronized EdgeElement getEdgeByName(String edgeName) {
        return edgeMap.get(edgeName);
    }


    public synchronized String getEdgeNameByCoordinate(Point2D destination, String vehicleType) {
        return rTree.getNearest(new Point((float) destination.getX(),
                        (float) destination.getY()),
                vehicleType);
    }

    public synchronized List<String> getNearestNEdgeByCoordinate(Point2D coordinate, int nearestN) {
        return rTree.getNearestN(new Point((float) coordinate.getX(), (float) coordinate.getY()), nearestN);
    }

    public synchronized JunctionElement getJunctionById(String id){
        return junctionMap.get(id);
    }

    public synchronized void setEdgeCongested(String edge, EdgeCongested edgeCongested) {
        edgeMap.get(edge).setEdgeCongested(edgeCongested);
    }

    public EdgeCongested getEdgeCongestionState(String edge){
        return edgeMap.get(edge).getEdgeCongested();
    }

    public void addEdge(EdgeElement edge, Rectangle rectangle) {

        rTree.add(edge, rectangle);
        edgeMap.put(edge.getId(),edge);
    }

    public void addJunction(JunctionElement junction){
        junctionMap.put(junction.getJunctionId(), junction);
    }


}
