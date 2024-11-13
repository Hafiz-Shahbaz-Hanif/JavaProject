package com.DC.pageobjects.adc.execute.mediaManagement.flightDeck;

import com.DC.objects.mediaManagement.CampaignWizardKeywordTableData;
import com.DC.objects.mediaManagement.CampaignWizardSummaryTableData;
import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.utilities.enums.Enums;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.testng.Assert.fail;

public class CampaignWizardModal extends NetNewNavigationMenu {

    private final By CAMPAIGN_WIZARD_MODAL_HEADER = By.xpath("//div[@id='campaign-wizard']");
    private final By CAMPAIGN_WIZARD_STEPS = By.xpath("//div[@class='MuiBox-root css-0']//a");
    private final By CAMPAIGN_TYPE_CARD = By.xpath("//div[@id='campaign-type-selector']");
    private final By SPONSORED_PRODUCTS_CHECKBOX = By.xpath("//input[@name='campaignType']");
    private final By PRODUCT_SELECTOR_CARD = By.xpath("//div[@id='stepProducts']");
    private final By PRODUCT_SELECTOR_ASIN_INPUT = By.xpath("//div[@id='stepProducts']//input[@placeholder='Search a product, press Enter to begin search']");
    private final By PRODUCT_SELECTOR_ASINS = By.xpath("//button[@aria-label='Open in new tab']//parent::div");
    private final By PRODUCT_SELECTOR_ASINS_BUTTON = By.xpath("//button[@aria-label='Open in new tab']//parent::div//button");
    private final By ADD_SEGMENTATION_CARD = By.xpath("//div[@id='stepSegmentation']");
    private final By BRIEF_ASIN_TITLE_INPUT = By.xpath("//input[@id='AsinTitle']");
    private final By CLAIM_SEGMENTATION_DROPDOWN = By.xpath("(//div[@id='campaign-segmentation']//div[@role='button'])[1]");
    private final By BRAND_SEGMENTATION_DROPDOWN = By.xpath("(//div[@id='campaign-segmentation']//div[@role='button'])[2]");
    private final By IO_SEGMENTATION_DROPDOWN = By.xpath("(//div[@id='campaign-segmentation']//div[@role='button'])[3]");
    private final By AUTO_TARGETING_BAR = By.xpath("//h4[text()='Auto']");
    private final By BRANDED_TARGETING_BAR = By.xpath("//h4[text()='Branded']");
    private final By NON_BRANDED_TARGETING_BAR = By.xpath("//h4[text()='Non Branded']");
    private final By COMPETITOR_TARGETING_BAR = By.xpath("//h4[text()='Competitor']");
    private final By TARGETING_SECTION = By.xpath("//div[@id='campaign-targeting']");
    private final By ADD_KEYWORD_MODAL = By.xpath("//div[@id='ModalContent']");
    private final By ENTER_KEYWORDS_INPUT_IN_MODAL = By.xpath("//div[@id='ModalContent']//input[@id='enter-keywords']");
    private final By SET_DEFAULT_BID_INPUT_IN_MODAL = By.xpath("//div[@id='ModalContent']//input[@id='float-Two-decimal-input']");
    private final By ADD_BUTTON_IN_MODAL = By.xpath("//div[@class='MuiBox-root css-1mskdau']//button");
    private final By KEYWORD_ADDED_SUCCESS_MESSAGE = By.xpath("//div[@class='MuiAlert-message css-1xsto0d']");
    private final By CAMPAIGN_BUDGET_FIELD = By.xpath("//div[@class='MuiBox-root css-1gnvw7h']//input[@placeholder='Enter value']");
    private final By BIDDING_STRATEGY_DOWN_ONLY_SELECTION = By.xpath("//div[@class='MuiBox-root css-1gnvw7h']//div[@role='radiogroup']//input[@value='downOnly']");
    private final By BIDDING_STRATEGY_UP_AND_DOWN_SELECTION = By.xpath("//div[@class='MuiBox-root css-1gnvw7h']//div[@role='radiogroup']//input[@value='upDown']");
    private final By DAY_PARTING_FIELD = By.xpath("//div[@class='MuiGrid-root MuiGrid-container css-1d3bbye']//div[@aria-haspopup='listbox']");
    private final By CAMPAIGN_WIZARD_LAUNCH_MODAL = By.xpath("//div[@class='MuiBox-root css-1gfgjpr']//h4");
    private final By LAUNCH_CAMPAIGN_BUTTON = By.xpath("//div[@class='MuiBox-root css-yghhjj']//button");
    private final By LAUNCH_CAMPAIGN_CLOSE_BUTTON = By.xpath("//div[@class='MuiBox-root css-1gfgjpr']//span");
    private final By SEARCH_PRODUCT = By.xpath("//div[@col-id='asinTitle']//div[contains(text(),'B01GEIMR2K')]");
    private final By X_ICON = By.xpath("//span[contains(text(),'close')]");

