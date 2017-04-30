package process;

import javafx.concurrent.Task;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Rectangle;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by szezso on 2017.04.04..
 */
public class NetFileLoad extends Task<RTree<EdgeElement, Rectangle>> {

    private String configfilepath;
    private RTree<EdgeElement, Rectangle> rTree;
    private List<EdgeElement> typeElements= new ArrayList<>();

    public NetFileLoad(String configFilePath) {
        this.configfilepath = configFilePath;
    }

    @Override
    protected RTree<EdgeElement, Rectangle> call() throws Exception {
        rTree = RTree.create();
        double width = 3.2;

        try {
            long kezd = System.nanoTime();
            File inputFile = new File(configfilepath);
            DocumentBuilderFactory dbFactory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            NodeList nListType = doc.getElementsByTagName("type");
            for (int temp = 0; temp < nListType.getLength(); temp++) {
                Node nNode = nListType.item(temp);
                Element typeElement = (Element) nNode;
                EdgeElement actType = new EdgeElement();

                if (typeElement.hasAttribute("id")) {
                    actType.setId(typeElement.getAttribute("id"));
                }
                if (typeElement.hasAttribute("width")) {
                    actType.setWidth(typeElement.getAttribute("width"));
                }
                if(typeElement.hasAttribute("allow")){
                    actType.setAllow(typeElement.getAttribute("allow"));
                }
                if(typeElement.hasAttribute("disallow")){
                    actType.setDisallow(typeElement.getAttribute("disallow"));
                }

                typeElements.add(actType);
            }


            NodeList nListEdge = doc.getElementsByTagName("edge");
            for (int temp = 0; temp < nListEdge.getLength(); temp++) {
                Node nNode = nListEdge.item(temp);
                Element edge = (Element) nNode;
                EdgeElement actEdge = new EdgeElement();

                if (!edge.hasAttribute("function")) {

                    String id = edge.getAttribute("id");
                    actEdge.setId(id);
                    double actwidth = width;
                    EdgeElement containsType = null;

                    if (edge.hasAttribute("type")) {
                        final String type = edge.getAttribute("type");
                        actEdge.setType(type);
                        containsType = typeElements.stream().filter(t->t.getId().equals(type)).findFirst().orElse(null);
                        if ( containsType!= null){
                            actEdge.setWidth(containsType.getWidth());
                        }
                    }
                    if (edge.hasAttribute("width")) {
                        actEdge.setWidth(edge.getAttribute("width"));
                    }
                    if(edge.hasAttribute("allow")){
                        actEdge.setAllow(edge.getAttribute("allow"));
                    }
                    else{
                        actEdge.setAllow(containsType.getAllow());
                    }
                    if(edge.hasAttribute("disallow")){
                        actEdge.setAllow(edge.getAttribute("disallow"));
                    }
                    else{
                        actEdge.setDisallow(containsType.getDisallow());
                    }
                    if (edge.hasAttribute("shape")) {
                        String[] shapes = edge.getAttribute("shape").split(" ");
                        storeRectangle(convertShapesToDouble(shapes), actEdge);
                    } else {
                        NodeList laneList = edge.getElementsByTagName("lane");



                        if (laneList.getLength() > 1) {
                            Point2D[][] lanePoints = new Point2D[laneList.getLength()][];

                            //TODO lane szelesseg, edge szelesseg meres

                            double laneWidth = 0.0;
                            for (int count = 0; count < laneList.getLength(); count++) {
                                Node nodeEdge = laneList.item(count);
                                Element lane = (Element) nodeEdge;

                                if(lane.hasAttribute("width")){
                                    laneWidth +=  Double.parseDouble(lane.getAttribute("width"));
                                }

                                if (lane.hasAttribute("shape")) {
                                    String[] shapes = lane.getAttribute("shape").split(" ");
                                    lanePoints[count] = convertShapesToDouble(shapes);
                                }
                            }
                            actEdge.setWidth(laneWidth);

                            Point2D[] edgePoints = new Point2D[lanePoints[0].length];

                            //System.out.println("Lane ID " + id + " LANE count " + lanePoints.length + " Shape point " + lanePoints[0].length);
                            for (int j = 0; j < lanePoints[0].length; j++) {
                                edgePoints[j] =
                                        new Point2D.Double((lanePoints[0][j].getX() + lanePoints[lanePoints.length - 1][j].getX()) / 2.0,
                                                (lanePoints[0][j].getY() + lanePoints[lanePoints.length - 1][j].getY()) / 2.0);
                            }
                            storeRectangle(edgePoints, actEdge);
                        } else {
                            for (int count = 0; count < laneList.getLength(); count++) {
                                Node nodeEdge = laneList.item(count);
                                Element lane = (Element) nodeEdge;

                                if (lane.hasAttribute("shape")) {
                                    String[] shapes = lane.getAttribute("shape").split(" ");
                                    if (edge.hasAttribute("width"))
                                        actEdge.setWidth(Double.parseDouble(edge.getAttribute("width")));

                                    //String idLane = lane.getAttribute("id");

                                    storeRectangle(convertShapesToDouble(shapes), actEdge);
                                }
                            }
                        }
                    }
                }
            }

            long veg = System.nanoTime();

            long difference = veg - kezd;
            System.out.println("Tree size " + rTree.size());
            System.out.println("Total execution time: " +
                    String.format("%d mil", TimeUnit.NANOSECONDS.toMillis(difference)));
            rTree.visualize(1366, 768).save(new File("target/treeMapEdgeEnhanced.png"), "PNG");


        } catch (Exception e) {
            e.printStackTrace();
        }

        return rTree;
    }

