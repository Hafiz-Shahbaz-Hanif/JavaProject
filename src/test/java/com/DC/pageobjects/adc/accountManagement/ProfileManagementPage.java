package com.DC.pageobjects.adc.accountManagement;

import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.pageobjects.filters.DCFilters;
import com.DC.utilities.sharedElements.DateAndIntervalPickerPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProfileManagementPage extends NetNewNavigationMenu {

    public DateAndIntervalPickerPage dateAndIntervalPickerPage;
    public DCFilters dcFilters;

    private final By PROFILE_MANAGEMENT_HEADER = By.xpath("//h4[normalize-space()='Profile Management']");
    private final By PROFILE_ICON = By.xpath("//button[contains(@class, 'MuiButton-colorInherit css-2yuuxm')]");
    private final By PROFILE_INFORMATION_OPTION = By.xpath("//a[normalize-space()='Profile Information']");
    private final By NOTIFICATION_TAB = By.xpath("//button[@id='tab-1']");
    private final By CREDENTIAL_API_TAB = By.xpath("//button[@id='tab-2']");
    private final By INFORMATION_TAB = By.xpath("//button[@id='tab-0']");
    private final By RESET_PASSWORD = By.xpath("//span[@id='reset-password-link-button']");
    private final By FIRST_NAME_INPUT_FIELD = By.xpath("//input[@id='mui-3']");
    private final By LAST_NAME_INPUT_FIELD = By.xpath("//input[@id='mui-4']");

    private final By USERNAME_INPUT_FIELD = By.xpath("//input[@id='mui-5']");

    private final By EMAIL_ADDRESS_INPUT_FIELD = By.xpath("//input[@id='mui-6']");





    public ProfileManagementPage(WebDriver driver) {
        super(driver);
    }

    public boolean isProfileManagementScreenDisplayed() {
        return isElementVisible(PROFILE_MANAGEMENT_HEADER);
    }

    public void OpenProfileManagementPage() throws InterruptedException {
        UI_LOGGER.info("Hover on Profile Section");
        hoverOverElement(PROFILE_ICON);
        UI_LOGGER.info("Click on Profile Information Option");
        findElementVisible(PROFILE_INFORMATION_OPTION);
        UI_LOGGER.info("Click on Profile Information");
        click(PROFILE_INFORMATION_OPTION);
    }

    public void NotificationTabDisplayed(){
        findElementVisible(NOTIFICATION_TAB);
    }
    public void verifyNotificationTabIsCickable() throws InterruptedException {
        UI_LOGGER.info("Click on Notification Tab");
        isElementClickable(NOTIFICATION_TAB);
        UI_LOGGER.info("Click Notification Tab");
        click(NOTIFICATION_TAB);

    }

    public void CredentialApiTabDisplayed(){
        findElementVisible(CREDENTIAL_API_TAB);
    }

    public void verifyCredentialApiTabIsClickable() throws InterruptedException {
        UI_LOGGER.info("Click on Credential Api Tab");
        isElementClickable(CREDENTIAL_API_TAB);
        UI_LOGGER.info("Click Credential Api Tab");
        click(CREDENTIAL_API_TAB);

    }

    public void InformationTabDisplayed(){
        findElementVisible(INFORMATION_TAB);
    }

    public void verifyInformationTabIsClickable() throws InterruptedException {
        UI_LOGGER.info("Click on Information Tab");
        isElementClickable(INFORMATION_TAB);
        UI_LOGGER.info("Click Information Tab");
        click(INFORMATION_TAB);

    }
    public boolean resetPasswordButtionDisplayed(){
        return isElementVisible(RESET_PASSWORD);
    }

    public boolean FirstNameFieldIsDisplayed(){
        return isElementVisible(FIRST_NAME_INPUT_FIELD);
    }

    public boolean LastNameFieldIsDisplayed(){
        return isElementVisible(LAST_NAME_INPUT_FIELD);
    }
    public boolean UsernameFieldIsDisplayed(){
        return isElementVisible(USERNAME_INPUT_FIELD);
    }
    public boolean EmailAddressFieldIsDisplayed(){
        return isElementVisible(EMAIL_ADDRESS_INPUT_FIELD);
    }
}
