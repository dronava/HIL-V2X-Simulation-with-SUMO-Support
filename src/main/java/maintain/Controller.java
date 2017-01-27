package maintain;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import java.io.File;
import javafx.stage.FileChooser.ExtensionFilter;

public class Controller implements Initializable{

    BlockingQueue<String> queue;
    ExecutorService cachedPool = Executors.newCachedThreadPool();
    List<GpsfakeRun> gpsfakes = new ArrayList<GpsfakeRun>();
    private Map<String, ConcurrentLinkedQueue<String>> gpsfakeManagmentQueues = new HashMap<String, ConcurrentLinkedQueue<String>>();

    private maintain.Simulation simulation;
    private ConfigurationParser configurationParser;
    private ObservableList<String> vehicleID = FXCollections.observableArrayList();

    private String configurationFile;
    private long simulationDelay;


    @FXML
    private TextField configFileTextField;
    @FXML
    private TextField simulationDelayTextField;
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
        System.out.println("GPSD_HOME: " + System.getenv("GPSD_HOME"));
    }
    public void runGpsfake(){
        if(!vehicleID.isEmpty() && !gpsfakeAvailableIDComboBox.getSelectionModel().isEmpty()) {
            ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();
            GpsfakeRun gpsfakeRun = new GpsfakeRun(gpsfakeCommandTextField.getText(),
                    gpsfakeAvailableIDComboBox.getValue().toString(),
                    gpsfakeAccordion, queue);
            gpsfakes.add(gpsfakeRun);
            GpsfakeManagement management =gpsfakeRun.getManagement();
            gpsfakeManagmentQueues.put(gpsfakeRun.getVehicleID(), queue);
            cachedPool.execute(gpsfakeRun);

            vehicleID.remove(gpsfakeAvailableIDComboBox.getValue().toString());
            gpsfakeAvailableIDComboBox.setItems(vehicleID);
            cachedPool.execute(management);
            System.out.println("Run new gpsfake");
        }
    }

    public void loadConfiguration(){
        if(!configFileTextField.getText().isEmpty()){

            configurationParser = new ConfigurationParser(configFileTextField.getText());
            cachedPool.execute(configurationParser);
            configurationFile = configFileTextField.getText();
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

    public void startSimulation(){
        if(!simulationDelayTextField.getText().isEmpty()){
            simulationDelay =Integer.parseInt(simulationDelayTextField.getText());

            simulation = new maintain.Simulation(configurationFile,simulationDelay,gpsfakeManagmentQueues);
            cachedPool.execute(simulation);
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