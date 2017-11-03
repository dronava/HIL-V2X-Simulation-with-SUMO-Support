package communication.Factory;

import communication.command.CommandEnum;
import communication.command.AbstractCommand;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by szezso on 2017.07.05..
 */
public class FactoryNavigationScenario1 extends AbstractFactoryCommand {

    public FactoryNavigationScenario1() {
    }

    @Override
    public Optional<AbstractCommand> createCommand(String messageJSON, CommandEnum command) throws IOException {
        Optional<AbstractCommand> concreteCommand = Optional.empty();

        switch (command) {
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
}