    Map<Enums.KeywordMatchType, String> keywordMatchTypes = new HashMap<>() {{
        put(Enums.KeywordMatchType.Exact, "Exact");
        put(Enums.KeywordMatchType.Phrase, "Phrase");
        put(Enums.KeywordMatchType.Broad, "Broad");
        put(Enums.KeywordMatchType.NegativeExact, "Negative Exact");
        put(Enums.KeywordMatchType.NegativePhrase, "Negative Phrase");
    }};

    Map<Enums.TargetingType, String> keywordTargetingTypes = new HashMap<>() {{
        put(Enums.TargetingType.Auto, "auto");
        put(Enums.TargetingType.Manual, "manual");
    }};

    public CampaignWizardModal(WebDriver driver) {
        super(driver);
        //findElementVisible(CAMPAIGN_WIZARD_MODAL_HEADER);
    }

    public void clickSponsoredProductsCheckbox() throws InterruptedException {
        click(SPONSORED_PRODUCTS_CHECKBOX);
    }

    public boolean areCampaignWizardStepsVisible() {
        return isElementVisible(CAMPAIGN_WIZARD_STEPS);
    }

    public List<String> getCampaignWizardSteps() {
        return getTextFromElements(findElementsVisible(CAMPAIGN_WIZARD_STEPS));
    }

    public void clickCampaignWizardStep(String step) throws InterruptedException {
        click(By.xpath("//div[@class='MuiBox-root css-0']//a//span[text()='" + step + "']"));
    }

    public void setSegmentationBriefAsinTitleField(String value) {
        sendKeys(BRIEF_ASIN_TITLE_INPUT, value);
    }

    public void openSegmentationDropdown(String segmentToSelect) throws InterruptedException {
        By dropdownExpanded = By.xpath("//ul[@role='listbox']//li");
        if (!isElementVisible(dropdownExpanded)) {
            switch (segmentToSelect) {
                case ("Claim"):
                    click(CLAIM_SEGMENTATION_DROPDOWN);
                    break;
                case ("Brand"):
                    click(BRAND_SEGMENTATION_DROPDOWN);
                    break;
                case ("IO Segment"):
                    click(IO_SEGMENTATION_DROPDOWN);
                    break;
            }
        }
    }

    public String getSelectedSegmentationValue(String segmentToSelect) {
        switch (segmentToSelect) {
            case ("Claim"):
                return getTextFromElement(CLAIM_SEGMENTATION_DROPDOWN);
            case ("Brand"):
                return getTextFromElement(BRAND_SEGMENTATION_DROPDOWN);
            case ("IO Segment"):
                return getTextFromElement(IO_SEGMENTATION_DROPDOWN);
        }
        return "";
    }

    public List<String> getAllValuesInSegmentationDropdownMenu(String segmentToSelect) throws InterruptedException {
        openSegmentationDropdown(segmentToSelect);
        return getTextFromElements(findElementsVisible(By.xpath("//ul[@role='listbox']//li")));
    }

