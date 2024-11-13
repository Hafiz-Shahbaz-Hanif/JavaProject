package com.DC.pageobjects.adc;

import com.DC.pageobjects.adc.identify.salesAndShare.ConversionPage;
import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.utilities.ReadConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppHomepage extends NetNewNavigationMenu {

    public static final By HOMEPAGE_LOGO = By.xpath("//img[@alt='logo']");
    private static final By IDENTIFY_HEADER = By.xpath("//p[text()='Identify']");
    private static final By ANALYZE_HEADER = By.xpath("//p[text()='Analyze']");
    private static final By EXECUTE_HEADER = By.xpath("//p[text()='Execute']");
    private static final By MANAGE_HEADER = By.xpath("//p[text()='Manage']");
    private static final By SEARCH_FIELD = By.xpath("//input[@placeholder='Find a report']");
    public static final By ATTRIBUTE_INSIGHTS = By.xpath("//a[text()='Attribute Insights']");
    public static final By CONVERSION = By.xpath("//a[text()='Conversion']");
    public static final By PDP_CHANGE_DASHBOARD = By.xpath("//a[text()='PDP Change Dashboard']");
    public static final By ROUND_UP = By.xpath("//a[text()='Round Up']");
    public static final By MEDIA_EXECUTIVE_DASHBOARD = By.xpath("//a[text()='Executive Dashboard']");
    public static final By MEDIA_SCRATCHPAD = By.xpath("//a[text()='Media Scratchpad']");
    public static final By RETAIL_SCRATCHPAD = By.xpath("//a[text()='Retail Scratchpad']");
    public static final By AT_A_GLANCE = By.xpath("//a[text()='At a Glance']");
    public static final By ALL_LINKS_LOCATOR = By.xpath("//a[parent::span]/p | //p[preceding-sibling::span] | //div[@class='MuiGrid-root MuiGrid-container MuiGrid-spacing-xs-2 css-1y9fiox']//div[@class='MuiStack-root css-mw6hv2']//p ");
    public static final By MODULES = By.xpath("//p[text()='Analyze']/../../div");
    public static final By PAGES = By.xpath("//div[contains(@class, 'css-1pztb2e')]");


    public By createLinkLocator(String linkText) {
        return By.xpath("//a[text()='" + linkText + "'] | //p[text()='" + linkText + "'] | //span[text()='" + linkText + "'] | //div[text()='" + linkText + "']");
    }

    public AppHomepage(WebDriver driver) {
        super(driver);
        findElementVisible(HOMEPAGE_LOGO);
    }

    public boolean isSearchBarDisplayed() {
        return isElementVisible(SEARCH_FIELD);
    }

    public boolean isIdentifyDropdownDisplayed() {
        return isElementVisible(IDENTIFY_DROPDOWN);
    }

    public boolean isAnalyzeDropdownDisplayed() {
        return isElementVisible(ANALYZE_DROPDOWN);
    }

    public boolean isExecuteDropdownDisplayed() {
        return isElementVisible(EXECUTE_DROPDOWN);
    }

    public boolean isUserProfileIconDisplayed() {
        return isElementVisible(USER_PROFILE_ICON);
    }

    public void linksVerification(String sectionName) throws InterruptedException {
        clickOnSection(sectionName);
        List<WebElement> links = findElementsVisible(ALL_LINKS_LOCATOR);
        List<String> linkNames = new ArrayList<>();
        List<String> brokenLinks = new ArrayList<>();
        List<String> nonNavigatingLinks = new ArrayList<>();
        List<String> accessDeniedLinks = new ArrayList<>();
        for (WebElement link : links) {
            linkNames.add(link.getText());
        }
        By accessDeniedLocator = By.xpath("//h3[text()='Access Denied ']");

        for (String linkName : linkNames) {
            clickOnSection(sectionName);
            try {
                WebElement link = findElementVisible(By.xpath("//*[text()='" + linkName + "']"));
                String currentUrl = getCurrentUrl();
                link.click();
                Thread.sleep(500);
                String newUrl = getCurrentUrl();

                if (currentUrl.equals(newUrl)) {
                    nonNavigatingLinks.add(linkName);
                    UI_LOGGER.info("Link " + linkName + " does not navigate to a new page.");
                } else {
                    By headerLocator = By.xpath("//*[text()='" + linkName + "']");
                    if (!isElementVisible(headerLocator)) {
                        if (isElementVisible(accessDeniedLocator)) {
                            accessDeniedLinks.add(linkName);
                            UI_LOGGER.info("Access denied for link " + linkName);
                        } else {
                            brokenLinks.add(linkName);
                            UI_LOGGER.error("Broken link found for " + linkName);
                        }
                    } else {
                        String headerName = getTextFromElement(headerLocator, Duration.ofSeconds(5));
                        if (!linkName.equals(headerName)) {
                            UI_LOGGER.info("Header name does not match the link name for link: " + linkName);
                        } else {
                            UI_LOGGER.info("Link " + linkName + " was verified successfully.");
                        }
                    }
                }
            } catch (Exception e) {
                UI_LOGGER.error("Error while verifying link " + linkName + ": " + e.getMessage());
            } finally {
                UI_LOGGER.info("Navigating back to the homepage.");
                if (isElementVisible(HOMEPAGE_LOGO)) {
                    click(HOMEPAGE_LOGO);
                    UI_LOGGER.info("Clicked on the homepage logo.");
                } else {
                    navigateBack();
                    UI_LOGGER.info("Navigated back.");
                    UI_LOGGER.info("Waiting for the homepage to load.");
                }
            }
        }
        UI_LOGGER.info("Broken links: " + brokenLinks);
        UI_LOGGER.info("Non-navigating links: " + nonNavigatingLinks);
        UI_LOGGER.info("Access denied links: " + accessDeniedLinks);
    }

    public void searchForAReportMetricOrPage(String searchTerm) {

        sendKeys(SEARCH_FIELD, searchTerm);
    }

    public List<String> getSearchResults() {
        By searchResultsLocator = By.xpath("//ul[@role='listbox']/li");
        List<WebElement> searchResults = findElementsVisible(searchResultsLocator);

        List<String> searchResultsList = new ArrayList<>();

        for (WebElement searchResult : searchResults) {
            String searchResultText = searchResult.getText();
            searchResultsList.add(searchResultText);
        }
        return searchResultsList;
    }

    public List<String> getSectionLinks(String section) throws InterruptedException {
        clickOnSection(section);
        List<WebElement> linkElements = findElementsVisible(ALL_LINKS_LOCATOR);
        return getTextFromElements(linkElements);
    }

    // will be updated when all links will be working
    public boolean verifyClickingOnFWLogoReturnsToHomePage() throws InterruptedException {

        click(HOMEPAGE_LOGO);
        String homepageURL = getCurrentUrl();

        for (String link : getSectionLinks("Analyze")) {
            if (link.equalsIgnoreCase("Data as a Service")) {
                waitForElementClickable(createLinkLocator(link), Duration.ofSeconds(5));
                click(createLinkLocator(link));
                click(HOMEPAGE_LOGO);
            }
        }

        String currentURL = getCurrentUrl();
        return currentURL.equals(homepageURL);
    }

    public void clickLink(String linkText) throws InterruptedException {
        click(createLinkLocator(linkText));
    }

    public void openPage(String page) throws InterruptedException {
        goToHomePage().clickLink(page);
    }

    public void loadHomePage() {
        driver.get(ReadConfig.getInstance().getDcAppUrl());
    }

    public AppHomepage goToHomePage() {
        if (!getCurrentUrl().equals(ReadConfig.getInstance().getDcAppUrl())) {
            loadHomePage();
            return new AppHomepage(driver);
        }
        return this;
    }

    public By createSectionLocator(String sectionName) {
        return By.xpath("//div[@class='MuiGrid-root MuiGrid-container MuiGrid-spacing-xs-2 css-1y9fiox']//p[text()='" + sectionName + "']");
    }

    public void clickOnSection(String sectionName) throws InterruptedException {
        click(createSectionLocator(sectionName));
    }

    public boolean isLoggedIn() {
        return isElementVisible(USER_PROFILE_ICON);
    }

    public void openPage(String module, String page) throws InterruptedException {
        goToHomePage();
        openModule(module);
        clickLink(page);

        if (page.equalsIgnoreCase("conversion")) {
            if (isElementVisible(ConversionPage.SELECT_CLIENT_POPUP)) {
                click(ConversionPage.SELECT_CLIENT_POPUP);
            }
        }
    }

    public void openModule(String module) throws InterruptedException {
        goToHomePage();

        if (isElementVisible(By.xpath("//button[contains(@id, 'pendo-close')]"), Duration.ofSeconds(1))) {
            click(By.xpath("//button[contains(@id, 'pendo-close')]"));
        }

        click(By.xpath("//p[text()='" + module + "']/.."));
    }

    public List<String> getModulePagesBySection(String module, String section) throws InterruptedException {
        List<String> urls = new ArrayList<>();

        click(By.xpath("//p[text()='" + module + "']/.."));
        List<WebElement> pages = findElementsVisible(By.xpath("//p[text()='" + section + "']/../div//p"));

        for (WebElement page : pages) {
            urls.add(page.getText());
        }
        Collections.sort(urls);
        return urls;
    }
}
