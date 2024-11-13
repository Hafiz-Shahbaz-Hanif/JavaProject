package com.DC.uitests.hub.authlogin;

import com.DC.apitests.hub.authservice.AuthServiceApiTest;
import com.DC.db.hubDbFunctions.HubDbFunctions;
import com.DC.utilities.apiEngine.apiRequests.hub.authservice.AuthServiceApiRequest;
import com.DC.utilities.apiEngine.apiRequests.hub.insights.authservice.InsightsAuthServiceApiRequest;
import com.DC.utilities.apiEngine.models.requests.hub.insights.authservice.HubInsightsRolesRequestBody;
import com.DC.utilities.hub.*;
import com.DC.pageobjects.adc.identify.salesAndShare.ConversionPage;
import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.utilities.SharedMethods;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import com.DC.pageobjects.adc.AppHomepage;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.testcases.BaseClass;
import com.DC.utilities.ReadConfig;
import com.DC.utilities.SecurityAPI;

public class LoginTest {

    HomepageReports homepageReports;
    HomepageReports.Identify identify;
    HomepageReports.Analyze analyze;
    HomepageReports.Manage manage;
    HomepageReports.UserManagement userManagement;
    BaseClass base;
    ReadConfig readConfig;
    WebDriver driver;
    String dcAppUrl;
    String dcFilaLegacyAppUrl;
    String dcInsightsAppUrl;
    Logger logger;
    boolean headless;
    String edgeTokenForAdmin;
    String authTokenForSupportUser;
    MsMethods msMethods;

    @BeforeClass
    private void setUp (ITestContext testContext) throws Exception {
        msMethods = new MsMethods();
        readConfig = ReadConfig.getInstance();
        dcAppUrl = readConfig.getDcAppUrl();
        dcFilaLegacyAppUrl = readConfig.getDcAppFilaLegacyUrl();
        dcInsightsAppUrl = readConfig.getDcAppInsightsUrl();
        base = new BaseClass();
        logger = Logger.getLogger(AuthServiceApiTest.class);
        headless = readConfig.getHeadlessMode();
        homepageReports = new HomepageReports();
        identify = homepageReports.new Identify();
        analyze = homepageReports.new Analyze();
        manage = homepageReports.new Manage();
        userManagement = homepageReports.new UserManagement();
        driver = base.initializeBrowser(testContext, headless);
        edgeTokenForAdmin = "Bearer " +  SecurityAPI.loginToDcAppToGetAuthToken(driver, readConfig.getHubEdgeUserEmail(), readConfig.getHubEdgeUserPassword());
        driver = base.initializeBrowser(testContext, headless);
        authTokenForSupportUser = "Bearer " +  SecurityAPI.loginToDcAppToGetAuthToken(driver, readConfig.getHubInsightsSupportUsername(), readConfig.getHubInsightsSupportUserPassword());
    }

    @Test(description = "PH-186-494")
    public void Fila_FE_Permissions_Homepage_Test(ITestContext testContext) throws Exception {
        SoftAssert softAssert = new SoftAssert();
        FilaUser filaUser = new FilaUser();

        driver = base.initializeBrowser(testContext, headless);
        DCLoginPage lp = new DCLoginPage(driver);
        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubFilaQaUserEmail(), readConfig.getHubFilaQaUserPassword());
        AppHomepage hp = new AppHomepage(driver);

        String authToken = "Bearer " + SecurityAPI.getAuthToken(driver);
        String userRolesIds = filaUser.getFilaRoles(authToken,"Internal Only: User Administration", "Internal Only: Role Administration", "Search: Frequency Clicks & Conversion Share", "Media: Scratchpad", "AMC Path to Purchase Dashboard", "P&G External Pilot: Campaign Segmentation", "Internal Only: Ensemble Management");
        filaUser.updateFilaUser(authToken, userRolesIds, true, true);
        refreshToGetUpdatesOnUi(hp, authToken, "fila");

        List<String> searchInsightsUpdate = identify.getNetNewSearchInsights();
        searchInsightsUpdate.add("Frequency, Clicks & Conversion Share");
        List<String> dataManagementUpdate = manage.getNetNewDataManagement();
        dataManagementUpdate.add("Ensemble Management");
        Collections.sort(dataManagementUpdate);
        Collections.sort(searchInsightsUpdate);

        List<String> marketingCloudAnalyticsUpdate = List.of("Path to Purchase");
        List<String> paidMediaReportingUpdate = List.of("Media Scratchpad");
        List<String> mediaManagementUpdate = List.of("Campaign Segmentation");

