package com.DC.pageobjects.adc.execute.productManager.products.productDetailsPage;

import com.DC.pageobjects.adc.execute.productManager.products.BulkEditSelectProductsPage;
import com.DC.utilities.enums.Enums;
import com.DC.pageobjects.filters.MultiselectFilterWithSearchInput;
import com.DC.utilities.sharedElements.SingleSelectDropdown;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.*;
import java.util.stream.Collectors;

public class KeywordsTab extends ProductDetailsPage {
    public final String KEYWORD_BUCKETS_FILTER = "Filter Keyword Buckets";
    public MultiselectFilterWithSearchInput bucketSelectorFilter;
    public SingleSelectDropdown singleSelectDropdown;

    private final String KEYWORDS_TAB = "//div[@data-qa='KeywordsTab']";
    private final String KEYWORD_ROW = "//div[@data-qa='KeywordRow']";
    private final String ADD_KEYWORD_ROW = "//div[@data-qa='AddKeywordRow']";
    private final String PANELS_LOCATOR = "//div[@data-qa='KeywordBucketsSection']";
    private final By FILTER_KEYWORD_BUCKETS_BUTTON = By.xpath("//button[text()='" + KEYWORD_BUCKETS_FILTER + "']");
    private final By SEARCH_KEYWORDS_INPUT = By.xpath("//input[@placeholder='Search Keywords ...']");
    private final By EXPAND_MORE_ICON = By.xpath(KEYWORDS_TAB + "//button[text()='expand_more' and parent::div]");
    private final By EXPAND_LESS_ICON = By.xpath(KEYWORDS_TAB + "//button[text()='expand_less' and parent::div]");
    private final By BUCKET_TYPE = By.xpath("//div[contains(@class,'content')]//p");


    public KeywordsTab(WebDriver driver) {
        super(driver);
        findElementVisible(FILTER_KEYWORD_BUCKETS_BUTTON);
        bucketSelectorFilter = new MultiselectFilterWithSearchInput(driver);
        singleSelectDropdown = new SingleSelectDropdown(driver);
    }

    public int getNumberOfPanelsDisplayed() {
        return findElementsVisibleMilliseconds(By.xpath(PANELS_LOCATOR)).size();
    }

    public int getPanelNumberOfProductVersion(String productVersion, String locale) {
        String panelXPath = getPanelXPath(productVersion, locale);
        var elementPresent = isElementPresentMilliseconds(By.xpath(panelXPath));
        if (!elementPresent) {
            throw new NoSuchElementException("Panel for product version " + productVersion + " and locale " + locale + " not found");
        }

        By precedingPanels = By.xpath(panelXPath + "/preceding-sibling::div");
        List<WebElement> panels = findElementsVisibleMilliseconds(precedingPanels);
        return !panels.isEmpty() ? panels.size() + 1 : 1;
    }

    public List<String> getAllKeywordsOfProductVersion(String productVersion, String locale) {
        int panelPosition = getPanelNumberOfProductVersion(productVersion, locale);
        By keywordsXPath = By.xpath("(//div[@data-qa='KeywordBucketsSection'])[" + panelPosition + "]//h6");
        List<WebElement> allKeywords = findElementsVisibleMilliseconds(keywordsXPath);
        return getTextFromElements(allKeywords);
    }

    public Map<Enums.KeywordBucketType, List<String>> getAllKeywordsOfVersionGroupedByBucket(String productVersion, String locale) {
        Map<Enums.KeywordBucketType, List<String>> keywordsByBucket = new HashMap<>();
        for (var bucket : Enums.KeywordBucketType.values()) {
            var keywords = getKeywordsInBucket(productVersion, locale, bucket);
            keywordsByBucket.put(bucket, keywords);
        }
        return keywordsByBucket;
    }

    public List<String> getKeywordsInBucket(String productVersion, String locale, Enums.KeywordBucketType bucketType) {
        String keywordBucketEditor = getKeywordBucketEditorXPath(productVersion, locale, bucketType);
        By bucketKeywordsLocator = By.xpath(keywordBucketEditor + "//h6");

        List<WebElement> allKeywords = findElementsVisibleMilliseconds(bucketKeywordsLocator);
        return getTextFromElements(allKeywords);
    }

    public List<Map.Entry<String, String>> getProductVersionsAndLocaleDisplayed() {
        List<Map.Entry<String, String>> productVersionsAndLocale = new ArrayList<>();
        By panelTitle = By.xpath(KEYWORDS_TAB + "//h4");
        List<WebElement> elements = findElementsVisibleMilliseconds(panelTitle);

        for (int i = 0; i < elements.size(); i++) {
            WebElement element = elements.get(i);
            String productVersion = element.getText().replace(" Version", "");
            By localeLocator = By.xpath("(//div[@data-qa='KeywordsTab']//h4)[" + (i + 1) + "]//following-sibling::span");
            String locale = getTextFromElementMilliseconds(localeLocator);
            productVersionsAndLocale.add(new AbstractMap.SimpleEntry<>(productVersion, locale));
        }
        return productVersionsAndLocale;
    }

