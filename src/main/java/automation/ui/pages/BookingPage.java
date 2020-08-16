package automation.ui.pages;

import automation.common.LoggerHelper;
import automation.ui.util.WaitHelper;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class BookingPage {
    Logger logger = LoggerHelper.getLogger(BookingPage.class);
    private final WebDriver driver;
    private final WaitHelper waitHelper;
    private final String pageTitleText = "Bookin";

    @FindBy(id = "mat-input-0")
    private WebElement checkDateInput;

    @FindBy(xpath = "//button/span[@class='mat-button-wrapper' and text()='Check']")
    private WebElement checkButton;

    @FindBy(id = "mat-input-1")
    private WebElement checkInDateInput;

    @FindBy(id = "mat-input-2")
    private WebElement numDaysInput;

    @FindBy(xpath = "//button/span[@class='mat-button-wrapper' and text()='Book']")
    private WebElement bookButton;


    public BookingPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        waitHelper = new WaitHelper(driver);
        waitHelper.waitForElementVisible(driver, checkButton, 60);
        waitHelper.waitForElementVisible(driver, bookButton, 60);

    }

    public boolean checkTileText() {
        return driver.getTitle().equals(pageTitleText);
    }

    public boolean checkAvailability(String date) {
        logger.info("Check availability from date {" + date + "}");
        checkDateInput.clear();
        logger.info("Entering date input: " + date);
        checkDateInput.sendKeys(date);
        logger.info("Clicking check button...");
        checkButton.click();

        WebElement result = driver.findElement(By.xpath("//pre[@class='response availabilty ng-star-inserted']"));
        waitHelper.waitForElementVisible(driver, result, 10);
        return result.isDisplayed();

    }

    public boolean bookRoom(String date, int numDays) {
        logger.info("Book days {" + numDays + "} from date {" + date + "}");
        checkInDateInput.clear();
        logger.info("Entering date input: " + date);
        checkInDateInput.sendKeys(date);
        numDaysInput.clear();
        logger.info("Entering number of days input: " + date);
        numDaysInput.sendKeys(String.valueOf(numDays));
        logger.info("Clicking book button...");
        bookButton.click();

        WebElement result = driver.findElement(By.xpath("//pre[@class='response booking ng-star-inserted']"));
        waitHelper.waitForElementVisible(driver, result, 10);
        return result.isDisplayed();

    }

}
