package maintain;

import de.tudresden.sumo.cmd.Lane;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.util.SumoCommand;
import de.tudresden.ws.container.SumoPosition2D;
import it.polito.appeal.traci.PositionConversionQuery;
import it.polito.appeal.traci.SumoTraciConnection;

import java.awt.geom.Point2D;
import java.io.IOException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * Created by szzso on 2017. 01. 21..
 */
public class Simulation implements Runnable {
    private double step;
    private String configfile;

    public Simulation(String configfile, double step){
        this.configfile = configfile;
        this.step = step;
    }

    @Override
    public void run() {
        System.out.println("This is a thread");
        try {
            SumoTraciConnection conn = new SumoTraciConnection(
                    configfile,  // config file
                    12345                                  // random seed
            );
            conn.runServer(true);


            // the first two steps of this simulation have no vehicles.
            conn.nextSimStep();
            conn.nextSimStep();

            it.polito.appeal.traci.Vehicle aVehicle  = conn.getVehicleRepository().getByID("555");

           // Collection<it.polito.appeal.traci.Vehicle> vehicles = conn.getVehicleRepository().getAll().values();

           // it.polito.appeal.traci.Vehicle aVehicle = vehicles.iterator().next();
            double speed = aVehicle.getSpeed();
            System.out.println("Speed: "+speed);
            double angle = aVehicle.getAngle();
            System.out.println("Angle: "+ angle);

            DateFormat dateFormatdate = new SimpleDateFormat("ddMMyy");
            DateFormat dateFormattime = new SimpleDateFormat("HHmmss.SS");
            Date date = new Date();
            String mydate= dateFormatdate.format(date);
            System.out.println("DAte: " + mydate); //2016/11/16 12:08:43
            String mytime = dateFormattime.format(date);
            System.out.println("Time: "+ mytime);

            Point2D pos2d = aVehicle.getPosition();
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
            conn.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

  /*  @Override
    public void run() {

       try{

            //start Simulation

            SumoTraciConnection conn = new SumoTraciConnection(path, configfile);

            //set some options
            conn.addOption("step-length", "0.1"); //timestep 1 second

            //start TraCI
            conn.runServer();

            //load routes and initialize the simulation
            conn.do_timestep();

            System.out.println(LocalDateTime.now());

            System.out.println(LocalDateTime.now());
            System.out.println("Pos3D: " + conn.do_job_get(Vehicle.getPosition3D("555")));
            String pos = conn.do_job_get(Vehicle.getPosition("555")).toString();
            double posx = Double.parseDouble(pos.split(",")[0]);
            double posy = Double.parseDouble(pos.split(",")[1]);
            System.out.println("Pos: " + pos);
            System.out.println("Speed: " + conn.do_job_get(Vehicle.getSpeed("555")));
            System.out.println("Angle: " + conn.do_job_get(Vehicle.getAngle("555")));
           // System.out.println(conn.do_job_get(de.tudresden.sumo.cmd.Simulation.convertGeo(posx,posy,"false")));
            Object[] array = new Object[]{4301.80, "POSITION_LON_LAT"};
            for(int i=0; i<array.length; i++){
                System.out.println("Type: " + array[i].getClass());
                //add_variable(array[i]);
            }

            System.out.println("GEO: "+ conn.do_job_get(de.tudresden.sumo.cmd.Simulation.convertGeo(1208.45,4301.80,"POSITION_LON_LAT")));
          //  System.out.println("GEO: "+ conn.do_job_get(de.tudresden.sumo.cmd.Simulation.convertGeo(1208.45,4301.80,"true")));


            double lanepos = Double.parseDouble(conn.do_job_get(Vehicle.getLanePosition("555")).toString());
            System.out.print("Lanepos: "+lanepos);
            byte laneIndex = Byte.parseByte(conn.do_job_get(Vehicle.getLaneIndex("555")).toString());
            System.out.println("Laneindex: "+ laneIndex);
            String edgeID = conn.do_job_get(Vehicle.getLaneID("555")).toString();

            System.out.println("Edgeid: "+edgeID);


            //System.out.println("GEO: "+ conn.do_job_get(de.tudresden.sumo.cmd.Simulation.convert3D(edgeID,lanepos,laneIndex,"POSITION_LON_LAT")));

           /* for(int i=0; i<3600; i++){

                //get the CO2 emission for a specific edge
                double co2 = (double) conn.do_job_get(Edge.getCO2Emission("gneE0"));
                System.out.println("timestep: " + i + " " + co2 + " g/s");

                //current simulation time
                int simtime = (int) conn.do_job_get(Simulation.getCurrentTime());

                //add new vehicle
                conn.do_job_set(Vehicle.add("veh"+i, "car", "r1", simtime, 0, 13.8, (byte) 1));
                conn.do_timestep();
            }*/

            //stop TraCI
         /*   conn.close();
            System.out.println("Connection Close");
        }catch(IOException io){
            System.out.println("rossz elérési út"+ io.getMessage());
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }*/
}
