package communication.command;

import communication.CommandEnum;
import communication.command.navigation.AbstractNavigationCommand;
import it.polito.appeal.traci.Edge;
import it.polito.appeal.traci.SumoTraciConnection;

import java.io.IOException;
import java.util.Optional;

public class CommandReturnValue extends AbstractNavigationCommand{

    CommandEnum command;
    Object returnValue;

    public Edge getRouteDestination(){
        return (Edge)returnValue;
    }

    public AbstractNavigationCommand getNavigationCommand(){
        return (AbstractNavigationCommand)returnValue;
    }

    public CommandEnum getCommand() {
        return command;
    }

    public void setCommand(CommandEnum command) {
        this.command = command;
    }

    public CommandReturnValue(CommandEnum command, Object returnValue) {
        this.command = command;
        this.returnValue = returnValue;
    }


    @Override
    public Optional<CommandReturnValue> processCommand(SumoTraciConnection connection) throws IOException {
        return Optional.empty();
    }

    @Override
    public String getVehicleID() {
        return "";
    }
}
