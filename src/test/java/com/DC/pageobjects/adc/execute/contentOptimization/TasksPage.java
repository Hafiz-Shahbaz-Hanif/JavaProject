package com.DC.pageobjects.adc.execute.contentOptimization;

import com.DC.pageobjects.adc.navigationMenus.InsightsNavigationMenu;
import com.DC.utilities.enums.Enums;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;

public class TasksPage extends InsightsNavigationMenu {
    private final String HEADER_XPATH = "//div[contains(@class,'MuiCardHeader')]";
    private final By TASKS_TABLE_LOCATOR = By.xpath("//table[@class='table dx-g-bs4-table']//tbody");
    private final By DEFAULT_LOADER = By.xpath( "//div[@data-qa='LoadState']" );
    private final By TASKS_CONTAINER = By.xpath( "//div[@class='table-responsive dx-g-bs4-table-container']" );
    private final By LOAD_STATE = By.xpath( "//div[@data-qa='LoadState']" );
    private final By SEARCH_INPUT_LOCATOR = By.xpath( HEADER_XPATH + "//input" );
    private final By REFRESH_TASKS_BUTTON_LOCATOR = By.xpath( HEADER_XPATH + "//button[text()='Refresh Tasks']" );

    public TasksPage(WebDriver driver) {
        super(driver);
        findElementVisible(TASKS_TABLE_LOCATOR, Duration.ofMinutes(1));
    }

    public TasksPage searchTask(String taskTitle) {
        setTextAndHitEnter(SEARCH_INPUT_LOCATOR, taskTitle);
        waitForDOMStabilization();
        return new TasksPage(driver);
    }

    public <T> T clickTaskTitle(String taskTitle,  Class<T> taskClass ) throws Exception {
        var taskTitleLink = scrollUntilTaskTitleIsDisplayed(taskTitle);
        taskTitleLink.click();
        getWait(Duration.ofSeconds(30)).until(ExpectedConditions.titleIs("Task | Flywheel"));
        return getPage(taskClass);
    }

    public WebElement scrollUntilTaskTitleIsDisplayed(String taskTitle) throws Exception {
        int maxAttempts = 3;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            waitForElementToBeInvisible(DEFAULT_LOADER);
            scrollMainBarToCenterAndInnerBarToTop(TASKS_CONTAINER);
            double currentPosition;
            double pixelsToScroll = getPixelsToScroll( TASKS_CONTAINER, maxAttempts );
            do {
                currentPosition = getVerticalScrollPosition(TASKS_CONTAINER);
                var taskTitleLocator = By.xpath("//td[3]//div[text()='" + taskTitle + "']");
                if (isElementPresent(taskTitleLocator)) {
                    moveToElement(taskTitleLocator);
                    return findElementPresent(taskTitleLocator);
                }
                scrollElementVertically(pixelsToScroll, TASKS_CONTAINER);
            } while (currentPosition != getVerticalScrollPosition(TASKS_CONTAINER));
            if ( attempt < maxAttempts ) {
                clickElement(REFRESH_TASKS_BUTTON_LOCATOR);
                new TasksPage(driver);
            }
        }
        throw new Exception("Task with title '" + taskTitle + "' was not found");
    }

    public TasksPage refreshTasks() {
        clickElement(REFRESH_TASKS_BUTTON_LOCATOR);
        return new TasksPage(driver);
    }


}
