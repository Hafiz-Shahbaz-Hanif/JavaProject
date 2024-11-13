package com.DC.apitests.productversioning.products;

import com.DC.db.productVersioning.ProductMasterVariantPropertySetCollection;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.CompanyApiRequests;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.ProductVersioningApiRequests;
import com.DC.utilities.apiEngine.apiServices.productversioning.CompanyApiService;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.apiEngine.models.requests.productVersioning.CreateProductMasterRequestBody;
import com.DC.utilities.apiEngine.models.requests.productVersioning.DeleteVariantPropertiesRequestBody;
import com.DC.utilities.apiEngine.models.requests.productVersioning.ProductVariantPropertySetRequestBody;
import com.DC.utilities.apiEngine.models.responses.productVersioning.*;
import com.DC.utilities.enums.Enums;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.*;

import static com.DC.apitests.ApiValidations.*;
import static com.DC.utilities.DateUtility.getCurrentDateTime;
import static java.util.Arrays.asList;

public class ProductPropertiesApiTests extends ProductApiTestsBaseClass {
    private static final CreateProductMasterRequestBody PRODUCT_FOR_RPC_TEST = new CreateProductMasterRequestBody(
            "QA-RPC-TEST",
            "QA RPC TEST",
            null
    );

    private static final CreateProductMasterRequestBody QA_API_PROPERTIES = new CreateProductMasterRequestBody(
            "QA-API-PROPERTIES",
            "QA API PROPERTIES",
            null
    );

    private ProductMaster productMaster;

    ProductPropertiesApiTests() {
        logger = Logger.getLogger(ProductPropertiesApiTests.class);
        PropertyConfigurator.configure("log4j.properties");
    }

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception {
        productMaster = addProductMasterIfNeeded(QA_API_PROPERTIES);
        productMaster = createProductVariantIfNeeded(productMaster, localeId);
    }

