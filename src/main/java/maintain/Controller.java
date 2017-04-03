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
    private Queue<Task> taskQueue = new ConcurrentLinkedQueue();


    private maintain.Simulation simulation;
    private V2XConfigurationServer configurationServer;
    private ConfigurationParser configurationParser;
    private ObservableList<String> vehicleID = FXCollections.observableArrayList();

    private String configurationFile;
    private int simulationDelay;


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
        System.out.println("OS: "+ System.getProperty("os.name"));
        gpsfakeCommandTextField.setText("gpsfake -o -G -P 5555 -M 7777 -f");
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
            //cachedPool.execute(management);
            System.out.println("Run new gpsfake");
        }
    }

    public void loadConfiguration(){
        if(!configFileTextField.getText().isEmpty()){

            if(gpsfakes.size() != 0){
                gpsfakeManagmentQueues.clear();

                for (int i = 0; i < gpsfakes.size(); i++) {
                    gpsfakes.get(i).stop();
                    Future actgpsfakeThread = cachedPool.submit(gpsfakes.get(i));
                   //actgpsfakeThread.cancel(true);
                   System.out.println("Canceled: " +actgpsfakeThread.isCancelled());
                }
                //gpsfakes.clear();

            }

            configurationFile = configFileTextField.getText();
            configurationParser = new ConfigurationParser(configurationFile);
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

    public void startSimulation(){
        if(!simulationDelayTextField.getText().isEmpty()){
            simulationDelay =Integer.parseInt(simulationDelayTextField.getText());

            simulation = new maintain.Simulation(configurationFile,simulationDelay,gpsfakeManagmentQueues, taskQueue);
            cachedPool.execute(simulation);

            configurationServer = new V2XConfigurationServer(11111,cachedPool,taskQueue);
            cachedPool.execute(configurationServer);

            for(GpsfakeRun gpsfakeRun: gpsfakes){
                gpsfakeRun.runManagementThread(cachedPool);
            }

        }
    }

    public void FileChooserClick(){
       // String userDirectoryString = "C:\\Users\\szzso\\IdeaProjects\\V2X-Simulation\\simulation";//System.getProperty("user.home");
        String userDirectoryString ="/home/szezso/V2X-Simulation-with-SUMO/simulation/";
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