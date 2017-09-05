package configuration;

import com.google.common.io.ByteStreams;
import configuration.pojo.AppConfig;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by szezso on 2017.07.19..
 */
public class LoadConfiguration {

    public AppConfig getAppConfig() {
        return appConfig;
    }

    AppConfig appConfig;



    public LoadConfiguration() throws IOException {
        appConfig = loadConfig();
        System.out.println(appConfig);
        System.out.println(appConfig.getSimulationDirectory());

//        Arrays.stream(appConfig.getClass().getFields()).forEach(s->System.out.println(s));
    }

    public AppConfig loadConfig() throws IOException {

        URL url = getConfigURL();
        InputStream is = url.openConnection().getInputStream();
        return new Yaml().loadAs(
                new ByteArrayInputStream(ByteStreams.toByteArray(is))
                , AppConfig.class);
    }

    private URL getConfigURL() throws IOException {
        URL url = LoadConfiguration.class.getResource("/config.yaml");
        if (url == null)
            System.out.println("null config");
        url.openStream().close();
        return url;
    }
}
