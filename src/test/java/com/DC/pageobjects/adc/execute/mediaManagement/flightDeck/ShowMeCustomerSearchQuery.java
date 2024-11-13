package com.DC.pageobjects.adc.execute.mediaManagement.flightDeck;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ShowMeCustomerSearchQuery extends FlightDeck{

    private By CSQ_DROPDOWN =   By.xpath("//app-selectize[@name='showMe']//div[@class='item' and text()='Customer Search Query']");

    public ShowMeCustomerSearchQuery(WebDriver driver) {
        super(driver);
        findElementVisible(CSQ_DROPDOWN);
    }
}
