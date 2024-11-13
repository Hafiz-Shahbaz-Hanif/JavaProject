package com.DC.pageobjects.adc.execute.productManager.products.bulkEditKeywordsPage;

import com.DC.pageobjects.adc.execute.productManager.products.BulkEditTableBase;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductVariantKeywords;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BulkEditKeywordsPage extends BulkEditTableBase {
    private final By BULK_ADD_KEYWORDS_BUTTON_LOCATOR = By.xpath(PAGE_BODY_XPATH + "//button[text()='Bulk Add Keywords']");
    private final By BULK_DELETE_KEYWORDS_BUTTON_LOCATOR = By.xpath(PAGE_BODY_XPATH + "//button[text()='Bulk Delete Keywords']");

    public BulkEditKeywordsPage(WebDriver driver) {
        super(driver);
        waitForDOMStabilization(Duration.ofSeconds(3));
    }

    public BulkEditKeywordsPage searchForKeywordValues(String propertyValue) {
        setTextAndHitEnter(SEARCH_INPUT_LOCATOR, propertyValue);
        waitForDOMStabilization();
        return new BulkEditKeywordsPage(driver);
    }

    public BulkAddDeleteKeywordsModal clickBulkAddKeywordsButton() throws InterruptedException {
        scrollIntoViewAndClick(BULK_ADD_KEYWORDS_BUTTON_LOCATOR);
        return new BulkAddDeleteKeywordsModal(driver);
    }

    public BulkAddDeleteKeywordsModal clickBulkDeleteKeywordsButton() throws InterruptedException {
        scrollIntoViewAndClick(BULK_DELETE_KEYWORDS_BUTTON_LOCATOR);
        return new BulkAddDeleteKeywordsModal(driver);
    }

    public List<String> getUniqueIdsOfInstancesThatContainKeywordValue(String keywordValue) {
        By instanceIdLocator = By.xpath("//div[@role='gridcell' and @col-id='version' and following-sibling::div[descendant::span[contains(text(),'" + keywordValue + "')]]]//span//span");
        return getTextFromElementsMilliseconds(instanceIdLocator);
    }

    public ProductVariantKeywords getKeywordSetDisplayed(String uniqueIdOfInstance) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.ALWAYS);
        objectMapper.configOverride(List.class).setSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY));
        ObjectNode keywords = objectMapper.createObjectNode();

        LinkedHashMap<String, List<String>> rowValues = getColumnIdsAndCellValuesFromInstance(uniqueIdOfInstance);

        for (Map.Entry<String, List<String>> entry : rowValues.entrySet()) {
            String bucket = entry.getKey();

            if (!entry.getValue().isEmpty()) {
                String cellValue = entry.getValue().get(0);
                List<String> keywordValues = Arrays.asList(cellValue.split("\\|"));
                ArrayNode node = objectMapper.createArrayNode();
                keywordValues.stream().map(String::trim).forEach(node::add);
                keywords.set(bucket, node);
            } else {
                ArrayNode node = objectMapper.createArrayNode();
                keywords.set(bucket, node);
            }
        }

        ProductVariantKeywords expectedKeywordSet = objectMapper.treeToValue(keywords, ProductVariantKeywords.class);
        return objectMapper.readValue(objectMapper.writeValueAsString(expectedKeywordSet), ProductVariantKeywords.class);
    }

}
