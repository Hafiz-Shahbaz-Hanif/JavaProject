package com.DC.pageobjects.legacy.legacyinsights;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class DestinationManagerPage extends NavigationMenuLegacy {

    protected final By PAGE_BUTTON = By.xpath("//div[@data-qa='DestinationManager']");

    public DestinationManagerPage( WebDriver driver ) throws InterruptedException {
        super(driver);
        findElementVisible(PAGE_BUTTON);
        Assert.assertEquals( "Destination Manager | OneSpace", driver.getTitle() );
    }
}
