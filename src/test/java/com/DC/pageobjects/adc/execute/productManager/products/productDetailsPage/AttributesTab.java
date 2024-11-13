package com.DC.pageobjects.adc.execute.productManager.products.productDetailsPage;

import com.DC.objects.productVersioning.AttributesTableData;
import com.DC.utilities.sharedElements.AGTableCommonFeatures;
import com.DC.utilities.sharedElements.AttributeTaggingOverlay;
import com.DC.utilities.sharedElements.CategoryTree;
import org.openqa.selenium.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

public class AttributesTab extends ProductDetailsPage {
    private final By EDIT_ATTRIBUTES_BUTTON_LOCATOR = By.xpath("//div[@data-qa='AttributesActionBar']//button[text()='Edit Attributes']");


    public CategoryTree categoryTree;
    public AGTableCommonFeatures tableCommonFeatures;

    public AttributesTab(WebDriver driver) {
        super(driver);
        categoryTree = new CategoryTree(driver);
        waitForElementToBeEnabled(EDIT_ATTRIBUTES_BUTTON_LOCATOR, Duration.ofSeconds(30));
        tableCommonFeatures = new AGTableCommonFeatures(driver);
    }

    public List<String> getAttributesDisplayed() {
        var cellAttributesLocator = By.xpath("//span[contains(@id,'cell-attribute')]");
        return getTextFromElementsMilliseconds(cellAttributesLocator);
    }

    public AttributeTaggingOverlay clickEditAttributeButton() {
        clickElement(EDIT_ATTRIBUTES_BUTTON_LOCATOR);
        return new AttributeTaggingOverlay(driver);
    }

    public AttributesTab selectAndApplyTag(String tagName) {
        var attributeTaggingOverlay = clickEditAttributeButton();
        attributeTaggingOverlay.selectTagToApply(tagName);
        return attributeTaggingOverlay.clickSaveAttributesButton(AttributesTab.class);
    }

    public List<AttributesTableData> getAttributesTableData() {
        LinkedHashMap<Integer, AttributesTableData> attributesMap = new LinkedHashMap<>();

        scrollToCenterIfTableScrollable(tableCommonFeatures.TABLE_LOCATOR);

        double currentPosition;
        double pixelsToScroll = getPixelsToScroll(tableCommonFeatures.TABLE_LOCATOR, 15);

        do {
            currentPosition = getVerticalScrollPosition(tableCommonFeatures.TABLE_LOCATOR);
            var rowsDisplayed = findElementsVisible(tableCommonFeatures.ROWS_LOCATOR, Duration.ofSeconds(2));

            for (var row : rowsDisplayed) {
                try {
                    getAttributeInfoAndAddItToList(row, attributesMap);
                } catch (StaleElementReferenceException | NoSuchElementException ex) {
                    break;
                }
            }
            scrollElementVertically(pixelsToScroll, tableCommonFeatures.TABLE_LOCATOR);

        } while (currentPosition != getVerticalScrollPosition(tableCommonFeatures.TABLE_LOCATOR));

        var sortedMap = new TreeMap<>(attributesMap);
        return new ArrayList<>(sortedMap.values());
    }

    public AttributesTableData getAttributeData(String rowId) {
        waitForDOMStabilization();

        var rowXPath = tableCommonFeatures.TABLE_VIEWPORT_XPATH + "//div[@row-id='" + rowId + "']";
        var attributeNameLocator = By.xpath(rowXPath + "//div[contains(@col-id,'attribute')]//span");
        var taggedValuesLocator = By.xpath(rowXPath + "//div[contains(@col-id,'taggedAttributes')]//span");
        var taggedVolumeLocator = By.xpath(rowXPath + "//div[contains(@col-id,'taggedVolume')]//span");

        var attributeName = getTextFromElement(attributeNameLocator);
        var taggedValues = List.of(getTextFromElement(taggedValuesLocator).split(", "));
        var taggedVolume = Double.parseDouble(getTextFromElement(taggedVolumeLocator));

        return new AttributesTableData(attributeName, taggedValues, taggedVolume);
    }

    private void getAttributeInfoAndAddItToList(WebElement row, LinkedHashMap<Integer, AttributesTableData> tableData) {
        var rowId = row.getAttribute("row-id");
        var rowIndex = Integer.parseInt(row.getAttribute("row-index"));
        var attributeAlreadyAdded = tableData.containsKey(rowIndex);

        if (!attributeAlreadyAdded) {
            var attributeData = getAttributeData(rowId);
            tableData.put(rowIndex, attributeData);
        }
    }


}
