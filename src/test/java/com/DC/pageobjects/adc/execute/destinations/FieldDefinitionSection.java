package com.DC.pageobjects.adc.execute.destinations;

import com.DC.pageobjects.adc.navigationMenus.InsightsNavigationMenu;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class FieldDefinitionSection extends InsightsNavigationMenu {

    private final By FIELD_DEFINITION_BODY = By.xpath("//div[@data-qa='FieldDefinition']");
    private final By DESTINATION_SETUP_PAGE_BREADCRUMB = By.xpath("//a[@href='/insights/destination-setup']");


    public FieldDefinitionSection(WebDriver driver) {
        super(driver);
        findElementVisible(FIELD_DEFINITION_BODY);
    }

    public DestinationSetUpPage returnToDestinationSetUpPage() throws InterruptedException {
        click(DESTINATION_SETUP_PAGE_BREADCRUMB);
        return new DestinationSetUpPage(driver);
    }
}
