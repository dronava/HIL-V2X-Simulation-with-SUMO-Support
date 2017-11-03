package communication.command.navigation;

import communication.command.CommandEnum;
import communication.command.CommandReturnValue;
import communication.message.MessageDestination;
import it.polito.appeal.traci.Edge;
import it.polito.appeal.traci.PositionConversionQuery;
import it.polito.appeal.traci.SumoTraciConnection;
import process.MapData;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Optional;

/**
 * Created by szezso on 2017.07.03..
 */
public class CommandNewDestination extends AbstractNavigationCommand {

    private MessageDestination messageDestination;

    public CommandNewDestination(MessageDestination destination) {
        this.messageDestination = destination;
    }

    @Override
    public Optional<CommandReturnValue> processCommand(SumoTraciConnection connection) throws IOException {
        System.out.println("Command: dst ");

        PositionConversionQuery pcq = connection.queryPositionConversion();
        pcq.setPositionToConvert(messageDestination.getDestination(), false);
        Point2D posCartesian = pcq.get();

        //TODO megirni a VehicleClass-t traciban, nem lehet lekerni
        String vehicleType = "passenger";
        System.out.println("Vehcile type (new dst)" + vehicleType);
        System.out.println("Param: " + messageDestination.getDestination().getX() + "," + messageDestination.getDestination().getY());
        //posCartesian = new Point2D.Double(2533.27,3901.24);

        MapData mapData = MapData.getInstance();
        String vehicleNewDstEdge = mapData.getEdgeNameByCoordinate(posCartesian, vehicleType);

        return createReturnObject(connection, vehicleNewDstEdge);
    }

    private Optional<CommandReturnValue> createReturnObject(SumoTraciConnection connection,
                                                                   String vehicleNewDstEdge) throws IOException {
        Optional<CommandReturnValue> returnValue = Optional.empty();
        if (vehicleNewDstEdge != "") {
            Edge newDst = connection.getEdgeRepository().getByID(vehicleNewDstEdge);
            connection.getVehicleRepository().getByID(messageDestination.getVehicleID()).changeTarget(newDst);

            Optional.of(new CommandReturnValue(CommandEnum.RETURNDST,
                    connection.getEdgeRepository().getByID(vehicleNewDstEdge)));
        } else {
            System.out.println("Edge Not Found");
        }
        return returnValue;
    }

    @Override
    public String getVehicleID() {
        return messageDestination.getVehicleID();
    }
}
