package process;

import java.util.List;

/**
 * Created by szezso on 2017.04.04..
 */
public class ConfigurationFile {

    private List<String> vehicleIds;
    private String netFilePath;
    private String routeFilePath;
    private String guiFilePath;
    private int simulationEndTime;

    public List<String> getVehicleIds() {
        return vehicleIds;
    }

    public void setVehicleIds(List<String> vehicleIds) {
        this.vehicleIds = vehicleIds;
    }

    public String getNetFilePath() {
        return netFilePath;
    }

    public void setNetFilePath(String netFilePath) {
        this.netFilePath = netFilePath;
    }

    public String getRouteFilePath() {
        return routeFilePath;
    }

    public void setRouteFilePath(String routeFilePath) {
        this.routeFilePath = routeFilePath;
    }

    public String getGuiFilePath() {
        return guiFilePath;
    }

    public void setGuiFilePath(String guiFilePath) {
        this.guiFilePath = guiFilePath;
    }

    public int getSimulationEndTime() {
        return simulationEndTime;
    }

    public void setSimulationEndTime(int simulationEndTime) {
        this.simulationEndTime = simulationEndTime;
    }

    public ConfigurationFile(List<String> vehicleIds, String netFilePath, String routeFilePath, String guiFilePath, int simulationEndTime) {
        this.vehicleIds = vehicleIds;
        this.netFilePath = netFilePath;
        this.routeFilePath = routeFilePath;
        this.guiFilePath = guiFilePath;
        this.simulationEndTime = simulationEndTime;
    }
}
