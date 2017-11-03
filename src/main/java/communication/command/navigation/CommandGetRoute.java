package communication.command.navigation;

import communication.command.CommandEnum;
import communication.command.CommandReturnValue;
import communication.message.MessageCommon;
import communication.message.MessageRoute;
import configuration.LoadConfiguration;
import configuration.pojo.AppConfig;
import it.polito.appeal.traci.SumoTraciConnection;
import simulation.RolesCatalog;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class CommandGetRoute extends AbstractNavigationCommand {

    private MessageCommon messageCommon;

    public CommandGetRoute(MessageCommon messageCommon) {
        this.messageCommon = messageCommon;
    }

    @Override
    public Optional<CommandReturnValue> processCommand(SumoTraciConnection connection) throws IOException {
        System.out.println("Command Get Route: " + messageCommon.getVehicleID());
        AppConfig appConfig = LoadConfiguration.getAppConfig();

        List<String> route = connection.getVehicleRepository()
                .getByID(messageCommon.getVehicleID())
                .getCurrentRoute().stream().map(e -> e.getID()).collect(Collectors.toList());

        MessageRoute messageRoute = new MessageRoute(CommandEnum.ROUTE,
                messageCommon.getVehicleID(), route);

        Optional<String> obuAddress = RolesCatalog.getOBUAddress(messageRoute.getVehicleID());
        if(obuAddress.isPresent()) {
            sendMessageToHost(obuAddress.get(), appConfig.getV2xAppListeningPort(), messageRoute);
        }

        return Optional.empty();
    }

    @Override
    public String getVehicleID() {
        return messageCommon.getVehicleID();
    }
}
