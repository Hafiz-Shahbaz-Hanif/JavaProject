package com.DC.pageobjects.adc.execute.productManager.products;

import com.DC.objects.productVersioning.UserFriendlyInstancePath;
import com.DC.pageobjects.adc.execute.productManager.products.bulkEditKeywordsPage.BulkEditKeywordsPage;
import com.DC.pageobjects.adc.execute.productManager.products.generateLaunchFilePage.CreateBatchTab;
import com.DC.pageobjects.adc.navigationMenus.InsightsNavigationMenu;
import com.DC.pageobjects.filters.AdvancedFilters;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.enums.Enums;
import com.DC.utilities.sharedElements.*;
import com.DC.pageobjects.adc.execute.productManager.productLists.AddToListModal;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.List;

public class ProductsPage extends ProductsPageBase {
    protected final By CONTENT_SUGGESTIONS_BANNER = By.xpath("//div[child::span[contains(text(),'info')]]//button[text()='close']");
    protected final By ADVANCED_FILTER_BUTTON_LOCATOR = By.xpath("//div[@data-qa='ProductsHeader']//button[text()='Advanced Filters']");
    protected final By ADD_PRODUCT_BUTTON_LOCATOR = By.xpath("//div[@data-qa='ProductsPage']//button[text()='Add Product']");
    protected final By EXPORT_BUTTON_LOCATOR = By.xpath("//div[@data-qa='ProductsHeader']//button[text()='Export']");
    protected final By IMPORT_BUTTON_LOCATOR = By.xpath("//div[@data-qa='ProductsHeader']//button[text()='Import']");

    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    public BottomActionBar selectAllFilterMatches() {
        clickElement(SELECT_PRODUCTS_CHECKBOX_LOCATOR);
        var selectAllCheckbox = By.xpath("//li[text()='Select All Filter Matches']");
        clickElement(selectAllCheckbox);
        return new BottomActionBar(driver);
    }

    public Boolean isSelectedCheckboxEnabled() {
        var firstCheckboxLocator = By.xpath(tableCommonFeatures.ROWS_LOCATOR + "//input");
        return isElementEnabled(firstCheckboxLocator);
    }

    public BottomActionBar selectAllProductsOnPage() throws InterruptedException {
        super.selectAllProductsOnPage();
        return new BottomActionBar(driver);
    }

    public BottomActionBar selectProduct(UserFriendlyInstancePath product) {
        super.selectProduct(product);
        return new BottomActionBar(driver);
    }

    public BottomActionBar selectProduct(String instanceUniqueId) {
        super.selectProduct(instanceUniqueId);
        return new BottomActionBar(driver);
    }

    public BottomActionBar selectProducts(List<UserFriendlyInstancePath> products) {
        super.selectProducts(products);
        return new BottomActionBar(driver);
    }

    public BottomActionBar selectProductsByInstanceUniqueId(List<String> instanceUniqueIds) {
        for (String instanceUniqueId : instanceUniqueIds) {
            selectProduct(instanceUniqueId);
        }
        return new BottomActionBar(driver);
    }

    public boolean isBottomActionBarVisible() {
        return isElementVisibleMilliseconds(BOTTOM_ACTION_BAR_LOCATOR);
    }

    public GenericMultiListModal openModalToEditColumns() throws InterruptedException {
        moreActionsDropdown.openDropdownMenu("More");
        return moreActionsDropdown.selectOption("Edit Columns", GenericMultiListModal.class);
    }

    public ProductsPage closeContentSuggestionsBanner() {
        boolean bannerIsVisible = isElementVisibleMilliseconds(CONTENT_SUGGESTIONS_BANNER);
        if (bannerIsVisible) {
            clickElement(CONTENT_SUGGESTIONS_BANNER);
        }
        return this;
    }

