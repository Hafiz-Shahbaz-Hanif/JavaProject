package com.DC.pageobjects.adc.execute.contentOptimization.taskui;

import com.DC.constants.ProductVersioningConstants;
import com.DC.pageobjects.adc.execute.contentOptimization.TasksPage;
import com.DC.pageobjects.adc.navigationMenus.InsightsNavigationMenu;
import com.DC.utilities.enums.Enums;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.time.Duration;

public class TaskUIBase extends InsightsNavigationMenu {
    // protected final String WRAPPER_XPATH = "//div[@class='data-task-wrapper']";
    // protected final By DYNAMIC_NAV_SIDEBAR = By.xpath(WRAPPER_XPATH + "//div[@data-qa='DynamicNavSidebar']");
    // protected final By PRODUCTS_SIDEBAR_ARROW = By.xpath("//div[@data-qa='NavDrawer']//i[contains(@class,'fa-angle') and not(ancestor::div[@data-qa='DynamicNavSidebar'])]");
    // protected final By TASK_UI_DIV = By.xpath("//div[@data-qa='NavDrawer']/../..");
    // protected final By EXPAND_ALL_POINTER = By.xpath("//div[@data-qa='DynamicNavSidebar']//div[text()='Expand All']");
    // protected final By EXPAND_ALL_POINTER = By.xpath("//div[@data-qa='DynamicNavSidebar']//div[text()='Expand All']");
    protected final By WORKFLOW_TASK_UI = By.id("solutions-workflow-taskui");
    protected final By DISCARD_TASK_BUTTON = By.xpath("//button[contains(text(), 'Discard This Task')]");
    protected final By SUBMIT_BUTTON = By.xpath("//span[text()='Submit'] | //input[@id='submitButton']");
    protected final By SUBMIT_CONFIRMATION_MODAL = By.xpath("//span[text()='Yes, submit'] | //div[@class='ReactModalPortal']//span[text()='Submit']");
    protected final By SPELL_CHECK_MODAL = By.xpath("//div[@data-qa='Spellcheck']");
    protected final By SPELL_CHECK_MODAL_SUBMIT_BUTTON = By.xpath("//div[@data-qa='Spellcheck']//span[text()='Submit']");
    protected final By GENERAL_FEEDBACK_TEXTAREA = By.xpath("//div[child::div[text()='Feedback / Comments']]//textarea");
    protected final By SAVE_TASK_BUTTON = By.xpath("//div[@data-qa='ActionsBar']//div[text()='Save'] | //div[@data-qa='LinkBar']//span[text()='Save']");
    protected final By MODAL = By.xpath("//*[contains(@class,'ReactModal__Overlay--after-open')] | //div[@data-qa='Overlay']");
    protected final By TASK_UI_DIV = By.xpath("//div[@data-qa='NavDrawer']/../..");
    protected final String PRODUCTS_IN_SIDEBAR_XPATH = "//div[@class='data-task-wrapper']//div[@data-qa='DynamicNavSidebar']/div[2]/div/div/div[descendant::div[@data-qa='ImageWrapper' or @data-qa='Icon']]";

    public TaskUIBase(WebDriver driver) throws Exception {
        super(driver);
        findElementVisible(WORKFLOW_TASK_UI, Duration.ofSeconds(85));
        switchFeedbackNotificationsIndicator(Enums.ToggleAction.Hide);
        Assert.assertEquals(driver.getTitle(), "Task | Flywheel");
    }

    public void switchFeedbackNotificationsIndicator(Enums.ToggleAction toggleAction) throws InterruptedException {
        By bellIconLocator = By.xpath("//li[@id='notifications' and not( contains(@class, 'active') or contains(@class, 'clicked'))]");
        By notificationsPanel = By.xpath("//div[@class='clicked']//div[@id='notification-alerts']");
        var isNotificationsPanelOpened = isElementVisibleMilliseconds(notificationsPanel);
        if ((isNotificationsPanelOpened && toggleAction == Enums.ToggleAction.Hide) || (!isNotificationsPanelOpened && toggleAction == Enums.ToggleAction.Show)) {
            click(bellIconLocator);
        }
    }

    public void clickDiscardButton() throws Exception {
        if (isElementEnabled(DISCARD_TASK_BUTTON, Duration.ofSeconds(15))) {
            moveToElementAndClick(DISCARD_TASK_BUTTON);
            wait(15);
            closeNoteIfDisplayed(Enums.NoteType.SUCCESS);
        } else {
            throw new Exception("Discard button is not visible/enabled.");
        }
    }

    public TasksPage discardTask() throws Exception {
        clickDiscardButton();
        var noteLocator = By.xpath(getNoteXPath(Enums.NoteType.SUCCESS));
        waitForElementToBeInvisible(noteLocator, Duration.ofSeconds(20));
        return new TasksPage(driver);
    }

