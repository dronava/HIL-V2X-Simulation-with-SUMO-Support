package communication;

import communication.command.*;

import java.util.List;

/**
 * Created by szezso on 2017.07.05..
 */
public class FactoryCommand {
    public static AbstractCommand getFactory(String id, String command, List<String> parameters) {
        AbstractCommand concreteCommand = null;

        if (id != null || !id.isEmpty()) {
            if (CommandEnum.contains(command)) {
                CommandEnum actCommand = CommandEnum.getNameByValue(command);
                switch (actCommand) {
                    case SPEED:
                        concreteCommand = createSpeedCommand(id, parameters);
                        break;
                    case DST:
                        concreteCommand = createNewDestinationCommand(id, parameters);
                        break;
                    case CONGESTION:
                        concreteCommand = createCongestionCheckCommand(id);
                        break;
                }
            }
        }
        return concreteCommand;
    }

    private static AbstractCommand createSpeedCommand(String id, List<String> parameters) {
        AbstractCommand concreteCommand = null;
        if (parameters.size() > 0) {
            double speed = Double.valueOf(parameters.get(0));
            concreteCommand = new CommandSpeed(id, speed);
            System.out.println("Create Command Speed Change");
        }
        return concreteCommand;
    }

    private static AbstractCommand createNewDestinationCommand(String id, List<String> parameters) {
        AbstractCommand concreteCommand = null;
        if (parameters.size() > 1) {
            double lat = Double.valueOf(parameters.get(0));
            double lon = Double.valueOf(parameters.get(1));
            concreteCommand = new CommandNewDestination(id, lat, lon);
            System.out.println("Create Command Destination Change");
        }
        return concreteCommand;
    }

    private static AbstractCommand createCongestionCheckCommand(String id) {
        return new CommandCongestion(id);
    }
}