    public KeywordsTab searchForKeyword(String keyword) {
        setTextAndHitEnter(SEARCH_KEYWORDS_INPUT, keyword);
        return new KeywordsTab(driver);
    }

    public KeywordsTab filterKeywordBuckets(List<String> bucketsToShow) throws InterruptedException {
        bucketSelectorFilter.openFilter(KEYWORD_BUCKETS_FILTER);
        bucketSelectorFilter.selectOptionsAndApplyChanges(bucketsToShow);
        return this;
    }

    public KeywordsTab expandAllPanels() throws InterruptedException {
        int totalOfCollapsedPanels = getElementCount(EXPAND_MORE_ICON, MAX_WAIT_TIME_SECS);
        for (int i = 0; i < totalOfCollapsedPanels; i++) {
            click(EXPAND_MORE_ICON);
        }
        return this;
    }

    public void openRowToAddKeyword(String productVersion, String locale, Enums.KeywordBucketType bucketName) throws InterruptedException {
        String keywordBucketEditor = getKeywordBucketEditorXPath(productVersion, locale, bucketName);
        By fullXpath = By.xpath(keywordBucketEditor + ADD_KEYWORD_ROW);
        scrollIntoViewAndClick(fullXpath);
    }

    public void addKeywordToBucket(String productVersion, String locale, String keyword, Enums.KeywordBucketType bucketName) throws InterruptedException {
        openRowToAddKeyword(productVersion, locale, bucketName);

        By addKeywordInputLocator = By.xpath(ADD_KEYWORD_ROW + "//input");
        setText(addKeywordInputLocator, keyword);

        saveKeyword();
    }

    public void addKeywordToBucketAndApplyChanges(String productVersion, String locale, String keyword, Enums.KeywordBucketType bucketName) throws InterruptedException {
        addKeywordToBucket(productVersion, locale, keyword, bucketName);
        click(APPLY_CHANGES_BUTTON);
    }

    public void deleteKeywordFromBucket(String productVersion, String locale, String keyword, Enums.KeywordBucketType bucketName) throws InterruptedException {
        String keywordBucketEditor = getKeywordBucketEditorXPath(productVersion, locale, bucketName);
        String deleteIcon = "//div[@data-qa='KeywordRow' and descendant::h6[text()='" + keyword + "']]//button[text()='delete']";

        By fullXpath = By.xpath(keywordBucketEditor + deleteIcon);
        scrollIntoViewAndClick(fullXpath);
    }

    public void deleteAllKeywordOccurrences(String keywordToDelete) throws InterruptedException {
        By deleteIcon = By.xpath("//div[@data-qa='KeywordRow' and descendant::h6[text()='" + keywordToDelete + "']]//button[text()='delete']");
        deleteKeywords(keywordToDelete, deleteIcon);
    }

    public void deleteAllKeywordOccurrencesContainsText(String keywordToDelete) throws InterruptedException {
        By deleteIcon = By.xpath("//div[@data-qa='KeywordRow' and descendant::h6[contains(text(),'" + keywordToDelete + "')]]//button[text()='delete']");
        deleteKeywords(keywordToDelete, deleteIcon);
    }

    public void editKeyword(String productVersion, String locale, String keyword, Enums.KeywordBucketType bucketName, String newKeyword) throws InterruptedException {
        String keywordBucketEditor = getKeywordBucketEditorXPath(productVersion, locale, bucketName);
        String editIcon = "//div[@data-qa='KeywordRow' and descendant::h6[text()='" + keyword + "']]//button[text()='edit']";
        By fullXpath = By.xpath(keywordBucketEditor + editIcon);
        scrollIntoViewAndClick(fullXpath);

        By editKeywordInputLocator = By.xpath(KEYWORD_ROW + "//input");
        setText(editKeywordInputLocator, newKeyword);

        saveKeyword();
    }

    public boolean isKeywordWithStrikeThrough(String productVersion, String locale, String keyword, Enums.KeywordBucketType bucketName) {
        var keywordBucketEditor = getKeywordBucketEditorXPath(productVersion, locale, bucketName);
        var keywordXPath = "//h6[text()='" + keyword + "']";
        var fullXpath = By.xpath(keywordBucketEditor + keywordXPath);
        return getAttribute(fullXpath, "class").contains("css-1x3v65c");
    }

    public void clickUndoButtonOfKeyword(String productVersion, String locale, String keyword, Enums.KeywordBucketType bucketName) throws InterruptedException {
        String keywordBucketEditor = getKeywordBucketEditorXPath(productVersion, locale, bucketName);
        String editIcon = "//div[@data-qa='KeywordRow' and descendant::h6[text()='" + keyword + "']]//button[text()='undo']";
        By fullXpath = By.xpath(keywordBucketEditor + editIcon);
        moveToElementAndClick(fullXpath);
    }

    public void saveKeyword() throws InterruptedException {
        By addKeywordButtonLocator = By.xpath("(" + ADD_KEYWORD_ROW + "|" + KEYWORD_ROW + ")//button[text()='save']");
        scrollIntoViewAndClick(addKeywordButtonLocator);
    }

