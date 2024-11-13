package com.DC.pageobjects.adc.execute.productManager.productLists;

import com.DC.objects.productVersioning.ProductListUI;
import com.DC.pageobjects.adc.execute.productManager.products.ProductsPage;
import com.DC.pageobjects.adc.navigationMenus.InsightsNavigationMenu;
import com.DC.utilities.enums.Enums;
import com.DC.utilities.sharedElements.AGTableCommonFeatures;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.*;
import java.util.stream.Collectors;

public class ProductListsPage extends InsightsNavigationMenu {
    public final String PERMISSION_COLUMN_ID = "permission";
    public final String LIST_NAME_COLUMN_ID = "name";
    public final String OWNER_COLUMN_ID = "ownerName";
    public final String NUMBER_OF_PRODUCTS_COLUMN_ID = "number";
    public final String LAST_ACTIVITY_COLUMN_ID = "lastActivity";

    private final String PAGE_BODY_XPATH = "//div[@class='ag-center-cols-viewport']";
    private final By ADD_LIST_BUTTON_LOCATOR = By.xpath("//button[text()='Add List']");
    private final By SEARCH_INPUT_LOCATOR = By.xpath("//input[@placeholder='Search Name or Owner']");

    protected final String BOTTOM_ACTION_BAR_XPATH = "//div[@data-qa='BottomActionBarPV']";
    protected final By SUBMIT_BUTTON_LOCATOR = By.xpath(BOTTOM_ACTION_BAR_XPATH + "//button[text()='Submit']");

    public AGTableCommonFeatures tableCommonFeatures;

    public ProductListsPage(WebDriver driver) {
        super(driver);
        findElementVisible(By.xpath(PAGE_BODY_XPATH), MAX_WAIT_TIME_SECS);
        tableCommonFeatures = new AGTableCommonFeatures(driver);
    }

    public ProductListsPage searchForList(String listNameOrOwner) {
        setTextAndHitEnter(SEARCH_INPUT_LOCATOR, listNameOrOwner);
        return this;
    }

    public ProductListsPage emptyOutSearchInput() {
        setTextAndHitEnter(SEARCH_INPUT_LOCATOR, "");
        return this;
    }

    public NewListModal clickAddListButton() throws InterruptedException {
        scrollIntoViewAndClick(ADD_LIST_BUTTON_LOCATOR);
        return new NewListModal(driver);
    }

    public boolean doesListExist(String listName) {
        By listNameCellLocator = By.xpath("//div[@col-id='name' and descendant::span[text()='" + listName + "']]");
        searchForList(listName);
        return isElementVisibleMilliseconds(listNameCellLocator);
    }

    public ProductListsPage addListIfNotExistent(String listName, Enums.ProductListPermission permission) throws InterruptedException {
        if (!doesListExist(listName)) {
            closeNoteIfDisplayed(Enums.NoteType.SUCCESS);
            return addList(listName, permission);
        }
        return this;
    }

    public ProductListsPage addList(String listName, Enums.ProductListPermission permission) throws InterruptedException {
        return clickAddListButton().createNewListAndCloseModal(listName, permission);
    }

    public BottomActionBar selectAllLists() throws InterruptedException {
        tableCommonFeatures.selectAll();
        return new BottomActionBar(driver);
    }

    public ProductListsPage deselectAllLists() {
        tableCommonFeatures.deselectAll();
        return this;
    }

    public boolean isListSelected(String listName) {
        By listNameCheckboxLocator = getListNameCheckboxLocator(listName);
        return getAttribute(listNameCheckboxLocator, "aria-label").contains("(checked)");
    }

    public BottomActionBar selectListIfNotSelected(String listName) throws InterruptedException {
        if (!isListSelected(listName)) {
            selectList(listName);
        }
        return new BottomActionBar(driver);
    }

    public BottomActionBar selectList(String listName) throws InterruptedException {
        By listNameCheckboxLocator = getListNameCheckboxLocator(listName);
        scrollUntilListIsPresent(listName);
        scrollIntoViewAndClick(listNameCheckboxLocator);
        return new BottomActionBar(driver);
    }

    public ProductListsPage deleteList(String listToDelete) throws InterruptedException {
        deselectAllLists();
        return selectList(listToDelete).deleteList(listToDelete);
    }

    public ProductListsPage deleteLists(List<String> listsToDelete) throws InterruptedException {
        deselectAllLists();
        BottomActionBar bottomActionBar = null;
        for (String listToDelete : listsToDelete) {
            bottomActionBar = selectList(listToDelete);
        }

        return bottomActionBar.deleteLists();
    }

