package com.DC.pageobjects.adc.execute.productManager.products.generateLaunchFilePage;

import com.DC.pageobjects.adc.navigationMenus.InsightsNavigationMenu;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class GenerateLaunchFilePage extends InsightsNavigationMenu {
    protected final By PAGE_BODY = By.xpath("//div[@data-qa='GenerateLaunchFile']");
    protected final By CONTINUE_BUTTON = By.xpath("//div[@data-qa='BottomActionBar']//button[text()='Continue']");

    public GenerateLaunchFilePage(WebDriver driver) {
        super(driver);
        findElementVisible(PAGE_BODY);
        hideResourceCenter();
    }
}
