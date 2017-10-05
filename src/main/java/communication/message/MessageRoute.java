package communication.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MessageRoute {

    private List<String> route;
    private String command;

    public List<String> getRoute() {
        return route;
    }

    public void setRoute(List<String> route) {
        this.route = route;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @JsonCreator
    public MessageRoute(@JsonProperty("command") String command,
                        @JsonProperty("route") List<String> route) {
        this.route = route;
        this.command = command;
    }
}

