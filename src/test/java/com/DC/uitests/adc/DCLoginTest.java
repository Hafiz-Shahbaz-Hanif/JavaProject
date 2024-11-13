package com.DC.uitests.adc;

import com.DC.pageobjects.adc.AppHomepage;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.testcases.BaseClass;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class DCLoginTest extends BaseClass {
    private static final String USER_NAME = READ_CONFIG.getUsername();
    private static final String PASSWORD = READ_CONFIG.getPassword();
    private static final Logger logger = Logger.getLogger(DCLoginTest.class);
    private static DCLoginPage loginPage;
    private static AppHomepage homepage;

    @BeforeClass
    public void initializeBrowser(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        loginPage = new DCLoginPage(driver);
        loginPage.loadPage();
    }

    @BeforeMethod
    public void setupTests() throws Exception {
        if (homepage != null) {
            loginPage = homepage.logout();
        }
    }

    @AfterClass(alwaysRun = true)
    public void killDriver() {
        quitBrowser();
    }

    @Test(priority = 1, description = "Testing login page with empty Username")
    public void LP_Login_CannotLoginWithEmptyUsername() throws InterruptedException {

        logger.info("Try to log in with blank Username field");

        loginPage.setEmptyUsername();
        loginPage.setPassword(PASSWORD);
        loginPage.clickLogin();

        Assert.assertTrue(loginPage.getErrorMessage(), "The error message is not displayed.");

        loginPage.clearUserNameAndPwdFields();
    }

    @Test(priority = 2, description = "Testing login page with empty Password")
    public void LP_Login_CannotLoginWithEmptyPwd() throws InterruptedException {

        logger.info("Try to log in with blank Password field");

        loginPage.setUserName(USER_NAME);
        loginPage.setEmptyPwd();
        loginPage.clickLogin();

        Assert.assertTrue(loginPage.getErrorMessage(), "The error message is not displayed.");

        loginPage.clearUserNameAndPwdFields();
    }

    @Test(priority = 3, description = "Testing login page with empty Username and Password")
    public void LP_Login_CannotLoginWithEmptyUsernameAndPwd() throws InterruptedException {

        logger.info("Try to log in with blank Username and Password fields");

        loginPage.setEmptyUsername();
        loginPage.setEmptyPwd();
        loginPage.clickLogin();

        Assert.assertTrue(loginPage.getErrorMessage(), "The error message is not displayed.");

        loginPage.clearUserNameAndPwdFields();
    }

    @Test(priority = 4, description = "Testing login page with bad Username")
    public void LP_Login_CannotLoginWithBadUsername() throws InterruptedException {

        logger.info("Try to log in with wrong format Username");

        loginPage.setUserName("bademail@example");
        loginPage.setPassword(PASSWORD);
        loginPage.clickLogin();

        Assert.assertTrue(loginPage.getErrorMessage(), "The error message is not displayed.");

        loginPage.clearUserNameAndPwdFields();
    }

    @Test(priority = 5, description = "Forgot password link redirects to the appropriate password reset page")
    public void LP_Login_ForgotPasswordWorksCorrectly() throws InterruptedException {

        Assert.assertTrue(loginPage.isForgotPasswordLinkDisplayed(), "The Forgot password link is not displayed.");

        loginPage.setUserName(USER_NAME);
        loginPage.clickForgotPasswordLink();

        Assert.assertTrue(loginPage.isSuccessMessageDisplayed(), "The success message is not displayed.");
        Assert.assertTrue(loginPage.isSuccessMessageTextCorrect("We've just sent you an email to reset your password."), "The success messages is not as expected.");

        loginPage.clearUserNameAndPwdFields();

    }

    @Test(priority = 6, description = "Password reveal icon is functioning as intended")
    public void LP_Login_PasswordRevealWorksCorrectly() throws InterruptedException {

        loginPage.setUserName(USER_NAME);
        loginPage.setPassword(PASSWORD);

        Assert.assertTrue(loginPage.isPasswordVisibleAfterClickingReveal(), "The password is not revealed.");
        Assert.assertTrue(loginPage.arePasswordsMatching(PASSWORD), "Actual and revealed passwords do not match.");

        loginPage.clearUserNameAndPwdFields();
    }

    @Test(priority = 7, description = "Central login screen for the new page is functioning as intended")
    public void LP_Login_LoginScreenWorksCorrectly() throws InterruptedException {

        Assert.assertTrue(loginPage.isLoginFormDisplayed(), "The central login screen is not displayed.");

        logger.info("Try to log in with valid credentials");

        homepage = loginPage.login(USER_NAME, PASSWORD, AppHomepage.class);

        Assert.assertTrue(loginPage.isNavBarMenuDisplayed(), "The Home page logo is not displayed.");
    }
}