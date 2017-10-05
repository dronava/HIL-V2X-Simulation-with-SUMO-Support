package communication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import communication.command.navigation.*;
import communication.command.tmc.CommandCongestion;
import communication.message.MessageCommon;
import communication.message.MessageDestinationChange;
import communication.message.MessageSpeed;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by szezso on 2017.07.05..
 */
public class FactoryCommand {
    public static Optional<AbstractNavigationCommand> getFactory(String address, String messageJSON, CommandEnum command) {
        Optional<AbstractNavigationCommand> concreteCommand = Optional.empty();

        switch (command) {
            case SPEED:
                concreteCommand = createSpeedCommand(messageJSON);
                break;
            case DST:
                concreteCommand = createNewDestinationCommand(messageJSON);
                break;
            case CONGESTION:
                concreteCommand = createCongestionCheckCommand(messageJSON);
                break;
            case ROUTE:
                concreteCommand = createGetRouteCommand(address, messageJSON);
                break;
        }
        return concreteCommand;
    }

    public static CommandEnum getCommandType(String messageJSON) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rooteNode;
        try {

            rooteNode = objectMapper.readTree(messageJSON.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
            return CommandEnum.UNDEFINED;
        }
        JsonNode commandNode = rooteNode.path("command");
        return CommandEnum.getNameByValue(commandNode.asText());
    }

    private static Optional<AbstractNavigationCommand> createSpeedCommand(String messageJSON) {
        Optional<AbstractNavigationCommand> concreteCommand = Optional.empty();

        try {
            MessageSpeed messageSpeed = new ObjectMapper().readValue(messageJSON, MessageSpeed.class);
            concreteCommand = Optional.of(new CommandSpeed(messageSpeed));
            System.out.println("Create Command Speed Change");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return concreteCommand;
    }

    private static Optional<AbstractNavigationCommand> createNewDestinationCommand(String messageJSON) {
        Optional<AbstractNavigationCommand> concreteCommand = Optional.empty();

        try {
            MessageDestinationChange messageDestinationChange =
                    new ObjectMapper().readValue(messageJSON,MessageDestinationChange.class);
            concreteCommand = Optional.of(new CommandNewDestination(messageDestinationChange));
            System.out.println("Create Command Destination Change");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return concreteCommand;
    }

    private static Optional<AbstractNavigationCommand> createCongestionCheckCommand(String messageJSON) {
        return Optional.of(new CommandCongestion(messageJSON));
    }

    private static Optional<AbstractNavigationCommand> createGetRouteCommand(String address, String messageJSON) {
        Optional<AbstractNavigationCommand> concreteCommand = Optional.empty();
         try {
            MessageCommon messageCommon =new ObjectMapper().readValue(messageJSON, MessageCommon.class);
            concreteCommand = Optional.of(new CommandGetRoute(messageCommon, address));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return concreteCommand;
    }
}
