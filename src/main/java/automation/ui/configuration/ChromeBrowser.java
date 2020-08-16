package automation.ui.configuration;

import automation.common.ResourceHelper;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

public class ChromeBrowser {
    private static final Logger logger = Logger.getLogger(ChromeBrowser.class);

    public WebDriver getChromeDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
        DesiredCapabilities cap = DesiredCapabilities.chrome();
        cap.setJavascriptEnabled(true);
        cap.setCapability(ChromeOptions.CAPABILITY, options);

        if (System.getProperty("os.name").contains("Mac")) {
            System.setProperty("webdriver.chrome.driver",
                    ResourceHelper.getResourcePath("/src/main/resources/driver/chromedriver"));

        } else if (System.getProperty("os.name").contains("Linux")) {
            System.setProperty("webdriver.chrome.driver",
                    ResourceHelper.getResourcePath("/src/main/resources/driver/chromedriver_linux"));

        } else if (System.getProperty("os.name").contains("Window")) {
            System.setProperty("webdriver.chrome.driver",
                    ResourceHelper.getResourcePath("\\src\\main\\resources\\driver\\chromedriver.exe"));

        } else {
            logger.info("Unknown OS");

        }
        return new ChromeDriver(cap);

    }

}
