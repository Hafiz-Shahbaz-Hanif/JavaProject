package com.DC.utilities.apiEngine.models.responses.productVersioning;

import com.DC.utilities.enums.Enums;

public abstract class ProductVariantDataSetBase {

    public Enums.ProductVariantType type;

    public Enums.ProductVariantLevel level;

    public String retailerId;

    public String campaignId;

    public String companyId;

    public String productMasterId;

    public String locale;

    public ProductDataSetMeta meta;

    public ProductVariantDataSetBase() {}
}
