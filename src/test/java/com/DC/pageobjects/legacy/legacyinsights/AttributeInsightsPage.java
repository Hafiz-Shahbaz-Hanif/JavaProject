package com.DC.pageobjects.legacy.legacyinsights;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class AttributeInsightsPage extends NavigationMenuLegacy{
    protected final By CATEGORY_TREE = By.xpath( "//div[@data-qa='CategoryTree']" );

    public AttributeInsightsPage(WebDriver driver) {
        super( driver);
        findElementVisible(CATEGORY_TREE);
        Assert.assertEquals( "Attribute Insights | OneSpace", driver.getTitle() );
    }
}