    public ProductListsPage deleteProductListIfExistent(String listToDelete) throws InterruptedException {
        if (doesListExist(listToDelete)) {
            deleteList(listToDelete);
        }
        return this;
    }

    public ProductListsPage deleteListsIfExistent(List<String> listsToDelete) throws InterruptedException {
        emptyOutSearchInput();
        deselectAllLists();
        BottomActionBar bottomActionBar = null;
        for (String listToDelete : listsToDelete) {
            if (doesListExist(listToDelete)) {
                bottomActionBar = selectList(listToDelete);
            }
        }

        if (bottomActionBar != null) {
            return bottomActionBar.deleteLists();
        } else {
            return this;
        }
    }

    public List<String> getAllListNames() {
        return getAllListsData().stream().map(list -> list.name)
                .collect(Collectors.toList());
    }

    public int getNumberOfProductsInList(String listName) {
        String cellLocatorXPath = getCellLocatorXPath(NUMBER_OF_PRODUCTS_COLUMN_ID, listName);
        By numberOfProductsLocator = By.xpath(cellLocatorXPath + "//a");
        return Integer.parseInt(getTextFromElement(numberOfProductsLocator, MAX_WAIT_TIME_SECS));
    }

    public String getOwnerOfList(String listName) {
        String cellLocatorXPath = getCellLocatorXPath(OWNER_COLUMN_ID, listName);
        By ownerNameLocator = By.xpath(cellLocatorXPath + "//span");
        return getTextFromElement(ownerNameLocator, MAX_WAIT_TIME_SECS);
    }

    public Enums.ProductListPermission getListPermission(String listName) {
        String cellLocatorXPath = getCellLocatorXPath(PERMISSION_COLUMN_ID, listName);
        By permissionLocator = By.xpath(cellLocatorXPath + "//span//span");
        String permission = getTextFromElement(permissionLocator, MAX_WAIT_TIME_SECS);
        return Enums.ProductListPermission.valueOf(permission.toUpperCase());
    }

    public String getLastActivity(String listName) {
        String cellLocatorXPath = getCellLocatorXPath(LAST_ACTIVITY_COLUMN_ID, listName);
        By lastActivityLocator = By.xpath(cellLocatorXPath + "//span");
        return getTextFromElement(lastActivityLocator, MAX_WAIT_TIME_SECS);
    }

    public ProductListUI getListData(String listName) {
        scrollUntilListIsPresent(listName);
        ProductListUI productListUI = new ProductListUI();
        productListUI.name = listName;
        productListUI.numberOfProducts = getNumberOfProductsInList(listName);
        productListUI.ownerName = getOwnerOfList(listName);
        productListUI.permission = getListPermission(listName);
        productListUI.lastActivity = getLastActivity(listName);
        return productListUI;
    }

    public List<ProductListUI> getAllListsData() {
        LinkedHashMap<Integer, ProductListUI> listsMap = new LinkedHashMap<>();

        scrollToCenterIfTableScrollable(tableCommonFeatures.TABLE_LOCATOR);

        double currentPosition;
        double pixelsToScroll = getPixelsToScroll(tableCommonFeatures.TABLE_LOCATOR, 15);

        do {
            waitForDOMStabilization();
            currentPosition = getVerticalScrollPosition(tableCommonFeatures.TABLE_LOCATOR);
            var rowsDisplayed = findElementsVisible(tableCommonFeatures.ROWS_LOCATOR, java.time.Duration.ofSeconds(5));

            for (var row : rowsDisplayed) {
                try {
                    int rowIndex = Integer.parseInt(row.getAttribute("row-index"));
                    boolean listAlreadyAdded = listsMap.containsKey(rowIndex);
                    if (!listAlreadyAdded) {
                        var name = row.findElement(By.xpath(".//div[@col-id='name']//span[text()]")).getText();
                        var listData = getListData(name);
                        listsMap.put(rowIndex, listData);
                    }
                } catch (StaleElementReferenceException | NoSuchElementException ex) {
                    break;
                }
            }
            scrollElementVertically(pixelsToScroll, tableCommonFeatures.TABLE_LOCATOR);

        } while (currentPosition != getVerticalScrollPosition(tableCommonFeatures.TABLE_LOCATOR));

        var sortedMap = new TreeMap<>(listsMap);
        return new ArrayList<>(sortedMap.values());
    }

