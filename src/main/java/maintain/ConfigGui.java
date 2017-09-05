package maintain;

import configuration.pojo.AppConfig;
import javafx.scene.layout.GridPane;

public class ConfigGui {

    private static ConfigGui instance = null;

    private ConfigGui(){}

    public static ConfigGui getInstance() {
        if(instance == null) {
            instance = new ConfigGui();
        }
        return instance;
    }

    public static void createForm(GridPane configGridPane, AppConfig appConfig){



    }

}
