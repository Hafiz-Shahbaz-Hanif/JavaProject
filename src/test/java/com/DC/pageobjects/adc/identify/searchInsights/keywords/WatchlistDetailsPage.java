package com.DC.pageobjects.adc.identify.searchInsights.keywords;

import com.DC.pageobjects.adc.navigationMenus.InsightsNavigationMenu;
import com.DC.utilities.SharedMethods;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.List;

public class WatchlistDetailsPage extends InsightsNavigationMenu {
    private final String PAGE_BODY_XPATH = "//div[@data-qa='WatchlistDetails']";
    private final By LOADER_LOCATOR = By.xpath("//div[@data-qa='LoadState']//h4[text()='Loading watchlist data']");
    private final By KEYWORDS_COUNTER_LOCATOR = By.xpath("//div[@data-qa='KeywordSearchActionBar']//h6");

    public WatchlistDetailsPage(WebDriver driver) {
        super(driver);
        waitForElementToBeInvisible(LOADER_LOCATOR, Duration.ofSeconds(2));
        findElementVisible(By.xpath(PAGE_BODY_XPATH));
    }

    public AddKeywordsModal clickAddKeywordsButton() {
        clickElement(By.xpath("//button[text()='Add Keywords']"));
        return new AddKeywordsModal(driver);
    }

    public List<String> getKeywordsInWatchlist() {
        var keywordCellLocator = By.xpath("//table/tbody/tr/td[2]/a");
        waitForElementToBeInvisible(LOADER_LOCATOR);
        try {
            return getTextFromElementsMilliseconds(keywordCellLocator);
        } catch (StaleElementReferenceException e) {
            return getTextFromElementsMilliseconds(keywordCellLocator);
        }
    }

    public int getKeywordsInCounter() {
        var keywordsCounter = getTextFromElement(KEYWORDS_COUNTER_LOCATOR);
        return SharedMethods.extractIntegerFromString(keywordsCounter);
    }

    public void selectKeyword(String keyword) {
        var keywordCheckbox = By.xpath("//table/tbody/tr[td[descendant::span[@title='" + keyword + "']]]/td[1]/input");
        try {
            selectElement(keywordCheckbox);
        } catch (StaleElementReferenceException e) {
            selectElement(keywordCheckbox);
        }
    }

    public WatchlistDetailsPage removeKeywordsFromWatchlist(List<String> keywordsToRemove) {
        var deleteFromWatchlistButton = By.xpath("//button[text()='Delete from Watchlist']");
        var deleteFromWatchlistButtonInModal = By.xpath("//div[@data-qa='DeleteFromWatchlist']//button[text()='Delete From Watchlist']");

        for (String keyword : keywordsToRemove) {
            selectKeyword(keyword);
        }

        clickElement(deleteFromWatchlistButton);
        clickElement(deleteFromWatchlistButtonInModal);
        return new WatchlistDetailsPage(driver);
    }

    public static class AddKeywordsModal extends InsightsNavigationMenu {
        private final String PAGE_BODY_XPATH = "//div[@data-qa='AddKeywordsToWatchLists']";
        public final By SUBMIT_BUTTON_LOCATOR = By.xpath(PAGE_BODY_XPATH + "//button[text()='Submit']");
        public final By CANCEL_BUTTON_LOCATOR = By.xpath(PAGE_BODY_XPATH + "//span[text()='Cancel']");
        public final By TEXTAREA_LOCATOR = By.xpath(PAGE_BODY_XPATH + "//textarea[not(@aria-hidden)]");

        public AddKeywordsModal(WebDriver driver) {
            super(driver);
            findElementVisible(SUBMIT_BUTTON_LOCATOR);
        }

        public AddKeywordsModal insertKeywords(String keywords) {
            setText(TEXTAREA_LOCATOR, keywords);
            return this;
        }

        public WatchlistDetailsPage clickCancelButton() {
            clickElement(CANCEL_BUTTON_LOCATOR);
            waitForElementToBeInvisible(SUBMIT_BUTTON_LOCATOR);
            return new WatchlistDetailsPage(driver);
        }

        public WatchlistDetailsPage addKeywordsToWatchlist(String keywords) {
            insertKeywords(keywords);
            clickElement(SUBMIT_BUTTON_LOCATOR);
            waitForElementToBeInvisible(SUBMIT_BUTTON_LOCATOR);
            return new WatchlistDetailsPage(driver);
        }
    }
}