    public TasksPage submitTaskAndRefreshTasksTable(boolean isCommitStep) throws Exception {
        click(SUBMIT_BUTTON);
        if (isCommitStep) {
            click(SUBMIT_CONFIRMATION_MODAL);
        }
        submitSpellCheckModalIfOpen();
        if (isModalOpen()) {
            waitForModalToClose();
        }
        return new TasksPage(driver).refreshTasks();
    }

    public String getAssignmentName() {
        var assignmentNameLocator = By.className("projectTitle");
        return getTextFromElement(assignmentNameLocator);
    }

    public void saveTask() throws InterruptedException {
        click(SAVE_TASK_BUTTON);
    }

    public void clickSubmit() throws InterruptedException {
        click(SUBMIT_BUTTON);
    }

    public boolean isModalOpen() {
        return isElementVisible(MODAL, Duration.ofSeconds(2));
    }

    public void waitForModalToClose() {
        waitForElementToBeInvisible(MODAL, Duration.ofSeconds(30));
    }

    public void submitSpellCheckModalIfOpen() {
        By SPELL_CHECK_LOADER = By.xpath("//div[@class='ReactModal__Overlay ReactModal__Overlay--after-open']//i[@class='fa fa-spinner fa-spin']");
        waitForElementToBeInvisible(SPELL_CHECK_LOADER, Duration.ofSeconds(30));
        if (isSpellCheckModalOpen()) {
            moveToElementAndClick(SPELL_CHECK_MODAL_SUBMIT_BUTTON);
            waitForElementToBeInvisible(SPELL_CHECK_MODAL);
        }
    }

    public boolean isSpellCheckModalOpen() {
        return isElementVisible(SPELL_CHECK_MODAL, Duration.ofSeconds(5));
    }

    public void insertGeneralFeedback(String feedback) {
        setText(GENERAL_FEEDBACK_TEXTAREA, feedback);
    }

    public void selectQualityEvaluation(int stars) {
        By starLocator = By.xpath("//div[@data-qa='QualityEvaluation']//input[@id='" + stars + "']");
        clickElement(starLocator);
    }

    public void checkIfDecisionRadioButtonIsSelected(By radioButton) throws Exception {
        if (!isElementSelected(radioButton)) {
            throw new Exception("Radio button was not selected");
        }
    }

    public void clickApprove() {
        var approveReviewButton = By.xpath("//*[@id='Approve'] | //*[@id='0']");
        try {
            moveToElementAndClick(approveReviewButton);
            checkIfDecisionRadioButtonIsSelected(approveReviewButton);
        } catch (Exception e) {
            sendKeys(approveReviewButton, String.valueOf(Keys.END));
            clickElement(approveReviewButton);
        }
    }

    protected void clickReject() {
        var rejectReviewButton = By.xpath("//div[@data-qa='GeneralDecision']//input[@id='Rework'] | //div[@data-qa='DecisionModule']//input[@id='1']");
        try {
            moveToElementAndClick(rejectReviewButton);
            checkIfDecisionRadioButtonIsSelected(rejectReviewButton);
        } catch (Exception e) {
            sendKeys(rejectReviewButton, String.valueOf(Keys.END));
            clickElement(rejectReviewButton);
        }
    }

    public void approveOrRejectReviewTask(ProductVersioningConstants.ReviewVerdict decision) {
        if (decision == ProductVersioningConstants.ReviewVerdict.APPROVE) {
            clickApprove();
        } else {
            clickReject();
        }
    }

    public TasksPage approveOrRejectClientReviewAndSubmit(ProductVersioningConstants.ReviewVerdict decision, Boolean isCommitStep) throws Exception {
        approveOrRejectReviewTask(decision);
        return submitTaskAndRefreshTasksTable(isCommitStep);
    }

    public void selectContentQualityRubricBox() {
        var qualityRubricCheckbox = By.id("quality-rubric-checkbox");
        var checkboxIsVisible = isElementVisibleMilliseconds(qualityRubricCheckbox);
        if (checkboxIsVisible) {
            clickElement(qualityRubricCheckbox);
        }
    }

    public void approveOrRejectInternalReview(ProductVersioningConstants.ReviewVerdict decision, int stars) {
        approveOrRejectReviewTask(decision);
        selectQualityEvaluation(stars);
    }

    public TasksPage approveOrRejectInternalReviewAndSubmit(ProductVersioningConstants.ReviewVerdict decision, int stars) throws Exception {
        selectQualityEvaluation(stars);
        approveOrRejectInternalReview(decision, stars);
        selectContentQualityRubricBox();
        return submitTaskAndRefreshTasksTable(false);
    }

    public void clickOnSpecificProduct(int productIndex) {
        var productXPath = String.format(PRODUCTS_IN_SIDEBAR_XPATH + "[%s]", productIndex);
        clickElement(By.xpath(productXPath));
        moveToElement(TASK_UI_DIV);
    }
}
