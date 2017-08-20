package it.polito.appeal.traci;

import de.uniluebeck.itm.tcpip.Storage;
import it.polito.appeal.traci.protocol.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created by szezso on 2017.07.23..
 */
public class ChangeGlobalEffortQuery extends ChangeObjectStateQuery {

    private int beginTime = 0;
    private int endTime = -1;
    private double effort;

    ChangeGlobalEffortQuery(DataInputStream dis, DataOutputStream dos,
                            String edgeID) {
        super(dis, dos, Constants.CMD_SET_EDGE_VARIABLE, edgeID, Constants.VAR_EDGE_EFFORT);
    }

    /**
     * @param beginTime the beginTime to set
     */
    public void setBeginTime(int beginTime) {
        this.beginTime = beginTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    /**
     * @param effort the travelTime to set
     */
    public void setEffort(double effort) {
        this.effort = effort;
    }

    @Override
    protected void writeParamsTo(Storage content) {
        content.writeByte(Constants.TYPE_COMPOUND);

        if(endTime !=-1) {
            content.writeInt(3);
            content.writeByte(Constants.TYPE_INTEGER);
            content.writeInt(beginTime);
            content.writeByte(Constants.TYPE_INTEGER);
            content.writeInt(endTime);
        }
        else {
            content.writeInt(1);
        }
        content.writeByte(Constants.TYPE_DOUBLE);
        content.writeDouble(effort);
    }
}
