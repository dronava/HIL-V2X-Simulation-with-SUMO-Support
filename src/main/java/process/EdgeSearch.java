package process;

import javafx.concurrent.Task;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by szezso on 2017.04.01..
 */
public class EdgeSearch extends Task<String> {

    private Point2D target;
    private Document netFile;

    private EdgeElement edgeElement = new EdgeElement();
    Map<String, Double> typeMap = new HashMap<String, Double>();

    public EdgeSearch(Point2D target, Document netFile){
        this.target = target;
        this.netFile = netFile;
        netFile.getDocumentElement().normalize();
    }

    @Override
    protected String call() throws Exception {
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


                    if (lane.hasAttribute("type")) {
                        edgeElement.setType(lane.getAttribute("type"));
                        if (typeMap.containsKey(edgeElement.getType()))
                            edgeElement.setWidth(typeMap.get(edgeElement.getType()));
                    }
                    if (lane.hasAttribute("width"))
                        edgeElement.setWidth(lane.getAttribute("width"));
                    if (lane.hasAttribute("shape"))
                        edgeElement.setShapes(lane.getAttribute("shape"));

                }

                boolean find = edgeElement.contains(target);
                if (find) {

                    System.out.println("Pos: " + target.getX() + "," + target.getY());
                    System.out.println("FOUND!!!! " + edgeElement.getId());
                    return edgeElement.getId();
                }
            }
        }
        return null;
    }
}
