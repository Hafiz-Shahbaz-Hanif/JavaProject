package com.DC.utilities.sharedElements;

import com.DC.pageobjects.PageHandler;
import com.DC.utilities.SharedMethods;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

public class Paginator extends PageHandler {
    private final String PAGINATOR_BASE_XPATH = "//div[child::nav[contains(@class,'Pagination')]]";

    private final By PAGINATOR_LOCATOR = By.xpath("//nav[contains(@aria-label,'pagination')]");
    private final By NEXT_PAGE_BUTTON_LOCATOR = By.xpath("//nav//button[text()='Next']");
    private final By PREVIOUS_PAGE_BUTTON_LOCATOR = By.xpath("//nav//button[text()='Previous']");
    private final By SPINNER = By.xpath("//i[contains(@class,'fa-spinner')]");

    private final By DROPDOWN_BUTTON = By.xpath(PAGINATOR_BASE_XPATH + "/div//button");
    private final By DROPDOWN_MENU = By.xpath(PAGINATOR_BASE_XPATH + "//li[@role='menuitem']");

    private final Duration MAX_WAIT_TIME_IN_SECONDS = Duration.ofSeconds(3);

    public Paginator(WebDriver driver) {
        super(driver);
        findElementVisibleMilliseconds(PAGINATOR_LOCATOR);
    }

    public void goToSpecificPage(int pageNumber) throws InterruptedException {
        By pageElement = By.xpath(PAGINATOR_BASE_XPATH + "//li//button[text()='" + pageNumber + "']");
        scrollIntoViewAndClick(pageElement);
        waitForElementToBeInvisible(SPINNER, MAX_WAIT_TIME_IN_SECONDS);
    }

    public void goToPreviousPage() throws InterruptedException {
        scrollIntoViewAndClick(PREVIOUS_PAGE_BUTTON_LOCATOR);
        waitForElementToBeInvisible(SPINNER, MAX_WAIT_TIME_IN_SECONDS);
    }

    public void goToNextPage() throws InterruptedException {
        scrollIntoViewAndClick(NEXT_PAGE_BUTTON_LOCATOR);
        waitForElementToBeInvisible(SPINNER, MAX_WAIT_TIME_IN_SECONDS);
    }

    public void selectPageNumber(int numberToSelect) throws InterruptedException {
        By pageElement = By.xpath(PAGINATOR_BASE_XPATH + "//li//button[text()='" + numberToSelect + "']");
        scrollIntoViewAndClick(pageElement);
        waitForElementToBeInvisible(SPINNER, MAX_WAIT_TIME_IN_SECONDS);
    }

    public void goToLastPage() throws InterruptedException {
        var lastPageXPath = By.xpath("(//div[child::nav[contains(@class,'Pagination')]]//button[contains(@class,'PaginationItem-page')])[last()]");
        scrollIntoViewAndClick(lastPageXPath);
    }

    public long getCurrentItemsPerPage() {
        String itemsPerPageText = getTextFromElementMilliseconds(DROPDOWN_BUTTON);
        return SharedMethods.extractNumbersFromString(itemsPerPageText).get(0);
    }

    public void openMenuToSelectItems() throws InterruptedException {
        boolean menuIsOpen = isElementVisibleMilliseconds(DROPDOWN_MENU);
        if (!menuIsOpen) {
            scrollIntoViewAndClick(DROPDOWN_BUTTON);
        }
    }

    public int getActivePageNumber() {
        String activePageNumber = getTextFromElementMilliseconds(By.xpath(PAGINATOR_BASE_XPATH + "//li/button[@aria-current='true']"));
        return SharedMethods.extractIntegerFromString(activePageNumber);
    }

    public void selectNumberOfItemsPerPage(int numberOfItemsPerPage) throws InterruptedException {
        By itemsPerPageLocator = By.xpath(PAGINATOR_BASE_XPATH + "//li[text()='" + numberOfItemsPerPage + " Items']");
        long currentItemsPerPage = getCurrentItemsPerPage();
        if (currentItemsPerPage != numberOfItemsPerPage) {
            openMenuToSelectItems();
            scrollIntoViewAndClick(itemsPerPageLocator);
            waitForElementToBeInvisible(SPINNER, MAX_WAIT_TIME_IN_SECONDS);
        }
    }


}
