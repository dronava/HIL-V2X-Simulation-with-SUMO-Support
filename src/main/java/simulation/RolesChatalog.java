package simulation;

import java.util.HashMap;
import java.util.Map;

public class RolesChatalog {

    private static Map<String,String> obu = new HashMap<>();
    private static String rsu  = "";

    public static final String NAVIGATION ="localhost";
    public static final String TMC ="localhost";

    public static String getOBUAddress(String vehicleID){
        return obu.get(vehicleID);
    }

    public static void newOBU(String vehicleID, String address){
        obu.put(vehicleID,address);
    }

    public static String getRSUAddress() {
        return rsu;
    }

    public static void setRSU(String rsu) {
        RolesChatalog.rsu = rsu;
    }
}
