package automation.common;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestNGListener implements ITestListener {
    public void onTestStart(ITestResult iTestResult) {
        System.out.println("TestCase started is: " + iTestResult.getName());

    }

    public void onTestSuccess(ITestResult iTestResult) {
        System.out.println("TestCase success is: " + iTestResult.getName());

    }

    public void onTestFailure(ITestResult iTestResult) {
        System.out.println("TestCase failed is: " + iTestResult.getName());

    }

    public void onTestSkipped(ITestResult iTestResult) {
        System.out.println("TestCase skipped is: " + iTestResult.getName());

    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
        System.out.println("onTestFailedButWithinSuccessPercentage " + iTestResult.getName());

    }

    public void onStart(ITestContext iTestContext) {
        System.out.println("onStart method started");

    }

    public void onFinish(ITestContext iTestContext) {
        System.out.println("onFinish method started");

    }
}
