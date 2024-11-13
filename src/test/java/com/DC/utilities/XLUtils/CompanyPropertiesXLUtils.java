package com.DC.utilities.XLUtils;

import com.DC.objects.productVersioning.CompanyPropertyExportExcelData;
import com.DC.utilities.apiEngine.models.responses.productVersioning.CompanyProperties;
import com.DC.utilities.apiEngine.models.responses.productVersioning.CompanyPropertiesBase;
import com.DC.utilities.enums.Enums;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class CompanyPropertiesXLUtils extends XLUtils {
    public static final List<String> EXPECTED_HEADERS = asList(
            "property id",
            "property name",
            "help text",
            "data type",
            "dropdown values",
            "property group",
            "multiple values"
    );

    public static List<CompanyPropertyExportExcelData> getExportedCompanyProperties(String path) throws IOException {
        List<CompanyPropertyExportExcelData> excelData = new ArrayList<>();
        List<List<String>> sheetData = getSheetDataByRow(path, true);

        for (int rowNum = 1; rowNum <= sheetData.size() - 1; rowNum++) {
            CompanyPropertyExportExcelData exportedRowData = getExportedCompanyPropertyRowData(sheetData, rowNum);
            excelData.add(exportedRowData);
        }

        return excelData;
    }

    private static CompanyPropertyExportExcelData getExportedCompanyPropertyRowData(List<List<String>> sheetData, int rowNum) {
        List<String> rowData = sheetData.get(rowNum);
        CompanyPropertyExportExcelData companyPropertyExported = new CompanyPropertyExportExcelData();
        companyPropertyExported.propertyId = rowData.get(0);
        companyPropertyExported.propertyName = rowData.get(1);
        companyPropertyExported.helpText = rowData.get(2);
        companyPropertyExported.dataType = Enums.PropertyType.fromText(rowData.get(3));
        companyPropertyExported.dropdownValues = rowData.get(4) == null ? null : asList(rowData.get(4).split("\\|"));
        companyPropertyExported.propertyGroup = rowData.get(5);
        companyPropertyExported.multipleValues = Boolean.parseBoolean(rowData.get(6));
        return companyPropertyExported;
    }

    public static List<CompanyProperties.Property> getCompanyPropertiesInImportFile(String path) throws IOException {
        List<CompanyProperties.Property> excelData = new ArrayList<>();
        List<List<String>> sheetData = getSheetDataByRow(path, true);

        for (int rowNum = 1; rowNum <= sheetData.size() - 1; rowNum++) {
            CompanyProperties.Property rowData = getCompanyPropertyRowData(sheetData, rowNum);
            excelData.add(rowData);
        }

        return excelData;
    }

    private static CompanyProperties.Property getCompanyPropertyRowData(List<List<String>> sheetData, int rowNum) {
        List<String> rowData = sheetData.get(rowNum);
        CompanyProperties.Property companyProperty = new CompanyProperties.Property();

        companyProperty.id = rowData.get(0);
        companyProperty.name = rowData.get(1);
        companyProperty.type = Enums.PropertyType.fromText(rowData.get(2));

        if (sheetData.get(0).contains("dropdown values")) {
            companyProperty.dropdownValues = getDropdownValues(rowData.get(3));
            companyProperty.group = rowData.get(4);
            companyProperty.helpText = rowData.get(5);
            companyProperty.allowMultipleValues = Boolean.parseBoolean(rowData.get(6));
        } else {
            companyProperty.group = rowData.get(3);
            companyProperty.helpText = rowData.get(4);
            companyProperty.allowMultipleValues = Boolean.parseBoolean(rowData.get(5));
        }
        return companyProperty;
    }

    private static List<CompanyPropertiesBase.PropertyDropdownValue> getDropdownValues(String dropdownValues) {
        if (dropdownValues == null || dropdownValues.isEmpty()) {
            return null;
        }
        List<CompanyPropertiesBase.PropertyDropdownValue> dropdownValuesList = new ArrayList<>();
        String[] values = dropdownValues.split("\\|");
        for (String value : values) {
            CompanyPropertiesBase.PropertyDropdownValue dropdownValue = new CompanyPropertiesBase.PropertyDropdownValue();
            dropdownValue.id = value;
            dropdownValue.name = value;
            dropdownValuesList.add(dropdownValue);
        }
        return dropdownValuesList;
    }
}
