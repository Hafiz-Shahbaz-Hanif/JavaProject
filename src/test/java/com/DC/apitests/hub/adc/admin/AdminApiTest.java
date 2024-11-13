package com.DC.apitests.hub.adc.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.DC.pageobjects.adc.AppHomepage;
import com.DC.testcases.BaseClass;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import com.DC.utilities.hub.FilaUser;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.utilities.ReadConfig;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.apiEngine.apiRequests.adc.admin.AdminApiRequests;
import com.DC.utilities.apiEngine.models.responses.adc.admin.AdminUserInfoResponseBody;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AdminApiTest extends BaseClass {

    Logger logger;
    ReadConfig readConfig;
    String auth0Token;
    BaseClass base;
    WebDriver driver;
    boolean headless;
    String dcAppUrl;
    String dcFilaLegacyAppUrl;
    FilaUser filaUser;

    AdminApiTest() throws IOException {
        logger = Logger.getLogger(AdminApiTest.class);
        readConfig = ReadConfig.getInstance();
        PropertyConfigurator.configure("log4j.properties");
        base = new BaseClass();
        headless = readConfig.getHeadlessMode();
        dcAppUrl = readConfig.getDcAppUrl();
        dcFilaLegacyAppUrl = readConfig.getDcAppFilaLegacyUrl();
        filaUser = new FilaUser();
    }

    @Test(description = "PH-116 - Verify admin user info response data")
    public void Hub_ADC_AdminUserInfo_Api_Test() throws Exception {

        logger.info("** Auth0 token for Admin User update test has started.");
        auth0Token = "Bearer " + SecurityAPI.loginToDcAppToGetAuthToken(driver, readConfig.getHubFilaOnlyUserEmail(), readConfig.getHubFilaOnlyUserPassword());
        ObjectMapper mapper = new ObjectMapper();

        String auiResponse = AdminApiRequests.getAdminUserInfoString(auth0Token);
        AdminUserInfoResponseBody auiResponseBody = mapper.readerFor(AdminUserInfoResponseBody.class).readValue(auiResponse);
        Assert.assertNotNull(auiResponseBody.getProperties().get("userId"), "** User id is null");
        Assert.assertNotNull(auiResponseBody.getProperties().get("userName"), "** User name is null");
        Assert.assertNotNull(auiResponseBody.getProperties().get("userEmail"), "** User email is null");
        Assert.assertNotNull(auiResponseBody.getProperties().get("theme"), "** Theme is null");
        Assert.assertNotNull(auiResponseBody.getProperties().get("externalUser"), "** External user info is null");

        JSONObject auiResponseJsonObject = new JSONObject(auiResponse);

        JSONArray userModules = auiResponseJsonObject.getJSONArray("userModules");
        Assert.assertNotNull(userModules, "** User modules is null");
        Assert.assertFalse(userModules.isEmpty(), "** User modules is empty");

        JSONArray businessUnitIds = auiResponseJsonObject.getJSONArray("businessUnitIds");
        Assert.assertNotNull(businessUnitIds, "** Business unit ids is null");
        Assert.assertFalse(businessUnitIds.isEmpty(), "** Business unit ids is empty");

        List<Integer> buIdsList = businessUnitIds.toList().stream().map(element -> (Integer) element).collect(Collectors.toList());
        JSONObject retailerPlatformsByBUIds = auiResponseJsonObject.getJSONObject("retailerPlatformsByBUIds");
        Assert.assertNotNull(retailerPlatformsByBUIds, "** Retailer platforms by BU ids is null");

        Map<String, Object> retailerPlatformsByBUIdsMap = retailerPlatformsByBUIds.toMap();
        List<Integer> buIdsInRetailerPlatforms = new ArrayList<>(retailerPlatformsByBUIdsMap.keySet()).stream().map(s -> Integer.parseInt(s)).collect(Collectors.toList());
        Collections.sort(buIdsInRetailerPlatforms);
        Collections.sort(buIdsList);

        Assert.assertTrue(buIdsList.equals(buIdsInRetailerPlatforms), "** BU ids in 'businessUnitIds' and 'retailerPlatformsByBUIds' properties do not match");
        logger.info("** Execution for test has completed successfully");
    }

    @Test(description = "PH-259-262 - Fila User Info Reflects on the UI Immediately After Update")
    public void Hub_Fila_User_Info_Reflects_On_Ui_After_Update_Test() throws Exception {
        logger.info("** Test has started.");

        SoftAssert softAssert = new SoftAssert();
        DCLoginPage lp = new DCLoginPage(driver);

        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubFilaOnlyUserEmail(), readConfig.getHubFilaOnlyUserPassword());
        String authToken = "Bearer " + SecurityAPI.getAuthToken(driver);
        AppHomepage hp = new AppHomepage(driver);

        String userRolesIds = filaUser.getFilaRoles(authToken,"Internal Only: User Administration", "Internal Only: Role Administration");
        filaUser.updateFilaUser(authToken, userRolesIds, true, true);
        hp.refreshPage();

        hp.openModule("Analyze");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Round Up")), "User still has access to Round Up.");

        userRolesIds = filaUser.getFilaRoles(authToken,"Retail: Roundup Report", "Internal Only: User Administration", "Internal Only: Role Administration");
        filaUser.updateFilaUser(authToken, userRolesIds,true, true);
        hp.refreshPage();

        hp.openModule("Analyze");
        softAssert.assertTrue(lp.isElementVisible(hp.createLinkLocator("Round Up")), "User does not have access to Round Up.");

        softAssert.assertAll();
        logger.info("** Test completed successfully");
    }

    @AfterMethod
    public void killDriver() {
        quitBrowser();
    }

    @BeforeMethod()
    public void initializeBrowser(ITestContext testContext) {
        driver = initializeBrowser(testContext, readConfig.getHeadlessMode());
    }
}