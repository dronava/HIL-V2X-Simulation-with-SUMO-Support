package communication;

import communication.command.AbstractCommand;
import communication.command.navigation.*;
import communication.message.*;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by szezso on 2017.07.05..
 */
public class FactoryNavigationCommand extends AbstractFactoryCommand {

    public FactoryNavigationCommand() {
    }

    @Override
    public Optional<AbstractCommand> createCommand(String messageJSON, CommandEnum command) throws IOException {
        Optional<AbstractCommand> concreteCommand = Optional.empty();

        switch (command) {
            case SPEED:
                concreteCommand = createSpeedCommand(messageJSON);
                break;
            case DST:
                concreteCommand = createNewDestinationCommand(messageJSON);
                break;
            case ROUTE:
                concreteCommand = createGetRouteCommand(messageJSON);
                break;
            case ROUTEQUERRY:
                concreteCommand = createCongestionDetailCommand(messageJSON);
                break;
            case REROUTE:
                concreteCommand = createReRouteCommand(messageJSON);
                break;
        }
        return concreteCommand;
    }


    private Optional<AbstractCommand> createReRouteCommand(String messageJSON) throws IOException {
        MessageRouteState messageRouteState = createMessage(messageJSON, MessageRouteState.class);
        return Optional.of(new CommandReRoute(messageRouteState));
    }

    private Optional<AbstractCommand> createSpeedCommand(String messageJSON) throws IOException {
        MessageSpeed messageSpeed = createMessage(messageJSON,MessageSpeed.class);//new ObjectMapper().readValue(messageJSON, MessageSpeed.class);
        System.out.println("Create Command Speed Change");
        return Optional.of(new CommandSpeed(messageSpeed));
    }

    private Optional<AbstractCommand> createNewDestinationCommand(String messageJSON) throws IOException {
        MessageDestinationChange messageDestinationChange = createMessage(messageJSON,MessageDestinationChange.class);
        System.out.println("Create Command Destination Change");
        return Optional.of(new CommandNewDestination(messageDestinationChange));
    }

    private Optional<AbstractCommand> createCongestionDetailCommand(String messageJSON) throws IOException {
        MessageRoute messageRoute = createMessage(messageJSON,MessageRoute.class);//new ObjectMapper().readValue(messageJSON, MessageRoute.class);
        return Optional.of(new CommandCongestionDetails(messageRoute));
    }

    private Optional<AbstractCommand> createGetRouteCommand(String messageJSON) throws IOException {
        MessageCommon messageCommon = createMessage(messageJSON, MessageCommon.class);
        return Optional.of(new CommandGetRoute(messageCommon));
    }
}
