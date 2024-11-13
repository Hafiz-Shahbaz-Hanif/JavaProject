package com.DC.pageobjects.adc.analyze.paidMediaReporting;

import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.List;

public class StreamDashboardPage extends NetNewNavigationMenu {
    private static final By DOWNLOAD_BUTTON = By.xpath("//span[contains(text(),'download')]");
    private static final By APPLY_BUTTON = By.xpath("//button[text()='Apply']");
    private static final By CAMPAIGN_TYPE_FILER = By.xpath("//h6[text()='Campaign Type']/..//following-sibling::div//input");
    private static final By HOURLY_STREAM_REPORTING_TABLE_ROWS = By.xpath("//div[contains(@style,'flex-direction: row;')]");
    private static final By FILTER_ELEMENTS = By.xpath("//div[@role='presentation']//span[text()]");

    public StreamDashboardPage(WebDriver driver) {
        super(driver);
    }

    public void clickDownloadButton() throws InterruptedException {
        click(DOWNLOAD_BUTTON);
    }

    public void selectItemFromCampaignTypeFilterDropdown(String optionToSelect) throws InterruptedException {
        selectItemsFromDropdownFilter(CAMPAIGN_TYPE_FILER, List.of(optionToSelect));
        click(APPLY_BUTTON);
    }

    public List<String> getAvailableOptionsFromCampaignTypeFilter() throws InterruptedException {
        return openSelectedFilterAndGetAllValues(CAMPAIGN_TYPE_FILER, FILTER_ELEMENTS);
    }

    public int getHourlyStreamReportingRowsCount() {
        waitForElementClickable(APPLY_BUTTON, Duration.ofSeconds(10));
        return getElementCount(HOURLY_STREAM_REPORTING_TABLE_ROWS);
    }
}