    private Point2D[] convertShapesToDouble(String[] shapes) {
        Point2D[] points = new Point2D[shapes.length];

        for (int i = 0; i < shapes.length; i++) {
            points[i] = new Point2D.Double(Double.parseDouble(shapes[i].split(",")[0]),
                    Double.parseDouble(shapes[i].split(",")[1]));
        }
        return points;
    }

    private void storeRectangle(Point2D[] points, EdgeElement actedge) {
        for (int i = 0; i < points.length - 1; i++) {
            Point2D point1 = newPoint(points[i], points[i + 1], -1, actedge.getWidth()); // ((j%2)== 0?-1:1)));
            Point2D point2 = newPoint(points[i + 1], points[i], -1, actedge.getWidth());

            Rectangle rectangle;
            rectangle = Geometries.rectangle(point2.getX(), point2.getY(), point1.getX(), point1.getY());

            rTree = rTree.add(actedge, rectangle);

            if (point1.getX() > point2.getX() && point1.getY() > point2.getY()) {
                rectangle = Geometries.rectangle(point2.getX(), point2.getY(), point1.getX(), point1.getY());

                rTree = rTree.add(actedge, rectangle);
            } else if (point1.getX() < point2.getX() && point1.getY() < point2.getY()) {
                rectangle = Geometries.rectangle(point1.getX(), point1.getY(), point2.getX(), point2.getY());
                rTree = rTree.add(actedge, rectangle);
            } else {
                Point2D tmp1 = point1;
                Point2D tmp2 = point2;
                point1 = newPoint(points[i], points[i + 1], 1, actedge.getWidth()); // ((j%2)== 0?-1:1)));
                point2 = newPoint(points[i + 1], points[i], 1, actedge.getWidth());
                if (point1.getX() > point2.getX() && point1.getY() > point2.getY()) {
                    rectangle = Geometries.rectangle(point2.getX(), point2.getY(), point1.getX(), point1.getY());

                    rTree = rTree.add(actedge, rectangle);
                } else if (point1.getX() < point2.getX() && point1.getY() < point2.getY()) {
                    rectangle = Geometries.rectangle(point1.getX(), point1.getY(), point2.getX(), point2.getY());
                    rTree = rTree.add(actedge, rectangle);
                }
            }


        }
    }
    
    private Point2D newPoint(Point2D a, Point2D b, int heading, double width) {

        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);
        dx /= dist;
        dy /= dist;
        double x = a.getX() + (width) * dy * heading;
        double y = a.getY() + (width) * dx * -1.0 * heading;


        //double x = refpoint.getX() + Math.sin(angle) * heading * width;
        //double y = refpoint.getY() + Math.cos(angle) * heading * width;
        return new Point2D.Double(x, y);
    }
}
