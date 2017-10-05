package communication.command.navigation;

import com.fasterxml.jackson.databind.ObjectMapper;
import communication.message.MessageCommon;
import communication.V2XCommunicationClient;
import communication.message.MessageRoute;
import configuration.LoadConfiguration;
import configuration.pojo.AppConfig;
import it.polito.appeal.traci.SumoTraciConnection;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


public class CommandGetRoute extends AbstractNavigationCommand {

    private MessageCommon messageCommon;
    private String address;

    public CommandGetRoute(MessageCommon messageCommon, String address) {
        this.messageCommon = messageCommon;
        this.address = address;
    }

    @Override
    public String processCommand(SumoTraciConnection conn) throws IOException {
        AppConfig appConfig = LoadConfiguration.getAppConfig();

        List<String> route = conn.getVehicleRepository().getByID(messageCommon.getVehicleID())
                .getCurrentRoute().stream().map(e->e.getID()).collect(Collectors.toList());

        MessageRoute messageRoute = new MessageRoute(CommandEnum.ROUTE.getCommand(),route);

        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(messageRoute);
        System.out.println(message);
        V2XCommunicationClient sendMessage =
                new V2XCommunicationClient(address,appConfig.getV2xAppListeningPort(),message);
        sendMessage.start();
        return "";
    }
}
