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

    static AppConfig appConfig = null;

    public static AppConfig getAppConfig() {
        if (appConfig == null) {
            try {
                return loadConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return appConfig;

    }

    private static AppConfig loadConfig() throws IOException {

        URL url = getConfigURL();
        InputStream is = url.openConnection().getInputStream();
        return new Yaml().loadAs(
                new ByteArrayInputStream(ByteStreams.toByteArray(is))
                , AppConfig.class);
    }

    private static URL getConfigURL() throws IOException {
        URL url = LoadConfiguration.class.getResource("/config.yaml");
        if (url == null)
            System.out.println("null config");
        url.openStream().close();
        return url;
    }
}