    @AfterClass(alwaysRun = true)
    public void cleanupProduct() throws Exception {
        cleanupAddedProduct(PRODUCT_FOR_RPC_TEST.uniqueId);
        cleanupAddedProduct(QA_API_PROPERTIES.uniqueId);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"}, priority = -1)
    public void Api_Products_CanReplaceVariantPropertySet_GlobalLevel() throws Exception {
        ProductVariantProperty firstProperty = new ProductVariantProperty("test_prop_1", asList("value_1", "value_2"));
        ProductVariantProperty secondProperty = new ProductVariantProperty("test_prop_2", Collections.singletonList(1));
        ProductVariantProperty thirdProperty = new ProductVariantProperty("test_prop_3", Collections.singletonList(getCurrentDateTime("yyyy-MM-dd HH:mm:ss")));
        ProductVariantProperty fourthProperty = new ProductVariantProperty("test_prop_4", Collections.singletonList(false));

        ProductVariantPropertySetRequestBody propertySetToAdd = new ProductVariantPropertySetRequestBody(
                Enums.ProductVariantLevel.GLOBAL.getLevel(),
                localeId,
                null,
                null,
                asList(firstProperty, secondProperty, thirdProperty, fourthProperty)
        );

        Response response = ProductVersioningApiRequests.replaceVariantPropertySet(propertySetToAdd, productMaster._id, jwt);
        ProductVariantPropertySet propertySetCreated = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductVariantPropertySet.class);

        Assert.assertNotNull(propertySetCreated._id, "Property set id was null");
        Assert.assertEquals(propertySetCreated._version, 1, "Version of property set didn't match with the expected version");
        Assert.assertNotNull(propertySetCreated.dateCreated, "DateCreated of property set was null");
        Assert.assertEquals(propertySetCreated.dateUpdated, propertySetCreated.dateCreated, "DateUpdated of property set didn't match with the dateCreated");

        ProductMaster.VariantSets.Live liveVariantBeforeUpdate = productMaster.variantSets.live
                .stream().filter(variant -> variant.localeId.equals(localeId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        verifyProductBaseData(propertySetCreated, productMaster, propertySetToAdd, liveVariantBeforeUpdate.instances.global.propertySetId);

        String companyPropertiesId = CompanyApiService.getCompany(jwt).companyPropertiesId;
        Assert.assertEquals(companyPropertiesId, propertySetCreated.companyPropertiesId, "companyPropertiesId didn't match with the expected id");

        Assert.assertEquals(
                propertySetCreated.properties.size(),
                propertySetToAdd.properties.size(),
                "Properties count in set created didn't match with the expected count"
        );

        for (ProductVariantProperty propertyCreated : propertySetCreated.properties) {
            boolean propertyWasCreated;
            if (propertyCreated.id.equals("test_prop_3")) {
                propertyWasCreated = propertySetToAdd.properties.stream().anyMatch(
                        prop -> prop.id.equals(propertyCreated.id)
                );
            } else {
                propertyWasCreated = propertySetToAdd.properties.stream().anyMatch(
                        prop -> prop.id.equals(propertyCreated.id) && prop.values.equals(propertyCreated.values)
                );
            }
            Assert.assertTrue(propertyWasCreated, "Unexpected property created: " + propertyCreated);
        }

        productMaster = PRODUCT_MASTER_COLLECTION.getProductMaster(productMaster._id);

        ProductMaster.VariantSets.Live updatedLiveVariant = productMaster.variantSets.live
                .stream().filter(variant -> variant.localeId.equals(localeId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        Assert.assertEquals(
                updatedLiveVariant.instances.global.propertySetId,
                propertySetCreated._id,
                "Global live instance didn't point to the property set created"
        );
        Assert.assertEquals(
                updatedLiveVariant.instances.global.digitalAssetSetId,
                liveVariantBeforeUpdate.instances.global.digitalAssetSetId,
                "DigitalAssetSetId changed after creating the property set"
        );
        Assert.assertEquals(
                updatedLiveVariant.instances.global.keywordSetId,
                liveVariantBeforeUpdate.instances.global.keywordSetId,
                "KeywordSetId changed after creating the property set"
        );

        propertySetCreated = ProductVersioningApiService.replaceVariantPropertySet(propertySetToAdd, productMaster._id, jwt);
        verifyProductBaseData(propertySetCreated, productMaster, propertySetToAdd, updatedLiveVariant.instances.global.propertySetId);

        productMaster = PRODUCT_MASTER_COLLECTION.getProductMaster(productMaster._id);
        updatedLiveVariant = productMaster.variantSets.live
                .stream().filter(variant -> variant.localeId.equals(localeId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        Assert.assertEquals(
                updatedLiveVariant.instances.global.propertySetId,
                propertySetCreated._id,
                "Global live instance didn't point to the most recent property set created"
        );
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotReplaceVariantPropertySets_DuplicateEntries() throws Exception {
        ProductVariantProperty firstProperty = new ProductVariantProperty("test_prop_1", asList("value_1", "value_2"));

        ProductVariantPropertySetRequestBody propertySetToAdd = new ProductVariantPropertySetRequestBody(
                Enums.ProductVariantLevel.GLOBAL.getLevel(),
                localeId,
                null,
                null,
                asList(firstProperty, firstProperty)
        );

        Response response = ProductVersioningApiRequests.replaceVariantPropertySet(propertySetToAdd, productMaster._id, jwt);
        String expectedError = "Type: PropertySetError. Subtype: PropertySetValidationError. Message: Duplicate entries detected for property id.";
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanUpdateVariantProperties() throws Exception {
        ProductVariantProperty firstProperty = new ProductVariantProperty("test_prop_1", asList("value_1", "value_2"));
        ProductVariantProperty secondProperty = new ProductVariantProperty("test_prop_2", Collections.singletonList(1));

        ProductVariantPropertySetRequestBody propertySetToAdd = new ProductVariantPropertySetRequestBody(
                Enums.ProductVariantLevel.GLOBAL.getLevel(),
                localeId,
                null,
                null,
                asList(firstProperty, secondProperty)
        );

        ProductVariantPropertySet propertySetOriginal = ProductVersioningApiService.replaceVariantPropertySet(propertySetToAdd, productMaster._id, jwt);

        ProductVariantProperty propertyToUpdate = new ProductVariantProperty(firstProperty.id, asList("value_1_new", "value_2_new"));
        ProductVariantPropertySetRequestBody propertySetToUpdate = new ProductVariantPropertySetRequestBody(
                propertySetToAdd.level,
                propertySetToAdd.localeId,
                propertySetToAdd.retailerId,
                propertySetToAdd.campaignId,
                Collections.singletonList(propertyToUpdate)
        );

        Response response = ProductVersioningApiRequests.updateVariantProperties(propertySetToUpdate, productMaster._id, jwt);
        ProductVariantPropertySet propertySetWithUpdates = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductVariantPropertySet.class);

        List<ProductVariantProperty> expectedProperties = asList(propertyToUpdate, secondProperty);
        verifyVariantPropertySetWasUpdatedProperly(propertySetWithUpdates, propertySetOriginal, expectedProperties, productMaster._id);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotUpdateVariantProperties_NonExistentPropertySet() throws Exception {
        productMaster = createProductVariantIfNeeded(productMaster, localeId_3);

        ProductVariantProperty property = new ProductVariantProperty("test_prop_1", asList("value_1", "value_2"));

        ProductVariantPropertySetRequestBody propertySetToUpdate = new ProductVariantPropertySetRequestBody(
                Enums.ProductVariantLevel.GLOBAL.getLevel(),
                localeId_3,
                null,
                null,
                Collections.singletonList(property)
        );

        Response response = ProductVersioningApiRequests.updateVariantProperties(propertySetToUpdate, productMaster._id, jwt);
        String expectedErrorMsg = "Type: ProductMasterError. Subtype: ProductMasterVariantError. Message: Could not find existing properties.";
        validateUnprocessableEntityError(response, expectedErrorMsg);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotUpdateVariantProperties_NonExistentPropertyInSet() throws Exception {
        productMaster = createProductVariantIfNeeded(productMaster, localeId_2);
        ProductVariantProperty property = new ProductVariantProperty("test_prop_1", asList("value_1", "value_2"));

        ProductVariantPropertySetRequestBody propertySet = new ProductVariantPropertySetRequestBody(
                Enums.ProductVariantLevel.GLOBAL.getLevel(),
                localeId_2,
                null,
                null,
                Collections.singletonList(property)
        );

        ProductVersioningApiService.replaceVariantPropertySet(propertySet, productMaster._id, jwt);

        ProductVariantProperty invalidProperty = new ProductVariantProperty("test_prop_5", Collections.singletonList("value_1"));
        propertySet.properties = Collections.singletonList(invalidProperty);

        Response response = ProductVersioningApiRequests.updateVariantProperties(propertySet, productMaster._id, jwt);
        String expectedErrorMsg = "Item does not exist in array";
        validateUnprocessableEntityError(response, expectedErrorMsg);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanAddPropertiesToVariantPropertySet() throws Exception {
        ProductVariantProperty firstProperty = new ProductVariantProperty("test_prop_1", asList("value_1", "value_2"));
        ProductVariantProperty secondProperty = new ProductVariantProperty("test_prop_2", Collections.singletonList(1));

        List<ProductVariantProperty> properties = new ArrayList<>();
        properties.add(firstProperty);
        properties.add(secondProperty);

        ProductVariantPropertySetRequestBody propertySetToAdd = new ProductVariantPropertySetRequestBody(
                Enums.ProductVariantLevel.GLOBAL.getLevel(),
                localeId,
                null,
                null,
                properties
        );

        ProductVariantPropertySet propertySetOriginal = ProductVersioningApiService.replaceVariantPropertySet(propertySetToAdd, productMaster._id, jwt);

        ProductVariantProperty propertyToMerge = new ProductVariantProperty("test_prop_5", Collections.singletonList("Test Value"));
        ProductVariantPropertySetRequestBody propertySetToMerge = new ProductVariantPropertySetRequestBody(
                propertySetToAdd.level,
                propertySetToAdd.localeId,
                propertySetToAdd.retailerId,
                propertySetToAdd.campaignId,
                Collections.singletonList(propertyToMerge)
        );

        Response response = ProductVersioningApiRequests.addPropertiesToVariantPropertySet(propertySetToMerge, productMaster._id, jwt);
        ProductVariantPropertySet propertySetWithUpdates = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductVariantPropertySet.class);

        propertySetToAdd.properties.add(propertyToMerge);
        verifyVariantPropertySetWasUpdatedProperly(propertySetWithUpdates, propertySetOriginal, propertySetToAdd.properties, productMaster._id);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanGetPropertySetData() throws Exception {
        ProductVariantProperty firstProperty = new ProductVariantProperty("test_prop_1", asList("value_1", "value_2"));
        ProductVariantProperty secondProperty = new ProductVariantProperty("test_prop_2", Collections.singletonList(1));

        List<ProductVariantProperty> properties = new ArrayList<>();
        properties.add(firstProperty);
        properties.add(secondProperty);

        productMaster = PRODUCT_MASTER_COLLECTION.getProductMaster(productMaster._id);

        String propertySetId = productMaster.variantSets.live
                .stream()
                .filter(attribute -> attribute.localeId.equals(localeId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new)
                .instances.global.propertySetId;

        ProductVariantPropertySet expectedPropertySet;

        if (propertySetId == null) {
            ProductVariantPropertySetRequestBody propertySetToAdd = new ProductVariantPropertySetRequestBody(
                    Enums.ProductVariantLevel.GLOBAL.getLevel(),
                    localeId,
                    null,
                    null,
                    properties
            );
            expectedPropertySet = ProductVersioningApiService.replaceVariantPropertySet(propertySetToAdd, productMaster._id, jwt);
        } else {
            expectedPropertySet = new ProductMasterVariantPropertySetCollection().getPropertySet(propertySetId);
        }

        var instancePath = new ProductVariantInstancePath(productMaster._id, localeId, Enums.ProductVariantType.LIVE, null, null);
        Response response = ProductVersioningApiRequests.getPropertySetData(instancePath, null, jwt);
        ProductVariantPropertySet propertySetReturned = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductVariantPropertySet.class);
        verifyReturnedPropertySetMatchWithExpectedPropertySet(propertySetReturned, expectedPropertySet);

        instancePath.productMasterId = productMaster.uniqueId;
        response = ProductVersioningApiRequests.getPropertySetDataByUniqueId(instancePath, null, jwt);
        propertySetReturned = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductVariantPropertySet.class);
        verifyReturnedPropertySetMatchWithExpectedPropertySet(propertySetReturned, expectedPropertySet);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanDeletePropertiesFromVariantPropertySet() throws Exception {
        ProductVariantProperty propertyToDelete = new ProductVariantProperty("test_prop_1", asList("value_1", "value_2"));
        ProductVariantProperty secondProperty = new ProductVariantProperty("test_prop_2", Collections.singletonList(1));

        ProductVariantPropertySetRequestBody requestBody = new ProductVariantPropertySetRequestBody(
                Enums.ProductVariantLevel.GLOBAL.getLevel(),
                localeId,
                null,
                null,
                asList(propertyToDelete, secondProperty)
        );

        ProductVariantPropertySet propertySetOriginal = ProductVersioningApiService.replaceVariantPropertySet(requestBody, productMaster._id, jwt);

        DeleteVariantPropertiesRequestBody requestBodyToDeleteProperty = new DeleteVariantPropertiesRequestBody(
                requestBody.level,
                requestBody.localeId,
                requestBody.retailerId,
                requestBody.campaignId,
                Collections.singletonList(propertyToDelete.id)
        );

        Response response = ProductVersioningApiRequests.deletePropertiesFromVariantPropertySet(requestBodyToDeleteProperty, productMaster._id, jwt);
        ProductVariantPropertySet propertySetReturned = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductVariantPropertySet.class);

        verifyVariantPropertySetWasUpdatedProperly(propertySetReturned, propertySetOriginal, Collections.singletonList(secondProperty), productMaster._id);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotReplaceUpdateMergeOrDeleteVariantProperties_MissingRequiredParameters() throws Exception {
        String incompleteBody = "{\n" +
                "    \"level\": \"global\"\n" +
                "}";

        List<String> expectedErrors = asList("localeId", "retailerId", "campaignId", "properties");

        Response response = ProductVersioningApiRequests.replaceVariantPropertySet(incompleteBody, productMaster._id, jwt);
        validateMissingRequestParametersError(response, expectedErrors);

        response = ProductVersioningApiRequests.updateVariantProperties(incompleteBody, productMaster._id, jwt);
        validateMissingRequestParametersError(response, expectedErrors);

        response = ProductVersioningApiRequests.addPropertiesToVariantPropertySet(incompleteBody, productMaster._id, jwt);
        validateMissingRequestParametersError(response, expectedErrors);

        response = ProductVersioningApiRequests.deletePropertiesFromVariantPropertySet(incompleteBody, productMaster._id, jwt);
        expectedErrors = asList("localeId", "retailerId", "campaignId", "propertyIds");
        validateMissingRequestParametersError(response, expectedErrors);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotReplaceUpdateMergeOrDeleteVariantProperties_InvalidParameters() throws Exception {
        String bodyWithInvalidParameters = "{\n" +
                "    \"level\": \"invalid level\",\n" +
                "    \"localeId\": \"locale\",\n" +
                "    \"retailerId\": 123,\n" +
                "    \"campaignId\": false,\n" +
                "    \"properties\": [\n" +
                "        {\n" +
                "            \"id\" : false,\n" +
                "            \"values\" : \"new value new\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        List<String> expectedErrors = new ArrayList<>();
        expectedErrors.add("\"level\" must be one of [global, retailer, globalCampaign, retailerCampaign]");
        expectedErrors.add("\"localeId\" needs to be a mongo Binary object");
        expectedErrors.add("\"retailerId\" needs to be a mongo Binary object");
        expectedErrors.add("\"campaignId\" needs to be a mongo Binary object");
        expectedErrors.add("\"id\" must be a string");
        expectedErrors.add("\"values\" must be an array");

        Response response = ProductVersioningApiRequests.replaceVariantPropertySet(bodyWithInvalidParameters, productMaster._id, jwt);
        validateInvalidRequestParametersError(response, expectedErrors);

        response = ProductVersioningApiRequests.updateVariantProperties(bodyWithInvalidParameters, productMaster._id, jwt);
        validateInvalidRequestParametersError(response, expectedErrors);

        response = ProductVersioningApiRequests.addPropertiesToVariantPropertySet(bodyWithInvalidParameters, productMaster._id, jwt);
        validateInvalidRequestParametersError(response, expectedErrors);

        bodyWithInvalidParameters = "{\n" +
                "    \"level\": \"invalid level\",\n" +
                "    \"localeId\": \"locale\",\n" +
                "    \"retailerId\": 123,\n" +
                "    \"campaignId\": false,\n" +
                "    \"propertyIds\": [\n" +
                "        {\n" +
                "            \"id\" : false,\n" +
                "            \"values\" : \"new value new\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        expectedErrors = new ArrayList<>();
        expectedErrors.add("\"level\" must be one of [global, retailer, globalCampaign, retailerCampaign]");
        expectedErrors.add("\"localeId\" needs to be a mongo Binary object");
        expectedErrors.add("\"retailerId\" needs to be a mongo Binary object");
        expectedErrors.add("\"campaignId\" needs to be a mongo Binary object");
        expectedErrors.add("\"0\" must be a string");
        response = ProductVersioningApiRequests.deletePropertiesFromVariantPropertySet(bodyWithInvalidParameters, productMaster._id, jwt);
        validateInvalidRequestParametersError(response, expectedErrors);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotReplaceUpdateMergeDeleteOrGetVariantProperties_NonExistentProductMaster() throws Exception {
        String randomUUID = UUID.randomUUID().toString();
        String expectedError = "Type: ProductMasterError. Subtype: ProductMasterVariantError. Message: Could not find product master.";

        ProductVariantPropertySetRequestBody propertySetRequestBody = new ProductVariantPropertySetRequestBody(
                Enums.ProductVariantLevel.GLOBAL.getLevel(),
                localeId,
                null,
                null,
                new ArrayList<>()
        );

        DeleteVariantPropertiesRequestBody requestBodyToDelete = new DeleteVariantPropertiesRequestBody(
                propertySetRequestBody.level,
                propertySetRequestBody.localeId,
                propertySetRequestBody.retailerId,
                propertySetRequestBody.campaignId,
                new ArrayList<>()
        );

        // POST
        Response response = ProductVersioningApiRequests.replaceVariantPropertySet(propertySetRequestBody, randomUUID, jwt);
        validateUnprocessableEntityError(response, expectedError);

        // PUT
        response = ProductVersioningApiRequests.updateVariantProperties(propertySetRequestBody, randomUUID, jwt);
        validateUnprocessableEntityError(response, expectedError);

        // PATCH
        response = ProductVersioningApiRequests.addPropertiesToVariantPropertySet(propertySetRequestBody, randomUUID, jwt);
        validateUnprocessableEntityError(response, expectedError);

        // DELETE
        response = ProductVersioningApiRequests.deletePropertiesFromVariantPropertySet(requestBodyToDelete, randomUUID, jwt);
        validateUnprocessableEntityError(response, expectedError);

        // GET
        expectedError = expectedError.replace("ProductMasterVariantError", "ProductMasterMissingError");
        response = ProductVersioningApiRequests.getPropertySetData(randomUUID, localeId, Enums.ProductVariantType.LIVE.getType(), jwt);
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotReplaceUpdateMergeDeleteOrGetVariantProperties_NonExistentVariantForLocale() throws Exception {
        String expectedError = "Type: ProductMasterError. Subtype: ProductMasterVariantError. Message: Could not find variant for the locale.";

        ProductVersioningApiService.deleteProductMasterVariants(
                productMaster._id,
                Collections.singletonList(localeId_2),
                jwt
        );

        ProductVariantPropertySetRequestBody propertySetRequestBody = new ProductVariantPropertySetRequestBody(
                Enums.ProductVariantLevel.GLOBAL.getLevel(),
                localeId_2,
                null,
                null,
                new ArrayList<>()
        );

        DeleteVariantPropertiesRequestBody requestBodyToDelete = new DeleteVariantPropertiesRequestBody(
                propertySetRequestBody.level,
                propertySetRequestBody.localeId,
                propertySetRequestBody.retailerId,
                propertySetRequestBody.campaignId,
                new ArrayList<>()
        );

        // POST
        Response response = ProductVersioningApiRequests.replaceVariantPropertySet(propertySetRequestBody, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);

        // PUT
        response = ProductVersioningApiRequests.updateVariantProperties(propertySetRequestBody, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);

        // PATCH
        response = ProductVersioningApiRequests.addPropertiesToVariantPropertySet(propertySetRequestBody, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);

        // DELETE
        response = ProductVersioningApiRequests.deletePropertiesFromVariantPropertySet(requestBodyToDelete, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);

        // GET
        response = ProductVersioningApiRequests.getPropertySetData(productMaster._id, localeId_2, Enums.ProductVariantType.LIVE.getType(), jwt);
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotReplaceUpdateMergeOrDeleteVariantProperties_RetailerLevel_NullRetailerId() throws Exception {
        ProductVariantPropertySetRequestBody propertySet = new ProductVariantPropertySetRequestBody(
                Enums.ProductVariantLevel.RETAILER.getLevel(),
                localeId,
                null,
                null,
                new ArrayList<>()
        );

        String expectedError = "Type: ProductMasterError. Subtype: ProductMasterVariantError. Message: Must supply a retailerId for retailer level variant.";

        Response response = ProductVersioningApiRequests.replaceVariantPropertySet(propertySet, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);

        response = ProductVersioningApiRequests.updateVariantProperties(propertySet, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);

        response = ProductVersioningApiRequests.addPropertiesToVariantPropertySet(propertySet, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);

        DeleteVariantPropertiesRequestBody deleteVariantPropertiesRequestBody = new DeleteVariantPropertiesRequestBody(
                propertySet.level,
                propertySet.localeId,
                propertySet.retailerId,
                propertySet.campaignId,
                new ArrayList<>()
        );
        response = ProductVersioningApiRequests.deletePropertiesFromVariantPropertySet(deleteVariantPropertiesRequestBody, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotReplaceUpdateMergeOrDeleteVariantProperties_CampaignLevel_NullCampaignId() throws Exception {
        ProductVariantPropertySetRequestBody propertySet = new ProductVariantPropertySetRequestBody(
                Enums.ProductVariantLevel.GLOBAL_CAMPAIGN.getLevel(),
                localeId,
                null,
                null,
                new ArrayList<>()
        );

        String expectedError = "Type: ProductMasterError. Subtype: ProductMasterVariantError. Message: Must supply a campaignId for campaign level variant.";

        Response response = ProductVersioningApiRequests.replaceVariantPropertySet(propertySet, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);

        response = ProductVersioningApiRequests.updateVariantProperties(propertySet, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);

        response = ProductVersioningApiRequests.addPropertiesToVariantPropertySet(propertySet, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);

        DeleteVariantPropertiesRequestBody deleteVariantPropertiesRequestBody = new DeleteVariantPropertiesRequestBody(
                propertySet.level,
                propertySet.localeId,
                propertySet.retailerId,
                propertySet.campaignId,
                new ArrayList<>()
        );
        response = ProductVersioningApiRequests.deletePropertiesFromVariantPropertySet(deleteVariantPropertiesRequestBody, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotReplaceUpdateMergeOrDeleteVariantProperties_RetailerCampaignLevel_NullIds() throws Exception {
        ProductVariantPropertySetRequestBody propertySet = new ProductVariantPropertySetRequestBody(
                Enums.ProductVariantLevel.RETAILER_CAMPAIGN.getLevel(),
                localeId,
                null,
                null,
                new ArrayList<>()
        );

        String expectedError = "Type: ProductMasterError. Subtype: ProductMasterVariantError. Message: Must supply a campaignId and retailerId for retailerCampaign level variant.";

        Response response = ProductVersioningApiRequests.replaceVariantPropertySet(propertySet, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);

        response = ProductVersioningApiRequests.updateVariantProperties(propertySet, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);

        response = ProductVersioningApiRequests.addPropertiesToVariantPropertySet(propertySet, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);

        DeleteVariantPropertiesRequestBody deleteVariantPropertiesRequestBody = new DeleteVariantPropertiesRequestBody(
                propertySet.level,
                propertySet.localeId,
                propertySet.retailerId,
                propertySet.campaignId,
                new ArrayList<>()
        );
        response = ProductVersioningApiRequests.deletePropertiesFromVariantPropertySet(deleteVariantPropertiesRequestBody, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);
    }

    @DataProvider(name = "rpcTest")
    public static Object[][] rpcTestDataProvider() {
        return new Object[][]{
                {"Amazon.com", null},
                {"Amazon.com", "Christmas"}
        };
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"}, dataProvider = "rpcTest", description = "User can update RPC and Business Units. Implicit tests to create instance live and get BUs available")
    public void Api_Products_CanUpdateRPCAndBUs(String retailer, String campaign) throws Exception {
        Company company = CompanyApiService.getCompany(jwt);
        String retailerIdToTest = company.getRetailerId(retailer);
        String campaignIdToTest = company.getCampaignId(campaign);

        String rpcToTest = "QA-RPC-TEST-" + SharedMethods.generateRandomNumber();
        List<String> expectedPreviousRpcs = new ArrayList<>();

        Response response = CompanyApiRequests.getAvailableBusinessUnits(jwt);
        List<String> businessUnitsAvailable = response.getBody().jsonPath().getList("businessUnitId");

        ProductMaster productMaster = addProductMasterIfNeeded(PRODUCT_FOR_RPC_TEST);
        InstancePathBase instancePathBase = new InstancePathBase(productMaster._id, localeId, retailerIdToTest, campaignIdToTest);

        ProductVersioningApiRequests.createInstanceLive(instancePathBase, jwt);

        // ONLY RPC
        response = ProductVersioningApiRequests.updateInstanceRPCAndBusinessUnits(instancePathBase, Map.entry(rpcToTest, true), null, jwt);
        ProductMaster productMasterAfter = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductMaster.class);
        var instanceRetailer = getInstanceRetailer(productMasterAfter, localeId, retailerIdToTest, campaignIdToTest);
        Assert.assertEquals(instanceRetailer.rpc, rpcToTest, "RPC was not updated");
        Assert.assertTrue(instanceRetailer.businessUnits.isEmpty(), "Business Units was updated when only RPC was sent");

        // ONLY BUSINESS UNITS & REMOVE RPC
        List<String> businessUnitsToTest = businessUnitsAvailable.subList(0, 2);
        response = ProductVersioningApiRequests.updateInstanceRPCAndBusinessUnits(instancePathBase, null, Map.entry(businessUnitsToTest, true), jwt);
        productMasterAfter = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductMaster.class);
        expectedPreviousRpcs.add(rpcToTest);
        instanceRetailer = getInstanceRetailer(productMasterAfter, localeId, retailerIdToTest, campaignIdToTest);
        Assert.assertEquals(instanceRetailer.businessUnits, businessUnitsToTest, "Business Units was not updated");
        Assert.assertNull(instanceRetailer.rpc, "RPC was not removed");
        Assert.assertEquals(instanceRetailer.previousRpcs, expectedPreviousRpcs, "Previous RPCs were not updated");

        // UPDATING BOTH
        String rpcToTestBothProperties = "QA-RPC-TEST-" + SharedMethods.generateRandomNumber();
        List<String> businessUnitsToTestBothProperties = businessUnitsAvailable.subList(2, 4);
        response = ProductVersioningApiRequests.updateInstanceRPCAndBusinessUnits(instancePathBase, Map.entry(rpcToTestBothProperties, true), Map.entry(businessUnitsToTestBothProperties, true), jwt);
        productMasterAfter = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductMaster.class);
        instanceRetailer = getInstanceRetailer(productMasterAfter, localeId, retailerIdToTest, campaignIdToTest);
        Assert.assertEquals(instanceRetailer.rpc, rpcToTestBothProperties, "RPC was not updated");
        Assert.assertEquals(instanceRetailer.businessUnits, businessUnitsToTestBothProperties, "Business Units was not updated");
        Assert.assertEquals(instanceRetailer.previousRpcs, expectedPreviousRpcs, "Previous RPCs were not updated");

        // UPDATING ONLY RPC - BUSINESS UNITS SHOULD NOT BE REMOVED
        expectedPreviousRpcs.add(rpcToTestBothProperties);
        rpcToTestBothProperties = "QA-RPC-TEST-" + SharedMethods.generateRandomNumber();
        response = ProductVersioningApiRequests.updateInstanceRPCAndBusinessUnits(instancePathBase, Map.entry(rpcToTestBothProperties, true), null, jwt);
        productMasterAfter = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductMaster.class);
        instanceRetailer = getInstanceRetailer(productMasterAfter, localeId, retailerIdToTest, campaignIdToTest);
        Assert.assertEquals(instanceRetailer.rpc, rpcToTestBothProperties, "RPC was not updated");
        Assert.assertFalse(instanceRetailer.businessUnits.isEmpty(), "Business Units were removed after updating only RPC");
        Assert.assertEquals(instanceRetailer.previousRpcs, expectedPreviousRpcs, "Previous RPCs were not updated after updating only RPC");

        // UPDATING ONLY BUSINESS UNITS - RPC SHOULD NOT BE REMOVED
        businessUnitsToTestBothProperties = businessUnitsAvailable.subList(0, 2);
        response = ProductVersioningApiRequests.updateInstanceRPCAndBusinessUnits(instancePathBase, Map.entry("", false), Map.entry(businessUnitsToTestBothProperties, true), jwt);
        productMasterAfter = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductMaster.class);
        instanceRetailer = getInstanceRetailer(productMasterAfter, localeId, retailerIdToTest, campaignIdToTest);
        Assert.assertEquals(instanceRetailer.businessUnits, businessUnitsToTestBothProperties, "Business Units was not updated");
        Assert.assertNotNull(instanceRetailer.rpc, "RPC was removed after updating only Business Units");
        Assert.assertEquals(instanceRetailer.previousRpcs, expectedPreviousRpcs, "Previous RPCs were updated after updating only Business Units");

        // REMOVING BUSINESS UNIT FROM PRODUCT
        expectedPreviousRpcs.add(rpcToTestBothProperties);
        rpcToTestBothProperties = "QA-RPC-TEST-" + SharedMethods.generateRandomNumber();
        response = ProductVersioningApiRequests.updateInstanceRPCAndBusinessUnits(instancePathBase, Map.entry(rpcToTestBothProperties, true), Map.entry(new ArrayList<>(), true), jwt);
        productMasterAfter = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductMaster.class);
        instanceRetailer = getInstanceRetailer(productMasterAfter, localeId, retailerIdToTest, campaignIdToTest);
        Assert.assertEquals(instanceRetailer.rpc, rpcToTestBothProperties, "RPC was not updated");
        Assert.assertTrue(instanceRetailer.businessUnits.isEmpty(), "Business Units were not removed");
        Assert.assertEquals(instanceRetailer.previousRpcs, expectedPreviousRpcs, "Previous RPCs were not updated");
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotUpdateRPCOrBusinessUnits_InvalidParameters() throws Exception {
        String randomUUID = SharedMethods.generateUUID();

        String bodyWithInvalidParameters = "{\n" +
                "    \"rpc\": 123,\n" +
                "    \"businessUnits\": \"abc\"\n" +
                "}";

        InstancePathBase instancePath = new InstancePathBase(randomUUID, localeId, randomUUID, randomUUID);
        Response response = ProductVersioningApiRequests.updateInstanceRPCAndBusinessUnits(instancePath, bodyWithInvalidParameters, jwt);
        validateInvalidRequestParametersError(response, List.of("\"rpc\" must be a string", "\"businessUnits\" must be an array"));
    }

    private ProductMaster.VariantSets.Live.ProductVariantInstances.ProductInstanceRetailer getInstanceRetailer(ProductMaster productMaster, String localeId, String retailerIdToTest, String campaignIdToTest) {
        var liveVariant = productMaster.variantSets.live
                .stream().filter(variant -> variant.localeId.equals(localeId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        if (campaignIdToTest != null) {
            return liveVariant.instances.retailerCampaign.stream()
                    .filter(instance -> instance.retailerId.equals(retailerIdToTest) && instance.campaignId.equals(campaignIdToTest))
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new);

        } else {
            return liveVariant.instances.retailer.stream()
                    .filter(instance -> instance.retailerId.equals(retailerIdToTest))
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new);
        }

    }

    private void verifyReturnedPropertySetMatchWithExpectedPropertySet(ProductVariantPropertySet propertySetReturned, ProductVariantPropertySet expectedPropertySet) {
        Assert.assertEquals(
                propertySetReturned, expectedPropertySet,
                "Returned property set didn't match with the expected property" +
                        "Expected property set: \n" + expectedPropertySet +
                        "Returned property set: \n" + propertySetReturned
        );
    }

    private void verifyVariantPropertySetWasUpdatedProperly(
            ProductVariantPropertySet propertySetReturned,
            ProductVariantPropertySet propertySetOriginal,
            List<ProductVariantProperty> expectedProperties,
            String productMasterId
    ) throws Exception {

        Assert.assertNotEquals(propertySetReturned._id, propertySetOriginal._id, "Property set id was not a new one");
        Assert.assertEquals(propertySetReturned.type, Enums.ProductVariantType.LIVE, "Type didn't match with expected type");
        Assert.assertEquals(propertySetReturned.level, propertySetOriginal.level, "Level didn't match with expected level");
        Assert.assertEquals(propertySetReturned.companyId, TEST_CONFIG.companyID, "CompanyId didn't match with expected companyId");
        Assert.assertEquals(propertySetReturned.productMasterId, productMasterId, "Product master didn't match with expected product");
        Assert.assertEquals(propertySetReturned.locale, propertySetOriginal.locale, "LocaleId didn't match with expected localeId");
        Assert.assertEquals(propertySetReturned.retailerId, propertySetOriginal.retailerId, "RetailerId didn't match with expected retailerId");
        Assert.assertEquals(propertySetReturned.campaignId, propertySetOriginal.campaignId, "CampaignId didn't match with expected campaignId");
        Assert.assertEquals(propertySetReturned.meta.sourceSetId, propertySetOriginal._id, "SourceSetId didn't match with previous propertySetId");
        Assert.assertEquals(propertySetReturned.companyPropertiesId, propertySetOriginal.companyPropertiesId, "CompanyPropertiesId didn't match with expected id");

        Assert.assertEquals(
                propertySetReturned.properties,
                expectedProperties,
                "Variant properties didn't match with expected properties" +
                        "\nExpected Properties: " + expectedProperties +
                        "\nActual Properties: " + propertySetReturned.properties
        );

        ProductMaster productMaster = ProductVersioningApiService.getProductMaster(productMasterId, jwt);
        String currentVariantPropertySetId = productMaster.variantSets.live.stream()
                .filter(variant -> variant.localeId.equals(propertySetOriginal.locale))
                .findFirst().orElseThrow(NoSuchElementException::new)
                .instances.global.propertySetId;

        Assert.assertEquals(propertySetReturned._id, currentVariantPropertySetId, "PropertySetId was not updated in product variant");
    }
}
