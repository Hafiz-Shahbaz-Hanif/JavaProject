package com.DC.pageobjects.adc.identify.searchInsights.keywords;

import com.DC.pageobjects.adc.navigationMenus.InsightsNavigationMenu;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class KeywordWatchlistsPage extends InsightsNavigationMenu {
    private final String PAGE_BODY_XPATH = "//div[@data-qa='Watchlists']";
    private final By LOADER_LOCATOR = By.xpath("//div[@data-qa='LoadState']//h4[text()='Loading Watchlists']");

    public KeywordWatchlistsPage(WebDriver driver) {
        super(driver);
        waitForElementToBeInvisible(LOADER_LOCATOR);
        findElementVisible(By.xpath(PAGE_BODY_XPATH));
    }

    public WatchlistDetailsPage clickViewDetailsButton(String watchlistName) {
        var viewDetailsButtonXpath = String.format("//div[contains(@class,'Header-root') and descendant::h3[text()='%s']]//button[text()='View Details']", watchlistName);
        clickElement(By.xpath(viewDetailsButtonXpath));
        return new WatchlistDetailsPage(driver);
    }
}
