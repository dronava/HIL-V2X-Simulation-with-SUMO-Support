package process;

import javafx.concurrent.Task;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by szzso on 2017. 01. 22..
 */
public class ConfigurationParser extends Task<ConfigurationFile> {

    private String configfilepath;
    private String routefilepath;

    List<String> indexes;

    ConfigurationFile configurationFile;

    public  ConfigurationParser(String configfile){
        this.configfilepath = configfile;
        indexes = new ArrayList<String>();
    }

    @Override
    protected ConfigurationFile call() throws Exception {
        String netFile="";
        String routeFile = "";
        String guiFile = "";
        int simulationEndTime = 1;

        try {
            File inputFile = new File(configfilepath);
            SAXReader reader = new SAXReader();
            Document document = reader.read( inputFile );

            String separator = File.separator;
            System.out.println("Separator: " + separator);

            Node node= document.selectSingleNode("/configuration/input/net-file");
            netFile = inputFile.getParent() + separator + node.valueOf( "@value" );

            node = document.selectSingleNode("/configuration/gui-settings-file");
            guiFile = inputFile.getParent() + separator + node.valueOf( "@value" );


            node = document.selectSingleNode( "/configuration/input/route-files" );
            routeFile= inputFile.getParent() + separator + node.valueOf( "@value" );

            node = document.selectSingleNode( "/configuration/time/end" );
            simulationEndTime= Integer.parseInt(node.valueOf( "@value" ));

            inputFile  = new File(routeFile);
            document = reader.read(inputFile);

            Element root = document.getRootElement();

            // iterate through child elements of root
            for ( Iterator i = root.elementIterator(); i.hasNext(); ) {
                Element element = (Element) i.next();
                if(element.getName().equals("trip")){
                    indexes.add(element.attribute("id").getValue() + "-" + element.attribute("type").getValue());
                }
            }

            for (int j = 0; j < indexes.size(); j++) {
                System.out.println("id: "+ indexes.get(j));
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        configurationFile = new ConfigurationFile(indexes, netFile, routeFile, guiFile, simulationEndTime);

        return configurationFile;
    }
}
