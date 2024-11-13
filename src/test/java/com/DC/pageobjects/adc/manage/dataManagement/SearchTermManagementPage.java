package com.DC.pageobjects.adc.manage.dataManagement;

import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.pageobjects.filters.DCFilters;
import com.DC.utilities.CommonFeatures;
import org.openqa.selenium.*;
import org.testng.Assert;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchTermManagementPage extends NetNewNavigationMenu {

    public DCFilters filters;
    public CommonFeatures commonFeatures;
    private static final By DATA_RETAILERS = By.xpath("//div[contains(text(),'Data')]");
    private static final By ASSOCIATED_RETAILER_PLATFORMS = By.xpath("//span[contains(@id, 'retailerPlatforms')]");
    private static final By ASSOCIATED_RETAILERS_TOOLTIP = By.xpath("//div[@role='tooltip']/div");
    private static final By STM_HEADER = By.xpath("//h4[text()='Manage Search Terms']");
    private static final By SEARCH_TERM = By.xpath("//div[@class='ag-header-row ag-header-row-column']/div[@col-id='searchTerm']");
    private static final By PRIORITY = By.xpath("//div[@class='ag-header-row ag-header-row-column']/div[@col-id='priorityTerm']");
    private static final By ASSOCIATED_RETAILERS = By.xpath("//div[@class='ag-header-row ag-header-row-column']/div[@col-id='retailerPlatforms']");
    private static final By FREQUENCY = By.xpath("//div[@class='ag-header-row ag-header-row-column']//h3[@aria-label='Scrape Frequency']");
    private static final By PAGINATION = By.xpath("//nav[@aria-label='pagination navigation']/following-sibling::div");
    private static final By EDIT_SEARCH_TERM_ICON = By.xpath("//div[@row-index='0']//span[@class='material-symbols-rounded'][text()='edit']");
    private static final By EDIT_POPUP_HEADER = By.xpath("//p[text()='Edit Search Term']");
    private static final By UPDATE_BUTTON = By.xpath("//button[text()='Update']");
    private static final By CLOSE_ALERT = By.xpath("//button[@title='Close']");
    private static final By DELETE_SEARCH_TERM_ICON = By.xpath("//div[@row-index='0']//span[@class='material-symbols-rounded'][text()='delete']");
    private static final By CONTINUE_BUTTON = By.xpath("//label[text()='Continue']");
    private static final By ADD_A_SEARCH_TERM_BUTTON = By.xpath("//span[text()='add']/parent::button");
    private static final By SEARCH_TERM_TEXT_BOX = By.xpath("//p[text()='Search Term*']/following-sibling::div//input");
    private static final By FREQUENCY_DROPDOWN = By.id("add-edit-popup-freq-select");
    private static final By PRIORITY_TOGGLE = By.xpath("//p[@id='add-edit-popup-priority-title']/following-sibling::span/span");
    private static final By SEARCH_PAGES_DROPDOWN = By.xpath("//div[text()='1st page only']");
    private static final By ADD_SEARCH_TERM_CREATE_BUTTON = By.xpath("//button[text()='Create']");
    private static final By OPTIONAL_FIELD_ST_GROUPS = By.xpath("//input[@id='groups-dropdown']");
    private static final By ADD_SEARCH_TERM_CANCEL_BUTTON = By.id("add-edit-popup-cancel-button");
    private static final By UPLOAD_BUTTON = By.xpath("//span[text()='upload']/parent::button");
    private static final By DOWNLOAD_BUTTON = By.xpath("//span[text()='download']/parent::button");
    private static final By ERROR_MESSAGE_ADD_SEARCH_TERM = By.xpath("//span[text()='This field is required']");
    private static final By ERROR_MESSAGE_RETAILERS = By.xpath("//span[text()='Select at least one retailer']");
    private static final By UPLOAD_POPUP_HEADER = By.xpath("//p[text()='Bulk Import Terms Or Bulk Delete Terms?']");
    private static final By UPLOAD_POPUP_CANCEL_BUTTON = By.xpath("//div[@role='dialog']//div//button[text()='Cancel']");
    private static final By UPLOAD_POPUP_DELETE_BUTTON = By.xpath("//button[text()='Delete']");
    private static final By UPLOAD_POPUP_IMPORT_BUTTON = By.xpath("//button[text()='Import']");
    private static final By IMPORT_POPUP_HEADER = By.xpath("//p[text()='Import Search Terms?']");
    private static final By DELETE_POPUP_HEADER = By.xpath("//p[text()='Bulk Import Terms Or Bulk Delete Terms?']");
    private static final By SEARCH_TERM_HEADER = By.xpath("//a[text()='Search Term Management']");
    private static final By SEARCH_TERM_GROUPS = By.xpath("//input[@id='groups-dropdown']//following-sibling::div//button[@title='Open']");
    private static final By GRID_ROW = By.xpath("(//div[@role='row'])[3]");
    private static final By APPLY_BUTTON = By.xpath("(//div[@role='row'])[3]//div[@col-id='searchTerm']");
    private static final By SEARCH_TERM_PAGE_SEARCH_BAR = By.xpath("//input[@placeholder='Search by Search Term']");
    private final By CREATE_NEW_GROUP_BUTTON = By.xpath("//li[@role='option']");
    public static final By SEARCH_TERM_ACTION_SUCCESS_MSG = By.xpath("//div[contains(@class, 'MuiAlert-message') and contains(text(), 'successfully')]");
    public static final By SEARCH_TERM_ACTION_ERROR_MSG = By.xpath("//div[contains(@class, 'MuiAlert-message') and contains(text(), 'error')]");
    public static final By SEARCH_TERM_SEARCH_BAR = By.xpath("//input[@placeholder='Search by Search Term']");
    public static final By SEARCH_TERM_SEARCH_ICON = By.xpath("//input[@placeholder='Search by Search Term']/../span");
    public static final By SEARCH_TERM_EXIST_NOTIFICATION = By.xpath("//span[text()='Group already exists']");

    public SearchTermManagementPage(WebDriver driver) {
        super(driver);
        findElementVisible(STM_HEADER);
        filters = new DCFilters(driver);
        commonFeatures = new CommonFeatures(driver);
    }

    public List<String> getDataRetailers() {
        WebElement dataRetailersElement = findElementVisible(DATA_RETAILERS);
        String[] retailers = dataRetailersElement.getText().split(": |\\n");
        List<String> retailerList = new ArrayList<>();
        if (retailers.length > 1) {
            String[] retailerNames = retailers[1].split(",");
            retailerList.addAll(Arrays.asList(retailerNames));
        }
        return retailerList;
    }

    // this is not completed yet
    public List<String> getAssociatedRetailersInGrid() throws InterruptedException {
        List<String> associatedRetailers = new ArrayList<>();
        List<WebElement> retailers = findElementsVisible(ASSOCIATED_RETAILER_PLATFORMS, "Associated Retailers");
        for (WebElement retailer : retailers) {
            if (retailer.getText().contains("Retailers")) {
                hoverOverElement(retailer);
                WebElement tooltipElement = findElementVisible(ASSOCIATED_RETAILERS_TOOLTIP);
                String tooltipText = tooltipElement.getText();
                List<String> tooltipList = Arrays.asList(tooltipText.split(","));
                associatedRetailers.addAll(tooltipList);
            } else {

            }

        }

        return associatedRetailers;

    }

    public boolean isSearchTermColumnHeaderDisplayed() {
        return isElementVisible(SEARCH_TERM);
    }

    public boolean isPriorityColumnHeaderDisplayed() {
        return isElementVisible(PRIORITY);
    }

    public boolean isAssociatedRetailersColumnHeaderDisplayed() {
        return isElementVisible(ASSOCIATED_RETAILERS);
    }

    public boolean isFrequencyColumnHeaderDisplayed() {
        return isElementVisible(FREQUENCY);
    }

    public boolean isEditSearchTermIconDisplayed() {
        return isElementVisible(EDIT_SEARCH_TERM_ICON);
    }

    public boolean isDeleteSearchTermIconDisplayed() {
        return isElementVisible(DELETE_SEARCH_TERM_ICON);
    }

    public void clickEditSearchTermIcon() throws InterruptedException {
        click(EDIT_SEARCH_TERM_ICON);
        Thread.sleep(2000);
    }

    public boolean isEditSearchTermPopupIsDisplayed() {
        return isElementVisible(EDIT_POPUP_HEADER);
    }

    public boolean checkIfCorrectSearchTermChosen(String searchTerm) {
        WebElement searchTermTextBoxElement = findElementVisible(SEARCH_TERM_TEXT_BOX);
        String searchTermTextBoxValue = searchTermTextBoxElement.getAttribute("value");
        UI_LOGGER.info("Search term text box value is: " + searchTermTextBoxValue);
        return searchTermTextBoxValue.equals(searchTerm);
    }

    public void clickUpdateButton() throws InterruptedException {
        click(UPDATE_BUTTON);
        Thread.sleep(2000);
    }

    public String getAlertMessageText(String expectedAlertMessage) {
        findElementVisible(By.xpath("//div[text()='" + expectedAlertMessage + "']"), Duration.ofSeconds(20));
        WebElement alertMessageElement = findElementVisible(By.xpath("//div[text()='" + expectedAlertMessage + "']"));
        String alertMessageText = alertMessageElement.getText();
        UI_LOGGER.info("Alert message text is: " + alertMessageText);
        return alertMessageText;
    }

    public void closeAlert() throws InterruptedException {
        click(CLOSE_ALERT);
    }

    public void clickDeleteSearchTermIcon() throws InterruptedException {
        click(DELETE_SEARCH_TERM_ICON);
    }

    public boolean isDeleteSearchTermPopupIsDisplayed() {
        WebElement deletePopupElement = findElementVisible(By.xpath("//p[text()='Delete Search Terms?']"));
        String deletePopupText = deletePopupElement.getText();
        UI_LOGGER.info("Delete popup text is: " + deletePopupText);
        return deletePopupText.equals("Delete Search Terms?");
    }

    public void clickContinueButton() throws InterruptedException {
        click(CONTINUE_BUTTON);
    }

    public boolean verifyAddSearchTermPopupIsDisplayed() throws InterruptedException {
        Thread.sleep(2000);
        WebElement addSearchTermPopupElement = findElementVisible(By.xpath("//p[text()='Add Search Term']"));
        String addSearchTermPopupText = addSearchTermPopupElement.getText();
        UI_LOGGER.info("Add a Search Term popup text is: " + addSearchTermPopupText);
        return addSearchTermPopupText.equals("Add Search Term");
    }

    public void clickAddASearchTermButton() throws InterruptedException {
        click(ADD_A_SEARCH_TERM_BUTTON);
    }

    public void addNameToSearchTerm(String searchTerm) {
        setText(SEARCH_TERM_TEXT_BOX, searchTerm);
    }

    public void chooseRetailer(List<String> retailer) {
        List<WebElement> retailersCheckboxLocator = findElementsVisible(By.xpath("//div[@id='add-edit-popup-form-group']/label//input/.."));
        for (WebElement retailerElement : retailersCheckboxLocator) {
            if (retailerElement.getAttribute("class").contains("Mui-checked")) {
                retailerElement.click();
            }
        }
        List<WebElement> retailers = findElementsVisible(By.xpath("//div[@class='MuiFormGroup-root css-1h7anqn']/label/span/following-sibling::span"));
        for (WebElement retailerName : retailers) {
            if (retailer.contains(retailerName.getText())) {
                retailerName.click();
            }
        }
    }

    public void createNewSearchTerm(String searchTerm, List<String> retailer, String frequency, String checkPriorityToggle, List<String> searchTermGroups) throws InterruptedException {

        findElementVisible(GRID_ROW, Duration.ofSeconds(30));
        clickAddASearchTermButton();
        addNameToSearchTerm(searchTerm);
        chooseRetailer(retailer);
        click(FREQUENCY_DROPDOWN);
        chooseFrequency(frequency);

        if (checkPriorityToggle.equals("t")) {
            if (!isElementChecked(PRIORITY_TOGGLE, Duration.ofSeconds(30))) {
                click(PRIORITY_TOGGLE);
                UI_LOGGER.info("Clicked on priority toggle");
            } else {
                UI_LOGGER.info("Priority toggle is already checked");
            }
        } else {
            if (isElementChecked(PRIORITY_TOGGLE, Duration.ofSeconds(30))) {
                click(PRIORITY_TOGGLE);
                UI_LOGGER.info("Clicked on priority toggle");
            } else {
                UI_LOGGER.info("Priority toggle is already unchecked");
            }
        }

        createNewSearchTermGroup(searchTermGroups);

        clickCreateButton();
        UI_LOGGER.info("Clicked on create button");
    }

    public void deleteSearchTerm(String searchTerm) throws InterruptedException {
        findSearchTermOnUi(searchTerm);
        clickDeleteSearchTermIcon();
        clickContinueButton();
    }

    public void findSearchTermOnUi(String searchTerm) throws InterruptedException {
        setText(SEARCH_TERM_SEARCH_BAR, searchTerm);
        click(SEARCH_TERM_SEARCH_ICON);
    }

    public void updateSearchTerm(String searchTerm) throws InterruptedException {
        findSearchTermOnUi(searchTerm);
        clickEditSearchTermIcon();
        clickUpdateButton();
    }

    public boolean isFrequencyDropdownDisplayed() {
        return isElementVisible(FREQUENCY_DROPDOWN);
    }

    public boolean verifyFrequencyDropdownOptions() throws InterruptedException {
        click(FREQUENCY_DROPDOWN);
        List<WebElement> frequencyOptions = findElementsVisible(By.xpath("//ul[@class='MuiList-root MuiList-padding MuiMenu-list css-r8u8y9']/li"));
        for (WebElement frequencyOption : frequencyOptions) {
            if (frequencyOption.getText().contains("HOURLY") || frequencyOption.getText().contains("DAILY")) {
                UI_LOGGER.info("Frequency option is: " + frequencyOption.getText());
            } else {
                UI_LOGGER.info("Frequency option is not present");
                return false;
            }
        }
        return true;
    }

    public boolean getDefaultValueOfFrequencyDropdown() {

        WebElement frequencyElement = findElementVisible(By.xpath("//p[text()='Frequency*']/following-sibling::div"));
        String frequencyText = frequencyElement.getText();
        UI_LOGGER.info("Frequency text is: " + frequencyText);
        return frequencyText.equals("DAILY");
    }

    public void chooseFrequency(String frequency) {
        List<WebElement> frequencyOptions = findElementsVisible(By.xpath("//ul[@class='MuiList-root MuiList-padding MuiMenu-list css-r8u8y9']/li"));
        for (WebElement frequencyOption : frequencyOptions) {
            if (frequencyOption.getText().contains(frequency)) {
                frequencyOption.click();
                UI_LOGGER.info(frequencyOption.getText() + " is selected");
                break;
            }
        }
    }

    public boolean verifyToggleIsDisplayed() {
        return isElementVisible(PRIORITY_TOGGLE);
    }

    public boolean isSearchPagesDropdownDisplayed() {
        return isElementVisible(SEARCH_PAGES_DROPDOWN);
    }

    public boolean verifySearchPagesDropdownOptions() throws InterruptedException {
        click(SEARCH_PAGES_DROPDOWN);

        List<WebElement> searchPagesOptions = findElementsVisible(By.xpath("//ul[@class='MuiList-root MuiList-padding MuiMenu-list css-r8u8y9']/li"));
        for (WebElement searchPagesOption : searchPagesOptions) {
            if (searchPagesOption.getText().contains("1st page only") || searchPagesOption.getText().contains("1st and 2nd pages")) {
                UI_LOGGER.info("Search pages option is: " + searchPagesOption.getText());
            } else {
                UI_LOGGER.info("Search pages option is not present");
                return false;
            }
        }
        return true;
    }

    public boolean verifyFirstPageOnlyOptionIsSelectedByDefault() {

        WebElement firstPageOnlyOptionElement = findElementVisible(By.xpath("//p[text()='Search Pages*']/following-sibling::div"));
        String firstPageOnlyOptionText = firstPageOnlyOptionElement.getText();
        UI_LOGGER.info("Page option text is: " + firstPageOnlyOptionText);
        return firstPageOnlyOptionText.equals("1st page only");
    }

    public void selectPagesOption(String pagesOption) {

        List<WebElement> searchPagesOptions = findElementsVisible(By.xpath("//ul[@class='MuiList-root MuiList-padding MuiMenu-list css-r8u8y9']/li"));
        for (WebElement searchPagesOption : searchPagesOptions) {
            if (searchPagesOption.getText().contains(pagesOption)) {
                searchPagesOption.click();
                UI_LOGGER.info(searchPagesOption.getText() + " is selected");
                break;
            }
        }
    }

    public boolean isCreateButtonDisplayed() {
        return isElementVisible(ADD_SEARCH_TERM_CREATE_BUTTON);
    }

    public void clickCreateButton() throws InterruptedException {
        click(ADD_SEARCH_TERM_CREATE_BUTTON);
        Thread.sleep(1000);
    }

    public boolean isSearchTermGroupsFieldDisplayed() {
        return isElementVisible(OPTIONAL_FIELD_ST_GROUPS);
    }

    public boolean isCancelButtonDisplayed() {
        return isElementVisible(ADD_SEARCH_TERM_CANCEL_BUTTON);
    }

    public void clickCancelButton() throws InterruptedException {
        click(ADD_SEARCH_TERM_CANCEL_BUTTON);
    }

    public boolean isAddASearchTermButtonDisplayed() {
        return isElementVisible(ADD_A_SEARCH_TERM_BUTTON);
    }

    public boolean isUploadButtonDisplayed() {
        return isElementVisible(UPLOAD_BUTTON);
    }

    public boolean isDownloadButtonDisplayed() {
        return isElementVisible(DOWNLOAD_BUTTON);
    }

    public boolean verifyErrorMessagesAreDisplayed() {
        return isElementVisible(ERROR_MESSAGE_ADD_SEARCH_TERM) && isElementVisible(ERROR_MESSAGE_RETAILERS);
    }

    public boolean isErrorMessageIsCorrectForSearchTerm() {
        WebElement errorMessageElement = findElementVisible(ERROR_MESSAGE_ADD_SEARCH_TERM);
        String errorMessageText = errorMessageElement.getText();
        UI_LOGGER.info("Error message text is: " + errorMessageText);
        return errorMessageText.equals("This field is required");
    }

    public boolean isErrorMessageIsCorrectForRetailers() {
        WebElement errorMessageElement = findElementVisible(ERROR_MESSAGE_RETAILERS);
        String errorMessageText = errorMessageElement.getText();
        UI_LOGGER.info("Error message text is: " + errorMessageText);
        return errorMessageText.equals("Select at least one retailer");
    }

    public void clickUploadButton() throws InterruptedException {
        click(UPLOAD_BUTTON);
        Thread.sleep(1000);
    }

    public boolean isUploadSearchTermsPopupIsDisplayed() {
        return isElementVisible(UPLOAD_POPUP_HEADER);
    }

    public boolean isUploadSearchTermsPopUpHeaderCorrect() {
        WebElement popUpHeaderElement = findElementVisible(UPLOAD_POPUP_HEADER);
        String popUpHeaderText = popUpHeaderElement.getText();
        UI_LOGGER.info("Pop up header text is: " + popUpHeaderText);
        return popUpHeaderText.equals("Bulk Import Terms Or Bulk Delete Terms?");
    }

    public boolean verifyAllThreeButtonsAreDisplayedInUploadPopup() {
        return isElementVisible(UPLOAD_POPUP_CANCEL_BUTTON) && isElementVisible(UPLOAD_POPUP_DELETE_BUTTON) && isElementVisible(UPLOAD_POPUP_IMPORT_BUTTON);
    }

    public void clickImportButtonInUploadPopup() throws InterruptedException {
        click(UPLOAD_POPUP_IMPORT_BUTTON);
    }

    public boolean isImportSearchTermsPopupIsDisplayed() {
        return isElementVisible(IMPORT_POPUP_HEADER);
    }

    public boolean isImportSearchTermsPopUpHeaderCorrect() {
        WebElement popUpHeaderElement = findElementVisible(IMPORT_POPUP_HEADER);
        String popUpHeaderText = popUpHeaderElement.getText();
        UI_LOGGER.info("Pop up header text is: " + popUpHeaderText);
        return popUpHeaderText.equals("Import Search Terms?");
    }

    public void clickCancelButtonInImportPopup() {
        try {
            WebElement cancelButtonElement = findElementVisible(By.xpath("//div[@class='MuiDialogActions-root MuiDialogActions-spacing css-1n9ly98']/button"));
            cancelButtonElement.click();
        } catch (StaleElementReferenceException e) {
            retryClickAction(By.xpath("//div[@class='MuiDialogActions-root MuiDialogActions-spacing css-1n9ly98']/button"));
        }
    }

    private void retryClickAction(By locator) {
        WebElement element = findElementVisible(locator);
        element.click();
    }

    public void clickDeleteButtonInImportPopup() throws InterruptedException {
        click(UPLOAD_POPUP_DELETE_BUTTON);
    }

    public boolean isDeleteSearchTermsPopupIsDisplayed() {
        return isElementVisible(DELETE_POPUP_HEADER);
    }

    public boolean isDeleteSearchTermsPopUpHeaderCorrect() {
        String popUpHeaderText = null;
        try {
            WebElement popUpHeaderElement = findElementVisible(By.xpath("//p[text()='Delete Search Terms?']"));
            popUpHeaderText = popUpHeaderElement.getText();
            UI_LOGGER.info("Pop up header text is: " + popUpHeaderText);
        } catch (StaleElementReferenceException e) {
            retryClickAction(By.xpath("//p[text()='Delete Search Terms?']"));
        }
        assert popUpHeaderText != null;
        return popUpHeaderText.equals("Delete Search Terms?");
    }

    public boolean isSearchTermHeaderDisplayed() {
        return isElementVisible(SEARCH_TERM_HEADER);
    }

    public String getSearchTermFromUI(String searchTermName) {
        waitForElementToBeInvisible(CommonFeatures.LOADING_BAR);
        WebElement rowLocator = findElementVisible(By.xpath("//div[text()='" + searchTermName + "']/ancestor::div[@role='row']"));
        WebElement searchTermElement = rowLocator.findElement(By.xpath("//div[@role='gridcell' and @col-id='searchTerm']"));
        String searchTerm = searchTermElement.getText();
        UI_LOGGER.info("Search term is: " + searchTerm);
        return searchTerm;
    }

    public String getPriorityStatusFromUI(String searchTermName) {
        WebElement rowLocator = findElementVisible(By.xpath("//div[text()='" + searchTermName + "']/ancestor::div[@role='row']"));
        WebElement priorityTermElement = rowLocator.findElement(By.xpath("//div[@role='gridcell' and @col-id='priorityTerm']"));
        String priorityTermStatus = priorityTermElement.getText().equals("flag") ? "t" : "f";
        UI_LOGGER.info("Priority term is: " + priorityTermStatus);
        return priorityTermStatus;
    }

    public String getFrequencyFromUI(String searchTermName) {
        WebElement rowLocator = findElementVisible(By.xpath("//div[text()='" + searchTermName + "']/ancestor::div[@role='row']"));
        WebElement scrapeFrequencyElement = rowLocator.findElement(By.xpath("//div[@role='gridcell' and @col-id='scrapeFrequency']"));
        String scrapeFrequency = scrapeFrequencyElement.getText();
        UI_LOGGER.info("Scrape frequency is: " + scrapeFrequency);
        return scrapeFrequency;
    }

    public String[] getRetailerFromUI(String searchTermName) {
        WebElement rowLocator = findElementVisible(By.xpath("//div[text()='" + searchTermName + "']/ancestor::div[@role='row']"));
        WebElement retailerElement = rowLocator.findElement(By.xpath("//div[@role='gridcell' and @col-id='retailerPlatforms']"));
        String retailer = retailerElement.getText();
        if (retailer.contains(",")) {
            UI_LOGGER.info("Retailer is: " + retailer);
            return retailer.split(",");
        } else if (retailer.contains("Retailers")) {
            WebElement moreThanTwoRetailersLocator = retailerElement.findElement(By.xpath(".//span/div"));
            String ariaLabel = moreThanTwoRetailersLocator.getAttribute("aria-label");
            UI_LOGGER.info("Retailer is: " + ariaLabel);
            return ariaLabel.split(",");
        } else {
            UI_LOGGER.info("Retailer is: " + retailer);
            return new String[]{retailer};
        }
    }

    public String getSearchTermGroupFromUI(String searchTermName) {
        By rowLocator = By.xpath("//div[text()='" + searchTermName + "']/ancestor::div[@role='row']");
        findElementVisible(rowLocator);
        WebElement searchTermGroupElement = findElementVisible(rowLocator).findElement(By.xpath("//div[@role='gridcell' and @col-id='groups']"));
        return searchTermGroupElement.getText().replace("folder_open", "").trim();
    }

    public void editSearchTerm(String searchTermTextBoxValue, List<String> retailer, String frequency, String checkPriorityToggle, List<String> searchTermGroup) throws InterruptedException {
        if (!checkIfCorrectSearchTermChosen(searchTermTextBoxValue)) {
            clickCancelButton();
            Assert.fail("Search term is not correct");
        } else {
            chooseRetailer(retailer);
            click(FREQUENCY_DROPDOWN);
            chooseFrequency(frequency);
            var duration = Duration.ofSeconds(3);
            findElementVisible(PRIORITY_TOGGLE, duration);

            if (checkPriorityToggle.equals("t")) {
                if (!isElementChecked(PRIORITY_TOGGLE, duration)) {
                    click(PRIORITY_TOGGLE);
                    UI_LOGGER.info("Clicked on priority toggle");
                } else {
                    UI_LOGGER.info("Priority toggle is already checked");
                }
            } else {
                if (isElementChecked(PRIORITY_TOGGLE, duration)) {
                    click(PRIORITY_TOGGLE);
                    UI_LOGGER.info("Clicked on priority toggle");
                } else {
                    UI_LOGGER.info("Priority toggle is already unchecked");
                }
            }

            Thread.sleep(1000);
            chooseSearchTermGroups(searchTermGroup);
        }
    }

    public void chooseSearchTermGroups(List<String> searchTermGroup) throws InterruptedException {
        try {
            List<WebElement> tagsInSearchTermGroup = findElementsVisible(By.xpath("//div[@id='add-edit-popup-5']/following-sibling::div//span[text()='close']"));
            for (WebElement tag : tagsInSearchTermGroup) {
                tag.click();
            }
        } catch (StaleElementReferenceException e) {
            List<WebElement> tagsInSearchTermGroup = findElementsVisible(By.xpath("//div[@id='add-edit-popup-5']/following-sibling::div//span[text()='close']"));
            for (WebElement tag : tagsInSearchTermGroup) {
                tag.click();
            }
        }

        if (searchTermGroup != null && !searchTermGroup.isEmpty()) {
            for (String searchTermGroupValue : searchTermGroup) {
                clickElement(SEARCH_TERM_GROUPS);
                Thread.sleep(2000);
                By searchTermGroupLocator = By.xpath("//ul[@id='groups-dropdown-listbox']//li[text()='" + searchTermGroupValue + "']");
                if (!isElementVisible(searchTermGroupLocator, Duration.ofSeconds(5))) {
                    clickElement(SEARCH_TERM_GROUPS);
                }
                findElementVisible(searchTermGroupLocator);
                clickElement(searchTermGroupLocator);
            }
        }
    }

    public boolean isSearchTermDisplayedInUI(String searchTermName) throws InterruptedException {
        Thread.sleep(1000);
        return isElementVisible(By.xpath("//div[text()='" + searchTermName + "']/ancestor::div[@role='row']"));
    }

    public int verifySearchTermTableHasOnlySearchTermsForRetailers(String... retailerNames) throws InterruptedException {

        int totalRecords = 0;

        By nextButtonLocator = By.xpath("//button[@aria-label='Go to next page']");
        WebElement nextButton;
        boolean isButtonDisabled;

        do {
            nextButton = findElementVisible(nextButtonLocator);
            findElementVisible(nextButtonLocator);

            String isDisabledValue = nextButton.getAttribute("disabled");
            isButtonDisabled = "true".equals(isDisabledValue);
            UI_LOGGER.info("Is button disabled: " + isButtonDisabled);

            List<String> associatedRetailersColumnsList;
            try {
                associatedRetailersColumnsList = getAssociatedRetailersList();
            } catch (StaleElementReferenceException e) {
                associatedRetailersColumnsList = getAssociatedRetailersList();
            }

            for (String associatedRetailer : associatedRetailersColumnsList) {
                boolean isAssociatedWithRetailer = false;

                if (associatedRetailer.contains("Retailers")) {
                    List<WebElement> associatedRetailersWithThreeRetailers = findElementsPresent(By.xpath("//span[contains(@id,'retailerPlatforms')]/div"));
                    for (WebElement associatedRetailerWithThreeRetailers : associatedRetailersWithThreeRetailers) {
                        String ariaLabel = associatedRetailerWithThreeRetailers.getAttribute("aria-label");
                        for (String retailerName : retailerNames) {
                            if (ariaLabel.contains(retailerName)) {
                                isAssociatedWithRetailer = true;
                            }
                        }
                    }
                } else {
                    String[] associatedRetailerParts = associatedRetailer.split(",");
                    for (String part : associatedRetailerParts) {
                        if (Arrays.stream(retailerNames).anyMatch(part::contains)) {
                            isAssociatedWithRetailer = true;
                        }
                    }
                }

                if (!isAssociatedWithRetailer) {
                    Assert.fail("Search term " + associatedRetailer + " is not associated with any retailer");
                } else {
                    totalRecords++;
                }
            }

            if (!isButtonDisabled) {
                UI_LOGGER.info("All search terms are associated with retailers. Going to the next page.");
                nextButton.click();
            }
        } while (!isButtonDisabled);

        return totalRecords;

    }

    public List<String> createNewSearchTermGroup(List<String> searchTermGroups) {
        List<String> createdSearchTermGroups = new ArrayList<>();
        if (searchTermGroups != null){
            By selectGroupLocator = By.xpath("//input[@id='groups-dropdown']");
            clickElement(selectGroupLocator);
            for (String searchTermGroup : searchTermGroups) {
                sendKeys(selectGroupLocator, searchTermGroup);
                findElementVisible(CREATE_NEW_GROUP_BUTTON);
                clickElement(CREATE_NEW_GROUP_BUTTON);
                By searchTermGroupTagLocator = By.xpath("//div[@id='add-edit-popup-5']/following-sibling::div//span");
                WebElement searchTermGroupTag = findElementVisible(searchTermGroupTagLocator);
                findElementVisible(searchTermGroupTagLocator);
                if (isElementVisible(searchTermGroupTagLocator) && searchTermGroupTag.getText().equals(searchTermGroup)) {
                    createdSearchTermGroups.add(searchTermGroupTag.getText());
                } else {
                    UI_LOGGER.info("Search term group was not created");
                }
            }
            UI_LOGGER.info("Created search term groups: " + createdSearchTermGroups);
        }
        return createdSearchTermGroups;
    }

    public List<String> getAssociatedRetailersList() throws InterruptedException {
        By tableLocator = By.xpath("//div[contains(@class,'ag-body-viewport')]");
        By associatedRetailersLocator = By.xpath("//span[contains(@id,'retailerPlatforms')]");
        List<String> retailersInTable = new ArrayList<>();
        List<String> retailerIds = new ArrayList<>();
        Thread.sleep(2000);
        scrollMainBarToCenterAndInnerBarToTop(tableLocator);
        double currentPosition;
        double pixelsToScroll = getPixelsToScroll(tableLocator, 10);
        do {
            currentPosition = getVerticalScrollPosition(tableLocator);
            List<WebElement> elements;
            try {
                elements = findElementsPresent(associatedRetailersLocator);
            } catch (StaleElementReferenceException e) {
                elements = findElementsPresent(associatedRetailersLocator);
            }
            for (WebElement element : elements) {
                String id = element.getAttribute("id");
                if (!retailerIds.contains(id)) {
                    retailerIds.add(id);
                    try {
                        retailersInTable.add(element.getText());
                    } catch (StaleElementReferenceException e) {
                        retailersInTable.add(element.getText());
                    }
                }
            }
            scrollElementVertically(pixelsToScroll, tableLocator);
        } while (currentPosition != getVerticalScrollPosition(tableLocator));
        return retailersInTable;
    }

    public boolean isSearchTermSearchBarDisplayed() {
        return isElementVisible(SEARCH_TERM_PAGE_SEARCH_BAR);
    }

    public boolean verifyAutocompleteIsWorkingCorrectly(String searchTerm) throws InterruptedException {
        boolean isAutocompleteWorkingCorrectly = false;
        sendKeysAndHitEnter(SEARCH_TERM_PAGE_SEARCH_BAR, searchTerm);
        Thread.sleep(1000);
        By autocompleteLocator = By.xpath("//div[@col-id='searchTerm' and @role='gridcell']");
        findElementVisible(autocompleteLocator);
        List<WebElement> autocompleteElements = findElementsVisible(autocompleteLocator);
        for (WebElement autocompleteElement : autocompleteElements) {
            String autocompleteText = autocompleteElement.getText();
            if (autocompleteText.contains(searchTerm)) {
                UI_LOGGER.info("Autocomplete result: " + autocompleteText);
                isAutocompleteWorkingCorrectly = true;
            } else {
                UI_LOGGER.info("Autocomplete results are not correct");
                isAutocompleteWorkingCorrectly = false;
                break;
            }
        }
        return isAutocompleteWorkingCorrectly;
    }

    public void findSearchTermInSearchBarAndClick(String searchTerm) throws InterruptedException {
        By searchTermLocator = By.xpath("//div[text()='" + searchTerm + "']");
        click(SEARCH_TERM_PAGE_SEARCH_BAR);
        sendKeysAndHitEnter(SEARCH_TERM_PAGE_SEARCH_BAR, searchTerm);
        findElementVisible(searchTermLocator);
    }

    public void clearSearchBar() {
        if (!findElementVisible(SEARCH_TERM_PAGE_SEARCH_BAR).getAttribute("value").isEmpty()) {
            clearInput(SEARCH_TERM_PAGE_SEARCH_BAR);
        } else {
            UI_LOGGER.info("Search bar is already empty");
        }
    }

    public boolean searchTermGroupAlreadyExists(String searchTerm, List<String> retailer, String searchTermGroup) throws InterruptedException {
        findElementVisible(GRID_ROW, Duration.ofSeconds(30));
        clickAddASearchTermButton();
        addNameToSearchTerm(searchTerm);
        chooseRetailer(retailer);
        By selectGroupLocator = By.xpath("//input[@id='groups-dropdown']");
        clickElement(selectGroupLocator);
        By groupOptionLocator = By.xpath("//ul[@id='groups-dropdown-listbox']//li[text()='" + searchTermGroup + "']");
        sendKeys(selectGroupLocator, searchTermGroup);
        if (isElementVisible(groupOptionLocator)) {
            click(CREATE_NEW_GROUP_BUTTON);
            String notificationText = findElementVisible(SEARCH_TERM_EXIST_NOTIFICATION).getText();
            Assert.assertEquals(notificationText, "Group already exists", "Notification text is not correct");
            UI_LOGGER.info("Search term group already exists. Notification " + notificationText + " is displayed");
            return true;
        } else {
            return false;
        }
    }

    public void clickCancelButtonInUploadPopUp() throws InterruptedException {
        click(UPLOAD_POPUP_CANCEL_BUTTON);
    }
}