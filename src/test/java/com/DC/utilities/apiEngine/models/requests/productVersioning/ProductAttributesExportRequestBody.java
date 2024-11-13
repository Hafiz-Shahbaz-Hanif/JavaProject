package com.DC.utilities.apiEngine.models.requests.productVersioning;

import com.DC.utilities.enums.Enums;

public class ProductAttributesExportRequestBody {

    public String productMasterId;

    public String localeId;

    public Enums.ProductVariantType type;
    public ProductAttributesExportRequestBody(String productMasterId, String localeId, Enums.ProductVariantType type) {
        this.productMasterId = productMasterId;
        this.localeId = localeId;
        this.type = type;
    }
}

