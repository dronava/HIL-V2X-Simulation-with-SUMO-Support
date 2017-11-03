package communication.message;

import communication.command.CommandEnum;

public class MessageSpeed extends MessageCommon {

    double speed;

    public MessageSpeed(CommandEnum command, String vehicleID, double speed) {
        super(command,vehicleID);
        this.speed = speed;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
