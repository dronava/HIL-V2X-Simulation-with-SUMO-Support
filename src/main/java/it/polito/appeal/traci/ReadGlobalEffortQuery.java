package it.polito.appeal.traci;

import it.polito.appeal.traci.protocol.Command;
import it.polito.appeal.traci.protocol.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.List;

/**
 * Created by szezso on 2017.08.08..
 */
public class ReadGlobalEffortQuery extends ReadObjectVarQuery.DoubleQ {

    private int effort = -1;

    ReadGlobalEffortQuery(DataInputStream dis, DataOutputStream dos,
                              int commandID, String objectID, int varID) {
        super(dis, dos, commandID, objectID, varID);
    }

    /**
     * The returned value of this query can be specific to a given simulation
     * time. This method allows to set the simulation time for the value that
     * will be returned.
     * @param effort
     */
    public void setEffort(int effort) {
		/*
		 * if the time is modified, forget the old value
		 */
        if (this.effort != effort)
            setObsolete();

        this.effort = effort;
    }

    @Override
    List<Command> getRequests() {
        if (effort == -1)
            throw new IllegalStateException("effort must be set first");

        List<Command> reqs = super.getRequests();
        Command req = reqs.iterator().next();
        req.content().writeByte(Constants.TYPE_INTEGER);
        req.content().writeInt(effort);
        return reqs;
    }

}
