package maintain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by szezso on 2017.03.18..
 */
public class Task {
    String command;
    String id;
    List<String> parameter;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getParameter() {
        return parameter;
    }

    public void setParameter(List<String> parameter) {
        this.parameter = parameter;
    }

    public void addParameter(String param){
        parameter.add(param);
    }

    public Task(){
        parameter = new ArrayList<String>();
    }

    public Task(String command, String id, List<String> parameter) {
        this.command = command;
        this.id = id;
        this.parameter = parameter;
    }
}
