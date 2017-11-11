package communication.factory;

import communication.command.CommandEnum;
import communication.command.AbstractCommand;
import communication.command.tmc.CommandOccupancy;
import communication.command.tmc.CommandVehicleRoute;
import communication.message.MessageRoute;
import communication.message.MessageRouteState;

import java.io.IOException;
import java.util.Optional;

public class FactoryTmcCommand extends AbstractFactoryCommand {


    public FactoryTmcCommand() {
    }

    @Override
    public Optional<AbstractCommand> createCommand(String messageJSON, CommandEnum command) throws IOException {
        Optional<AbstractCommand> concreteCommand = Optional.empty();
        switch (command) {
            case ROUTE:
                concreteCommand = createVehicleRouteCommand(messageJSON);
                break;
            case OCCUPANCY:
                concreteCommand = createOccupancyCommand(messageJSON);
                break;
        }
        return concreteCommand;
    }


    private Optional<AbstractCommand> createOccupancyCommand(String messageJSON) throws IOException {
        MessageRouteState messageRouteState = createMessage(messageJSON, MessageRouteState.class);
        return Optional.of(new CommandOccupancy(messageRouteState));
    }

    private Optional<AbstractCommand> createVehicleRouteCommand(String messageJSON) throws IOException {
        MessageRoute messageRoute = createMessage(messageJSON, MessageRoute.class);
        return Optional.of(new CommandVehicleRoute(messageRoute));
    }
}