    public AdvancedFilters openAdvancedFiltersSection() throws InterruptedException {
        boolean advancedFiltersIsVisible = isElementVisibleMilliseconds(By.xpath(AdvancedFilters.ADVANCED_FILTER_SECTION_XPATH));
        if (!advancedFiltersIsVisible) {
            scrollIntoViewAndClick(ADVANCED_FILTER_BUTTON_LOCATOR);
        }
        return new AdvancedFilters(driver);
    }

    public boolean isAssignCategoryModalDisplayed() {
        By modalLocator = By.xpath("//div[@data-qa='AssignProductCategory']");
        return isElementVisibleMilliseconds(modalLocator);
    }

    public boolean isAssignBusinessUnitModalDisplayed() {
        By modalLocator = By.xpath("//div[@data-qa='AssignBusinessUnit']");
        return isElementVisibleMilliseconds(modalLocator);
    }

    public boolean isAddProductModalDisplayed() {
        By modalLocator = By.xpath("//div[@data-qa='ModalWrapper' and descendant::h4[text()='Add Product']]");
        return isElementVisibleMilliseconds(modalLocator);
    }

    public void clickAddProductButton() {
        closeContentSuggestionsBanner();
        scrollIntoView(ADD_PRODUCT_BUTTON_LOCATOR);
        clickElement(ADD_PRODUCT_BUTTON_LOCATOR);
    }

    public AddProductModal openModalToAddProducts() {
        clickAddProductButton();
        return new AddProductModal(driver);
    }

    public ProductsPage addProducts(List<UserFriendlyInstancePath> productsToAdd) {
        AddProductModal addProductModal = openModalToAddProducts();
        for (var product : productsToAdd) {
            addProductModal.addProduct(product);
            addProductModal.waitForSuccessNoteAndClose();
        }
        addProductModal.clickCancelButton();
        closeContentSuggestionsBanner();
        return this;
    }

    public ExportModal openModalToExportData() {
        scrollIntoView(EXPORT_BUTTON_LOCATOR);
        clickElement(EXPORT_BUTTON_LOCATOR);
        return new ExportModal(driver);
    }

    public ProductsImportModal openModalToImportData() {
        waitForDOMStabilization();
        clickElement(IMPORT_BUTTON_LOCATOR);
        return new ProductsImportModal(driver);
    }

    public boolean isModalToDeleteProductsDisplayed() {
        return isElementVisible(DeleteProductModal.MODAL_BODY, Duration.ofSeconds(3));
    }

    public static class BottomActionBar extends ProductsPage {
        private final By PRODUCTS_SELECTED_TEXT_LOCATOR = By.xpath(BOTTOM_ACTION_BAR_XPATH + "//span[contains(text(),'version') and contains(text(),'selected')]");
        private final By DELETE_PRODUCTS_BUTTON_LOCATOR = By.xpath(BOTTOM_ACTION_BAR_XPATH + "//button[text()='Delete Products']");

        public SingleSelectDropdown singleSelectDropdown;

        public BottomActionBar(WebDriver driver) {
            super(driver);
            findElementVisible(BOTTOM_ACTION_BAR_LOCATOR);
            singleSelectDropdown = new SingleSelectDropdown(driver);
        }

        public void clickButtonFromEditDropdown(String itemToSelect) throws InterruptedException {
            singleSelectDropdown.openDropdownMenu("Edit");
            singleSelectDropdown.selectOption(itemToSelect);
        }

        public CreateBatchTab clickGenerateLaunchFile() throws InterruptedException {
            singleSelectDropdown.openDropdownMenu("Optimize");
            singleSelectDropdown.selectOption("Generate Launch File");
            return new CreateBatchTab(driver);
        }

        public BulkEditProductPropertiesPage clickEditPropertiesButton() throws InterruptedException {
            clickButtonFromEditDropdown("Properties");
            return new BulkEditProductPropertiesPage(driver);
        }

