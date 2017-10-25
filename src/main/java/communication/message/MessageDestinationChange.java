package communication.message;

import communication.CommandEnum;

import java.awt.geom.Point2D;

public class MessageDestinationChange extends MessageCommon{

    private Point2D.Double destination;

    public Point2D.Double getDestination() {
        return destination;
    }

    public void setDestination(Point2D.Double destination) {
        this.destination = destination;
    }

    public MessageDestinationChange(CommandEnum command, String vehicleID, Point2D.Double destination) {
        super(command, vehicleID);
        this.destination = destination;
    }
}