package maintain;

public class Gpsfake implements Runnable {

    private String managementPort;
    private String port;

    public String getManagementPort() {
        return managementPort;
    }

    public void setManagementPort(String managementPort) {
        this.managementPort = managementPort;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public Gpsfake(String command){

    }


    @Override
    public void run() {

    }
}
