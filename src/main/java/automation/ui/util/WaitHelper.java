package automation.ui.util;

import automation.common.LoggerHelper;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

public class WaitHelper {

    private final WebDriver driver;
    private static final Logger logger = LoggerHelper.getLogger(WaitHelper.class);

    public WaitHelper(WebDriver driver) {
        this.driver = driver;
        logger.debug("WaitHelper: " + this.driver.hashCode());

    }

    public void setImplicitWait(long timeout, TimeUnit unit) {
        logger.info("Implicit wait: " + timeout);
        driver.manage().timeouts().implicitlyWait(timeout, unit == null ? TimeUnit.SECONDS : unit);
    }

    public void sleep(long time) {
        try {
            Thread.sleep(time * 1000);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
    }

    public void waitForElementVisible(WebDriver driver, WebElement element, long timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(ExpectedConditions.visibilityOf(element));
        logger.info("Element found... " + element.getText());

    }
}

