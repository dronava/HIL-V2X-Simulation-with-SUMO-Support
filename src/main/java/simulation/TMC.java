package simulation;


import communication.V2XCommunicationClient;
import it.polito.appeal.traci.SumoTraciConnection;
import jdk.nashorn.internal.runtime.OptimisticReturnFilters;
import process.MapData;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class TMC {
    private Point2D coordinate;
    private List<String> edges;
    private List<String> controlledVehicles;
    private String address;
    private int port;
    private MapData mapdata;


    public TMC(MapData mapData, int controlledEdgeNumber, Point2D coordinate, String address, int port, List<String> controlledVehicles){
        this.coordinate = coordinate;
        this.address = address;
        this.port = port;
        this.controlledVehicles = controlledVehicles;
        this.edges = mapData.getNearestNEdgeByCoordinate(coordinate, controlledEdgeNumber);
        this.mapdata = mapData;
    }

    public void setEdges(List<String> edges){
        this.edges = edges;
    }

    public List<String> getEdges() {
        return edges;
    }

    public void checkCongestion(SumoTraciConnection connection, MapData mapData){

        List<String> changedEdges = new ArrayList<>();
        for (String edge:edges) {
            if(CongestionDetection.detectCongestion(edge,connection,mapData))
                changedEdges.add(edge);
        }
        StringBuilder message = new StringBuilder();
        for (String vehicle:controlledVehicles) {
            StringBuilder affectedEdges = getAffectedEdges(connection, vehicle,changedEdges);
            if(affectedEdges.length() >0){
                message.append(vehicle);
                message.append(":");
                message.append(affectedEdges);
            }

        }

            V2XCommunicationClient client = new V2XCommunicationClient(address, port, message.toString());
            client.start();
    }

    private StringBuilder getAffectedEdges(SumoTraciConnection connection , String vehicleID, List<String> changedEdges){
        StringBuilder sb = new StringBuilder();
        try {
            List<String> edges = connection.getVehicleRepository().getByID(vehicleID).getCurrentRoute().stream()
                    .filter(p->changedEdges.contains(p.getID())).map(p-> p.getID()).collect(Collectors.toList());

            for (String edge: edges) {
                sb.append(edge);
                sb.append(",");
            }
            if(sb.length() >0)
                sb.replace(sb.length()-1,1,"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb;
    }
}
