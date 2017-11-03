package communication.command.navigation;

import communication.command.CommandEnum;
import communication.command.CommandReturnValue;
import communication.message.MessageCommon;
import communication.message.MessageRouteState;
import it.polito.appeal.traci.Edge;
import it.polito.appeal.traci.SumoTraciConnection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandReRoute extends AbstractNavigationCommand {

    MessageRouteState messageRouteState;

    Map<Edge,Double> originalEffort = new HashMap<>();

    public CommandReRoute(MessageRouteState messageRouteState) {
        this.messageRouteState = messageRouteState;
    }

    @Override
    public Optional<CommandReturnValue> processCommand(SumoTraciConnection connection) throws IOException {
        System.out.println("CommandReRoute" + messageRouteState.getVehicleID());
        messageRouteState.getEdgeState().forEach(edge->{
            try {
                Edge actualEdge = connection.getEdgeRepository().getByID(edge.getEdge());
                originalEffort.put(actualEdge,actualEdge.getEffort(connection.getCurrentSimTime()));
                actualEdge.changeEffort(edge.getState());
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        connection.getVehicleRepository().getByID(messageRouteState.getVehicleID())
                .reRouteByEffort();

        setOriginalEffort();

        MessageCommon newRoute = new MessageCommon(CommandEnum.ROUTE,
                messageRouteState.getVehicleID());
        CommandGetRoute getRoute = new CommandGetRoute(newRoute);

        CommandReturnValue returnValue = new CommandReturnValue(CommandEnum.ROUTE,getRoute);

        return Optional.of(returnValue);
    }

    private void setOriginalEffort(){
        originalEffort.forEach((edge, effort) -> {
                    try {
                        edge.changeEffort(effort);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    @Override
    public String getVehicleID() {
        return messageRouteState.getVehicleID();
    }
}
