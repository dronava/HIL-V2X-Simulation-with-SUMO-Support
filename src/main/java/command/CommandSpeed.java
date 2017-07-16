package command;

import it.polito.appeal.traci.SumoTraciConnection;
import it.polito.appeal.traci.Vehicle;

import java.io.IOException;

/**
 * Created by szezso on 2017.07.03..
 */
public class CommandSpeed extends AbstractCommand {

    private double speed;

    public CommandSpeed(String id, double speed){
        this.id = id;
        this.speed = speed;
    }

    @Override
    public String processCommand(SumoTraciConnection conn ) throws IOException {
        Vehicle actVehicle = conn.getVehicleRepository().getByID(id);

        actVehicle.changeSpeed(speed);
        return "";
    }
}
