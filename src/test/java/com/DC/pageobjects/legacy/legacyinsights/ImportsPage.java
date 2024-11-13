package com.DC.pageobjects.legacy.legacyinsights;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class ImportsPage extends NavigationMenuLegacy{

    private final By PAGE_BODY = By.className("imports");

    public ImportsPage(WebDriver driver) {
        super(driver);
        findElementVisible(PAGE_BODY);
        Assert.assertEquals("Imports | OneSpace", driver.getTitle());
    }
}
