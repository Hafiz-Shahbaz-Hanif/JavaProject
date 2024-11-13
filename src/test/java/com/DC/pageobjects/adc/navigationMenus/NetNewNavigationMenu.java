package com.DC.pageobjects.adc.navigationMenus;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NetNewNavigationMenu extends BaseNavigationMenu {
    private final By BU_FILTER = By.xpath("(//span[contains(@class, 'css-1rzb3uu')]/../.. | //button[text()='Select a business unit'])[2]");
    private final By SEARCH_BAR_BU_SELECTOR = By.xpath("//input[@placeholder='Search Company Name'] | //div[@class='MuiBox-root css-isr4pv']//input[@placeholder='Type to filter']");
    private final By BU_SELECTOR_CLEAR_BUTTON = By.xpath("//div[@role='tooltip']//button[@type='button'][normalize-space()='Cancel']");
    private final By BU_SELECTOR_SAVE_BUTTON = By.xpath("//div[@role='tooltip']//button[@type='button'][normalize-space()='Save']");
    private final By SEARCH_REPORT_BAR = By.xpath("//*[name()='svg' and @data-testid='SearchOutlinedIcon']/../input | //input[@placeholder = 'Find a report']");
    private final By SELECT_BU_RADIO_BUTTON = By.xpath("//span[contains(@class, 'css-7jwj1x')]/input[not(@checked)] | //span[contains(@class, 'css-17nlnql')]/input");
    private final By INFORMATIONAL_POP_UP_CLOSE_BUTTON = By.xpath("//button[text()='×']");
    public static final By CLIENT_ADMIN_PAGES = By.xpath("//div[@role='tooltip']//div[contains(@class, 'css-1pztb2e')]//p[not(contains(text(), 'Request'))]/../..");
    public static final By NOTIFICATIONS = By.xpath("//p[text()='Notifications']/../..");
    public static final By API_CREDENTIALS = By.xpath("//p[text()='API Credentials']/../..");
    private final By SEARCH_BAR_REPORTS = By.xpath("//ul[@role = 'listbox']/li");
    private final By CLEAR_BU_SELECTIONS = By.xpath("//button[text()='Clear Selections']");
    private final By BU_OPTIONS = By.xpath("//div[@class='MuiBox-root css-dvxtzn']");
    private final By BU_CLEAR_ORG_BUTTON = By.xpath("//*[local-name()='svg' and contains(@class,'MuiChip-deleteIcon' )]/*[local-name()='path']");

    public NetNewNavigationMenu(WebDriver driver) {
        super(driver);
    }

    public void selectBU(String... bus) throws InterruptedException {
        List<String> buList = Arrays.asList(bus);
        click(BU_FILTER);

        if (getCurrentUrl().contains("#") || getCurrentUrl().contains("app")) {
            List<WebElement> selectedBus = findElementsVisible(BU_CLEAR_ORG_BUTTON);
            for (WebElement selectedBu : selectedBus) {
                selectedBu.click();
            }
        }
        for (String bu : buList) {
            setText(SEARCH_BAR_BU_SELECTOR, bu);
            List<WebElement> buOptions = findElementsVisible(BU_OPTIONS);
            for (WebElement buOption : buOptions) {
                if (buOption.getText().contains(bu)) {
                    click(SELECT_BU_RADIO_BUTTON);
                    UI_LOGGER.info("Selected BU: " + bu);
                    break;
                }
            }
        }
        click(BU_SELECTOR_SAVE_BUTTON);
    }

    public String verifyBUSelected() {
        By filterLocator = By.xpath("//div[@class='MuiAvatar-root MuiAvatar-circular css-yc3jty']");
        return getAttribute(filterLocator, "aria-label");
    }

    public void searchForReportOrPage(String reportName) throws InterruptedException {
        setText(SEARCH_REPORT_BAR, reportName);
        click(By.xpath("//li[@role='option' and text()='" + reportName + "']"));
    }

    public void closeInformationalPopUp(String pageName) throws InterruptedException {
        if (isElementVisible(By.xpath("//p[text()='" + pageName + "']"))) {
            click(INFORMATIONAL_POP_UP_CLOSE_BUTTON);
        }
    }

    public By createNavbarLinkLocator(String linkText) {
        return By.xpath("//div[@role='tooltip']//p[text()='" + linkText + "']");
    }

    public List<String> getSearchBarReports() throws InterruptedException {
        Thread.sleep(4000);
        click(SEARCH_REPORT_BAR);
        List<WebElement> reports = findElementsVisible(SEARCH_BAR_REPORTS);
        List<String> reportList = getTextFromElements(reports);
        Collections.sort(reportList);
        return reportList;
    }

    public List<String> getNavbarPagesBySection(String module, String section) throws InterruptedException {
        List<String> urls = new ArrayList<>();
        clickOnNavbarModule(module);
        List<WebElement> pages = findElementsVisible(By.xpath("//div[@role='tooltip']//p[text()='" + section + "']/../div//p"));

        for (WebElement page : pages) {
            urls.add(page.getText());
        }

        clickOnNavbarModule(module);
        Collections.sort(urls);
        return urls;
    }

    public void clickOnNavbarModule(String module) throws InterruptedException {
        click(By.xpath("//button[text()='" + module + "']"));
    }

    public void openPageViaNavBar(String module, String page) throws InterruptedException {
        clickOnNavbarModule(module);
        click(By.xpath("//p[text()='" + page + "']/.."));
    }

    public List<String> getUserProfilePagesBySection(String section) {
        List<String> urls = new ArrayList<>();
        List<WebElement> pages = findElementsVisible(By.xpath("//p[text()='" + section + "']/../div//p"));

        for (WebElement page : pages) {
            if (!page.getText().equals("Log Out")) {
                urls.add(page.getText());
            }
        }
        Collections.sort(urls);
        return urls;
    }

}
