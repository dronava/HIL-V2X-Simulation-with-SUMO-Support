package maintain;


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

    private boolean closeSumo;

    public Simulation(String configfile, long delay, Map<String, ConcurrentLinkedQueue<String>> gpsfakeManagmentQueues){
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
            conn.runServer(true);

            // the first two steps of this simulation have no vehicles.
            conn.nextSimStep();
            conn.nextSimStep();
            System.out.println("ok");

            do {
                Date date = new Date();
                String actdate = dateFormatdate.format(date);
                String acttime = dateFormattime.format(date);

                for (Map.Entry<String, ConcurrentLinkedQueue<String>> managmentQueue : gpsfakeManagmentQueues.entrySet()) {
                    String vehicleID = managmentQueue.getKey();
                    ConcurrentLinkedQueue<String> queue = managmentQueue.getValue();
                    Vehicle actVehicle = conn.getVehicleRepository().getByID(vehicleID);

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

                Thread.sleep(delay);
                conn.nextSimStep();
            }while(closeSumo);

            conn.close();

            //it.polito.appeal.traci.Vehicle aVehicle  = conn.getVehicleRepository().getByID("555");

           // Collection<it.polito.appeal.traci.Vehicle> vehicles = conn.getVehicleRepository().getAll().values();

           // it.polito.appeal.traci.Vehicle aVehicle = vehicles.iterator().next();






            /*Point2D pos2d = aVehicle.getPosition();
            PositionConversionQuery pcq = conn.queryPositionConversion();
            pcq.setPositionToConvert(pos2d, true);
            Point2D latlon = pcq.get();
            System.out.println("Pos: "+latlon.getX()+ ", "+latlon.getY() );
            System.out.println("Vehicle " + aVehicle
                    + " will traverse these edges: "
                    + aVehicle.getCurrentRoute());

            Nmea nmea = new Nmea(angle, speed, latlon, mydate, mytime);

            System.out.println(nmea.newGGA());
            System.out.println(nmea.newRMC());
            int index=0;
            do {
                index++;
                it.polito.appeal.traci.Vehicle aVehiclem  = conn.getVehicleRepository().getByID("555");
                Point2D pos2dm = aVehiclem.getPosition();
                PositionConversionQuery pcqm = conn.queryPositionConversion();
                pcq.setPositionToConvert(pos2d, true);
                Point2D latlonm = pcq.get();
                Thread.sleep(200);
                conn.nextSimStep();
            }while(index<1005);
            conn.close();*/
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
