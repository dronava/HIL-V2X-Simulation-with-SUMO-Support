package process;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Rectangle;
import javafx.concurrent.Task;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import rx.observables.BlockingObservable;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by szezso on 2017.04.01..
 */
public class EdgeSearch {

    private Document netFile;

    private EdgeElement edgeElement = new EdgeElement();
    Map<String, Double> typeMap = new HashMap<String, Double>();

    private Queue<EdgeConvertType> pointToEdgeQueue;
    private Queue<EdgeConvertType> resultEdge;

    RTree<EdgeElement, Rectangle> rTree;

    public volatile boolean isRunning;

    public EdgeSearch(RTree<EdgeElement, Rectangle> rTree){
        this.rTree = rTree;
        //this.pointToEdgeQueue = pointToEdgeQueue;
        //this.resultEdge = resultEdge;
        //netFile.getDocumentElement().normalize();
        isRunning = false;
    }

    public String getEdgeFromCoordinate(Point2D destination, String vehicleType){
        //1974.35,4293.80
        long kezdKeres = System.nanoTime();
        //Iterable<Entry<EdgeElement, Rectangle>> results =
                //rTree.search(Geometries.point(destination.getX(), destination.getY())).toBlocking().toIterable();

        BlockingObservable<Entry<EdgeElement, Rectangle>> results = rTree.search(Geometries.point(destination.getX(),destination.getY()))
                // filter for allowed vehicle type
                .filter(entry ->(entry.value().getAllow().contains(vehicleType) && !entry.value().getDisallow().contains(vehicleType))).toBlocking();

        Iterator<Entry<EdgeElement,Rectangle>> iter = results.toIterable().iterator();

        while(iter.hasNext()) {
            Entry<EdgeElement,Rectangle> act =iter.next();
            System.out.println("Found: " +act.value().getId());
        }

        long vegKeres = System.nanoTime();


        long differenceKeres = vegKeres-kezdKeres;
        System.out.println("Total execution time Search: " +
                String.format("%d mil", TimeUnit.NANOSECONDS.toMillis(differenceKeres)));



        Entry<EdgeElement, Rectangle> result = results.firstOrDefault(null);
        if(result != null){
            return result.value().getId();
        }
        else
            return "";
    }

   /* @Override
    public void run() {
        isRunning = true;
        while(isRunning){
            try {
                EdgeConvertType target = null;

                synchronized (this) {
                    while (pointToEdgeQueue.isEmpty() )
                        this.wait();

                    // Get the next work item off of the queue
                    System.out.println("New Search");
                    target = pointToEdgeQueue.poll();
                }

                // Process the work item
                String edge = getEdgeID(target.getPosition());
                EdgeConvertType result = new EdgeConvertType(target.getVehicleID(), edge);
                resultEdge.offer(result);
            }
            catch ( InterruptedException ie ) {
                ie.printStackTrace();
                break;  // Terminate
            }
        }
    }

    private String getEdgeID(Point2D target){
        NodeList nListType = netFile.getElementsByTagName("type");
        for (int temp = 0; temp < nListType.getLength(); temp++) {
            Node nNode = nListType.item(temp);
            Element typeElement = (Element)nNode;
            if(typeElement.hasAttribute("width")) {
                String id = typeElement.getAttribute("id");
                double width = Double.parseDouble(typeElement.getAttribute("width"));
                typeMap.put(id, width);
            }
        }

        NodeList nListEdge = netFile.getElementsByTagName("edge");
        boolean skipSearch= false;
        for (int temp = 0; temp < nListEdge.getLength(); temp++) {
            Node nNode = nListEdge.item(temp);
            Element edge = (Element) nNode;


            edgeElement.setId(edge.getAttribute("id"));


            if (edge.hasAttribute("type")) {
                edgeElement.setType(edge.getAttribute("type"));
                if (typeMap.containsKey(edgeElement.getType()))
                    edgeElement.setWidth(typeMap.get(edgeElement.getType()));
            }
            if (edge.hasAttribute("width"))
                edgeElement.setWidth(edge.getAttribute("width"));
            if (edge.hasAttribute("shape"))
                edgeElement.setShapes(edge.getAttribute("shape"));
            else {
                NodeList laneList =
                        edge.getElementsByTagName("lane");

                for (int count = 0;
                     count < laneList.getLength(); count++) {
                    Node nodeEdge = laneList.item(count);
                    Element lane = (Element) nodeEdge;

                    double length = 0.0;
                    if(lane.hasAttribute("length"))
                        length = Double.parseDouble(lane.getAttribute("length"));

                    if(lane.hasAttribute("shape")) {
                        String shape = lane.getAttribute("shape");
                        String coordString = shape.split(" ")[0];
                        Point2D firstCoord = new Point2D.Double(Double.parseDouble(coordString.split(",")[0]),
                                Double.parseDouble(coordString.split(",")[1]));
                        if(target.distance(firstCoord)>length){
                            skipSearch =true;
                            break;
                        }
                        edgeElement.setShapes(shape);
                    }


                    if(lane.hasAttribute("type")) {
                        edgeElement.setType(lane.getAttribute("type"));
                        if(typeMap.containsKey(edgeElement.getType()))
                            edgeElement.setWidth(typeMap.get(edgeElement.getType()));
                    }
                    if(lane.hasAttribute("width"))
                        edgeElement.setWidth(lane.getAttribute("width"));


                }

                if(!skipSearch) {
                    boolean find = edgeElement.contains(target);
                    if (find) {

                        System.out.println("Pos: " + target.getX() + "," + target.getY());
                        System.out.println("FOUND!!!! " + edgeElement.getId());
                        return edgeElement.getId();
                    }
                }
                else
                    skipSearch = false;
            }
        }
        return "";
    }*/
}
