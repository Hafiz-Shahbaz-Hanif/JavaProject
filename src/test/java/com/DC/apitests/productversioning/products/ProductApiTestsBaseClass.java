package com.DC.apitests.productversioning.products;

import com.DC.apitests.productversioning.ApiTestConfig;
import com.DC.db.productVersioning.*;
import com.DC.testcases.BaseClass;
import com.DC.utilities.apiEngine.apiServices.productversioning.CompanyApiService;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.apiEngine.models.requests.productVersioning.CreateProductMasterRequestBody;
import com.DC.utilities.apiEngine.models.requests.productVersioning.ProductVariantRequestBodyBase;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductEditableVariantDataSetBase;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductMaster;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;

import static com.DC.utilities.SecurityAPI.changeInsightsCompanyAndGetJwt;
import static com.DC.utilities.SecurityAPI.loginAndGetJwt;

public class ProductApiTestsBaseClass extends BaseClass {

    protected static String jwt;

    protected static String localeId;

    protected static String localeId_2;

    protected static String localeId_3;

    protected static final ApiTestConfig.TestConfig TEST_CONFIG = ApiTestConfig.getTestConfig();

    protected static final ProductMasterCollection PRODUCT_MASTER_COLLECTION = new ProductMasterCollection();

    protected static Logger logger = Logger.getLogger(ProductApiTestsBaseClass.class);

    @BeforeTest(alwaysRun = true)
    public void setupTests() throws Exception {
        logger.info("Setting up product versioning api tests");
        jwt = loginAndGetJwt(TEST_CONFIG.loginEndpoint, TEST_CONFIG.supportUsername, TEST_CONFIG.password);
        jwt = changeInsightsCompanyAndGetJwt(jwt, TEST_CONFIG.companyID, TEST_CONFIG.companyName);
        var company = CompanyApiService.getCompany(jwt);
        localeId = company.getLocaleId("en-US");
        localeId_2 = company.getLocaleId("es-MX");
        localeId_3 = company.getLocaleId("fr-FR");
    }

    public void cleanupAddedProduct(String productUniqueId) throws Exception {
        ProductMaster productMaster = PRODUCT_MASTER_COLLECTION.getProductMaster(productUniqueId, TEST_CONFIG.companyID);

        if (productMaster == null) {
            return;
        }
        ProductVersioningApiService.deleteProductMaster(productMaster._id, jwt);
    }

    protected ProductMaster addProductMasterIfNeeded(CreateProductMasterRequestBody productToAdd) throws Exception {
        ProductMaster productMaster = PRODUCT_MASTER_COLLECTION.getProductMaster(productToAdd.uniqueId, TEST_CONFIG.companyID);

        if (productMaster == null) {
            ProductVersioningApiService.createProductMaster(productToAdd, jwt);
            productMaster = PRODUCT_MASTER_COLLECTION.getProductMaster(productToAdd.uniqueId, TEST_CONFIG.companyID);
        }

        return productMaster;
    }

    protected ProductMaster createProductVariantIfNeeded(ProductMaster productMaster, String localeId) throws Exception {
        int localeVariantSetsCount = (int) productMaster.variantSets.live
                .stream()
                .filter(set -> set.localeId.equals(localeId)).count();
        if (localeVariantSetsCount == 0) {
            ProductVersioningApiService.createProductVariant(productMaster._id, localeId, jwt);
            productMaster = PRODUCT_MASTER_COLLECTION.getProductMaster(productMaster.uniqueId, TEST_CONFIG.companyID);
        }
        return productMaster;
    }

    protected void verifyProductBaseData(
            ProductEditableVariantDataSetBase dataSetBaseCreated,
            ProductMaster productMaster,
            ProductVariantRequestBodyBase requestBodyBase,
            String expectedSourceSetId
    ) {
        Assert.assertFalse(dataSetBaseCreated.isEditable, "isEditable field was true");
        Assert.assertEquals(dataSetBaseCreated.level.getLevel(), requestBodyBase.level, "Instance level didn't match with the expected type");
        Assert.assertEquals(dataSetBaseCreated.companyId, productMaster.companyId, "CompanyId didn't match with the expected id");
        Assert.assertEquals(dataSetBaseCreated.productMasterId, productMaster._id, "Data was not stored in correct product master");
        Assert.assertEquals(dataSetBaseCreated.locale, requestBodyBase.localeId, "Data was not stored in correct locale");
        Assert.assertNull(dataSetBaseCreated.retailerId, "RetailerId was not null");
        Assert.assertNull(dataSetBaseCreated.campaignId, "CampaignId was not null");
        Assert.assertEquals(dataSetBaseCreated.meta.sourceSetId, expectedSourceSetId, "SourceSetId didn't match with the expected id");
    }
}
