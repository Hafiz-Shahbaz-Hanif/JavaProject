package com.DC.pageobjects.legacy.legacyinsights;

import com.DC.utilities.sharedElements.GenericConfirmationModal;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.HashMap;
import java.util.Set;

public class ConfigureTaskAndExportSection extends GenerateLaunchFilePage{

    private int productNameColPosition;
    private int uniqueIdColPosition;
    private int ancestorUniqueIdColPosition;
    private int productLevelColPosition;
    protected final By CONFIGURE_TASK_AND_EXPORT_BODY = By.xpath("//div[@data-qa='ConfigureTaskAndExport']");
    protected final By LAUNCH_BUTTON = By.xpath( "//div[@data-qa='BottomActionBar']//span[text()='Launch']" );

    public ConfigureTaskAndExportSection(WebDriver driver) throws Exception {
        super(driver);
        findElementVisible(CONFIGURE_TASK_AND_EXPORT_BODY);
        productNameColPosition = getPositionOfColumnInTable( "Product Name" ) - 1;
        uniqueIdColPosition = getPositionOfColumnInTable( "unique_id" ) - 1;
        ancestorUniqueIdColPosition = getPositionOfColumnInTable( "Ancestor Unique Id" ) - 1;
        productLevelColPosition = getPositionOfColumnInTable( "Product Level" ) - 1;
    }

    public void clickOnTask( int taskNumber ) throws InterruptedException {
        By taskToClick = By.xpath( "//div[@data-qa='TaskNavigationSidebar']//div[text()='Task "+ taskNumber + "']" );
        scrollIntoViewAndClick( taskToClick );
    }

    public void selectAnAssociatedProduct( String productUniqueId ) throws InterruptedException {
        By checkboxToSelect = By.xpath( "//input[@id='" + productUniqueId + "']" );
        selectElement( checkboxToSelect );
    }

    public void fillDataField( String fieldLabel, String inputValue ) {
        By fieldInput = getFieldInput( fieldLabel );
        sendKeys( fieldInput, inputValue );
    }

    public void emptyDataField( String fieldLabel ) {
        By fieldInput = getFieldInput( fieldLabel );
        clearInput( fieldInput );
    }

    public void fillOutAllFields( HashMap<String, String> fieldsAndValues ) {
        fieldsAndValues.forEach( (field, value) -> fillDataField( field, value ) );
    }

    public void emptyOutAllFields( Set<String> fieldsToClearOut ) {
        for ( String field : fieldsToClearOut ) {
            emptyDataField( field );
        }
    }

    private By getFieldInput( String fieldLabel ) {
        return By.xpath( "//div[text()='" + fieldLabel + "']//following-sibling::div/input" );
    }

    public void launchBatch() throws InterruptedException {
        scrollIntoViewAndClick( LAUNCH_BUTTON );
        new GenericConfirmationModal( driver ).confirm();
    }

    public void waitForSpinnerInvisibility() {
        By spinner = By.xpath( "//div[@data-qa='Button']//i[contains(@class,'spinner')]" );
        waitForElementToBeInvisible(spinner, Duration.ofSeconds(20));
    }

    public FormatBatchSection backToPreviousStep() throws Exception {
        scrollIntoViewAndClick( BACK_TO_PREVIOUS_STEP_BUTTON );
        return new FormatBatchSection( driver );
    }
}
