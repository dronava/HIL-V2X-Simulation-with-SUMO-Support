package maintain;

import communication.V2XListeningServer;
import communication.command.navigation.AbstractNavigationCommand;
import configuration.LoadConfiguration;
import configuration.pojo.AppConfig;
import gpsfake.GpsfakeManagement;
import gpsfake.GpsfakeRun;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import process.*;
import simulation.RolesEnum;
import simulation.Simulation;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Controller implements Initializable {

    private BlockingQueue<String> queue;
    private ExecutorService cachedPool = Executors.newCachedThreadPool();
    private List<GpsfakeRun> gpsfakes = new ArrayList<>();
    private Map<String, ConcurrentLinkedQueue<String>> gpsfakeManagmentQueues = new HashMap<String, ConcurrentLinkedQueue<String>>();
    private Queue<AbstractNavigationCommand> taskQueue = new ConcurrentLinkedQueue();

    private Simulation simulation;
    private V2XListeningServer listeningServer;
    private ConfigurationParser configurationParser;
    private ConfigurationFile configurationFiles;
    private NetFileLoad netFileLoad;
    private MapData mapData;
    private ObservableList<String> vehicleID = FXCollections.observableArrayList();

    private String configurationFile;
    private int simulationDelay;

    private AppConfig appConfig;


    @FXML
    private TextField configFileTextField;
    @FXML
    private TextField simulationDelayTextField;
    @FXML
    private TextField gpsfakeCommandTextField;
    @FXML
    private Button startSimulationButton;
    @FXML
    private Button gpsfakeRunButton;
    @FXML
    private Button configButton;
    @FXML
    private Label configurationReadSateLabel;
    @FXML
    private Label configurationFileLoaderLabel;
    @FXML
    private ComboBox gpsfakeAvailableIDComboBox;
    @FXML
    private Accordion gpsfakeAccordion;
    @FXML
    private GridPane configurationGridPane;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cachedPool = Executors.newCachedThreadPool();
        queue = new LinkedBlockingQueue<>();
        System.out.println("GPSD_HOME: " + System.getenv("GPSD_HOME"));
        System.out.println("SUMO_HOME: " + System.getenv("SUMO_HOME"));
        //System.out.println("OS: "+ System.getProperty("os.name"));
        gpsfakeCommandTextField.setText("gpsfake -o -G -P 5555 -M 7777 -f");

       loadYaml();
       System.out.println(appConfig.getSimulationDirectory());
    }

    public void runGpsfake() {
        if (!vehicleID.isEmpty() && !gpsfakeAvailableIDComboBox.getSelectionModel().isEmpty()) {
            ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();
            GpsfakeRun gpsfakeRun = new GpsfakeRun(gpsfakeCommandTextField.getText(),
                    gpsfakeAvailableIDComboBox.getValue().toString(),
                    gpsfakeAccordion, queue);
            gpsfakes.add(gpsfakeRun);
            GpsfakeManagement management = gpsfakeRun.getManagement();
            gpsfakeManagmentQueues.put(gpsfakeRun.getVehicleID(), queue);
            cachedPool.execute(gpsfakeRun);

            vehicleID.remove(gpsfakeAvailableIDComboBox.getValue().toString());
            gpsfakeAvailableIDComboBox.setItems(vehicleID);
            //cachedPool.execute(management);
            System.out.println("Run new gpsfake");
        }
    }

    public void loadConfiguration() {
        if (!configFileTextField.getText().isEmpty()) {

            if (gpsfakes.size() != 0) {
                gpsfakeManagmentQueues.clear();

                for (int i = 0; i < gpsfakes.size(); i++) {
                    gpsfakes.get(i).stop();
                    Future actgpsfakeThread = cachedPool.submit(gpsfakes.get(i));
                    //actgpsfakeThread.cancel(true);
                    System.out.println("Canceled: " + actgpsfakeThread.isCancelled());
                }
                //gpsfakes.clear();

            }

            configurationFile = configFileTextField.getText();
            configurationParser = new ConfigurationParser(configurationFile);
            cachedPool.execute(configurationParser);

            configurationParser.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                    t -> {
                        configurationFiles = configurationParser.getValue();

                        vehicleID.addAll(configurationFiles.getVehicleIds());
                        gpsfakeAvailableIDComboBox.setItems(vehicleID);
                        configurationReadSateLabel.setText("Configuration file loaded");
                        configurationReadSateLabel.setTextFill(Color.web("#3ae437"));


                        for (String s : vehicleID) {
                            System.out.println("Vehicles: " + s);
                        }

                        netFileLoad = new NetFileLoad(configurationFiles.getNetFilePath());
                        cachedPool.execute(netFileLoad);
                        netFileLoad.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                                t1 -> {
                                    mapData = netFileLoad.getValue();
                                    configurationReadSateLabel.setText("Net file loaded");
                                    startSimulationButton.setDisable(false);
                                });

                    });
        }
    }

    public void startSimulation() {
        if (mapData != null) {
            //TODO Delay
            //!simulationDelayTextField.getText().isEmpty() &&
            //simulationDelay =Integer.parseInt(simulationDelayTextField.getText());
            simulationDelay = 0;
            List<String> managedVehicles = gpsfakes.stream().map(u -> u.getVehicleID()).collect(Collectors.toList());
            managedVehicles.forEach(u -> System.out.println(u));
            simulation = new Simulation(configurationFile, simulationDelay, gpsfakeManagmentQueues, taskQueue, mapData);
            cachedPool.execute(simulation);

            listeningServer = new V2XListeningServer(appConfig.getNavigationListeningPort(), taskQueue, RolesEnum.NAVIGATION);
            cachedPool.execute(listeningServer);

            for (GpsfakeRun gpsfakeRun : gpsfakes) {
                gpsfakeRun.runManagementThread(cachedPool);
            }

        }
    }

    public void FileChooserClick() {
        // String userDirectoryString = "C:\\Users\\szzso\\IdeaProjects\\V2X-Simulation\\simulation";
        System.out.println(System.getProperty("user.home"));
        String userDirectoryString = appConfig.getSimulationDirectory();// "/home/szezso/V2X-Simulation-with-SUMO/simulation/";
        File userDirectory = new File(userDirectoryString);
        if (!userDirectory.canRead()) {
            userDirectory = new File("/home/");
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Sumo Configuration File");
        fileChooser.setInitialDirectory(userDirectory);
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Configuration Files", "*.cfg"),
                new ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(configFileTextField.getScene().getWindow());
        if (selectedFile != null)
            configFileTextField.setText(selectedFile.getPath());

    }

    public void loadYaml() {
        System.out.println("Load Yaml");

        appConfig = LoadConfiguration.getAppConfig();

    }
}