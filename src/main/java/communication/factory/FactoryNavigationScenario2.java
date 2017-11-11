package communication.factory;

import communication.command.CommandEnum;
import communication.command.AbstractCommand;

import java.io.IOException;
import java.util.Optional;

public class FactoryNavigationScenario2 extends AbstractFactoryCommand {

    @Override
    public Optional<AbstractCommand> createCommand(String messageJSON, CommandEnum command) throws IOException {
        Optional<AbstractCommand> concreteCommand = Optional.empty();

        switch (command) {
            case ROUTE:
                concreteCommand = createGetRouteCommand(messageJSON);
                break;
            case POSITIONRELEVANT:
                concreteCommand = createReRouteByBlocking(messageJSON);
                break;
        }
        return concreteCommand;
    }
}
