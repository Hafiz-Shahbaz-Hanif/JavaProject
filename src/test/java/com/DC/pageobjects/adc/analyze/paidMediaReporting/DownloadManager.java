package com.DC.pageobjects.adc.analyze.paidMediaReporting;

import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DownloadManager extends NetNewNavigationMenu {
    private static final By QUERY_COLUMN_NAME = By.xpath("//span[text()='Query']");
    private static final By RELOAD_BUTTON = By.xpath("//span[text()='autorenew']");

    public DownloadManager(WebDriver driver) {
        super(driver);
    }

    public String moveToDownloadManagerTab() {
        waitForNewTabToOpen(2);
        switchToTab(2, 1);
        return getTextFromElement(By.xpath("//h4[text()='Download manager']"));
    }

    public void clickOnCorrectDownloadOption(String timeOfDownload, int maxRetries) throws InterruptedException {
        findElementVisible(QUERY_COLUMN_NAME);
        int retryCount;
        for (retryCount = 0; retryCount <= maxRetries; retryCount++) {
            if (retryCount == maxRetries) {
                UI_LOGGER.info("Failed to find completed status for max retry count");
                throw new RuntimeException("Failed to find completed status for max retry count");
            }
            String status = getStatusForDownload(timeOfDownload);
            if (status.equalsIgnoreCase("COMPLETED")) {
                By fileExportButton = By.xpath("//div[@role='row' and child::div[@role='gridcell' and descendant::span[contains(text(),'" + timeOfDownload + "')]]]//div[@col-id='fileExport']//button");
                click(fileExportButton);
                UI_LOGGER.info("Clicked on the file export button for the row with time " + timeOfDownload + " and status " + status + " and file export is visible");
                break;
            } else if (status.equalsIgnoreCase("In Progress")) {
                UI_LOGGER.info("Clicking on the reload button for the row with time " + timeOfDownload + " and status " + status);
                scrollIntoViewAndClick(RELOAD_BUTTON);
                Thread.sleep(2000);
            }
        }
    }

    public String getStatusForDownload(String timeOfDownload) {
        var rowLocatorXpath = "//div[@role='row' and child::div[@role='gridcell' and descendant::span[contains(text(),'" + timeOfDownload + "')]]]";
        var statusLocator = By.xpath(rowLocatorXpath + "//div[@col-id='status']//span//span");
        return getTextFromElement(statusLocator);
    }
}
