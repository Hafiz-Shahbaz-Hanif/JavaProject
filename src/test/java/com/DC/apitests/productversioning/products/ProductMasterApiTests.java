package com.DC.apitests.productversioning.products;

import com.DC.apitests.ApiValidations;
import com.DC.db.productVersioning.CompanyCollection;
import com.DC.db.productVersioning.ProductMasterVariantPropertySetCollection;
import com.DC.db.productVersioning.ProductVariantListCollection;
import com.DC.utilities.SharedMethods;
import com.DC.objects.productVersioning.UserFriendlyInstancePath;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.ProductVersioningApiRequests;
import com.DC.utilities.apiEngine.apiServices.insights.CPGData.SearchPhrases.SearchPhrasesService;
import com.DC.utilities.apiEngine.apiServices.productversioning.CompanyApiService;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductListApiService;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.apiEngine.models.requests.productVersioning.CreateProductMasterRequestBody;
import com.DC.utilities.apiEngine.models.requests.productVersioning.DeleteVariantInstancesRequestBody;
import com.DC.utilities.apiEngine.models.requests.productVersioning.ProductVariantPropertySetRequestBody;
import com.DC.utilities.apiEngine.models.responses.insights.CPGData.SearchPhrases.SearchPhraseVolume;
import com.DC.utilities.apiEngine.models.responses.productVersioning.*;
import com.DC.utilities.apiEngine.routes.productVersioning.ProductVersioningRoutes;
import com.DC.utilities.enums.Enums;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.DC.apitests.ApiValidations.*;
import static com.DC.tests.sharedAssertions.ImportAssertions.importFile;
import static com.DC.utilities.SecurityAPI.loginAndGetJwt;
import static com.DC.utilities.CommonApiMethods.callEndpoint;
import static com.DC.utilities.XLUtils.ProductPropertiesXLUtils.downloadFileAndGetVariantDataToImport;
import static java.util.Arrays.asList;

public class ProductMasterApiTests extends ProductApiTestsBaseClass {
    private static final List<String> PRODUCTS_TO_CLEANUP = new ArrayList<>();
    private static final ProductVariantListCollection LIST_COLLECTION = new ProductVariantListCollection();

    private static final CreateProductMasterRequestBody PRODUCT_TO_DELETE = new CreateProductMasterRequestBody(
            "QA-TEST-TO-DELETE",
            "QA Test TO DELETE",
            null
    );

    private static final CreateProductMasterRequestBody PRODUCT_TO_ADD = new CreateProductMasterRequestBody(
            "QA-API-MASTER",
            "QA API MASTER",
            null
    );

    private static final String PRODUCT_UNIQUE_ID_COMPOSITION_TESTS = "QA-STATIC-PRODUCT-001";

    private static final String LIST_NAME = "Static List For Automated API Tests";

    private ProductMaster productMasterForCompositionTests;

    private ProductMaster.VariantSets.Live variantSetForCompositionTests;

    ProductMasterApiTests() {
        logger = Logger.getLogger(ProductMasterApiTests.class);
        PropertyConfigurator.configure("log4j.properties");
    }

