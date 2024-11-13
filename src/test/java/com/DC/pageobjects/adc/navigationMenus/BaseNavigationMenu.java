package com.DC.pageobjects.adc.navigationMenus;

import com.DC.pageobjects.PageHandler;
import com.DC.pageobjects.adc.AppHomepage;
import com.DC.pageobjects.adc.DCLoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

public abstract class BaseNavigationMenu extends PageHandler {

    public final By IDENTIFY_DROPDOWN = By.xpath("//button[text()='Identify']");
    public final By ANALYZE_DROPDOWN = By.xpath("//button[text()='Analyze']");
    public final By EXECUTE_DROPDOWN = By.xpath("//button[text()='Execute']");
    public final By USER_PROFILE_ICON = By.xpath("//span[text()='account_circle']/ancestor::button");
    public final By LOGOUT_BUTTON = By.xpath("//a[text()='Log Out']");
    public final By LINKS_NAV_BAR = By.xpath("//div[@role='tooltip']//a[parent::span]/p | //div[@role='tooltip']//p[preceding-sibling::span] | (//div[@class='MuiBox-root css-jym0mx'])[2]//div[@class='MuiStack-root css-mw6hv2']//p");
    public static final By HOMEPAGE_LOGO = By.xpath("//img[@alt='logo']");
    public static final By CONNECT_BU_DROPDOWN_IMAGE = By.xpath("//img[@alt='s']");
    public static final By CONNECT_BU_DROPDOWN = By.xpath("//img[@alt='s']/..");
    public static final By CONNECT_BU_DROPDOWN_LIST = By.xpath("//ul[@role='menu']//li");
    public static final By HIDE_RC_BUTTON = By.xpath("//button[@id='hide-rc' and (contains(@style,'rotate(0deg)') or not(@style))]");

    public BaseNavigationMenu(WebDriver driver) {
        super(driver);
        findElementVisible(HOMEPAGE_LOGO);
    }

    public DCLoginPage logout() throws InterruptedException {
        hoverOverElement(USER_PROFILE_ICON);
        click(LOGOUT_BUTTON);
        return new DCLoginPage(driver);
    }

    public List<String> getNavBarSectionLinks(String section) throws InterruptedException {

        By navBarDropdownOption = By.xpath("//img[@alt='logo']/ancestor::a/following-sibling::div//button[text()='" + section + "']");
        moveToElementAndClick(navBarDropdownOption);

        List<WebElement> links = findElementsVisible(LINKS_NAV_BAR);
        List<String> linkTexts = getTextFromElements(links);

        click(navBarDropdownOption);

        return linkTexts;
    }

    public List<String> getUserProfileIconLinks() throws InterruptedException {

        moveToElementAndClick(USER_PROFILE_ICON);

        List<WebElement> links = findElementsVisible(LINKS_NAV_BAR);

        return getTextFromElements(links);
    }

    public AppHomepage clickFWLogo() throws InterruptedException {
        click(HOMEPAGE_LOGO);
        return new AppHomepage(driver);
    }

    public boolean isBlankPageDisplayed() {
        return !isElementPresent(HOMEPAGE_LOGO);
    }

    // Not sure if this is the best way to do this, but it works for now.
    public <T> T recoverPageIfBlankPageIsDisplayed(String urlToRecover, T classObject) throws Exception {
        boolean blankPage = isBlankPageDisplayed();
        if (blankPage) {
            driver.get(urlToRecover);
            @SuppressWarnings("unchecked")
            Class<T> targetClass = (Class<T>) classObject.getClass();
            return targetClass.getDeclaredConstructor(WebDriver.class).newInstance(driver);
        }
        return classObject;
    }

    public void hideResourceCenter() {
        if (isElementVisible(HIDE_RC_BUTTON, Duration.ofSeconds(3))) {
            try {
                clickElement(HIDE_RC_BUTTON);
            } catch (StaleElementReferenceException e) {
                clickElement(HIDE_RC_BUTTON);
            }
            waitForDOMStabilization();
        }
    }
}
