package process;

import java.awt.geom.Point2D;

/**
 * Created by szezso on 2017.04.04..
 */
public class EdgeConvertType {
    String edge;
    String vehicleID;
    Point2D position;

    public String getEdge() {

        return edge;
    }

    public void setEdge(String edge) {
        this.edge = edge;
    }

    public String getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(String vehicleID) {
        this.vehicleID = vehicleID;
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    public EdgeConvertType(String vehicleID, Point2D position) {
        this.vehicleID = vehicleID;
        this.position = position;
    }

    public EdgeConvertType(String vehicleID, String edge) {
        this.vehicleID = vehicleID;
        this.edge = edge;
    }
}
