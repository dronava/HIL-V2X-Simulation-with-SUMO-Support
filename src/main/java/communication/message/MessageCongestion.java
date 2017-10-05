package communication.message;

import java.util.List;

public class MessageCongestion extends MessageCommon{

    List<MessageEdgeState> edgeState;

    public List<MessageEdgeState> getEdgeState() {
        return edgeState;
    }

    public void setEdgeState(List<MessageEdgeState> edgeState) {
        this.edgeState = edgeState;
    }

    public MessageCongestion(String vehicleID, List<MessageEdgeState> edgeState) {
        super(vehicleID);
        this.edgeState = edgeState;
    }
}