    public List<String> getBucketsDisplayed() {
        List<WebElement> buckets = findElementsVisible(BUCKET_TYPE);
        return getTextFromElements(buckets)
                .stream().distinct()
                .map(bucket -> bucket.replace("Keywords", "").trim())
                .collect(Collectors.toList());
    }

    public void moveKeywordsToAnotherBucket(String productVersion, String locale, Enums.KeywordBucketType sourceBucket, Enums.KeywordBucketType targetBucket) throws InterruptedException {
        String keywordBucketEditor = getKeywordBucketEditorXPath(productVersion, locale, sourceBucket);
        By copyButtonLocator = By.xpath(keywordBucketEditor + "//button[text()='Move / Copy']");
        scrollIntoViewAndClick(copyButtonLocator);

        singleSelectDropdown.selectOption("Move to another Bucket");
        singleSelectDropdown.selectOption(targetBucket.getBucketTypeForUI());
    }

    public void copyKeywordsToAnotherVersion(String sourceVersion, String sourceLocale, Enums.KeywordBucketType bucketToCopy, String targetVersion, String targetLocale) throws InterruptedException {
        String keywordBucketEditor = getKeywordBucketEditorXPath(sourceVersion, sourceLocale, bucketToCopy);
        By copyButtonLocator = By.xpath(keywordBucketEditor + "//button[text()='Move / Copy']");
        scrollIntoViewAndClick(copyButtonLocator);

        singleSelectDropdown.selectOption("Copy to another Version");
        singleSelectDropdown.selectOption(targetVersion + " Version - " + targetLocale);
    }

    public BulkEditSelectProductsPage copyKeywordsToOtherProducts(String sourceVersion, String sourceLocale, Enums.KeywordBucketType sourceBucket) throws InterruptedException {
        String keywordBucketEditor = getKeywordBucketEditorXPath(sourceVersion, sourceLocale, sourceBucket);
        By copyButtonLocator = By.xpath(keywordBucketEditor + "//button[text()='Move / Copy']");
        scrollIntoViewAndClick(copyButtonLocator);

        singleSelectDropdown.selectOption("Copy to other Products");
        return new BulkEditSelectProductsPage(driver);
    }

    public void copyEntireVersionKeywordsToAnotherVersion(String sourceVersion, String sourceLocale, String targetVersion, String targetLocale) throws InterruptedException {
        int panelNumber = getPanelNumberOfProductVersion(sourceVersion, sourceLocale);
        var copyButtonLocator = By.xpath("(//div[@data-qa='KeywordBucketsSection'])[" + panelNumber + "]/../../div[1]//button[text()='Move / Copy']");
        scrollIntoViewAndClick(copyButtonLocator);

        singleSelectDropdown.selectOption("Copy to another Version");
        singleSelectDropdown.selectOption(targetVersion + " Version - " + targetLocale);
    }

    public BulkEditSelectProductsPage copyEntireVersionKeywordsToOtherProduct(String sourceVersion, String sourceLocale) throws InterruptedException {
        int panelNumber = getPanelNumberOfProductVersion(sourceVersion, sourceLocale);
        var copyButtonLocator = By.xpath("(//div[@data-qa='KeywordBucketsSection'])[" + panelNumber + "]/../../div[1]//button[text()='Move / Copy']");
        scrollIntoViewAndClick(copyButtonLocator);

        singleSelectDropdown.selectOption("Copy to other Products");
        return new BulkEditSelectProductsPage(driver);
    }

    public int getNumberOfHighlightedKeywords() {
        By highlightedCellsLocator = By.cssSelector(".css-p4pm9n");
        return getElementCountMilliseconds(highlightedCellsLocator);
    }

    private String getPanelXPath(String productVersion, String locale) {
        return "//div[@data-qa='KeywordsTab']/div[2]/div[descendant::div[child::h4[text()='" + productVersion +
                " Version'] and child::span[text()='" + locale + "']]]";
    }

    private String getKeywordBucketEditorXPath(String productVersion, String locale, Enums.KeywordBucketType bucketName) {
        int panelNumber = getPanelNumberOfProductVersion(productVersion, locale);
        return "(//div[@data-qa='KeywordBucketsSection'])[" + panelNumber + "]" +
                "//div[@data-qa='KeywordBucketEditor' and descendant::p[contains(text(),'" + bucketName.getBucketTypeForUI() + "')]]";
    }

    private void deleteKeywords(String keywordToRemove, By deleteIconLocator) throws InterruptedException {
        searchForKeyword(keywordToRemove);
        List<WebElement> deleteIcons = findElementsVisibleMilliseconds(deleteIconLocator);

        if (!deleteIcons.isEmpty()) {
            for (WebElement deleteIconElement : deleteIcons) {
                scrollIntoViewAndClick(deleteIconElement);
            }
            clickApplyChangesButtonAndWaitForInvisibility();
        }
        searchForKeyword("");
    }

}