    public ProductListsPage scrollUntilListIsPresent(String listName) {
        By rowLocatorXPath = By.xpath(getCellLocatorXPath(LIST_NAME_COLUMN_ID, listName));
        String errorMsg = "Product list '" + listName + "' was not found in the table";
        scrollDownToElement(tableCommonFeatures.TABLE_LOCATOR, rowLocatorXPath, 10, errorMsg);
        return this;
    }

    public void clickOnListProductNumber(String listName) {
        String cellLocatorXPath = getCellLocatorXPath(NUMBER_OF_PRODUCTS_COLUMN_ID, listName);
        By numberOfProductsLocator = By.xpath(cellLocatorXPath + "//a");
        scrollIntoView(numberOfProductsLocator);
        clickElement(numberOfProductsLocator);
    }

    public boolean openProductsOfListInNewTab(String listName) {
        String cellLocatorXPath = getCellLocatorXPath(NUMBER_OF_PRODUCTS_COLUMN_ID, listName);
        By numberOfProductsLocator = By.xpath(cellLocatorXPath + "//a");
        scrollIntoView(numberOfProductsLocator);
        performControlClickOnElement(numberOfProductsLocator);
        switchToTab(1);

        boolean productsPageFiltered;

        try {
            productsPageFiltered = new ProductsPage(driver).isPageFilteredByList(listName);
        } catch (Exception e) {
            UI_LOGGER.info("Exception caught after opening products in a new tab: " + e.getMessage());
            productsPageFiltered = false;
        }

        closeCurrentTabAndSwitchToMainTab();
        return productsPageFiltered;
    }

    public void clickEditIcon(String listName, String columnId) throws InterruptedException {
        String cellLocatorXpath = getCellLocatorXPath(columnId, listName);
        By editButtonLocator = By.xpath(cellLocatorXpath + "//button[text()='edit']");
        scrollIntoViewAndClick(editButtonLocator);
    }

    public void openDropdownToEditListPermission(String listNameToEdit) throws InterruptedException {
        clickEditIcon(listNameToEdit, PERMISSION_COLUMN_ID);
        String cellLocatorXpath = getCellLocatorXPath(PERMISSION_COLUMN_ID, listNameToEdit);
        By permissionDropdownLocator = By.xpath(cellLocatorXpath + "//input");
        clickElement(permissionDropdownLocator);
    }

    public void clickEditIconIfDisplayed(String listNameToEdit, String columnId) throws InterruptedException {
        String cellLocatorXpath = getCellLocatorXPath(columnId, listNameToEdit);
        By editButtonLocator = By.xpath(cellLocatorXpath + "//button[text()='edit']");
        if (isElementVisibleMilliseconds(editButtonLocator)) {
            scrollIntoViewAndClick(editButtonLocator);
        }
    }

    public void clickSaveIcon(String listName, String columnId) throws InterruptedException {
        String cellLocatorXpath = getCellLocatorXPath(columnId, listName);
        By saveValueIconLocator = By.xpath(cellLocatorXpath + "//button[text()='save']");
        scrollIntoViewAndClick(saveValueIconLocator);
    }

    public void saveListName(String listName) throws InterruptedException {
        clickSaveIcon(listName, LIST_NAME_COLUMN_ID);
    }

    public void saveListPermission(String listName) throws InterruptedException {
        clickSaveIcon(listName, PERMISSION_COLUMN_ID);
    }

    public void clickCloseIcon(String listName, String columnId) throws InterruptedException {
        String cellLocatorXpath = getCellLocatorXPath(columnId, listName);
        By closeIconLocator = By.xpath(cellLocatorXpath + "//button[text()='close']");
        scrollIntoViewAndClick(closeIconLocator);
    }

    public void closeInputToEditListName(String listNameToEdit) throws InterruptedException {
        clickCloseIcon(listNameToEdit, LIST_NAME_COLUMN_ID);
    }

    public void closeDropdownToEditListPermission(String listNameToEdit) throws InterruptedException {
        clickCloseIcon(listNameToEdit, PERMISSION_COLUMN_ID);
    }

    public void editListNameInput(String listToEdit, String newListName) {
        String cellLocatorXpath = getCellLocatorXPath(LIST_NAME_COLUMN_ID, listToEdit);
        By inputLocator = By.xpath(cellLocatorXpath + "//input");
        WebElement inputToEditName = findElementVisibleMilliseconds(inputLocator);
        setText(inputToEditName, newListName);
    }

