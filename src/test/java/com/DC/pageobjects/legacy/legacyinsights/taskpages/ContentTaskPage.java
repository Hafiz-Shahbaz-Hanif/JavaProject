package com.DC.pageobjects.legacy.legacyinsights.taskpages;

import com.DC.utilities.apiEngine.apiServices.insights.ProductService;
import com.DC.utilities.apiEngine.models.responses.insights.TaskUIConfig;
import com.DC.utilities.enums.Enums;
import com.DC.utilities.sharedElements.SpellCheckModal;
import com.google.common.base.Splitter;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ContentTaskPage extends TaskPage {
    protected final By SAVE_TASK_BUTTON = By.xpath( "//div[@data-qa='ActionsBar']//div[text()='Save'] | //div[@data-qa='LinkBar']//span[text()='Save']" );
    protected final By LOAD_STATE = By.xpath( "//div[(@data-qa='TypeComponent' and text()='Preparing your task. Please wait.') or (@class='_qlzowm ')]" );
    protected final By ALL_INPUTS = By.xpath("//div[@data-qa='TextInput']//textarea[not(contains(@id, 'product_description'))]");
    protected final By PRODUCT_DESCRIPTION_AREA = By.id( "product_description" );
    private final By TITLE_KEYWORDS = By.xpath( "(//div[@data-qa='InlineAccordion'][1]/div[2]//div[@data-qa='TypeComponent' and not(ancestor::div[@data-qa='Tooltip'])])[position() mod 2 = 1]" );
    private final By KEYWORD_BUCKETS = By.xpath( "(//div[@data-qa='InlineAccordion']/div[1]//div[@data-qa='TypeComponent' and not(ancestor::div[@data-qa='Tooltip'])])[position() mod 2 = 0]" );
    private final By PRODUCT_TITLE_INPUT = By.xpath( "//div[@data-qa='TextInput']//textarea[contains(@id,'product_title')]" );
    private final By HIGHLIGHT_KEYWORD_TOGGLE = By.xpath( "//div[@data-qa='Toggle']" );
    private final By COPY_PASTE_BUTTON = By.xpath( "//i[@class='fa fa-file-import']" );
    protected final By TASK_SUBMITTING_SPINNER = By.xpath( "//div[@id='loadingGraphic']" );
    public final String PRODUCT_DESCRIPTION_ID = "product_description";
    public final String PRODUCT_TITLE_ID = "product_title";
    public final String BULLET_ONE_ID = "bullet_1";

    public ContentTaskPage(WebDriver driver) throws Exception {
        super(driver);
    }

    public void fillProducts() throws InterruptedException {
        String text = "Automated test trial word";
        int desiredLengthForInputs = 20;
        int desiredLengthForTextArea = 500;
        List<WebElement> products = findElementsVisible( By.xpath( PRODUCTS_IN_DYNAMIC_NAV_SIDEBAR_XPATH ) );

        for ( WebElement product : products ) {
            product.click();
            fillInputsAndTextArea( text, desiredLengthForInputs, desiredLengthForTextArea );
            driver.findElements(PRODUCT_CHECKBOXES).get(0).click();
        }
    }

    public void fillInAllInputs(String word, int desiredLength) throws InterruptedException {
        List<WebElement> inputs = findElementsVisible( ALL_INPUTS );
        for ( WebElement input : inputs ) {
            String text = "";

            while ( text.length() <= desiredLength ) {
                text += word + "";
            }

            text = text.trim();
            input.clear();

            if ( !input.getAttribute( "value" ).isEmpty() ) {
                WebDriverWait wait = new WebDriverWait( driver, Duration.ofSeconds(30) );
                wait.until(ExpectedConditions.textToBePresentInElementValue( input, "" ) );

                while ( !input.getAttribute( "value" ).isEmpty() ) {
                    for ( int i = 0; i <= input.getAttribute( "value" ).length(); i++ ) {
                        input.sendKeys( Keys.CONTROL + "a" + Keys.BACK_SPACE );
                        input.clear();
                    }
                }
            }
            input.sendKeys( text + Keys.TAB );
        }
    }

    public void emptyOutAllFields() {
        String taskUIFields = "//div[@id='feedback-section']//textarea";
        By allInputs = By.xpath( taskUIFields );
        int inputsCount = getElementCount( allInputs );
        for ( int index = 1; index <= inputsCount; index++ ) {
            By input = By.xpath( "(" + taskUIFields + ")[" + index + "]" );
            clearInput( input );
            WebDriverWait wait = new WebDriverWait( driver, Duration.ofSeconds(30) );
            wait.until(ExpectedConditions.textToBePresentInElementValue( input, "" ) );

            while ( !Objects.equals(getAttribute(input, "value"), "") ) {
                clearInput( input );
            }
        }
    }

    public void fillInTextArea(String word, int desiredLength) throws InterruptedException {
        String text = "";

        while ( text.length() <= desiredLength ) {
            text += word + " ";
        }

        text = text.trim();
        WebElement textarea = findElementVisible( PRODUCT_DESCRIPTION_AREA );
        textarea.getAttribute( "value" );
        Thread.sleep( 500 );
        textarea.clear();
        if ( !textarea.getAttribute( "value" ).equals("") ) {
            WebDriverWait wait = new WebDriverWait( driver, Duration.ofSeconds(30) );
            wait.until(ExpectedConditions.textToBePresentInElementValue( textarea, "" ) );

            while ( !textarea.getAttribute( "value" ).equals("") ) {
                for ( int i = 0; i < textarea.getAttribute( "value" ).length(); i++ ) {

                    textarea.sendKeys( Keys.CONTROL + "a" + Keys.BACK_SPACE );
                    textarea.clear();
                }
            }
        }
        int chunkLength = 200;
        Iterable<String> chunks = Splitter.fixedLength(chunkLength).split(text);
        for ( String chunk : chunks ) {
            textarea.sendKeys( chunk );
        }
        textarea.sendKeys( Keys.TAB );
    }

    public void fillInputsAndTextArea( String word , int desiredLengthForInputs , int desiredLengthForTextArea  ) throws InterruptedException {
        fillInAllInputs( word, desiredLengthForInputs );
        fillInTextArea( word, desiredLengthForTextArea );
    }

    public boolean fillInputsWithLimitedText() throws InterruptedException {
        String text = "Automated test trial word";
        fillInputsAndTextArea( text, 19, 499 );
        return isElementVisible(VALIDATION_ICON);
    }

    public boolean fillInputsWithTooMuchText() throws InterruptedException {
        String text = "Automated test trial word";
        fillInputsAndTextArea( text, 151, 1001 );
        return isElementVisible(VALIDATION_ICON);
    }

    public boolean fillInputsWithCorrectAmountOfText() throws InterruptedException {
        String text = "Automated test trial word";
        fillInputsAndTextArea( text, 25, 500 );
        return !isElementVisible(VALIDATION_ICON);
    }

    public List<String> getAllKeywordBucketsInitials() {
        moveToElement( KEYWORD_BUCKETS );
        return findElementsPresent( KEYWORD_BUCKETS ).stream().map(set -> set.getText()).collect(Collectors.toList());
    }

    public void waitForTaskToBeDisplayed() {
        WebDriverWait wait = new WebDriverWait( driver, Duration.ofSeconds(80) );
        if (isElementVisible(LOAD_STATE, Duration.ofSeconds(5))) {
            wait.until( ExpectedConditions.visibilityOf(findElementVisible(WORKFLOW_TASK_UI) ) );
        }
        wait.until( ExpectedConditions.elementToBeClickable( SAVE_TASK_BUTTON ) );
    }

    public void saveTask() throws InterruptedException {
        click( SAVE_TASK_BUTTON );
    }

    public void refreshTask() throws InterruptedException {
        refreshPage();
        waitForTaskToBeDisplayed();
        switchFeedbackNotificationsIndicator( Enums.ToggleAction.Hide );
    }

    public void waitForSubmitSpinnerInvisibility() {
        waitForElementToBeInvisible(TASK_SUBMITTING_SPINNER, Duration.ofSeconds(35));
    }

    public SpellCheckModal isSpellCheckModalDisplayed() {
        return isElementVisible(SPELL_CHECK_MODAL, Duration.ofSeconds(5)) ? new SpellCheckModal(driver) : null;
    }

    public String checkTitleKeywordValidationBeginningAndEndOfString() {
        WebElement titleKeywordElement = findElementVisible( TITLE_KEYWORDS );
        String titleKeyword = titleKeywordElement.getText();
        String trialText = titleKeyword + " testing testing " + titleKeyword;
        findElementVisible(PRODUCT_TITLE_INPUT, Duration.ofSeconds(35));
        sendKeys( PRODUCT_TITLE_INPUT, trialText );
        return getKeywordValidation( "Title", "test" ).replace( "\r\n", " " );
    }

    public String getKeywordValidation( String keywordBucket, String keyword ) {
        By keywordCount = By.xpath( "//div[text()='" + keywordBucket + "']//ancestor::div[@data-qa='InlineAccordion']//div[child::div[text()='" + keyword + "']]//following-sibling::div/div" );
        return getTextFromElement( keywordCount );
    }

    public boolean checkKeywordsHighlighting() throws InterruptedException {
        click( HIGHLIGHT_KEYWORD_TOGGLE );
        boolean highlightedKeyword = isElementVisible(By.xpath("//span[@data-markjs='true']"), Duration.ofSeconds(30));
        if ( !highlightedKeyword ) {
            click( HIGHLIGHT_KEYWORD_TOGGLE );
            return false;
        }
        click( HIGHLIGHT_KEYWORD_TOGGLE );
        return true;
    }

    public String checkMultiWordKeywordValidation() {
        return getKeywordValidation( "Page", "trial word" ).replace( "\r\n", " " );
    }

    public String checkCopyPasteFunctionality( String family, String brand, String copyPasteString ) throws InterruptedException {
        By xPathForFamilyLevelProduct = By.xpath( "//div[@data-qa='DynamicNavSidebar']//div[contains(text(), '" + family + "')]" );
        By xPathForBrandLevelProduct = By.xpath( "//div[@data-qa='DynamicNavSidebar']//div[contains(text(), '" + brand + "')]" );
        click( xPathForFamilyLevelProduct );
        findElementVisible(PRODUCT_TITLE_INPUT, Duration.ofSeconds(20));
        sendKeys( PRODUCT_TITLE_INPUT, copyPasteString );
        click( xPathForBrandLevelProduct );
        clearInput( PRODUCT_TITLE_INPUT );
        findElementVisible(COPY_PASTE_BUTTON, Duration.ofSeconds(20));
        click( COPY_PASTE_BUTTON );
        findElementVisible(By.xpath("//div[@data-qa='Overlay']"), Duration.ofSeconds(30)); // waiting for the same overlay here and in condition below
        if (isElementVisible(By.xpath("//div[@data-qa='Overlay']"), Duration.ofSeconds(15))) {
            By secondListedCheckBox = By.xpath( "(//div[@data-qa='Overlay']//input)[2]" );
            By overlayButton = By.xpath( "//div[@data-qa='Overlay']//div[@data-qa='Button']" );
            By textDiffToggle = By.xpath( "//div[@data-qa='Overlay']//div[@data-qa='Toggle']" );
            By familyProductTitleInOverlay = By.xpath( "//div[@data-qa='Overlay']//div[contains(text(), '" + family + "')]" );
            click( familyProductTitleInOverlay );
            click( secondListedCheckBox );
            click( overlayButton );
            click( textDiffToggle );
            click( secondListedCheckBox );
            click( overlayButton );
            closeSuccessMessage();
            waitForElementToBeInvisible(SUCCESS_MESSAGE, Duration.ofSeconds(20));
            click( xPathForBrandLevelProduct );
            Thread.sleep( 5000 );
            sendKeys( PRODUCT_TITLE_INPUT, String.valueOf(Keys.TAB));
            return getTextFromElement( PRODUCT_TITLE_INPUT );
        }
        return getTextFromElement( PRODUCT_TITLE_INPUT );
    }

    public void fillInput( String sectionID, String word ) {
        By requestedInput = By.xpath( "//textarea[contains(@id,'" + sectionID + "')]" );
        WebElement inputElement = findElementPresent( requestedInput );

        String message = "";
        int requiredWords;
        if ( sectionID.contains( "description" ) ) {
            requiredWords = 500 / word.length();
        } else {
            requiredWords = 1;
        }

        for ( int i = 0; i < requiredWords; i++ ) {
            message += word + " ";
        }

        if ( sectionID.contains( "description" ) && message.length() < 500 ) {
            message += word + " ";
        }
        message = message.trim();
        inputElement.sendKeys( Keys.CONTROL + "a" + Keys.BACK_SPACE );
        sendKeys( requestedInput, message + Keys.TAB );
    }

    public void fillSingleInput( String sectionID, String word, int productIndex  ) throws InterruptedException {
        clickOnSpecificProduct( productIndex );
        fillInput( sectionID, word );
    }

    public boolean doesTaskHaveField( String fieldID ) {
        By fieldElement = By.xpath( "//div[@id='\" + fieldID + \"'] | //textarea[@id='" + fieldID + "']" );
        return isElementVisible(fieldElement, Duration.ofSeconds(15));
    }

    public void fillRequiredFields( int productIndex, String text ) throws InterruptedException {
        fillSingleInput( PRODUCT_TITLE_ID, text, productIndex );
        fillSingleInput( BULLET_ONE_ID, text, productIndex );
        if ( doesTaskHaveField( PRODUCT_DESCRIPTION_ID ) ) {
            fillSingleInput( PRODUCT_DESCRIPTION_ID, text, productIndex );
        }
    }

    public boolean isKeywordCountValidated( String keywordBucket ) {
        By validKeywordCountIcon = By.xpath( "//div[text()='" + keywordBucket + "']//ancestor::div[@data-qa='InlineAccordion']//span[contains(@class,'check')]" );
        return isElementVisible(validKeywordCountIcon, Duration.ofSeconds(30));
    }

    public boolean isTitleKeywordBucketCountValidated() {
        return isKeywordCountValidated( "Title" );
    }

    public List<WebElement> getProductsWithErrors() {
        By productWithError = By.xpath( "//div[@class='_e296pg']/../../preceding-sibling::div" );
        return findElementsPresent( productWithError );
    }

    public int getOccurrencesOfWordInField ( String sectionID, String word ) {
        By field = By.xpath( "//textarea[@id='" + sectionID + "']" );
        String fieldValue = getTextFromElement( field );
        int occurrences = 0;
        String[] fieldValues = fieldValue.split( " " );
        for ( String value : fieldValues ) {
            if ( value.equals( word ) ) {
                occurrences++;
            }
        }
        return occurrences;
    }

    public void removeFieldsFromTaskUIIfExistent(String jwt, String mappingId, ArrayList<String> fieldsToRemove) throws Exception {
        TaskUIConfig taskUIConfig = ProductService.getTaskUIMappingConfig( jwt, mappingId );
        ProductService.removePropertiesFromMappingConfig( jwt, mappingId, fieldsToRemove, taskUIConfig );
        refreshTask();
    }

    public void removeFieldFromTaskUIIfExistent( String jwt, String mappingId, String fieldIDToRemove ) throws Exception {
        TaskUIConfig taskUIConfig = ProductService.getTaskUIMappingConfig( jwt, mappingId );
        if ( doesTaskHaveField( fieldIDToRemove ) ) {
            ProductService.removePropertyFromMappingConfig( jwt, mappingId, fieldIDToRemove, taskUIConfig );
            refreshTask();
        }
    }

    public void addFieldToTaskUIIfNotExistent( String jwt, String mappingID, String fieldIDToAdd ) throws Exception {
        if ( !doesTaskHaveField( fieldIDToAdd ) ) {
           ProductService.addPropertyToMappingConfig( jwt, mappingID, fieldIDToAdd );
            refreshTask();
        }
    }

    public void displayOriginalFields( String jwt, String mappingID, String specialProperty ) throws Exception {
        removeFieldFromTaskUIIfExistent( jwt, mappingID, specialProperty );
        addFieldToTaskUIIfNotExistent( jwt, mappingID, PRODUCT_DESCRIPTION_ID );
    }

    public void clickOrFillProductsAndSubmitTask( boolean isCommitStep ) throws Exception {
        clickAllProducts();
        try {
            submitTaskAndRefreshTasksTable( isCommitStep );
        } catch ( Exception e ) {
            closeModalIfOpened();
            List<WebElement> elements = getProductsWithErrors();
            for ( WebElement element : elements ) {
                element.click();
                fillInputsAndTextArea( "Automated test trial word", 29, 500 );
            }
            submitTaskAndRefreshTasksTable( isCommitStep );
        }
    }
}
