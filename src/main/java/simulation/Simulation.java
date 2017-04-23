package simulation;


import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Rectangle;
import it.polito.appeal.traci.*;
import org.w3c.dom.Document;
import process.EdgeConvertType;
import process.EdgeSearch;

import java.awt.geom.Point2D;
import java.io.IOException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by szzso on 2017. 01. 21..
 */
public class Simulation implements Runnable {
    private long delay;
    private String configfile;
    private RTree<String, Rectangle> edgeRTree;
    private Map<String, ConcurrentLinkedQueue<String>> gpsfakeManagmentQueues = new HashMap<String, ConcurrentLinkedQueue<String>>();
    private Queue<Task> taskQueue;
    private DateFormat dateFormatdate = new SimpleDateFormat("ddMMyy");
    private DateFormat dateFormattime = new SimpleDateFormat("HHmmss.SS");
    private List<SumoVehicle> vehicleScrDst = new ArrayList<SumoVehicle>();


    private Queue<EdgeConvertType> pointToEdgeQueue = new ConcurrentLinkedQueue<>();
    private Queue<EdgeConvertType> resultEdge = new ConcurrentLinkedQueue<>();
    private EdgeSearch edgeSearch;

    private boolean closeSumo;

    public Simulation(String configfile, int delay, Map<String,
            ConcurrentLinkedQueue<String>> gpsfakeManagmentQueues, Queue<Task> taskQueue, RTree<String, Rectangle> edgeRTree){
        this.configfile = configfile;
        this.delay = delay;
        this.gpsfakeManagmentQueues = gpsfakeManagmentQueues;
        this.taskQueue = taskQueue;
        this.edgeRTree = edgeRTree;
        closeSumo = true;
        edgeSearch = new EdgeSearch(edgeRTree);
    }
    //555;dst;47.473643;19.052962

    @Override
    public void run() {
        try {
            SumoTraciConnection conn = new SumoTraciConnection(
                    configfile,  // config file
                    12345                                  // random seed
            );

            conn.addOption("quit-on-end", "1");
            conn.runServer(true);


            // the first two steps of this simulation have no vehicles.
            conn.nextSimStep();
            conn.nextSimStep();
            System.out.println("ok");
            Vehicle fVehicle = conn.getVehicleRepository().getByID("555");

            List<Edge> actroute1=  fVehicle.getCurrentRoute();
            Edge src = actroute1.get(0);
            Edge dst = actroute1.get(actroute1.size()-1);
            System.out.println("Src: " + src);
            System.out.println("Dst: " + dst);




            do {
                Task task;
                while((task = taskQueue.poll())!= null){
                    if(gpsfakeManagmentQueues.containsKey(task.getId())) {
                        System.out.print("SUMO id: " + task.getId() + "command: " + task.getCommand());
                        for (int i = 0; i < task.getParameter().size(); i++) {
                            System.out.print(task.getParameter().get(i));
                        }
                        System.out.print("\n");

                        creatCommand(task,conn);
                    }
                }

                //TODO erre jobb feltetelt adni, igy beragadhat
                while(!resultEdge.isEmpty()){
                    EdgeConvertType result = resultEdge.poll();
                    if(result.getEdge() != "") {
                        Vehicle actVehicle = conn.getVehicleRepository().getByID(result.getVehicleID());
                        dst = conn.getEdgeRepository().getByID(result.getEdge());
                        actVehicle.changeTarget(dst);

                    }
                    else{
                        System.out.println("Not found the EDGE!!!");
                    }
                }

                Date date = new Date();
                String actdate = dateFormatdate.format(date);
                String acttime = dateFormattime.format(date);

                for (Map.Entry<String, ConcurrentLinkedQueue<String>> managmentQueue : gpsfakeManagmentQueues.entrySet()) {
                    String vehicleID = managmentQueue.getKey();
                    ConcurrentLinkedQueue<String> queue = managmentQueue.getValue();
                    Vehicle actVehicle = conn.getVehicleRepository().getByID(vehicleID);

                    if(actVehicle == null)
                        System.out.println("A " + vehicleID + " null értékű");
                    else {

                        List<Edge> actroute = actVehicle.getCurrentRoute();


                        Edge actedge = actVehicle.getCurrentEdge();
                        // System.out.println("ASD");
                        //System.out.println("Act edge: " + actedge.toString());
                        if (actroute.get(actroute.size() - 1) == actVehicle.getCurrentEdge()) {

                            Edge newdst = src;

                            src = dst;
                            dst = newdst;


                            System.out.println("A végére ért a kör elindul " + actVehicle.getCurrentEdge() + " -ról ide: " + newdst);
                            System.out.println("New src: " + src + " new dst: " + dst);
                            if (newdst != null)
                                actVehicle.changeTarget(newdst);

                        }

                        double speed = actVehicle.getSpeed();
                        double angle = actVehicle.getAngle();

                        Point2D posCartesian = actVehicle.getPosition();
                        PositionConversionQuery pcq = conn.queryPositionConversion();
                        pcq.setPositionToConvert(posCartesian, true);
                        Point2D latlon = pcq.get();

                        Nmea nmea = new Nmea(angle, speed, latlon, actdate, acttime);

                        queue.offer(nmea.getRMC());
                        queue.offer(nmea.getGGA());
                    }
                }


                conn.nextSimStep();
            }while(closeSumo && !conn.isClosed());

            conn.close();
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        System.out.println("SUMO shutdown");
    }

    public void creatCommand(Task task, SumoTraciConnection conn){
        String command = task.getCommand().toLowerCase();
        Vehicle actVehicle = null;
        try {
            Set<String> ids = conn.getVehicleRepository().getIDs();
            if(ids.contains(task.getId())) {
                actVehicle = conn.getVehicleRepository().getByID(task.getId());

                if (actVehicle != null) {
                    switch (command) {
                        case "speed":
                            actVehicle.changeSpeed(Double.valueOf(task.getParameter().get(0)));
                            break;
                        case "dst":
                            System.out.println("Command: dst ");
                            Point2D.Double latlon = new Point2D.Double(Double.valueOf(task.getParameter().get(0)),
                                    Double.valueOf(task.getParameter().get(1)));
                            PositionConversionQuery pcq = conn.queryPositionConversion();
                            pcq.setPositionToConvert(latlon, false);
                            Point2D posCartesian = pcq.get();

                            String vehicleType = actVehicle.getType();
                            System.out.println("Vehcile type (new dst)" + vehicleType);
                            System.out.println("Param: " + latlon.getX() + "," + latlon.getY());

                            String vehicleNewDstEdge = edgeSearch.getEdgeFromCoordinate(posCartesian, vehicleType);
                            //EdgeConvertType target = new EdgeConvertType(task.getId(), posCartesian);


                        /*pointToEdgeQueue.offer(target);
                        if(edgeSearch.isRunning) {
                            synchronized (edgeSearch) {
                                edgeSearch.notify();
                            }
                        }
                        else {
                            edgeSearch = new EdgeSearch(netFileDocument, pointToEdgeQueue, resultEdge);
                            edgeSearch.start();
                        }*/


                            System.out.println("Cartesian pos: " + posCartesian.toString());
                            break;
                    }
                }
            }
            else{
                System.out.println("ID is not in the list: " + task.getId());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
