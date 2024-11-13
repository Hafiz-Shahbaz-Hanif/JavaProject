package com.DC.testcases;

import com.DC.utilities.ReadConfig;
import com.DC.utilities.SeleniumDriver;
import io.qameta.allure.Attachment;
import io.qameta.allure.TmsLink;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.IOException;
import java.lang.reflect.Method;

public class BaseClass_Old implements IHookable {
    public WebDriver driver;

    public String DownloadFolder;

    public static String Browser;

    public static String MasterSpreadSheet = "./src/test/java/com/DC/testdata/DC_MasterTestSheet.xlsx";

    public static final ReadConfig READ_CONFIG = ReadConfig.getInstance();

    public static final Logger LOGGER = Logger.getLogger(BaseClass_Old.class);

    protected static String testMethodName;
    public static String downloadFolder;

    public BaseClass_Old() {
        PropertyConfigurator.configure("log4j.properties");
    }

    @Override
    public void run(IHookCallBack callBack, ITestResult testResult) {
        callBack.runTestMethod(testResult);
        Throwable exception = testResult.getThrowable();
        if (exception != null) {
            StringBuilder exceptions = new StringBuilder();
            StackTraceElement[] ste = exception.getStackTrace();
            for (StackTraceElement st : ste) {
                exceptions.append(st.toString() + "\n");
            }

            LOGGER.error("Exception: " + exceptions);

            try {
                if (!testResult.getTestClass().getName().contains("ApiTest")) {
                    takeScreenshotOnFailure(testResult.getMethod().getMethodName());
                }
            } catch (IOException e) {
                LOGGER.error("Exception:" + e.getMessage());
            }
        }
    }

    @Attachment(value = "Failure in method {0}", type = "image/png")
    public byte[] takeScreenshotOnFailure(String methodName) throws IOException {
        LOGGER.info("Taking Screenshot on failure");
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    public String getTestCaseIdValue(Method method) {
        if (method.isAnnotationPresent(TmsLink.class)) {
            TmsLink tc = method.getAnnotation(TmsLink.class);
            return tc.value();
        } else {
            LOGGER.info("Test Case id is not present for this test case");
            return "";
        }
    }

    @BeforeSuite()
    public void beforeSuite(ITestContext testContext) {
        LOGGER.info("************* STARTED SUITE " + testContext.getSuite().getName() + " ***************");
    }

    @BeforeTest()
    public void beforeTest(ITestContext testContext) {
        LOGGER.info("************* STARTED TEST " + testContext.getName() + " ***************");
    }

    @BeforeClass()
    public void beforeClass(ITestContext testContext) {
        String suiteName = testContext.getSuite().getName();
        if (suiteName.contains("Default Suite")) {
            String className = testContext.getAllTestMethods()[0].getTestClass().getName().toLowerCase();
            if (!className.contains("apitest")) {
                boolean headless = READ_CONFIG.getHeadlessMode();
                driver = initializeBrowserNoAnnotation(testContext, headless);
            }
        } else if (!suiteName.contains("API TESTS")) {
            boolean headless = READ_CONFIG.getHeadlessMode();
            driver = initializeBrowserNoAnnotation(testContext, headless);
        }
    }

    @BeforeMethod
    public void setupTestMethod(final ITestContext testContext, ITestResult tr) {
        testMethodName = tr.getMethod().getMethodName();
        LOGGER.info("************* STARTED TEST METHOD " + testMethodName + " ***************");
        String suiteName = testContext.getSuite().getName();
        if (!suiteName.contains("API TESTS")) {
            tr.setAttribute("WebDriver", driver);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownTestMethod(final ITestContext testContext, ITestResult result) {
        Method method = result.getMethod().getConstructorOrMethod().getMethod();
        String testCaseId = getTestCaseIdValue(method);
        LOGGER.info("************* FINISHED TEST METHOD " + testMethodName + " ***************");
        LOGGER.info("TEST CASE ID: " + testCaseId);
    }

    @AfterClass(alwaysRun = true)
    public void afterClass(ITestContext testContext) {
        try {
            String suiteName = testContext.getSuite().getName();
            if (!suiteName.contains("API TESTS") && driver != null && ((ChromeDriver) driver).getSessionId() != null) {
                LOGGER.info("***** - QUITTING BROWSER - *****");
                driver.quit();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @AfterTest(alwaysRun = true)
    public void afterTest(ITestContext testContext) {
        LOGGER.info("************* FINISHED TEST " + testContext.getName() + " ***************");
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite(ITestContext testContext) {
        LOGGER.info("************* FINISHED SUITE " + testContext.getSuite().getName() + " ***************");
    }

    public WebDriver initializeBrowserNoAnnotation(ITestContext testContext, boolean headless) {
        Browser = "chrome";
        try {
            driver = new SeleniumDriver().initializeChromeDriver(headless, true);
            downloadFolder = SeleniumDriver.downloadFolder;
            testContext.setAttribute("WebDriver", driver);
            return driver;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    @BeforeMethod(onlyForGroups = {"NoDataProvider"}, alwaysRun = true)
    public void getTestName(Method method) {
        testMethodName = method.getName();
        LOGGER.info("Test " + testMethodName + " started");
    }
}
