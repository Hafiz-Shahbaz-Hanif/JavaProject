package com.DC.testcases;

import com.DC.constants.TestRailsConstants;
import com.DC.utilities.ReadConfig;
import com.DC.utilities.SeleniumDriver;
import com.DC.utilities.apiEngine.apiServices.testRail.TestRailAPIClient;
import io.qameta.allure.Attachment;
import io.qameta.allure.TmsLink;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONObject;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.IOException;
import java.lang.reflect.Method;

public class BaseClass implements IHookable {
    public WebDriver driver;

    public static String Browser;

    public static String MasterSpreadSheet = "./src/test/java/com/DC/testdata/DC_MasterTestSheet.xlsx";

    public static final ReadConfig READ_CONFIG = ReadConfig.getInstance();

    public static final Logger LOGGER = Logger.getLogger(BaseClass.class);

    protected static ThreadLocal<String> testMethodName = new ThreadLocal<>();

    public static String downloadFolder;

    public BaseClass() {
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

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite(ITestContext testContext) {
        LOGGER.info("************* STARTED SUITE " + testContext.getSuite().getName() + " ***************");
    }

    @BeforeTest(alwaysRun = true)
    public void beforeTest(ITestContext testContext) {
        LOGGER.info("************* STARTED TEST " + testContext.getName() + " ***************");
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTestMethod(final ITestContext testContext, ITestResult tr) {
        testMethodName.set(tr.getMethod().getMethodName());
        LOGGER.info("************* STARTED TEST METHOD " + testMethodName.get() + " ***************");
        String suiteName = testContext.getSuite().getName();
        if (!suiteName.contains("API TESTS")) {
            tr.setAttribute("WebDriver", driver);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownTestMethod(final ITestContext testContext, ITestResult result) {
        Method method = result.getMethod().getConstructorOrMethod().getMethod();
        String testCaseId = getTestCaseIdValue(method);
        LOGGER.info("************* FINISHED TEST METHOD " + testMethodName.get() + " ***************");
        LOGGER.info("TEST CASE ID: " + testCaseId);
    }

    @AfterTest(alwaysRun = true)
    public void afterTest(ITestContext testContext) {
        LOGGER.info("************* FINISHED TEST " + testContext.getName() + " ***************");
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite(ITestContext testContext) {
        LOGGER.info("************* FINISHED SUITE " + testContext.getSuite().getName() + " ***************");
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

    public int getTestCaseData(String testCaseId, String field) {
        int estimate = 0;
        TestRailAPIClient client = new TestRailAPIClient(TestRailsConstants.TESTRAIL_URL);
        client.setUser(TestRailsConstants.TESTRAIL_USERNAME);
        client.setPassword(TestRailsConstants.TESTRAIL_API_KEY);
        String[] testcaseids = testCaseId.split(",");
        JSONObject c;
        try {
            for (String testcaseid : testcaseids) {
                c = (JSONObject) client.sendGet("get_case/" + testcaseid);
                String timeValue = (String) c.get(field);
                if (!timeValue.isEmpty())
                    estimate += Integer.parseInt(timeValue.replaceAll("[^0-9]", ""));
            }
        } catch (IOException | TestRailAPIClient.APIException e) {
            LOGGER.error("Exception: " + e);
        }

        return estimate;
    }

    public WebDriver initializeBrowser(ITestContext testContext, boolean headless) {
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

    public WebDriver initializeNonIncognitoBrowser(ITestContext testContext, boolean headless) {
        Browser = "chrome";
        try {
            driver = new SeleniumDriver().initializeChromeDriver(headless, false);
            downloadFolder = SeleniumDriver.downloadFolder;
            testContext.setAttribute("WebDriver", driver);
            return driver;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    public void quitBrowser() {
        try {
            LOGGER.info("***** - QUITTING BROWSER - *****");
            try {
                driver.close();
                driver.quit();
            } catch (UnreachableBrowserException ex) {
                LOGGER.error(ex.getMessage());
            } catch (NoSuchSessionException noSuchSessionException) {
                LOGGER.error("Tried to quit browser with NULL session: " + noSuchSessionException.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
