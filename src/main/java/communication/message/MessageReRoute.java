package communication.message;

import communication.CommandEnum;

public class MessageReRoute extends MessageCommon{

    public MessageReRoute(CommandEnum command, String vehicleID) {
        super(command, vehicleID);
    }
}
