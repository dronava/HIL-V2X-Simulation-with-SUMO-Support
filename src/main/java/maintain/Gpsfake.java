package maintain;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;

public class Gpsfake implements Runnable {

    private String managementPort;
    private String port;
    private String vehicleID;
    private String vehicletype;

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

    public Gpsfake(String command, String vehicleid, Accordion accordion){
        System.out.println("ID: "+ vehicleid);
        this.vehicleID = vehicleid.split("-")[0];
        this.vehicletype = vehicleid.split("-")[1];
        makeTiledPane(accordion);
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
        grid.add(new Label("5555"), 1, 2);
        grid.add(new Label("Management port: "), 0, 3);
        grid.add(new Label("6555"), 1, 3);

        grid.add(new Button("Restart"), 0, 4);
        grid.add(new Button("Stop"), 1, 4);

        tiledPane.setText("gpsfake"+accordion.getPanes().size());
        tiledPane.setContent(grid);
        tiledPane.setAnimated(true);
        accordion.getPanes().addAll(tiledPane);
    }


    @Override
    public void run() {

    }
}
