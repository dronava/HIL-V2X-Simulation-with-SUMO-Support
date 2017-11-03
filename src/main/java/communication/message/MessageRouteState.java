package communication.message;

import communication.command.CommandEnum;

import java.util.List;

public class MessageRouteState extends MessageCommon{

    List<MessageEdgeState> edgeState;

    public List<MessageEdgeState> getEdgeState() {
        return edgeState;
    }

    public void setEdgeState(List<MessageEdgeState> edgeState) {
        this.edgeState = edgeState;
    }

    public MessageRouteState(CommandEnum command, String vehicleID, List<MessageEdgeState> edgeState) {
        super(command, vehicleID);
        this.edgeState = edgeState;
    }

    public MessageRouteState() {
    }
}
