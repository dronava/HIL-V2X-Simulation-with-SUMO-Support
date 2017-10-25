package simulation.TMC;

import communication.message.MessageRoute;

import java.time.LocalTime;
import java.util.Optional;

public class RouteData {

    private Optional<MessageRoute> messageRoute = Optional.empty();
    private Optional<LocalTime> routeReceivedTime = Optional.empty();
    private LocalTime reRouteQueryTime;

    public LocalTime getReRouteQueryTime() {
        return reRouteQueryTime;
    }

    public void setReRouteQueryTime(LocalTime reRouteQueryTime) {
        this.reRouteQueryTime = reRouteQueryTime;
    }

    public Optional<MessageRoute> getMessageRoute() {
        return messageRoute;
    }

    public void setMessageRoute(MessageRoute messageRoute) {
        this.messageRoute = Optional.of(messageRoute);
    }

    public Optional<LocalTime> getRouteReceivedTime() {
        return routeReceivedTime;
    }

    public void setRouteReceivedTime(LocalTime routeReceivedTime) {
        this.routeReceivedTime = Optional.of(routeReceivedTime);
    }

    public RouteData(MessageRoute messageRoute, LocalTime receivedTime) {
        this.messageRoute = Optional.ofNullable(messageRoute);
        this.routeReceivedTime = Optional.of(receivedTime);
    }

    public RouteData(LocalTime reRouteQueryTime){
        this.reRouteQueryTime = reRouteQueryTime;
    }
}
