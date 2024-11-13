package com.DC.pageobjects.legacy.legacyinsights;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class CategoryBrandsharePage extends NavigationMenuLegacy {

    private final By PAGE_BODY = By.xpath("//div[@data-qa='CategoryBrandshare']");

    public CategoryBrandsharePage(WebDriver driver) {
        super(driver);
        findElementVisible(PAGE_BODY);
        Assert.assertEquals( "Category Brandshare | OneSpace", driver.getTitle() );
    }
}
