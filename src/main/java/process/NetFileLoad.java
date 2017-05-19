package process;

import javafx.concurrent.Task;
import net.sf.jsi.Rectangle;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.geom.Point2D;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Created by szezso on 2017.04.04..
 */
public class NetFileLoad extends Task<MyRTree> {

    private String configfilepath;
    private MyRTree rTree;
    private List<EdgeElement> typeElements= new ArrayList<>();

    public NetFileLoad(String configFilePath) {
        this.configfilepath = configFilePath;
    }

    @Override
    protected MyRTree call() throws Exception {
        rTree = new MyRTree();

        HashMap<String, String> map = new HashMap<>();
        //TODO config file
        File storedFile = new File("saved_maps/netFiles.dat");


        try {
            long kezd = System.nanoTime();
            File inputFile = new File(configfilepath);

            if(storedFile.exists()){
                FileInputStream fis = new FileInputStream(storedFile);
                ObjectInputStream ois = new ObjectInputStream(fis);
                map = (HashMap) ois.readObject();
                ois.close();
                fis.close();

                for (Map.Entry<String,  String> entry: map.entrySet()){
                    System.out.println("Key: " + entry.getKey() +" Value: " + entry.getValue());
                }
            }

            String checkSum = calculateCheckSum(inputFile);

            if(map.containsKey(checkSum)){
                String savedMap = map.get(checkSum);
                File mapFile = new File(savedMap);
                if(mapFile.exists()) {
                    FileInputStream fis = new FileInputStream(mapFile);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    rTree = (MyRTree) ois.readObject();
                    ois.close();
                    fis.close();
                }
                else{
                    readAndSerializeTree(checkSum, inputFile,map);
                }
            }
            else{
                readAndSerializeTree(checkSum, inputFile,map );
            }



            long veg = System.nanoTime();

            long difference = veg - kezd;
            System.out.println("Tree size " + rTree.getRTree().size());
            System.out.println("Total execution time: " +
                    String.format("%d mil", TimeUnit.NANOSECONDS.toMillis(difference)));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rTree;
    }

    private void createTree(File inputFile) throws ParserConfigurationException, IOException, SAXException {
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
                    if(containsType!= null) {
                        actEdge.setAllow(containsType.getAllow());
                    }
                }
                if(edge.hasAttribute("disallow")){
                    actEdge.setAllow(edge.getAttribute("disallow"));
                }
                else{
                    if(containsType!= null) {
                        actEdge.setDisallow(containsType.getDisallow());
                    }
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

            Rectangle rectangle = new Rectangle((float) point1.getX(), (float) point1.getY(),
                    (float) point2.getX(), (float) point2.getY());
            actedge.setRectangle(rectangle);

            rTree.add(actedge, rectangle);
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


    /**
     *
     * @param inputFile
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * src https://www.mkyong.com/java/how-to-generate-a-file-checksum-value-in-java/
     */
    private String calculateCheckSum(File inputFile) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        FileInputStream fis = new FileInputStream(inputFile);
        byte[] dataBytes = new byte[1024];

        int nread = 0;

        while ((nread = fis.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        };

        byte[] mdbytes = md.digest();

        //convert the byte to hex format
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < mdbytes.length; i++) {
            sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        String checkSum = sb.toString();
        System.out.println("Digest(in hex format):: " + checkSum);

        return checkSum;
    }


    private void readAndSerializeTree(String checkSum, File inputFile, HashMap<String,String> map) throws IOException, SAXException, ParserConfigurationException {
        createTree(inputFile);
        String fileName = serializeTree();
        map.put(checkSum,fileName);
        serializeMapsCatalogue(map);

        for (Map.Entry<String,  String> entry: map.entrySet()){
            System.out.println("Key: " + entry.getKey() +" Value: " + entry.getValue());
        }
    }

    private void serializeMapsCatalogue(HashMap<String,String> map) throws IOException {
        serializeMyObject(map, "saved_maps/netFiles.dat");
    }

    private  String serializeTree() throws IOException {
        LocalDateTime currentTime = LocalDateTime.now();
        String fileName = "saved_maps/".concat(currentTime.toString() + ".dat");
        serializeMyObject(rTree, fileName);
        return fileName;
    }


    private <T extends Serializable> void serializeMyObject(T serializeObject, String fileName) throws IOException {
        File file = new File(fileName);
        file.getParentFile().mkdirs();
        file.createNewFile(); // if file already exists will do nothing

        FileOutputStream fos =
                new FileOutputStream(fileName);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(serializeObject);
        oos.close();
        fos.close();
    }
}
