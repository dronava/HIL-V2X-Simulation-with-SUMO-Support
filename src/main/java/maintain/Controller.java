package maintain;

import de.tudresden.sumo.cmd.Edge;
import de.tudresden.sumo.cmd.Simulation;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.util.Sumo;
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
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.stage.FileChooser;
import java.io.File;
import javafx.stage.FileChooser.ExtensionFilter;

public class Controller implements Initializable{

    BlockingQueue<String> queue;
    ExecutorService cachedPool = Executors.newCachedThreadPool();

    private maintain.Simulation simulation;
    private  ConfigurationParser configurationParser;

    @FXML
    TextField configFileTextField;
    @FXML
    Button startSimulationButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cachedPool = Executors.newCachedThreadPool();
        queue = new LinkedBlockingQueue<String>();


       /* final String config_file = "simulation/map.sumo.cfg";


        simulation = new maintain.Simulation(config_file,0.1);
        cachedPool.execute(simulation);*/

    }
    public void runGpsfake(){
        System.out.println("Run new gpsfake");
    }

    public void loadConfiguration(){
        System.out.println("text: "+ configFileTextField.getText().isEmpty());
        if(!configFileTextField.getText().isEmpty()){
            configurationParser = new ConfigurationParser(configFileTextField.getText());
            cachedPool.execute(configurationParser);

            startSimulationButton.setDisable(false);
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
