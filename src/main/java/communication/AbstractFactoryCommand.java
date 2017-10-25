package communication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import communication.command.AbstractCommand;
import communication.message.MessageCommon;

import java.io.IOException;
import java.util.Optional;

public abstract class AbstractFactoryCommand {


    public abstract Optional<AbstractCommand> createCommand(String messageJSON,
                                                            CommandEnum command) throws IOException;

    public static CommandEnum getCommandType(String messageJSON) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode rootNode = objectMapper.readTree(messageJSON.getBytes());
            JsonNode commandNode = rootNode.path("command");
            return CommandEnum.getNameByValue(commandNode.asText());

        } catch (IOException e) {
            return CommandEnum.UNDEFINED;
        }

    }

    protected <M extends MessageCommon> M createMessage(
            String messageJSON, Class<M> messageClass) throws IOException {
        return new ObjectMapper().readValue(messageJSON, messageClass);
    }
}
