package com.DC.utilities.sharedElements;

import com.DC.pageobjects.PageHandler;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class GenericConfirmationModal extends PageHandler {
    protected final By GENERIC_CONFIRMATION_BUTTON = By.xpath("//div[@data-qa='GenericConfirmation']//span");

    public GenericConfirmationModal( WebDriver driver ) {
        super( driver);
        findElementVisible(GENERIC_CONFIRMATION_BUTTON);
    }

    public void confirm() throws InterruptedException {
        scrollIntoViewAndClick( GENERIC_CONFIRMATION_BUTTON );
    }
}
