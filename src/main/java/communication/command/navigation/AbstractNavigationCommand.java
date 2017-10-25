package communication.command.navigation;

import communication.command.AbstractCommand;
import communication.command.CommandReturnValue;
import it.polito.appeal.traci.SumoTraciConnection;
import process.MapData;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by szezso on 2017.07.03..
 */
public abstract class AbstractNavigationCommand extends AbstractCommand {

    public abstract Optional<CommandReturnValue> processCommand(SumoTraciConnection connection) throws IOException;



}
