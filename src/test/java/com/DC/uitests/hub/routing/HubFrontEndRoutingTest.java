package com.DC.uitests.hub.routing;

import com.DC.pageobjects.adc.AppHomepage;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.testcases.BaseClass;
import com.DC.uitests.hub.marketshare.HubMarketShareTokenExchangeForUsersTest;
import com.DC.utilities.ReadConfig;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HubFrontEndRoutingTest extends BaseClass {

    Logger logger;
    ReadConfig readConfig;
    String dcAppUrl;

    HubFrontEndRoutingTest() {
        readConfig =  ReadConfig.getInstance();
        logger = Logger.getLogger(HubMarketShareTokenExchangeForUsersTest.class);
        PropertyConfigurator.configure("log4j.properties");
        dcAppUrl = readConfig.getDcAppUrl();
    }

    @Test(description = "PH-119 - Hub Frontend Routing for Legacy Platforms")
    public void Hub_Frontend_Routing_For_Platforms_Test() throws Exception {
        logger.info("** Test has started.");

        DCLoginPage lp = new DCLoginPage(driver);
        SoftAssert softAssert = new SoftAssert();
        AppHomepage nm;


        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubFilaInsightsEdgeUserEmail(), readConfig.getHubFilaInsightsEdgeUserPassword());
        nm = new AppHomepage(driver);

        nm.openPage("Identify", "Conversion");
        verifyFrontEndRoutingForPlatform("marketshare", softAssert);

        nm.openPage("Identify", "Keyword Search");
        verifyFrontEndRoutingForPlatform("insights", softAssert);

        nm.openPage("Analyze", "Round Up");
        verifyFrontEndRoutingForPlatform("filanext", softAssert);

        nm.openPage("Analyze", "PDP Change Dashboard");
        verifyFrontEndRoutingForPlatform("filalegacy", softAssert);

        nm.openPage("Identify", "At a Glance");
        verifyFrontEndRoutingForPlatform("connect", softAssert);

        softAssert.assertAll();
        logger.info("** Test completed successfully");
    }

    private void verifyFrontEndRoutingForPlatform(String platform, SoftAssert softAssert) throws InterruptedException {
        List<String> pageRequestUrls = extractUiApiUrls(driver);
        List<String> legacyPlatformPaths = null;
        int count = 0;

        switch (platform.toLowerCase()) {
            case "insights":
                legacyPlatformPaths = Collections.singletonList("insights");
                break;
            case "marketshare":
                legacyPlatformPaths = Collections.singletonList("ms");
                break;
            case "connect":
                legacyPlatformPaths = Collections.singletonList("cockpit");
                break;
            case "filanext":
            case "filalegacy":
                legacyPlatformPaths = Arrays.asList("admin", "catalog", "advertising", "daas");
                break;
        }

        List<String> routedUrls = getRoutedUrls(pageRequestUrls, legacyPlatformPaths);

        while (routedUrls.size() == 0) {
            pageRequestUrls = extractUiApiUrls(driver);
            routedUrls = getRoutedUrls(pageRequestUrls, legacyPlatformPaths);
            Thread.sleep(500);
            if (count == 5) {
                break;
            }
            count++;
        }

        softAssert.assertTrue(routedUrls.size() > 0, "No routed calls found in this page. Either wrong env or no routing. Platform: " + platform);
    }

    private List<String> extractUiApiUrls(WebDriver driver) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;

        List<String> apiUrls = (List<String>) jsExecutor.executeScript(
                "var apiUrls = [];" +
                        "var requests = window.performance.getEntriesByType('resource');" +
                        "requests.forEach(function(request) {" +
                        "if (request.initiatorType === 'xmlhttprequest' || request.initiatorType === 'fetch') {" +
                        "apiUrls.push(request.name);" +
                        "}" +
                        "});" +
                        "return apiUrls;");

        return apiUrls;
    }

    private List<String> getRoutedUrls(List<String> pageUrls, List<String> paths) {
        List<String> routedUrls = new ArrayList<>();
        dcAppUrl = dcAppUrl.split("app")[0];
        for (String path : paths) {
            String  platformUrl = "https://external-gateway-service-api" + dcAppUrl.split("cloud")[1] + path;
            for (String pageUrl : pageUrls) {
                if (pageUrl.contains(platformUrl)){
                    routedUrls.add(pageUrl);
                }
            }
        }
        return routedUrls;
    }

    @AfterMethod
    public void killDriver() {
        quitBrowser();
    }

    @BeforeMethod()
    public void initializeBrowser(ITestContext testContext) {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
    }
}