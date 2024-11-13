package com.DC.pageobjects.legacy.legacyinsights;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class RequestsPage extends NavigationMenuLegacy {

    private final By PAGE_BODY = By.xpath("//div[@data-qa='MyRequests']");

    public RequestsPage(WebDriver driver) {
        super(driver);
        findElementVisible(PAGE_BODY);
        Assert.assertEquals("Requests | OneSpace", driver.getTitle());
    }
}
