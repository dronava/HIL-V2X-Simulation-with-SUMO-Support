package communication.command;

import it.polito.appeal.traci.SumoTraciConnection;
import process.EdgeSearch;

import java.io.IOException;

/**
 * Created by szezso on 2017.07.03..
 */
public abstract class AbstractCommand {

    protected String id;
    protected static EdgeSearch edgeSearch;

    public static void setEdgeSearch(EdgeSearch edges) {
        edgeSearch = edges;
    }

    public String getVehicleID() {
        return id;
    }


    public abstract String processCommand(SumoTraciConnection conn) throws IOException;
}
