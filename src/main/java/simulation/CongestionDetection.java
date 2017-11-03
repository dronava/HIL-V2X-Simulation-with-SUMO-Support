package simulation;

import it.polito.appeal.traci.SumoTraciConnection;
import process.EdgeCongested;
import process.MapData;

import java.io.IOException;
import java.util.List;

public class CongestionDetection {

    /*public static boolean detectCongestion(String edge, SumoTraciConnection connection, MapData mapData) {

        List<String> lanes = mapData.getEdgeByName(edge).getLanes();

        double edgeOccupancy = lanes.stream().mapToDouble(lane -> {
            try {
                return connection.getLaneRepository().getByID(lane).getLastStepOccupancy();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }).average().getAsDouble();

        EdgeCongested edgeCongested = new EdgeCongested(CongestionState.getStateByOccupancy(edgeOccupancy), calculateWeight(edgeOccupancy),
                connection.getCurrentSimTime(),
                connection.getCurrentSimTime() + 10);
        boolean changed = false;
        if(mapData.getEdgeCongestionState(edge).getCongestionState().getState() < edgeCongested.getCongestionState().getState()) {
            changed = true;
        }
        mapData.setEdgeCongested(edge, edgeCongested);
        return changed;
    }

    private static double calculateWeight(double occupancy) {
        return occupancy;
    }*/
}
