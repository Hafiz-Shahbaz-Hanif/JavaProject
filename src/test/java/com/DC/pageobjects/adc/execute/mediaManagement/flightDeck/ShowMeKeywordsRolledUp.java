package com.DC.pageobjects.adc.execute.mediaManagement.flightDeck;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ShowMeKeywordsRolledUp extends FlightDeck{

    private By KEYWORDS_ROLLED_UP_DROPDOWN = By.xpath("//app-selectize[@name='showMe']//div[@class='item' and contains(text(),'Keywords/Targets (rolled up)') or text()='Keywords/Targets (rolled up)']]");

    public ShowMeKeywordsRolledUp(WebDriver driver) {
        super(driver);
        findElementVisible(KEYWORDS_ROLLED_UP_DROPDOWN);
    }
}
