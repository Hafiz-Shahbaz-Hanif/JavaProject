package com.DC.pageobjects.legacy.legacyinsights;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

public class CreateBatchSection extends GenerateLaunchFilePage {
    protected final By BATCH_NAME_INPUT = By.xpath("//input[@type='text' and contains(@placeholder,'Enter a name')]");
    protected final By TASKS_NUMBER_INPUT = By.xpath( "//input[@type='number' and contains(@placeholder,'Enter a number')]" );
    protected final By CHAINS_DROPDOWN = By.xpath( "//div[@data-qa='CreateBatch']//div[@data-qa='Select']" );
    protected final By CHAINS_DROPDOWN_SEARCH = By.xpath( "//div[@data-qa='CreateBatch']//div[@data-qa='Select']//input" );
    protected final By CHAINS_DROPDOWN_MENU = By.xpath( "//div[@data-qa='CreateBatch']//div[@data-qa='Select']//div[@data-qa='DropdownMenu']" );


    public CreateBatchSection(WebDriver driver) throws Exception {
        super(driver);
        findElementVisible(BATCH_NAME_INPUT);
    }

    public CreateBatchSection enterNameOfBatch( String nameOfBatch ) {
        sendKeys( BATCH_NAME_INPUT, nameOfBatch );
        return this;
    }

    public CreateBatchSection enterNumberOfTasks( int numberOfTasks ) {
        setText( TASKS_NUMBER_INPUT, String.valueOf(numberOfTasks));
        return this;
    }

    public CreateBatchSection selectChain( String chainName ) throws InterruptedException {
        By chain = By.xpath( "//div[@data-qa='ItemList']//div[text()='" + chainName + "']" );
        openDropdownToSelectChain();
        sendKeys( CHAINS_DROPDOWN_SEARCH, chainName);
        scrollIntoViewAndClick( chain );
        return this;
    }

    public void openDropdownToSelectChain() throws InterruptedException {
        if ( !isDropdownMenuToSelectChainVisible() ) {
            scrollIntoViewAndClick( CHAINS_DROPDOWN );
        }
    }

    public boolean isDropdownMenuToSelectChainVisible() {
        return isElementVisible(CHAINS_DROPDOWN_MENU, Duration.ofSeconds(15));
    }

    public FormatBatchSection clickContinue() throws Exception {
        scrollIntoViewAndClick( CONTINUE_BUTTON );
        return new FormatBatchSection( driver );
    }
}
