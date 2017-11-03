package communication.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import communication.V2XCommunicationClient;
import communication.command.navigation.CommandGetRoute;
import communication.message.MessageCommon;
import configuration.LoadConfiguration;
import configuration.pojo.AppConfig;
import it.polito.appeal.traci.PositionConversionQuery;
import it.polito.appeal.traci.SumoTraciConnection;
import process.MapData;

import java.awt.geom.Point2D;
import java.io.IOException;

public abstract class AbstractCommand {
    protected AppConfig appConfig = LoadConfiguration.getAppConfig();

    public abstract String getVehicleID();

    public<T extends MessageCommon> void sendMessageToHost(String address, int port, T message){
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

    protected String getEdgeByPosition(Point2D destination, String vehicleType){
        MapData mapData = MapData.getInstance();
        return mapData.getEdgeNameByCoordinate(destination, vehicleType);
    }

    protected Point2D getCartesianCoordinateFromPosition(SumoTraciConnection connection, Point2D.Double destination) throws IOException {
        PositionConversionQuery pcq = connection.queryPositionConversion();
        pcq.setPositionToConvert(destination, false);
        return  pcq.get();
    }

    protected CommandReturnValue createGetRouteReturnCommand(String vehicleID){
        MessageCommon newRoute = new MessageCommon(CommandEnum.ROUTE,
                vehicleID);
        CommandGetRoute getRoute = new CommandGetRoute(newRoute);

        return new CommandReturnValue(CommandEnum.ROUTE,getRoute);
    }
}
