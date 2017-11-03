package communication.command.tmc;

import com.fasterxml.jackson.core.JsonProcessingException;
import communication.command.AbstractCommand;

public abstract class AbstractTmcCommand extends AbstractCommand {

    public abstract void processCommand() throws JsonProcessingException;
}
