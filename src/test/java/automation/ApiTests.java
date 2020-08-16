package automation;

import automation.common.LoggerHelper;
import automation.common.ResourceHelper;
import automation.common.TestNGListener;
import automation.rest.APIs;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

@Listeners(TestNGListener.class)
public class ApiTests {
    private final APIs api;
    private static final Logger logger = LoggerHelper.getLogger(ApiTests.class);
    private static final String checkAvailabilitySchemaJSON = "json-templates" + File.separator + "checkAvailability.json";
    private static final String roomBookSchemaJSON = "json-templates" + File.separator + "bookRoom.json";
    private String reportOutFile;
    private static ExtentReports testReport;
    private static ExtentTest extent;


    @Parameters({"api-host"})
    public ApiTests(String host) {
        this.api = new APIs(host);
    }

    public String getCurrentDate(String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        return dateFormat.format(date);

    }

    public String addDaysToDate(String format, String date, int days) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendar.add(Calendar.DAY_OF_MONTH, days);
        return dateFormat.format(calendar.getTime());

    }

    public String subtractDaysToDate(String format, String date, int days) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendar.add(Calendar.DAY_OF_MONTH, (days * -1));
        return dateFormat.format(calendar.getTime());

    }

    public String getJSONSchemaAsString(String jsonFile) {
        String jsonText = null;
        try {
            jsonText = FileUtils.readFileToString(new File(ResourceHelper.getResourcePath(File.separator + "src" +
                            File.separator + "test" + File.separator + "resources" + File.separator + jsonFile)),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return jsonText;

    }

    @BeforeTest
    public void startReport() {
        reportOutFile = ResourceHelper.getResourcePath(File.separator + "ApiTestsReport.html");
        String extentRpt = File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator +
                "extent-config.xml";
        testReport = new ExtentReports(reportOutFile);
        testReport.addSystemInfo("Environment", System.getProperty("os.name"));
        testReport.loadConfig(new File(ResourceHelper.getResourcePath(extentRpt)));
    }

    @Test(dataProvider = "parameter-data", priority = 1, enabled = true)
    public void checkAvailabilityApiPositiveTest(String date) {
        extent = testReport.startTest("API Test: CheckAvailability Positive Test [Date: " + date + "]");
        extent.log(LogStatus.INFO, "Availability date: " + date);
        Response response = api.checkAvailabilityApi(date);
        extent.log(LogStatus.INFO, "Response: " + response.statusCode() + "\n" + response.asString());
        Assert.assertEquals(response.statusCode(), 200, "Request failed.");

        response.then()
                .body("date", equalTo(date))
                .body("rooms_available", greaterThanOrEqualTo(0))
                .body("price", is(new Integer(response.jsonPath().getString("price"))));
        extent.log(LogStatus.PASS, "CheckAvailability API Response validated");

    }

    @Test(priority = 2, enabled = true)
    public void checkAvailabilityResponseJSONSchemaTest() {
        String date = getCurrentDate("yyyy-MM-dd");
        logger.info("JSON schema template path: " + checkAvailabilitySchemaJSON);
        extent = testReport.startTest("API TEST: CheckAvailability Response JSON Schema Validation");
        extent.log(LogStatus.INFO, "JSON schema template:<br>" + getJSONSchemaAsString(checkAvailabilitySchemaJSON));

        Response response = api.checkAvailabilityApi(date);
        extent.log(LogStatus.INFO, "Response: " + response.statusCode() + "\n" + response.asString());
        Assert.assertEquals(response.statusCode(), 200, "Request failed.");

        response.then().assertThat().body(matchesJsonSchemaInClasspath(checkAvailabilitySchemaJSON));
        extent.log(LogStatus.PASS, "CheckAvailability API Response schema validated");

    }

    @Test(dataProvider = "parameter-data", priority = 3, enabled = true)
    public void checkAvailabilityApiNegativeTest(String date) {
        extent = testReport.startTest("API Test: CheckAvailability Negative Test [Date: " + date + "]");
        extent.log(LogStatus.INFO, "Availability date: " + date);
        Response response = api.checkAvailabilityApi(date);
        extent.log(LogStatus.INFO, "Response: " + response.statusCode() + "\n" + response.asString());
        Assert.assertEquals(response.statusCode(), 400, "Invalid (wrong Date parameter) Request expected.");
        extent.log(LogStatus.PASS, "CheckAvailability API Response status 400 validated");

    }

    @Test(dataProvider = "parameter-data", priority = 4, enabled = true)
    public void bookRoomApiPositiveTest(int numOfDays, String checkInDate) {
        extent = testReport.startTest("API Test: BookRoom Positive Test [CheckIn: " + checkInDate +
                ", NumDays: " + numOfDays + "]");
        extent.log(LogStatus.INFO, "CheckIn date: " + checkInDate + "<br>Number of days: " + numOfDays);
        String checkOutDate = addDaysToDate("yyyy-MM-dd", checkInDate, numOfDays);
        Response response = api.bookRoomApi(numOfDays, checkInDate);
        extent.log(LogStatus.INFO, "Response: " + response.statusCode() + "\n" + response.asString());
        Assert.assertEquals(response.statusCode(), 200, "Request failed.");

        response.then()
                .body("checkInDate", equalTo(checkInDate))
                .body("checkOutDate", equalTo(checkOutDate))
                .body("totalPrice", is(new Integer(response.jsonPath().getString("totalPrice"))));
        extent.log(LogStatus.PASS, "BookRoom API Response validated");

    }

    @Test(priority = 5, enabled = true)
    public void bookRoomResponseJSONSchemaTest() {
        logger.info("JSON schema template path: " + roomBookSchemaJSON);
        extent = testReport.startTest("API TEST: BookRoom Response JSON Schema Validation");
        extent.log(LogStatus.INFO, "JSON schema template:<br>" + getJSONSchemaAsString(roomBookSchemaJSON));
        Response response = api.bookRoomApi(1, getCurrentDate("yyyy-MM-dd"));
        extent.log(LogStatus.INFO, "Response: " + response.statusCode() + "\n" + response.asString());
        Assert.assertEquals(response.statusCode(), 200, "Request failed.");
        response.then().assertThat().body(matchesJsonSchemaInClasspath(roomBookSchemaJSON));
        extent.log(LogStatus.PASS, "CheckAvailability API Response schema validated");

    }

    @Test(dataProvider = "parameter-data", priority = 6, enabled = true)
    public void bookRoomApiNegativeTest(int numOfDays, String checkInDate) {
        System.out.println("Days: " + numOfDays + " Check In Date: " + checkInDate);
        extent = testReport.startTest("API Test: BookRoom Negative Test [CheckIn: " + checkInDate +
                ", NumDays: " + numOfDays + "]");
        extent.log(LogStatus.INFO, "CheckIn date: " + checkInDate + "<br>Number of days: " + numOfDays);
        Response response = api.bookRoomApi(numOfDays, checkInDate);
        extent.log(LogStatus.INFO, "Response: " + response.statusCode() + "\n" + response.asString());
        Assert.assertEquals(response.statusCode(), 400, "Invalid (wrong input JSON) Request expected.");
        extent.log(LogStatus.PASS, "BookRoom API Response status 400 validated");

    }

    @Test(dataProvider = "parameter-data", priority = 7, enabled = true)
    public void bookRoomApiPriceTest(String checkInDates) {
        String[] dates = checkInDates.split(",");
        extent = testReport.startTest("API Test: BookRoom Price Test [CheckIn: " + dates[0] +
                ", NumDays: " + dates.length + "]");
        int total = 0;
        for (String date : dates) {
            Response response = api.checkAvailabilityApi(date);
            int dayPrice = response.jsonPath().getInt("price");
            extent.log(LogStatus.INFO, "Call CheckAvailability API<br>Response: " + response.statusCode() + "\n" + response.asString());
            extent.log(LogStatus.INFO, "Available date " + date + " and price " + dayPrice);
            total += dayPrice;
        }

        extent.log(LogStatus.INFO, "CheckIn date: " + dates[0] + "<br>Number of days: " + dates.length);
        String checkOutDate = addDaysToDate("yyyy-MM-dd", dates[0], dates.length);
        Response response = api.bookRoomApi(dates.length, dates[0]);
        extent.log(LogStatus.INFO, "Response: " + response.statusCode() + "\n" + response.asString());
        Assert.assertEquals(response.statusCode(), 200, "Request failed.");

        response.then()
                .body("checkInDate", equalTo(dates[0]))
                .body("checkOutDate", equalTo(checkOutDate))
                .body("totalPrice", is(total));
        extent.log(LogStatus.PASS, "BookRoom API Response validated");

    }

    @DataProvider(name = "parameter-data")
    public Object[][] requestDateParam(Method method) {
        switch (method.getName()) {
            case "checkAvailabilityApiPositiveTest":
                return new Object[][]{
                        {getCurrentDate("yyyy-MM-dd")},
                        {addDaysToDate("yyyy-MM-dd", getCurrentDate("yyyy-MM-dd"), 1)},
                        {addDaysToDate("yyyy-MM-dd", getCurrentDate("yyyy-MM-dd"), 10)},
                        {addDaysToDate("yyyy-MM-dd", getCurrentDate("yyyy-MM-dd"), 30)},
                        {addDaysToDate("yyyy-MM-dd", getCurrentDate("yyyy-MM-dd"), 31)},
                        {addDaysToDate("yyyy-MM-dd", getCurrentDate("yyyy-MM-dd"), 60)},
                        {addDaysToDate("yyyy-MM-dd", getCurrentDate("yyyy-MM-dd"), 365)},
                        {addDaysToDate("yyyy-MM-dd", "2020-09-01", 0)},
                        {addDaysToDate("yyyy-MM-dd", "2020-10-01", 0)},
                        {addDaysToDate("yyyy-MM-dd", "2020-11-01", 0)},
                        {addDaysToDate("yyyy-MM-dd", "2020-12-31", 0)},
                        {addDaysToDate("yyyy-MM-dd", "2021-01-01", 0)},
                        {addDaysToDate("yyyy-MM-dd", "2022-01-01", 0)}
                };

            case "checkAvailabilityApiNegativeTest":
                return new Object[][]{
                        {""},
                        {"yyyy-MM-dd"},
                        {"20201020"},
                        {"0001-13-32"},
                        {getCurrentDate("dd-MM-yyyy")},
                        {subtractDaysToDate("yyyy-MM-dd", getCurrentDate("yyyy-MM-dd"), 1)},
                        {subtractDaysToDate("yyyy-MM-dd", getCurrentDate("yyyy-MM-dd"), 10)},
                };

            case "bookRoomApiPositiveTest":
                return new Object[][]{
                        {1, getCurrentDate("yyyy-MM-dd")},
                        {7, "2020-08-26"},
                        {10, getCurrentDate("yyyy-MM-dd")},
                        {30, getCurrentDate("yyyy-MM-dd")},
                        {31, getCurrentDate("yyyy-MM-dd")},
                        {1, addDaysToDate("yyyy-MM-dd", getCurrentDate("yyyy-MM-dd"), 1)},
                        {10, addDaysToDate("yyyy-MM-dd", getCurrentDate("yyyy-MM-dd"), 10)},
                        {30, addDaysToDate("yyyy-MM-dd", getCurrentDate("yyyy-MM-dd"), 30)},
                        {31, addDaysToDate("yyyy-MM-dd", getCurrentDate("yyyy-MM-dd"), 31)},
                        {1, subtractDaysToDate("yyyy-MM-dd", getCurrentDate("yyyy-MM-dd"), 1)},
                        {10, subtractDaysToDate("yyyy-MM-dd", getCurrentDate("yyyy-MM-dd"), 10)},
                        {30, subtractDaysToDate("yyyy-MM-dd", getCurrentDate("yyyy-MM-dd"), 30)},
                        {31, subtractDaysToDate("yyyy-MM-dd", getCurrentDate("yyyy-MM-dd"), 31)}
                };

            case "bookRoomApiNegativeTest":
                return new Object[][]{
                        {0, getCurrentDate("yyyy-MM-dd")},
                        {-1, getCurrentDate("yyyy-MM-dd")},
                        {1, getCurrentDate("dd-MM-yyyy")},
                        {0, "yyyy-mm-dd"},
                        {31, subtractDaysToDate("dd-MM-yyyy", getCurrentDate("dd-MM-yyyy"), 31)}
                };

            case "bookRoomApiPriceTest":
                return new Object[][]{
                        {"2020-08-01"},
                        {"2020-08-26,2020-08-27"},
                        {"2020-08-30,2020-08-31"},
                        {"2020-08-26,2020-08-27,2020-08-28,2020-08-29"},
                        {"2020-08-01,2020-08-02,2020-08-03,2020-08-04,2020-08-05,2020-08-06,2020-08-07,2020-08-08," +
                                "2020-08-09"}
                };

        }
        return null;

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