    public Enums.ProductListPermission switchListPermission(String listToEdit) {
        String cellLocatorXPath = getCellLocatorXPath(PERMISSION_COLUMN_ID, listToEdit);
        By permissionLocator = By.xpath(cellLocatorXPath + "//input");
        String currentPermission = getAttribute(permissionLocator, "value");
        String optionToSelect = Objects.equals(currentPermission, "Public") ? "Private" : "Public";
        String cellLocatorXpath = getCellLocatorXPath(PERMISSION_COLUMN_ID, listToEdit);
        By dropdownOptionToSelect = By.xpath(cellLocatorXpath + "//li[text()='" + optionToSelect + "']");
        clickElement(dropdownOptionToSelect);
        return Enums.ProductListPermission.valueOf(optionToSelect.toUpperCase());
    }

    public BottomActionBar updateListNameAndSwitchPermissions(String listToEdit, String newListName) throws InterruptedException {
        clickEditIcon(listToEdit, LIST_NAME_COLUMN_ID);
        openDropdownToEditListPermission(listToEdit);
        switchListPermission(listToEdit);
        editListNameInput(listToEdit, newListName);
        saveListName(newListName);
        //saveListPermission(newListName); //TODO - UNCOMMENT ONCE ISSUE 1 IN PPV-327 GETS RESOLVED
        return new BottomActionBar(driver);
    }

    public ProductListsPage updateListNameAndSubmitChanges(String listToEdit, String newListName) throws InterruptedException {
        clickEditIcon(listToEdit, LIST_NAME_COLUMN_ID);
        editListNameInput(listToEdit, newListName);
        saveListName(newListName);
        return new BottomActionBar(driver).clickSubmitButton();
    }

    public boolean isCellHighlighted(String listName, String columnId) {
        String cellLocatorXpath = getCellLocatorXPath(columnId, listName);
        By cellTextLocator = By.xpath(cellLocatorXpath + "//span[not(@id)]");
        String backgroundColor = getCssValue(cellTextLocator, "background-color");
        return backgroundColor.equals("rgba(255, 208, 103, 1)");
    }

    public boolean canListBeSelected(String listName) {
        By listCheckboxLocator = getListNameCheckboxLocator(listName);
        return isElementEnabledMilliseconds(listCheckboxLocator);
    }

    public boolean isEditPermissionIconDisplayed(String listName) {
        String cellLocatorXpath = getCellLocatorXPath(PERMISSION_COLUMN_ID, listName);
        By editPermissionIconLocator = By.xpath(cellLocatorXpath + "//button[text()='edit']");
        return isElementVisibleMilliseconds(editPermissionIconLocator);
    }

    public boolean isEditNameIconDisplayed(String listName) {
        String cellLocatorXpath = getCellLocatorXPath(LIST_NAME_COLUMN_ID, listName);
        By editNameIconLocator = By.xpath(cellLocatorXpath + "//button[text()='edit']");
        return isElementVisibleMilliseconds(editNameIconLocator);
    }

    public boolean areEditIconsDisplayed(String listName) {
        boolean editNameIconDisplayed = isEditNameIconDisplayed(listName);
        boolean editPermissionIconDisplayed = isEditPermissionIconDisplayed(listName);
        UI_LOGGER.info("Edit name icon displayed: " + editNameIconDisplayed);
        UI_LOGGER.info("Edit permission icon displayed: " + editPermissionIconDisplayed);
        return editNameIconDisplayed && editPermissionIconDisplayed;
    }

    private By getListNameCheckboxLocator(String listName) {
        return By.xpath("//div[child::div[@col-id='name' and descendant::span[text()='" + listName + "']]]//div[@col-id='selectionCheckbox']//input");
    }

    private String getCellLocatorXPath(String columnId, String listName) {
        if (columnId.equals("name")) {
            return "//div[@col-id='name' and (descendant::span[text()='" + listName + "'] or descendant::input[@value='" + listName + "'])]";
        } else {
            return "//div[@col-id='name' and (descendant::span[text()='" + listName + "'] or descendant::input[@value='" + listName + "'])]//following-sibling::div[@col-id='" + columnId + "']";
        }
    }

