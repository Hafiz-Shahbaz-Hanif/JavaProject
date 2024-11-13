package com.DC.pageobjects.adc;

import com.DC.pageobjects.PageHandler;
import com.DC.pageobjects.adc.navigationMenus.BaseNavigationMenu;
import com.DC.utilities.ReadConfig;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.NoSuchElementException;

public class DCLoginPage extends PageHandler {
    private static final By LOGIN_FORM = By.xpath("//h3[text()='Log In']");
    private static final By EMAIL_ADDRESS_BOX = By.xpath("//*[@id='email' or @id='username']");
    private static final By PASSWORD_BOX = By.id("password");
    public static final By LOGIN_BUTTON = By.xpath("//button[@type='submit' and text()='Log In' and not(@aria-hidden) or @id='btn-login']");
    private static final By FORGOT_PASSWORD_LINK = By.id("forgot-password");
    private static final By NAVBAR_MENU = By.tagName("header");
    private static final ReadConfig READ_CONFIG = ReadConfig.getInstance();
    private static final Logger LOGGER = Logger.getLogger(DCLoginPage.class);
    private static final By ERROR_MESSAGE = By.cssSelector("div#error-message");
    private static final By PASSWORD_REVEAL_ICON = By.id("password-mask");
    private static final By SUCCESS_MESSAGE_FORGOT_PASSWORD = By.id("success-message");
    private static final By USER_ICON = By.xpath("//span[text()='account_circle']/.. | //img[contains(@src, 'account')]/..");
    private static final By LOGOUT = By.xpath("//a[text()='Log Out'] | //p[text()='Log Out']");
    private static final By INSIGHTS_USER_ICON = By.xpath("//*[name()='svg']/../img[contains(@src, 'data:image')]/..");
    private static final By FILA_LEGACY_LOGOUT_ICON = By.xpath("//img[contains(@src, 'data:image/png') and not(@alt)]/..");

    public DCLoginPage(WebDriver driver) {
        super(driver);
    }

    public void loadPage() throws InterruptedException {
        String pageUrl = READ_CONFIG.getDcAppUrl();
        Thread.sleep(5000);
        driver.get(pageUrl);
        LOGGER.info("Page loaded successfully");
    }

    public boolean isLoginFormDisplayed() {
        return isElementVisible(LOGIN_FORM);
    }

    public void setUserName(String userName) {
        sendKeys(EMAIL_ADDRESS_BOX, userName);
    }

    public void setPassword(String password) {
        sendKeys(PASSWORD_BOX, password);
    }

    public void clickLogin() throws InterruptedException {
        scrollIntoViewAndClick(LOGIN_BUTTON);
    }

    public boolean isNavBarMenuDisplayed() {
        return isElementVisible(NAVBAR_MENU);
    }

    public boolean isForgotPasswordLinkDisplayed() {
        return isElementVisible(FORGOT_PASSWORD_LINK);
    }

    public void clickForgotPasswordLink() throws InterruptedException {
        click(FORGOT_PASSWORD_LINK);
    }

    public boolean isSuccessMessageDisplayed() {
        return isElementVisible(SUCCESS_MESSAGE_FORGOT_PASSWORD);
    }

    public boolean isSuccessMessageTextCorrect(String expectedText) {
        WebElement successMessage = findElementVisible(SUCCESS_MESSAGE_FORGOT_PASSWORD);
        String actualText = successMessage.getText();
        return expectedText.equals(actualText);
    }

    public boolean isPasswordVisibleAfterClickingReveal() throws InterruptedException {
        click(PASSWORD_REVEAL_ICON);

        WebElement passwordField = findElementVisible(PASSWORD_BOX);
        String inputType = passwordField.getAttribute("type");

        return inputType.equalsIgnoreCase("text");
    }

    public boolean arePasswordsMatching(String expectedPwd) {
        WebElement passwordField = findElementVisible(PASSWORD_BOX);
        String revealedPwd = passwordField.getAttribute("value");
        return expectedPwd.equals(revealedPwd);
    }

    public void setEmptyUsername() {
        setText(EMAIL_ADDRESS_BOX, "");
    }

    public void setEmptyPwd() {
        setText(PASSWORD_BOX, "");
    }

    public void clearUserNameAndPwdFields() {
        clearInput(EMAIL_ADDRESS_BOX);
        clearInput(PASSWORD_BOX);
    }

    public boolean getErrorMessage() {
        try {
            return isElementVisible(ERROR_MESSAGE, Duration.ofSeconds(5));
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void login(String userName, String password) throws InterruptedException {
        setUserName(userName);
        setPassword(password);
        clickLogin();
        Thread.sleep(500);
        findElementVisible(NAVBAR_MENU, Duration.ofSeconds(60));
    }

    public <T> T login(String userName, String password, Class<T> pageClass) throws InterruptedException {
        login(userName, password);
        return (T) getPage(pageClass);
    }

    public void loginDcApp(String userName, String password) throws InterruptedException {
        setUserName(userName);
        setPassword(password);
        clickLogin();
        findElementVisible(BaseNavigationMenu.HOMEPAGE_LOGO, Duration.ofSeconds(60));
    }

    public void openLoginPage(WebDriver driver, String url) throws InterruptedException {
        driver.get(url);
    }

    public void logoutDcApp() throws InterruptedException {
        openUserProfile();
        click(LOGOUT);
    }

    public void logoutFilaLegacyApp() throws InterruptedException {
        click(FILA_LEGACY_LOGOUT_ICON);
        click(LOGOUT);
    }

    public void logoutInsightsDcApp() throws InterruptedException {
        hoverOverElement(INSIGHTS_USER_ICON);
        click(LOGOUT);
    }

    public void openUserProfile() throws InterruptedException {
        click(USER_ICON);
    }
}