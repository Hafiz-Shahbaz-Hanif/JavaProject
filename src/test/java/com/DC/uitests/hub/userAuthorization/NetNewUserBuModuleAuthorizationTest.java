package com.DC.uitests.hub.userAuthorization;

import com.DC.db.hubDbFunctions.HubDbFunctions;
import com.DC.pageobjects.adc.AppHomepage;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.manage.dataManagement.SearchTermManagementPage;
import com.DC.testcases.BaseClass;
import com.DC.utilities.ReadConfig;
import com.DC.utilities.RedisUtility;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.hub.HubCommonMethods;
import org.apache.log4j.Logger;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.Arrays;
import java.util.Map;

public class NetNewUserBuModuleAuthorizationTest extends BaseClass {

    ReadConfig readConfig;
    Logger logger;
    String dcAppUrl;
    String dcFilaLegacyAppUrl;
    String dcInsightsAppUrl;
    String prefixForRedisKey = "authContextCache::";
    String businessUnit = "McCormick US";
    String module = "Search Term Management";

    NetNewUserBuModuleAuthorizationTest() {
        readConfig = ReadConfig.getInstance();
        dcAppUrl = readConfig.getDcAppUrl();
        dcFilaLegacyAppUrl = readConfig.getDcAppFilaLegacyUrl();
        dcInsightsAppUrl = readConfig.getDcAppInsightsUrl();
        logger = Logger.getLogger(NetNewUserBuModuleAuthorizationTest.class);
    }

    @Test(description = "PNN-243 (PH-244) - User Bu Module Authorization - STM Positive")
    public void Hub_NetNew_User_Has_Authorizations_To_Bu_Modules_Api_Test() throws Exception {
        DCLoginPage lp = new DCLoginPage(driver);
        HubCommonMethods hubCommonMethods = new HubCommonMethods();
        HubDbFunctions db = new HubDbFunctions();
        RedisUtility redis = new RedisUtility();
        SoftAssert softAssert = new SoftAssert();
        String randomSearchTermOne = SharedMethods.generateRandomString();
        logger.info("Random Search Term One: " + randomSearchTermOne);

        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubFilaInsightsUserEmail(), readConfig.getHubFilaInsightsUserPassword());
        AppHomepage hp = new AppHomepage(driver);

        String auth0Id = hubCommonMethods.getAuth0IdFromToken("Bearer " +  SecurityAPI.getAuthToken(driver));
        Map<String, String> userBuModuleIds =  db.getUserBuModuleIds(auth0Id, businessUnit, module);
        String userId = userBuModuleIds.get("user_id");
        String buModuleId = userBuModuleIds.get("bu_module_id");

        redis.clearRedisCache(prefixForRedisKey + auth0Id);
        db.updateUserBuModulePermissions(true, true, true, true, userId, buModuleId);

        hp.openPage("Manage", module);
        hp.selectBU(businessUnit);
        SearchTermManagementPage stm = new SearchTermManagementPage(driver);
        softAssert.assertFalse(hp.isElementVisible(SearchTermManagementPage.SEARCH_TERM_ACTION_ERROR_MSG), "User does not have STM read permission.");

        stm.createNewSearchTerm(randomSearchTermOne, Arrays.asList("Amazon"), "DAILY", "", null);
        softAssert.assertTrue(hp.isElementVisible(SearchTermManagementPage.SEARCH_TERM_ACTION_SUCCESS_MSG), "User does not have STM create permission.");
        hp.waitForElementToBeInvisible(SearchTermManagementPage.SEARCH_TERM_ACTION_SUCCESS_MSG);

        stm.updateSearchTerm(randomSearchTermOne);
        softAssert.assertTrue(hp.isElementVisible(SearchTermManagementPage.SEARCH_TERM_ACTION_SUCCESS_MSG), "User does not have STM update permission.");
        hp.waitForElementToBeInvisible(SearchTermManagementPage.SEARCH_TERM_ACTION_SUCCESS_MSG);

