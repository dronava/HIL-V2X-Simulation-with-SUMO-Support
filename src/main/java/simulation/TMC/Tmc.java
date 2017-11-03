package simulation.TMC;


import com.fasterxml.jackson.core.JsonProcessingException;
import communication.V2XListeningServer;
import communication.command.tmc.AbstractTmcCommand;
import communication.command.tmc.CommandVehicleRoute;
import configuration.LoadConfiguration;
import configuration.pojo.AppConfig;
import maintain.ThreadManager;
import simulation.RolesEnum;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Tmc implements Runnable {

    private Queue<AbstractTmcCommand> taskQueue = new ConcurrentLinkedQueue();
    private V2XListeningServer listeningServer;

    private final long waitTime = 500;


    public Tmc() {
        AppConfig appConfig = LoadConfiguration.getAppConfig();

        listeningServer = new V2XListeningServer(appConfig.getTMCListeningPort(), taskQueue, RolesEnum.TMC);
    }

    @Override
    public void run() {
        ThreadManager.getInstance().execute(listeningServer);
        RouteStore routeStore = RouteStore.getInstance();

        while (true) {


            routeStore.getAllVehicleID().forEach(vehicleID -> {

                Optional<RouteData> routeDataOptional = routeStore.getRouteData(vehicleID);


                if (routeDataOptional.isPresent() && routeStore.isReRouteAvailable(vehicleID)) {
                    RouteData routeData = routeDataOptional.get();
                    if (routeData.getMessageRoute().isPresent())
                        taskQueue.offer(new CommandVehicleRoute(routeData.getMessageRoute().get(), true));

                }

            });

            processCommand();
            try {
                synchronized (this) {
                    this.wait(waitTime);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void processCommand() {
        int actualCommandQueueSize = taskQueue.size();
        while (actualCommandQueueSize != 0) {
            AbstractTmcCommand command = taskQueue.poll();
            actualCommandQueueSize--;
            try {
                command.processCommand();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }


   /* public void checkCongestion(SumoTraciConnection connection, MapData mapData){

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
    }*/
}
