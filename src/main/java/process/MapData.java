package process;

import net.sf.jsi.Point;
import net.sf.jsi.Rectangle;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapData implements Serializable {

    private static final long serialVersionUID = -7604766932017737115L;
    private static MapData mapDataInstance = new MapData();

    private MyRTree rTree;
    private Map<String, EdgeElement> hashMap;

    public MyRTree getRTree() {
        return rTree;
    }

    public Map<String, EdgeElement> getHashMap(){
        return hashMap;
    }

    public static MapData getInstance() {
        return mapDataInstance;
    }

    private MapData() {
        rTree = new MyRTree();
        hashMap = new HashMap<>();
    }

    /**
     * https://www.journaldev.com/1377/java-singleton-design-pattern-best-practices-examples#serialization-and-singleton
     * https://stackoverflow.com/questions/35632581/serialization-with-singleton-design-pattern
     * @return
     */
    protected Object readResolve() {
        mapDataInstance.setHashMap(getHashMap());
        mapDataInstance.setrTree(getrTree());
        return mapDataInstance;
    }

    public synchronized EdgeElement getEdgeByName(String edgeName) {
        return hashMap.get(edgeName);
    }


    public synchronized String getEdgeNameByCoordinate(Point2D destination, String vehicleType) {
        return rTree.getNearest(new Point((float) destination.getX(),
                        (float) destination.getY()),
                vehicleType);
    }

    public synchronized List<String> getNearestNEdgeByCoordinate(Point2D coordinate, int nearestN) {
        return rTree.getNearestN(new Point((float) coordinate.getX(), (float) coordinate.getY()), nearestN);
    }

    public synchronized void setEdgeCongested(String edge, EdgeCongested edgeCongested) {
        hashMap.get(edge).setEdgeCongested(edgeCongested);
    }

    public EdgeCongested getEdgeCongestionState(String edge){
        return hashMap.get(edge).getEdgeCongested();
    }

    public void addEdge(EdgeElement edge, Rectangle rectangle) {

        rTree.add(edge, rectangle);
        hashMap.put(edge.getId(),edge);
    }

    public MyRTree getrTree() {
        return rTree;
    }

    public void setrTree(MyRTree rTree) {
        this.rTree = rTree;
    }

    public void setHashMap(Map<String, EdgeElement> hashMap) {
        this.hashMap = hashMap;
    }
}
