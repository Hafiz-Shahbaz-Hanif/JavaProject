package com.DC.pageobjects.legacy.legacyinsights;

import com.DC.objects.insightslegacy.FormatBatchProductConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class FormatBatchSection extends GenerateLaunchFilePage {
    protected final By FORMAT_BATCH_BODY = By.xpath("//div[@data-qa='FormatBatch']");

    public FormatBatchSection(WebDriver driver) throws Exception {
        super(driver);
        findElementVisible(FORMAT_BATCH_BODY);
    }

    public void configureFormatBatch( FormatBatchProductConfig productBatchConfig ) throws InterruptedException {
        if ( productBatchConfig.levelsToInclude != null ) {
            for ( String levelToInclude : productBatchConfig.levelsToInclude ) {
                int checkboxPosition = getPositionOfColumnInTable( "Include " + levelToInclude + " Level" );
                By levelCheckbox = By.xpath( "//tbody/tr[td[position()=2 and text()='" + productBatchConfig.product.uniqueID + "']]//td[" + checkboxPosition + "]//input" );
                selectElement( levelCheckbox );
            }
        }
        setTaskNumber( productBatchConfig, productBatchConfig.taskNumber );
    }

    public void setTaskNumber(FormatBatchProductConfig productBatchConfig, int taskNumber) {
        int taskNumberInputPosition = getPositionOfColumnInTable( "Task Number" );
        By taskNumberInput = By.xpath( "//tbody/tr[td[position()=2 and text()='" + productBatchConfig.product.uniqueID + "']]//td[" + taskNumberInputPosition + "]//input" );
        sendKeys( taskNumberInput, String.valueOf(taskNumber));
    }

    public ConfigureTaskAndExportSection clickContinue() throws Exception {
        scrollIntoViewAndClick( CONTINUE_BUTTON );
        return new ConfigureTaskAndExportSection( driver );
    }

    public CreateBatchSection backToPreviousStep() throws Exception {
        scrollIntoViewAndClick( BACK_TO_PREVIOUS_STEP_BUTTON );
        return new CreateBatchSection( driver );
    }
}
