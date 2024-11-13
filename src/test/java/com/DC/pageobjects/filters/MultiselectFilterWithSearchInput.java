package com.DC.pageobjects.filters;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MultiselectFilterWithSearchInput extends MultiselectFilter {
    protected final By SEARCH_INPUT = By.xpath(MENU_XPATH + "//input[@type='text']");

    public MultiselectFilterWithSearchInput(WebDriver driver) {
        super(driver);
    }

    public boolean isChipDisplayed(String label) {
        By chipLocator = By.xpath("//div[contains(@class,'MuiPaper-menu')]//span[contains(@class,'label') and text()='" + label + "']");
        return isElementVisibleMilliseconds(chipLocator);
    }

    public void searchForOption(String option) {
        setTextAndHitEnter(SEARCH_INPUT, option);
    }
}
