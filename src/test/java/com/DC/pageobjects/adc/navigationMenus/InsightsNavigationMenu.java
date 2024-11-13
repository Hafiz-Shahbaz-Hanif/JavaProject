package com.DC.pageobjects.adc.navigationMenus;

import com.DC.utilities.enums.Enums;
import com.DC.utilities.sharedElements.SingleSelectDropdown;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

public class InsightsNavigationMenu extends BaseNavigationMenu {
    protected final By REACT_MODAL_CANCEL_BUTTON_LOCATOR = By.xpath("//div[@class='ReactModalPortal']//button[contains(text(),'Cancel')]");
    protected final By REACT_MODAL_CLOSE_ICON_LOCATOR = By.xpath("//div[@class='ReactModalPortal']//button[contains(text(),'close')]");
    protected final Duration MAX_WAIT_TIME_SECS = Duration.ofSeconds(3);
    public static final By COMPANY_SWITCHER = By.xpath("//div[@data-qa='InlineList']//input[contains(@id, 'mui-') and not(@value='Default Dataset')]");

    public InsightsNavigationMenu(WebDriver driver) {
        super(driver);
    }

    public void closeReactModalIfDisplayed() throws InterruptedException {
        if (isElementVisibleMilliseconds(REACT_MODAL_CANCEL_BUTTON_LOCATOR)) {
            scrollIntoViewAndClick(REACT_MODAL_CANCEL_BUTTON_LOCATOR);
        }
    }

    public void clickCloseIconFromReactModal() {
        clickElement(REACT_MODAL_CLOSE_ICON_LOCATOR);
        waitForElementToBeInvisible(REACT_MODAL_CLOSE_ICON_LOCATOR);
    }

    public boolean isNoteDisplayed(Enums.NoteType noteType) {
        String noteXPath = getNoteXPath(noteType);
        return isElementVisible(By.xpath(noteXPath), Duration.ofSeconds(2));
    }

    public boolean isNoteDisplayedWithMessage(Enums.NoteType noteType, String message) {
        if (!isNoteDisplayed(noteType)) {
            return false;
        }

        String noteXPath = getNoteXPath(noteType);
        By noteLocator = By.xpath(noteXPath + "//*[text()='" + message + "']");
        return isElementVisible(noteLocator, Duration.ofSeconds(2));
    }

    public void closeNoteIfDisplayed(Enums.NoteType noteType) {
        String noteXPath = getNoteXPath(noteType);
        By closeButton = By.xpath(noteXPath + "//button[text()='close']");
        if (isNoteDisplayed(noteType)) {
            clickElement(closeButton);
        }
    }

    public void switchCompany(String companyName) throws InterruptedException {
        var defaultCompany = waitUntilAttributeValuePresentInElement(InsightsNavigationMenu.COMPANY_SWITCHER, "value");

        if (!defaultCompany.equals(companyName)) {
            var dropdown = new SingleSelectDropdown(driver);
            dropdown.openDropdownMenu(COMPANY_SWITCHER);
            dropdown.selectOption(companyName);
        }
    }

    public void switchCompanyNew(String companyName) throws InterruptedException {
        var companySwitcher = By.xpath("//span[contains(@class,'MuiBadge') and parent::div]");
        var defaultCompany = getTextFromElement(companySwitcher);

        if (!defaultCompany.equals(companyName)) {
            var searchInput = By.xpath("//div[contains(@class,'MuiStack')]//input[@type='text']");
            var saveButton = By.xpath("//div[contains(@class,'MuiStack')]//button[text()='Save']");
            var optionToSelect = By.xpath("//div[contains(@class,'MuiListItem')]//p[text()='" + companyName + "']");
            clickElement(companySwitcher);
            setText(searchInput, companyName);
            clickElement(optionToSelect);
            clickElement(saveButton);
            waitForElementToBeInvisible(saveButton, Duration.ofSeconds(5));
        }
    }

    public <T> T switchCompany(String companyName, Class<T> pageObjectClass) throws InterruptedException {
        switchCompanyNew(companyName);
        return getPage(pageObjectClass);
    }

    protected String getNoteXPath(Enums.NoteType noteType) {
        switch (noteType) {
            case SUCCESS:
                return "//div[child::button[text()='check_circle']] | //div[child::span[text()='check_circle']]";
            case INFO:
                return "//div[child::button[text()='info']] | //div[child::span[text()='info']]";
            case WARNING:
                return "//div[child::button[text()='warning']] | //div[child::span[text()='warning']]";
            default:
                return null;
        }
    }

}
