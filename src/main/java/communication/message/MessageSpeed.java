package communication.message;

public class MessageSpeed extends MessageCommon {

    double speed;

    public MessageSpeed(String vehicleID, double speed) {
        super(vehicleID);
        this.speed = speed;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
