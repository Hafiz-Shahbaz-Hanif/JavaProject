package com.DC.utilities.XLUtils;

import com.DC.objects.productVersioning.ProductPropertiesExportExcelData;
import com.DC.objects.productVersioning.VariantImportExcelData;

import java.io.*;
import java.net.URI;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.DC.constants.ProductVersioningConstants.PRODUCT_IMPORT_EXPORT_BASE_HEADERS;
import static com.DC.utilities.SharedMethods.downloadFileFromUrl;

public class ProductPropertiesXLUtils extends XLUtils {

    public static final List<String> EXPECTED_PRODUCT_PROPERTIES_DEFAULT_HEADERS = Stream.concat(PRODUCT_IMPORT_EXPORT_BASE_HEADERS.stream(), Stream.of("product name", "rpc"))
            .collect(Collectors.toList());

    public static List<VariantImportExcelData> downloadFileAndGetVariantDataToImport(String directory, String url) throws Exception {
        String path = directory + Paths.get(new URI(url).getPath()).getFileName().toString();
        downloadFileFromUrl(url, path);
        return getVariantDataToImport(path);
    }

    public static List<LinkedHashMap<String, String>> downloadFileAndGetProductDataToImport(String directory, String url) throws Exception {
        String path = directory + Paths.get(new URI(url).getPath()).getFileName().toString();
        downloadFileFromUrl(url, path);
        return getDataToImport(path);
    }

    public static List<VariantImportExcelData> getVariantDataToImport(String path) throws IOException {
        List<VariantImportExcelData> excelData = new ArrayList<>();
        List<List<String>> sheetData = getSheetDataByRow(path, false);
        List<String> headers = sheetData.get(0);

        for (int rowNum = 1; rowNum <= sheetData.size() - 1; rowNum++) {
            VariantImportExcelData variantImportExcelData = getVariantImportRowData(sheetData, headers, rowNum);
            excelData.add(variantImportExcelData);
        }

        return excelData;
    }

    private static VariantImportExcelData getVariantImportRowData(List<List<String>> sheetData, List<String> headers, int rowNum) {
        List<String> rowData = sheetData.get(rowNum);
        List<VariantImportExcelData.PropertyData> properties = new ArrayList<>();

        VariantImportExcelData variantImportExcelData = new VariantImportExcelData();

        int propertyColumnsCount;

        // Check if the last column is "product list" or not
        if (headers.get(headers.size() - 1).equals("product list")) {
            propertyColumnsCount = headers.size() - 2;
            variantImportExcelData.productListName = rowData.get(headers.size() - 1);
        } else {
            propertyColumnsCount = headers.size() - 1;
        }

        for (int column = 7; column <= propertyColumnsCount; column++) {
            VariantImportExcelData.PropertyData property = new VariantImportExcelData.PropertyData();
            property.id = headers.get(column);
            property.value = rowData.get(column);
            properties.add(property);
        }

        variantImportExcelData.productIdentifier = rowData.get(0);
        variantImportExcelData.locale = rowData.get(1);
        variantImportExcelData.retailer = rowData.get(2);
        variantImportExcelData.campaign = rowData.get(3);
        variantImportExcelData.productName = rowData.get(4);
        variantImportExcelData.thumbnail = rowData.get(5);
        variantImportExcelData.rpc = rowData.get(6);
        variantImportExcelData.properties = properties;
        return variantImportExcelData;
    }

    public static List<LinkedHashMap<String, String>> getDataToImport(String path) throws IOException {
        List<LinkedHashMap<String, String>> excelData = new ArrayList<>();
        List<List<String>> sheetData = getSheetDataByRow(path, false);
        List<String> headers = sheetData.get(0);

        for (int rowNum = 1; rowNum <= sheetData.size() - 1; rowNum++) {
            excelData.add(getImportRowDataMap(sheetData, headers, rowNum));
        }

        return excelData;
    }

    private static LinkedHashMap<String, String> getImportRowDataMap(List<List<String>> sheetData, List<String> headers, int rowNum) {
        List<String> rowData = sheetData.get(rowNum);
        LinkedHashMap<String, String> rowDataWithColumn = new LinkedHashMap<>();

        for (int column = 0; column <= headers.size() - 1; column++) {
            rowDataWithColumn.put(headers.get(column), rowData.get(column));
        }
        return rowDataWithColumn;
    }

    public static List<ProductPropertiesExportExcelData> getProductPropertiesExported(String path) throws IOException {
        List<ProductPropertiesExportExcelData> excelData = new ArrayList<>();
        List<List<String>> sheetData = getSheetDataByRow(path, false);
        List<String> headers = sheetData.get(0);

        for (int rowNum = 1; rowNum <= sheetData.size() - 1; rowNum++) {
            ProductPropertiesExportExcelData exportedRowData = getProductPropertyExportedRowData(sheetData, headers, rowNum);
            excelData.add(exportedRowData);
        }

        return excelData;
    }

    private static ProductPropertiesExportExcelData getProductPropertyExportedRowData(List<List<String>> sheetData, List<String> headers, int rowNum) {
        List<String> rowData = sheetData.get(rowNum);
        List<ProductPropertiesExportExcelData.PropertyData> properties = new ArrayList<>();

        for (int column = 6; column <= headers.size() - 1; column++) {
            ProductPropertiesExportExcelData.PropertyData property = new ProductPropertiesExportExcelData.PropertyData();
            property.id = headers.get(column);
            property.value = rowData.get(column);
            properties.add(property);
        }

        ProductPropertiesExportExcelData productPropertyExported = new ProductPropertiesExportExcelData();
        productPropertyExported.productIdentifier = rowData.get(0);
        productPropertyExported.locale = rowData.get(1);
        productPropertyExported.retailer = rowData.get(2);
        productPropertyExported.campaign = rowData.get(3);
        productPropertyExported.productName = rowData.get(4);
        productPropertyExported.rpc = rowData.get(5);
        productPropertyExported.properties = properties;
        return productPropertyExported;
    }
}
