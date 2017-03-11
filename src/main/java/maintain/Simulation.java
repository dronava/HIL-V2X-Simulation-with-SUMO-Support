package maintain;


import it.polito.appeal.traci.Edge;
import it.polito.appeal.traci.PositionConversionQuery;
import it.polito.appeal.traci.SumoTraciConnection;
import it.polito.appeal.traci.Vehicle;

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
    private Map<String, ConcurrentLinkedQueue<String>> gpsfakeManagmentQueues = new HashMap<String, ConcurrentLinkedQueue<String>>();
    private DateFormat dateFormatdate = new SimpleDateFormat("ddMMyy");
    private DateFormat dateFormattime = new SimpleDateFormat("HHmmss.SS");
    private List<SumoVehicle> vehicleScrDst = new ArrayList<SumoVehicle>();

    private boolean closeSumo;

    public Simulation(String configfile, int delay, Map<String, ConcurrentLinkedQueue<String>> gpsfakeManagmentQueues){
        this.configfile = configfile;
        this.delay = delay;
        this.gpsfakeManagmentQueues = gpsfakeManagmentQueues;
        closeSumo = true;
    }

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
                //System.out.println("Time: "+ conn.getCurrentSimTime());
                Date date = new Date();
                String actdate = dateFormatdate.format(date);
                String acttime = dateFormattime.format(date);

                for (Map.Entry<String, ConcurrentLinkedQueue<String>> managmentQueue : gpsfakeManagmentQueues.entrySet()) {
                    String vehicleID = managmentQueue.getKey();
                    ConcurrentLinkedQueue<String> queue = managmentQueue.getValue();
                    Vehicle actVehicle = conn.getVehicleRepository().getByID(vehicleID);

                    if(actVehicle == null)
                        System.out.println("A " + vehicleID + " null értékű");

                    List<Edge> actroute=  actVehicle.getCurrentRoute();


                    Edge actedge = actVehicle.getCurrentEdge();
                   // System.out.println("ASD");
                    //System.out.println("Act edge: " + actedge.toString());
                    if(actroute.get(actroute.size()-1) == actVehicle.getCurrentEdge()){

                        Edge newdst = src;

                        src = dst;
                        dst = newdst;

                        System.out.println("A végére ért a kör elindul " + actVehicle.getCurrentEdge() +" -ról ide: "+ newdst);
                        System.out.println("New src: "+ src + " new dst: "+ dst );
                        if(newdst != null)
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


                conn.nextSimStep();
            }while(closeSumo);

            conn.close();
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        System.out.println("SUMO shutdown");
    }
}
