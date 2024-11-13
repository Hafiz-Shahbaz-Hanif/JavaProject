package com.DC.pageobjects.adc.execute.productManager.products;

import com.DC.pageobjects.adc.navigationMenus.InsightsNavigationMenu;
import com.DC.utilities.enums.Enums;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ExportModal extends InsightsNavigationMenu {
    private final By CREATE_EXPORT_BUTTON = By.xpath("//button[text()='Create Export']");
    private final By ALL_PRODUCTS_RADIO_CHECKBOX = By.xpath("//input[@name='product-settings-radio-buttons-group' and @value='all']");
    private final By ONLY_SELECTED_PRODUCTS_RADIO_CHECKBOX = By.xpath("//input[@name='product-settings-radio-buttons-group' and @value='selected']");
    private final By CANCEL_BUTTON = By.xpath("//div[@data-qa='ProductExports']//button[text()='Cancel']");

    public ExportModal(WebDriver driver) {
        super(driver);
        findElementVisible(CREATE_EXPORT_BUTTON);
    }

    public boolean isOnlySelectedProductsOptionEnabled() {
        return isElementEnabled(ONLY_SELECTED_PRODUCTS_RADIO_CHECKBOX);
    }

    public boolean isCreateExportButtonEnabled() {
        return isElementEnabled(CREATE_EXPORT_BUTTON);
    }

    public boolean isPropertySettingsSectionDisplayed() {
        var propertySettingsSection = By.xpath("//label[text()='Property Settings']");
        return isElementVisible(propertySettingsSection);
    }

    public ProductsPage clickCancelButton() {
        clickElement(CANCEL_BUTTON);
        waitForElementToBeInvisibleInMilliseconds(CREATE_EXPORT_BUTTON);
        return new ProductsPage(driver);
    }

    public ProductsPage clickCreateExportButton() {
        clickElement(CREATE_EXPORT_BUTTON);
        waitForElementToBeInvisible(CREATE_EXPORT_BUTTON);
        return new ProductsPage(driver);
    }

    public ExportModal selectProductSettings(boolean exportAllProducts) {
        if (exportAllProducts) {
            selectElement(ALL_PRODUCTS_RADIO_CHECKBOX);
        } else {
            selectElement(ONLY_SELECTED_PRODUCTS_RADIO_CHECKBOX);
        }
        return this;
    }

    public ExportModal selectDataTypesToExport(List<Enums.ExportSubType> dataTypesToExport) {
        for (Enums.ExportSubType dataType : dataTypesToExport) {
            var dataTypeCheckbox = By.xpath("//div[@name='data-types-checkbox-group']//input[@value='" + dataType.getExportSubTypeForValueInUI() + "']");
            selectElement(dataTypeCheckbox);
        }
        return this;
    }

    public ExportModal selectAllDataTypes() {
        var dataTypesToExport = By.xpath("//div[@name='data-types-checkbox-group']//input[not(@disabled)]");
        selectAllElements(dataTypesToExport, java.time.Duration.ofMillis(500));
        return this;
    }

    public ExportModal deselectDataTypes(List<Enums.ExportSubType> dataTypesToExport) {
        for (Enums.ExportSubType dataType : dataTypesToExport) {
            var dataTypeCheckbox = By.xpath("//div[@name='data-types-checkbox-group']//input[@value='" + dataType.getExportSubTypeForValueInUI() + "']");
            deselectElement(dataTypeCheckbox);
        }
        return this;
    }

    public ExportModal selectDataSettings(Enums.ProductVariantType dataToExport) {
        var dataSettingsRadioButton = By.xpath("//input[@name='data-settings-radio-buttons-group' and @value='" + dataToExport.getType() + "']");
        selectElement(dataSettingsRadioButton);
        return this;
    }

    public ExportModal selectPropertySettings(boolean exportAllProperties) {
        var valueToSelect = exportAllProperties ? "allProperties" : "onlyShowing";
        var propertySettingsRadioButton = By.xpath("//input[@name='property-settings-radio-buttons-group' and @value='" + valueToSelect + "']");
        selectElement(propertySettingsRadioButton);
        return this;
    }

    public String getSelectedProductSettings() {
        var selectedProductSettings = By.xpath("//input[@name='product-settings-radio-buttons-group' and @checked]");
        return getAttribute(selectedProductSettings, "value");
    }

    public List<String> getAvailableDataTypes() {
        var dataTypesToExport = By.xpath("//div[@name='data-types-checkbox-group']//input[not(@disabled)]");
        var dataTypesElements = findElementsPresentMilliseconds(dataTypesToExport);
        return dataTypesElements.stream().map(dataType -> dataType.getAttribute("value")).collect(Collectors.toList());
    }

    public List<String> getSelectedDataTypes() {
        var selectedProductSettings = By.xpath("//div[@name='data-types-checkbox-group']");
        var dataTypesElementValue = getAttribute(selectedProductSettings, "value");
        List<String> valuesSelected;

        if (dataTypesElementValue.isEmpty()) {
            return new ArrayList<>();
        } else {
            valuesSelected = Arrays.stream(dataTypesElementValue.split(",")).collect(Collectors.toList());
        }

        return new ArrayList<>(valuesSelected);
    }

    public String getSelectedDataSettings() {
        var dataSettingsSelectedOption = By.xpath("//input[@name='data-settings-radio-buttons-group' and parent::span[contains(@class,'checked')]]");
        return isElementPresentMilliseconds(dataSettingsSelectedOption) ? getAttribute(dataSettingsSelectedOption, "value") : null;
    }

    public String getSelectedPropertySettings() {
        var propertySettingsSelectedOption = By.xpath("//input[@name='property-settings-radio-buttons-group' and parent::span[contains(@class,'checked')]]");
        return isElementPresentMilliseconds(propertySettingsSelectedOption) ? getAttribute(propertySettingsSelectedOption, "value") : null;
    }
}