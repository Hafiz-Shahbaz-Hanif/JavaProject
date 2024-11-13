package com.DC.utilities;

//Listener class used to generate Extent reports

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.*;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Reporting extends TestListenerAdapter implements ITestListener {
    public ExtentHtmlReporter htmlReporter;
    public ExtentReports extent;
    public ExtentTest logger;

    public void onStart(ITestContext testContext) {
        File output = new File(System.getProperty("user.dir") + "/test-output/");
        if (!output.exists()) {
            output.mkdirs();
            output.setReadable(true, false); // set readable
            output.setWritable(true, false); // set writable
            output.setExecutable(true, false); // set executable
        }
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());//time stamp
        String repName = "Test-Report-" + testContext.getName() + "-" + timeStamp + ".html";

        htmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir") + "/test-output/" + repName);//specify location of the report
        htmlReporter.loadXMLConfig(System.getProperty("user.dir") + "/extent-config.xml");

        extent = new ExtentReports();

        extent.attachReporter(htmlReporter);
        try {
            extent.setSystemInfo("Host name", InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            logger.error(e.getMessage());
        }
        extent.setSystemInfo("Environment", "Staging");

        htmlReporter.config().setDocumentTitle("Digital Commerce Test Project"); // Tile of report
        htmlReporter.config().setReportName("Functional Test Automation Report"); // name of the report
        htmlReporter.config().setTestViewChartLocation(ChartLocation.TOP); //location of the chart
        htmlReporter.config().setTheme(Theme.DARK);
    }

    public void onTestSuccess(ITestResult tr) {
        Object[] parameters = tr.getParameters();
        String testName = tr.getTestName();
        if (parameters.length > 0)
            testName = parameters[0].toString();
        if(testName == null)
            testName = tr.getName();
        logger = extent.createTest(testName); // create new entry in th report
        logger.log(Status.PASS, MarkupHelper.createLabel(testName, ExtentColor.GREEN)); // send the passed information to the report with GREEN color highlighted
    }

    public void onTestFailure(ITestResult tr) {
        String testCasename = tr.getName();
        Object[] parameters = tr.getParameters();
        if (parameters.length > 0)
            testCasename = parameters[0].toString();
        //Create new entry in the report
        logger = extent.createTest(testCasename); // create new entry in th report
        logger.log(Status.FAIL, MarkupHelper.createLabel(testCasename, ExtentColor.RED)); // send the passed information to the report with RED color highlighted

        Throwable exception = tr.getThrowable();
        boolean hasThrowable = exception != null;

        if (hasThrowable) {
            StringBuilder exceptions = new StringBuilder();
            StackTraceElement[] ste = exception.getStackTrace();
            for (StackTraceElement st : ste) {
                exceptions.append(st.toString() + "\n");
            }
            logger.fail("Exception: " + exceptions.toString());
        }


        try {
            WebDriver driver = (WebDriver) tr.getAttribute("WebDriver");
            if (driver != null) {
                String encodedImage = takeScreenShot(driver);
                logger.fail("Screenshot below:" + logger.addScreenCaptureFromPath("<img src=\"data:image/gif;base64," + encodedImage + "\">"));
                Reporter.log("<a href='data:image/jpeg;base64," + encodedImage +
                        "' data-featherlight='image'><img width='100' height='100' src='data:image/jpeg;base64," + encodedImage + "'/></a>");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void onTestSkipped(ITestResult tr) {
        String testCasename = tr.getName();
        Object[] parameters = tr.getParameters();
        if (parameters.length > 0)
            testCasename = parameters[0].toString();
        logger = extent.createTest(testCasename); // create new entry in th report
        logger.log(Status.SKIP, MarkupHelper.createLabel(testCasename, ExtentColor.ORANGE));
    }

    public void onFinish(ITestContext testContext) {
        extent.flush();
    }

    public String takeScreenShot(WebDriver driver) {
        String encodedImage = null;
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            encodedImage = SharedMethods.encodeFileToBase64Binary(scrFile);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return encodedImage;
    }

    @Override
    public void onTestStart(ITestResult result) {
        // Fetch groups from the test method
        String[] groups = result.getMethod().getGroups();
        for (String group : groups) {
            // Add each group as a tag in Allure
            Allure.label("tag", group);
        }
    }


}