    public void openSegmentationDropdownAndSelectValue(String segmentToSelect, String value) throws InterruptedException {
        openSegmentationDropdown(segmentToSelect);
        click(By.xpath("//ul[@role='listbox']//li[text()='" + value + "']"));
        waitForElementToBeInvisible(By.xpath("//ul[@role='listbox']//li"));
    }

    public boolean isSponsoredProductsCheckboxSelected() {
        return isElementSelected(SPONSORED_PRODUCTS_CHECKBOX);
    }

    public boolean isCampaignTypeSelectCardVisible() {
        return isElementVisible(CAMPAIGN_TYPE_CARD);
    }

    public boolean isProductsSelectorCardVisible() {
        return isElementVisible(PRODUCT_SELECTOR_CARD);
    }

    public void searchProductSelectorByASIN(String asinToSearch) {
        setTextAndHitEnter(PRODUCT_SELECTOR_ASIN_INPUT, asinToSearch);
    }

    public List<String> getAllAsinsInTable() {
        return getTextFromElements(findElementsVisible(PRODUCT_SELECTOR_ASINS));
    }

    public void selectASINCheckbox(int row) throws InterruptedException {
        By asinCheckbox = By.xpath("(//div[@role='rowgroup']//div[@ref='eCheckbox'])[" + row + "]");
        click(asinCheckbox);
    }

    public boolean isAsinCheckBoxSelected(int row) {
        By asinCheckbox = By.xpath("(//div[@role='row' and ancestor::div[@class='ag-center-cols-container']])[" + row + "]//input");
        String checkStatus = getAttribute(asinCheckbox, "aria-label");
        if (checkStatus.contains("unchecked")) {
            return false;
        }
        return true;
    }

    public boolean isAddSegmentationCardVisible() {
        return isElementVisible(ADD_SEGMENTATION_CARD);
    }

    public boolean doesAsinRedirectToSameASINUrl(String asinValue) {
        if (!isElementVisible(PRODUCT_SELECTOR_ASINS_BUTTON)) {
            scrollIntoView(PRODUCT_SELECTOR_ASINS_BUTTON);
        }

        List<WebElement> rpcs = findElementsVisible(PRODUCT_SELECTOR_ASINS_BUTTON);
        for (int i = 0; i < rpcs.size(); i++) {
            WebElement asin = findElementsVisible(PRODUCT_SELECTOR_ASINS_BUTTON).get(i);
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            jsExecutor.executeScript("arguments[0].click();", asin);
            waitForNewTabToOpen(2);
            switchToTab(2, 1);
            UI_LOGGER.info("Current URL is " + getCurrentUrl() + " and it should contain " + asinValue);

            boolean isOptionRedirectedToSameRPC;
            isOptionRedirectedToSameRPC = getCurrentUrl().contains(asinValue);

            if (!isOptionRedirectedToSameRPC) {
                fail("Option " + asinValue + " did not redirect to the same RPC");
            }
            closeCurrentTabAndSwitchToMainTab();
        }
        return true;
    }

    public boolean isTargetingSectionVisible() {
        return isElementVisible(TARGETING_SECTION);
    }

    public boolean isKeywordTargetingCheckBoxSelected(Enums.TargetingType targetingType) {
        return isElementVisible((By.xpath("//div[@id='" + keywordTargetingTypes.get(targetingType) + "-targeting']//*[local-name() = 'svg' and @data-testid='CheckBoxIcon']")));
    }

    public void selectTargetingType(Enums.TargetingType targetingType) throws InterruptedException {
        if (!isKeywordTargetingCheckBoxSelected(targetingType)) {
            click(By.xpath("//div[@id='" + keywordTargetingTypes.get(targetingType) + "-targeting']//input"));
        }
    }

