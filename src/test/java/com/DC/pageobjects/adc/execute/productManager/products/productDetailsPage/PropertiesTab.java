package com.DC.pageobjects.adc.execute.productManager.products.productDetailsPage;

import com.DC.utilities.enums.Enums;
import com.DC.pageobjects.filters.MultiselectFilter;
import com.DC.utilities.sharedElements.SingleSelectDropdown;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class PropertiesTab extends ProductDetailsPage {
    private static final By SEARCH_INPUT = By.xpath("//input[@placeholder='Search property name or values ...']");
    private static final By SECTIONS = By.xpath("//div[@data-qa='PropertiesTab']//div[child::h4]");

    public PropertiesTab(WebDriver driver) {
        super(driver);
        findElementVisible(SECTIONS);
    }

    public List<String> getPropertyNamesInGroup(String version, String locale, String groupName) {
        String versionXPath = getVersionSectionXPath(version, locale);
        String groupTableXPath = getGroupTableXPath(groupName);
        String propertyNamesXPath = versionXPath + groupTableXPath + "//div[@data-qa='PropertyEditorRow']//h6";
        UI_LOGGER.info("Full xpath to get property names: " + propertyNamesXPath);
        List<WebElement> elements = findElementsVisibleMilliseconds(By.xpath(propertyNamesXPath));
        return getTextFromElements(elements);
    }

    public List<String> getPropertyGroupNamesInVersion(String version, String locale) {
        String versionXPath = getVersionSectionXPath(version, locale);
        String groupNamesXPath = versionXPath + "//div[@data-qa='PropertyGroupTable']/div//h5";
        UI_LOGGER.info("Full xpath to get group names: " + groupNamesXPath);
        List<WebElement> elements = findElementsVisibleMilliseconds(By.xpath(groupNamesXPath));
        return getTextFromElements(elements);
    }

    public PropertiesTab filterPropertyType(Enums.Property propertyType) throws InterruptedException {
        MultiselectFilter filter = new MultiselectFilter(driver);
        filter.openFilter("Filter Property Type");
        if (propertyType.equals(Enums.Property.ALL)) {
            filter.selectAllOptions();
            filter.applyFilter();
        } else {
            filter.deselectAllAndSelectOption(propertyType.getPropertyType());
            filter.applyFilter();
        }
        return new PropertiesTab(driver);
    }

    public SingleSelectDropdown openActionsDropdown(String version, String locale) {
        var versionXPath = getVersionSectionXPath(version, locale);
        var actionsButton = By.xpath(versionXPath + "//button[text()='Actions']");
        var singleSelectDropdown = new SingleSelectDropdown(driver);
        singleSelectDropdown.openDropdownMenu(actionsButton);
        return singleSelectDropdown;
    }

    public SingleSelectDropdown clickAddPropertyButton(String version, String locale) {
        var singleSelectDropdown = openActionsDropdown(version, locale);
        singleSelectDropdown.selectOption("Add Property");
        return singleSelectDropdown;
    }

    public void clickEditIcon(String version, String locale, String propertyName) {
        var versionXPath = getVersionSectionXPath(version, locale);
        var rowXPath = getPropertyRowXPath(propertyName);
        var editIconXPath = versionXPath + rowXPath + "//button[text()='edit']";
        var editIconLocator = By.xpath(editIconXPath);
        scrollToElement(editIconLocator);
        clickElement(editIconLocator);
    }

    public boolean areDeleteAndEditIconsEnabled(String version, String locale, String propertyName) {
        var versionXPath = getVersionSectionXPath(version, locale);
        var rowXPath = getPropertyRowXPath(propertyName);
        var editIconLocator = By.xpath(versionXPath + rowXPath + "//button[text()='edit']");
        var deleteIconLocator = By.xpath(versionXPath + rowXPath + "//button[text()='delete']");
        scrollToElement(editIconLocator);
        return isElementEnabled(editIconLocator) && isElementEnabled(deleteIconLocator);
    }

    public void clickSaveIcon(String version, String locale, String propertyName) {
        var versionXPath = getVersionSectionXPath(version, locale);
        var rowXPath = getPropertyRowXPath(propertyName);
        var saveIconXPath = versionXPath + rowXPath + "//button[text()='save']";
        var saveIconLocator = By.xpath(saveIconXPath);
        scrollToElement(saveIconLocator);
        clickElement(saveIconLocator);
    }

    public List<String> getPropertyValues(String version, String locale, String propertyName) {
        var versionXPath = getVersionSectionXPath(version, locale);
        var rowXPath = getPropertyRowXPath(propertyName);
        var propertyValuesXPath = versionXPath + rowXPath + "//span";
        return getTextFromElementsMilliseconds((By.xpath(propertyValuesXPath)));
    }

    public List<String> getDropdownPropertyOptions(String version, String locale, String propertyName) {
        clickEditIcon(version, locale, propertyName);
        var versionXPath = getVersionSectionXPath(version, locale);
        var dropdownInput = By.xpath(versionXPath + "//input");
        var singleSelectDropdown = new SingleSelectDropdown(driver);
        singleSelectDropdown.openDropdownMenu(dropdownInput);
        var dropdownOptions = singleSelectDropdown.getDropdownOptions();
        var closeIcon = By.xpath(versionXPath + "//button[text()='close']");
        singleSelectDropdown.closeDropdownMenu(closeIcon);
        return dropdownOptions;
    }

    public void editValueOfDropdownPropertyAndSave(String version, String locale, String propertyName, String optionToSelect) {
        clickEditIcon(version, locale, propertyName);
        var versionXPath = getVersionSectionXPath(version, locale);
        var dropdownInput = By.xpath(versionXPath + "//input");
        var singleSelectDropdown = new SingleSelectDropdown(driver);
        singleSelectDropdown.openDropdownMenu(dropdownInput);
        singleSelectDropdown.selectOption(optionToSelect);
        clickSaveIcon(version, locale, propertyName);
    }

    public boolean isPropertyValueHighlighted(String version, String locale, String propertyName) {
        var versionXPath = getVersionSectionXPath(version, locale);
        var rowXPath = getPropertyRowXPath(propertyName);
        var highlightedValueLocator = versionXPath + rowXPath + "//span[contains(@class,'css-gq3olp')]";
        return isElementVisibleMilliseconds(By.xpath(highlightedValueLocator));
    }

    public boolean isPropertyMarkedAsToBeDeleted(String version, String locale, String propertyName) {
        var versionXPath = getVersionSectionXPath(version, locale);
        var rowXPath = "//div[@data-qa='PropertyEditorRow' and descendant::h6[text()='" + propertyName + "' and contains(@class,'css-1x3v65c')]]";
        return isElementVisibleMilliseconds(By.xpath(versionXPath + rowXPath));
    }

    public int getPanelsDisplayedCount() {
        return getElementCount(SECTIONS);
    }

    public void expandSection(String version, String localeName) {
        var expandMoreButton = "//div[child::div[child::h4[text()='" + version + " Version'] and child::span[text()='" + localeName + "']]]//button[text()='expand_more']";
        clickElement(By.xpath(expandMoreButton));
    }

    private String getVersionSectionXPath(String version, String locale) {
        return "//div[@data-qa='PropertiesTab']/div[2]/div[descendant::div[(child::h4[text()='" + version + " Version']) and (child::span[text()='" + locale + "'])]]";
    }

    private String getGroupTableXPath(String groupName) {
        return "//div[@data-qa='PropertyGroupTable' and descendant::h5[text()='" + groupName + "']]";
    }

    private String getPropertyRowXPath(String propertyName) {
        return "//div[@data-qa='PropertyEditorRow' and descendant::h6[text()='" + propertyName + "']]";
    }
}
