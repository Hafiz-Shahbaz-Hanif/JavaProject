package com.DC.pageobjects.adc.identify.searchInsights;

import com.DC.pageobjects.adc.navigationMenus.InsightsNavigationMenu;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AttributeInsightsPage extends InsightsNavigationMenu {

    public static final By VIEW_CATEGORY_BUTTON = By.xpath("//button[text()='View Category']");

    public AttributeInsightsPage(WebDriver driver) {
        super(driver);
    }
}
