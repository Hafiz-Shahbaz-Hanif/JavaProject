package com.DC.utilities.XLUtils;

import com.DC.constants.ProductVersioningConstants;
import com.DC.objects.productVersioning.ProductKeywordsDataInExcel;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductVariantKeywords;
import com.DC.utilities.enums.Enums;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

public class ProductKeywordsXLUtils extends XLUtils {

    public static final List<String> SUBSEQUENT_COLUMNS = asList(
            "keyword bucket",
            "search phrase"
    );

    public static final List<String> SUBSEQUENT_COLUMNS_EXPORT = Stream.concat(SUBSEQUENT_COLUMNS.stream(), Stream.of("volume"))
            .collect(Collectors.toList());

    public static List<String> EXPECTED_HEADERS_IN_EXPORT_FILE = Stream.of(ProductVersioningConstants.PRODUCT_IMPORT_EXPORT_BASE_HEADERS, SUBSEQUENT_COLUMNS_EXPORT)
            .flatMap(List::stream)
            .collect(Collectors.toList());

    public static List<ProductKeywordsDataInExcel> getProductKeywordsInFile(String path) throws IOException {
        List<ProductKeywordsDataInExcel> excelData = new ArrayList<>();
        List<List<String>> sheetData = getSheetDataByRow(path, true);

        for (int rowNum = 1; rowNum <= sheetData.size() - 1; rowNum++) {
            ProductKeywordsDataInExcel exportedRowData = getKeywordDataInRow(sheetData, rowNum);
            excelData.add(exportedRowData);
        }

        return excelData;
    }

    private static ProductKeywordsDataInExcel getKeywordDataInRow(List<List<String>> sheetData, int rowNum) {
        List<String> rowData = sheetData.get(rowNum);

        ProductKeywordsDataInExcel keywordExported = new ProductKeywordsDataInExcel();
        keywordExported.productIdentifier = rowData.get(0);
        keywordExported.locale = rowData.get(1);
        keywordExported.retailer = rowData.get(2);
        keywordExported.campaign = rowData.get(3);
        keywordExported.keywordBucket = (rowData.get(4) == null) ? null : Enums.KeywordBucketType.fromText(rowData.get(4));
        keywordExported.searchPhrase = rowData.get(5);
        keywordExported.volume = (rowData.size() > 6) ? Long.parseLong(rowData.get(6)) : 0;
        return keywordExported;
    }

    public static ProductVariantKeywords getWholeKeywordSetWithNullsAsEmptyArray(List<ProductKeywordsDataInExcel> group) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.ALWAYS);
        objectMapper.configOverride(List.class).setSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY));
        ObjectNode keywords = objectMapper.createObjectNode();

        for (ProductKeywordsDataInExcel row : group) {
            ArrayNode node = objectMapper.createArrayNode();
            List<String> expectedSearchPhrases = Arrays.asList(row.searchPhrase.split("\\|"));
            expectedSearchPhrases.stream().map(String::trim).forEach(node::add);
            keywords.set(row.keywordBucket.getBucketType(), node);
        }

        ProductVariantKeywords expectedKeywordSet = objectMapper.treeToValue(keywords, ProductVariantKeywords.class);
        return objectMapper.readValue(objectMapper.writeValueAsString(expectedKeywordSet), ProductVariantKeywords.class);
    }
}