    /***** MODAL TO ADD NEW LIST/LISTS  ******/
    public static class NewListModal extends InsightsNavigationMenu {
        private final String MODAL_BODY_XPATH = "//div[@data-qa='AddList']";
        private final By LIST_NAME_INPUT_LOCATOR = By.xpath(MODAL_BODY_XPATH + "//input[@type='text']");
        private final By ADD_BUTTON_LOCATOR = By.xpath(MODAL_BODY_XPATH + "//button[text()='Add']");
        private final By ADD_AND_CLOSE_BUTTON_LOCATOR = By.xpath(MODAL_BODY_XPATH + "//button[text()='Add & Close']");
        private final By CANCEL_BUTTON_LOCATOR = By.xpath(MODAL_BODY_XPATH + "//button[text()='Cancel']");
        private final By ADD_BUTTON_SPINNER_LOCATOR = By.xpath(MODAL_BODY_XPATH + "//button[text()='Add']//i");

        public NewListModal(WebDriver driver) {
            super(driver);
            findElementVisibleMilliseconds(By.xpath(MODAL_BODY_XPATH));
        }

        public void insertListName(String listName) {
            setText(LIST_NAME_INPUT_LOCATOR, listName);
        }

        public void selectPermission(Enums.ProductListPermission permission) {
            String permissionLabel = permission.getPermissionTypeForUI();
            By permissionRadioCheckboxLocator = By.xpath(MODAL_BODY_XPATH + "//span[following-sibling::span[text()='" + permissionLabel + "']]/input");
            clickElement(permissionRadioCheckboxLocator);
        }

        public void insertListSettings(String listName, Enums.ProductListPermission permission) {
            insertListName(listName);
            selectPermission(permission);
        }

        public ProductListsPage createNewListAndCloseModal(String listName, Enums.ProductListPermission permission) {
            insertListSettings(listName, permission);
            clickElement(ADD_AND_CLOSE_BUTTON_LOCATOR);
            return new ProductListsPage(driver);
        }

        public NewListModal createNewList(String listName, Enums.ProductListPermission permission) {
            insertListSettings(listName, permission);
            clickElement(ADD_BUTTON_LOCATOR);
            return waitForSpinnerToDisappear();
        }

        public ProductListsPage createNewLists(LinkedHashMap<String, Enums.ProductListPermission> lists) {
            for (Map.Entry<String, Enums.ProductListPermission> list : lists.entrySet()) {
                createNewList(list.getKey(), list.getValue());
            }
            clickElement(CANCEL_BUTTON_LOCATOR);
            return new ProductListsPage(driver);
        }

        public ProductListsPage cancelCreatingANewList() {
            waitForSpinnerToDisappear();
            clickElement(CANCEL_BUTTON_LOCATOR);
            return new ProductListsPage(driver);
        }

        public void clickAddButton() {
            clickElement(ADD_BUTTON_LOCATOR);
        }

        public void clickAddAndCloseButton() {
            clickElement(ADD_AND_CLOSE_BUTTON_LOCATOR);
        }

        public boolean isErrorDisplayedForDuplicateList(String listName) {
            By errorLocator = By.xpath(MODAL_BODY_XPATH + "//div[@role='alert']//span[contains(text(),'" + "\"" + listName + "\"" + " already exists')]");
            return isElementVisible(errorLocator, MAX_WAIT_TIME_SECS);
        }

        public NewListModal waitForSpinnerToDisappear() {
            waitForElementToBeInvisible(ADD_BUTTON_SPINNER_LOCATOR, MAX_WAIT_TIME_SECS);
            return this;
        }
    }


    /***** BOTTOM ACTION BAR  ******/
    public static class BottomActionBar extends ProductListsPage {
        private final By DELETE_LIST_BUTTON_LOCATOR = By.xpath(BOTTOM_ACTION_BAR_XPATH + "//button[contains(text(),'Delete')]");
        private final By DELETE_CONFIRMATION_BUTTON_LOCATOR = By.xpath("//div[@data-qa='DeleteConfirmation']//button[contains(text(),'Delete Product List')]");

        public BottomActionBar(WebDriver driver) {
            super(driver);
            findElementVisibleMilliseconds(By.xpath(BOTTOM_ACTION_BAR_XPATH));
        }

        public ProductListsPage deleteLists() {
            clickElement(DELETE_LIST_BUTTON_LOCATOR);
            clickElement(DELETE_CONFIRMATION_BUTTON_LOCATOR);
            waitForElementToBeInvisible(DELETE_CONFIRMATION_BUTTON_LOCATOR);
            return new ProductListsPage(driver);
        }

        public ProductListsPage clickSubmitButton() throws InterruptedException {
            scrollIntoViewAndClick(SUBMIT_BUTTON_LOCATOR);
            return new ProductListsPage(driver);
        }
    }
}
