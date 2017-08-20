package communication.command;

import it.polito.appeal.traci.Edge;
import it.polito.appeal.traci.PositionConversionQuery;
import it.polito.appeal.traci.SumoTraciConnection;

import java.awt.geom.Point2D;
import java.io.IOException;

/**
 * Created by szezso on 2017.07.03..
 */
public class CommandNewDestination extends AbstractCommand {

    private Point2D.Double dst;

    public CommandNewDestination(String id, Double lat, Double lon) {
        this.id = id;
        dst = new Point2D.Double(Double.valueOf(lat), Double.valueOf(lon));
    }

    @Override
    public String processCommand(SumoTraciConnection conn) throws IOException {
        System.out.println("Command: dst ");

        PositionConversionQuery pcq = conn.queryPositionConversion();
        pcq.setPositionToConvert(dst, false);
        Point2D posCartesian = pcq.get();

        //TODO megirni a VehicleClass-t traciban, nem lehet lekerni
        String vehicleType = "passenger";
        System.out.println("Vehcile type (new dst)" + vehicleType);
        System.out.println("Param: " + dst.getX() + "," + dst.getY());
        //posCartesian = new Point2D.Double(2533.27,3901.24);

        String action = "";
        String vehicleNewDstEdge = edgeSearch.getEdgeFromCoordinate(posCartesian, vehicleType);
        if (vehicleNewDstEdge != "") {
            // SumoVehicle modifyDst = vehicleSrcDst.get(task.getId());

            Edge newDst = conn.getEdgeRepository().getByID(vehicleNewDstEdge);
            conn.getVehicleRepository().getByID(getVehicleID()).changeTarget(newDst);
            action = "newDst";
        } else {
            System.out.println("Not found Edge");
        }
        return action;
    }
}
