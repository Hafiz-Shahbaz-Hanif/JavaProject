package com.DC.pageobjects.legacy.legacyinsights;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class WatchlistsPage extends NavigationMenuLegacy {

    protected final By DEFAULT_LOADER = By.xpath("//div[@data-qa='LoadState']");
    private final By CREATE_WATCHLISTS_BUTTON = By.xpath( "//div[@data-qa='Watchlists']//span[text()='Create Watchlist']" );

    public WatchlistsPage(WebDriver driver) {
        super( driver);
        waitForElementToBeInvisible(DEFAULT_LOADER);
        findElementVisible(CREATE_WATCHLISTS_BUTTON);
        Assert.assertEquals( "Watchlists | OneSpace", driver.getTitle() );
    }
}
