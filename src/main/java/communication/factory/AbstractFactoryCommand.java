package communication.factory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import communication.command.AbstractCommand;
import communication.command.CommandEnum;
import communication.command.navigation.*;
import communication.message.*;

import java.io.IOException;
import java.util.Optional;

public abstract class AbstractFactoryCommand {


    public abstract Optional<AbstractCommand> createCommand(String messageJSON,
                                                            CommandEnum command) throws IOException;

    public static CommandEnum getCommandType(String messageJSON) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode rootNode = objectMapper.readTree(messageJSON.getBytes());
            JsonNode commandNode = rootNode.path("command");
            return CommandEnum.getNameByValue(commandNode.asText());

        } catch (IOException e) {
            return CommandEnum.UNDEFINED;
        }
    }

    protected <M extends MessageCommon> M createMessage(
            String messageJSON, Class<M> messageClass) throws IOException {
        return new ObjectMapper().readValue(messageJSON, messageClass);
    }


    /**
     * Command factory functions
     */

    protected Optional<AbstractCommand> createReRouteCommand(String messageJSON) throws IOException {
        MessageRouteState messageRouteState = createMessage(messageJSON, MessageRouteState.class);
        return Optional.of(new CommandReRoute(messageRouteState));
    }

    protected Optional<AbstractCommand> createSpeedCommand(String messageJSON) throws IOException {
        MessageSpeed messageSpeed = createMessage(messageJSON,MessageSpeed.class);//new ObjectMapper().readValue(messageJSON, MessageSpeed.class);
        System.out.println("Create Command Speed Change");
        return Optional.of(new CommandSpeed(messageSpeed));
    }

    protected Optional<AbstractCommand> createNewDestinationCommand(String messageJSON) throws IOException {
        MessageDestination messageDestination = createMessage(messageJSON,MessageDestination.class);
        System.out.println("Create Command Destination Change");
        return Optional.of(new CommandNewDestination(messageDestination));
    }

    protected Optional<AbstractCommand> createCongestionDetailCommand(String messageJSON) throws IOException {
        MessageRoute messageRoute = createMessage(messageJSON,MessageRoute.class);//new ObjectMapper().readValue(messageJSON, MessageRoute.class);
        return Optional.of(new CommandCongestionDetails(messageRoute));
    }

    protected Optional<AbstractCommand> createGetRouteCommand(String messageJSON) throws IOException {
        MessageCommon messageCommon = createMessage(messageJSON, MessageCommon.class);
        return Optional.of(new CommandGetRoute(messageCommon));
    }

    protected Optional<AbstractCommand> createReRouteByBlocking(String messageJSON) throws IOException {
        MessageDestination messageDestination = createMessage(messageJSON, MessageDestination.class);
        return Optional.of(new CommandBlockingPosition(messageDestination));
    }
}
