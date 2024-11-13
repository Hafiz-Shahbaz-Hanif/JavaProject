package com.DC.apitests.hub.insights;

import com.DC.db.hubDbFunctions.HubDbFunctions;
import com.DC.pageobjects.adc.AppHomepage;
import com.DC.testcases.BaseClass;
import com.DC.utilities.RedisUtility;
import com.DC.utilities.hub.InsightsMethods;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.utilities.PostgreSqlUtility;
import com.DC.utilities.ReadConfig;
import com.DC.utilities.SecurityAPI;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class HubRedisCachingForInsightsUserApiTest {

    Logger logger;
    ReadConfig readConfig;
    BaseClass base;
    WebDriver driver;
    String insightsSupportUsername;
    String insightsSupportPassword;
    String header = "Content-Type=application/json";
    boolean headless;
    String dcAppUrl;
    PostgreSqlUtility pu;

    HubRedisCachingForInsightsUserApiTest() {
        logger = Logger.getLogger(HubRedisCachingForInsightsUserApiTest.class);
        readConfig = ReadConfig.getInstance();
        pu = new PostgreSqlUtility();
        PropertyConfigurator.configure("log4j.properties");
        base = new BaseClass();
        headless = readConfig.getHeadlessMode();
        dcAppUrl = readConfig.getDcAppUrl();
    }

    @BeforeClass
    private void setUp(ITestContext testContext) {
        insightsSupportUsername = readConfig.getHubInsightsSupportUsername();
        insightsSupportPassword = readConfig.getHubInsightsSupportUserPassword();
    }

    @AfterClass
    public void killDriver() {
        base.quitBrowser();
    }

    @Test(description = "PH-123-285-118 - Hub Insights Support User Company Switch JWT Storage")
    public void Hub_Insights_Support_User_CompanySwitch_Jwt_Storage_Test(ITestContext testContext) throws Exception {
        logger.info("** Test has started.");

        RedisUtility redis = new RedisUtility();
        HubDbFunctions db = new HubDbFunctions();
        SoftAssert softAssert = new SoftAssert();
        driver = base.initializeBrowser(testContext, headless);
        DCLoginPage lp = new DCLoginPage(driver);

        String redisKey = "oneSpaceTokenCache::" + db.getUserAuth0Id(insightsSupportUsername);
        redis.clearRedisCache(redisKey);

        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(insightsSupportUsername, insightsSupportPassword);
        String insightsSupportUserAuthToken = "Bearer " + SecurityAPI.getAuthToken(driver);
        InsightsMethods im = new InsightsMethods();
        AppHomepage hp = new AppHomepage(driver);

        // will be removed after a bug fix for another test
        lp.refreshPage();
        Thread.sleep(2000);
        lp.refreshPage();

        String defaultCompanyRedisCache = redis.getRedisCache(redisKey);
        JSONArray companiesForUser = im.getCompaniesForUser(insightsSupportUserAuthToken, header);
        String defaultCompanyIdOnUi = im.getDefaultCompanyIdForUser(companiesForUser, driver);

        String switchedCompanyApiResponse = im.switchToRandomCompany(insightsSupportUserAuthToken, companiesForUser);
        String switchedCompanyRedisCache = redis.getRedisCache(redisKey);
        softAssert.assertEquals(switchedCompanyApiResponse, switchedCompanyRedisCache, "Jwt in Redis and Api response do not match.");
        softAssert.assertNotEquals(defaultCompanyRedisCache, switchedCompanyRedisCache, "Jwt in Redis expected to differ after switching company.");

        hp.goToHomePage();
        lp.refreshPage();

        String switchedCompanyIdOnUi = im.getDefaultCompanyIdForUser(companiesForUser, driver);
        softAssert.assertNotEquals(defaultCompanyIdOnUi, switchedCompanyIdOnUi, "Company id on UI expected to differ after switching company.");

        softAssert.assertAll();
        logger.info("** Test completed successfully");
    }

}