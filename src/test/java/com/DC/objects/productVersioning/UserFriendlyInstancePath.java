package com.DC.objects.productVersioning;

import com.DC.pageobjects.filters.ProductsLeftSideFilter;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.apiEngine.models.responses.productVersioning.Company;
import com.DC.utilities.apiEngine.models.responses.productVersioning.InstancePathBase;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductVariantInstancePath;
import com.DC.utilities.enums.Enums;

import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class UserFriendlyInstancePath {

    public String productIdentifier;

    public String localeName;

    public String retailerName;

    public String campaignName;

    public UserFriendlyInstancePath(String productIdentifier, String localeName, String retailerName, String campaignName) {
        this.productIdentifier = productIdentifier;
        this.localeName = localeName;
        this.retailerName = retailerName;
        this.campaignName = campaignName;
    }

    @Override
    public String toString() {
        return "{" +
                "productIdentifier='" + productIdentifier + '\'' +
                ", localeName='" + localeName + '\'' +
                ", retailerName='" + retailerName + '\'' +
                ", campaignName='" + campaignName + '\'' +
                '}';
    }

    public String getProductVersion() {
        if (isBlank(this.retailerName) && isBlank(this.campaignName)) {
            return "Base";
        }
        if (!isBlank(this.retailerName) && isBlank(this.campaignName)) {
            return this.retailerName;
        }
        if (!isBlank(this.retailerName) && !isBlank(this.campaignName)) {
            return this.retailerName + " " + this.campaignName;
        }
        if (isBlank(this.retailerName) && !isBlank(this.campaignName)) {
            return this.campaignName;
        }
        return null;
    }

    public com.DC.utilities.enums.Enums.ProductVariantLevel getProductLevel() {
        if (isBlank(this.retailerName) && isBlank(this.campaignName)) {
            return Enums.ProductVariantLevel.GLOBAL;
        }
        if (!isBlank(this.retailerName) && isBlank(this.campaignName)) {
            return Enums.ProductVariantLevel.RETAILER;
        }
        if (!isBlank(this.retailerName) && !isBlank(this.campaignName)) {
            return Enums.ProductVariantLevel.RETAILER_CAMPAIGN;
        }
        if (isBlank(this.retailerName) && !isBlank(this.campaignName)) {
            return Enums.ProductVariantLevel.GLOBAL_CAMPAIGN;
        }
        return null;
    }

    public ProductsLeftSideFilter.FilterType getProductVersionFilter() {
        if (isBlank(this.retailerName) && isBlank(this.campaignName)) {
            return ProductsLeftSideFilter.FilterType.BASE;
        }
        if (!isBlank(this.retailerName) && isBlank(this.campaignName)) {
            return ProductsLeftSideFilter.FilterType.RETAILER;
        }
        if (!isBlank(this.retailerName) && !isBlank(this.campaignName)) {
            return ProductsLeftSideFilter.FilterType.RETAILER_CAMPAIGN;
        }
        if (isBlank(this.retailerName) && !isBlank(this.campaignName)) {
            return ProductsLeftSideFilter.FilterType.CAMPAIGN;
        }
        return null;
    }

    public InstancePathBase convertToInstancePathBase(Company company, String jwt) throws Exception {
        var productMasterId = ProductVersioningApiService.getProductMasterByUniqueId(this.productIdentifier, jwt)._id;
        String localeId = company.getLocaleId(this.localeName);
        String retailerId = company.getRetailerId(this.retailerName);
        String campaignId = company.getCampaignId(this.campaignName);
        return new InstancePathBase(productMasterId, localeId, retailerId, campaignId);
    }

    public ProductVariantInstancePath convertToInstancePath(Company company, String jwt, Enums.ProductVariantType type) throws Exception {
        var productMasterId = ProductVersioningApiService.getProductMasterByUniqueId(this.productIdentifier, jwt)._id;
        String localeId = company.getLocaleId(this.localeName);
        String retailerId = company.getRetailerId(this.retailerName);
        String campaignId = company.getCampaignId(this.campaignName);
        return new ProductVariantInstancePath(productMasterId, localeId, type, retailerId, campaignId);
    }

    public UserFriendlyInstancePath getBaseFriendlyInstancePath() {
        return new UserFriendlyInstancePath(this.productIdentifier, this.localeName, null, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserFriendlyInstancePath)) return false;
        UserFriendlyInstancePath that = (UserFriendlyInstancePath) o;
        return Objects.equals(productIdentifier, that.productIdentifier) &&
                Objects.equals(localeName, that.localeName) &&
                Objects.equals(retailerName, that.retailerName) &&
                Objects.equals(campaignName, that.campaignName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productIdentifier, localeName, retailerName, campaignName);
    }
}
