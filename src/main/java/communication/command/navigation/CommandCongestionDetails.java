package communication.command.navigation;

import communication.message.MessageRoute;
import it.polito.appeal.traci.SumoTraciConnection;

import java.io.IOException;

public class CommandCongestionDetails extends AbstractNavigationCommand{

    MessageRoute messageRoute;

    public CommandCongestionDetails(MessageRoute messageRoute) {
        this.messageRoute = messageRoute;
    }

    @Override
    public String processCommand(SumoTraciConnection conn) throws IOException {

        messageRoute.getRoute().stream().forEach(edge->{

        });

        return null;
    }
}
