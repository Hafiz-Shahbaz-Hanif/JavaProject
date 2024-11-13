package com.DC.utilities.sharedElements;

import com.DC.pageobjects.PageHandler;
import com.DC.utilities.enums.Enums;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;

public class SpellCheckModal extends PageHandler {
    protected final By SPELL_CHECK_MODAL = By.xpath("//div[@data-qa='Spellcheck']");
    protected final By NEXT_PRODUCT_BUTTON = By.xpath( "//div[@data-qa='Spellcheck']//div[div[text()='Next Product']]" );
    protected final By PREVIOUS_PRODUCT_BUTTON = By.xpath( "//div[@data-qa='Spellcheck']//div[div[text()='Previous Product']]" );
    protected final By NEXT_ITEM_ICON = By.xpath( "//div[@id='spellcheck-word-panel']//i[contains(@class,'chevron-right')]" );
    protected final By PREVIOUS_ITEM_ICON = By.xpath( "//div[@id='spellcheck-word-panel']//i[contains(@class,'chevron-left')]" );
    protected final By UNDO_ICON = By.xpath( "//div[@id='spellcheck-word-panel']//i[contains(@class,'undo')]" );
    protected final By ACTIVE_WORD = By.xpath( "(//div[@unique-id and contains(@style,'background-color: rgb(208, 236, 243)')]//div[@data-qa='TypeComponent'])[1]" );
    protected final By WORDS_IN_PANEL = By.xpath( "//div[@id='spellcheck-word-panel']//div[@unique-id]//div[@data-qa='TypeComponent' and position()=1]" );
    protected final By REPLACEMENT_INPUT = By.xpath( "//div[@class='spellcheck-replacement']//input" );
    protected final By REPLACEMENT_SAVE_ICON = By.xpath( "//div[@class='spellcheck-replacement']//i" );
    protected final By SPELL_CHECK_MODAL_SUBMIT_BUTTON = By.xpath( "//div[@data-qa='Spellcheck']//span[text()='Submit']" );
    protected final By FIRST_WORD_IN_PANEL = By.xpath( "(//div[@id='spellcheck-word-panel']//div[@unique-id])[1]" );
    protected final By LAST_WORD_IN_PANEL = By.xpath( "(//div[@id='spellcheck-word-panel']//div[@unique-id])[last()]" );
    protected final String OPACITY = "1";

    public SpellCheckModal(WebDriver driver ) {
        super(driver);
        findElementVisible(SPELL_CHECK_MODAL);
    }

    public boolean isNextProductButtonEnabled() {
        WebElement element = findElementVisible( NEXT_PRODUCT_BUTTON );
        String elementOpacity = element.getCssValue( "opacity" );
        return elementOpacity == OPACITY;
    }

    public boolean isPreviousProductButtonEnabled() {
        WebElement element = findElementVisible( PREVIOUS_PRODUCT_BUTTON );
        String elementOpacity = element.getCssValue( "opacity" );
        return elementOpacity == OPACITY;
    }

    public boolean isPreviousItemIconEnabled() {
        return isElementEnabledMilliseconds(PREVIOUS_ITEM_ICON);
    }

    public boolean isNextItemIconEnabled() {
        return isElementEnabledMilliseconds(NEXT_ITEM_ICON);
    }

    public SpellCheckModal clickNextItemIcon() throws InterruptedException {
        click( NEXT_ITEM_ICON );
        return new SpellCheckModal( driver );
    }

    public SpellCheckModal clickPreviousItemIcon() throws InterruptedException {
        click( PREVIOUS_ITEM_ICON );
        return new SpellCheckModal( driver );
    }

    public SpellCheckModal clickNextProductButton() throws InterruptedException {
        click( NEXT_PRODUCT_BUTTON );
        return new SpellCheckModal( driver );
    }

    public SpellCheckModal clickPreviousProductButton() throws InterruptedException {
        click( PREVIOUS_PRODUCT_BUTTON );
        return new SpellCheckModal( driver );
    }

    public SpellCheckModal clickUndoButton() throws InterruptedException {
        click( UNDO_ICON );
        return new SpellCheckModal( driver );
    }

    public SpellCheckModal clickOnWord(String word) throws InterruptedException {
        By wordInPanel = By.xpath( "//div[@id='spellcheck-word-panel']//div[text()='" + word + "']" );
        click( wordInPanel );
        return new SpellCheckModal( driver );
    }

    public SpellCheckModal clickFirstWordInPanel() throws InterruptedException {
        click( FIRST_WORD_IN_PANEL );
        return new SpellCheckModal( driver );
    }

    public SpellCheckModal clickLastWordInPanel() throws InterruptedException {
        click( LAST_WORD_IN_PANEL );
        return new SpellCheckModal( driver );
    }

    public SpellCheckModal typeReplacement(String replacement, Enums.SpellCheckOption option) throws InterruptedException {
        scrollIntoView( NEXT_ITEM_ICON );
        sendKeys( REPLACEMENT_INPUT, replacement );
        click( REPLACEMENT_SAVE_ICON );
        return selectReplacementOption( option );
    }

    public SpellCheckModal selectReplacement(String replacement, Enums.SpellCheckOption option) throws InterruptedException {
        By replacementItem = By.xpath( "//div[@class='spellcheck-replacement']//div[text()='" + replacement + "']" );
        click( replacementItem );
        return selectReplacementOption( option );
    }

    public SpellCheckModal selectReplacementOption( Enums.SpellCheckOption option ) throws InterruptedException {
        By optionToSelect = By.xpath( "//div[@class='spellcheck-replacement']//div[@unique-id='" + option.value + "']" );
        click( optionToSelect );
        return this;
    }

    public String getWordStatus( String word ) {
        By wordStatus = By.xpath( "//div[@unique-id and descendant::div[text()='" + word + "']]/div[2]" );
        return getTextFromElement( wordStatus );
    }

    public ArrayList<String> getWordsInSpellCheckPanel() {
        ArrayList<String> words = new ArrayList<>();
        for (WebElement item : findElementsPresent( WORDS_IN_PANEL )) {
            words.add( item.getText() );
        }
        return words;
    }

    public ArrayList<String> getStatusOfAllWordOccurrences(String word) {
        By wordStatus = By.xpath( "//div[@unique-id and descendant::div[text()='" + word + "']]/div[2]" );
        ArrayList<String> statusOfAllOccurences = new ArrayList<>();
        for (WebElement item : findElementsPresent( wordStatus )) {
            statusOfAllOccurences.add( item.getText() );
        }
        return statusOfAllOccurences;
    }

    public String getFieldValue(String field) {
        By fieldValue = By.xpath( "//div[@class='spellcheck-content']//div[child::div[text()='" + field + "']]/div[@data-qa='TypeComponent']/div" );
        scrollIntoView( fieldValue );
        return getTextFromElement( fieldValue );
    }

    public String getActiveWord() {
        return getTextFromElement( ACTIVE_WORD );
    }

    public boolean isThereAnActiveWord() {
        return isElementEnabledMilliseconds( ACTIVE_WORD );
    }

    public void submitTask() throws InterruptedException {
        click( SPELL_CHECK_MODAL_SUBMIT_BUTTON );
    }
}
