package simulation;

public class SimulationParameter {

    String configurationFile;
    int delay;
    int simulationEndTime;

    public String getConfigurationFile() {
        return configurationFile;
    }

    public void setConfigurationFile(String configurationFile) {
        this.configurationFile = configurationFile;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getSimulationEndTime() {
        return simulationEndTime;
    }

    public void setSimulationEndTime(int simulationEndTime) {
        this.simulationEndTime = simulationEndTime;
    }

    public SimulationParameter(String configurationFile, int simulationEndTime) {
        this.configurationFile = configurationFile;
        this.simulationEndTime = simulationEndTime;
    }
}
