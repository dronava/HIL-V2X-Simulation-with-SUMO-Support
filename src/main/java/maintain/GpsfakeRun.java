package maintain;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GpsfakeRun implements Runnable {

    private String host;
    private int managementPort;
    private int port;
    private String vehicleID;
    private String vehicletype;
    private String command;
    private GpsfakeManagement management;
    private Process process;

    @FXML
    private TitledPane tiledpane;

    public String getVehicleID() {
        return vehicleID;
    }

    public String getVehicletype() {
        return vehicletype;
    }

    public TitledPane getTiledpane() {
        return tiledpane;
    }

    public void setTiledpane(TitledPane tiledpane) {
        this.tiledpane = tiledpane;
    }

    public int getManagementPort() {
        return managementPort;
    }

    public void setManagementPort(int managementPort) {
        this.managementPort = managementPort;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public GpsfakeManagement getManagement() {
        return management;
    }


    public GpsfakeRun(String command, String vehicleid, Accordion accordion,ConcurrentLinkedQueue<String> queue ){
        this.host ="localhost";
        this.command= command;
        this.vehicleID = vehicleid.split("-")[0];
        this.vehicletype = vehicleid.split("-")[1];

        commandProcess();
        makeTiledPane(accordion);

        management = new GpsfakeManagement(host, managementPort, queue);
    }

    //gpsfake -o -G -P 5555 -M 6555 -c 0.07
    private void commandProcess(){
        String[] splitcommand = command.split(" ");
        for (int i = 1; i < splitcommand.length; i++) {
            if(splitcommand[i].equals("-M")) {
                managementPort = Integer.parseInt(splitcommand[i + 1]);
            }else{
                if(splitcommand[i].equals("-P")) {
                    port = Integer.parseInt(splitcommand[i + 1]);
                }
            }
        }
    }

    private void makeTiledPane(Accordion accordion){
        TitledPane tiledPane = new TitledPane();
        GridPane grid = new GridPane();
        grid.setVgap(4);

        grid.setPadding(new Insets(5, 5, 5, 5));
        grid.add(new Label("Vehicle ID: "), 0, 0);
        grid.add(new Label(vehicleID), 1, 0);
        grid.add(new Label("Vehicle type: "), 0, 1);
        grid.add(new Label(vehicletype), 1, 1);
        grid.add(new Label("Port: "), 0, 2);
        grid.add(new Label(Integer.toString(port)), 1, 2);
        grid.add(new Label("Management port: "), 0, 3);
        grid.add(new Label(Integer.toString(managementPort)), 1, 3);

        grid.add(new Button("Restart"), 0, 4);
        grid.add(new Button("Stop"), 1, 4);

        tiledPane.setText("gpsfake"+accordion.getPanes().size());
        tiledPane.setContent(grid);
        tiledPane.setAnimated(true);
        accordion.getPanes().addAll(tiledPane);
    }


    @Override
    public void run() {
        try {
            System.out.println(command);

            //Az új gpsfake elindítása, a környezeti változó megadása
            String environmentVariable= System.getenv("GPSD_HOME");
            if(environmentVariable == null)
                process = Runtime.getRuntime().exec(command,new String[]{"GPSD_HOME=/opt/local/sbin"});
            else
                process = Runtime.getRuntime().exec(command);
            process.waitFor();

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            //Parancs kimenete
            System.out.println("Standard output of the command:\n");
            String s;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            //Parancs
            System.out.println("Standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
