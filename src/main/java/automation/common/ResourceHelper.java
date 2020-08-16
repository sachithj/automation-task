package automation.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ResourceHelper {
    public static String getResourcePath(String resource) {
        return getBaseResourcePath() + resource;
    }

    private static String getBaseResourcePath() {
        return System.getProperty("user.dir");
    }

    public static InputStream getResourcePathInputStream(String path) throws FileNotFoundException {
        return new FileInputStream(ResourceHelper.getResourcePath(path));
    }

}
