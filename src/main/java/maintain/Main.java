package maintain;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("view.fxml"));
        primaryStage.setTitle("V2X Simulation Tool With SUMO");
        primaryStage.setScene(new Scene(root, 800, 450));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(450);
        primaryStage.show();
        System.out.println("OS: "+ System.getProperty("os.name"));
    }


    public static void main(String[] args) {
        launch(args);
    }
}