        public int getNumberOfSelectedProducts() {
            String textInElement = getTextFromElementMilliseconds(PRODUCTS_SELECTED_TEXT_LOCATOR);
            return SharedMethods.extractIntegerFromString(textInElement);
        }

        public AddToListModal openAddToListModal() throws InterruptedException {
            clickButtonFromEditDropdown("Add to List(s)");
            return new AddToListModal(driver);
        }

        public BulkEditKeywordsPage clickEditKeywordsButton() throws InterruptedException {
            clickButtonFromEditDropdown("Keywords");
            return new BulkEditKeywordsPage(driver);
        }

        public ProductsPage removeSelectedProductsFromList() throws InterruptedException {
            clickButtonFromEditDropdown("Remove from List");
            By confirmDeleteButton = By.xpath("//div[@data-qa='ModalWrapper']//button[text()='Yes, Remove From List']");
            clickElement(confirmDeleteButton);
            waitForElementToBeInvisible(confirmDeleteButton, MAX_WAIT_TIME_SECS);
            return new ProductsPage(driver);
        }

        public AssignCategoryModal clickEditCategoryButton() throws InterruptedException {
            clickButtonFromEditDropdown("Category");
            return new AssignCategoryModal(driver);
        }

        public AssignBusinessUnitModal clickEditAssignBusinessUnitButton() throws InterruptedException {
            clickButtonFromEditDropdown("Assign Business Unit");
            return new AssignBusinessUnitModal(driver);
        }

        public DeleteProductModal openModalToDeleteProducts() {
            clickElement(DELETE_PRODUCTS_BUTTON_LOCATOR);
            return new DeleteProductModal(driver);
        }

        public void clickDeleteProductsButton() {
            clickElement(DELETE_PRODUCTS_BUTTON_LOCATOR);
        }
    }

    public static class AssignCategoryModal extends InsightsNavigationMenu {
        private final By SAVE_BUTTON_LOCATOR = By.xpath("//div[@data-qa='ModalWrapper']//button[text()='Save']");
        private final By CANCEL_BUTTON_LOCATOR = By.xpath("//div[@data-qa='ModalWrapper']//button[text()='Cancel']");
        private final By WARNING_LOCATOR = By.xpath("//div[@data-qa='ModalWrapper']//div[text()='Warning']");
        public CategoryTree categoryTree;

        public AssignCategoryModal(WebDriver driver) {
            super(driver);
            categoryTree = new CategoryTree(driver);
        }

        public ProductsPage clickSaveButton() {
            clickElement(SAVE_BUTTON_LOCATOR);
            waitForElementToBeInvisible(SAVE_BUTTON_LOCATOR);
            return new ProductsPage(driver);
        }

        public ProductsPage clickCancelButton() {
            clickElement(CANCEL_BUTTON_LOCATOR);
            return new ProductsPage(driver);
        }

        public boolean isWarningMessageDisplayed() {
            return isElementVisibleMilliseconds(WARNING_LOCATOR);
        }
    }

    public static class AddProductModal extends InsightsNavigationMenu {
        private final String MODAL_XPATH = "//div[@data-qa='ModalWrapper']";
        private final By PRODUCT_IDENTIFIER_INPUT_LOCATOR = By.id("product-identifier-input");
        private final By LOCALES_INPUT_LOCATOR = By.xpath(MODAL_XPATH + "//div[child::label[@for='locale-select']]//input");
        private final By RETAILER_INPUT_LOCATOR = By.xpath(MODAL_XPATH + "//div[child::label[@for='retailer-select']]//input");
        private final By CAMPAIGN_INPUT_LOCATOR = By.xpath(MODAL_XPATH + "//div[child::label[@for='campaign-select']]//input");
        private final By ADD_BUTTON_LOCATOR = By.xpath(MODAL_XPATH + "//button[text()='Add']");
        private final By ADD_AND_CLOSE_BUTTON_LOCATOR = By.xpath(MODAL_XPATH + "//button[text()='Add & Close']");
        private final By CANCEL_BUTTON_LOCATOR = By.xpath(MODAL_XPATH + "//button[text()='Cancel']");

