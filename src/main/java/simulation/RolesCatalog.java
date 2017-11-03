package simulation;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class RolesCatalog {

    private static Map<String,String> obu = new ConcurrentHashMap<>();
    private static String rsu  = "";

    public static final String NAVIGATION ="localhost";
    public static final String TMC ="localhost";

    public static Optional<String> getOBUAddress(String vehicleID){
        if(obu.containsKey(vehicleID)) {
            return Optional.of(obu.get(vehicleID));
        }else{
            return Optional.empty();
        }
    }

    public static void newOBU(String vehicleID, String address){
        obu.put(vehicleID,address);
    }

    public static String getRSUAddress() {
        return rsu;
    }

    public static void setRSU(String rsu) {
        RolesCatalog.rsu = rsu;
    }
}
