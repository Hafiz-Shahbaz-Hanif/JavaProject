package com.DC.pageobjects.legacy.legacyinsights;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class PropertyGroupsPage extends NavigationMenuLegacy {

    protected final By PAGE_BODY = By.className("property-groups");

    public PropertyGroupsPage(WebDriver driver) {
        super(driver);
        findElementVisible(PAGE_BODY);
        Assert.assertEquals("Property Groups | OneSpace", driver.getTitle());
    }
}
