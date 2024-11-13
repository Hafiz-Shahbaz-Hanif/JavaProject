package com.DC.utilities.XLUtils;

import com.DC.objects.productVersioning.ProductAttributesDataInExcel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class ProductAttributesXLUtils extends XLUtils {
    public static final List<String> EXPECTED_HEADERS = asList("product identifier", "locale", "product name", "category name", "path", "category id", "attribute", "segment id", "tagged value", "segment value id", "volume");

    public static List<ProductAttributesDataInExcel> getProductAttributesInFile(String path) throws IOException {
        List<ProductAttributesDataInExcel> excelData = new ArrayList<>();
        List<List<String>> sheetData = getSheetDataByRow(path, true);

        for (int rowNum = 1; rowNum <= sheetData.size() - 1; rowNum++) {
            ProductAttributesDataInExcel exportedRowData = getAttributeDataInRow(sheetData, rowNum);
            excelData.add(exportedRowData);
        }
        return excelData;
    }

    private static ProductAttributesDataInExcel getAttributeDataInRow(List<List<String>> sheetData, int rowNum) {
        List<String> rowData = sheetData.get(rowNum);
        ProductAttributesDataInExcel attributeExported = new ProductAttributesDataInExcel();
        attributeExported.productIdentifier = rowData.get(0);
        attributeExported.locale = rowData.get(1);
        attributeExported.productName = rowData.get(2);
        attributeExported.categoryName = rowData.get(3)==null ? null: rowData.get(3);
        attributeExported.path = rowData.get(4)==null? null: rowData.get(4);
        attributeExported.categoryId = rowData.get(5)==null ? 0 : Long.parseLong(rowData.get(5));
        attributeExported.attribute = rowData.get(6)==null ? null: rowData.get(6);
        attributeExported.segmentId = rowData.get(7)==null ? 0 : Long.parseLong(rowData.get(7));
        attributeExported.taggedValue = rowData.get((8))==null? null: rowData.get(8);
        attributeExported.segmentValueId = rowData.get(9)==null ? 0 : Long.parseLong(rowData.get(9));
        attributeExported.volume = rowData.get(10)==null ? 0 : Long.parseLong(rowData.get(10));
        return attributeExported;
    }
}
