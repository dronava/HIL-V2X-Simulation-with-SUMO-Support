package communication.command.navigation;

import it.polito.appeal.traci.SumoTraciConnection;
import process.MapData;

import java.io.IOException;

/**
 * Created by szezso on 2017.07.03..
 */
public abstract class AbstractNavigationCommand {

    protected static MapData mapData;

    public static void setEdgeSearch(MapData mapData) {
        AbstractNavigationCommand.mapData = mapData;
    }

    public abstract String processCommand(SumoTraciConnection conn) throws IOException;
}
