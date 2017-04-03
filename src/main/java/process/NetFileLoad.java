package process;

import javafx.concurrent.Task;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;


/**
 * Created by szezso on 2017.04.04..
 */
public class NetFileLoad extends Task<Document> {

    private String configfilepath;

    public  NetFileLoad(String configfilepath){
        this.configfilepath = configfilepath;
    }

    @Override
    protected Document call() throws Exception {

        File inputFile = new File(configfilepath);
        DocumentBuilderFactory dbFactory =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();

        return null;
    }
}
