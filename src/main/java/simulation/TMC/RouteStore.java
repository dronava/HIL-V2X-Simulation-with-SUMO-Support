package simulation.TMC;

import communication.message.MessageRoute;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class RouteStore {

    private Map<String, RouteData> knownRoutes = new HashMap<>();
    private static RouteStore instance = null;

    private final long timeChekPeriod = 10; //seconds

    private RouteStore(){}

    public static RouteStore getInstance(){
        if(instance != null){
            return instance;
        }
        else{
            instance = new RouteStore();
            return instance;
        }
    }

    public synchronized void putRoute(MessageRoute messageRoute){
        LocalTime now = LocalTime.now();
        String actualVehicle = messageRoute.getVehicleID();
        if(knownRoutes.containsKey(actualVehicle)){
            RouteData original = knownRoutes.get(actualVehicle);
            original.setMessageRoute(messageRoute);
            original.setRouteReceivedTime(now);
            knownRoutes.put(actualVehicle, original);
        }
        else{
            knownRoutes.put(actualVehicle,new RouteData(messageRoute,now));
        }
    }

    public synchronized Optional<MessageRoute> getMessageRoute(String vehicleID){
        return knownRoutes.get(vehicleID).getMessageRoute();
    }

    public synchronized Optional<RouteData> getRouteData(String vehicleID){
        if(knownRoutes.containsKey(vehicleID)){
            return Optional.of(knownRoutes.get(vehicleID));
        }
        else{
            return Optional.empty();
        }
    }

    public synchronized List<String> getAllVehicleID(){
        return new ArrayList<>(knownRoutes.keySet());
    }

    public synchronized void setReRouteQueryTime(String vehicleID, LocalTime time){
        if(knownRoutes.containsKey(vehicleID)) {
            knownRoutes.get(vehicleID).setReRouteQueryTime(time);
        }
        else{
            knownRoutes.put(vehicleID, new RouteData(time));
        }

    }

    public synchronized Optional<LocalTime>  getRouteReceivedTime(String vehicleID){
        if(knownRoutes.containsKey(vehicleID)){
            return knownRoutes.get(vehicleID).getRouteReceivedTime();
        }
        else
            return Optional.empty();
    }

    public synchronized boolean isReRouteAvailable(String vehicleID){
        LocalTime now = LocalTime.now();
        Optional<LocalTime> receivedTime = getRouteReceivedTime(vehicleID);

        if(receivedTime.isPresent()) {
            long time = ChronoUnit.SECONDS.between(receivedTime.get(), now);
            return time > timeChekPeriod;
        }
        else {
            return true;
        }
    }
}
