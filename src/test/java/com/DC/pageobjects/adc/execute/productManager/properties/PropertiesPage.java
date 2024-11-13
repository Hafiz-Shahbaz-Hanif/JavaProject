package com.DC.pageobjects.adc.execute.productManager.properties;

import com.DC.pageobjects.adc.navigationMenus.InsightsNavigationMenu;
import com.DC.pageobjects.filters.MultiselectFilter;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.models.responses.productVersioning.CompanyPropertiesBase;
import com.DC.utilities.enums.Enums;
import com.DC.utilities.sharedElements.*;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class PropertiesPage extends InsightsNavigationMenu {
    private final Duration MAX_WAIT_TIME_MILLISECONDS = Duration.ofMillis(300);
    private final String PAGE_HEADER = "//div[@data-qa='PropertiesHeader']";
    private final By SEARCH_INPUT_LOCATOR = By.xpath("//div[@data-qa='PropertiesHeader']//input");
    private final By PROPERTIES_DISPLAYED_COUNT_LOCATOR = By.xpath(PAGE_HEADER + "//h6");
    private final By DELETE_BUTTON_LOCATOR = By.xpath("//button[text()='Delete']");
    private final By IMPORT_BUTTON_LOCATOR = By.xpath("//button[text()='Import']");
    private final By SKELETON_LOCATOR = By.xpath("//span[contains(@class,'MuiSkeleton')]");
    public final String DEFAULT_GROUP = "Unassigned Properties";
    public final String DEFAULT_DIGITAL_ASSET_GROUP = "Unassigned Digital Assets";

    public SingleSelectDropdown singleSelectDropdown;
    public AGTableCommonFeatures tableCommonFeatures;

    public PropertiesPage(WebDriver driver) {
        super(driver);
        findElementVisible(By.xpath(PAGE_HEADER));
        waitForElementToBeInvisible(SKELETON_LOCATOR, Duration.ofSeconds(30));
        tableCommonFeatures = new AGTableCommonFeatures(driver);
        singleSelectDropdown = new SingleSelectDropdown(driver);
        waitForDOMStabilization();
    }

    public int getNumberDisplayedNextToSearchInput() {
        String textInElement = getTextFromElementMilliseconds(PROPERTIES_DISPLAYED_COUNT_LOCATOR);
        return SharedMethods.extractIntegerFromString(textInElement);
    }

    public PropertiesPage searchForProperty(String searchTerm) {
        setTextAndHitEnter(SEARCH_INPUT_LOCATOR, searchTerm);
        return new PropertiesPage(driver);
    }

    public PropertiesPage emptyOutSearchInput() {
        setTextAndHitEnter(SEARCH_INPUT_LOCATOR, "");
        return this;
    }

    public AddPropertyModal openModalToAddProperty() throws InterruptedException {
        singleSelectDropdown.openDropdownMenu("Add");
        singleSelectDropdown.selectOption("Add Property");
        return new AddPropertyModal(driver);
    }

    public AddPropertyGroupModal openModalToAddPropertyGroup() throws InterruptedException {
        singleSelectDropdown.openDropdownMenu("Add");
        singleSelectDropdown.selectOption("Add Property Group");
        return new AddPropertyGroupModal(driver);
    }

    public GenericMultiListModal openModalToDeleteProperties() {
        clickElement(DELETE_BUTTON_LOCATOR);
        return new GenericMultiListModal(driver);
    }

    public PropertiesPage deleteProperties(List<String> propertiesToDelete) throws InterruptedException {
        if (!propertiesToDelete.isEmpty()) {
            GenericMultiListModal modal = openModalToDeleteProperties();
            modal.moveOptionsToTheRight(propertiesToDelete);
            modal.applyChanges();
            return confirmDeletion();
        }
        return this;
    }

    public PropertiesPage deleteProperty(String idOfPropertyToDelete) {
        By deleteIconLocator = By.xpath("//div[@row-id='" + idOfPropertyToDelete + "']//button[@data-qa='delete']");
        searchForProperty(idOfPropertyToDelete);
        clickElement(deleteIconLocator);
        confirmDeletion();
        return this;
    }

    public PropertiesPage confirmDeletion() {
        By confirmDeleteButton = By.xpath("//div[@data-qa='ModalWrapper']//button[text()='Yes, Delete' or text()='Yes, delete']");
        clickElement(confirmDeleteButton);
        return new PropertiesPage(driver);
    }

    public PropertiesPage exportAllProperties() throws InterruptedException {
        singleSelectDropdown.openDropdownMenu("Export");
        singleSelectDropdown.selectOption("Export All");
        return this;
    }

    public PropertiesPage exportFilteredProperties() throws InterruptedException {
        singleSelectDropdown.openDropdownMenu("Export");
        singleSelectDropdown.selectOption("Export Filtered");
        return this;
    }

    public ImportModal clickImportButton() {
        clickElement(IMPORT_BUTTON_LOCATOR);
        return new ImportModal(driver);
    }

    public boolean doesGroupExist(String groupName) {
        try {
            scrollUntilGroupIsPresent(groupName);
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    public PropertiesPage collapseGroup(String groupToCollapse) {
        By expandLessIcon = By.xpath("//div[child::h6[text()='" + groupToCollapse + "']]//following-sibling::div/button[text()='expand_less']");
        scrollUntilGroupIsPresent(groupToCollapse);
        clickElement(expandLessIcon);
        waitForDOMStabilization();
        return this;
    }

    public PropertiesPage expandGroup(String groupToExpand) {
        By expandLessIcon = By.xpath("//div[child::h6[text()='" + groupToExpand + "']]//following-sibling::div/button[text()='expand_more']");
        scrollUntilGroupIsPresent(groupToExpand);
        clickElement(expandLessIcon);
        waitForDOMStabilization();
        return this;
    }

    public PropertiesPage expandGroupIfCollapsed(String groupToExpand) {
        if (!isGroupExpanded(groupToExpand)) {
            return expandGroup(groupToExpand);
        } else {
            return this;
        }
    }

    public boolean isGroupExpanded(String groupName) {
        By groupRowLocator = By.xpath(tableCommonFeatures.TABLE_FULL_WIDTH_XPATH + "//div[descendant::h6[text()='" + groupName + "'] and @role='row']");
        scrollUntilGroupIsPresent(groupName);
        boolean ariaExpanded = Boolean.parseBoolean(getAttribute(groupRowLocator, "aria-expanded"));

        List<String> propertiesInGroup = getIdsOfPropertiesInGroup(groupName);
        return ariaExpanded && !propertiesInGroup.isEmpty();
    }

    public List<String> getIdsOfPropertiesInGroup(String groupName) {
        scrollUntilGroupIsPresent(groupName);
        groupName = groupName.replaceAll(" ", "-");
        By groupPropertiesLocator = By.xpath(tableCommonFeatures.TABLE_VIEWPORT_XPATH + "//div[contains(@class,'" + groupName + "')]");
        waitForDOMStabilization();
        List<WebElement> propertiesInGroup = findElementsPresentMilliseconds(groupPropertiesLocator);
        if (!propertiesInGroup.isEmpty()) {
            try {
                return sortPropertiesByRowId(propertiesInGroup);
            } catch (StaleElementReferenceException ex) {
                propertiesInGroup = findElementsPresentMilliseconds(groupPropertiesLocator);
                return sortPropertiesByRowId(propertiesInGroup);
            }
        } else {
            return new ArrayList<>();
        }
    }

    public List<CompanyPropertiesBase.PropertyBase> getPropertiesInGroup(String groupName) {
        List<CompanyPropertiesBase.PropertyBase> propertiesInGroup = new ArrayList<>();
        List<String> propertiesIdsInGroup = getIdsOfPropertiesInGroup(groupName);
        for (String propertyId : propertiesIdsInGroup) {
            propertiesInGroup.add(getProperty(propertyId));
        }
        return propertiesInGroup;
    }

    public String getGroup(String propertyId) {
        By rowLocatorXPath = By.xpath(tableCommonFeatures.TABLE_VIEWPORT_XPATH + "//div[@row-id='" + propertyId + "']");
        WebElement row = findElementPresent(rowLocatorXPath, MAX_WAIT_TIME_MILLISECONDS);
        String rowClass = row.getAttribute("class");
        String groupName = rowClass.substring(rowClass.indexOf("group_") + 6, rowClass.indexOf(" ", rowClass.indexOf("group_")));
        groupName = groupName.replaceAll("-", " ");
        return groupName;
    }

    public String getName(String propertyId) {
        String cellLocatorXPath = getCellLocatorXPath("name", propertyId);
        By propertyNameLocator = By.xpath(cellLocatorXPath + "//span//span");
        return getTextFromPresentElement(propertyNameLocator, MAX_WAIT_TIME_MILLISECONDS);
    }

    public String getHelpText(String propertyId) {
        String cellLocatorXPath = getCellLocatorXPath("helpText", propertyId);
        By helpTextLocator = By.xpath(cellLocatorXPath + "//span//span");
        return getTextFromPresentElement(helpTextLocator, MAX_WAIT_TIME_MILLISECONDS);
    }

    public Enums.PropertyType getType(String propertyId) {
        String cellLocatorXPath = getCellLocatorXPath("type", propertyId);
        By propertyTypeLocator = By.xpath(cellLocatorXPath + "//span");
        String type = getTextFromPresentElement(propertyTypeLocator, MAX_WAIT_TIME_MILLISECONDS);
        return Enums.PropertyType.fromText(type);
    }

    public String getAllowMultipleValues(String propertyId) {
        String cellLocatorXPath = getCellLocatorXPath("allowMultipleValues", propertyId);
        By allowMultipleValuesLocator = By.xpath(cellLocatorXPath + "//span");
        return getTextFromPresentElement(allowMultipleValuesLocator, MAX_WAIT_TIME_MILLISECONDS);
    }

    public CompanyPropertiesBase.PropertyBase getPropertyData(String propertyId) {
        waitForDOMStabilization();
        try {
            return getProperty(propertyId);
        } catch (StaleElementReferenceException ex) {
            return getProperty(propertyId);
        }
    }

    public boolean isPropertyDisplayed(String propertyId) {
        By rowLocator = By.xpath(tableCommonFeatures.TABLE_VIEWPORT_XPATH + "//div[@row-id='" + propertyId + "']");
        return isElementPresentMilliseconds(rowLocator);
    }

    public boolean isPropertyNameDisplayed(String propertyName) {
        By rowLocator = By.xpath(tableCommonFeatures.TABLE_VIEWPORT_XPATH + "//div[@col-id='name' and descendant::span[text()='" + propertyName + "']]");
        return isElementPresentMilliseconds(rowLocator);
    }

    public List<CompanyPropertiesBase.PropertyBase> getAllPropertiesData() {
        LinkedHashMap<Integer, CompanyPropertiesBase.PropertyBase> propertiesMap = new LinkedHashMap<>();

        scrollToCenterIfTableScrollable(tableCommonFeatures.TABLE_LOCATOR);

        double currentPosition;
        double pixelsToScroll = getPixelsToScroll(tableCommonFeatures.TABLE_LOCATOR, 15);

        do {
            waitForDOMStabilization();
            currentPosition = getVerticalScrollPosition(tableCommonFeatures.TABLE_LOCATOR);
            var rowsDisplayed = findElementsVisible(tableCommonFeatures.ROWS_LOCATOR, Duration.ofSeconds(2));

            for (var row : rowsDisplayed) {
                try {
                    getPropertyInfoAndAddItToList(row, propertiesMap);
                } catch (StaleElementReferenceException | NoSuchElementException ex) {
                    break;
                }
            }
            scrollElementVertically(pixelsToScroll, tableCommonFeatures.TABLE_LOCATOR);

        } while (currentPosition != getVerticalScrollPosition(tableCommonFeatures.TABLE_LOCATOR));

        var sortedMap = new TreeMap<>(propertiesMap);
        return new ArrayList<>(sortedMap.values());
    }

    public PropertiesPage rearrangePropertyPosition(String propertyToMove, String rearrangementProperty) {
        By dragIconLocator = By.xpath("//div[@row-id='" + propertyToMove + "']/div[last()]//button[@data-qa='drag']");
        By newPositionLocator = By.xpath("//div[@row-id='" + rearrangementProperty + "']/div");

        scrollUntilPropertyIsPresent(propertyToMove);
        waitForDOMStabilization();
        dragAndDrop(dragIconLocator, newPositionLocator);
        return new PropertiesPage(driver);
    }

    public boolean isReorderingWentWrongErrorDisplayed() {
        String errorMessage = "Something went wrong reordering your properties.";
        return isNoteDisplayedWithMessage(Enums.NoteType.INFO, errorMessage);
    }

    public PropertiesPage filterPropertyType(Enums.Property propertyType) throws InterruptedException {
        MultiselectFilter filter = new MultiselectFilter(driver);
        filter.openFilter("Filter Property Type");
        if (propertyType.equals(Enums.Property.ALL)) {
            filter.selectAllOptions();
            filter.applyFilter();
        } else {
            filter.deselectAllAndSelectOption(propertyType.getPropertyType());
            filter.applyFilter();
        }
        return new PropertiesPage(driver);
    }

    public void deleteGroups(List<String> groupsToDelete) throws InterruptedException {
        singleSelectDropdown.openDropdownMenu("Manage Groups");
        GenericMultiListModal modal = singleSelectDropdown.selectOption("Delete Groups", GenericMultiListModal.class);
        modal.moveAllOptionsToTheLeft();
        modal.moveOptionsToTheRight(groupsToDelete);
        modal.applyChanges();
        confirmDeletion();
    }

    public OrderPropertyGroupsModal openModalToReorderGroups() throws Exception {
        singleSelectDropdown.openDropdownMenu("Manage Groups");
        return singleSelectDropdown.selectOption("Manage Group Order", OrderPropertyGroupsModal.class);
    }

    public void reorderPropertyGroups(LinkedHashMap<List<String>, Enums.PropertyGroupType> propertyGroups) throws Exception {
        OrderPropertyGroupsModal modal = openModalToReorderGroups();
        modal.rearrangePropertyGroups(propertyGroups);
        modal.saveGroupOrder();
    }

    public void clickEditIcon(String propertyId, String columnId) throws InterruptedException {
        var cellLocatorXPath = getCellLocatorXPath(columnId, propertyId);
        var editButtonLocator = By.xpath(cellLocatorXPath + "//button[text()='edit']");
        var saveValueIconLocator = By.xpath(cellLocatorXPath + "//button[text()='save']");
        scrollIntoViewAndClick(editButtonLocator);
        findElementVisibleMilliseconds(saveValueIconLocator);
    }

    public int getNumberOfHighlightedCells() {
        return getElementCountMilliseconds(By.xpath("//div[@role='gridcell' and descendant::span[contains(@class,'css-gq3olp')]]"));
    }

    public void editCellValue(String propertyId, String columnId, String newValue) {
        var cellLocatorXPath = getCellLocatorXPath(columnId, propertyId);
        By inputLocator = By.xpath(cellLocatorXPath + "//input");
        scrollUntilPropertyIsPresent(propertyId);
        setText(inputLocator, newValue);
    }

    public void clickSaveValueIcon(String propertyId, String columnId) throws InterruptedException {
        String cellLocatorXpath = getCellLocatorXPath(columnId, propertyId);
        By saveValueIconLocator = By.xpath(cellLocatorXpath + "//button[text()='save']");
        scrollIntoViewAndClick(saveValueIconLocator);
    }

    public void clickApplyAllChangesButton() {
        By applyAllChangesButtonLocator = By.xpath("//button[text()='Apply All Changes']");
        clickElement(applyAllChangesButtonLocator);
    }

    public PropertiesPage applyAllChanges() {
        clickApplyAllChangesButton();
        return new PropertiesPage(driver);
    }

    private void scrollUntilGroupIsPresent(String groupName) {
        By groupRowLocator = By.xpath(tableCommonFeatures.TABLE_FULL_WIDTH_XPATH + "//div[descendant::h6[text()='" + groupName + "'] and @role='row']");
        String errorMsg = "Row  '" + groupName + "' was not found in the table.";
        scrollDownToElement(tableCommonFeatures.TABLE_LOCATOR, groupRowLocator, 10, errorMsg);
    }

    private void scrollUntilPropertyIsPresent(String propertyId) {
        By rowLocatorXPath = By.xpath(tableCommonFeatures.TABLE_VIEWPORT_XPATH + "//div[@row-id='" + propertyId + "']");
        String errorMsg = "Row  '" + propertyId + "' was not found in the table";
        scrollDownToElement(tableCommonFeatures.TABLE_LOCATOR, rowLocatorXPath, 10, errorMsg);
    }

    private String getCellLocatorXPath(String columnId, String propertyId) {
        return "//div[@row-id='" + propertyId + "']//div[@col-id='" + columnId + "']";
    }

    private CompanyPropertiesBase.PropertyBase getProperty(String propertyId) {
        scrollUntilPropertyIsPresent(propertyId);
        CompanyPropertiesBase.PropertyBase property = new CompanyPropertiesBase.PropertyBase();
        property.id = propertyId;
        property.name = getName(propertyId);
        property.helpText = getHelpText(propertyId);
        property.type = getType(propertyId);
        property.allowMultipleValues = Objects.equals(getAllowMultipleValues(propertyId), "Allowed");
        property.group = getGroup(propertyId);
        return property;
    }

    private static List<String> sortPropertiesByRowId(List<WebElement> propertiesInGroup) {
        return propertiesInGroup.stream().sorted((property1, property2) -> {
            String property1RowIndex = property1.getAttribute("row-index");
            String property2RowIndex = property2.getAttribute("row-index");
            return Integer.parseInt(property1RowIndex) - Integer.parseInt(property2RowIndex);
        }).map(property -> property.getAttribute("row-id")).collect(Collectors.toList());
    }

    private void getPropertyInfoAndAddItToList(WebElement row, LinkedHashMap<Integer, CompanyPropertiesBase.PropertyBase> properties) {
        String propertyId = row.getAttribute("row-id");
        int propertyRowIndex = Integer.parseInt(row.getAttribute("row-index"));

        boolean propertyAlreadyAdded = properties.containsKey(propertyRowIndex);
        if (!propertyAlreadyAdded) {
            CompanyPropertiesBase.PropertyBase property = getPropertyData(propertyId);
            properties.put(propertyRowIndex, property);
        }
    }
}
