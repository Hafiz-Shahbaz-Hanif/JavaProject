package com.DC.pageobjects.legacy.legacyinsights;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class APIAdministrationPage extends NavigationMenuLegacy{
    private final By PAGE_BODY = By.xpath("//div[@data-qa='ApiAdmin']");

    public APIAdministrationPage(WebDriver driver) {
        super(driver);
        findElementVisible(PAGE_BODY);
        Assert.assertEquals("API Administration | OneSpace", driver.getTitle());
    }
}
