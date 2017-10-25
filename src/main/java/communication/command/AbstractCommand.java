package communication.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import communication.V2XCommunicationClient;
import communication.message.MessageCommon;
import configuration.LoadConfiguration;
import configuration.pojo.AppConfig;

public abstract class AbstractCommand {
    protected AppConfig appConfig = LoadConfiguration.getAppConfig();

    public<T extends MessageCommon> void sendMessagetoHost(String address, int port,  T message){

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String messageJSON = objectMapper.writeValueAsString(message);
            System.out.println(messageJSON);
            V2XCommunicationClient sendMessage =
                    new V2XCommunicationClient(address,
                            port, messageJSON);
            sendMessage.start();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public abstract String getVehicleID();
}
