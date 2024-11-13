package com.DC.pageobjects.adc.identify.salesAndShare;

import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.utilities.SharedMethods;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ConversionPage extends NetNewNavigationMenu {

    private final By MANAGE_BUTTON = By.xpath("//div[@id='top-nav-right-manage']");
    private final By CLIENT_DROPDOWN = By.xpath("//div[contains(@class, 'TopNav__Users')]/button[contains(@class, 'MuiButtonBase')]/div/following-sibling::span/../../../button/div/..");
    private final By USER_DROPDOWN = By.xpath("//div[contains(@class, 'TopNav__Users')]/button[contains(@class, 'MuiButtonBase')]/div/following-sibling::span/..");
    private final By SAVE = By.xpath("//div[@role='tooltip']//button[@type='button'][normalize-space()='Save']");
    private final By PROFILE_TAB = By.xpath("//a[@id='header-group-Account-route-account-profile']");
    private final By X_API_KEY = By.xpath("//div[text()='API Key']/following-sibling::div");
    public static final By CONVERSION_TAB = By.xpath("//nav[@id='sub-nav']//div[text()='Conversion']");
    public static final By SELECT_CLIENT_POPUP = By.xpath("//span[text()='close']");
    public static final By BUS = By.xpath("//div[@class='MuiBox-root css-dvxtzn']/..");
    private static final By USERS_BUTTON = By.xpath("//a[@id='header-group-Manage-route-manage-users']");
    public final static By USER_EDIT = By.xpath("//div[text() = 'Edit']");
    public final static By USER_DELETE = By.xpath("//div[text() = 'Delete']");



    public ConversionPage(WebDriver driver) {
        super(driver);
    }

    public void selectRandomClient() throws InterruptedException {
        click(CLIENT_DROPDOWN);
        List<WebElement> buOptions = findElementsVisible(By.xpath("//div[@class='MuiBox-root css-dvxtzn']/.."));
        WebElement option = (WebElement) SharedMethods.getRandomItemFromList(buOptions);
        option.click();
        click(SAVE);
    }

    public void selectRandomUser() throws InterruptedException {
        click(USER_DROPDOWN);
        List<WebElement> buOptions = findElementsVisible(BUS);
        WebElement option = (WebElement) SharedMethods.getRandomItemFromList(buOptions);
        option.click();
        click(SAVE);
    }

    public void selectClient(String clientName) throws InterruptedException {
        click(MANAGE_BUTTON);
        click(CLIENT_DROPDOWN);
        click(By.xpath("//div[contains(@id, 'simple-tabpanel')]//span[text()='" + clientName + "']"));
    }

    public String getXapiKeyFromProfileTab() throws InterruptedException {
        click(MANAGE_BUTTON);
        click(PROFILE_TAB);
        return getTextFromElement(X_API_KEY);
    }

    public void openManageUsersPage() throws InterruptedException {
        click(MANAGE_BUTTON);
        click(USERS_BUTTON);
    }

}