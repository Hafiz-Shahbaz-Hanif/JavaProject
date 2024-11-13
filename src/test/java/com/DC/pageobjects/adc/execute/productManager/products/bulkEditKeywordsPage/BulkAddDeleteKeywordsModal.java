package com.DC.pageobjects.adc.execute.productManager.products.bulkEditKeywordsPage;

import com.DC.pageobjects.adc.navigationMenus.InsightsNavigationMenu;
import com.DC.utilities.sharedElements.SingleSelectDropdown;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.List;

public class BulkAddDeleteKeywordsModal extends InsightsNavigationMenu {
    private final String BULK_ADD_KEYWORDS_MODAL_XPATH = "//div[@data-qa='BulkKeywordAction']";
    private final String CLEAR_ALL_BUTTON_XPATH = "//button[text()='Clear All']";
    private final String ADD_KEYWORD_ROW_XPATH = "//div[@data-qa='AddKeywordRow']";

    private final By ADD_KEYWORD_BUCKET_BUTTON_LOCATOR = By.xpath("//p[text()='Add Keyword Bucket']");

    public SingleSelectDropdown singleSelectDropdown;

    public BulkAddDeleteKeywordsModal(WebDriver driver) {
        super(driver);
        findElementVisible(By.xpath(BULK_ADD_KEYWORDS_MODAL_XPATH), Duration.ofSeconds(3));
        singleSelectDropdown = new SingleSelectDropdown(driver);
    }

    public void selectBucket(int bucketPosition, String bucketName) throws InterruptedException {
        singleSelectDropdown.openDropdownMenu("Select Bucket", bucketPosition);
        singleSelectDropdown.selectOption(bucketName);
    }

    public void selectBucket(String bucketName) throws InterruptedException {
        singleSelectDropdown.openDropdownMenu("Select Bucket");
        singleSelectDropdown.selectOption(bucketName);
    }

    public void clearAllKeywordsFromBucket(int bucketPosition) {
        By clearAllButtonLocator = By.xpath("(//div[@data-qa='KeywordBucket'])[ " + bucketPosition + "]" + CLEAR_ALL_BUTTON_XPATH);
        clickElement(clearAllButtonLocator);
    }

    public void clearAllKeywordsFromBucket(String bucketName) {
        String bucketLocator = getBucketSectionLocator(bucketName);
        By clearAllButtonLocator = By.xpath(bucketLocator + CLEAR_ALL_BUTTON_XPATH);
        clickElement(clearAllButtonLocator);
    }

    public void clickAddKeywordButton(String bucketName) {
        String bucketLocator = getBucketSectionLocator(bucketName);
        By addKeywordButtonLocator = By.xpath(bucketLocator + ADD_KEYWORD_ROW_XPATH + "//button[contains(text(),'add')]");
        clickElement(addKeywordButtonLocator);
    }

    public void addKeywordToBucket(String bucketName, String keyword) {
        clickAddKeywordButton(bucketName);

        String bucketLocator = getBucketSectionLocator(bucketName);
        By addKeywordInputLocator = By.xpath(bucketLocator + ADD_KEYWORD_ROW_XPATH + "//input");
        setText(addKeywordInputLocator, keyword);

        saveNewKeyword(bucketName);
    }

    public void addKeywordsToBucket(String bucketName, List<String> keywords) {
        for (String keyword : keywords) {
            addKeywordToBucket(bucketName, keyword);
        }
    }

    public void saveNewKeyword(String bucketName) {
        String bucketLocator = getBucketSectionLocator(bucketName);
        By saveKeywordButtonLocator = By.xpath(bucketLocator + "//div[@data-qa='AddKeywordRow' and descendant::input]//button[text()='save']");
        clickElement(saveKeywordButtonLocator);
    }

    public void clickRemoveKeywordIcon(String bucketName, String keyword) {
        String keywordRowLocator = getKeywordRowLocator(bucketName, keyword);
        By editKeywordButtonLocator = By.xpath(keywordRowLocator + "//button[text()='delete']");
        clickElement(editKeywordButtonLocator);
    }

    public void clickEditKeywordIcon(String bucketName, String keyword) {
        String keywordRowLocator = getKeywordRowLocator(bucketName, keyword);
        By editKeywordButtonLocator = By.xpath(keywordRowLocator + "//button[text()='edit']");
        clickElement(editKeywordButtonLocator);
    }

    public void saveEditedKeyword(String bucketName, String keyword) {
        String keywordRowLocator = getKeywordRowLocator(bucketName, keyword);
        By saveKeywordButtonLocator = By.xpath(keywordRowLocator + "//button[text()='save']");
        clickElement(saveKeywordButtonLocator);
    }

    public void cancelEditingKeyword(String bucketName, String keyword) {
        String keywordRowLocator = getKeywordRowLocator(bucketName, keyword);
        By cancelKeywordButtonLocator = By.xpath(keywordRowLocator + "//button[text()='close']");
        clickElement(cancelKeywordButtonLocator);
    }

    public void clickAddKeywordBucketButton() {
        clickElement(ADD_KEYWORD_BUCKET_BUTTON_LOCATOR);
    }

    public int getNumberOfKeywordBucketSections() {
        By keywordBucketLocator = By.xpath("//div[@data-qa='KeywordBucket']");
        return getElementCount(keywordBucketLocator);
    }

    public String getCharactersKeywordsAndBytesCount(String bucketName) {
        String bucketLocator = getBucketSectionLocator(bucketName);
        By charactersKeywordsAndBytesCountLocator = By.xpath(bucketLocator + "//div[contains(@class,'CardContent')]/div[2]//p");
        return getTextFromElement(charactersKeywordsAndBytesCountLocator);
    }

    public <T> T clickSaveAndExitButton(Class<T> expectedPage) {
        By saveAndExitButtonLocator = By.xpath("//div[@data-qa='ModalWrapper']//button[text()='Save & Exit']");
        clickElement(saveAndExitButtonLocator);
        return getPage(expectedPage);
    }

    public <T> T clickCancelButton(Class<T> expectedPage) {
        By saveAndExitButtonLocator = By.xpath("//div[@data-qa='ModalWrapper']//button[text()='Cancel']");
        clickElement(saveAndExitButtonLocator);
        return getPage(expectedPage);
    }

    public List<String> getKeywordsInBucket(String bucketName) {
        String bucketLocator = getBucketSectionLocator(bucketName);
        By keywordsInBucketLocator = By.xpath(bucketLocator + "//div[@data-qa='KeywordRow']//h6");
        return getTextFromElementsMilliseconds(keywordsInBucketLocator);
    }

    private String getBucketSectionLocator(String bucketName) {
        return "//div[@data-qa='KeywordBucket' and descendant::button[text()='" + bucketName + "']]";
    }

    private String getKeywordRowLocator(String bucketName, String keyword) {
        return getBucketSectionLocator(bucketName) + "//div[@data-qa='KeywordRow' and descendant::h6[text()='" + keyword + "']]";
    }
}
