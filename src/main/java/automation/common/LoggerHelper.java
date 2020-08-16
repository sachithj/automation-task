package automation.common;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;

public class LoggerHelper {
    private static boolean root = false;

    public static Logger getLogger(Class className) {

        String configFile = File.separator + "src" + File.separator + "main" + File.separator + "resources" +
                File.separator + "log4j.properties";

        if (root) {
            return Logger.getLogger(className);
        }

        PropertyConfigurator.configure(ResourceHelper.getResourcePath(configFile));
        root = true;
        return Logger.getLogger(className);

    }
}
