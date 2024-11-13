package com.DC.objects.productVersioning;

import com.DC.utilities.enums.Enums;

import java.util.List;

public class CompanyPropertyExportExcelData {

    public String propertyId;

    public String propertyName;

    public String helpText;

    public Enums.PropertyType dataType;

    public List<String> dropdownValues;
    
    public boolean multipleValues;

    public String propertyGroup;
}
