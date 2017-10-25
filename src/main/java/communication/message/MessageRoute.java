package communication.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import communication.CommandEnum;

import java.util.List;

public class MessageRoute extends MessageCommon {

    private List<String> route;

    public List<String> getRoute() {
        return route;
    }

    public void setRoute(List<String> route) {
        this.route = route;
    }

    @JsonCreator
    public MessageRoute(@JsonProperty("command")CommandEnum command,  @JsonProperty("vehicleID")String vehicleID,
                        @JsonProperty("route") List<String> route) {
        super(command, vehicleID);
        this.route = route;
        this.command = command;
    }
}