        private SingleSelectDropdown singleSelectDropdown;

        public AddProductModal(WebDriver driver) {
            super(driver);
            findElementVisible(PRODUCT_IDENTIFIER_INPUT_LOCATOR);
            singleSelectDropdown = new SingleSelectDropdown(driver);
        }

        public void insertProductIdentifier(String productIdentifier) {
            setText(PRODUCT_IDENTIFIER_INPUT_LOCATOR, productIdentifier);
        }

        public void insertLocale(String localeName) {
            clickElement(LOCALES_INPUT_LOCATOR);
            singleSelectDropdown.selectOption(localeName);
        }

        public void insertRetailer(String retailerName) {
            if (retailerName == null) return;
            clickElement(RETAILER_INPUT_LOCATOR);
            singleSelectDropdown.selectOption(retailerName);
        }

        public void insertCampaign(String campaignName) {
            if (campaignName == null) return;
            clickElement(CAMPAIGN_INPUT_LOCATOR);
            singleSelectDropdown.selectOption(campaignName);
        }

        public void fillForm(UserFriendlyInstancePath product) {
            insertProductIdentifier(product.productIdentifier);
            insertLocale(product.localeName);
            insertRetailer(product.retailerName);
            insertCampaign(product.campaignName);
        }

        public UserFriendlyInstancePath getDataInForm() {
            var attributeToFind = "value";
            var productIdentifier = getAttribute(PRODUCT_IDENTIFIER_INPUT_LOCATOR, attributeToFind);
            var localeName = getAttribute(LOCALES_INPUT_LOCATOR, attributeToFind);
            var retailerName = getAttribute(RETAILER_INPUT_LOCATOR, attributeToFind);
            var campaignName = getAttribute(CAMPAIGN_INPUT_LOCATOR, attributeToFind);
            return new UserFriendlyInstancePath(
                    productIdentifier.isEmpty() ? null : productIdentifier,
                    localeName.isEmpty() ? null : localeName,
                    retailerName.isEmpty() ? null : retailerName,
                    campaignName.isEmpty() ? null : campaignName
            );
        }

        public AddProductModal addProduct(UserFriendlyInstancePath product) {
            fillForm(product);
            return clickAddButton();
        }

        public AddProductModal clickAddButton() {
            clickElement(ADD_BUTTON_LOCATOR);
            return this;
        }

        public ProductsPage clickAddAndCloseButtonAndWaitForInvisibility() {
            clickElement(ADD_AND_CLOSE_BUTTON_LOCATOR);
            waitForElementToBeInvisible(ADD_AND_CLOSE_BUTTON_LOCATOR, Duration.ofSeconds(10));
            return new ProductsPage(driver);
        }

        public void clickAddAndCloseButton() {
            clickElement(ADD_AND_CLOSE_BUTTON_LOCATOR);
        }

        public ProductsPage clickCancelButton() {
            clickElement(CANCEL_BUTTON_LOCATOR);
            waitForElementToBeInvisible(CANCEL_BUTTON_LOCATOR, Duration.ofSeconds(3));
            return new ProductsPage(driver);
        }

        public ProductsPage clickCloseIcon() {
            clickCloseIconFromReactModal();
            return new ProductsPage(driver);
        }

        public void waitForSuccessNote() {
            By successNoteLocator = By.xpath(MODAL_XPATH + "//span[text()='check_circle']");
            findElementVisible(successNoteLocator, Duration.ofSeconds(5));
        }

        public void waitForSuccessNoteAndClose() {
            By closeButtonLocator = By.xpath("(//div[@data-qa='ModalWrapper']//button[text()='close'])[2]");
            waitForSuccessNote();
            clickElement(closeButtonLocator);
        }
    }