    public void deselectTargetingType(Enums.TargetingType targetingType) throws InterruptedException {
        if (isKeywordTargetingCheckBoxSelected(targetingType)) {
            click(By.xpath("//div[@id='" + keywordTargetingTypes.get(targetingType) + "-targeting']//input"));
        }
    }

    public boolean isAutoKeywordsBarVisible() {
        return isElementVisible(AUTO_TARGETING_BAR);
    }

    public boolean isBrandedKeywordsBarVisible() {
        return isElementVisible(BRANDED_TARGETING_BAR);
    }

    public boolean isNonBrandedKeywordsBarVisible() {
        return isElementVisible(NON_BRANDED_TARGETING_BAR);
    }

    public boolean isCompetitorKeywordsBarVisible() {
        return isElementVisible(COMPETITOR_TARGETING_BAR);
    }

    public void clickAddKeywordButton(String campaign) throws InterruptedException {
        click(By.xpath("//h4[text()='" + campaign +"']/parent::div/parent::div/parent::div/following-sibling::div//button[text()='Add Keyword']"));
    }

    public void enterKeywordInAddKeywordModal(String keyword) {
        sendKeys(ENTER_KEYWORDS_INPUT_IN_MODAL, keyword);
    }

    public void setDefaultBidInAddKeywordModal(String bid) {
        sendKeys(SET_DEFAULT_BID_INPUT_IN_MODAL, bid);
    }

    public void selectMatchTypeTextBoxInAddKeywordModal(Enums.KeywordMatchType matchType) throws InterruptedException {
        click((By.xpath("//div[@id='ModalContent']//span[text()='" + keywordMatchTypes.get(matchType) + "']")));
    }

    public void addKeywordToCampaign(String campaign, String bid, Enums.KeywordMatchType matchType, String keyword) throws InterruptedException {
        clickAddKeywordButton(campaign);
        waitForElementToBeEnabled(ADD_KEYWORD_MODAL, Duration.ofSeconds(2));
        setDefaultBidInAddKeywordModal(bid);
        selectMatchTypeTextBoxInAddKeywordModal(matchType);
        enterKeywordInAddKeywordModal(keyword);
        click(ADD_BUTTON_IN_MODAL);
        waitForElementToBeInvisible(ADD_KEYWORD_MODAL);
    }

    public boolean isKeywordAddedSuccessMessageDisplayed() {
        return isElementVisible(KEYWORD_ADDED_SUCCESS_MESSAGE);
    }

    public boolean isBrandTargetingSectionVisible() {
        return isElementVisible(BRANDED_TARGETING_BAR);
    }

    public boolean isNonBrandedTargetingSectionVisible() {
        return isElementVisible(NON_BRANDED_TARGETING_BAR);
    }

    public boolean isCompetitorTargetingSectionVisible() {
        return isElementVisible(COMPETITOR_TARGETING_BAR);
    }

    public void openKeywordTypeBar(String campaign) throws InterruptedException {
        click(By.xpath("//h4[text()='" + campaign + "']/parent::div/following-sibling::div//input"));
    }

    public List<CampaignWizardKeywordTableData> getAllKeywordDataFromTable(String campaign) {
        List<CampaignWizardKeywordTableData> keywordTableData = new ArrayList<>();
        int rowLocator = 0;
        switch (campaign) {
            case("Branded"):
                rowLocator = 1;
                break;
            case("Non Branded"):
                rowLocator = 3;
                break;
            case("Competitor"):
                rowLocator = 5;
                break;
        }
        int rows= getElementCount(By.xpath("(//div[@id='keyword-table-inner']//div[@class='ag-center-cols-container'])[" + rowLocator + "]"));
        for (int i = 1; i <= rows; i++) {
            CampaignWizardKeywordTableData rowData = new CampaignWizardKeywordTableData(
                    getTextFromElement(getKeywordColumnXpath("keyword", rowLocator, i)),
                    getTextFromElement(getKeywordColumnXpath("matchType", rowLocator, i)),
                    getTextFromElement(getKeywordColumnXpath("bid", rowLocator, i))
            );
            keywordTableData.add(rowData);
        }
        return keywordTableData;
    }

