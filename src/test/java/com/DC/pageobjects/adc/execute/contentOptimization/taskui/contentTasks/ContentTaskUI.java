package com.DC.pageobjects.adc.execute.contentOptimization.taskui.contentTasks;

import com.DC.pageobjects.adc.execute.contentOptimization.taskui.TaskUIBase;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Includes content create, content revisions and internal review since they are almost the same
 */

public class ContentTaskUI extends TaskUIBase {
    private final By LOAD_STATE = By.xpath("//div[(@data-qa='TypeComponent' and text()='Preparing your task. Please wait.') or (@class='_qlzowm ')]");
    private final By FIELDS_LABEL_LOCATOR = By.xpath("//div[@data-qa='TextInput']//div[@data-qa='InlineList']//div[@data-qa='TypeComponent']");

    public ContentTaskUI(WebDriver driver) throws Exception {
        super(driver);
        waitForTaskToBeDisplayed();
    }

    public void waitForTaskToBeDisplayed() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(80));
        if (isElementVisible(LOAD_STATE, Duration.ofSeconds(5))) {
            wait.until(ExpectedConditions.visibilityOf(findElementVisible(WORKFLOW_TASK_UI)));
        }
        wait.until(ExpectedConditions.elementToBeClickable(SAVE_TASK_BUTTON));
    }

    public void replaceTextOfField( String sectionID, String text ) {
        var requestedInput = By.xpath("//textarea[contains(@id,'" + sectionID + "')]");
        var inputElement = findElementPresent(requestedInput);

        inputElement.sendKeys( Keys.CONTROL + "a" + Keys.BACK_SPACE );
        setText( requestedInput, text + Keys.TAB );
    }

    public String getTextFromField(String sectionID) {
        By requestedInput = By.xpath("//textarea[contains(@id,'" + sectionID + "')]");
        return getTextFromElement(requestedInput);
    }

    public List<String> getFieldIdsDisplayed() {
        var fieldNames = getTextFromElementsMilliseconds(FIELDS_LABEL_LOCATOR);
        return fieldNames.stream().map(fieldName -> fieldName.toLowerCase().replace(" ", "_")).collect(Collectors.toList());
    }
}