    public static class AssignBusinessUnitModal extends InsightsNavigationMenu {
        private final String MODAL_XPATH = "//div[@data-qa='AssignBusinessUnit']";
        private final By SEARCH_INPUT_LOCATOR = By.xpath(MODAL_XPATH + "//input[@placeholder='Search Business Units']");
        public SingleSelectDropdown singleSelectDropdown;

        public AssignBusinessUnitModal(WebDriver driver) {
            super(driver);
            findElementVisible(By.xpath(MODAL_XPATH));
            singleSelectDropdown = new SingleSelectDropdown(driver);
        }

        public String getModalTitle() {
            var modalTitleLocator = By.xpath("//div[@data-qa='ModalWrapper']//h4");
            return getTextFromElementMilliseconds(modalTitleLocator);
        }

        public void searchForBusinessUnit(String businessUnitName) {
            clickElement(SEARCH_INPUT_LOCATOR);
            setText(SEARCH_INPUT_LOCATOR, businessUnitName);
        }

        public String getSelectedBusinessUnit() {
            return getAttribute(SEARCH_INPUT_LOCATOR, "value");
        }

        public void openDropdownMenu() {
            var dropdownIcon = By.xpath(MODAL_XPATH + "//button[text()='expand_more']");
            singleSelectDropdown.openDropdownMenu(dropdownIcon);
        }

        public void closeDropdownMenu() {
            var dropdownIcon = By.xpath(MODAL_XPATH + "//button[text()='expand_less']");
            singleSelectDropdown.closeDropdownMenu(dropdownIcon);
        }

        public ProductsPage clickSaveButton() {
            var saveButtonLocator = By.xpath(MODAL_XPATH + "//button[text()='Save']");
            clickElement(saveButtonLocator);
            waitForElementToBeInvisible(By.xpath(MODAL_XPATH));
            return new ProductsPage(driver);
        }

        public ProductsPage clickCancelButton() {
            var cancelButtonLocator = By.xpath(MODAL_XPATH + "//button[text()='Cancel']");
            clickElement(cancelButtonLocator);
            waitForElementToBeInvisible(cancelButtonLocator);
            return new ProductsPage(driver);
        }

        public ProductsPage clickCloseIcon() {
            clickCloseIconFromReactModal();
            return new ProductsPage(driver);
        }
    }

    public static class DeleteProductModal extends InsightsNavigationMenu {
        private final By SAVE_AND_EXIT_BUTTON = By.xpath("//div[@data-qa='ModalWrapper']//button[text()='Save & Exit']");
        public static final By MODAL_BODY = By.xpath("//div[@data-qa='ModalWrapper']//h4[text()='Confirm']");

        public DeleteProductModal(WebDriver driver) {
            super(driver);
            findElementVisible(MODAL_BODY);
        }

        public ProductsPage confirmDeletion() {
            clickElement(SAVE_AND_EXIT_BUTTON);
            waitForElementToBeInvisible(SAVE_AND_EXIT_BUTTON);
            return new ProductsPage(driver);
        }

        public String getModalMessage() {
            var modalMessageLocator = By.xpath("//div[@data-qa='ModalWrapper']//p");
            return getTextFromElementMilliseconds(modalMessageLocator);
        }

        public ProductsPage clickCloseIcon() {
            clickCloseIconFromReactModal();
            return new ProductsPage(driver);
        }

        public boolean isWarningMessageDisplayed() {
            if (!isNoteDisplayed(Enums.NoteType.WARNING)) {
                return false;
            }

            var warningTitle = "You have chosen Base products to delete.";
            var warningMessage = "This will delete any other products in that locale for this Product Identifier.";
            By noteLocator = By.xpath("//div[child::span[text()='warning'] and descendant::h5[text()='" + warningTitle + "'] and descendant::span[text()='" + warningMessage + "']]");
            return isElementVisible(noteLocator, Duration.ofSeconds(2));
        }

    }
}
