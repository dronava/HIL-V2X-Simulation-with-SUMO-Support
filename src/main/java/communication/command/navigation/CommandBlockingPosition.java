package communication.command.navigation;

import communication.command.CommandEnum;
import communication.command.CommandReturnValue;
import communication.message.MessageDestination;
import communication.message.MessageRoute;
import it.polito.appeal.traci.Edge;
import it.polito.appeal.traci.SumoTraciConnection;
import simulation.RolesCatalog;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class CommandBlockingPosition extends AbstractNavigationCommand  {

    private MessageDestination blockingPosition;

    public CommandBlockingPosition(MessageDestination blockingPosition) {
        this.blockingPosition = blockingPosition;
    }

    @Override
    public Optional<CommandReturnValue> processCommand(SumoTraciConnection connection) throws IOException {

        Point2D position = getCartesianCoordinateFromPosition(connection,blockingPosition.getDestination());
        String destinationEdge = getEdgeByPosition(position, "passenger");

        if (!destinationEdge.isEmpty()){
            Edge actualEdge = connection.getEdgeRepository().getByID(destinationEdge);
            double originalEffort = actualEdge.getEffort(connection.getCurrentSimTime());
            actualEdge.changeEffort(100);
            connection.getVehicleRepository().getByID(blockingPosition.getVehicleID())
                    .reRouteByEffort();
            actualEdge.changeEffort(originalEffort);

            return Optional.of(createGetRouteReturnCommand(blockingPosition.getVehicleID()));

        }else{
            MessageRoute messageRoute = new MessageRoute(CommandEnum.ROUTE,
                    blockingPosition.getVehicleID(), new ArrayList<>());
            Optional<String> obuAddress = RolesCatalog.getOBUAddress(messageRoute.getVehicleID());
            if(obuAddress.isPresent()) {
                sendMessageToHost(obuAddress.get(), appConfig.getV2xAppListeningPort(), messageRoute);
            }
        }
        return Optional.empty();
    }

    @Override
    public String getVehicleID() {
        return blockingPosition.getVehicleID();
    }
}