        stm.deleteSearchTerm(randomSearchTermOne);
        softAssert.assertTrue(hp.isElementVisible(SearchTermManagementPage.SEARCH_TERM_ACTION_SUCCESS_MSG), "User does not have STM delete permission.");
        hp.waitForElementToBeInvisible(SearchTermManagementPage.SEARCH_TERM_ACTION_SUCCESS_MSG);
        softAssert.assertAll();
    }

    @Test(description = "PNN-243 (PH-244) - User Bu Module Authorization - STM Negative")
    public void Hub_NetNew_User_Does_Not_Have_Authorizations_To_Bu_Modules_Api_Test() throws Exception {
        DCLoginPage lp = new DCLoginPage(driver);
        HubCommonMethods hubCommonMethods = new HubCommonMethods();
        HubDbFunctions db = new HubDbFunctions();
        RedisUtility redis = new RedisUtility();
        SoftAssert softAssert = new SoftAssert();
        String randomSearchTermOne = SharedMethods.generateRandomString();
        String randomSearchTermTwo = SharedMethods.generateRandomString();
        logger.info("Random Search Term I: " + randomSearchTermOne + " / Random Search Term II" + randomSearchTermTwo);

        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubFilaInsightsUserEmail(), readConfig.getHubFilaInsightsUserPassword());
        AppHomepage hp = new AppHomepage(driver);

        String auth0Id = hubCommonMethods.getAuth0IdFromToken("Bearer " +  SecurityAPI.getAuthToken(driver));
        Map<String, String> userBuModuleIds =  db.getUserBuModuleIds(auth0Id, businessUnit, module);
        String userId = userBuModuleIds.get("user_id");
        String buModuleId = userBuModuleIds.get("bu_module_id");

        redis.clearRedisCache(prefixForRedisKey + auth0Id);
        db.updateUserBuModulePermissions(true, true, true, true, userId, buModuleId);
        hp.openPage("Manage", module);
        hp.selectBU(businessUnit);
        SearchTermManagementPage stm = new SearchTermManagementPage(driver);
        stm.createNewSearchTerm(randomSearchTermOne, Arrays.asList("Amazon"), "DAILY", "", null);

        redis.clearRedisCache(prefixForRedisKey + auth0Id);
        db.updateUserBuModulePermissions(false, true, false, false, userId, buModuleId);

        stm.createNewSearchTerm(randomSearchTermTwo, Arrays.asList("Amazon"), "DAILY", "", null);
        softAssert.assertTrue(hp.isElementVisible(SearchTermManagementPage.SEARCH_TERM_ACTION_ERROR_MSG), "User has STM create permission.");
        hp.waitForElementToBeInvisible(SearchTermManagementPage.SEARCH_TERM_ACTION_ERROR_MSG);

        stm.updateSearchTerm(randomSearchTermOne);
        softAssert.assertTrue(hp.isElementVisible(SearchTermManagementPage.SEARCH_TERM_ACTION_ERROR_MSG), "User has STM update permission.");
        hp.waitForElementToBeInvisible(SearchTermManagementPage.SEARCH_TERM_ACTION_ERROR_MSG);

        stm.deleteSearchTerm(randomSearchTermOne);
        softAssert.assertTrue(hp.isElementVisible(SearchTermManagementPage.SEARCH_TERM_ACTION_ERROR_MSG), "User has STM delete permission.");
        hp.waitForElementToBeInvisible(SearchTermManagementPage.SEARCH_TERM_ACTION_ERROR_MSG);

        redis.clearRedisCache(prefixForRedisKey + auth0Id);
        db.updateUserBuModulePermissions(false, false, false, false, userId, buModuleId);

        hp.refreshPage();
        softAssert.assertTrue(hp.isElementVisible(SearchTermManagementPage.SEARCH_TERM_ACTION_ERROR_MSG), "User has STM read permission.");
        hp.waitForElementToBeInvisible(SearchTermManagementPage.SEARCH_TERM_ACTION_ERROR_MSG);
        softAssert.assertAll();
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