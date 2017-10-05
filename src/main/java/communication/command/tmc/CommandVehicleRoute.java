package communication.command.tmc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import communication.V2XCommunicationClient;
import communication.command.navigation.CommandEnum;
import communication.message.MessageRoute;
import configuration.LoadConfiguration;
import configuration.pojo.AppConfig;

import java.util.List;

public class CommandVehicleRoute extends AbstractTMCCommand {

    List<String> route;

    public CommandVehicleRoute(List<String> route) {
        this.route = route;
    }

    @Override
    public void processCommand() throws JsonProcessingException {

        AppConfig appConfig = LoadConfiguration.getAppConfig();

        MessageRoute messageRouteState = new MessageRoute(CommandEnum.CONGESTION.getCommand(), route);

        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(messageRouteState);
        System.out.println(message);
        V2XCommunicationClient sendMessage = new V2XCommunicationClient("localhost", appConfig.getCommunicationListeningPort(), message);
        sendMessage.start();

    }
}
