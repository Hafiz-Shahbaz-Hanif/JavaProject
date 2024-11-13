package com.DC.pageobjects.adc.analyze.retailReporting;

import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.utilities.DateUtility;
import com.DC.utilities.SQLUtility;
import org.openqa.selenium.*;
import org.testng.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;

public class CaseManagementReportingPage extends NetNewNavigationMenu {
    private static final By CLIPPER_HEADER = By.xpath("//a[text()='Case Management Reporting']");
    private static final By TOTAL_CASES_SUBMITTED = By.xpath("//h4[text()='Total Cases Submitted']");
    private static final By TIME_SAVED = By.xpath("//h4[text()='Time Saved']");
    private static final By CONVERSION_RATE = By.xpath("//h4[text()='Conversion Rate']");
    private static final By DOWNLOAD_BUTTON = By.xpath("//button[@type='button']//span[contains(text(),'download')]");
    private static final By WORK_FLOW_LABS_LOGO = By.id("topNavigation_logoIcon");
    private static final By WORK_FLOW_LABS_USERNAME_FIELD = By.id("username");
    private static final By WORK_FLOW_LABS_PASSWORD_FIELD = By.id("password");
    private static final By WORK_FLOW_LABS_LOGIN_BUTTON = By.xpath("//button[@type='submit']");
    private static final By DAYS_OPEN_COLUMN_HEADER = By.xpath("//h3[@aria-label='Days Open']");
    private static final By SEARCH_FIELD = By.xpath("//input[@placeholder='Search']");
    private static final By CREATED_ON_COLUMN = By.xpath("//div[@col-id='createdOn']/div/span");
    private static final By DAYS_OPEN_TOOLTIP = By.xpath("//div[@aria-label='Tooltip']");
    private static final By ASINS_IN_TABLE = By.xpath("//div[@col-id='asin']//span/a");

    public String getClientNameXpath(String clientName) {
        return "//div[@id='SideMenu_accounts_accordion']//span[text()='" + clientName + "']";
    }

    public CaseManagementReportingPage(WebDriver driver) {
        super(driver);
        findElementVisible(CLIPPER_HEADER);
    }

    public boolean isTotalCaseSubmittedPresent() {
        return isElementVisible(TOTAL_CASES_SUBMITTED);
    }

    public boolean isTimeSavedPresent() {
        return isElementVisible(TIME_SAVED);
    }

    public boolean isConversionRatePresent() {
        return isElementVisible(CONVERSION_RATE);
    }

    public boolean isClipperScreenAdded() {return isElementVisible(CLIPPER_HEADER);}

    public boolean isDownloadButtonVisible() {return isElementVisible(DOWNLOAD_BUTTON);}

    public void clickDownloadButton() throws InterruptedException {click(DOWNLOAD_BUTTON);}

    public int getWFLId(String clientName) throws SQLException {
        int fwId = getIdAssignedByFW(clientName);
        if (fwId == 0) {
            Assert.fail("Client with name " + clientName + " was not found in the database. Please check the database.");
        }
        UI_LOGGER.info("FW ID for client " + clientName + " is " + fwId);
        return getIdAssignedByWFL(fwId);
    }

    public int getIdAssignedByFW(String clientsName) throws SQLException {
        int fwId = 0;
        SQLUtility.connectToServer();
        ResultSet resultSet = SQLUtility.executeQuery(getFWIdForClient(clientsName));
        while (resultSet.next()) {
            fwId = resultSet.getInt("ClientAccountId");
        }
        SQLUtility.closeConnections();
        return fwId;
    }

    public int getIdAssignedByWFL(int fwId) throws SQLException {
        int wflId = 0;
        SQLUtility.connectToServer();
        ResultSet resultSet = SQLUtility.executeQuery(getWFLIdForClient(fwId));
        while (resultSet.next()) {
            if (resultSet.getInt("CLIENT_ACCOUNT_ID") == fwId && resultSet.getString("IS_ACTIVE").equals("Y")) {
                wflId = resultSet.getInt("ORGANIZATION_ID");
                break;
            } else {
                Assert.fail("Client with FW ID " + fwId + " was not found in the database or is not active. Please check the database.");
            }
        }
        SQLUtility.closeConnections();
        return wflId;
    }

