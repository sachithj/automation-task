package automation.ui.util;

import automation.common.LoggerHelper;
import automation.common.ResourceHelper;
import automation.ui.configuration.BrowserType;
import automation.ui.configuration.ChromeBrowser;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class TestBase {
    private static final Logger logger = LoggerHelper.getLogger(TestBase.class);
    public static WebDriver driver;

    public static String getCurrentDateTime() {

        DateFormat dateFormat = new SimpleDateFormat("_yyyy-MM-dd_HH-mm-ss");
        Calendar cal = Calendar.getInstance();
        return "" + dateFormat.format(cal.getTime());
    }

    public static String getCurrentDate() {
        return getCurrentDateTime().substring(0, 11);

    }

    private Function<WebDriver, Boolean> elementLocated(final WebElement element) {
        return driver -> {
            logger.debug("Waiting for Element : " + element);
            return element.isDisplayed();
        };
    }

    public static String takeScreenShot(String name) {

        File destDir = new File(ResourceHelper.getResourcePath("/screenshots/") + getCurrentDate());
        if (!destDir.exists()) {
            destDir.mkdir();
        }

        File destPath = new File(destDir.getAbsolutePath() + System.getProperty("file.separator") + name + ".jpg");
        try {
            FileUtils.copyFile(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE), destPath);
        } catch (IOException e) {
            logger.error(e.getMessage());

        }
        logger.info("[take screenshots] " + destPath.getAbsolutePath());
        return destPath.getAbsolutePath();
    }

    public WebDriver getBrowserObject(BrowserType bType) throws Exception {
        try {
            logger.info("Selected browser type: " + bType);

            switch (bType) {

                case chrome:
                    ChromeBrowser chrome = ChromeBrowser.class.newInstance();
                    return chrome.getChromeDriver();

                default:
                    throw new Exception("Driver Not Found : " + bType);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    public void setUpDriver(BrowserType bType) throws Exception {
        driver = getBrowserObject(bType);
        logger.debug("InitializeWebDrive: " + driver.hashCode());
        driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
        driver.manage().window().setSize(new Dimension(1920, 1080));
    }

    @BeforeTest()
    public void before() throws Exception {
        setUpDriver(BrowserType.chrome);
        logger.info("Opening browser: " + BrowserType.chrome);
    }

    @AfterTest()
    public void after() {
        driver.close();
        driver.quit();
        logger.info("Closing browser...");
    }

}
