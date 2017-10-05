package configuration.pojo;

import com.google.common.base.MoreObjects;

import java.lang.reflect.Field;

public class AppConfig {
    private String simulationDirectory;
    private int communicationListeningPort;
    private int v2xAppListeningPort;
    private MapSaveConfig mapSaveConfig;

    public String getSimulationDirectory() {
        return simulationDirectory;
    }

    public void setSimulationDirectory(String simulationDirectory) {
        this.simulationDirectory = simulationDirectory;
    }

    public int getCommunicationListeningPort() {
        return communicationListeningPort;
    }

    public void setCommunicationListeningPort(int communicationListeningPort) {
        this.communicationListeningPort = communicationListeningPort;
    }

    public MapSaveConfig getMapSaveConfig() {
        return mapSaveConfig;
    }

    public void setMapSaveConfig(MapSaveConfig mapSaveConfig) {
        this.mapSaveConfig = mapSaveConfig;
    }

    public int getV2xAppListeningPort() {
        return v2xAppListeningPort;
    }

    public void setV2xAppListeningPort(int v2xAppListeningPort) {
        this.v2xAppListeningPort = v2xAppListeningPort;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("simulationDirectory", simulationDirectory + "\n")
                .add("communicationPort", communicationListeningPort + "\n").add("MapSaveConfig", mapSaveConfig + "\n")
                .toString();
    }

    public Field[] discoveryFields(){
        return this.getClass().getFields();
    }


}
