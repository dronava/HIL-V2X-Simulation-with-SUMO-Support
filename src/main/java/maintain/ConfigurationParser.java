package maintain;

import java.io.*;
import java.util.*;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

/**
 * Created by szzso on 2017. 01. 22..
 */
public class ConfigurationParser implements  Runnable {

    private String configfilepath;
    private String routefilepath;

    public  ConfigurationParser(String configfile){
        this.configfilepath = configfile;

    }

    @Override
    public void run() {
        File inputFile = new File(configfilepath);
        SAXReader reader = new SAXReader();
        Document document = null;
        try {
            document = reader.read( inputFile );
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        System.out.println("Root element :"
                + document.getRootElement().getName());


        Element classElement = document.getRootElement();
        System.out.println("element:" + classElement.getName());

       // List<Node> nodes = document.selectNodes("/class/student" );
    }
}