        softAssert.assertEquals(searchInsightsUpdate, hp.getModulePagesBySection("Identify", "Search Insights"), "User permissions do not match for Search Insights.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Executive Dashboard")), "Executive Dashboard is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Sales & Share")), "Sales & Share is visible.");

        softAssert.assertEquals(analyze.getFilaRetailReportingForAllFilaUsers(), hp.getModulePagesBySection("Analyze", "Retail Reporting"), "User permissions do not match for Retail Reporting.");
        softAssert.assertEquals(analyze.getNetNewProductHealth(), hp.getModulePagesBySection("Analyze", "Product Health"), "User permissions do not match for Product Health.");
        softAssert.assertEquals(analyze.getNetNewSearchReporting(), hp.getModulePagesBySection("Analyze", "Search Reporting"), "User permissions do not match for Search Reporting.");
        softAssert.assertEquals(marketingCloudAnalyticsUpdate, hp.getModulePagesBySection("Analyze", "Marketing Cloud Analytics"), "User permissions do not match for Marketing Cloud Analytics.");
        softAssert.assertEquals(paidMediaReportingUpdate, hp.getModulePagesBySection("Analyze", "Paid Media Reporting"), "User permissions do not match for Paid Media Reporting.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Data As Service")), "Data As Service is visible.");

        softAssert.assertEquals(mediaManagementUpdate, hp.getModulePagesBySection("Execute", "Media Management"), "User permissions do not match for Media Management.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Content Optimization")), "Content Optimization is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Integrations")), "Integrations is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Product Manager")), "Product Manager is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Retail Management")), "Retail Management is visible.");

        softAssert.assertEquals(dataManagementUpdate, hp.getModulePagesBySection("Manage", "Data Management"), "User permissions do not match for Data Management.");
        softAssert.assertAll();
    }

    @Test(description = "PH-186-494")
    public void Fila_FE_Permissions_SearchBar_Test(ITestContext testContext) throws Exception {
        SoftAssert softAssert = new SoftAssert();
        FilaUser filaUser = new FilaUser();

        driver = base.initializeBrowser(testContext, headless);
        DCLoginPage lp = new DCLoginPage(driver);
        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubFilaQaUserEmail(), readConfig.getHubFilaQaUserPassword());
        AppHomepage hp = new AppHomepage(driver);

        String authToken = "Bearer " + SecurityAPI.getAuthToken(driver);
        String userRolesIds = filaUser.getFilaRoles(authToken,"Internal Only: User Administration", "Internal Only: Role Administration", "Media: Scratchpad", "AMC Path to Purchase Dashboard", "P&G External Pilot: Campaign Segmentation");
        filaUser.updateFilaUser(authToken, userRolesIds, true, true);
        refreshToGetUpdatesOnUi(hp, authToken, "fila");

        List<String> searchInsights = identify.getNetNewSearchInsights();
        List<String> dataManagement = manage.getNetNewDataManagement();
        List<String> productHealth = analyze.getNetNewProductHealth();
        List<String> searchReporting = analyze.getNetNewSearchReporting();
        List<String> retailReportingForAllFilaUsers = analyze.getFilaRetailReportingForAllFilaUsers();
        List<String> marketingCloudAnalytics = List.of("Path to Purchase");
        List<String> paidMediaReporting = List.of("Media Scratchpad");
        List<String> mediaManagement = List.of("Campaign Segmentation");
        List<String> clientAdministration = List.of("DC App User Management", "DC App Role Management");
        List<String> accountManagement = List.of("Profile Information", "Request Support");

        List<String> searchBarReports = new ArrayList<>();
        searchBarReports.addAll(searchInsights);
        searchBarReports.addAll(dataManagement);
        searchBarReports.addAll(productHealth);
        searchBarReports.addAll(paidMediaReporting);
        searchBarReports.addAll(mediaManagement);
        searchBarReports.addAll(searchReporting);
        searchBarReports.addAll(retailReportingForAllFilaUsers);
        searchBarReports.addAll(marketingCloudAnalytics);
        searchBarReports.addAll(clientAdministration);
        searchBarReports.addAll(accountManagement);
        Collections.sort(searchBarReports);

        List<String>  uiSearchBarReports = hp.getSearchBarReports();
        softAssert.assertEquals(searchBarReports, uiSearchBarReports, "Search Bar reports do not match.");
        softAssert.assertAll();
    }

    @Test(description = "PH-186-494")
    public void Fila_FE_Permissions_NavBar_Test(ITestContext testContext) throws Exception {
        SoftAssert softAssert = new SoftAssert();
        FilaUser filaUser = new FilaUser();

        driver = base.initializeBrowser(testContext, headless);
        DCLoginPage lp = new DCLoginPage(driver);
        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubFilaQaUserEmail(), readConfig.getHubFilaQaUserPassword());
        AppHomepage hp = new AppHomepage(driver);

        String authToken = "Bearer " + SecurityAPI.getAuthToken(driver);
        String userRolesIds = filaUser.getFilaRoles(authToken,"Internal Only: User Administration", "Internal Only: Role Administration", "Search: Frequency Clicks & Conversion Share", "Media: Scratchpad", "AMC Path to Purchase Dashboard", "P&G External Pilot: Campaign Segmentation", "Internal Only: Ensemble Management");
        filaUser.updateFilaUser(authToken, userRolesIds, true, true);
        refreshToGetUpdatesOnUi(hp, authToken, "fila");

        softAssert.assertEquals(hp.getModulePagesBySection("Identify", "Search Insights"), hp.getNavbarPagesBySection("Identify", "Search Insights"), "Module and Navbar pages do not match for Search Insights.");
        softAssert.assertEquals(hp.getModulePagesBySection("Analyze", "Retail Reporting"), hp.getNavbarPagesBySection("Analyze", "Retail Reporting"), "Module and Navbar pages do not match for Retail Reporting.");
        softAssert.assertEquals(hp.getModulePagesBySection("Analyze", "Product Health"), hp.getNavbarPagesBySection("Analyze", "Product Health"), "Module and Navbar pages do not match for Product Health.");
        softAssert.assertEquals(hp.getModulePagesBySection("Analyze", "Search Reporting"), hp.getNavbarPagesBySection("Analyze", "Search Reporting"), "Module and Navbar pages do not match for Search Reporting.");
        softAssert.assertEquals(hp.getModulePagesBySection("Analyze", "Paid Media Reporting"), hp.getNavbarPagesBySection("Analyze", "Paid Media Reporting"), "Module and Navbar pages do not match for Paid Media Reporting.");
        softAssert.assertEquals(hp.getModulePagesBySection("Analyze", "Marketing Cloud Analytics"), hp.getNavbarPagesBySection("Analyze", "Marketing Cloud Analytics"), "Module and Navbar pages do not match for Marketing Cloud Analytics.");
        softAssert.assertEquals(hp.getModulePagesBySection("Execute", "Media Management"), hp.getNavbarPagesBySection("Execute", "Media Management"), "Module and Navbar pages do not match for Media Management.");
        softAssert.assertEquals(hp.getModulePagesBySection("Manage", "Data Management"), hp.getNavbarPagesBySection("Manage", "Data Management"), "Module and Navbar pages do not match for Data Management.");

        hp.clickOnNavbarModule("Identify");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createNavbarLinkLocator("Executive Dashboard")), "Executive Dashboard is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createNavbarLinkLocator("Sales & Share")), "Sales & Share is visible.");

        hp.clickOnNavbarModule("Analyze");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createNavbarLinkLocator("Data As Service")), "Data As Service is visible.");

        hp.clickOnNavbarModule("Execute");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createNavbarLinkLocator("Content Optimization")), "Content Optimization is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createNavbarLinkLocator("Integrations")), "Integrations is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createNavbarLinkLocator("Product Manager")), "Product Manager is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createNavbarLinkLocator("Retail Management")), "Retail Management is visible.");
        softAssert.assertAll();
    }

    @Test(description = "PH-186-494")
    public void Fila_FE_Permissions_UserProfile_Test(ITestContext testContext) throws Exception {
        SoftAssert softAssert = new SoftAssert();
        FilaUser filaUser = new FilaUser();

        driver = base.initializeBrowser(testContext, headless);
        String	userAuthTokenWithAdminRoles = "Bearer " +  SecurityAPI.loginToDcAppToGetAuthToken(driver, readConfig.getHubFilaOnlyUserEmail(), readConfig.getHubFilaOnlyUserPassword());

        driver = base.initializeBrowser(testContext, headless);
        DCLoginPage lp = new DCLoginPage(driver);
        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubFilaQaUserEmail(), readConfig.getHubFilaQaUserPassword());
        AppHomepage hp = new AppHomepage(driver);
        String authTokenForUserToUpdate = "Bearer " + SecurityAPI.getAuthToken(driver);

        List<String> userProfileClientAdminPages = userManagement.getFilaAdminUserClientAdminPages();
        List<String> userProfileClientManagementPages = userManagement.getFilaClientManagementPages();

        String userRolesIds = filaUser.getFilaRoles(userAuthTokenWithAdminRoles, "Media: Scratchpad");
        filaUser.updateNonAdminFilaUser(userAuthTokenWithAdminRoles, authTokenForUserToUpdate, userRolesIds, true, true);
        refreshToGetUpdatesOnUi(hp, authTokenForUserToUpdate, "fila");

        lp.openUserProfile();
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Client Administration")), "Client Administration is visible.");

        userRolesIds = filaUser.getFilaRoles(userAuthTokenWithAdminRoles, "Internal Only: Base User", "Internal Only: Client Administration", "Internal Only: User Administration", "Internal Only: Role Administration", "Media: Scratchpad");
        filaUser.updateNonAdminFilaUser(userAuthTokenWithAdminRoles, authTokenForUserToUpdate, userRolesIds, true, true);
        refreshToGetUpdatesOnUi(hp, authTokenForUserToUpdate, "fila");

        lp.openUserProfile();
        softAssert.assertEquals(userProfileClientAdminPages, hp.getUserProfilePagesBySection("Client Administration"), "User permissions do not match for Client Administration.");
        softAssert.assertEquals(userProfileClientManagementPages, hp.getUserProfilePagesBySection("Account Management"), "User permissions do not match for Account Management.");

        List<WebElement> clientAdminPages = lp.findElementsVisible(NetNewNavigationMenu.CLIENT_ADMIN_PAGES);
        WebElement page = (WebElement) SharedMethods.getRandomItemFromList(clientAdminPages);
        String pageName = page.getText().contains("DC App") ? page.getText().split(" App ")[1] : page.getText();
        page.click();
        softAssert.assertTrue(lp.isElementVisible(hp.createLinkLocator(pageName)), pageName + " page is not visible.");

        hp.goToHomePage();
        lp.openUserProfile();
        hp.click(NetNewNavigationMenu.NOTIFICATIONS);

        String url = hp.getCurrentUrl();
        softAssert.assertTrue(url.contains("/account?tab=notifications&secondaryTab=media"), "Notifications Url for Fila user is wrong. Url: " + url);
        softAssert.assertAll();
    }

    @Test(description = "PH-186-187-494")
    public void Fila_FE_Permissions_Navigate_To_Pages_Test(ITestContext testContext) throws Exception {
        SoftAssert softAssert = new SoftAssert();
        FilaUser filaUser = new FilaUser();

        driver = base.initializeBrowser(testContext, headless);
        DCLoginPage lp = new DCLoginPage(driver);
        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubFilaOnlyUserEmail(), readConfig.getHubFilaOnlyUserPassword());
        AppHomepage hp = new AppHomepage(driver);

        String authToken = "Bearer " + SecurityAPI.getAuthToken(driver);
        restoreFilaUser(filaUser, authToken);
        refreshToGetUpdatesOnUi(hp, authToken, "fila");

        for (int i = 0; i < 10; i++){
            boolean flag = false;
            hp.goToHomePage();
            List<WebElement> modules = lp.findElementsVisible(AppHomepage.MODULES);
            WebElement module = (WebElement) SharedMethods.getRandomItemFromList(modules);
            module.click();

            List<WebElement> pages = lp.findElementsVisible(AppHomepage.PAGES);
            for (WebElement page : pages) {
                String url = page.findElement(By.xpath("./a")).getAttribute("href");
                if (url.contains("#") || url.contains("advertising") || url.contains("daas") || url.contains("catalog")) {
                    flag = true;
                    String pageName = page.getText();

                    if (pageName.equalsIgnoreCase("Long Term Value")) {
                        pageName = "Lifetime Value";
                    } else if (pageName.equalsIgnoreCase("Rule-Based Bidding")) {
                        pageName = "Rule Based Bidding";
                    }

                    page.click();
                    softAssert.assertTrue(lp.isElementVisible(hp.createLinkLocator(pageName)), pageName + " page is not loading.");
                    break;
                }
            }
            if (flag) {
                break;
            }
        }

        lp.logoutDcApp();
        softAssert.assertTrue(hp.isElementVisible(DCLoginPage.LOGIN_BUTTON), "Log out not successful.");
        softAssert.assertAll();
    }

    @Test(description = "PH-184-494")
    public void Insights_FE_Permissions_Homepage_Test(ITestContext testContext) throws Exception {
        SoftAssert softAssert = new SoftAssert();

        driver = base.initializeBrowser(testContext, headless);
        DCLoginPage lp = new DCLoginPage(driver);
        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubInsightsUserEmail(), readConfig.getHubInsightsUserPassword());
        InsightsMethods insightsMethods = new InsightsMethods();
        AppHomepage hp = new AppHomepage(driver);

        String authToken = "Bearer " + SecurityAPI.getAuthToken(driver);
        String nonSupportUserJwt = insightsMethods.getJwtForInsightsUser(authToken);
        String nonSupportUserId  = insightsMethods.decodeInsightsJwtAndGetUserId(nonSupportUserJwt);
        String nonSupportUserCompanyId  = insightsMethods.decodeInsightsJwtToGetCompanyId(nonSupportUserJwt);

        String jwt = insightsMethods.switchCompany(authTokenForSupportUser, nonSupportUserCompanyId);
        HubInsightsRolesRequestBody requestBody = new HubInsightsRolesRequestBody(List.of("insights-user", "CPG"));
        InsightsAuthServiceApiRequest.updateUserRoles(requestBody, nonSupportUserId, jwt);
        refreshToGetUpdatesOnUi(hp, authToken, "fila");

        List<String> searchInsights = identify.getOsNetNewSearchInsights();
        List<String> searchReporting = analyze.getNetNewSearchReporting();
        List<String> productHealth = analyze.getNetNewProductHealth();
        List<String> dataManagement = manage.getNetNewDataManagement();

        softAssert.assertEquals(searchInsights, hp.getModulePagesBySection("Identify", "Search Insights"), "User permissions do not match for Search Insights.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Executive Dashboard")), "Executive Dashboard is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Sales & Share")), "Sales & Share is visible.");

        softAssert.assertEquals(searchReporting, hp.getModulePagesBySection("Analyze", "Search Reporting"), "User permissions do not match for Search Reporting.");
        softAssert.assertEquals(productHealth, hp.getModulePagesBySection("Analyze", "Product Health"), "User permissions do not match for Product Health.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Marketing Cloud Analytics")), "Marketing Cloud Analytics is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Paid Media Reporting")), "Paid Media Reporting is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Data As Service")), "Data As Service is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Retail Reporting")), "Retail Reporting is visible.");

        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Execute")), "Execute module is visible.");
        softAssert.assertEquals(dataManagement, hp.getModulePagesBySection("Manage", "Data Management"), "User permissions do not match for Data Management.");
        softAssert.assertAll();
    }

    @Test(description = "PH-184-494")
    public void Insights_FE_Permissions_SearchBar_Test(ITestContext testContext) throws Exception {
        SoftAssert softAssert = new SoftAssert();

        driver = base.initializeBrowser(testContext, headless);
        DCLoginPage lp = new DCLoginPage(driver);
        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubInsightsUserEmail(), readConfig.getHubInsightsUserPassword());
        InsightsMethods insightsMethods = new InsightsMethods();
        AppHomepage hp = new AppHomepage(driver);

        String authToken = "Bearer " + SecurityAPI.getAuthToken(driver);
        String nonSupportUserJwt = insightsMethods.getJwtForInsightsUser(authToken);
        String nonSupportUserId  = insightsMethods.decodeInsightsJwtAndGetUserId(nonSupportUserJwt);
        String nonSupportUserCompanyId  = insightsMethods.decodeInsightsJwtToGetCompanyId(nonSupportUserJwt);

        String jwt = insightsMethods.switchCompany(authTokenForSupportUser, nonSupportUserCompanyId);
        HubInsightsRolesRequestBody requestBody = new HubInsightsRolesRequestBody(List.of("insights-user", "CPG", "CPG-deployment-api"));
        InsightsAuthServiceApiRequest.updateUserRoles(requestBody, nonSupportUserId, jwt);
        refreshToGetUpdatesOnUi(hp, authToken, "fila");

        List<String> searchInsights = identify.getOsNetNewSearchInsights();
        List<String> dataManagement = manage.getNetNewDataManagement();
        List<String> productHealth = analyze.getNetNewProductHealth();
        List<String> searchReporting = analyze.getNetNewSearchReporting();
        List<String> accountManagement = List.of("API Credentials", "Profile Information", "Notifications", "Request Support");

        List<String> searchBarReports = new ArrayList<>();
        searchBarReports.addAll(searchInsights);
        searchBarReports.addAll(dataManagement);
        searchBarReports.addAll(productHealth);
        searchBarReports.addAll(searchReporting);
        searchBarReports.addAll(accountManagement);
        Collections.sort(searchBarReports);

        List<String>  uiSearchBarReports = hp.getSearchBarReports();
        softAssert.assertEquals(searchBarReports, uiSearchBarReports, "Search Bar reports do not match.");
        softAssert.assertAll();
    }

    @Test(description = "PH-184-494")
    public void Insights_FE_Permissions_NavBar_Test(ITestContext testContext) throws Exception {
        SoftAssert softAssert = new SoftAssert();

        driver = base.initializeBrowser(testContext, headless);
        DCLoginPage lp = new DCLoginPage(driver);
        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubInsightsUserEmail(), readConfig.getHubInsightsUserPassword());
        InsightsMethods insightsMethods = new InsightsMethods();
        AppHomepage hp = new AppHomepage(driver);

        String authToken = "Bearer " + SecurityAPI.getAuthToken(driver);
        String nonSupportUserJwt = insightsMethods.getJwtForInsightsUser(authToken);
        String nonSupportUserId  = insightsMethods.decodeInsightsJwtAndGetUserId(nonSupportUserJwt);
        String nonSupportUserCompanyId  = insightsMethods.decodeInsightsJwtToGetCompanyId(nonSupportUserJwt);

        String jwt = insightsMethods.switchCompany(authTokenForSupportUser, nonSupportUserCompanyId);
        HubInsightsRolesRequestBody requestBody = new HubInsightsRolesRequestBody(List.of("insights-user", "CPG"));
        InsightsAuthServiceApiRequest.updateUserRoles(requestBody, nonSupportUserId, jwt);
        refreshToGetUpdatesOnUi(hp, authToken, "onespace");

        softAssert.assertEquals(hp.getModulePagesBySection("Identify", "Search Insights"), hp.getNavbarPagesBySection("Identify", "Search Insights"), "Module and Navbar pages do not match for Search Insights.");
        softAssert.assertEquals(hp.getModulePagesBySection("Analyze", "Product Health"), hp.getNavbarPagesBySection("Analyze", "Product Health"), "Module and Navbar pages do not match for Product Health.");
        softAssert.assertEquals(hp.getModulePagesBySection("Analyze", "Search Reporting"), hp.getNavbarPagesBySection("Analyze", "Search Reporting"), "Module and Navbar pages do not match for Search Reporting.");
        softAssert.assertTrue(hp.isElementNotVisible(By.xpath("//button[text()='Execute']")), "Execute dropdown is visible.");
        softAssert.assertEquals(hp.getModulePagesBySection("Manage", "Data Management"), hp.getNavbarPagesBySection("Manage", "Data Management"), "Module and Navbar pages do not match for Data Management.");

        hp.clickOnNavbarModule("Identify");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createNavbarLinkLocator("Executive Dashboard")), "Executive Dashboard is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createNavbarLinkLocator("Sales & Share")), "Sales & Share is visible.");

        hp.clickOnNavbarModule("Analyze");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createNavbarLinkLocator("Marketing Cloud Analytics")), "Marketing Cloud Analytics is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createNavbarLinkLocator("Paid Media Reporting")), "Paid Media Reporting is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createNavbarLinkLocator("Data As Service")), "Data As Service is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createNavbarLinkLocator("Retail Reporting")), "Retail Reporting is visible.");
        softAssert.assertAll();
    }

    @Test(description = "PH-184-494")
    public void Insights_FE_Permissions_Navigate_To_Pages_Test(ITestContext testContext) throws Exception {
        SoftAssert softAssert = new SoftAssert();

        driver = base.initializeBrowser(testContext, headless);
        DCLoginPage lp = new DCLoginPage(driver);
        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubInsightsUserEmail(), readConfig.getHubInsightsUserPassword());
        InsightsMethods insightsMethods = new InsightsMethods();
        AppHomepage hp = new AppHomepage(driver);

        String authToken = "Bearer " + SecurityAPI.getAuthToken(driver);
        String nonSupportUserJwt = insightsMethods.getJwtForInsightsUser(authToken);
        String nonSupportUserId  = insightsMethods.decodeInsightsJwtAndGetUserId(nonSupportUserJwt);
        String nonSupportUserCompanyId  = insightsMethods.decodeInsightsJwtToGetCompanyId(nonSupportUserJwt);

        String jwt = insightsMethods.switchCompany(authTokenForSupportUser, nonSupportUserCompanyId);
        HubInsightsRolesRequestBody requestBody = new HubInsightsRolesRequestBody(List.of("insights-user", "category-intelligence-user", "CPG-deployment", "status-insights-user",
                "CPG-deployment-api", "CPG-contributor", "pim-user", "CPG"));
        InsightsAuthServiceApiRequest.updateUserRoles(requestBody, nonSupportUserId, jwt);
        refreshToGetUpdatesOnUi(hp, authToken, "onespace");

        for (int i = 0; i < 10; i++){
            boolean flag = false;
            hp.goToHomePage();
            List<WebElement> modules = lp.findElementsVisible(AppHomepage.MODULES);
            WebElement module = (WebElement) SharedMethods.getRandomItemFromList(modules);
            module.click();

            List<WebElement> pages = lp.findElementsVisible(AppHomepage.PAGES);
            for (WebElement page : pages) {
                String url = page.findElement(By.xpath("./a")).getAttribute("href");
                if (url.contains("insights")) {
                    flag = true;
                    String pageName = page.getText().equals("Keyword Watchlists") ? "Watchlists" : page.getText();
                    page.click();
                    softAssert.assertTrue(lp.isElementVisible(hp.createLinkLocator(pageName)), pageName + " page is not loading.");
                    break;
                }
            }
            if (flag) {
                break;
            }
        }

        lp.logoutDcApp();
        softAssert.assertTrue(hp.isElementVisible(DCLoginPage.LOGIN_BUTTON), "Log out not successful.");
        softAssert.assertAll();
    }

    @Test(description = "PH-184-494")
    public void Insights_Support_User_FE_Permissions_UserProfile_Test(ITestContext testContext) throws Exception {
        SoftAssert softAssert = new SoftAssert();
        driver = base.initializeBrowser(testContext, headless);
        DCLoginPage lp = new DCLoginPage(driver);
        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubInsightsSupportUsername(), readConfig.getHubInsightsSupportUserPassword());
        AppHomepage hp = new AppHomepage(driver);

        List<String> userProfileClientAdminPages = userManagement.getOsSupportUserClientAdminPages();
        List<String> userProfileClientManagementPages = userManagement.getOsClientManagementPages();

        lp.openUserProfile();
        softAssert.assertEquals(userProfileClientAdminPages, hp.getUserProfilePagesBySection("Client Administration"), "User permissions do not match for Client Administration.");
        softAssert.assertEquals(userProfileClientManagementPages, hp.getUserProfilePagesBySection("Account Management"), "User permissions do not match for Account Management.");

        List<WebElement> clientAdminPages = lp.findElementsVisible(NetNewNavigationMenu.CLIENT_ADMIN_PAGES);
        WebElement page = (WebElement) SharedMethods.getRandomItemFromList(clientAdminPages);
        String pageName = page.getText();
        page.click();
        softAssert.assertTrue(lp.isElementVisible(hp.createLinkLocator(pageName)), pageName + " page is not visible.");

        hp.goToHomePage();
        lp.openUserProfile();
        hp.click(NetNewNavigationMenu.NOTIFICATIONS);

        String url = hp.getCurrentUrl();
        softAssert.assertTrue(url.contains("/insights/account?tab=notifications&secondaryTab=content"), "Notifications Url for Insights user is wrong. Url: " + url);
        softAssert.assertAll();
    }

    @Test(description = "PH-184-494")
    public void Insights_NonSupport_User_FE_Permissions_UserProfile_Test(ITestContext testContext) throws Exception {
        SoftAssert softAssert = new SoftAssert();

        driver = base.initializeBrowser(testContext, headless);
        DCLoginPage lp = new DCLoginPage(driver);
        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubInsightsUserEmail(), readConfig.getHubInsightsUserPassword());
        InsightsMethods insightsMethods = new InsightsMethods();
        AppHomepage hp = new AppHomepage(driver);

        String authToken = "Bearer " + SecurityAPI.getAuthToken(driver);
        String nonSupportUserJwt = insightsMethods.getJwtForInsightsUser(authToken);
        String nonSupportUserId  = insightsMethods.decodeInsightsJwtAndGetUserId(nonSupportUserJwt);
        String nonSupportUserCompanyId  = insightsMethods.decodeInsightsJwtToGetCompanyId(nonSupportUserJwt);

        String jwt = insightsMethods.switchCompany(authTokenForSupportUser, nonSupportUserCompanyId);
        HubInsightsRolesRequestBody requestBody = new HubInsightsRolesRequestBody(List.of("insights-user", "CPG", "CPG-deployment-api"));
        InsightsAuthServiceApiRequest.updateUserRoles(requestBody, nonSupportUserId, jwt);
        refreshToGetUpdatesOnUi(hp, authToken, "onespace");

        lp.openUserProfile();
        List<String> userProfileClientManagementPages = userManagement.getOsClientManagementPages();
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Client Administration")), "Client Administration is visible.");
        softAssert.assertEquals(userProfileClientManagementPages, hp.getUserProfilePagesBySection("Account Management"), "User permissions do not match for Account Management.");

        List<WebElement> clientAdminPages = lp.findElementsVisible(NetNewNavigationMenu.CLIENT_ADMIN_PAGES);
        WebElement page = (WebElement) SharedMethods.getRandomItemFromList(clientAdminPages);
        String pageName = page.getText();
        page.click();
        softAssert.assertTrue(lp.isElementVisible(hp.createLinkLocator(pageName)), pageName + " page is not visible.");

        hp.goToHomePage();
        lp.openUserProfile();
        hp.click(NetNewNavigationMenu.API_CREDENTIALS);

        String url = hp.getCurrentUrl();
        softAssert.assertTrue(url.contains("/insights/account?tab=api&secondaryTab=salsify"), "API Credentials Url for Insights user is wrong. Url: " + url);

        hp.goToHomePage();
        lp.openUserProfile();
        hp.click(NetNewNavigationMenu.NOTIFICATIONS);

        url = hp.getCurrentUrl();
        softAssert.assertTrue(url.contains("/insights/account?tab=notifications&secondaryTab=content"), "Notifications Url for Insights user is wrong. Url: " + url);
        softAssert.assertAll();
    }

    @Test(description = "PH-185-494")
    public void MS_Reports_User_FE_Permissions_Homepage_Test(ITestContext testContext) throws Exception {
        SoftAssert softAssert = new SoftAssert();
        driver = base.initializeBrowser(testContext, headless);
        DCLoginPage lp = new DCLoginPage(driver);
        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubEdgeOnlyUserEmail(), readConfig.getHubEdgeOnlyUserPassword());
        AppHomepage hp = new AppHomepage(driver);

        String	edgeTokenForUserToUpdate = "Bearer " +  SecurityAPI.getAuthToken(driver);
        msMethods.updateEdgeUser(edgeTokenForAdmin, edgeTokenForUserToUpdate, 200);
        refreshToGetUpdatesOnUi(hp, edgeTokenForUserToUpdate, "marketshare");

        List<String> searchInsights = identify.getNetNewSearchInsights();
        List<String> salesShare = identify.getMsSalesShare();
        List<String> searchReporting = analyze.getMsNetNewSearchReporting();
        List<String> productHealth = analyze.getMsNetNewProductHealth();
        List<String> dataManagement = manage.getMsReportsUserNetNewDataManagement();

        softAssert.assertEquals(searchInsights, hp.getModulePagesBySection("Identify", "Search Insights"), "User permissions do not match for Search Insights.");
        softAssert.assertEquals(salesShare, hp.getModulePagesBySection("Identify", "Sales & Share"), "User permissions do not match for Sales & Share.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Executive Dashboard")), "Executive Dashboard is visible.");
        softAssert.assertEquals(searchReporting, hp.getModulePagesBySection("Analyze", "Search Reporting"), "User permissions do not match for Search Reporting.");
        softAssert.assertEquals(productHealth, hp.getModulePagesBySection("Analyze", "Product Health"), "User permissions do not match for Product Health.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Paid Media Reporting")), "Paid Media Reporting is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Marketing Cloud Analytics")), "Marketing Cloud Analytics is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Data As Service")), "Data As Service is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Retail Reporting")), "Retail Reporting is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Execute")), "Execute module is visible.");
        softAssert.assertEquals(dataManagement, hp.getModulePagesBySection("Manage", "Data Management"), "User permissions do not match for Data Management.");
        softAssert.assertAll();
    }

    @Test(description = "PH-185-494")
    public void MS_Client_Admin_FE_Permissions_Homepage_Test(ITestContext testContext) throws Exception {
        SoftAssert softAssert = new SoftAssert();
        driver = base.initializeBrowser(testContext, headless);
        DCLoginPage lp = new DCLoginPage(driver);
        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubEdgeOnlyUserEmail(), readConfig.getHubEdgeOnlyUserPassword());
        AppHomepage hp = new AppHomepage(driver);

        String	edgeTokenForUserToUpdate = "Bearer " +  SecurityAPI.getAuthToken(driver);
        msMethods.updateEdgeUser(edgeTokenForAdmin, edgeTokenForUserToUpdate, 600);
        refreshToGetUpdatesOnUi(hp, edgeTokenForUserToUpdate, "marketshare");

        List<String> searchInsights = identify.getNetNewSearchInsights();
        List<String> salesShare = identify.getMsSalesShare();
        List<String> searchReporting = analyze.getMsNetNewSearchReporting();
        List<String> productHealth = analyze.getMsNetNewProductHealth();
        List<String> dataManagement = manage.getMsClientAdminNetNewDataManagement();

        softAssert.assertEquals(searchInsights, hp.getModulePagesBySection("Identify", "Search Insights"), "User permissions do not match for Search Insights.");
        softAssert.assertEquals(salesShare, hp.getModulePagesBySection("Identify", "Sales & Share"), "User permissions do not match for Sales & Share.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Executive Dashboard")), "Executive Dashboard is visible.");
        softAssert.assertEquals(searchReporting, hp.getModulePagesBySection("Analyze", "Search Reporting"), "User permissions do not match for Search Reporting.");
        softAssert.assertEquals(productHealth, hp.getModulePagesBySection("Analyze", "Product Health"), "User permissions do not match for Product Health.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Paid Media Reporting")), "Paid Media Reporting is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Marketing Cloud Analytics")), "Marketing Cloud Analytics is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Data As Service")), "Data As Service is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Retail Reporting")), "Retail Reporting is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Execute")), "Execute module is visible.");
        softAssert.assertEquals(dataManagement, hp.getModulePagesBySection("Manage", "Data Management"), "User permissions do not match for Data Management.");
        softAssert.assertAll();
    }

    @Test(description = "PH-185-494")
    public void MS_Reports_User_FE_Permissions_SearchBar_Test(ITestContext testContext) throws Exception {
        SoftAssert softAssert = new SoftAssert();
        driver = base.initializeBrowser(testContext, headless);
        DCLoginPage lp = new DCLoginPage(driver);
        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubEdgeOnlyUserEmail(), readConfig.getHubEdgeOnlyUserPassword());
        AppHomepage hp = new AppHomepage(driver);

        String	edgeTokenForUserToUpdate = "Bearer " +  SecurityAPI.getAuthToken(driver);
        msMethods.updateEdgeUser(edgeTokenForAdmin, edgeTokenForUserToUpdate, 200);
        refreshToGetUpdatesOnUi(hp, edgeTokenForUserToUpdate, "marketshare");

        List<String> searchInsights = identify.getNetNewSearchInsights();
        List<String> salesShare = identify.getMsSalesShare();
        List<String> searchReporting = analyze.getMsNetNewSearchReporting();
        List<String> productHealth = analyze.getMsNetNewProductHealth();
        List<String> dataManagement = manage.getMsReportsUserNetNewDataManagement();
        List<String> accountManagement = userManagement.getMsClientManagementPages();

        List<String> searchBarReports = new ArrayList<>();
        searchBarReports.addAll(salesShare);
        searchBarReports.addAll(searchInsights);
        searchBarReports.addAll(dataManagement);
        searchBarReports.addAll(productHealth);
        searchBarReports.addAll(searchReporting);
        searchBarReports.addAll(accountManagement);
        Collections.sort(searchBarReports);

        List<String>  uiSearchBarReports = hp.getSearchBarReports();
        softAssert.assertEquals(searchBarReports, uiSearchBarReports, "Search Bar reports do not match.");
        softAssert.assertAll();
    }

    @Test(description = "PH-185-494")
    public void Ms_Reports_User_FE_Permissions_NavBar_Test(ITestContext testContext) throws Exception {
        SoftAssert softAssert = new SoftAssert();
        driver = base.initializeBrowser(testContext, headless);
        DCLoginPage lp = new DCLoginPage(driver);
        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubEdgeOnlyUserEmail(), readConfig.getHubEdgeOnlyUserPassword());
        AppHomepage hp = new AppHomepage(driver);

        String	edgeTokenForUserToUpdate = "Bearer " +  SecurityAPI.getAuthToken(driver);
        msMethods.updateEdgeUser(edgeTokenForAdmin, edgeTokenForUserToUpdate, 200);
        refreshToGetUpdatesOnUi(hp, edgeTokenForUserToUpdate, "marketshare");

        softAssert.assertEquals(hp.getModulePagesBySection("Identify", "Search Insights"), hp.getNavbarPagesBySection("Identify", "Search Insights"), "Module and Navbar pages do not match for Search Insights.");
        softAssert.assertEquals(hp.getModulePagesBySection("Identify", "Sales & Share"), hp.getNavbarPagesBySection("Identify", "Sales & Share"), "Module and Navbar pages do not match for Sales & Share.");
        softAssert.assertEquals(hp.getModulePagesBySection("Analyze", "Product Health"), hp.getNavbarPagesBySection("Analyze", "Product Health"), "Module and Navbar pages do not match for Product Health.");
        softAssert.assertEquals(hp.getModulePagesBySection("Analyze", "Search Reporting"), hp.getNavbarPagesBySection("Analyze", "Search Reporting"), "Module and Navbar pages do not match for Search Reporting.");
        softAssert.assertTrue(hp.isElementNotVisible(By.xpath("//button[text()='Execute']")), "Execute dropdown is visible.");
        softAssert.assertEquals(hp.getModulePagesBySection("Manage", "Data Management"), hp.getNavbarPagesBySection("Manage", "Data Management"), "Module and Navbar pages do not match for Data Management.");

        hp.clickOnNavbarModule("Identify");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createNavbarLinkLocator("Executive Dashboard")), "Executive Dashboard is visible.");

        hp.clickOnNavbarModule("Analyze");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createNavbarLinkLocator("Marketing Cloud Analytics")), "Marketing Cloud Analytics is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createNavbarLinkLocator("Paid Media Reporting")), "Paid Media Reporting is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createNavbarLinkLocator("Data As Service")), "Data As Service is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createNavbarLinkLocator("Retail Reporting")), "Retail Reporting is visible.");
        softAssert.assertAll();
    }

    @Test(description = "PH-185-494")
    public void Ms_FE_Permissions_Navigate_To_Pages_Test(ITestContext testContext) throws Exception {
        SoftAssert softAssert = new SoftAssert();
        driver = base.initializeBrowser(testContext, headless);
        DCLoginPage lp = new DCLoginPage(driver);
        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubEdgeOnlyUserEmail(), readConfig.getHubEdgeOnlyUserPassword());
        AppHomepage hp = new AppHomepage(driver);

        for (int i = 0; i < 10; i++){
            boolean flag = false;
            hp.goToHomePage();
            List<WebElement> modules = lp.findElementsVisible(AppHomepage.MODULES);
            WebElement module = (WebElement) SharedMethods.getRandomItemFromList(modules);
            module.click();

            List<WebElement> pages = lp.findElementsVisible(AppHomepage.PAGES);
            for (WebElement page : pages) {
                String url = page.findElement(By.xpath("./a")).getAttribute("href");
                if (url.contains("edge")) {
                    flag = true;
                    String pageName = page.getText();
                    page.click();
                    if (pageName.equals("Catalog Classifier")){
                        softAssert.assertTrue(lp.getCurrentUrl().contains("classifier"), pageName + " page is not loading.");
                    } else if (pageName.equals("Toolbox API")){
                        softAssert.assertTrue(lp.getCurrentUrl().contains("toolboxapi"), pageName + " page is not loading.");
                    } else if (pageName.equals("Toolbox UI")){
                        softAssert.assertTrue(lp.getCurrentUrl().contains("toolboxui"), pageName + " page is not loading.");
                    } else if (pageName.equals("Promotion")) {
                        pageName = "Promotions";
                        softAssert.assertTrue(lp.isElementVisible(hp.createLinkLocator(pageName)), pageName + " page is not loading.");
                    } else {
                        softAssert.assertTrue(lp.isElementVisible(hp.createLinkLocator(pageName)), pageName + " page is not loading.");
                    }
                    break;
                }
            }
            if (flag) {
                break;
            }
        }

        lp.logoutDcApp();
        softAssert.assertTrue(hp.isElementVisible(DCLoginPage.LOGIN_BUTTON), "Log out not successful.");
        softAssert.assertAll();
    }

    @Test(description = "PH-185-494")
    public void Ms_FE_Permissions_UserProfile_Test(ITestContext testContext) throws Exception {
        SoftAssert softAssert = new SoftAssert();

        driver = base.initializeBrowser(testContext, headless);
        DCLoginPage lp = new DCLoginPage(driver);
        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubEdgeOnlyUserEmail(), readConfig.getHubEdgeOnlyUserPassword());
        AppHomepage hp = new AppHomepage(driver);

        lp.openUserProfile();
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Client Administration")), "Client Administration is visible.");
        softAssert.assertEquals(userManagement.getMsClientManagementPages(), hp.getUserProfilePagesBySection("Account Management"), "User permissions do not match for Account Management.");

        List<WebElement> clientAdminPages = lp.findElementsVisible(NetNewNavigationMenu.CLIENT_ADMIN_PAGES);
        WebElement page = (WebElement) SharedMethods.getRandomItemFromList(clientAdminPages);
        String pageName = page.getText();
        page.click();
        softAssert.assertTrue(lp.isElementVisible(hp.createLinkLocator(pageName)), pageName + " page is not visible.");

        hp.goToHomePage();
        lp.openUserProfile();
        hp.click(NetNewNavigationMenu.NOTIFICATIONS);

        String url = hp.getCurrentUrl();
        softAssert.assertTrue(url.contains("/edge/account/notifications?secondaryTab=sales-share"), "Notifications Url for MS user is wrong. Url: " + url);

        hp.goToHomePage();
        lp.openUserProfile();
        hp.click(NetNewNavigationMenu.API_CREDENTIALS);

        url = hp.getCurrentUrl();
        softAssert.assertTrue(url.contains("/edge/account/api/sales-share"), "API Credentials Url for MS user is wrong. Url: " + url);
        softAssert.assertAll();
    }

    @Test(description = "PH-185-494")
    public void Ms_FE_Permissions_Reports_User_Test(ITestContext testContext) throws Exception {
        SoftAssert softAssert = new SoftAssert();

        driver = base.initializeBrowser(testContext, headless);
        DCLoginPage lp = new DCLoginPage(driver);
        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubEdgeOnlyUserEmail(), readConfig.getHubEdgeOnlyUserPassword());
        AppHomepage hp = new AppHomepage(driver);
        ConversionPage cp = new ConversionPage(driver);

        String	edgeTokenForUserToUpdate = "Bearer " +  SecurityAPI.getAuthToken(driver);
        msMethods.updateEdgeUser(edgeTokenForAdmin, edgeTokenForUserToUpdate, 200);
        refreshToGetUpdatesOnUi(hp, edgeTokenForUserToUpdate, "marketshare");

        hp.openPage("Identify", "Conversion");
        cp.openManageUsersPage();

        softAssert.assertEquals(hp.findElementsVisible(ConversionPage.USER_EDIT).size(), 0, "Edit buttons are visible.");
        softAssert.assertEquals(hp.findElementsVisible(ConversionPage.USER_DELETE).size(), 0, "Delete buttons are visible.");
        softAssert.assertAll();

    }

    @Test(description = "PH-151-494")
    public void Connect_FE_Permissions_Homepage_Test(ITestContext testContext) throws Exception {
        SoftAssert softAssert = new SoftAssert();
        HubDbFunctions hubDbFunctions = new HubDbFunctions();

        driver = base.initializeBrowser(testContext, headless);
        DCLoginPage lp = new DCLoginPage(driver);
        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubConnectUserEmail(), readConfig.getHubConnectUserPassword());
        AppHomepage hp = new AppHomepage(driver);

        hubDbFunctions.updateConnectUserScreenPermissions(1, 29);
        refreshToGetUpdatesOnUi(hp, "Bearer " +  SecurityAPI.getAuthToken(driver), "connect");

        List<String> searchInsights = identify.getNetNewSearchInsights();
        List<String> executiveDashboard = identify.getConnectExecutiveDashboardUnrestrictedUser();
        List<String> searchReporting = analyze.getNetNewSearchReporting();
        List<String> productHealth = analyze.getNetNewProductHealth();
        List<String> dataManagement = manage.getConnectNetNewDataManagement();

        softAssert.assertEquals(searchInsights, hp.getModulePagesBySection("Identify", "Search Insights"), "User permissions do not match for Search Insights.");
        softAssert.assertEquals(executiveDashboard, hp.getModulePagesBySection("Identify", "Executive Dashboard"), "User permissions do not match for Executive Dashboard.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Sales & Share")), "Sales & Share is visible.");
        softAssert.assertEquals(searchReporting, hp.getModulePagesBySection("Analyze", "Search Reporting"), "User permissions do not match for Search Reporting.");
        softAssert.assertEquals(productHealth, hp.getModulePagesBySection("Analyze", "Product Health"), "User permissions do not match for Product Health.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Paid Media Reporting")), "Paid Media Reporting is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Marketing Cloud Analytics")), "Marketing Cloud Analytics is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Data As Service")), "Data As Service is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Retail Reporting")), "Retail Reporting is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Execute")), "Execute module is visible.");
        softAssert.assertEquals(dataManagement, hp.getModulePagesBySection("Manage", "Data Management"), "User permissions do not match for Data Management.");
        softAssert.assertAll();
    }

    @Test(description = "PH-151-494")
    public void Connect_FE_Permissions_SearchBar_Test(ITestContext testContext) throws Exception {
        SoftAssert softAssert = new SoftAssert();
        HubDbFunctions hubDbFunctions = new HubDbFunctions();

        driver = base.initializeBrowser(testContext, headless);
        DCLoginPage lp = new DCLoginPage(driver);
        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubConnectUserEmail(), readConfig.getHubConnectUserPassword());
        AppHomepage hp = new AppHomepage(driver);

        hubDbFunctions.updateConnectUserScreenPermissions(5, 29);
        refreshToGetUpdatesOnUi(hp, "Bearer " +  SecurityAPI.getAuthToken(driver), "connect");

        List<String> searchInsights = identify.getNetNewSearchInsights();
        List<String> executiveDashboard = identify.getConnectExecutiveDashboardRestrictedUser();
        List<String> searchReporting = analyze.getNetNewSearchReporting();
        List<String> productHealth = analyze.getNetNewProductHealth();
        List<String> dataManagement = manage.getNetNewDataManagement();
        List<String> accountManagement = List.of("Profile Information", "Request Support");

        List<String> searchBarReports = new ArrayList<>();
        searchBarReports.addAll(searchInsights);
        searchBarReports.addAll(executiveDashboard);
        searchBarReports.addAll(dataManagement);
        searchBarReports.addAll(productHealth);
        searchBarReports.addAll(searchReporting);
        searchBarReports.addAll(accountManagement);
        Collections.sort(searchBarReports);

        List<String>  uiSearchBarReports = hp.getSearchBarReports();
        softAssert.assertEquals(searchBarReports, uiSearchBarReports, "Search Bar reports do not match.");
        softAssert.assertAll();
    }

    @Test(description = "PH-151-494")
    public void Connect_FE_Permissions_NavBar_Test(ITestContext testContext) throws Exception {
        SoftAssert softAssert = new SoftAssert();
        HubDbFunctions hubDbFunctions = new HubDbFunctions();

        driver = base.initializeBrowser(testContext, headless);
        DCLoginPage lp = new DCLoginPage(driver);
        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubConnectUserEmail(), readConfig.getHubConnectUserPassword());
        AppHomepage hp = new AppHomepage(driver);

        hubDbFunctions.updateConnectUserScreenPermissions(1, 29);
        refreshToGetUpdatesOnUi(hp, "Bearer " +  SecurityAPI.getAuthToken(driver), "connect");

        softAssert.assertEquals(hp.getModulePagesBySection("Identify", "Search Insights"), hp.getNavbarPagesBySection("Identify", "Search Insights"), "Module and Navbar pages do not match for Search Insights.");
        softAssert.assertEquals(hp.getModulePagesBySection("Identify", "Executive Dashboard"), hp.getNavbarPagesBySection("Identify", "Executive Dashboard"), "Module and Navbar pages do not match for Executive Dashboard.");
        softAssert.assertEquals(hp.getModulePagesBySection("Analyze", "Product Health"), hp.getNavbarPagesBySection("Analyze", "Product Health"), "Module and Navbar pages do not match for Product Health.");
        softAssert.assertEquals(hp.getModulePagesBySection("Analyze", "Search Reporting"), hp.getNavbarPagesBySection("Analyze", "Search Reporting"), "Module and Navbar pages do not match for Search Reporting.");
        softAssert.assertTrue(hp.isElementNotVisible(By.xpath("//button[text()='Execute']")), "Execute dropdown is visible.");
        softAssert.assertEquals(hp.getModulePagesBySection("Manage", "Data Management"), hp.getNavbarPagesBySection("Manage", "Data Management"), "Module and Navbar pages do not match for Data Management.");

        hp.clickOnNavbarModule("Identify");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createNavbarLinkLocator("Sales & Share")), "Sales & Share is visible.");

        hp.clickOnNavbarModule("Analyze");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createNavbarLinkLocator("Marketing Cloud Analytics")), "Marketing Cloud Analytics is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createNavbarLinkLocator("Paid Media Reporting")), "Paid Media Reporting is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createNavbarLinkLocator("Data As Service")), "Data As Service is visible.");
        softAssert.assertTrue(hp.isElementNotVisible(hp.createNavbarLinkLocator("Retail Reporting")), "Retail Reporting is visible.");
        softAssert.assertAll();
    }

    @Test(description = "PH-151-494")
    public void Connect_FE_Permissions_Navigate_To_Pages_Test(ITestContext testContext) throws Exception {
        SoftAssert softAssert = new SoftAssert();
        HubDbFunctions hubDbFunctions = new HubDbFunctions();

        driver = base.initializeBrowser(testContext, headless);
        DCLoginPage lp = new DCLoginPage(driver);
        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubConnectUserEmail(), readConfig.getHubConnectUserPassword());
        AppHomepage hp = new AppHomepage(driver);

        hubDbFunctions.updateConnectUserScreenPermissions(1, 29);
        refreshToGetUpdatesOnUi(hp, "Bearer " +  SecurityAPI.getAuthToken(driver), "connect");

        for (int i = 0; i < 10; i++){
            boolean flag = false;
            hp.goToHomePage();
            List<WebElement> modules = lp.findElementsVisible(AppHomepage.MODULES);
            WebElement module = (WebElement) SharedMethods.getRandomItemFromList(modules);
            module.click();

            List<WebElement> pages = lp.findElementsVisible(AppHomepage.PAGES);
            for (WebElement page : pages) {
                String url = page.findElement(By.xpath("./a")).getAttribute("href");
                if (url.contains("connect")) {
                    flag = true;
                    String pageName = page.getText();
                    page.click();
                    softAssert.assertTrue(lp.isElementVisible(hp.createLinkLocator(pageName)), pageName + " page is not loading.");
                    break;
                }
            }
            if (flag) {
                break;
            }
        }

        lp.logoutDcApp();
        softAssert.assertTrue(hp.isElementVisible(DCLoginPage.LOGIN_BUTTON), "Log out not successful.");
        softAssert.assertAll();
    }

    @Test(description = "PH-151-494")
    public void Connect_FE_Permissions_UserProfile_Test(ITestContext testContext) throws Exception {
        SoftAssert softAssert = new SoftAssert();

        driver = base.initializeBrowser(testContext, headless);
        DCLoginPage lp = new DCLoginPage(driver);
        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubConnectUserEmail(), readConfig.getHubConnectUserPassword());
        AppHomepage hp = new AppHomepage(driver);

        lp.openUserProfile();
        List<String> userProfileClientManagementPages = userManagement.getConnectClientManagementPages();
        softAssert.assertTrue(hp.isElementNotVisible(hp.createLinkLocator("Client Administration")), "Client Administration is visible.");
        softAssert.assertEquals(userProfileClientManagementPages, hp.getUserProfilePagesBySection("Account Management"), "User permissions do not match for Account Management.");

        List<WebElement> clientAdminPages = lp.findElementsVisible(NetNewNavigationMenu.CLIENT_ADMIN_PAGES);
        WebElement page = (WebElement) SharedMethods.getRandomItemFromList(clientAdminPages);
        String pageName = page.getText();
        page.click();

        softAssert.assertTrue(lp.isElementVisible(hp.createLinkLocator(pageName)), pageName + " page is not visible.");
        softAssert.assertAll();
    }

    private void refreshToGetUpdatesOnUi(AppHomepage hp, String authToken, String localStorageKey) throws Exception {
        AuthServiceApiRequest.logOutUser(authToken);
        hp.clearLocalStorageItem(localStorageKey);
        msMethods.getUserHubInfo(authToken);
        hp.refreshPage();
    }

    private void restoreFilaUser(FilaUser filaUser, String authToken) throws Exception {
        String userRolesIds = filaUser.getFilaRoles(authToken,"Internal Only: Base User","Internal Only: User Administration", "Internal Only: Role Administration");
        String buIds = filaUser.getFilaBus(authToken,"3M", "Hersheys - US");
        filaUser.updateFilaUser(authToken, userRolesIds, buIds, true, true);
    }

    @AfterMethod
    public void killDriver() {
        base.quitBrowser();
    }
}