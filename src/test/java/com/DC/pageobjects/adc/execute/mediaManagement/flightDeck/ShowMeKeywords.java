package com.DC.pageobjects.adc.execute.mediaManagement.flightDeck;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ShowMeKeywords extends FlightDeck{

    private By KEYWORDS_DROPDOWN = By.xpath("//app-selectize[@name='showMe']//div[@class='item' and contains(text(),'Keywords/Targets (by campaign)') or text()='Keywords/Targets (by campaign')]]");

    public ShowMeKeywords(WebDriver driver) {
        super(driver);
        findElementVisible(KEYWORDS_DROPDOWN);
    }
}
