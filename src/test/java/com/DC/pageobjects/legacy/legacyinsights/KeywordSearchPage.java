package com.DC.pageobjects.legacy.legacyinsights;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class KeywordSearchPage extends NavigationMenuLegacy {

    private final By PAGE_BODY = By.className( "keyword-search" );

    public KeywordSearchPage(WebDriver driver) {
        super( driver);
        findElementVisible(PAGE_BODY);
        Assert.assertEquals( "Keyword Search | OneSpace", driver.getTitle() );
    }
}
