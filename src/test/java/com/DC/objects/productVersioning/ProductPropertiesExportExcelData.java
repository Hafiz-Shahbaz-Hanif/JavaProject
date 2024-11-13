package com.DC.objects.productVersioning;

import com.DC.utilities.enums.Enums;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class ProductPropertiesExportExcelData {

    public String productIdentifier;

    public String locale;

    public String retailer;

    public String campaign;

    public String rpc;
    public String productName;

    public List<PropertyData> properties;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductPropertiesExportExcelData)) return false;
        ProductPropertiesExportExcelData that = (ProductPropertiesExportExcelData) o;
        return Objects.equals(productIdentifier, that.productIdentifier)
                && Objects.equals(locale, that.locale)
                && Objects.equals(retailer, that.retailer)
                && Objects.equals(campaign, that.campaign)
                && Objects.equals(rpc, that.rpc)
                && Objects.equals(productName, that.productName)
                && Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productIdentifier, locale, retailer, campaign, rpc, productName, properties);
    }

    public static class PropertyData {

        public String id;

        public String value;

        @Override
        public String toString() {
            return "{" +
                    "id='" + id + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PropertyData)) return false;
            PropertyData that = (PropertyData) o;
            return Objects.equals(id, that.id) && Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, value);
        }
    }

    @Override
    public String toString() {
        return "{" +
                "productIdentifier='" + productIdentifier + '\'' +
                ", locale='" + locale + '\'' +
                ", retailer='" + retailer + '\'' +
                ", campaign='" + campaign + '\'' +
                ", rpc='" + rpc + '\'' +
                ", productName='" + productName + '\'' +
                ", properties=" + properties +
                '}';
    }

    @JsonIgnore
    public com.DC.utilities.enums.Enums.ProductVariantLevel getProductLevel() {
        if (isBlank(this.retailer) && isBlank(this.campaign)) {
            return Enums.ProductVariantLevel.GLOBAL;
        }
        if (!isBlank(this.retailer) && isBlank(this.campaign)) {
            return Enums.ProductVariantLevel.RETAILER;
        }
        if (!isBlank(this.retailer) && !isBlank(this.campaign)) {
            return Enums.ProductVariantLevel.RETAILER_CAMPAIGN;
        }
        if (isBlank(this.retailer) && !isBlank(this.campaign)) {
            return Enums.ProductVariantLevel.GLOBAL_CAMPAIGN;
        }
        return null;
    }

}
