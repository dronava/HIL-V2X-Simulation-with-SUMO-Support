package process;

import net.sf.jsi.Point;

import java.awt.geom.Point2D;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by szezso on 2017.04.01..
 */
public class EdgeSearch {

    private EdgeElement edgeElement = new EdgeElement();
    Map<String, Double> typeMap = new HashMap<String, Double>();

    private Queue<EdgeConvertType> pointToEdgeQueue;
    private Queue<EdgeConvertType> resultEdge;

    MyRTree rTree;


    public EdgeSearch(MyRTree rTree){
        this.rTree = rTree;
    }

    public String getEdgeFromCoordinate(Point2D destination, String vehicleType){
        long kezdKeres = System.nanoTime();

        Point dest = new Point((float) destination.getX(), (float) destination.getY());

        List<EdgeElement> results = new ArrayList<>();
        final int[] nearest = {0};
        final float[] nearestval = {Float.MAX_VALUE};
        // a procedure whose execute() method will be called with the results
        rTree.getRTree().nearest(dest,      // the point for which we want to find nearby rectangles
                i -> {
                    if((rTree.getNode(i).getAllow().contains(vehicleType) || rTree.getNode(i).getAllow().isEmpty())
                            && !rTree.getNode(i).getDisallow().contains(vehicleType)){
                        results.add(rTree.getNode(i));
                        if(rTree.getNode(i).getRectangle().distance(dest) < nearest[0]){
                            nearest[0] = results.size()-1;
                            nearestval[0] = rTree.getNode(i).getRectangle().distance(dest);
                        }
                    }
                    System.out.println("Rectangle nearest " + i + " " + rTree.getNode(i).getId() + " dist: "
                            + rTree.getNode(i).getRectangle().distance(dest));
                    return true;              // return true here to continue receiving results
                },Float.MAX_VALUE);

        long vegKeres = System.nanoTime();
        long differenceKeres = vegKeres-kezdKeres;

        System.out.println("Total execution time Search: " +
                String.format("%d mil", TimeUnit.NANOSECONDS.toMillis(differenceKeres)));

        if(results.size() > 0){
            System.out.println("Search result: " + results.get(nearest[0]).getId());
            return results.get(nearest[0]).getId();
        }
        else
            return "";
    }
}