package communication.command.tmc;

import communication.CommandEnum;
import communication.message.MessageRouteState;
import simulation.RolesChatalog;
import simulation.TMC.RouteStore;

import java.time.LocalTime;

/**
 * Created by szezso on 2017.07.16..
 */
public class CommandOccupancy extends AbstractTMCCommand {

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
            sendMessagetoHost(RolesChatalog.NAVIGATION, appConfig.getNavigationListeningPort(), messageReRoute);
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
