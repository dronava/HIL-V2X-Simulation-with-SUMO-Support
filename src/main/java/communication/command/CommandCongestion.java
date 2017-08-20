package communication.command;

import it.polito.appeal.traci.Edge;
import it.polito.appeal.traci.SumoTraciConnection;
import it.polito.appeal.traci.Vehicle;

import java.io.IOException;
import java.util.List;

/**
 * Created by szezso on 2017.07.16..
 */
public class CommandCongestion extends AbstractCommand {


    public CommandCongestion(String id) {
        this.id = id;
    }

    @Override
    public String processCommand(SumoTraciConnection conn) throws IOException {
        List<Edge> vehiceRoute = conn.getVehicleRepository().getByID(id).getCurrentRoute();

        Vehicle actvehicle = conn.getVehicleRepository().getByID(id);
        System.out.println("Congestion");
        actvehicle.rerouteTraveltime();
        System.out.println("Congestion detection");


        vehiceRoute.stream().forEach(r -> {
            if (isCrowded(r)) {

            }
        });

        /*RerouteQuery q = actvehicle.queryReroute();
        q.run();*/
        //interpreter.exec

        /*Map<String, VehicleType> type = conn.getVehicleTypeRepository().getAll();

        //conn.getEdgeRepository().getByID("f").
        //conn.getVehicleRepository().getByID("555").

        type.entrySet().stream().forEach(u -> {
            try {
                System.out.println(u.getKey() + " Hossz: " + u.getValue().getLength());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
*/
        /*for (Edge actEdge : vehiceRoute){
            //actEdge.
            VehicleType ads;


        }*/
        return "";
    }

    private boolean isCrowded(Edge edge) {
        return false;
    }
}
