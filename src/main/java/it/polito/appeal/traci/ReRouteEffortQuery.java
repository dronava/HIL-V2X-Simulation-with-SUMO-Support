package it.polito.appeal.traci;

import de.uniluebeck.itm.tcpip.Storage;
import it.polito.appeal.traci.protocol.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created by szezso on 2017.08.09..
 */
public class ReRouteEffortQuery extends ChangeObjectStateQuery {

    ReRouteEffortQuery(DataInputStream dis, DataOutputStream dos,
                       String objectID) {
        super(dis, dos, Constants.CMD_SET_VEHICLE_VARIABLE, objectID, Constants.CMD_REROUTE_EFFORT);
    }

    @Override
    protected void writeParamsTo(Storage content) {
        content.writeByte(Constants.TYPE_COMPOUND);
        content.writeInt(0);
    }

}
