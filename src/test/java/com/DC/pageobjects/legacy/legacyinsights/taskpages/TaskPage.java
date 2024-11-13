package com.DC.pageobjects.legacy.legacyinsights.taskpages;

import com.DC.pageobjects.legacy.legacyinsights.NavigationMenuLegacy;
import com.DC.pageobjects.legacy.legacyinsights.TasksPage;
import com.DC.utilities.enums.Enums;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaskPage extends NavigationMenuLegacy {

    protected final By WORKFLOW_TASK_UI = By.id( "solutions-workflow-taskui" );
    protected final By DISCARD_TASK_BUTTON = By.xpath( "//span[contains(text(), 'Discard This Task')]" );
    protected final By EXPAND_ALL_POINTER = By.xpath( "//div[@data-qa='DynamicNavSidebar']//div[text()='Expand All']" );
    protected final String PRODUCTS_IN_DYNAMIC_NAV_SIDEBAR_XPATH = "(//div[@data-qa='DynamicNavSidebar']/div[2]/div/div/div[not(@data-qa='IconButton') and not(child::div[@data-qa='ImageWrapper'])]//div[@data-qa='TypeComponent' and not(ancestor::div[@data-qa='Tooltip' or @data-qa='IconButton'])])";
    protected final By DYNAMIC_NAV_SIDEBAR = By.xpath( "//div[@data-qa='DynamicNavSidebar']" );
    protected final By PRODUCT_CHECKBOXES = By.xpath( "//div[@data-qa='DynamicNavSidebar']//input" );
    protected final By PRODUCT_DETAILS_TOGGLE = By.xpath( "//div[@data-qa='ProductInfoDisplay']//div[contains(text(),'Product Images & Details')]" );
    protected final By SUBMIT_BUTTON = By.xpath( "//span[text()='Submit'] | //input[@id='submitButton']" );
    protected final By SUBMIT_CONFIRMATION_MODAL = By.xpath( "//span[text()='Yes, submit'] | //div[@class='ReactModalPortal']//span[text()='Submit']" );
    protected final By SPELL_CHECK_MODAL = By.xpath( "//div[@data-qa='Spellcheck']" );
    protected final By SPELL_CHECK_MODAL_SUBMIT_BUTTON = By.xpath( "//div[@data-qa='Spellcheck']//span[text()='Submit']" );
    protected final By FLAG_THIS_TASK_BUTON = By.xpath( "//div[@data-qa='FlagTaskDropdown']//span[text()='Flag This Task']" );
    protected final By REASON_FOR_FLAG_AREA = By.xpath( "//div[child::div[text()='Reason For Flag']]//textarea" );
    protected final By FLAG_THIS_TASK_SUBMIT_BUTTON = By.xpath( "//div[child::div[text()='Reason For Flag']]//span[text()='Submit']" );
    protected final By FIRST_PRODUCT_DETAILS_IMAGE = By.xpath( "(//div[@data-qa='ImageWrapper'])[1]" );
    protected final By CONTENT_PDF_BUTTON = By.xpath( "//div[@data-qa='IconButton']//span[contains(text(), 'Download PDF')]" );
    protected final By IMAGE_PDF_BUTTON = By.xpath( "//div[@data-qa='DownloadPdfMenu']" );
    protected final By PDF_DROPDOWN_MENU = By.xpath( "//div[@data-qa='DropdownMenu']" );
    protected final By PDF_DROPDOWN_MENU_DOWNLOAD_BUTTON = By.xpath( "//div[@data-qa='DropdownMenu']//div[@data-qa='Button']" );
    protected final By PRODUCTS_SIDEBAR_ARROW = By.xpath( "//div[@data-qa='NavDrawer']//i[contains(@class,'fa-angle') and not(ancestor::div[@data-qa='DynamicNavSidebar'])]" );
    protected final By TASK_UI_DIV = By.xpath( "//div[@data-qa='ActionsBar']/following-sibling::div[1] | //div[i[contains(@class, 'fa-save')]]//ancestor::div[@data-qa='LinkBar']/following-sibling::div[1]" );
    protected final By PRODUCT_DETAILS_IMAGES_TITLE = By.xpath( "//div[@data-qa='LinkBar']//div[contains(text(), 'Images')]" );
    protected final By KEY_INFORMATION_TITLE = By.xpath( "(//div[@data-qa='LinkBar']//div[contains(text(), 'Key Information')])" );
    protected final By VALIDATION_ICON = By.xpath( "//i[@class='fa fa-exclamation-triangle']" );

    public TaskPage(WebDriver driver) throws Exception {
        super(driver);
        findElementVisible(WORKFLOW_TASK_UI, Duration.ofSeconds(85));
        switchFeedbackNotificationsIndicator(Enums.ToggleAction.Hide);
        Assert.assertEquals(driver.getTitle(), "Task | OneSpace");
    }

    public void switchFeedbackNotificationsIndicator( Enums.ToggleAction toggleAction ) throws InterruptedException {
        By indicator = By.xpath( "//li[@id='notifications' and not( contains(@class, 'active')) or contains(@class, 'clicked')]" );
        By notificationsPanel = By.xpath( "//div[@id='notification-panel'][1]" );
        Boolean isNotificationsPanelOpened = isElementVisibleMilliseconds(notificationsPanel);
        if ( isNotificationsPanelOpened && toggleAction == toggleAction.Hide ) {
            click( indicator );
        } else if ( !isNotificationsPanelOpened && toggleAction == toggleAction.Show ) {
            click( indicator );
        }
    }

    public void clickDiscardButton() throws Exception {
        if (isElementEnabled(DISCARD_TASK_BUTTON, Duration.ofSeconds(15))) {
            scrollIntoViewAndClick( DISCARD_TASK_BUTTON );
            wait(15);
            closeSuccessMessage();
            waitForElementToBeInvisible(SUCCESS_MESSAGE, Duration.ofSeconds(20));
        } else {
            throw new Exception( "Discard button is not visible/enabled." );
        }
    }

    public TasksPage discardTask() throws Exception {
        clickDiscardButton();
        waitForElementToBeInvisible(SUCCESS_MESSAGE, Duration.ofSeconds(20));
        return new TasksPage( driver );
    }

    public boolean clickAllProductsAndCheckTaskVisibility() throws InterruptedException {
        expandAllProducts();
        int totalProducts = getTotalProducts();
        for (int index = 1; index <= totalProducts; index++ ) {
            clickOnSpecificProduct( index );
            if (isElementVisible(WORKFLOW_TASK_UI, Duration.ofSeconds(20))) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }
    public void clickAllProducts() throws InterruptedException {
        findElementVisible(DYNAMIC_NAV_SIDEBAR, Duration.ofSeconds(60));
        int totalProducts = getTotalProducts();
        for ( int index = 1; index <= totalProducts; index++ ) {
            clickOnSpecificProduct( index );
        }
    }

    public TasksPage submitTaskAndRefreshTasksTable( boolean isCommitStep ) throws Exception {
        click( SUBMIT_BUTTON );
        if ( isCommitStep ) {
            click( SUBMIT_CONFIRMATION_MODAL );
        }
        submitSpellCheckModalIfOpen();
        if ( !isModalOpen() ) {
            WebDriverWait wait = new WebDriverWait( driver, Duration.ofSeconds(30) );
            wait.withMessage("Task was not submitted");
            return new TasksPage( driver ).refreshTasksTable();
        } else {
            throw new Exception( "Modal was opened" );
        }
    }

    public void clickSubmit() throws InterruptedException {
        click( SUBMIT_BUTTON );
    }

    public boolean doesProductHaveAnErrorIcon( int productIndex ) {
        String exclamationIconXPath = PRODUCTS_IN_DYNAMIC_NAV_SIDEBAR_XPATH + "[" + productIndex + "]//..//following-sibling::div[//@data-qa='IconButton']//i";
        By exclamationIcon = By.xpath( exclamationIconXPath );
        return isElementVisible(exclamationIcon, Duration.ofSeconds(3));
    }

    public boolean isModalOpen() {
        By modal = By.xpath( "//*[contains(@class,'ReactModal__Overlay--after-open')] | //div[@data-qa='Overlay']" );
        return isElementVisible(modal, Duration.ofSeconds(2));
    }

    public void closeModalIfOpened() throws InterruptedException {
        String regularModalCloseButton = "//div[@class='ReactModalPortal']//div[@class='icon-button']";
        String copyPasteModalCloseButton = "//i[@class='fa fa-times' and ancestor::div[@data-qa='Overlay']]";
        By closeModal = By.xpath( regularModalCloseButton + "|" +  copyPasteModalCloseButton );
        if ( isModalOpen() ) {
            click( closeModal );
        }
    }

    public void submitSpellCheckModalIfOpen() throws InterruptedException {
        By SPELL_CHECK_LOADER = By.xpath( "//div[@class='ReactModal__Overlay ReactModal__Overlay--after-open']//i[@class='fa fa-spinner fa-spin']" );
        waitForElementToBeInvisible( SPELL_CHECK_LOADER );
        if ( isSpellCheckModalOpen() ) {
            scrollIntoViewAndClick( SPELL_CHECK_MODAL_SUBMIT_BUTTON );
            waitForElementToBeInvisible( SPELL_CHECK_MODAL );
        }
    }

    public boolean isSpellCheckModalOpen() {
        return isElementVisible(SPELL_CHECK_MODAL, Duration.ofSeconds(5));
    }

    public boolean isProductsSideBarOpen() {
        return isElementVisible( DYNAMIC_NAV_SIDEBAR );
    }

    public void scrollToCenterOfTaskUI() {
        scrollIntoView( TASK_UI_DIV );
    }

    public int getTotalProducts() {
        return getElementCount( By.xpath( PRODUCTS_IN_DYNAMIC_NAV_SIDEBAR_XPATH ) );
    }

    public void openOrCloseProductsSideBar( boolean openSideBar ) throws InterruptedException {
        if ( isProductsSideBarOpen() != openSideBar ) {
            scrollIntoViewAndClick( PRODUCTS_SIDEBAR_ARROW );
        }
    }

    public void expandAllProducts() throws InterruptedException {
        if ( isElementVisible( EXPAND_ALL_POINTER ) ) {
            scrollIntoViewAndClick( EXPAND_ALL_POINTER );
        }
    }

    public void clickOnSpecificProduct( int productIndex ) throws InterruptedException {
        String product = PRODUCTS_IN_DYNAMIC_NAV_SIDEBAR_XPATH + "[" + productIndex + "]";
        openOrCloseProductsSideBar( true );
        click( By.xpath( product ) );
        scrollToCenterOfTaskUI();
    }

    public List<WebElement> getAllProducts() {
        return findElementsPresent( By.xpath( PRODUCTS_IN_DYNAMIC_NAV_SIDEBAR_XPATH ) );
    }

    public List<String> getProductsNames() {
        List<WebElement> products = getAllProducts();
        List<String> productsNames = new ArrayList<String>();
        for ( WebElement product : products ) {
            productsNames.add( product.getText() );
        }
        return productsNames;
    }

    public boolean checkProductDetails( String productName ) throws InterruptedException {
        By product = By.xpath( "//div[text()='" + productName + "']" );
        findElementVisible(product, Duration.ofSeconds(20));
        click( product );
        checkProductDetailsComponentIsExpanded();
        boolean isTitleVisible = isElementVisible(PRODUCT_DETAILS_IMAGES_TITLE, Duration.ofSeconds(20));
        boolean isKeyInfoVisible = isElementVisible(KEY_INFORMATION_TITLE, Duration.ofSeconds(20));
        return isTitleVisible && isKeyInfoVisible;
    }

    private PdfPage clickPdfPage( String type ) throws Exception {
        if ( type.equalsIgnoreCase("content")) {
            findElementVisible(CONTENT_PDF_BUTTON, Duration.ofSeconds(60));
            click( CONTENT_PDF_BUTTON );
        } else if ( type.equalsIgnoreCase("image")) {
            findElementVisible(IMAGE_PDF_BUTTON, Duration.ofSeconds(60));
            click( IMAGE_PDF_BUTTON );
        } else {
            throw new Exception( "Type not recognised" );
        }
        findElementVisible(PDF_DROPDOWN_MENU, Duration.ofSeconds(30));
        findElementVisible(PDF_DROPDOWN_MENU_DOWNLOAD_BUTTON, Duration.ofSeconds(30));
        try {
            scrollIntoViewAndClick( PDF_DROPDOWN_MENU_DOWNLOAD_BUTTON );
        } catch ( Exception e ) {
            executeJavascript( "arguments[0].click();", PDF_DROPDOWN_MENU_DOWNLOAD_BUTTON );
        }
        return new PdfPage( driver );
    }

    public boolean verifyPDFDownLoad( String taskType ) throws Exception {
        return clickPdfPage( taskType ).verifyPdf();
    }

    public void showOrHideFlagThisTaskSection( Enums.ToggleAction action ) throws InterruptedException {
        boolean sectionIsDisplayed = isElementVisibleMilliseconds(REASON_FOR_FLAG_AREA);
        if ( action == Enums.ToggleAction.Hide && sectionIsDisplayed ) {
            click( FLAG_THIS_TASK_BUTON );
            waitForElementToBeInvisible( REASON_FOR_FLAG_AREA );
        } else if ( action == Enums.ToggleAction.Show && !sectionIsDisplayed ) {
            click( FLAG_THIS_TASK_BUTON );
            findElementVisible(REASON_FOR_FLAG_AREA);
        }
    }

    public void flagTask( String reasonForFlag ) throws InterruptedException {
        showOrHideFlagThisTaskSection( Enums.ToggleAction.Show );
        sendKeys( REASON_FOR_FLAG_AREA, reasonForFlag );
        click( FLAG_THIS_TASK_SUBMIT_BUTTON );
    }

    public void clickOnProduct( String productName ) throws InterruptedException {
        openOrCloseProductsSideBar( true );
        By product = By.xpath( "//div[@data-qa='DynamicNavSidebar']//div[text()='" + productName + "']" );
        findElementVisible(product, Duration.ofSeconds(20));
        click( product );
    }

    public boolean checkProductDetailsImage( String productName ) throws InterruptedException {
        clickOnProduct( productName );
        return isElementVisible(FIRST_PRODUCT_DETAILS_IMAGE, Duration.ofSeconds(30));
    }

    public void checkProductDetailsComponentIsExpanded() throws InterruptedException {
        findElementVisible(PRODUCT_DETAILS_TOGGLE, Duration.ofSeconds(30));
        String productDetailsType = getTextFromElement(PRODUCT_DETAILS_TOGGLE);
        if ( Objects.equals(productDetailsType, "View Product Images & Details") ) {
            click( PRODUCT_DETAILS_TOGGLE );
        }
    }

    public boolean checkCorrectNumberOfImagesInProductDetails( String productName, int numberOfExpectedImageThumbnails ) throws InterruptedException {
        int largeImageThumbnailPlusOne = 2;
        int moreThanExpectedImageCount = numberOfExpectedImageThumbnails + largeImageThumbnailPlusOne;
        By imageNotExpectedToBeDisplayed = By.xpath( "(//div[@data-qa='ProductImages']//div[@data-qa='ImageWrapper'])[" + moreThanExpectedImageCount + "]" );
        clickOnProduct( productName );
        checkProductDetailsComponentIsExpanded();
        return isElementVisible(imageNotExpectedToBeDisplayed, Duration.ofSeconds(15));
    }
}
