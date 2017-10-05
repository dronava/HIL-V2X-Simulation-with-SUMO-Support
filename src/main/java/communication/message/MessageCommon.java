package communication.message;

public class MessageCommon {

    String vehicleID;

    public MessageCommon(String vehicleID) {
        this.vehicleID = vehicleID;
    }

    public String getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(String vehicleID) {
        this.vehicleID = vehicleID;
    }
}
