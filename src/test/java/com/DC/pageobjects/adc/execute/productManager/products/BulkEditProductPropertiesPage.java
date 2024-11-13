package com.DC.pageobjects.adc.execute.productManager.products;

import org.openqa.selenium.WebDriver;

public class BulkEditProductPropertiesPage extends BulkEditTableBase {
    public BulkEditProductPropertiesPage(WebDriver driver) {
        super(driver);
    }

    public BulkEditProductPropertiesPage searchForPropertyValues(String propertyValue) {
        setTextAndHitEnter(SEARCH_INPUT_LOCATOR, propertyValue);
        return new BulkEditProductPropertiesPage(driver);
    }
}
