package configuration.pojo;

import com.google.common.base.MoreObjects;

import java.lang.reflect.Field;

public class AppConfig {
    private String simulationDirectory;
    private int communicationPort;
    private MapSaveConfig mapSaveConfig;

    public String getSimulationDirectory() {
        return simulationDirectory;
    }

    public void setSimulationDirectory(String simulationDirectory) {
        this.simulationDirectory = simulationDirectory;
    }

    public int getCommunicationPort() {
        return communicationPort;
    }

    public void setCommunicationPort(int communicationPort) {
        this.communicationPort = communicationPort;
    }

    public MapSaveConfig getMapSaveConfig() {
        return mapSaveConfig;
    }

    public void setMapSaveConfig(MapSaveConfig mapSaveConfig) {
        this.mapSaveConfig = mapSaveConfig;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("simulationDirectory", simulationDirectory + "\n")
                .add("communicationPort", communicationPort + "\n").add("MapSaveConfig", mapSaveConfig + "\n")
                .toString();
    }

    public Field[] discoveryFields(){
        return this.getClass().getFields();
    }


}