    public void loginToWFL(String wflUrl, String wflUsername, String wflPwd) throws InterruptedException {
        openNewTabAndSwitchToMakeItActive(wflUrl);
        sendKeys(WORK_FLOW_LABS_USERNAME_FIELD, wflUsername);
        sendKeys(WORK_FLOW_LABS_PASSWORD_FIELD, wflPwd);
        click(WORK_FLOW_LABS_LOGIN_BUTTON);
        findElementVisible(WORK_FLOW_LABS_LOGO);
    }

    public boolean isClientNameFound(String clientName) throws InterruptedException {
        String clientNameXpath = getClientNameXpath(clientName);
        By clientNameElement = By.xpath(clientNameXpath);
        if (isElementVisible(clientNameElement)) {
            click(clientNameElement);
            return true;
        }
        return false;
    }

    public boolean isWFLIdInUrl(int wflId) {
        return getCurrentUrl().contains(String.valueOf(wflId));
    }

    public static String getFWIdForClient(String clientsName) {
        return "SELECT * FROM T_VC_CLIENT_ACCOUNT_MAPPING WHERE VC_Account_Name = '" + clientsName + "'";
    }

    public static String getWFLIdForClient(int fwId) {
        return "select * from T_CLIENT_ACCOUNT_WFL_ORGANIZATION WHERE CLIENT_ACCOUNT_ID = " + fwId + "";
    }

    public boolean isDaysOpenColumnVisible() {
        scrollIntoView(DAYS_OPEN_COLUMN_HEADER);
        return isElementVisible(DAYS_OPEN_COLUMN_HEADER);
    }

    public void searchForAsin(String asin) throws InterruptedException {
        sendKeys(SEARCH_FIELD, asin);
        Thread.sleep(3000);
        List<WebElement> asinElements = findElementsVisible(ASINS_IN_TABLE);
        for (WebElement asinElement : asinElements) {
            Assert.assertEquals(asin, asinElement.getText(), "ASIN " + asin + " was not found in the table");
        }
    }

    public boolean checkDaysOpenCalculation() {
        String today = DateUtility.getTodayDate();

        List<WebElement> createdOnElements = findElementsVisible(CREATED_ON_COLUMN);
        for (int i = 0; i < createdOnElements.size(); i++) {
            WebElement createdOnElement = createdOnElements.get(i);
            String createdOnDate = createdOnElement.getText();
            String formattedDate = DateUtility.formatDateNonStandard(createdOnDate);
            By daysOpen = By.xpath("ancestor::div[@row-index='" + i + "']//div[@col-id='daysOpen']");
            WebElement daysOpenElement = createdOnElement.findElement(daysOpen);
            String numberOfDaysOpen = daysOpenElement.getText();

            int days = DateUtility.getDaysBetweenDates(formattedDate, today);
            if (days > 3) {
                hoverOverElement(daysOpenElement);
                findElementVisible(DAYS_OPEN_TOOLTIP);
                By tooltipTextElement = By.xpath("//div[@aria-label='Tooltip']//span");
                String tooltipText = findElementVisible(tooltipTextElement).getText();
                String expectedTooltipText = "This issue is still persistent on site, and the case has been open for " + days + " days.";
                Assert.assertTrue(tooltipText.contains(expectedTooltipText), "Tooltip text is not as expected");
                hoverOverElement(DAYS_OPEN_COLUMN_HEADER);
                waitForElementToBeInvisible(DAYS_OPEN_TOOLTIP, Duration.ofSeconds(5));
            }
            if (!numberOfDaysOpen.equals(String.valueOf(days))) {
                return false;
            }
        }
        return true;
    }
}