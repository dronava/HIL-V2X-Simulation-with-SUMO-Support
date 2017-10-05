package communication.command.tmc;

import communication.command.navigation.AbstractNavigationCommand;
import it.polito.appeal.traci.Edge;
import it.polito.appeal.traci.SumoTraciConnection;
import it.polito.appeal.traci.Vehicle;

import java.io.IOException;
import java.util.List;

/**
 * Created by szezso on 2017.07.16..
 */
public class CommandCongestion extends AbstractTMCCommand {


    public CommandCongestion() {

    }

    @Override
    public void processCommand() {

    }

    private boolean isCrowded(Edge edge) {
        return false;
    }
}
