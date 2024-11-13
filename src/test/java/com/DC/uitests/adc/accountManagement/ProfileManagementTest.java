package com.DC.uitests.adc.accountManagement;

import com.DC.constants.NetNewConstants;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.accountManagement.ProfileManagementPage;
import com.DC.testcases.BaseClass;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ProfileManagementTest extends BaseClass {

    private static final String USER_NAME = READ_CONFIG.getUsername();
    private static final String PASSWORD = READ_CONFIG.getPassword();
    private static final String LOGIN_ENDPOINT = READ_CONFIG.getDcAppUrl();

    private ProfileManagementPage profileManagementPage;


    @BeforeClass
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USER_NAME, PASSWORD);

        String profileManagementUrl = NetNewConstants.getReportsUrl("account", "?=profile");
        driver.get(profileManagementUrl);
        profileManagementPage = new ProfileManagementPage(driver);
    }

    @AfterClass()
    public void killDriver() {
        quitBrowser();
    }

    @Test(priority = 1, description = "Verify Display of Profile Management Screen")
    public void PM_ProfileManagementScreenIsDisplayed() {

        Assert.assertTrue(profileManagementPage.isProfileManagementScreenDisplayed(), "Profile Management Screen is not displayed");
        String currentUrl = profileManagementPage.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("profile"), "Profile Management Screen is not displayed");
    }

    @Test(priority = 2, description = "Verify Display of Notification Tab")
    public void PM_NotificationTabIsDisplayed() throws InterruptedException {
        Assert.assertTrue(profileManagementPage.isProfileManagementScreenDisplayed(), "Profile Management Screen is not displayed");
        LOGGER.info("Verify that Notification Tab is displayed");
        profileManagementPage.NotificationTabDisplayed();
        LOGGER.info("Verify Notification Tab is Clickable");
        profileManagementPage.verifyNotificationTabIsCickable();
        String currentUrl = profileManagementPage.getCurrentUrl();
        LOGGER.info("Notification Screen Verify Notification URL");
        Assert.assertTrue(currentUrl.contains("notifications"), "Page url does not contain Notifications");
        driver.navigate().back();
    }

    @Test(priority = 3, description = "Verify Display of Credential APi Tab")
    public void PM_CredentialTabIsDisplayed() throws InterruptedException {
        Assert.assertTrue(profileManagementPage.isProfileManagementScreenDisplayed(), "Profile Management Screen is not displayed");
        LOGGER.info("Verify that Credential API Tab is displayed");
        profileManagementPage.CredentialApiTabDisplayed();
        LOGGER.info("Verify Credential API Tab is Clickable");
        profileManagementPage.verifyCredentialApiTabIsClickable();
        String currentUrl = profileManagementPage.getCurrentUrl();
        LOGGER.info("Credential API Screen Verify Credential API URL");
        Assert.assertTrue(currentUrl.contains("api"), "Page url does not contain Api");
        driver.navigate().back();
    }

    @Test(priority = 4, description = "Verify Display of Information Tab")
    public void PM_InformationTabIsDisplayed() throws InterruptedException {
        Assert.assertTrue(profileManagementPage.isProfileManagementScreenDisplayed(), "Profile Management Screen is not displayed");
        LOGGER.info("Verify that Information Tab is displayed");
        profileManagementPage.InformationTabDisplayed();
        LOGGER.info("Verify Information Tab is Clickable");
        profileManagementPage.verifyInformationTabIsClickable();
        String currentUrl = profileManagementPage.getCurrentUrl();
        LOGGER.info("Information Screen Verify Information URL");
        Assert.assertTrue(currentUrl.contains("profile"), "Page url does not contain Profile");
    }

    @Test(priority = 7, description = "Verify Display of First name Input Field")
    public void PM_FirstNameInputFieldIsDisplayed() {
        Assert.assertTrue(profileManagementPage.isProfileManagementScreenDisplayed(), "Profile Management Screen is not displayed");
        LOGGER.info("Verify that First Name Input Field is displayed");
        Assert.assertTrue(profileManagementPage.FirstNameFieldIsDisplayed(),"First Name input field is not Displayed");
    }

    @Test(priority = 8, description = "Verify Display of Last name Input Field")
    public void PM_LastNameInputFieldIsDisplayed() {
        Assert.assertTrue(profileManagementPage.isProfileManagementScreenDisplayed(), "Profile Management Screen is not displayed");
        LOGGER.info("Verify that Last Name Input Field is displayed");
        Assert.assertTrue(profileManagementPage.LastNameFieldIsDisplayed(),"Last Name input field is not Displayed");
    }

    @Test(priority = 9, description = "Verify Display of Username Input Field")
    public void PM_UserNameInputFieldIsDisplayed() {
        Assert.assertTrue(profileManagementPage.isProfileManagementScreenDisplayed(), "Profile Management Screen is not displayed");
        LOGGER.info("Verify that Username Input Field is displayed");
        Assert.assertTrue(profileManagementPage.UsernameFieldIsDisplayed(),"Username input field is not Displayed");
    }

    @Test(priority = 10, description = "Verify Display of Email Address Input Field")
    public void PM_EmailAddressInputFieldIsDisplayed() {
        Assert.assertTrue(profileManagementPage.isProfileManagementScreenDisplayed(), "Profile Management Screen is not displayed");
        LOGGER.info("Verify that Email Address Input Field is displayed");
        Assert.assertTrue(profileManagementPage.EmailAddressFieldIsDisplayed(),"Email Address input field is not Displayed");
    }

    @Test(priority = 5, description = "Verify Display of Reset Password Button")
    public void PM_ResetPasswordButtonIsDisplayed() {

        Assert.assertTrue(profileManagementPage.isProfileManagementScreenDisplayed(), "Profile Management Screen is not displayed");
        LOGGER.info("Verify Reset Password Button");
        Assert.assertTrue(profileManagementPage.resetPasswordButtionDisplayed(),"Reset Password Button is not Displayed");
        }

    @Test(priority = 6, description = "Verify that after clicking Profile Information link on Home Page user is redirected to the Profile Management page")
    public void PM_ClickingHomePageLinkRedirectsUserToHomePage() throws InterruptedException {
        profileManagementPage.clickFWLogo();
        LOGGER.info("After clicking HomePage link user is redirected to the Home Page");
        profileManagementPage.OpenProfileManagementPage();
        Assert.assertTrue(profileManagementPage.isProfileManagementScreenDisplayed(), "Profile Management page is not displayed.");
        String currentUrl = profileManagementPage.getCurrentUrl();
        LOGGER.info("Current page url: " + currentUrl);
        Assert.assertTrue(currentUrl.contains("profile"), "Page url does not contain Profile");
    }
}
