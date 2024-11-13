package com.DC.objects.productVersioning;

import java.util.List;

public class VariantImportExcelData {

    public String productIdentifier;

    public String locale;

    public String retailer;

    public String campaign;
    
    public String rpc;
    public String productName;
    public String thumbnail;

    public List<PropertyData> properties;

    public String productListName;

    public static class PropertyData {

        public String id;

        public String value;
    }
}
