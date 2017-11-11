package simulation.managedobject;

import communication.command.CommandReturnValue;
import communication.command.navigation.AbstractNavigationCommand;
import gpsfake.Nmea;
import it.polito.appeal.traci.*;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by szezso on 2017.02.03..
 */
public class SumoVehicle {

    private Edge src;
    private Edge dst;
    private String vehicleID;
    private DateFormat dateFormatdate = new SimpleDateFormat("ddMMyy");
    private DateFormat dateFormattime = new SimpleDateFormat("HHmmss.SS");

    private ConcurrentLinkedQueue<String> nmeaToDevicesQueue;
    private boolean gpsFakeEnable;

    public Edge getSrc() {
        return src;
    }

    public void setSrc(Edge src) {
        this.src = src;
    }

    public Edge getDst() {
        return dst;
    }

    public void setDst(Edge dst) {
        this.dst = dst;
    }

    public String getVehicleID() {
        return vehicleID;
    }

    public SumoVehicle(ConcurrentLinkedQueue<String> nmeaToDevicesQueue, String vehicleID, Edge src, Edge dst) {
        this.vehicleID = vehicleID;
        this.src = src;
        this.dst = dst;
        this.nmeaToDevicesQueue = nmeaToDevicesQueue;
        gpsFakeEnable= true;
    }

    public SumoVehicle(String vehicleID, Edge src, Edge dst) {
        this.vehicleID = vehicleID;
        this.src = src;
        this.dst = dst;
        gpsFakeEnable = false;

    }

    public void changeSrcDst(){
        Edge tmp = src;
        src = dst;
        dst = tmp;
    }

    public void nextStep(SumoTraciConnection conn,
                         List<AbstractNavigationCommand> tasks) throws IOException {

        Vehicle actVehicle = conn.getVehicleRepository().getByID(vehicleID);

        if(actVehicle != null) {
            List<Edge> actroute = actVehicle.getCurrentRoute();

            if(tasks != null) {

                for (AbstractNavigationCommand task : tasks) {
                    Optional<CommandReturnValue> action = task.processCommand(conn);

                    if(action.isPresent())
                        actionAfterCommand(conn, action.get());
                }
            }

            if (actroute.get(actroute.size() - 1) == actVehicle.getCurrentEdge()) {

                changeSrcDst();

                System.out.println("A végére ért a kör elindul " + actVehicle.getCurrentEdge() + " -ról ide: " + getDst());
                if (getDst() != null)
                    actVehicle.changeTarget(getDst());

            }

            if(gpsFakeEnable) {
                Nmea nmea = createNmeaSentences(conn, actVehicle);
                nmeaToDevicesQueue.offer(nmea.getRMC());
                nmeaToDevicesQueue.offer(nmea.getGGA());
            }
        }
        else {
            //TODO ki kell torolni az ilyen jarmuvet
        }

    }

    private Nmea createNmeaSentences(SumoTraciConnection conn, Vehicle actVehicle) throws IOException {
        Date date = new Date();
        String actdate = dateFormatdate.format(date);
        String acttime = dateFormattime.format(date);

        double speed = actVehicle.getSpeed();
        double angle = actVehicle.getAngle();

        Point2D posCartesian = actVehicle.getPosition();
        PositionConversionQuery pcq = conn.queryPositionConversion();
        pcq.setPositionToConvert(posCartesian, true);
        Point2D latlon = pcq.get();

        return new Nmea(angle, speed, latlon, actdate, acttime);
    }

    private void actionAfterCommand(SumoTraciConnection connection, CommandReturnValue action) throws IOException {

        switch (action.getCommand()) {
            case RETURNDST:
                    setDst(action.getRouteDestination());
                break;
            case ROUTE:
                    action.getNavigationCommand().processCommand(connection);
                break;
        }
    }
}