    public By getKeywordColumnXpath(String columnId, int rowLocator, int row) {
        return By.xpath("((//div[@id='keyword-table-inner']//div[@class='ag-center-cols-container'])[" + rowLocator + "]//div[@col-id='" + columnId + "'])[" + row + "]");
    }

    public String getCampaignBudgetFieldValue() {
        return getAttribute(CAMPAIGN_BUDGET_FIELD, "value");
    }

    public void setCampaignBudget(String budget) {
        setText(CAMPAIGN_BUDGET_FIELD, budget);
    }

    public boolean isBiddingStrategySelected(String biddingStrategy) {
        return isElementVisible(By.xpath("//div[@aria-labelledby='radio-buttons-group-set-bidding-strategy']//span[contains(@class, 'checked')]/following-sibling::span[text()='" + biddingStrategy + "']"));
    }

    public void selectBiddingStrategy(boolean upDownTrue) throws InterruptedException {
        if (!upDownTrue) {
            click(BIDDING_STRATEGY_DOWN_ONLY_SELECTION);
        } else {
            click(BIDDING_STRATEGY_UP_AND_DOWN_SELECTION);
        }
    }

    public boolean isDayPartingFieldVisible() {
        return isElementVisible(DAY_PARTING_FIELD);
    }

    public void selectDayPartingConfig(String config) throws InterruptedException {
        click(DAY_PARTING_FIELD);
        click(By.xpath("//ul[@role='listbox']//li[text()='" + config + "']"));
    }
    
    public boolean isCampaignWizardLaunchModalPopUpVisible() {
        return isElementVisible(CAMPAIGN_WIZARD_LAUNCH_MODAL);
    }
    
    public CampaignWizardSummaryTableData getCampaignWizardSelectedSettings() {
        var baseXPath = "(//div[@class='MuiGrid-root MuiGrid-container css-sow6z2']//p/parent::div/following-sibling::div)";
        CampaignWizardSummaryTableData tableData = new CampaignWizardSummaryTableData();
        tableData.campaignName = findElementVisible(By.xpath("(//div[@class='MuiGrid-root MuiGrid-container css-sow6z2']//p/parent::div/following-sibling::div)[1]")).getAttribute("innerHTML");
        tableData.campaignBudget = findElementVisible(By.xpath( baseXPath +"[3]")).getAttribute("innerHTML");
        tableData.brand = getTextFromElement(By.xpath(baseXPath + "[5]"));
        tableData.claim = getTextFromElement(By.xpath(baseXPath + "[7]"));
        tableData.ioSegment = getTextFromElement(By.xpath(baseXPath + "[9]"));
        return tableData;
    }

    public boolean isLaunchCampaignButtonEnabled() {
        return isElementEnabled(LAUNCH_CAMPAIGN_BUTTON);
    }

    public void clickLaunchCampaignButton() throws InterruptedException {
        click(LAUNCH_CAMPAIGN_BUTTON);
    }

    public void closeLaunchCampaignModal() throws InterruptedException {
        click(LAUNCH_CAMPAIGN_CLOSE_BUTTON);
    }

    public void clickCrossIcon() throws InterruptedException {
        UI_LOGGER.info("Verify Search Product is Displayed");
        isElementVisible(SEARCH_PRODUCT);
        UI_LOGGER.info("Click on X icon");
        click(X_ICON);
        Assert.assertTrue(isElementNotVisible(SEARCH_PRODUCT));
        UI_LOGGER.info("Search Product not visible");
    }

    public boolean verifyRandomCharacterSearched(){
        boolean f = isElementNotVisible(SEARCH_PRODUCT);
        if (f){
            UI_LOGGER.info("Random Character are not accepted by search field");
        }
        return f;
    }
}
