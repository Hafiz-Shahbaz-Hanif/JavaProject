package com.DC.pageobjects.legacy.legacyinsights;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class ProductListsPage extends NavigationMenuLegacy {

    private final By PAGE_BODY = By.className( "property-groups" );

    public ProductListsPage(WebDriver driver) {
        super(driver);
        findElementVisible(PAGE_BODY);
        Assert.assertEquals("Product Lists | OneSpace", driver.getTitle());
    }
}
