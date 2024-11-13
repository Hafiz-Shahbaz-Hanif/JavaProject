package com.DC.pageobjects.legacy.legacyinsights;

import com.DC.objects.insightslegacy.AssignmentDetails;
import com.DC.objects.insightslegacy.LaunchFileSettings;
import com.DC.objects.insightslegacy.ProductBasicSettings;
import com.DC.pageobjects.legacy.legacyinsights.taskpages.*;
import com.DC.utilities.enums.Enums;
import com.DC.utilities.sharedElements.TaskLaunchFileSettings;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TasksPage extends NavigationMenuLegacy {

    protected final By TASKS_TABLE = By.xpath("//table[@class='table dx-g-bs4-table']//tbody");
    protected final By DEFAULT_LOADER = By.xpath( "//div[@data-qa='LoadState']" );
    private final By TASKS_CONTAINER = By.xpath( "//div[@class='table-responsive dx-g-bs4-table-container']" );
    private final By REFRESH_TASKS_ICON = By.xpath( "//i[@class='fa fa-sync-alt']" );
    private final By MORE_ACTIONS_DROPDOWN = By.xpath( "//div[@data-qa='ActionsDropdown']" );
    private final By MORE_ACTIONS_DROPDOWN_MENU = By.xpath( "//div[@data-qa='ActionsDropdown']//div[@data-qa='DropdownMenu']" );
    private final By REFRESH_TASKS_IN_MORE_ACTIONS = By.xpath( "//div[@data-qa='ActionsDropdown']//div[@data-qa='TypeComponent' and text()='Refresh Tasks']" );
    private final By STATUS_FILTER = By.xpath( "(//div[@data-qa='HeaderCellComponent'])[1]" );
    private final By LOAD_STATE = By.xpath( "//div[@data-qa='LoadState']" );

    Map<Enums.TaskStatus, String> taskStatusMap = new HashMap<Enums.TaskStatus, String>() {{
        put(Enums.TaskStatus.Available, "Available");
        put(Enums.TaskStatus.InProgress, "In Progress");
        put(Enums.TaskStatus.MyTask, "My Task");
        put(Enums.TaskStatus.Revision, "Revision");
    }};

    public TasksPage( WebDriver driver ) throws Exception {
        super(driver);
        Assert.assertEquals( "Tasks | OneSpace", driver.getTitle() );
        try {
            try {
                findElementVisible(TASKS_TABLE, Duration.ofSeconds(59));
            } catch (Exception err) {
                System.out.println("ERROR IN INNER EXCEPTION: " + err);
                refreshPage();
                findElementVisible(TASKS_TABLE, Duration.ofSeconds(60));
            }
        } catch (Exception err) {
            throw new Exception("Task table failed to load.\n " + err);
        }
    }

    public TasksPage changeTasksPerPage( String number ) throws Exception {
        By dropdown = By.xpath( "//div[@data-qa='ActionBar']//div[@data-qa='IconDropdown']" );
        selectItemFromDropdown( dropdown, number );
        return new TasksPage( driver );
    }

    public TaskPage claimRequiredAssignmentIfExists(AssignmentDetails details) throws Exception {
        WebElement taskLinkElement = lookForRequiredAssignment( details.assignmentName, details.requiredStatus );
        return clickTaskTitle( taskLinkElement, details );
    }

    public WebElement lookForRequiredAssignment(String assignmentName, Enums.TaskStatus status) throws InterruptedException {
        int maxAttempts = 3;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            waitForElementToBeInvisible(DEFAULT_LOADER);
            scrollMainBarToCenterAndInnerBarToTop(TABLE_CONTAINER);
            double currentPosition;
            double pixelsToScroll = getPixelsToScroll( TABLE, maxAttempts );
            do {
                currentPosition = getVerticalScrollPosition(TABLE_CONTAINER);
                WebElement tasksTable = findElementPresent(TASKS_TABLE);
                List<WebElement> rowsList = findElementsVisible(By.xpath("//table[@class='table dx-g-bs4-table']//tbody//tr"));
                for (WebElement row : rowsList) {
                    List<WebElement> cellList = row.findElements(By.tagName("td"));
                    boolean availableTask = cellList.get(4).getText().equals(assignmentName) && cellList.get(1).getText().equals(taskStatusMap.get(status));
                    if (availableTask) {
                        scrollCellToCenterOfView(cellList.get(2));
                        return getActualTaskTitleLinkElementToClick(cellList.get(2));
                    }
                }
                    scrollElementVertically(pixelsToScroll, TASKS_CONTAINER);
                } while (currentPosition != getVerticalScrollPosition(TABLE_CONTAINER));
                refreshTasksIfNecessary(attempt, maxAttempts);
            }
        return null;
        }

    public List<String> getAvailableRequiredAssignments(String assignmentName, Enums.TaskStatus status) throws InterruptedException {
        int maxAttempts = 3;
        List<String> requiredTasksAvailable = new ArrayList<>();
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            waitForElementToBeInvisible(DEFAULT_LOADER);
            scrollMainBarToCenterAndInnerBarToTop(TABLE_CONTAINER);
            double currentPosition;
            double pixelsToScroll = getPixelsToScroll( TABLE, maxAttempts );
            do {
                currentPosition = getVerticalScrollPosition(TABLE_CONTAINER);
                List<WebElement> rowsList = findElementsVisible(By.xpath("//table[@class='table dx-g-bs4-table']//tbody//tr"));
                for (WebElement row : rowsList) {
                    List<WebElement> cellList = row.findElements(By.tagName("td"));
                    if (!requiredTasksAvailable.contains(cellList.get(2).getText()) &&  cellList.get(1).getText().equals(taskStatusMap.get(status)) && cellList.get(4).getText().equals(assignmentName)) {
                        requiredTasksAvailable.add(cellList.get(2).getText());
                    }
                }
                scrollElementVertically(pixelsToScroll, TASKS_CONTAINER);
            } while (currentPosition != getVerticalScrollPosition(TABLE_CONTAINER));
        }
        return requiredTasksAvailable;
    }

    public TaskPage clickTaskTitle( WebElement taskLinkElement, AssignmentDetails details ) throws Exception {
        if ( taskLinkElement != null ) {
            claimTaskAndWaitForPageToChange( taskLinkElement, details.assignmentName, details.requiredStatus );
            switch ( details.taskType ) {
                case Content:
                    return new ContentTaskPage( driver );
                case Image:
                    return new ImageTaskPage( driver );
                case Attribute:
                    return new AttributeTaggingTaskUIPage( driver, details.jwt, details.expectedProducts );
                case Keyword:
                    return new KeywordResearchTaskUIPage( driver );
                case ContentCollabReview:
                    return new CollabReviewTaskPage( driver );
                case Rpc:
                    return new RPCDiscoveryTaskUIPage( driver );
            }
        }
        throw new Exception( "Unable to find assignment: {details.AssignmentName} with the required conditions." );
    }

    public void claimTaskAndWaitForPageToChange( WebElement taskLinkElement, String assignmentName, Enums.TaskStatus status ) throws Exception {
        moveToElement( taskLinkElement );
        taskLinkElement.click();
        try {
            getWait().until( ExpectedConditions.titleIs( "Task | OneSpace" ) );
        } catch ( Exception err ) {
            clickDashboard();
            clickTasks();
            waitForTasksTableToBeDisplayed();
            WebElement newTaskLinkElement = lookForRequiredAssignment( assignmentName, Enums.TaskStatus.MyTask );
            if ( newTaskLinkElement == null ) {
                WebElement newUnclaimedTaskElement = lookForRequiredAssignment( assignmentName, status );
                moveToElement( newUnclaimedTaskElement );
                newUnclaimedTaskElement.click();
                getWait().until( ExpectedConditions.titleIs( "Task | OneSpace" ) );
            } else {
                newTaskLinkElement.click();
                getWait().until( ExpectedConditions.titleIs( "Task | OneSpace" ) );
            }
        }
    }

    public void openMoreActionsDropdownIfNeeded() throws InterruptedException {
        Boolean moreActionsIsDisplayed = isElementVisible( MORE_ACTIONS_DROPDOWN );
        Boolean moreActionsDropdownMenuDisplayed = isElementVisible( MORE_ACTIONS_DROPDOWN_MENU );
        if ( moreActionsIsDisplayed ) {
            if ( !moreActionsDropdownMenuDisplayed ) {
                click( MORE_ACTIONS_DROPDOWN );
                moreActionsDropdownMenuDisplayed = isElementVisible( MORE_ACTIONS_DROPDOWN_MENU );
                if ( !moreActionsDropdownMenuDisplayed ) {
                    click( MORE_ACTIONS_DROPDOWN );
                    findElementVisible(MORE_ACTIONS_DROPDOWN_MENU);
                }
            }
        }
    }

    public void refreshTasks() throws InterruptedException {
        waitForElementToBeInvisible(DEFAULT_LOADER);
        openMoreActionsDropdownIfNeeded();
        if ( isElementVisible(MORE_ACTIONS_DROPDOWN) ) {
            click( REFRESH_TASKS_IN_MORE_ACTIONS );
        } else {
            click( REFRESH_TASKS_ICON );
        }
        waitForTasksTableToBeDisplayed();
    }

    public TasksPage refreshTasksTable() throws Exception {
        if ( isElementVisible( MORE_ACTIONS_DROPDOWN ) ) {
            click( MORE_ACTIONS_DROPDOWN );
            click( REFRESH_TASKS_IN_MORE_ACTIONS );
        } else {
            click( REFRESH_TASKS_ICON );
        }
        return new TasksPage( driver );
    }

    private void refreshTasksIfNecessary( int attempt, int maxAttemptsAllowed ) throws InterruptedException {
        if ( attempt < maxAttemptsAllowed ) {
            refreshTasks();
        }
    }

    public void waitForTasksTableToBeDisplayed() {
        if (isElementVisible(LOAD_STATE, Duration.ofSeconds(3))) {
            getWait().until( ExpectedConditions.visibilityOfElementLocated( TASKS_TABLE ) );
        }
        getWait().until( ExpectedConditions.elementToBeClickable( STATUS_FILTER ) );
    }

    public void scrollCellToCenterOfView( WebElement element ) {
        String scrollToMiddle = "var tasksContainerHeight = arguments[0].clientHeight;" +
                "var elementTop = arguments[1].getBoundingClientRect().top;" +
                "arguments[0].scrollBy(0, elementTop-(tasksContainerHeight/2));";
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript( scrollToMiddle, findElementVisible( TASKS_CONTAINER ), element );
    }

    public void switchFeedbackNotificationsIndicator( Enums.ToggleAction toggleAction ) throws InterruptedException {
        By indicator = By.xpath( "//li[@id='notifications' and not( contains(@class, 'active')) or contains(@class, 'clicked')]" );
        By notificationsPanel = By.xpath( "//div[@id='notification-panel'][1]" );
        Boolean isNotificationsPanelOpened = isElementVisible( notificationsPanel );
        if ( isNotificationsPanelOpened && toggleAction == Enums.ToggleAction.Hide ) {
            click( indicator );
        } else if ( !isNotificationsPanelOpened && toggleAction == Enums.ToggleAction.Show ) {
            click( indicator );
        }
    }

    private WebElement getActualTaskTitleLinkElementToClick( WebElement webElement ) {
        return webElement.findElement( By.xpath( ".//div[ancestor::div[@data-qa='InlineList']]" ) );
    }

    public void releaseMoreTasksIfNeeded(String assignmentName, LaunchFileSettings launchFileSettings, int tasksRequired) throws Exception {
        List<String> requiredTaskAvailable = getAvailableRequiredAssignments(assignmentName, Enums.TaskStatus.Available);
        if (tasksRequired > requiredTaskAvailable.size()) {
            releaseTasksFromCPG(launchFileSettings);
        }
    }

    public TasksPage releaseTasksFromCPG(LaunchFileSettings launchFileSettings) throws Exception {
        List<ProductBasicSettings> list = new ArrayList<>();
        list.add( launchFileSettings.formatBatchConfig.product );

        CreateBatchSection generateLaunchPage = clickProducts()
                .searchProduct( launchFileSettings.formatBatchConfig.product.uniqueID, launchFileSettings.formatBatchConfig.product.productLevel )
                .startProcessToGenerateLaunchFile( list, launchFileSettings.formatBatchConfig.product.productLevel );

        generateLaunchPage.enterNameOfBatch( launchFileSettings.batchName );
        generateLaunchPage.enterNumberOfTasks( launchFileSettings.numberOfTasks );
        generateLaunchPage.selectChain( launchFileSettings.chainName );
        FormatBatchSection formatBatchSection = generateLaunchPage.clickContinue();
        formatBatchSection.configureFormatBatch( launchFileSettings.formatBatchConfig );
        ConfigureTaskAndExportSection configureTaskAndExportSection = formatBatchSection.clickContinue();
        for (int i = 1; i <= launchFileSettings.numberOfTasks; i++) {
            configureAndLaunchTask(configureTaskAndExportSection, launchFileSettings);
        }
        return configureTaskAndExportSection.clickTasks().changeTasksPerPage( "100" );
    }

    public void configureAndLaunchTask(ConfigureTaskAndExportSection configureTaskAndExportSection, LaunchFileSettings launchFileSettings) throws InterruptedException {
        configureTaskAndExportSection.clickOnTask( launchFileSettings.formatBatchConfig.taskNumber );
        configureTaskAndExportSection.selectAnAssociatedProduct( launchFileSettings.formatBatchConfig.product.uniqueID );
        configureTaskAndExportSection.emptyOutAllFields( launchFileSettings.fieldIdsAndLabels.keySet() );
        launchFileSettings.fieldIdsAndLabels.replace("TaskTitle", launchFileSettings.fieldIdsAndLabels.get("TaskTitle") + RandomStringUtils.randomAlphabetic(4).toUpperCase());
        configureTaskAndExportSection.fillOutAllFields( launchFileSettings.fieldIdsAndLabels);
        configureTaskAndExportSection.launchBatch();
        configureTaskAndExportSection.waitForSpinnerInvisibility();
        configureTaskAndExportSection.waitForNotesInvisibility();
    }

    public LaunchFileSettings getLaunchFileSettings(Enums.TaskType taskType) {
        switch(taskType) {
            case Content:
                return TaskLaunchFileSettings.getContentCreateLaunchFileSettings();
            case Image:
                return TaskLaunchFileSettings.getImageCreateLaunchFileSettings();
            case Attribute:
                return TaskLaunchFileSettings.getAttributeTaggingLaunchFileSettings();
            case Keyword:
                return TaskLaunchFileSettings.getKeywordResearchLaunchFileSettings();
            case ContentCollabReview:
                return TaskLaunchFileSettings.getCollabReviewLaunchFileSettings();
            case Rpc:
                return TaskLaunchFileSettings.getRPCDiscoveryLaunchFileSettings();
        }
        return null;
    }

    }
