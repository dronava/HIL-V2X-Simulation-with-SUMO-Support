package communication.command.tmc;

import com.fasterxml.jackson.core.JsonProcessingException;
import communication.command.CommandEnum;
import communication.message.MessageRoute;
import configuration.LoadConfiguration;
import configuration.pojo.AppConfig;
import simulation.RolesCatalog;
import simulation.TMC.RouteStore;

public class CommandVehicleRoute extends AbstractTmcCommand {

    MessageRoute messageRoute;
    boolean selfIndicated;

    public CommandVehicleRoute(MessageRoute messageRoute){
        this.messageRoute = messageRoute;
        selfIndicated = false;
    }

    public CommandVehicleRoute(MessageRoute messageRoute, boolean selfIndicated) {
        this.messageRoute = messageRoute;
        this.selfIndicated = selfIndicated;
    }

    @Override
    public void processCommand() throws JsonProcessingException {
        AppConfig appConfig = LoadConfiguration.getAppConfig();

        System.out.println("CommandVehicleRoute " + messageRoute.getVehicleID());

        if(!selfIndicated){
            RouteStore.getInstance().putRoute(messageRoute);
        }

        RouteStore routeStore = RouteStore.getInstance();

        if(routeStore.isReRouteAvailable(getVehicleID())) {
            messageRoute.setCommand(CommandEnum.ROUTEQUERRY);
            sendMessageToHost(RolesCatalog.getRSUAddress(), appConfig.getV2xAppListeningPort(),messageRoute);
        }
    }

    @Override
    public String getVehicleID() {
        return messageRoute.getVehicleID();
    }
}
