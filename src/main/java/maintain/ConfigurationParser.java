package maintain;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;

import javafx.concurrent.Task;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

/**
 * Created by szzso on 2017. 01. 22..
 */
public class ConfigurationParser extends Task<List<String>> {

    private String configfilepath;
    private String routefilepath;

    List<String> indexes;

    public  ConfigurationParser(String configfile){
        this.configfilepath = configfile;
        indexes = new ArrayList<String>();
    }

  /*  @Override
    public void run() {
        try {
            File inputFile = new File(configfilepath);
            SAXReader reader = new SAXReader();
            Document document = reader.read( inputFile );

            Node node = document.selectSingleNode( "//configuration/input/route-files" );
            String routevalue = node.valueOf( "@value" );


            String routefile= inputFile.getParent()+"\\"+routevalue;

            inputFile  = new File(routefile);
            document = reader.read(inputFile);


            Element root = document.getRootElement();

            // iterate through child elements of root
            for ( Iterator i = root.elementIterator(); i.hasNext(); ) {
                Element element = (Element) i.next();
                if(element.getName().equals("trip")){
                    indexes.add(element.attribute("id").getValue() + "-" + element.attribute("type").getValue());
                }

                for (int j = 0; j < indexes.size(); j++) {
                    System.out.println("id: "+ indexes.get(j));
                }
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    protected List<String> call() throws Exception {
        try {
            File inputFile = new File(configfilepath);
            SAXReader reader = new SAXReader();
            Document document = reader.read( inputFile );

            Node node = document.selectSingleNode( "/configuration/input/route-files" );
            String routevalue = node.valueOf( "@value" );


            String routefile= inputFile.getParent()+"/"+routevalue;

            inputFile  = new File(routefile);
            document = reader.read(inputFile);


            Element root = document.getRootElement();

            // iterate through child elements of root
            for ( Iterator i = root.elementIterator(); i.hasNext(); ) {
                Element element = (Element) i.next();
                if(element.getName().equals("trip")){
                    indexes.add(element.attribute("id").getValue() + "-" + element.attribute("type").getValue());
                }

                for (int j = 0; j < indexes.size(); j++) {
                    System.out.println("id: "+ indexes.get(j));
                }
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return indexes;
    }
}
