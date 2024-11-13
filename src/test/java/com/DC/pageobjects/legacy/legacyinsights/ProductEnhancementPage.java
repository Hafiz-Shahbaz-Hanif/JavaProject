package com.DC.pageobjects.legacy.legacyinsights;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class ProductEnhancementPage extends NavigationMenuLegacy {

    private final By PAGE_BODY = By.className("products");

    public ProductEnhancementPage(WebDriver driver) {
        super(driver);
        findElementVisible(PAGE_BODY);
        Assert.assertEquals("Product Enhancement | OneSpace", driver.getTitle());
    }
}
