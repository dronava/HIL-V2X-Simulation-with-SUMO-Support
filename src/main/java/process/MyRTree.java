package process;

import net.sf.jsi.Point;
import net.sf.jsi.Rectangle;
import net.sf.jsi.SpatialIndex;
import net.sf.jsi.rtree.RTree;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by szezso on 2017.04.30..
 */
public class MyRTree implements Serializable {
    static final long SerialVersionUID = -4862926644813433707L;
    private SpatialIndex rTree;
    private List<EdgeElement> rTreeNodes;

    public EdgeElement getNode(int i) {
        return rTreeNodes.get(i);
    }

    public void addNode(EdgeElement edge) {
        rTreeNodes.add(edge);
    }

    public void addRTreeElement(Rectangle rectangle) {
        rTree.add(rectangle, rTreeNodes.size() - 1);
    }

    public SpatialIndex getRTree() {
        return rTree;
    }

    public void add(EdgeElement edge, Rectangle rectangle) {
        rTreeNodes.add(edge);
        rTree.add(rectangle, rTreeNodes.size() - 1);
    }

    public List<String> getNearestN(Point coordinate, int nearestN){
        List<EdgeElement> results = new ArrayList<>();
        rTree.nearestN(
                coordinate,      // the point for which we want to find nearby rectangles
                i-> {
                    results.add(getNode(i));
                    return true;
                    },
                nearestN,                            // the number of nearby rectangles to find
                Float.MAX_VALUE               // Don't bother searching further than this. MAX_VALUE means search everything
        );

        return results.stream().map(i->i.getId()).collect(Collectors.toList());
    }

    public String getNearest(Point destination, String vehicleType){
        long kezdKeres = System.nanoTime();


        List<EdgeElement> results = new ArrayList<>();
        final int[] nearest = {0};
        final float[] nearestval = {Float.MAX_VALUE};
        // a procedure whose execute() method will be called with the results
        rTree.nearest(destination,      // the point for which we want to find nearby rectangles
                i -> {
                    if((getNode(i).getAllow().contains(vehicleType) || getNode(i).getAllow().isEmpty())
                            && !getNode(i).getDisallow().contains(vehicleType)){
                        results.add(getNode(i));
                        if(getNode(i).getRectangle().distance(destination) < nearest[0]) {
                            nearest[0] = results.size() - 1;
                            nearestval[0] = getNode(i).getRectangle().distance(destination);
                        }

                    }
                    System.out.println("Rectangle nearest " + i + " " + getNode(i).getId() + " dist: "
                            + getNode(i).getRectangle().distance(destination) + " nearest: " + nearest[0]);
                    return true;              // return true here to continue receiving results
                },Float.MAX_VALUE);

        //EdgeElement result = results.stream().min(Comparator.comparing(i->i.getRectangle().distance(dest))).orElse(null);
        EdgeElement result = results.get(nearest[0]);

        long vegKeres = System.nanoTime();
        long differenceKeres = vegKeres-kezdKeres;

        System.out.println("Total execution time Search: " +
                String.format("%d mil", TimeUnit.NANOSECONDS.toMillis(differenceKeres)));

        if(result != null){
            System.out.println("Search result: " + result.getId());
            return result.getId();
        }
        else
            return "";
    }

    public MyRTree() {
        rTree = new RTree();
        rTreeNodes = new ArrayList<>();
        rTree.init(null);
    }
}
