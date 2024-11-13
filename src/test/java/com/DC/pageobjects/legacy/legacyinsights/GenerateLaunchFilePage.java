package com.DC.pageobjects.legacy.legacyinsights;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class GenerateLaunchFilePage extends NavigationMenuLegacy {
    protected final By PAGE_BODY = By.xpath("//div[@data-qa='GenerateLaunchFile']");
    protected final By CONTINUE_BUTTON = By.xpath( "//div[@data-qa='BottomActionBar']//span[text()='Continue']" );
    protected final By BACK_TO_PREVIOUS_STEP_BUTTON = By.xpath( "//div[@data-qa='BottomActionBar']//div[text()='Back to Previous Step']" );

    public GenerateLaunchFilePage(WebDriver driver) throws Exception {
        super(driver);
        findElementVisible(PAGE_BODY);
    }

    protected int getPositionOfColumnInTable( String columTitle ) {
        By precedingColumns = By.xpath( "//table//thead//th[descendant::span[text()='" + columTitle + "']]//preceding-sibling::th" );
        return getElementCount( precedingColumns ) + 1;
    }
}
