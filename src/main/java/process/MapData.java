package process;

import net.sf.jsi.Point;
import net.sf.jsi.Rectangle;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapData implements Serializable {

    private static MapData mapDataInstance = null;

    private MyRTree rTree;
    private Map<String, EdgeElement> hashMap;

    public MyRTree getRTree() {
        return rTree;
    }

    public static MapData getInstance() {
        if (mapDataInstance == null) {
            mapDataInstance = new MapData();
        }
        return mapDataInstance;
    }

    private MapData() {
        rTree = new MyRTree();
        hashMap = new HashMap<>();
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
    }
}
