package communication.command.navigation;

import com.fasterxml.jackson.databind.ObjectMapper;
import communication.CommandEnum;
import communication.command.CommandReturnValue;
import communication.message.MessageCommon;
import communication.V2XCommunicationClient;
import communication.message.MessageRoute;
import configuration.LoadConfiguration;
import configuration.pojo.AppConfig;
import it.polito.appeal.traci.SumoTraciConnection;
import simulation.RolesChatalog;

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
                .getCurrentRoute().stream().map(e->e.getID()).collect(Collectors.toList());

        MessageRoute messageRoute = new MessageRoute(CommandEnum.ROUTE,
                messageCommon.getVehicleID(), route);

        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(messageRoute);
        System.out.println(message);
        V2XCommunicationClient sendMessage =
                new V2XCommunicationClient(
                        //RolesChatalog.getOBUAddress(messageCommon.getVehicleID())
                        RolesChatalog.TMC
                        ,appConfig.getTMCListeningPort(),message);
        sendMessage.start();
        return Optional.empty();
    }

    @Override
    public String getVehicleID() {
        return messageCommon.getVehicleID();
    }
}
