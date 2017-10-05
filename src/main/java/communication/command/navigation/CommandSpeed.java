package communication.command.navigation;

import communication.message.MessageSpeed;
import it.polito.appeal.traci.SumoTraciConnection;
import it.polito.appeal.traci.Vehicle;

import java.io.IOException;

/**
 * Created by szezso on 2017.07.03..
 */
public class CommandSpeed extends AbstractNavigationCommand {

    private MessageSpeed messageSpeed;

    public CommandSpeed(MessageSpeed messageSpeed) {
        this.messageSpeed = messageSpeed;
    }

    @Override
    public String processCommand(SumoTraciConnection conn) throws IOException {
        Vehicle actVehicle = conn.getVehicleRepository().getByID(messageSpeed.getVehicleID());

        actVehicle.changeSpeed(messageSpeed.getSpeed());
        return "";
    }
}
