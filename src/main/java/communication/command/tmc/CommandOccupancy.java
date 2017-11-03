package communication.command.tmc;

import communication.command.CommandEnum;
import communication.message.MessageRouteState;
import simulation.RolesCatalog;
import simulation.TMC.RouteStore;

import java.time.LocalTime;

/**
 * Created by szezso on 2017.07.16..
 */
public class CommandOccupancy extends AbstractTmcCommand {

    MessageRouteState messageRouteState;

    public CommandOccupancy(MessageRouteState messageRouteState) {
        this.messageRouteState = messageRouteState;
    }

    @Override
    public void processCommand() {

        System.out.println("CommandOccupancy" + messageRouteState.getVehicleID());

        if(isCrowded()) {
            MessageRouteState messageReRoute = new MessageRouteState(CommandEnum.REROUTE,
                    messageRouteState.getVehicleID(), messageRouteState.getEdgeState());
            sendMessageToHost(RolesCatalog.getRSUAddress(), appConfig.getV2xAppListeningPort(), messageReRoute);
            RouteStore.getInstance().setReRouteQueryTime(messageReRoute.getVehicleID(), LocalTime.now());
        }
    }

    private boolean isCrowded() {
        return true;
    }

    @Override
    public String getVehicleID() {
        return messageRouteState.getVehicleID();
    }
}
