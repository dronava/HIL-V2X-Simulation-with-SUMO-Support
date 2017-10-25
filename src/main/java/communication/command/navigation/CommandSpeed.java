package communication.command.navigation;

import communication.command.CommandReturnValue;
import communication.message.MessageSpeed;
import it.polito.appeal.traci.SumoTraciConnection;
import it.polito.appeal.traci.Vehicle;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by szezso on 2017.07.03..
 */
public class CommandSpeed extends AbstractNavigationCommand {

    private MessageSpeed messageSpeed;

    public CommandSpeed(MessageSpeed messageSpeed) {
        this.messageSpeed = messageSpeed;
    }

    @Override
    public Optional<CommandReturnValue> processCommand(SumoTraciConnection connection) throws IOException {
        Vehicle actVehicle = connection.getVehicleRepository().getByID(messageSpeed.getVehicleID());

        actVehicle.changeSpeed(messageSpeed.getSpeed());
        return Optional.empty();
    }

    @Override
    public String getVehicleID() {
        return messageSpeed.getVehicleID();
    }
}
