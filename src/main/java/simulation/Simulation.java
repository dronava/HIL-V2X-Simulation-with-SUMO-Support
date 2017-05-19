package simulation;


import it.polito.appeal.traci.*;
import process.EdgeConvertType;
import process.EdgeSearch;
import process.MyRTree;

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
    private int delay;
    private String configfile;
    private Map<String, ConcurrentLinkedQueue<String>> gpsfakeManagmentQueues = new HashMap<String, ConcurrentLinkedQueue<String>>();
    private Queue<Task> taskQueue;
    private DateFormat dateFormatdate = new SimpleDateFormat("ddMMyy");
    private DateFormat dateFormattime = new SimpleDateFormat("HHmmss.SS");
    private Map<String,SumoVehicle> vehicleSrcDst = new HashMap<>();


    private Queue<EdgeConvertType> pointToEdgeQueue = new ConcurrentLinkedQueue<>();
    private Queue<EdgeConvertType> resultEdge = new ConcurrentLinkedQueue<>();
    private EdgeSearch edgeSearch;

    private boolean closeSumo;

    public Simulation(String configfile, int delay, Map<String,
            ConcurrentLinkedQueue<String>> gpsfakeManagmentQueues, Queue<Task> taskQueue, MyRTree edgeRTree){
        this.configfile = configfile;
        this.delay = delay;
        this.gpsfakeManagmentQueues = gpsfakeManagmentQueues;
        this.taskQueue = taskQueue;
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


            do {
                if(conn.getVehicleRepository().getAll().size() > vehicleSrcDst.size() ){
                    Map<String,Vehicle> vehicles = conn.getVehicleRepository().getAll();
                    storeVehicleSrcDst(vehicles);
                }

                Task task;
                //TODO erre jobb feltetelt adni, igy beragadhat
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

               /* /
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
                }*/

                Date date = new Date();
                String actdate = dateFormatdate.format(date);
                String acttime = dateFormattime.format(date);

                for (Map.Entry<String, ConcurrentLinkedQueue<String>> managmentQueue : gpsfakeManagmentQueues.entrySet()) {
                    String vehicleID = managmentQueue.getKey();
                    ConcurrentLinkedQueue<String> queue = managmentQueue.getValue();
                    Vehicle actVehicle = conn.getVehicleRepository().getByID(vehicleID);

                    if(actVehicle == null) {
                        System.out.println("A " + vehicleID + " null értékű");
                        gpsfakeManagmentQueues.remove(vehicleID);
                    }
                    else {

                        List<Edge> actroute = actVehicle.getCurrentRoute();


                        Edge actedge = actVehicle.getCurrentEdge();
                        // System.out.println("ASD");
                        //System.out.println("Act edge: " + actedge.toString());
                        if (actroute.get(actroute.size() - 1) == actVehicle.getCurrentEdge()) {

                            vehicleSrcDst.get(vehicleID).changeSrcDst();



                            System.out.println("A végére ért a kör elindul " + actVehicle.getCurrentEdge() + " -ról ide: " + vehicleSrcDst.get(vehicleID).getDst());
                            if (vehicleSrcDst.get(vehicleID).getDst() != null)
                                actVehicle.changeTarget(vehicleSrcDst.get(vehicleID).getDst());

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

                            //TODO megirni a VehicleClass-t traciban, nem lehet lekerni
                            String vehicleType = "passenger";
                            System.out.println("Vehcile type (new dst)" + vehicleType);
                            System.out.println("Param: " + latlon.getX() + "," + latlon.getY());
                            //posCartesian = new Point2D.Double(2533.27,3901.24);

                            String vehicleNewDstEdge = edgeSearch.getEdgeFromCoordinate(posCartesian, vehicleType);
                            if(vehicleNewDstEdge !="") {
                                SumoVehicle modifyDst = vehicleSrcDst.get(task.getId());
                                modifyDst.setDst(conn.getEdgeRepository().getByID(vehicleNewDstEdge));

                                actVehicle.changeTarget(modifyDst.getDst());
                                vehicleSrcDst.put(task.getId(), modifyDst);
                            }
                            else{
                                System.out.println("Not found Edge");
                            }
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

    private void storeVehicleSrcDst(Map<String, Vehicle> vehicles) throws IOException {
        Set<Map.Entry<String, Vehicle>> vehicleSet= vehicles.entrySet();
        for(Map.Entry<String,Vehicle> entry: vehicleSet){
            if(!vehicleSrcDst.containsKey(entry.getKey())){
                List<Edge> actroute=  entry.getValue().getCurrentRoute();
                System.out.println("Src: " +entry.getKey() +" - " + actroute.get(0));
                System.out.println("Dst: " + actroute.get(actroute.size()-1));
                vehicleSrcDst.put(entry.getKey(), new SumoVehicle(actroute.get(0),actroute.get(actroute.size()-1)));
            }

        }
    }
}
