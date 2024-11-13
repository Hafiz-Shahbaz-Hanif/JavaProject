package com.DC.uitests.hub.marketshare;

import com.DC.pageobjects.adc.identify.salesAndShare.ConversionPage;
import com.DC.pageobjects.PageHandler;
import com.DC.pageobjects.adc.AppHomepage;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.testcases.BaseClass;
import com.DC.utilities.ReadConfig;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.apiEngine.apiRequests.hub.marketshare.authservice.MarketShareAuthServiceApiRequest;
import com.DC.utilities.apiEngine.models.responses.hub.marketshare.authservice.MarketShareUserInfoResponseBody;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class HubMarketShareTokenExchangeForUsersTest extends BaseClass {

    Logger logger;
    ReadConfig readConfig;
    String dcAppUrl;

    HubMarketShareTokenExchangeForUsersTest() {
        readConfig = ReadConfig.getInstance();
        logger = Logger.getLogger(HubMarketShareTokenExchangeForUsersTest.class);
        PropertyConfigurator.configure("log4j.properties");
        dcAppUrl = readConfig.getDcAppUrl();
    }

    @Test(description = "PH-133-180 - MS accepts auth0 token and fetches MS api key for MS user - MS external user Client switch")
    public void MS_Accepts_Auth0Token_ApiKey_External_User_Client_Switch_Test() throws Exception {
        logger.info("** Test has started.");

        DCLoginPage lp = new DCLoginPage(driver);
        SoftAssert softAssert = new SoftAssert();

        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubEdgeExternalUserEmail(), readConfig.getHubEdgeExternalUserPassword());
        String authToken = "Bearer " + SecurityAPI.getAuthToken(driver);
        AppHomepage hp = new AppHomepage(driver);
        ConversionPage msm = new ConversionPage(driver);
        hp.openPage("Identify", "Conversion");

        String defaultApiKeyProfile = msm.getXapiKeyFromProfileTab();
        softAssert.assertNotNull(defaultApiKeyProfile, "default client Api key is null");

        MarketShareUserInfoResponseBody.User randomUserAssociatedWithEmail = MarketShareAuthServiceApiRequest.getRandomMsUserAssociatedWithToken(authToken);
        String apiKeyForUser = randomUserAssociatedWithEmail.getKey().getKey();
        String clientNameForUser = randomUserAssociatedWithEmail.getClient().getClientName();

        hp.openPage("Identify", "Conversion");
        hp.selectBU(clientNameForUser);

        String switchedApiKeyProfile = msm.getXapiKeyFromProfileTab();
        softAssert.assertNotEquals(defaultApiKeyProfile, switchedApiKeyProfile, "Default and switched API keys are same.");
        softAssert.assertEquals(apiKeyForUser, switchedApiKeyProfile, "API key of user/client is not matching with API key after client switch.");

        softAssert.assertAll();
        logger.info("** Test completed successfully");
    }

    @Test(description = "PH-180 - MS internal user Client switch")
    public void MS_ApiKey_Internal_User_Client_Switch_Test() throws Exception {
        logger.info("** Test has started.");

        DCLoginPage lp = new DCLoginPage(driver);
        PageHandler ph = new PageHandler(driver);
        SoftAssert softAssert = new SoftAssert();

        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubEdgeUserEmail(), readConfig.getHubEdgeUserPassword());
        AppHomepage hp = new AppHomepage(driver);
        ConversionPage msm = new ConversionPage(driver);
        hp.openPage("Identify", "Conversion");

        String defaultApiKeyProfile = msm.getXapiKeyFromProfileTab();
        softAssert.assertNotNull(defaultApiKeyProfile, "default client Api key is null");

        hp.openPage("Identify", "Conversion");
        msm.selectRandomClient();
        msm.selectRandomUser();

        String apiKeyLocalStorage = ph.getLocalStorageItemValue("switched");
        String switchedApiKeyProfile = msm.getXapiKeyFromProfileTab();
        softAssert.assertNotEquals(defaultApiKeyProfile, switchedApiKeyProfile, "Default and switched API keys are same.");
        softAssert.assertEquals(apiKeyLocalStorage, switchedApiKeyProfile, "API key of user/client is not matching with API key after client switch.");

        softAssert.assertAll();
        logger.info("** Test completed successfully");
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