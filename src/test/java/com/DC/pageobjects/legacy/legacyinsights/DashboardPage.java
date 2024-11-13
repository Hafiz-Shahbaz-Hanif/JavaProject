package com.DC.pageobjects.legacy.legacyinsights;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardPage extends NavigationMenuLegacy {
    protected final By PAGE_BODY = By.className("dashboard");
    private final By KEYWORD_SEARCH_BAR = By.xpath( "//div[@data-qa='KeywordSearch']" );
    private final By PUBLISH_SETS_SECTION = By.xpath( "//div[@data-qa='TypeComponent']//div[text()='Publish Sets']" );
    private final By TASKS_SECTION = By.xpath( "//div[@data-qa='Tasks']" );

    public DashboardPage( WebDriver driver )  {
        super(driver);
        findElementVisible(PAGE_BODY);
    }

    public boolean isPublishSetsSectionVisible() {
        return isElementVisibleMilliseconds( PUBLISH_SETS_SECTION );
    }

    public boolean isKeywordSearchBarVisible() {
        return isElementVisibleMilliseconds( KEYWORD_SEARCH_BAR );
    }

    public boolean isTaskSectionVisible() {
        return isElementVisibleMilliseconds( TASKS_SECTION );
    }
}
