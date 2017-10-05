package communication.message;

import java.awt.geom.Point2D;

public class MessageDestinationChange extends MessageCommon{

    private Point2D.Double destination;

    public Point2D.Double getDestination() {
        return destination;
    }

    public void setDestination(Point2D.Double destination) {
        this.destination = destination;
    }

    public MessageDestinationChange(String vehicleID, Point2D.Double destination) {
        super(vehicleID);
        this.destination = destination;
    }
}
