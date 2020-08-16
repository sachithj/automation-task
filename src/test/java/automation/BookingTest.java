package automation;

import automation.common.ResourceHelper;
import automation.common.TestNGListener;
import automation.ui.configuration.BrowserType;
import automation.ui.pages.BookingPage;
import automation.ui.util.TestBase;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;

@Listeners(TestNGListener.class)
public class BookingTest extends TestBase {
    private final BrowserType browserType;
    BookingPage bookingPage;
    private final String platformUrl;
    private static final Logger logger = Logger.getLogger(BookingTest.class);
    private String reportOutFile;
    private static ExtentReports testReport;
    private static ExtentTest extent;
    private String extentRpt;


    @Parameters({"browser", "host-url"})
    public BookingTest(BrowserType browser, String url) {
        this.browserType = browser;
        this.platformUrl = url;

    }

    @BeforeTest
    public void startReport() {
        reportOutFile = ResourceHelper.getResourcePath(File.separator + "BookingTestReport.html");
        extentRpt = File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator +
                "extent-config.xml";
        testReport = new ExtentReports(reportOutFile);
        testReport.addSystemInfo("Environment", System.getProperty("os.name"));
        testReport.loadConfig(new File(ResourceHelper.getResourcePath(extentRpt)));
    }

    @BeforeTest
    public void setUp() {
        TestBase.driver.get(platformUrl);
        logger.info("Getting Booking website...");
        bookingPage = new BookingPage(TestBase.driver);
    }

    @Test(priority = 1)
    public void checkAvailability() {
        String date = "2020-10-10";
        extent = testReport.startTest("UI Test: Check Availability");
        extent.log(LogStatus.INFO, "Check Availability on date " + date);
        Assert.assertTrue(bookingPage.checkAvailability("2020-10-10"), "CheckAvailability failed.");
        String screenShot = TestBase.takeScreenShot("checkAvailability");
        extent.log(LogStatus.INFO, "Screenshot: " + screenShot);
        extent.log(LogStatus.INFO, "<img src=\"" + screenShot + "\">");
        extent.log(LogStatus.PASS, "CheckAvailability completed successfully");

    }

    @Test(priority = 2)
    public void bookingRoom() {
        int days = 10;
        String date = "2020-10-10";
        extent = testReport.startTest("UI Test: Book Room");
        extent.log(LogStatus.INFO, "Book Room from " + date + " for " + days + " days");
        Assert.assertTrue(bookingPage.bookRoom(date, days), "BookingRoom failed.");
        String screenShot = TestBase.takeScreenShot("bookingRoom");
        extent.log(LogStatus.INFO, "Screenshot: " + screenShot);
        extent.log(LogStatus.INFO, "<img src=\"" + screenShot + "\">");
        extent.log(LogStatus.PASS, "BookingRoom completed successfully");

    }

    @AfterMethod
    public void getResult(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            extent.log(LogStatus.FAIL, "Failure: " + result.getThrowable().getMessage());
            extent.log(LogStatus.FAIL, "Test Case Failed: " + result.getName());

        } else if (result.getStatus() == ITestResult.SKIP) {
            extent.log(LogStatus.SKIP, "Test Case Skipped: " + result.getName());
        }
        testReport.endTest(extent);

    }

    @AfterTest
    public void endReport() {
        logger.info("Test report path: " + reportOutFile);
        testReport.flush();
        testReport.close();

    }


}
