package com.DC.pageobjects.beta.contentHealth;

import com.DC.pageobjects.PageHandler;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import java.util.List;

public class ContentHealthDashboardPage extends PageHandler {

    private final By CURRENT_CONTENT_HEALTH_HEADER = By.xpath("//h3//div[text()='Current Content Health']");
    private final By SIDE_BAR_SELECTIONS_EXPANDED = By.xpath("//div[@class=' overflow-y-auto']//li//div[@class='text-base font-medium text-gray-3-dark dark:text-gray-3-light translate-x-1 overflow-hidden text-ellipsis whitespace-nowrap flex']");
    private final By SIDE_BAR_SELECTIONS_HIDDEN = By.xpath("//div[@class=' overflow-y-auto']//li//div[@class='text-base font-medium text-gray-3-dark dark:text-gray-3-light translate-x-1 overflow-hidden text-ellipsis whitespace-nowrap hidden']");
    private final By SIDE_BAR_BUTTON = By.xpath("//button[@data-cy='ui-button']//span[@data-cy='collapse']");
    private final By CONTENT_CONFIGURATION_BUTTON = By.xpath("//div[text()='Content Health Configuration']");
    private final By SCORES_OVER_TIME_TABS = By.xpath("//div[@data-cy='ui-container']/div[contains(@data-cy, 'UITab')]/div[text()]");
    private final By EDIT_SCORES_BUTTON = By.xpath("//button//div[text()='Edit Score Thresholds']");
    //These xpaths have placeholder texts and will be updated when UI is filled out
    private final By CONTENT_HEALTH_TOP_BANNER_TOOLTIP = By.xpath("(//span[@data-cy='info'])[1]");
    private final By CURRENT_HEALTH_CONTENT_TOOLTIP = By.xpath("(//span[@data-cy='info'])[2]");
    private final By CONTENT_SCORES_OVER_TIME_TOOLTIP = By.xpath("(//span[@data-cy='info'])[3]");
    private final By CONTENT_HEALTH_TOP_BANNER_TOOLTIP_POPUP = By.xpath("//div[@data-cy='ui-tooltip-title' and text()='Content Health Tooltip']");
    private final By CURRENT_HEALTH_CONTENT_TOOLTIP_POPUP = By.xpath("//div[@data-cy='ui-tooltip-title' and text()='Current Content Health']");
    private final By CONTENT_SCORES_OVER_TIME_POPUP = By.xpath("//div[@data-cy='ui-tooltip-title' and text()='Content Score Over Time']");
    private final By CONTENT_SCORES_OVER_TIME_GRAPH = By.xpath("//div[@data-cy='ui-line-graph']");
    private final By CONTENT_SCORES_OVER_TIME_RETAILERS_TABLE  = By.xpath("//div[@class='bg-white shadow-oasis mx-8 my-3 flex flex-col rounded-lg border border-gray-4-light']");

    public ContentHealthDashboardPage(WebDriver driver) {
        super(driver);
        findElementVisible(CURRENT_CONTENT_HEALTH_HEADER);
    }

    public boolean isSideBarExpanded() {
        return isElementPresent(SIDE_BAR_SELECTIONS_EXPANDED);
    }

    public void closeSideBar() throws InterruptedException {
        if (isSideBarExpanded()) {
            click(SIDE_BAR_BUTTON);
            waitForElementToBeInvisible(SIDE_BAR_SELECTIONS_EXPANDED);
        }
    }

    public List<String> getSideBarFilterOptions() {
        return getTextFromElements(By.xpath("//li//a//div"));
    }

    public List<String> getScoresOverTimeTabs() {
        return getTextFromElements(SCORES_OVER_TIME_TABS);
    }

    public boolean hoverOverContentHealthHeaderTooltipAndReturnPopupVisiblity(){
        hoverOverElement(CONTENT_HEALTH_TOP_BANNER_TOOLTIP);
        return isElementPresent(CONTENT_HEALTH_TOP_BANNER_TOOLTIP_POPUP);
    }

    public boolean hoverOverCurrentContentHealthTooltipAndReturnPopupVisiblity(){
        hoverOverElement(CURRENT_HEALTH_CONTENT_TOOLTIP);
        return isElementPresent(CURRENT_HEALTH_CONTENT_TOOLTIP_POPUP);
    }

    public boolean hoverOverContentScoresOverTimeTooltipAndReturnPopupVisiblity(){
        hoverOverElement(CONTENT_SCORES_OVER_TIME_TOOLTIP);
        return isElementPresent(CONTENT_SCORES_OVER_TIME_POPUP);
    }

    public boolean isContentScoresOverTimeGraphVisible() {
        return isElementVisible(CONTENT_SCORES_OVER_TIME_GRAPH);
    }

    public boolean isContentConfigurationButtonVisible() {
        return isElementVisible(CONTENT_CONFIGURATION_BUTTON);
    }

    public boolean isContentScoresOverTimeRetailerTableVisible() {
        return isElementVisible(CONTENT_SCORES_OVER_TIME_RETAILERS_TABLE);
    }

    public boolean isEditScoresButtonVisible() {
        return isElementVisible(EDIT_SCORES_BUTTON);
    }
}
