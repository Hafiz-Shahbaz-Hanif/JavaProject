package com.DC.pageobjects.legacy.legacyinsights;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class PropertiesPage extends NavigationMenuLegacy{

    private final By PROPERTIES = By.xpath("//div[@data-qa='Properties']");

    public PropertiesPage(WebDriver driver) {
        super(driver);
        findElementVisible(PROPERTIES);
        Assert.assertEquals("Properties | OneSpace", driver.getTitle());
    }
}
