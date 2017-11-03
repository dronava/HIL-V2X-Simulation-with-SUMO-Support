package communication.command.navigation;

import communication.command.CommandEnum;
import communication.command.CommandReturnValue;
import communication.message.MessageRouteState;
import communication.message.MessageEdgeState;
import communication.message.MessageRoute;
import it.polito.appeal.traci.SumoTraciConnection;
import process.MapData;
import simulation.RolesCatalog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

public class CommandCongestionDetails extends AbstractNavigationCommand{

    MessageRoute messageRoute;

    public CommandCongestionDetails(MessageRoute messageRoute) {
        this.messageRoute = messageRoute;
    }

    @Override
    public Optional<CommandReturnValue> processCommand(SumoTraciConnection connection) throws IOException {
        System.out.println("CommandCongestionDetails " + messageRoute.getVehicleID());
        List<MessageEdgeState> edgeState = new ArrayList<>();

        messageRoute.getRoute().stream().forEach(edge->{
            double occupancy = getEdgeOccupancy(connection, edge);
            edgeState.add(new MessageEdgeState(edge, occupancy));
        });

        MessageRouteState messageRouteState =
                new MessageRouteState(CommandEnum.OCCUPANCY, messageRoute.getVehicleID(),edgeState);

        sendMessageToHost(RolesCatalog.TMC, appConfig.getTMCListeningPort(), messageRouteState);

        return Optional.empty();
    }

    private double getEdgeOccupancy(SumoTraciConnection connection, String edge){

        MapData mapData = MapData.getInstance();

        List<String> lanes = mapData.getEdgeByName(edge).getLanes();
        OptionalDouble avarage = lanes.stream().mapToDouble(lane -> {
            try {
                return connection.getLaneRepository().getByID(lane).getLastStepOccupancy();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }).average();

        if(avarage.isPresent())
            return avarage.getAsDouble();
        return 0.0;
    }

    @Override
    public String getVehicleID() {
        return messageRoute.getVehicleID();
    }
}