    @AfterClass(alwaysRun = true)
    public void cleanupProducts() throws Exception {
        PRODUCTS_TO_CLEANUP.add(PRODUCT_TO_ADD.uniqueId);
        for (String productIdentifier : PRODUCTS_TO_CLEANUP) {
            ProductVersioningApiService.deleteProductMasterByUniqueId(productIdentifier, jwt);
        }
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanCreateAnEmptyProductMasterRecord() throws Exception {
        PRODUCT_MASTER_COLLECTION.deleteProductMaster(PRODUCT_TO_ADD.uniqueId, TEST_CONFIG.companyID);

        Response response = ProductVersioningApiRequests.createProductMaster(PRODUCT_TO_ADD, jwt);
        ProductMaster productCreated = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductMaster.class);

        Assert.assertEquals(productCreated.name, PRODUCT_TO_ADD.name, "Name of product doesn't match with the expected name");
        Assert.assertEquals(productCreated.uniqueId, PRODUCT_TO_ADD.uniqueId, "UniqueId of product doesn't match with the expected uniqueId");
        Assert.assertEquals(productCreated.thumbnail, PRODUCT_TO_ADD.thumbnail, "Thumbnail of product doesn't match with the expected thumbnail");
        Assert.assertEquals(productCreated._version, 1, "Version of product doesn't match with the expected version");
        Assert.assertNotNull(productCreated.dateCreated, "DateCreated value was null on product created");
        Assert.assertEquals(productCreated.dateUpdated, productCreated.dateCreated, "DateUpdated value doesn't match with the dateCreated value");
        Assert.assertEquals(productCreated.companyId, TEST_CONFIG.companyID, "Product was created in a different company");
        Assert.assertNotNull(productCreated.variantSets, "Variant sets was null on product created");
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotCreateProductMaster_UnauthorizedErrorIsThrown() throws Exception {
        String username = "qa+productmanagerviewonly@juggle.com";
        String jwt = loginAndGetJwt(TEST_CONFIG.loginEndpoint, username, TEST_CONFIG.password);

        Response response = ProductVersioningApiRequests.createProductMaster(PRODUCT_TO_ADD, jwt);

        validateUnauthorizedError(response);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotCreateProductMaster_MissingParameters() throws Exception {
        String bodyWithoutParameter = "{\n" +
                "    \"name\": \"QA-InvalidProduct-Test\",\n" +
                "    \"thumbnail\": null\n" +
                "}";
        Response response = callEndpoint(ProductVersioningRoutes.getProductMasterRoutePath(), jwt, "POST", bodyWithoutParameter, "");
        String missingParameter = "uniqueId";
        validateMissingRequestParametersError(response, missingParameter);

        bodyWithoutParameter = "{\n" +
                "    \"uniqueId\": \"QA-InvalidProduct-Test\",\n" +
                "    \"thumbnail\": null\n" +
                "}";
        response = callEndpoint(ProductVersioningRoutes.getProductMasterRoutePath(), jwt, "POST", bodyWithoutParameter, "");
        missingParameter = "name";
        validateMissingRequestParametersError(response, missingParameter);

        bodyWithoutParameter = "{\n" +
                "    \"uniqueId\": \"QA-InvalidProduct-Test\",\n" +
                "    \"name\": \"QA-InvalidProduct-Test\"\n" +
                "}";
        response = callEndpoint(ProductVersioningRoutes.getProductMasterRoutePath(), jwt, "POST", bodyWithoutParameter, "");
        missingParameter = "thumbnail";
        validateMissingRequestParametersError(response, missingParameter);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotCreateProductMaster_InvalidParameters() throws Exception {
        String bodyWithInvalidParameters = "{\n" +
                "    \"uniqueId\": null,\n" +
                "    \"name\": 123,\n" +
                "    \"thumbnail\": true\n" +
                "}";
        Response response = callEndpoint(ProductVersioningRoutes.getProductMasterRoutePath(), jwt, "POST", bodyWithInvalidParameters, "");
        List<String> expectedErrors = new ArrayList<>();
        expectedErrors.add("\"uniqueId\" must be a string");
        expectedErrors.add("\"name\" must be a string");
        expectedErrors.add("\"thumbnail\" must be a string");
        validateInvalidRequestParametersError(response, expectedErrors);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotCreateProductMaster_DuplicateUniqueId() throws Exception {
        boolean productExists = PRODUCT_MASTER_COLLECTION.doesProductMasterExist(PRODUCT_TO_ADD.uniqueId, TEST_CONFIG.companyID);

        if (!productExists) {
            ProductVersioningApiService.createProductMaster(PRODUCT_TO_ADD, jwt);
        }

        Response response = ProductVersioningApiRequests.createProductMaster(PRODUCT_TO_ADD, jwt);
        String expectedError = "Type: ProductMasterError. Subtype: ProductMasterVariantError. Message: A product with this uniqueId already exists on the company.";
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotCreateProductMaster_IncorrectUniqueIdFormat() throws Exception {
        var incorrectProduct = new CreateProductMasterRequestBody("QA TEST Product", "QA Test 1", null);
        Response response = ProductVersioningApiRequests.createProductMaster(incorrectProduct, jwt);
        String expectedError = "Type: ProductMasterError. Subtype: ProductMasterVariantError. Message: Unique Ids can only contain alphanumeric characters, dashes, or underscores.";
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanGetProductMaster() throws Exception {
        String errorMessage = "Product returned doesn't match with product in database";
        ProductMaster productMaster = addProductMasterIfNeeded(PRODUCT_TO_ADD);

        Response response = ProductVersioningApiRequests.getProductMaster(productMaster._id, jwt);
        ProductMaster productReturned = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductMaster.class);
        Assert.assertEquals(
                productReturned,
                productMaster,
                errorMessage + "\nReturned product: " + productReturned + "\nProduct in database: " + productMaster
        );

        response = ProductVersioningApiRequests.getProductMasterByUniqueId(productMaster.uniqueId, jwt);
        productReturned = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductMaster.class);
        Assert.assertEquals(
                productReturned,
                productMaster,
                errorMessage + "\nReturned product: " + productReturned + "\nProduct in database: " + productMaster
        );
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotGetProductMaster_NonExistentProduct() throws Exception {
        String nonExistentProduct = UUID.randomUUID().toString();
        Response response = ProductVersioningApiRequests.getProductMaster(nonExistentProduct, jwt);
        String expectedError = "Type: ProductMasterError. Subtype: ProductMasterMissingError. Message: Could not find product master.";
        validateUnprocessableEntityError(response, expectedError);

        response = ProductVersioningApiRequests.getProductMasterByUniqueId(nonExistentProduct, jwt);
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanCreateProductVariant() throws Exception {
        ProductMaster productMasterBeforeUpdate = addProductMasterIfNeeded(PRODUCT_TO_ADD);
        String productMasterId = productMasterBeforeUpdate._id;
        ProductMaster.VariantSets variantSets = productMasterBeforeUpdate.variantSets;
        List<ProductMaster.VariantSets.Live> liveVariantSets = variantSets.live;
        List<ProductMaster.VariantSets.Staged> stagedVariantSets = variantSets.staged;

        Response response = ProductVersioningApiRequests.createProductVariant(productMasterId, localeId, jwt);
        ProductMaster returnedProduct = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductMaster.class);

        Assert.assertEquals(returnedProduct._id, productMasterId, "Live variant was created in a different product");
        Assert.assertEquals(
                returnedProduct.variantSets.live.size(),
                liveVariantSets.size() + 1,
                "Live variant sets count didn't increase by 1 after creating new live variant"
        );
        Assert.assertEquals(
                returnedProduct.variantSets.staged.size(),
                stagedVariantSets.size(),
                "Staged variant sets count changed after creating new live variant"
        );

        List<ProductMaster.VariantSets.Live> localeVariantSets = returnedProduct.variantSets.live
                .stream()
                .filter(set -> set.localeId.equals(localeId))
                .collect(Collectors.toList());
        Assert.assertNotEquals(localeVariantSets.size(), 0, "Unable to find live variant for the locale");
        Assert.assertEquals(localeVariantSets.size(), 1, "Live variant for the locale was duplicated");

        ProductMaster.VariantSets.Live localeVariantSetCreated = localeVariantSets.get(0);
        Assert.assertNotNull(localeVariantSetCreated.id, "Live variant id was null");

        String expectedVariantName = returnedProduct.name + " " + localeId;
        Assert.assertEquals(
                localeVariantSetCreated.name,
                expectedVariantName,
                "Live variant name created didn't match with the expected name"
        );

        String uniqueIdRegexPattern = "\\b" + returnedProduct.uniqueId + ":\\b\\d{13}";
        boolean uniqueIdHasCorrectFormat = Pattern.matches(uniqueIdRegexPattern, localeVariantSetCreated.uniqueId);
        Assert.assertTrue(
                uniqueIdHasCorrectFormat,
                "Live variant uniqueId created didn't match with the expected format. UniqueId was: " +
                        localeVariantSetCreated.uniqueId
        );

        Assert.assertNotNull(localeVariantSetCreated.invariantData, "Invariant data for the live variant created was null");
        Assert.assertNull(localeVariantSetCreated.invariantData.attributeSetId, "Live variant AttributeSetId was not null");
        Assert.assertTrue(localeVariantSetCreated.invariantData.brand.isEmpty(), "Live variant brand list was not empty");

        Assert.assertNotNull(localeVariantSetCreated.instances, "Instances for the live variant created was null");
        Assert.assertTrue(localeVariantSetCreated.instances.retailer.isEmpty(), "Retailer live instance was not empty");
        Assert.assertTrue(localeVariantSetCreated.instances.globalCampaign.isEmpty(), "Global campaign live instance was not empty");
        Assert.assertTrue(localeVariantSetCreated.instances.retailerCampaign.isEmpty(), "Retailer campaign live instance was not empty");

        ProductMaster.VariantSets.Live.ProductVariantInstances.ProductInstanceGlobal globalLiveInstance = localeVariantSetCreated.instances.global;
        Assert.assertNotNull(globalLiveInstance, "Global live instance was null");
        Assert.assertNotNull(globalLiveInstance.id, "Global live instance id was null");
        Assert.assertNotNull(globalLiveInstance.dateCreated, "DateCreated value of global live instance was null");
        Assert.assertEquals(
                globalLiveInstance.dateUpdated,
                globalLiveInstance.dateCreated,
                "DateUpdated value of global live instance didn't match with the dateCreated"
        );

        verifyInstanceUniqueIdIsStandardized(Enums.ProductVariantLevel.GLOBAL, localeVariantSetCreated.uniqueId, globalLiveInstance.uniqueId);

        String expectedVariantGlobalInstanceName = expectedVariantName + " global";
        Assert.assertEquals(
                globalLiveInstance.name,
                expectedVariantGlobalInstanceName,
                "Name of global live instance didn't match with the expected name"
        );
        Assert.assertNull(globalLiveInstance.thumbnail, "Thumbnail of global live instance was not null");
        Assert.assertNull(globalLiveInstance.propertySetId, "PropertySetId of global live instance was not null");
        Assert.assertNull(globalLiveInstance.keywordSetId, "KeywordSetId of global live instance was not null");
        Assert.assertNull(globalLiveInstance.digitalAssetSetId, "DigitalAssetSetId of global live instance was not null");
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanDeleteProductVariant() throws Exception {
        var productToTest = new CreateProductMasterRequestBody(
                "QA-TEST-TO-DELETE-VARIANTS",
                "QA Test TO DELETE VARIANTS",
                null
        );
        var productMaster = addProductMasterIfNeeded(productToTest);
        PRODUCTS_TO_CLEANUP.add(productMaster.uniqueId);

        // Create variant
        var localeToTest = localeId;
        productMaster = createProductVariantIfNeeded(productMaster, localeToTest);

        var company = new CompanyCollection().getCompany(TEST_CONFIG.companyID);
        var retailerIdToTest = company.retailers.get(0).systemRetailerId;

        var staticListId = cleanupProductList();

        var softAssert = new SoftAssert();

        // TESTING GLOBAL INSTANCE
        var instancePath = new InstancePathBase(productMaster._id, localeToTest, null, null);
        addInstanceToListAndReturnInstanceId(instancePath, staticListId);
        var expectedProductList = ProductListApiService.getProductList(staticListId, jwt);
        productMaster = performTestToDeleteVariant(instancePath, expectedProductList, staticListId, true, softAssert);

        // TESTING RETAILER INSTANCE
        productMaster = createProductVariantIfNeeded(productMaster, localeToTest);
        instancePath = setupInstanceAtRetailerLevel(localeToTest, retailerIdToTest, productMaster._id);
        addInstanceToListAndReturnInstanceId(instancePath, staticListId);
        expectedProductList = ProductListApiService.getProductList(staticListId, jwt);
        productMaster = performTestToDeleteVariant(instancePath, expectedProductList, staticListId, false, softAssert);

        // TESTING GLOBAL CAMPAIGN INSTANCE
        String campaignIdToTest = company.campaigns.get(0).id;
        productMaster = createProductVariantIfNeeded(productMaster, localeToTest);
        instancePath = setupInstanceAtCampaignLevel(localeToTest, campaignIdToTest, productMaster._id);
        addInstanceToListAndReturnInstanceId(instancePath, staticListId);
        expectedProductList = ProductListApiService.getProductList(staticListId, jwt);
        productMaster = performTestToDeleteVariant(instancePath, expectedProductList, staticListId, false, softAssert);

        // TESTING RETAILER CAMPAIGN INSTANCE
        productMaster = createProductVariantIfNeeded(productMaster, localeToTest);
        var set = setupInstanceAtRetailerCampaignLevel(localeToTest, retailerIdToTest, campaignIdToTest, productMaster._id);
        instancePath = new InstancePathBase(set.productMasterId, set.locale, set.retailerId, set.campaignId);
        addInstanceToListAndReturnInstanceId(instancePath, staticListId);
        expectedProductList = ProductListApiService.getProductList(staticListId, jwt);
        productMaster = performTestToDeleteVariant(instancePath, expectedProductList, staticListId, false, softAssert);

        // MIX OF ALL PRODUCTS
        productMaster = createProductVariantIfNeeded(productMaster, localeToTest);
        var instancePathBase = new InstancePathBase(productMaster._id, localeToTest, null, null);
        addInstanceToListAndReturnInstanceId(instancePathBase, staticListId);
        instancePath = setupInstanceAtRetailerLevel(localeToTest, retailerIdToTest, productMaster._id);
        addInstanceToListAndReturnInstanceId(instancePath, staticListId);
        instancePath = setupInstanceAtCampaignLevel(localeToTest, campaignIdToTest, productMaster._id);
        addInstanceToListAndReturnInstanceId(instancePath, staticListId);
        set = setupInstanceAtRetailerCampaignLevel(localeToTest, retailerIdToTest, campaignIdToTest, productMaster._id);
        instancePath = new InstancePathBase(set.productMasterId, set.locale, set.retailerId, set.campaignId);
        addInstanceToListAndReturnInstanceId(instancePath, staticListId);
        expectedProductList = ProductListApiService.getProductList(staticListId, jwt);
        performTestToDeleteVariant(instancePathBase, expectedProductList, staticListId, true, softAssert);

        cleanupProductList();
        softAssert.assertAll();
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_InstanceUniqueIdsAreProperlyStandardized() throws Exception {
        Company company = new CompanyCollection().getCompany(TEST_CONFIG.companyID);
        String retailerIdToTest = company.retailers.get(0).systemRetailerId;
        String campaignIdToTest = company.campaigns.get(0).id;

        ProductMaster productMaster = addProductMasterIfNeeded(PRODUCT_TO_ADD);
        createProductVariantIfNeeded(productMaster, localeId);

        setupInstanceAtRetailerLevel(localeId, retailerIdToTest, productMaster._id);
        setupInstanceAtCampaignLevel(localeId, campaignIdToTest, productMaster._id);
        setupInstanceAtRetailerCampaignLevel(localeId, retailerIdToTest, campaignIdToTest, productMaster._id);

        productMaster = ProductVersioningApiService.getProductMaster(productMaster._id, jwt);
        ProductMaster.VariantSets.Live variantSet = productMaster.variantSets.live
                .stream()
                .filter(set -> set.localeId.equals(localeId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        verifyInstanceUniqueIdIsStandardized(Enums.ProductVariantLevel.GLOBAL, variantSet.uniqueId, variantSet.instances.global.uniqueId);
        verifyInstanceUniqueIdIsStandardized(Enums.ProductVariantLevel.RETAILER, variantSet.uniqueId, variantSet.instances.retailer.get(0).uniqueId);
        verifyInstanceUniqueIdIsStandardized(Enums.ProductVariantLevel.GLOBAL_CAMPAIGN, variantSet.uniqueId, variantSet.instances.globalCampaign.get(0).uniqueId);
        verifyInstanceUniqueIdIsStandardized(Enums.ProductVariantLevel.RETAILER_CAMPAIGN, variantSet.uniqueId, variantSet.instances.retailerCampaign.get(0).uniqueId);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotCreateProductVariant_NonExistentLocaleId() throws Exception {
        ProductMaster productMaster = addProductMasterIfNeeded(PRODUCT_TO_ADD);
        String localeId = "000f0b1e-f05d-4caa-92c6-08f7ad8cb409";
        Response response = ProductVersioningApiRequests.createProductVariant(productMaster._id, localeId, jwt);
        String expectedError = "Type: ProductMasterError. Subtype: ProductMasterVariantError. Message: LocaleId does not exist on company.";
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotCreateProductVariant_DuplicateLocaleId() throws Exception {
        ProductMaster productMaster = addProductMasterIfNeeded(PRODUCT_TO_ADD);
        productMaster = createProductVariantIfNeeded(productMaster, localeId);
        Response response = ProductVersioningApiRequests.createProductVariant(productMaster._id, localeId, jwt);
        String expectedError = "Type: ProductMasterError. Subtype: ProductMasterVariantError. Message: A live variant for the locale already exists on product master.";
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"}, expectedExceptions = {java.lang.Exception.class}, expectedExceptionsMessageRegExp = ".*Instance already exists on variant.*")
    public void Api_Products_CannotCreateProductVariant_DuplicateInstance() throws Exception {
        var duplicateProduct = new UserFriendlyInstancePath("QA-STATIC-PRODUCT-001", "es-MX", "Amazon.com", null);
        var company = new CompanyCollection().getCompany(TEST_CONFIG.companyID);
        var product = duplicateProduct.convertToInstancePathBase(company, jwt);
        var pathOfInstanceToAdd = new InstancePathBase(product.productMasterId, product.localeId, product.retailerId, product.campaignId);
        var response = ProductVersioningApiRequests.createInstanceLive(pathOfInstanceToAdd, jwt);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanGetProductVariant() throws Exception {
        ProductMaster productMaster = addProductMasterIfNeeded(PRODUCT_TO_ADD);
        productMaster = createProductVariantIfNeeded(productMaster, localeId);

        ProductMaster.VariantSets.Live expectedLiveVariantSet = productMaster.variantSets.live
                .stream().filter(set -> set.localeId.equals(localeId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        Response response = ProductVersioningApiRequests.getProductVariantData(productMaster._id, localeId, Enums.ProductVariantType.LIVE.getType(), jwt);
        ProductMaster.VariantSets.Live liveVariantSetReturned = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductMaster.VariantSets.Live.class);
        verifyReturnedPropertySetMatchWithExpectedPropertySet(liveVariantSetReturned, expectedLiveVariantSet);

        response = ProductVersioningApiRequests.getProductVariantDataByUniqueId(productMaster.uniqueId, localeId, Enums.ProductVariantType.LIVE.getType(), jwt);
        liveVariantSetReturned = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductMaster.VariantSets.Live.class);
        verifyReturnedPropertySetMatchWithExpectedPropertySet(liveVariantSetReturned, expectedLiveVariantSet);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotGetProductVariant_NonExistentProductMaster() throws Exception {
        String randomUUID = UUID.randomUUID().toString();
        Response response = ProductVersioningApiRequests.getProductVariantData(randomUUID, localeId, Enums.ProductVariantType.LIVE.getType(), jwt);
        String expectedError = "Type: ProductMasterError. Subtype: ProductMasterMissingError. Message: Could not find product master.";
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotGetProductVariant_NonExistentVariantForLocale() throws Exception {
        ProductMaster productMaster = addProductMasterIfNeeded(PRODUCT_TO_ADD);
        ProductVersioningApiService.deleteProductMasterVariants(productMaster._id, Collections.singletonList(localeId_2), jwt);

        Response response = ProductVersioningApiRequests.getProductVariantData(productMaster._id, localeId_2, Enums.ProductVariantType.LIVE.getType(), jwt);
        String expectedError = "Type: ProductMasterError. Subtype: ProductMasterVariantError. Message: Could not find variant for the locale.";
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanDeleteProductVariantInstances() throws Exception {
        ProductMaster productMaster = addProductMasterIfNeeded(PRODUCT_TO_DELETE);
        PRODUCTS_TO_CLEANUP.add(productMaster.uniqueId);

        // Create two variants for the product master
        productMaster = createProductVariantIfNeeded(productMaster, localeId);
        productMaster = createProductVariantIfNeeded(productMaster, localeId_2);

        Company company = new CompanyCollection().getCompany(TEST_CONFIG.companyID);
        String retailerIdToTest = company.retailers.get(0).systemRetailerId;
        String retailerIdToTest2 = company.retailers.get(1).systemRetailerId;
        String campaignIdToTest = company.campaigns.get(0).id;
        String campaignIdToTest2 = company.campaigns.get(1).id;

        String staticListId = cleanupProductList();

        // Create versions of the product master at retailer level in both locales
        InstancePathBase instancePath = setupInstanceAtRetailerLevel(localeId, retailerIdToTest, productMaster._id);
        addInstanceToListAndReturnInstanceId(instancePath, staticListId);
        instancePath = setupInstanceAtRetailerLevel(localeId_2, retailerIdToTest, productMaster._id);
        addInstanceToListAndReturnInstanceId(instancePath, staticListId);
        instancePath = setupInstanceAtRetailerLevel(localeId, retailerIdToTest2, productMaster._id);
        addInstanceToListAndReturnInstanceId(instancePath, staticListId);
        instancePath = setupInstanceAtRetailerLevel(localeId_2, retailerIdToTest2, productMaster._id);
        addInstanceToListAndReturnInstanceId(instancePath, staticListId);

        // Create versions of the product master at campaign level in both locales
        instancePath = setupInstanceAtCampaignLevel(localeId, campaignIdToTest, productMaster._id);
        addInstanceToListAndReturnInstanceId(instancePath, staticListId);
        instancePath = setupInstanceAtCampaignLevel(localeId_2, campaignIdToTest, productMaster._id);
        addInstanceToListAndReturnInstanceId(instancePath, staticListId);
        instancePath = setupInstanceAtCampaignLevel(localeId, campaignIdToTest2, productMaster._id);
        addInstanceToListAndReturnInstanceId(instancePath, staticListId);
        instancePath = setupInstanceAtCampaignLevel(localeId_2, campaignIdToTest2, productMaster._id);
        addInstanceToListAndReturnInstanceId(instancePath, staticListId);

        // Create versions of the product master at retailer-campaign level in both locales
        instancePath = new InstancePathBase(productMaster._id, localeId, retailerIdToTest, campaignIdToTest);
        setupInstanceAtRetailerCampaignLevel(instancePath.localeId, instancePath.retailerId, instancePath.campaignId, productMaster._id);
        addInstanceToListAndReturnInstanceId(instancePath, staticListId);

        instancePath = new InstancePathBase(productMaster._id, localeId_2, retailerIdToTest2, campaignIdToTest);
        setupInstanceAtRetailerCampaignLevel(instancePath.localeId, instancePath.retailerId, instancePath.campaignId, productMaster._id);
        addInstanceToListAndReturnInstanceId(instancePath, staticListId);

        instancePath = new InstancePathBase(productMaster._id, localeId_2, retailerIdToTest, campaignIdToTest);
        ProductVariantPropertySet setToTest_RetailerCampaign = setupInstanceAtRetailerCampaignLevel(instancePath.localeId, instancePath.retailerId, instancePath.campaignId, productMaster._id);
        addInstanceToListAndReturnInstanceId(instancePath, staticListId);

        ProductMaster.VariantSets.Live expectedVariantSetFirstLocale = ProductVersioningApiService.getProductVariantData(
                productMaster._id,
                localeId,
                Enums.ProductVariantType.LIVE.getType(),
                jwt
        );

        ProductMaster.VariantSets.Live expectedVariantSetSecondLocale = ProductVersioningApiService.getProductVariantData(
                productMaster._id,
                localeId_2,
                Enums.ProductVariantType.LIVE.getType(),
                jwt
        );

        FriendlyProductVariantList expectedProductList = ProductListApiService.getProductList(staticListId, jwt);

        // DELETE VARIANT INSTANCE - RETAILER LEVEL BOTH LOCALES
        DeleteVariantInstancesRequestBody requestBodyToDeleteInstances = new DeleteVariantInstancesRequestBody(
                Enums.DeleteProductVariantLevel.RETAILER,
                retailerIdToTest,
                null,
                asList(localeId, localeId_2)
        );
        Response response = ProductVersioningApiRequests.deleteVariantInstances(productMaster._id, requestBodyToDeleteInstances, jwt);

        int statusCode = response.getStatusCode();
        checkResponseStatus(testMethodName.get(), "200", statusCode);

        ProductMaster.VariantSets.Live variantDataFirstLocaleAfter = ProductVersioningApiService.getProductVariantData(
                productMaster._id,
                localeId,
                Enums.ProductVariantType.LIVE.getType(),
                jwt
        );

        ProductMaster.VariantSets.Live variantDataSecondLocaleAfter = ProductVersioningApiService.getProductVariantData(
                productMaster._id,
                localeId_2,
                Enums.ProductVariantType.LIVE.getType(),
                jwt
        );

        expectedVariantSetFirstLocale.instances.retailer.removeIf(instance -> Objects.equals(instance.retailerId, retailerIdToTest));
        expectedVariantSetSecondLocale.instances.retailer.removeIf(instance -> Objects.equals(instance.retailerId, retailerIdToTest));
        verifyVariantSetIsCorrectAfterDeletion(variantDataFirstLocaleAfter, expectedVariantSetFirstLocale);
        verifyVariantSetIsCorrectAfterDeletion(variantDataSecondLocaleAfter, expectedVariantSetSecondLocale);

        expectedProductList.products.removeIf(instance -> Objects.equals(instance.retailerId, retailerIdToTest)
                && (Objects.equals(instance.localeId, localeId) || Objects.equals(instance.localeId, localeId_2))
                && instance.campaignId == null);

        FriendlyProductVariantList variantList = ProductListApiService.getProductList(staticListId, jwt);
        Assert.assertEquals(variantList.products, expectedProductList.products, "Products in list are not as expected after removing instances at retailer level");

        // DELETE VARIANT INSTANCE - CAMPAIGN LEVEL localeId
        requestBodyToDeleteInstances = new DeleteVariantInstancesRequestBody(
                Enums.DeleteProductVariantLevel.GLOBAL_CAMPAIGN,
                null,
                campaignIdToTest2,
                Collections.singletonList(localeId)
        );
        response = ProductVersioningApiRequests.deleteVariantInstances(productMaster._id, requestBodyToDeleteInstances, jwt);

        statusCode = response.getStatusCode();
        checkResponseStatus(testMethodName.get(), "200", statusCode);

        variantDataFirstLocaleAfter = ProductVersioningApiService.getProductVariantData(
                productMaster._id,
                localeId,
                Enums.ProductVariantType.LIVE.getType(),
                jwt
        );

        variantDataSecondLocaleAfter = ProductVersioningApiService.getProductVariantData(
                productMaster._id,
                localeId_2,
                Enums.ProductVariantType.LIVE.getType(),
                jwt
        );

        expectedVariantSetFirstLocale.instances.globalCampaign.removeIf(instance -> Objects.equals(instance.campaignId, campaignIdToTest2));
        verifyVariantSetIsCorrectAfterDeletion(variantDataFirstLocaleAfter, expectedVariantSetFirstLocale);
        verifyVariantSetIsCorrectAfterDeletion(variantDataSecondLocaleAfter, expectedVariantSetSecondLocale);

        expectedProductList.products.removeIf(instance -> Objects.equals(instance.campaignId, campaignIdToTest2)
                && Objects.equals(instance.localeId, localeId)
                && instance.retailerId == null);
        variantList = ProductListApiService.getProductList(staticListId, jwt);
        Assert.assertEquals(variantList.products, expectedProductList.products, "Products in list are not as expected after removing instances at campaign level");

        // DELETE VARIANT INSTANCE - RETAILER-CAMPAIGN LEVEL localeId_2
        requestBodyToDeleteInstances = new DeleteVariantInstancesRequestBody(
                Enums.DeleteProductVariantLevel.RETAILER_CAMPAIGN,
                setToTest_RetailerCampaign.retailerId,
                setToTest_RetailerCampaign.campaignId,
                Collections.singletonList(setToTest_RetailerCampaign.locale)
        );
        response = ProductVersioningApiRequests.deleteVariantInstances(productMaster._id, requestBodyToDeleteInstances, jwt);

        statusCode = response.getStatusCode();
        checkResponseStatus(testMethodName.get(), "200", statusCode);

        variantDataFirstLocaleAfter = ProductVersioningApiService.getProductVariantData(
                productMaster._id,
                localeId,
                Enums.ProductVariantType.LIVE.getType(),
                jwt
        );

        variantDataSecondLocaleAfter = ProductVersioningApiService.getProductVariantData(
                productMaster._id,
                localeId_2,
                Enums.ProductVariantType.LIVE.getType(),
                jwt
        );

        expectedVariantSetSecondLocale.instances.retailerCampaign.removeIf(instance -> Objects.equals(instance.campaignId, setToTest_RetailerCampaign.campaignId)
                && Objects.equals(instance.retailerId, setToTest_RetailerCampaign.retailerId)
        );
        verifyVariantSetIsCorrectAfterDeletion(variantDataFirstLocaleAfter, expectedVariantSetFirstLocale);
        verifyVariantSetIsCorrectAfterDeletion(variantDataSecondLocaleAfter, expectedVariantSetSecondLocale);

        expectedProductList.products.removeIf(instance -> Objects.equals(instance.campaignId, setToTest_RetailerCampaign.campaignId)
                && Objects.equals(instance.retailerId, setToTest_RetailerCampaign.retailerId)
                && Objects.equals(instance.localeId, setToTest_RetailerCampaign.locale));

        variantList = ProductListApiService.getProductList(staticListId, jwt);
        Assert.assertEquals(variantList.products, expectedProductList.products, "Products in list are not as expected after removing instances at retailer-campaign level");

        cleanupProductList();
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotDeleteProductVariantInstances_MissingParameters() throws Exception {
        String emptyPayload = "{}";
        Response response = ProductVersioningApiRequests.deleteVariantInstances(emptyPayload, UUID.randomUUID().toString(), jwt);
        List<String> missingParameters = asList("retailerToDelete", "campaignToDelete", "localeIdsToDelete", "level");
        validateMissingRequestParametersError(response, missingParameters);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotDeleteProductVariantInstances_InvalidParameters() throws Exception {
        String bodyWithInvalidParameters = "{\n" +
                "    \"level\": \"invalid\",\n" +
                "    \"retailerToDelete\": [\"6067d71a-5905-4a72-b291-3ea0c0044a59\"],\n" +
                "    \"campaignToDelete\": \"abc\",\n" +
                "    \"localeIdsToDelete\": \"abc\"\n" +
                "}";

        Response response = ProductVersioningApiRequests.deleteVariantInstances(bodyWithInvalidParameters, UUID.randomUUID().toString(), jwt);

        List<String> expectedErrors = new ArrayList<>();
        expectedErrors.add("\"retailerToDelete\" needs to be a mongo Binary object");
        expectedErrors.add("\"campaignToDelete\" needs to be a mongo Binary object");
        expectedErrors.add("\"localeIdsToDelete\" must be an array");
        expectedErrors.add("\"level\" must be one of [retailer, globalCampaign, retailerCampaign]");
        validateInvalidRequestParametersError(response, expectedErrors);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotDeleteProductVariantInstances_IncorrectLevelForIdsProvided() throws Exception {
        String expectedErrorMessage = "Type: ProductMasterError. Subtype: ProductMasterVariantError. Message: Incorrect level for retailer and campaign ids provided.";

        String randomUUID = UUID.randomUUID().toString();

        DeleteVariantInstancesRequestBody requestBodyToDeleteInstances = new DeleteVariantInstancesRequestBody(
                Enums.DeleteProductVariantLevel.RETAILER,
                null,
                randomUUID,
                new ArrayList<>()
        );
        Response response = ProductVersioningApiRequests.deleteVariantInstances(randomUUID, requestBodyToDeleteInstances, jwt);
        validateUnprocessableEntityError(response, expectedErrorMessage);

        requestBodyToDeleteInstances = new DeleteVariantInstancesRequestBody(
                Enums.DeleteProductVariantLevel.GLOBAL_CAMPAIGN,
                randomUUID,
                null,
                new ArrayList<>()
        );
        response = ProductVersioningApiRequests.deleteVariantInstances(randomUUID, requestBodyToDeleteInstances, jwt);
        validateUnprocessableEntityError(response, expectedErrorMessage);

        requestBodyToDeleteInstances.level = Enums.DeleteProductVariantLevel.RETAILER_CAMPAIGN;
        response = ProductVersioningApiRequests.deleteVariantInstances(randomUUID, requestBodyToDeleteInstances, jwt);
        validateUnprocessableEntityError(response, expectedErrorMessage);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanDeleteProductInstancesAndVariantsTogether() throws Exception {
        var productToTest = new CreateProductMasterRequestBody(
                "QA-TEST-TO-DELETE-VARIANTS-AND-INSTANCES",
                "QA TEST TO DELETE VARIANTS AND INSTANCES",
                null
        );
        var productMaster = addProductMasterIfNeeded(productToTest);
        PRODUCTS_TO_CLEANUP.add(productMaster.uniqueId);

        var company = new CompanyCollection().getCompany(TEST_CONFIG.companyID);
        var retailerIdToTest = company.retailers.get(0).systemRetailerId;
        var retailerIdToTest2 = company.retailers.get(1).systemRetailerId;
        var campaignIdToTest = company.campaigns.get(0).id;
        var campaignIdToTest2 = company.campaigns.get(1).id;

        var staticListId = cleanupProductList();

        LinkedHashMap<String, Enums.ProductVariantLevel> firstBatchOfInstancesToDelete = new LinkedHashMap<>();
        LinkedHashMap<String, Enums.ProductVariantLevel> secondBatchOfInstancesToDelete = new LinkedHashMap<>();

        // CREATE INSTANCES. FOR FIRST LOCALE, WE WILL REMOVE EACH INSTANCE SEPARATELY.
        // FOR SECOND LOCALE, WE WILL REMOVE ALL INSTANCES AT ONCE USING THE GLOBAL INSTANCE ID
        var pathOfInstanceToAdd = new InstancePathBase(productMaster._id, localeId, retailerIdToTest, null);
        ProductVersioningApiRequests.createInstanceLive(pathOfInstanceToAdd, jwt);
        var instanceId = addInstanceToListAndReturnInstanceId(pathOfInstanceToAdd, staticListId);
        firstBatchOfInstancesToDelete.put(instanceId, pathOfInstanceToAdd.getProductLevel());

        pathOfInstanceToAdd = new InstancePathBase(productMaster._id, localeId, retailerIdToTest2, null);
        ProductVersioningApiRequests.createInstanceLive(pathOfInstanceToAdd, jwt);
        instanceId = addInstanceToListAndReturnInstanceId(pathOfInstanceToAdd, staticListId);
        firstBatchOfInstancesToDelete.put(instanceId, pathOfInstanceToAdd.getProductLevel());

        pathOfInstanceToAdd = new InstancePathBase(productMaster._id, localeId, null, campaignIdToTest);
        ProductVersioningApiRequests.createInstanceLive(pathOfInstanceToAdd, jwt);
        instanceId = addInstanceToListAndReturnInstanceId(pathOfInstanceToAdd, staticListId);
        firstBatchOfInstancesToDelete.put(instanceId, pathOfInstanceToAdd.getProductLevel());

        pathOfInstanceToAdd = new InstancePathBase(productMaster._id, localeId, null, campaignIdToTest2);
        ProductVersioningApiRequests.createInstanceLive(pathOfInstanceToAdd, jwt);
        instanceId = addInstanceToListAndReturnInstanceId(pathOfInstanceToAdd, staticListId);
        firstBatchOfInstancesToDelete.put(instanceId, pathOfInstanceToAdd.getProductLevel());

        pathOfInstanceToAdd = new InstancePathBase(productMaster._id, localeId, retailerIdToTest, campaignIdToTest);
        ProductVersioningApiRequests.createInstanceLive(pathOfInstanceToAdd, jwt);
        instanceId = addInstanceToListAndReturnInstanceId(pathOfInstanceToAdd, staticListId);
        firstBatchOfInstancesToDelete.put(instanceId, pathOfInstanceToAdd.getProductLevel());

        pathOfInstanceToAdd = new InstancePathBase(productMaster._id, localeId, retailerIdToTest2, campaignIdToTest2);
        ProductVersioningApiRequests.createInstanceLive(pathOfInstanceToAdd, jwt);
        instanceId = addInstanceToListAndReturnInstanceId(pathOfInstanceToAdd, staticListId);
        firstBatchOfInstancesToDelete.put(instanceId, pathOfInstanceToAdd.getProductLevel());

        pathOfInstanceToAdd = new InstancePathBase(productMaster._id, localeId_2, retailerIdToTest, null);
        ProductVersioningApiRequests.createInstanceLive(pathOfInstanceToAdd, jwt);
        instanceId = addInstanceToListAndReturnInstanceId(pathOfInstanceToAdd, staticListId);

        var pathOfBaseProduct = new InstancePathBase(productMaster._id, localeId_2, null, null);
        ProductMaster.VariantSets.Live.ProductVariantInstances.ProductInstanceGlobal globalInstance = ProductVersioningApiService.getLiveProductInstance(pathOfBaseProduct, jwt);
        secondBatchOfInstancesToDelete.put(globalInstance.id, pathOfBaseProduct.getProductLevel());
        secondBatchOfInstancesToDelete.put(instanceId, pathOfInstanceToAdd.getProductLevel());

        pathOfInstanceToAdd = new InstancePathBase(productMaster._id, localeId_2, retailerIdToTest2, null);
        ProductVersioningApiRequests.createInstanceLive(pathOfInstanceToAdd, jwt);
        addInstanceToListAndReturnInstanceId(pathOfInstanceToAdd, staticListId);

        pathOfInstanceToAdd = new InstancePathBase(productMaster._id, localeId_2, null, campaignIdToTest);
        ProductVersioningApiRequests.createInstanceLive(pathOfInstanceToAdd, jwt);
        addInstanceToListAndReturnInstanceId(pathOfInstanceToAdd, staticListId);

        pathOfInstanceToAdd = new InstancePathBase(productMaster._id, localeId_2, null, campaignIdToTest2);
        ProductVersioningApiRequests.createInstanceLive(pathOfInstanceToAdd, jwt);
        addInstanceToListAndReturnInstanceId(pathOfInstanceToAdd, staticListId);

        pathOfInstanceToAdd = new InstancePathBase(productMaster._id, localeId_2, retailerIdToTest, campaignIdToTest);
        ProductVersioningApiRequests.createInstanceLive(pathOfInstanceToAdd, jwt);
        addInstanceToListAndReturnInstanceId(pathOfInstanceToAdd, staticListId);

        pathOfInstanceToAdd = new InstancePathBase(productMaster._id, localeId_2, retailerIdToTest2, campaignIdToTest2);
        ProductVersioningApiRequests.createInstanceLive(pathOfInstanceToAdd, jwt);
        addInstanceToListAndReturnInstanceId(pathOfInstanceToAdd, staticListId);

        var expectedProductList = ProductListApiService.getProductList(staticListId, jwt);

        var productMasterOriginal = ProductVersioningApiService.getProductMaster(productMaster._id, jwt);
        var variantSetsOriginal = productMasterOriginal.variantSets.live;

        var expectedVariantSets = new ArrayList<>(variantSetsOriginal);

        for (var instanceToDelete : firstBatchOfInstancesToDelete.entrySet()) {
            var idOfInstance = instanceToDelete.getKey();
            var levelOfInstance = instanceToDelete.getValue();
            var response = ProductVersioningApiRequests.deleteVariantInstances(productMaster._id, Collections.singletonList(idOfInstance), jwt);
            int statusCode = response.getStatusCode();

            if (statusCode != 200) {
                LOGGER.info("response body: " + response.body().asString());
                Assert.fail("Unable to delete instance with id: " + idOfInstance + ". Level: " + levelOfInstance);
            }

            var variantSetsAfter = ProductVersioningApiService.getProductMaster(productMaster._id, jwt).variantSets.live;

            extractInstanceOrVariantFromExpectedResults(expectedVariantSets, instanceToDelete, expectedProductList, productMaster._id);

            SoftAssert softAssert = new SoftAssert();
            softAssert.assertEquals(variantSetsAfter, expectedVariantSets, "Variant sets are not as expected after deleting instance with id: " + idOfInstance + ". Level: " + levelOfInstance);
            var currentProductsInList = ProductListApiService.getProductList(staticListId, jwt).products;
            softAssert.assertEquals(currentProductsInList, expectedProductList.products, "Products in list are not as expected after deleting instance with id: " + idOfInstance + ". Level: " + levelOfInstance);
            softAssert.assertAll();
        }

        // RE ADD INSTANCES OF FIRST LOCALE
        pathOfInstanceToAdd = new InstancePathBase(productMaster._id, localeId, retailerIdToTest, null);
        ProductVersioningApiRequests.createInstanceLive(pathOfInstanceToAdd, jwt);
        addInstanceToListAndReturnInstanceId(pathOfInstanceToAdd, staticListId);

        pathOfInstanceToAdd = new InstancePathBase(productMaster._id, localeId, null, campaignIdToTest);
        ProductVersioningApiRequests.createInstanceLive(pathOfInstanceToAdd, jwt);
        instanceId = addInstanceToListAndReturnInstanceId(pathOfInstanceToAdd, staticListId);
        secondBatchOfInstancesToDelete.put(instanceId, pathOfInstanceToAdd.getProductLevel());

        pathOfInstanceToAdd = new InstancePathBase(productMaster._id, localeId, null, campaignIdToTest2);
        ProductVersioningApiRequests.createInstanceLive(pathOfInstanceToAdd, jwt);
        addInstanceToListAndReturnInstanceId(pathOfInstanceToAdd, staticListId);

        productMasterOriginal = ProductVersioningApiService.getProductMaster(productMaster._id, jwt);

        expectedVariantSets = new ArrayList<>(productMasterOriginal.variantSets.live);

        expectedProductList = ProductListApiService.getProductList(staticListId, jwt);

        var idsOfInstancesToDelete = new ArrayList<>(secondBatchOfInstancesToDelete.keySet());
        var response = ProductVersioningApiRequests.deleteVariantInstances(productMaster._id, idsOfInstancesToDelete, jwt);
        int statusCode = response.getStatusCode();

        if (statusCode != 200) {
            LOGGER.info("response body: " + response.body().asString());
            Assert.fail("Unable to delete instances with ids: " + idsOfInstancesToDelete);
        }

        var variantSetsAfter = ProductVersioningApiService.getProductMaster(productMaster._id, jwt).variantSets.live;

        for (var instanceToDelete : secondBatchOfInstancesToDelete.entrySet()) {
            var idOfInstance = instanceToDelete.getKey();
            extractInstanceOrVariantFromExpectedResults(expectedVariantSets, instanceToDelete, expectedProductList, productMaster._id);
        }

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(variantSetsAfter, expectedVariantSets, "Variant sets are not as expected after deleting instances with ids: " + idsOfInstancesToDelete);
        var currentProductsInList = ProductListApiService.getProductList(staticListId, jwt).products;
        softAssert.assertEquals(currentProductsInList, expectedProductList.products, "Products in list are not as expected after deleting instances with ids: " + idsOfInstancesToDelete);
        softAssert.assertAll();

        cleanupProductList();
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanDeleteProductMaster() throws Exception {
        ProductMaster productMaster = addProductMasterIfNeeded(PRODUCT_TO_DELETE);
        PRODUCTS_TO_CLEANUP.add(productMaster.uniqueId);

        productMaster = createProductVariantIfNeeded(productMaster, localeId);
        productMaster = createProductVariantIfNeeded(productMaster, localeId_2);

        var retailerId = CompanyApiService.getCompany(jwt).getRetailerId("Amazon.com");
        var pathOfInstanceToAdd = new InstancePathBase(productMaster._id, localeId, retailerId, null);
        var response = ProductVersioningApiRequests.createInstanceLive(pathOfInstanceToAdd, jwt);

        String staticListId = cleanupProductList();
        InstancePathBase instancePath = new InstancePathBase(productMaster._id, localeId, null, null);
        InstancePathBase instancePath2 = new InstancePathBase(productMaster._id, localeId_2, null, null);

        ProductListApiService.addProductInstancesToList(
                asList(instancePath, instancePath2, pathOfInstanceToAdd),
                staticListId,
                jwt
        );

        ProductVariantPropertySetRequestBody propertySetToAdd = new ProductVariantPropertySetRequestBody(
                Enums.ProductVariantLevel.GLOBAL.getLevel(),
                localeId,
                null,
                null,
                Collections.singletonList(new ProductVariantProperty("test_prop_1", asList("value_1", "value_2")))
        );

        ProductVariantPropertySet propertySet = ProductVersioningApiService.replaceVariantPropertySet(propertySetToAdd, productMaster._id, jwt);

        // DELETE PRODUCT
        response = ProductVersioningApiRequests.deleteProductMaster(productMaster._id, jwt);

        int statusCode = response.getStatusCode();
        checkResponseStatus(testMethodName.get(), "200", statusCode);

        // VERIFY PRODUCT CANT BE FOUND WITH GET CALL
        response = ProductVersioningApiRequests.getProductMaster(productMaster._id, jwt);
        String expectedError = "Type: ProductMasterError. Subtype: ProductMasterMissingError. Message: Could not find product master.";
        validateUnprocessableEntityError(response, expectedError);

        // VERIFY REFERENCES ARE REMOVED
        FriendlyProductVariantList variantList = new ProductVariantListCollection().getProductList(staticListId);
        boolean variantsWereRemoved = variantList.products
                .stream()
                .noneMatch(product -> product.productMasterUniqueId.equals(PRODUCT_TO_DELETE.uniqueId));
        Assert.assertTrue(variantsWereRemoved, "References of product " + PRODUCT_TO_DELETE.uniqueId + " were not removed from product list\n" + variantList);

        boolean propertySetExists = new ProductMasterVariantPropertySetCollection().doesPropertySetExist(propertySet._id);
        Assert.assertTrue(propertySetExists, "Property set was removed from database when product master was deleted");

        cleanupProductList();
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotDeleteProductMaster_InvalidParameters() throws Exception {
        Response response = callEndpoint(
                ProductVersioningRoutes.getProductMasterRoutePath("productMasterId"),
                jwt,
                "DELETE",
                "",
                ""
        );
        String expectedError = "\"productMasterId\" needs to be a mongo Binary object";
        validateInvalidRequestParametersError(response, Collections.singletonList(expectedError));
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanGetFullProductComposition() throws Exception {
        List<ProductMasterComposition> expectedProductMasterCompositions = new ArrayList<>();

        productMasterForCompositionTests = ProductVersioningApiService.getProductMasterByUniqueId(PRODUCT_UNIQUE_ID_COMPOSITION_TESTS, jwt);

        for (ProductMaster.VariantSets.Live variantSet : productMasterForCompositionTests.variantSets.live) {
            ProductMasterComposition expectedProductMasterComposition = getExpectedVariantComposition(productMasterForCompositionTests, variantSet);
            expectedProductMasterCompositions.add(expectedProductMasterComposition);
        }

        List<ProductMasterComposition> compositionsReturned = ProductVersioningApiService.getProductMasterComposed(productMasterForCompositionTests._id, jwt);

        for (ProductMasterComposition expectedComposition : expectedProductMasterCompositions) {
            sortCompositionSearchPhraseVolumes(expectedComposition);
        }

        for (ProductMasterComposition returnedComposition : compositionsReturned) {
            sortCompositionSearchPhraseVolumes(returnedComposition);
        }

        Assert.assertEquals(
                compositionsReturned,
                expectedProductMasterCompositions,
                "Full product composition returned doesn't match with the expected composition"
        );
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanGetProductVariantComposition() throws Exception {
        setProductAndVariantForCompositionTests(PRODUCT_UNIQUE_ID_COMPOSITION_TESTS, localeId_2);

        ProductMasterComposition expectedProductMasterComposition = getExpectedVariantComposition(productMasterForCompositionTests, variantSetForCompositionTests);
        ProductMasterComposition compositionReturned = ProductVersioningApiService.getProductVariantComposed(productMasterForCompositionTests._id, localeId_2, jwt);

        sortCompositionSearchPhraseVolumes(expectedProductMasterComposition);
        sortCompositionSearchPhraseVolumes(compositionReturned);

        Assert.assertEquals(compositionReturned.localeId, expectedProductMasterComposition.localeId);
        verifyCompositionsAreEqual(compositionReturned.global, expectedProductMasterComposition.global);
        verifyListRetailerCompositionsAreEqual(compositionReturned.retailer, expectedProductMasterComposition.retailer);
        verifyListCompositionsAreEqual(compositionReturned.globalCampaign, expectedProductMasterComposition.globalCampaign);
        verifyListRetailerCompositionsAreEqual(compositionReturned.retailerCampaign, expectedProductMasterComposition.retailerCampaign);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanGetProductInstanceComposition_GlobalLevel() throws Exception {
        setProductAndVariantForCompositionTests(PRODUCT_UNIQUE_ID_COMPOSITION_TESTS, localeId_2);

        ProductMaster.VariantSets.Live.ProductVariantInstances.ProductInstanceGlobal globalInstance = variantSetForCompositionTests.instances.global;

        ProductVariantInstancePath instancePath = new ProductVariantInstancePath(
                productMasterForCompositionTests._id,
                variantSetForCompositionTests.localeId,
                Enums.ProductVariantType.LIVE,
                null,
                null
        );

        ProductMasterInstanceComposition expectedComposition = getExpectedInstanceComposition(variantSetForCompositionTests, instancePath, globalInstance, productMasterForCompositionTests);
        verifyProductInstanceComposition(instancePath, expectedComposition);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanGetProductInstanceComposition_RetailerLevel() throws Exception {
        setProductAndVariantForCompositionTests(PRODUCT_UNIQUE_ID_COMPOSITION_TESTS, localeId_2);

        ProductMaster.VariantSets.Live.ProductVariantInstances.ProductInstanceRetailer retailerInstance = variantSetForCompositionTests.instances.retailer.get(0);

        ProductVariantInstancePath instancePath = new ProductVariantInstancePath(
                productMasterForCompositionTests._id,
                variantSetForCompositionTests.localeId,
                Enums.ProductVariantType.LIVE,
                retailerInstance.retailerId,
                null
        );

        ProductMasterInstanceComposition expectedComposition = getExpectedInstanceComposition(variantSetForCompositionTests, instancePath, retailerInstance, productMasterForCompositionTests);
        ProductMasterRetailerInstanceComposition expectedRetailerInstanceComposition = new ProductMasterRetailerInstanceComposition(expectedComposition, retailerInstance.rpc, retailerInstance.previousRpcs, retailerInstance.businessUnits);
        verifyProductInstanceRetailerComposition(instancePath, expectedRetailerInstanceComposition);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanGetProductInstanceComposition_GlobalCampaignLevel() throws Exception {
        setProductAndVariantForCompositionTests(PRODUCT_UNIQUE_ID_COMPOSITION_TESTS, localeId_2);

        ProductMaster.VariantSets.Live.ProductVariantInstances.ProductInstanceCampaign campaignInstance = variantSetForCompositionTests.instances.globalCampaign.get(0);

        ProductVariantInstancePath instancePath = new ProductVariantInstancePath(
                productMasterForCompositionTests._id,
                variantSetForCompositionTests.localeId,
                Enums.ProductVariantType.LIVE,
                null,
                campaignInstance.campaignId
        );

        ProductMasterInstanceComposition expectedComposition = getExpectedInstanceComposition(variantSetForCompositionTests, instancePath, campaignInstance, productMasterForCompositionTests);
        verifyProductInstanceComposition(instancePath, expectedComposition);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanGetProductInstanceComposition_RetailerCampaignLevel() throws Exception {
        setProductAndVariantForCompositionTests(PRODUCT_UNIQUE_ID_COMPOSITION_TESTS, localeId_2);

        ProductMaster.VariantSets.Live.ProductVariantInstances.ProductInstanceRetailerCampaign retailerCampaignInstance =
                variantSetForCompositionTests.instances.retailerCampaign.get(0);

        ProductVariantInstancePath instancePath = new ProductVariantInstancePath(
                productMasterForCompositionTests._id,
                variantSetForCompositionTests.localeId,
                Enums.ProductVariantType.LIVE,
                retailerCampaignInstance.retailerId,
                retailerCampaignInstance.campaignId
        );


        ProductMasterInstanceComposition expectedComposition = getExpectedInstanceComposition(variantSetForCompositionTests, instancePath, retailerCampaignInstance, productMasterForCompositionTests);
        ProductMasterRetailerInstanceComposition expectedRetailerCampaignInstanceComposition = new ProductMasterRetailerInstanceComposition(
                expectedComposition,
                retailerCampaignInstance.rpc,
                retailerCampaignInstance.previousRpcs,
                retailerCampaignInstance.businessUnits
        );
        verifyProductInstanceRetailerComposition(instancePath, expectedRetailerCampaignInstanceComposition);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotGetComposeData_NonExistentProductMaster() throws Exception {
        String nonExistentProduct = UUID.randomUUID().toString();
        String expectedError = "Type: ProductMasterError. Subtype: ProductMasterMissingError. Message: Could not find product master.";

        Response response = ProductVersioningApiRequests.getProductMasterComposed(nonExistentProduct, jwt);
        validateUnprocessableEntityError(response, expectedError);

        response = ProductVersioningApiRequests.getProductVariantComposed(nonExistentProduct, localeId_2, jwt);
        validateUnprocessableEntityError(response, expectedError);

        ProductVariantInstancePath instancePath = new ProductVariantInstancePath(
                nonExistentProduct,
                localeId_2,
                Enums.ProductVariantType.LIVE,
                null,
                null
        );
        response = ProductVersioningApiRequests.getProductInstanceComposed(instancePath, jwt);
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotGetComposeData_NonExistentVariantForLocale() throws Exception {
        productMasterForCompositionTests = ProductVersioningApiService.getProductMasterByUniqueId(PRODUCT_UNIQUE_ID_COMPOSITION_TESTS, jwt);
        String expectedError = "Type: ProductMasterError. Subtype: ProductMasterVariantError. Message: Could not find variant for the locale.";
        String nonExistentLocaleId = UUID.randomUUID().toString();

        Response response = ProductVersioningApiRequests.getProductVariantComposed(productMasterForCompositionTests._id, nonExistentLocaleId, jwt);
        validateUnprocessableEntityError(response, expectedError);

        ProductVariantInstancePath instancePath = new ProductVariantInstancePath(
                productMasterForCompositionTests._id,
                nonExistentLocaleId,
                Enums.ProductVariantType.LIVE,
                null,
                null
        );
        response = ProductVersioningApiRequests.getProductInstanceComposed(instancePath, jwt);
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotGetProductInstanceComposition_NonExistentInstance() throws Exception {
        setProductAndVariantForCompositionTests(PRODUCT_UNIQUE_ID_COMPOSITION_TESTS, localeId_2);

        String expectedError = "Could not find product instance for composition.";
        String randomID = UUID.randomUUID().toString();

        ProductVariantInstancePath instancePath = new ProductVariantInstancePath(
                productMasterForCompositionTests._id,
                variantSetForCompositionTests.localeId,
                Enums.ProductVariantType.LIVE,
                randomID,
                null
        );

        Response response = ProductVersioningApiRequests.getProductInstanceComposed(instancePath, jwt);
        validateUnprocessableEntityError(response, expectedError);

        instancePath = new ProductVariantInstancePath(
                productMasterForCompositionTests._id,
                variantSetForCompositionTests.localeId,
                Enums.ProductVariantType.LIVE,
                null,
                randomID
        );

        response = ProductVersioningApiRequests.getProductInstanceComposed(instancePath, jwt);
        validateUnprocessableEntityError(response, expectedError);

        instancePath = new ProductVariantInstancePath(
                productMasterForCompositionTests._id,
                variantSetForCompositionTests.localeId,
                Enums.ProductVariantType.LIVE,
                randomID,
                randomID
        );

        response = ProductVersioningApiRequests.getProductInstanceComposed(instancePath, jwt);
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanGenerateCarouselInMotion() throws Exception {
        var fileToImport = "https://os-media-service.s3.amazonaws.com/qa/imports/ImportProducts_CarouselInMotion.xlsx";
        var directory = System.getProperty("user.dir") + "/src/test/java/com/DC/testData/";
        var dataInExcelFile = downloadFileAndGetVariantDataToImport(directory, fileToImport);

        importFile(fileToImport, Enums.ImportType.PROPERTY, testMethodName.get(), jwt);
        var company = CompanyApiService.getCompanyWithProperties(jwt);

        var productsArray = new JSONArray();

        for (var row : dataInExcelFile) {
            PRODUCTS_TO_CLEANUP.add(row.productIdentifier);

            var retailerName = row.retailer.isEmpty() ? null : row.retailer;
            var campaignName = row.campaign.isEmpty() ? null : row.campaign;
            var localeName = row.locale;
            var retailerId = company.getRetailerId(retailerName);
            var campaignId = company.getCampaignId(campaignName);
            var localeId = company.getLocaleId(localeName);

            var productMaster = ProductVersioningApiService.getProductMasterByUniqueId(row.productIdentifier, jwt);
            var pathOfInstanceToAdd = new InstancePathBase(productMaster._id, localeId, retailerId, campaignId);
            var composedProduct = ProductVersioningApiService.getProductInstanceComposed(pathOfInstanceToAdd, jwt, ProductMasterRetailerInstanceComposition.class);

            var product = new JSONObject();
            product.put("campaignId", composedProduct.productMasterInfo.campaignId);
            product.put("campaignName", campaignName);
            product.put("clientRetailerName", retailerName);
            product.put("localeId", composedProduct.productMasterInfo.localeId);
            product.put("localeName", localeName);
            product.put("productMasterId", composedProduct.productMasterInfo.productMasterId);
            product.put("productMasterUniqueId", composedProduct.productMasterInfo.productMasterUniqueId);
            product.put("productName", composedProduct.instanceName);
            product.put("retailerId", composedProduct.productMasterInfo.retailerId);
            product.put("uniqueId", composedProduct.instanceUniqueId);
            productsArray.add(product);
        }

        var propertiesArray = new JSONArray();
        var firstDigitalAsset = company.companyProperties.digitalAssetPropertySchema.stream().filter(prop -> prop.id.equals("test_digital_asset_1")).findFirst().orElseThrow();
        var secondDigitalAsset = company.companyProperties.digitalAssetPropertySchema.stream().filter(prop -> prop.id.equals("test_digital_asset_2")).findFirst().orElseThrow();
        propertiesArray.add(new JSONObject().put("id", firstDigitalAsset.id).put("name", firstDigitalAsset.name));
        propertiesArray.add(new JSONObject().put("id", secondDigitalAsset.id).put("name", secondDigitalAsset.name));
        List<String> propertiesIds = (List<String>) propertiesArray.stream().map(productObject -> ((JSONObject) productObject).getString("id")).collect(Collectors.toList());

        var storagePropertyId = "carousel_in_motion";
        var requestBody = new JSONObject();
        requestBody.put("products", productsArray);
        requestBody.put("properties", propertiesArray);
        requestBody.put("includeSilentVideoCaptionFile", false);
        performCarouselInMotionTest(requestBody, firstDigitalAsset.id, storagePropertyId);

        requestBody.put("storageProperty", new JSONObject().put("id", storagePropertyId).put("name", "Carousel In Motion"));
        performCarouselInMotionTest(requestBody, firstDigitalAsset.id, storagePropertyId);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanUpdateProductMasterThumbnailAndName() throws Exception {
        var productToTest = "QA-STAGE-PROD";
        var productMaster = ProductVersioningApiService.getProductMasterByUniqueId(productToTest, jwt);
        var newMasterThumbnail = "https://media.dev.onespace.com/assets/df10ead4-7fcb-4b6d-8ce6-bd9c11a7e638/tigger.png";
        if (Objects.equals(productMaster.thumbnail, newMasterThumbnail)) {
            newMasterThumbnail = "https://media.dev.onespace.com/assets/a0d4b566-7c51-484b-aeeb-bc665d2020ed/ghost.png";
        }
        var newMasterName = "Product Name From API Bulk Endpoint" + SharedMethods.generateRandomNumber();

        var requestsArray = new JSONArray();
        var request = new JSONObject();
        request.put("productMasterUniqueId", productToTest);
        request.put("retailerId", JSONObject.NULL);
        request.put("campaignId", JSONObject.NULL);
        request.put("localeId", JSONObject.NULL);
        request.put("thumbnail", newMasterThumbnail);
        request.put("productName", newMasterName);

        requestsArray.add(request);
        var requestbody = new JSONObject().put("requests", requestsArray);

        var response = ProductVersioningApiRequests.bulkProductMasterInstances(requestbody, jwt);
        checkResponseStatus(testMethodName.get(), "200", response.getStatusCode());

        var productMasterAfter = ProductVersioningApiService.getProductMasterByUniqueId(productToTest, jwt);
        var currentThumbnail = productMasterAfter.thumbnail;
        Assert.assertEquals(productMasterAfter.thumbnail, newMasterThumbnail, "Thumbnail was not updated");
        Assert.assertEquals(productMasterAfter.name, newMasterName, "Master name was not updated");
    }

    private void sortCompositionSearchPhraseVolumes(ProductMasterComposition composition) {
        composition.global.sortSearchPhraseVolumes();
        composition.retailer.forEach(ProductMasterRetailerInstanceComposition::sortSearchPhraseVolumes);
        composition.globalCampaign.forEach(ProductMasterInstanceComposition::sortSearchPhraseVolumes);
        composition.retailerCampaign.forEach(ProductMasterRetailerInstanceComposition::sortSearchPhraseVolumes);
    }

    private void verifyReturnedPropertySetMatchWithExpectedPropertySet(ProductMaster.VariantSets.Live variantSetReturned, ProductMaster.VariantSets.Live expectedVariantSet) {
        Assert.assertEquals(variantSetReturned, expectedVariantSet,
                "Returned variant set didn't match with expected set" +
                        "\nExpected:\n" + expectedVariantSet +
                        "\nActual:\n" + variantSetReturned
        );
    }

    private InstancePathBase setupInstanceAtRetailerLevel(String localeId, String retailerId, String productMasterId) throws Exception {
        ProductVariantPropertySetRequestBody propertySetToAdd = new ProductVariantPropertySetRequestBody(
                Enums.ProductVariantLevel.RETAILER.getLevel(),
                localeId,
                retailerId,
                null,
                new ArrayList<>()
        );

        ProductVersioningApiService.replaceVariantPropertySet(propertySetToAdd, productMasterId, jwt);
        return new InstancePathBase(productMasterId, propertySetToAdd.localeId, propertySetToAdd.retailerId, propertySetToAdd.campaignId);
    }

    private InstancePathBase setupInstanceAtCampaignLevel(String localeId, String campaignId, String productMasterId) throws Exception {
        ProductVariantPropertySetRequestBody propertySetToAdd = new ProductVariantPropertySetRequestBody(
                Enums.ProductVariantLevel.GLOBAL_CAMPAIGN.getLevel(),
                localeId,
                null,
                campaignId,
                new ArrayList<>()
        );

        ProductVersioningApiService.replaceVariantPropertySet(propertySetToAdd, productMasterId, jwt);
        return new InstancePathBase(productMasterId, propertySetToAdd.localeId, propertySetToAdd.retailerId, propertySetToAdd.campaignId);
    }

    private ProductVariantPropertySet setupInstanceAtRetailerCampaignLevel(String localeId, String retailerId, String campaignId, String productMasterId) throws Exception {
        ProductVariantPropertySetRequestBody propertySetToAdd_CampaignLevel = new ProductVariantPropertySetRequestBody(
                Enums.ProductVariantLevel.RETAILER_CAMPAIGN.getLevel(),
                localeId,
                retailerId,
                campaignId,
                new ArrayList<>()
        );

        return ProductVersioningApiService.replaceVariantPropertySet(propertySetToAdd_CampaignLevel, productMasterId, jwt);
    }

    private void verifyInstanceUniqueIdIsStandardized(Enums.ProductVariantLevel level, String variantUniqueId, String actualUniqueId) {
        String uniqueIdRegexPattern = "\\b" + variantUniqueId + ":" + level.getLevel() + ":\\b\\d{13}";
        boolean uniqueIdHasCorrectFormat = Pattern.matches(uniqueIdRegexPattern, actualUniqueId);
        Assert.assertTrue(
                uniqueIdHasCorrectFormat,
                "UniqueId didn't match the format. " + actualUniqueId
        );
    }

    private void verifyVariantSetIsCorrectAfterDeletion(ProductMaster.VariantSets.Live variantData, ProductMaster.VariantSets.Live expectedVariantData) {
        logger.info("Verifying variant set after deletion for locale: " + variantData.localeId);
        Assert.assertEquals(variantData.instances.global, expectedVariantData.instances.global, "Global instances don't match with expected instances after deletion");
        Assert.assertEquals(variantData.instances.retailer, expectedVariantData.instances.retailer, "Retailer instances don't match with expected instances after deletion");
        Assert.assertEquals(variantData.instances.globalCampaign, expectedVariantData.instances.globalCampaign, "Campaign instances don't match with expected instances after deletion");
        Assert.assertEquals(variantData.instances.retailerCampaign, expectedVariantData.instances.retailerCampaign, "Retailer-campaign instances don't match with expected instances after deletion");
    }

    private void setProductAndVariantForCompositionTests(String productMasterUniqueId, String localeId) throws Exception {
        productMasterForCompositionTests = ProductVersioningApiService.getProductMasterByUniqueId(productMasterUniqueId, jwt);

        variantSetForCompositionTests = productMasterForCompositionTests.variantSets.live.stream()
                .filter(variant -> variant.localeId.equals(localeId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Product didn't have variant for localeId " + localeId));
    }

    private ProductMasterComposition getExpectedVariantComposition(ProductMaster productMaster, ProductMaster.VariantSets.Live variantSet) throws Exception {
        ProductVariantInstancePath instancePath = new ProductVariantInstancePath(
                productMaster._id,
                variantSet.localeId,
                Enums.ProductVariantType.LIVE,
                null,
                null
        );

        ProductMasterComposition expectedProductMasterComposition = new ProductMasterComposition();
        expectedProductMasterComposition.localeId = instancePath.localeId;
        expectedProductMasterComposition.global = getExpectedInstanceComposition(variantSet, instancePath, variantSet.instances.global, productMaster);
        expectedProductMasterComposition.retailer = new ArrayList<>();
        expectedProductMasterComposition.globalCampaign = new ArrayList<>();
        expectedProductMasterComposition.retailerCampaign = new ArrayList<>();

        for (ProductMaster.VariantSets.Live.ProductVariantInstances.ProductInstanceRetailer retailerInstance : variantSet.instances.retailer) {
            instancePath = new ProductVariantInstancePath(
                    productMaster._id,
                    variantSet.localeId,
                    Enums.ProductVariantType.LIVE,
                    retailerInstance.retailerId,
                    null
            );

            ProductMasterInstanceComposition retailerInstanceBase = getExpectedInstanceComposition(variantSet, instancePath, retailerInstance, productMaster);
            ProductMasterRetailerInstanceComposition retailerInstanceComposition = new ProductMasterRetailerInstanceComposition(retailerInstanceBase, retailerInstance.rpc, retailerInstance.previousRpcs, retailerInstance.businessUnits);
            expectedProductMasterComposition.retailer.add(retailerInstanceComposition);
        }

        for (ProductMaster.VariantSets.Live.ProductVariantInstances.ProductInstanceCampaign campaignInstance : variantSet.instances.globalCampaign) {
            instancePath = new ProductVariantInstancePath(
                    productMaster._id,
                    variantSet.localeId,
                    Enums.ProductVariantType.LIVE,
                    null,
                    campaignInstance.campaignId
            );
            expectedProductMasterComposition.globalCampaign.add(getExpectedInstanceComposition(variantSet, instancePath, campaignInstance, productMaster));
        }

        for (ProductMaster.VariantSets.Live.ProductVariantInstances.ProductInstanceRetailerCampaign retailerCampaignInstance : variantSet.instances.retailerCampaign) {
            instancePath = new ProductVariantInstancePath(
                    productMaster._id,
                    variantSet.localeId,
                    Enums.ProductVariantType.LIVE,
                    retailerCampaignInstance.retailerId,
                    retailerCampaignInstance.campaignId
            );
            ProductMasterInstanceComposition retailerCampaignInstanceBase = getExpectedInstanceComposition(variantSet, instancePath, retailerCampaignInstance, productMaster);
            ProductMasterRetailerInstanceComposition retailerInstanceComposition = new ProductMasterRetailerInstanceComposition(
                    retailerCampaignInstanceBase,
                    retailerCampaignInstance.rpc,
                    retailerCampaignInstance.previousRpcs,
                    retailerCampaignInstance.businessUnits
            );
            expectedProductMasterComposition.retailerCampaign.add(retailerInstanceComposition);
        }
        return expectedProductMasterComposition;
    }

    private ProductMasterInstanceComposition getExpectedInstanceComposition(
            ProductMaster.VariantSets.Live variantSet,
            ProductVariantInstancePath instancePath,
            ProductMaster.VariantSets.Live.ProductVariantInstances.ProductInstanceGlobal instance,
            ProductMaster productMaster
    ) throws Exception {
        ProductMasterInfo productMasterInfo = new ProductMasterInfo(
                instancePath.productMasterId,
                productMaster.uniqueId,
                instancePath.localeId,
                instancePath.retailerId,
                instancePath.campaignId,
                variantSet.id,
                instance.id
        );

        ProductInvariantAttributeSetCore expectedAttributes = null;
        if (variantSet.invariantData.attributeSetId != null) {
            ProductInvariantAttributeSet attributeSet = ProductVersioningApiService.getAttributeSetData(
                    instancePath.productMasterId, variantSet.localeId,
                    Enums.ProductVariantType.LIVE,
                    jwt
            );
            expectedAttributes = new ProductInvariantAttributeSetCore(attributeSet.categoryId, attributeSet.attributes);
        }

        List<ProductVariantProperty> expectedProperties = ProductVersioningApiService.getPropertySetData(instancePath, null, jwt).properties;
        expectedProperties = expectedProperties == null ? new ArrayList<>() : expectedProperties;

        ProductVariantKeywords expectedKeywords = ProductVersioningApiService.getProductKeywordSet(instancePath, null, jwt).keywords;

        List<SearchPhraseVolume> expectedKeywordVolumes = new ArrayList<>();

        if (expectedKeywords == null) {
            expectedKeywords = new ProductVariantKeywords(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                    new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        } else {
            List<List<String>> keywordTypes = asList(
                    expectedKeywords.title,
                    expectedKeywords.onPage,
                    expectedKeywords.optional,
                    expectedKeywords.reserved,
                    expectedKeywords.branded,
                    expectedKeywords.hidden,
                    expectedKeywords.unused,
                    expectedKeywords.rankTracking
            );

            List<List<String>> nonEmptyKeywords = keywordTypes.stream()
                    .filter(keyword -> !keyword.isEmpty())
                    .collect(Collectors.toList());

            List<Integer> domainIds = new ArrayList<>();

            if (instancePath.retailerId != null) {
                Company company = CompanyApiService.getCompany(jwt);
                int retailerDomainId = company.retailers
                        .stream()
                        .filter(retailer -> retailer.systemRetailerId.equals(instancePath.retailerId))
                        .findFirst()
                        .orElseThrow(() -> new NoSuchElementException("Retailer " + instancePath.retailerId + " not found in company"))
                        .retailerDomainId;
                domainIds.add(retailerDomainId);
            }

            for (List<String> nonEmptyKeyword : nonEmptyKeywords) {
                expectedKeywordVolumes.addAll(SearchPhrasesService.getSearchPhraseVolumes(nonEmptyKeyword, domainIds, jwt).searchPhraseVolumes);
            }
        }

        List<DigitalAssetProperty> expectedDigitalAssets = ProductVersioningApiService.getDigitalAssetSetData(instancePath, null, jwt).digitalAssets;

        if (expectedDigitalAssets == null) {
            expectedDigitalAssets = new ArrayList<>();
        }

        return new ProductMasterInstanceComposition(
                productMasterInfo,
                instance.uniqueId,
                instance.name,
                instance.thumbnail,
                instance.id,
                productMaster.uniqueId,
                productMaster.name,
                productMaster.thumbnail,
                variantSet.invariantData.brand,
                expectedAttributes,
                expectedProperties,
                expectedKeywords,
                expectedKeywordVolumes,
                expectedDigitalAssets
        );
    }

    private void verifyProductInstanceComposition(InstancePathBase instancePath, ProductMasterInstanceComposition expectedComposition) throws Exception {
        ProductMasterInstanceComposition instanceCompositionReturned = ProductVersioningApiService.getProductInstanceComposed(instancePath, jwt);
        instanceCompositionReturned.sortSearchPhraseVolumes();
        expectedComposition.sortSearchPhraseVolumes();
        verifyCompositionsAreEqual(instanceCompositionReturned, expectedComposition);
    }

    private void verifyProductInstanceRetailerComposition(InstancePathBase instancePath, ProductMasterRetailerInstanceComposition expectedComposition) throws Exception {
        ProductMasterRetailerInstanceComposition instanceCompositionReturned = ProductVersioningApiService.getProductInstanceComposed(instancePath, jwt, ProductMasterRetailerInstanceComposition.class);
        instanceCompositionReturned.sortSearchPhraseVolumes();
        expectedComposition.sortSearchPhraseVolumes();
        verifyCompositionsAreEqual(instanceCompositionReturned, expectedComposition);
    }

    private void verifyCompositionsAreEqual(Object actualComposition, Object expectedComposition) {
        Assert.assertEquals(
                actualComposition,
                expectedComposition,
                "Composition returned doesn't match with the expected composition" +
                        "\nExpected Composition: " + expectedComposition +
                        "\nActual Composition: " + actualComposition
        );
    }

    private void verifyListCompositionsAreEqual(List<ProductMasterInstanceComposition> actualCompositions, List<ProductMasterInstanceComposition> expectedCompositions) {
        actualCompositions.forEach(ProductMasterInstanceComposition::sortSearchPhraseVolumes);
        expectedCompositions.forEach(ProductMasterInstanceComposition::sortSearchPhraseVolumes);
        Assert.assertEquals(
                actualCompositions,
                expectedCompositions,
                "List of compositions returned doesn't match with the expected compositions" +
                        "\nExpected Compositions: " + expectedCompositions +
                        "\nActual Compositions: " + actualCompositions
        );
    }

    private void verifyListRetailerCompositionsAreEqual(List<ProductMasterRetailerInstanceComposition> actualCompositions, List<ProductMasterRetailerInstanceComposition> expectedCompositions) {
        actualCompositions.forEach(ProductMasterRetailerInstanceComposition::sortSearchPhraseVolumes);
        expectedCompositions.forEach(ProductMasterRetailerInstanceComposition::sortSearchPhraseVolumes);
        Assert.assertEquals(
                actualCompositions,
                expectedCompositions,
                "List of retailer compositions returned doesn't match with the expected compositions" +
                        "\nExpected Compositions: " + expectedCompositions +
                        "\nActual Compositions: " + actualCompositions
        );
    }

    private String addInstanceToListAndReturnInstanceId(InstancePathBase instancePath, String listId) throws Exception {
        var list = ProductListApiService.addProductInstancesToList(Collections.singletonList(instancePath), listId, jwt);
        return list.products.stream().filter(p -> p.getInstancePathBase().equals(instancePath))
                .findFirst()
                .orElseThrow(() -> new Exception("Instance not found in list"))
                .instanceId;
    }

    private String cleanupProductList() throws Exception {
        FriendlyProductVariantList list = LIST_COLLECTION.getProductListByName(LIST_NAME);
        String staticListId = list._id;

        ProductListApiService.removeProductsFromList(list.products.stream().map(p -> p.instanceId).collect(Collectors.toList()), staticListId, jwt);
        return staticListId;
    }

    private ProductMaster performTestToDeleteVariant(InstancePathBase pathBase, FriendlyProductVariantList expectedProductList, String listId, Boolean isBase, SoftAssert softAssert) throws Exception {
        Response response = ProductVersioningApiRequests.deleteProductMasterVariants(pathBase.productMasterId, Collections.singletonList(pathBase.localeId), jwt);
        int statusCode = response.getStatusCode();

        if (statusCode != 200) {
            LOGGER.info("response body: " + response.body().asString());
            softAssert.fail("Unable to delete product variant for product with path: " + pathBase + ".\nResponse body was: " + response.body().asString());
        }

        response = ProductVersioningApiRequests.getProductVariantData(
                pathBase.productMasterId,
                pathBase.localeId,
                Enums.ProductVariantType.LIVE.getType(),
                jwt
        );

        ApiValidations.validateUnprocessableEntityError(response, "Type: ProductMasterError. Subtype: ProductMasterVariantError. Message: Could not find variant for the locale.");

        var productMasterId = pathBase.productMasterId;

        if (isBase) {
            expectedProductList.products.removeIf(instance -> Objects.equals(instance.productMasterId, productMasterId)
                    && Objects.equals(instance.localeId, pathBase.localeId)
            );
        } else {
            expectedProductList.products.removeIf(instance -> Objects.equals(instance.productMasterId, productMasterId)
                    && Objects.equals(instance.localeId, pathBase.localeId)
                    && Objects.equals(instance.retailerId, pathBase.retailerId)
                    && Objects.equals(instance.campaignId, pathBase.campaignId)
            );
        }

        FriendlyProductVariantList variantList = ProductListApiService.getProductList(listId, jwt);
        softAssert.assertEquals(variantList.products, expectedProductList.products, "Products in list are not as expected after removing variant with path " + pathBase);
        return ProductVersioningApiService.getProductMaster(productMasterId, jwt);
    }

    private void extractInstanceOrVariantFromExpectedResults(ArrayList<ProductMaster.VariantSets.Live> expectedVariantSets, Map.Entry<String, Enums.ProductVariantLevel> instanceIdAndLevel, FriendlyProductVariantList expectedProductList, String productMasterId) {
        var idOfInstance = instanceIdAndLevel.getKey();
        for (var variantSet : expectedVariantSets) {
            if (instanceIdAndLevel.getValue().equals(Enums.ProductVariantLevel.GLOBAL)) {
                if (variantSet.instances.global.id.equals(idOfInstance)) {
                    expectedVariantSets.remove(variantSet);
                    expectedProductList.products.removeIf(instance -> instance.productMasterId.equals(productMasterId) && instance.localeId.equals(variantSet.localeId));
                    break;
                }
            } else {
                variantSet.instances.retailer.removeIf(instance -> instance.id.equals(idOfInstance));
                variantSet.instances.globalCampaign.removeIf(instance -> instance.id.equals(idOfInstance));
                variantSet.instances.retailerCampaign.removeIf(instance -> instance.id.equals(idOfInstance));
                expectedProductList.products.removeIf(instance -> instance.instanceId.equals(idOfInstance));
            }
        }
    }

    private void performCarouselInMotionTest(JSONObject requestBody, String idOfDigitalAsset, String storagePropertyId) throws Exception {
        var response = ProductVersioningApiRequests.generateCarouselInMotion(requestBody, jwt);
        var statusCode = response.getStatusCode();
        checkResponseStatus(testMethodName.get(), "200", statusCode);
        List<String> idsInResponse = response.jsonPath().getList("data");

        var productsArray = requestBody.getJSONArray("products");

        List<String> productMasterIds = new ArrayList<>();
        for (var product : productsArray) {
            var productMasterId = ((JSONObject) product).getString("productMasterId");
            productMasterIds.add(productMasterId);
        }

        Assert.assertEquals(idsInResponse, productMasterIds, "Product master ids returned in response are not as expected");

        for (var product : productsArray) {
            var productMasterId = ((JSONObject) product).getString("productMasterId");
            var productMasterUniqueId = ((JSONObject) product).getString("productMasterUniqueId");
            var localeId = ((JSONObject) product).getString("localeId");
            var productMaster = ProductVersioningApiService.getProductMaster(productMasterId, jwt);

            var instancePath = new ProductVariantInstancePath(
                    productMasterId,
                    ((JSONObject) product).getString("localeId"),
                    Enums.ProductVariantType.LIVE,
                    ((JSONObject) product).optString("retailerId", null),
                    ((JSONObject) product).optString("campaignId", null)
            );
            var composition = ProductVersioningApiService.getProductInstanceComposed(instancePath, jwt, ProductMasterRetailerInstanceComposition.class);
            LOGGER.info("Composition for product master " + productMasterId + " is: " + composition);
            Assert.assertTrue(
                    composition.digitalAssets.stream().anyMatch(asset -> asset.id.equals(idOfDigitalAsset)),
                    "Digital Asset: " + idOfDigitalAsset + " was removed from product " + productMasterUniqueId + " after generating carousel in motion");

            var storagePropertyObject = requestBody.optJSONObject("storageProperty", null);
            if (storagePropertyObject != null) {
                Assert.assertTrue(
                        composition.digitalAssets.stream().anyMatch(asset -> asset.id.equals(storagePropertyId)),
                        "Carousel in motion video was not stored in property " + storagePropertyId + " for product " + productMasterUniqueId + " after generating carousel in motion"
                );
            } else {
                Assert.assertTrue(
                        composition.digitalAssets.stream().noneMatch(asset -> asset.id.equals(storagePropertyId)),
                        "Carousel in motion video was stored in property " + storagePropertyId + " for product " + productMasterUniqueId + " after generating carousel in motion with NULL storage property"
                );
            }
        }
    }
}
