package communication.command.tmc;

import com.fasterxml.jackson.core.JsonProcessingException;

public abstract class AbstractTMCCommand {

    public abstract void processCommand() throws JsonProcessingException;
}
