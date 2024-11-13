package com.DC.uitests.adc.identify.searchInsights;

import com.DC.constants.InsightsConstants;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.identify.searchInsights.keywords.KeywordWatchlistsPage;
import com.DC.pageobjects.adc.identify.searchInsights.keywords.WatchlistDetailsPage;
import com.DC.testcases.BaseClass;
import com.DC.utilities.enums.Enums;
import org.testng.ITestContext;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.DC.constants.NetNewConstants.DC_LOGIN_ENDPOINT;

public class WatchlistDetailsPageUITests extends BaseClass {
    private final String USERNAME = READ_CONFIG.getInsightsUsername();
    private final String PASSWORD = READ_CONFIG.getInsightsPassword();
    private final String WATCHLIST_TO_TEST = "Static Watchlist (DO NOT REMOVE)";
    private final List<String> KEYWORDS_TO_TEST = Arrays.asList("dry wine", "white wine", "sweet wine", "10.5%");
    WatchlistDetailsPage watchlistDetailsPage;

    @BeforeClass
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(DC_LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USERNAME, PASSWORD);
        driver.get(InsightsConstants.INSIGHTS_WATCHLISTS_URL);
        watchlistDetailsPage = new KeywordWatchlistsPage(driver)
                .clickViewDetailsButton(WATCHLIST_TO_TEST);
    }

    @BeforeMethod
    public void closeReactModalIfDisplayed() throws InterruptedException {
        watchlistDetailsPage.closeReactModalIfDisplayed();
    }

    @AfterClass
    public void killDriver() {
        try {
            removeTestingKeywordsIfNeeded();
        } catch (Exception ignored) {
        }
        quitBrowser();
    }

    @Test(priority = 1,
            description = "C244606 - modal opens when add keywords button is clicked," +
                    "modal closes when cancel button is clicked," +
                    "modal closes when x button is clicked" +
                    "keywords are added when submit button is clicked"
    )
    public void CSCAT_WatchlistsDetails_AddKeywordsModalWorksAsExpected() {
        SoftAssert softAssert = new SoftAssert();
        var keywordsSeparatedByPipe = String.join("|", KEYWORDS_TO_TEST);

        var keywordsBefore = watchlistDetailsPage.getKeywordsInWatchlist();

        // TESTING CLOSING MODAL DOES NOT ADD KEYWORDS
        var keywordsAfterClosingModal = watchlistDetailsPage.clickAddKeywordsButton()
                .insertKeywords(keywordsSeparatedByPipe)
                .clickCancelButton()
                .getKeywordsInWatchlist();

        softAssert.assertEquals(keywordsAfterClosingModal, keywordsBefore, "keywords were added after clicking cancel button");

        watchlistDetailsPage.clickAddKeywordsButton()
                .insertKeywords(keywordsSeparatedByPipe)
                .clickCloseIconFromReactModal();

        keywordsAfterClosingModal = watchlistDetailsPage.getKeywordsInWatchlist();

        softAssert.assertEquals(keywordsAfterClosingModal, keywordsBefore, "keywords were added after clicking close icon");

        // TESTING ADDING KEYWORDS
        watchlistDetailsPage = watchlistDetailsPage.clickAddKeywordsButton()
                .addKeywordsToWatchlist(keywordsSeparatedByPipe);

        var successMessageDisplayed = watchlistDetailsPage.isNoteDisplayedWithMessage(Enums.NoteType.SUCCESS, "Your keywords were added to the selected watchlists.");
        softAssert.assertTrue(successMessageDisplayed, "success message was not displayed after adding keywords");

        var keywordsAfterAddingKeywords = watchlistDetailsPage.getKeywordsInWatchlist();

        var expectedKeywords = new ArrayList<>(keywordsBefore);
        expectedKeywords.addAll(Arrays.asList(keywordsSeparatedByPipe.split("\\|")));
        expectedKeywords = expectedKeywords.stream().distinct().collect(Collectors.toCollection(ArrayList::new));
        softAssert.assertEqualsNoOrder(keywordsAfterAddingKeywords.toArray(), expectedKeywords.toArray(),
                "keywords were not added after clicking submit button" +
                        "\nexpected: " + expectedKeywords +
                        "\nactual: " + keywordsAfterAddingKeywords
        );

        var keywordsCountAfterAddingKeywords = watchlistDetailsPage.getKeywordsInCounter();
        softAssert.assertEquals(keywordsCountAfterAddingKeywords, expectedKeywords.size(), "keywords in counter is not correct after adding keywords");

        softAssert.assertAll();
    }

    @Test(priority = 2, description = "C244607 - Can remove keywords from watchlist")
    public void CSCAT_WatchlistsDetails_CanRemoveKeywordsFromWatchlist() {
        SoftAssert softAssert = new SoftAssert();

        addKeywordsToTestIfNeeded();

        var keywordsBefore = watchlistDetailsPage.getKeywordsInWatchlist();
        watchlistDetailsPage = watchlistDetailsPage.removeKeywordsFromWatchlist(KEYWORDS_TO_TEST);

        var successMessageDisplayed = watchlistDetailsPage.isNoteDisplayedWithMessage(Enums.NoteType.SUCCESS, " keywords have been removed!");
        softAssert.assertTrue(successMessageDisplayed, "success message was not displayed after adding keywords");

        var keywordsAfterRemovingKeywords = watchlistDetailsPage.getKeywordsInWatchlist();
        var expectedKeywords = new ArrayList<>(keywordsBefore);
        expectedKeywords.removeAll(KEYWORDS_TO_TEST);
        softAssert.assertEqualsNoOrder(keywordsAfterRemovingKeywords.toArray(), expectedKeywords.toArray(), "keywords were not removed after clicking submit button");

        softAssert.assertAll();
    }

    private void addKeywordsToTestIfNeeded() {
        var currentKeywords = watchlistDetailsPage.getKeywordsInWatchlist();
        var missingKeywords = KEYWORDS_TO_TEST.stream().filter(keyword -> !currentKeywords.contains(keyword)).collect(Collectors.toList());
        if (!missingKeywords.isEmpty()) {
            var keywordsSeparatedByPipe = String.join("|", missingKeywords);
            watchlistDetailsPage = watchlistDetailsPage.clickAddKeywordsButton()
                    .addKeywordsToWatchlist(keywordsSeparatedByPipe);
        }
    }

    private void removeTestingKeywordsIfNeeded() {
        var currentKeywords = watchlistDetailsPage.getKeywordsInWatchlist();
        var keywordsToRemove = KEYWORDS_TO_TEST.stream().filter(currentKeywords::contains).collect(Collectors.toList());
        if (!keywordsToRemove.isEmpty()) {
            watchlistDetailsPage = watchlistDetailsPage.removeKeywordsFromWatchlist(keywordsToRemove);
        }
    }
}
