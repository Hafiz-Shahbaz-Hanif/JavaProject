package com.DC.pageobjects.legacy.legacyinsights;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class InsightsProductsPage extends NavigationMenuLegacy {

    protected final By TASKS_TABLE = By.xpath("//table[@class='table dx-g-bs4-table']//tbody");

    public InsightsProductsPage( WebDriver driver ) throws Exception {
        super(driver);
        Assert.assertEquals( "Products | OneSpace", driver.getTitle() );
    }
}
