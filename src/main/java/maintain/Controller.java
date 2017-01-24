package maintain;

import de.tudresden.sumo.cmd.Edge;
import de.tudresden.sumo.cmd.Simulation;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.util.Sumo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.polito.appeal.traci.SumoTraciConnection;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import java.io.File;
import javafx.stage.FileChooser.ExtensionFilter;

public class Controller implements Initializable{

    BlockingQueue<String> queue;
    ExecutorService cachedPool = Executors.newCachedThreadPool();
    List<Gpsfake> gpsfakes = new ArrayList<Gpsfake>();

    private maintain.Simulation simulation;
    private ConfigurationParser configurationParser;
    private ObservableList<String> vehicleID = FXCollections.observableArrayList();


    @FXML
    private TextField configFileTextField;
    @FXML
    private TextField gpsfakeCommandTextField;
    @FXML
    private Button startSimulationButton;
    @FXML
    private  Button gpsfakeRunButton;
    @FXML
    private Label configurationReadSateLabel;
    @FXML
    private  Label configurationFileLoaderLabel;
    @FXML
    private ComboBox gpsfakeAvailableIDComboBox;
    @FXML
    private Accordion gpsfakeAccordion;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cachedPool = Executors.newCachedThreadPool();
        queue = new LinkedBlockingQueue<String>();


       /* final String config_file = "simulation/map.sumo.cfg";


        simulation = new maintain.Simulation(config_file,0.1);
        cachedPool.execute(simulation);*/

    }
    public void runGpsfake(){
        if(!vehicleID.isEmpty() && !gpsfakeAvailableIDComboBox.getSelectionModel().isEmpty()) {
            gpsfakes.add(new Gpsfake(gpsfakeCommandTextField.getText(),gpsfakeAvailableIDComboBox.getValue().toString(),gpsfakeAccordion));
            vehicleID.remove(gpsfakeAvailableIDComboBox.getValue().toString());
            gpsfakeAvailableIDComboBox.setItems(vehicleID);
            System.out.println("Run new gpsfake");
        }
    }

    public void loadConfiguration(){
        if(!configFileTextField.getText().isEmpty()){
            configurationParser = new ConfigurationParser(configFileTextField.getText());
            cachedPool.execute(configurationParser);
            configurationParser.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                    new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent t) {
                            vehicleID.addAll(configurationParser.getValue());
                            gpsfakeAvailableIDComboBox.setItems(vehicleID);
                            configurationReadSateLabel.setText("Configuration file loaded");
                            configurationReadSateLabel.setTextFill(Color.web("#3ae437"));

                            startSimulationButton.setDisable(false);
                            for (String s: vehicleID){
                                System.out.println("Vehicles: "+ s);
                            }

                        }
                    });


        }

    }

    public void FileChooserClick(){
        String userDirectoryString = "C:\\Users\\szzso\\IdeaProjects\\V2X-Simulation\\simulation";//System.getProperty("user.home");
        File userDirectory = new File(userDirectoryString);
        if(!userDirectory.canRead()) {
            userDirectory = new File("c:/");
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Sumo Configuration File");
        fileChooser.setInitialDirectory(userDirectory);
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Configuration Files", "*.cfg"),
                new ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(configFileTextField.getScene().getWindow());
        if(selectedFile!=null)
            configFileTextField.setText(selectedFile.getPath());

    }







}
