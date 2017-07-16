package command;

import java.util.List;

/**
 * Created by szezso on 2017.07.05..
 */
public class FactoryCommand {
    public static AbstractCommand getFactory(String id, String command, List<String> parameters){
        AbstractCommand concreteCommand = null;
        switch (command){
            case "speed":
                if(parameters.size() > 0) {
                    double speed = Double.valueOf(parameters.get(0));
                    concreteCommand = new CommandSpeed(id, speed);
                    System.out.println("Create Command Speed Change");
                }
                break;
            case "dst":
                if(parameters.size() > 1) {
                    double lat = Double.valueOf(parameters.get(0));
                    double lon = Double.valueOf(parameters.get(1));
                    concreteCommand = new CommandNewDestination(id, lat, lon);
                    System.out.println("Create Command Destination Change");
                }
                break;
        }
        return concreteCommand;
    }
}
