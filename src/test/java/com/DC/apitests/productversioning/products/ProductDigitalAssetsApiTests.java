package com.DC.apitests.productversioning.products;

import com.DC.db.productVersioning.ProductMasterVariantDigitalAssetCollection;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.ProductVersioningApiRequests;
import com.DC.utilities.apiEngine.apiServices.productversioning.CompanyApiService;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.apiEngine.models.requests.productVersioning.CreateProductMasterRequestBody;
import com.DC.utilities.apiEngine.models.requests.productVersioning.DeleteVariantDigitalAssetsRequestBody;
import com.DC.utilities.apiEngine.models.requests.productVersioning.ProductVariantDigitalAssetSetRequestBody;
import com.DC.utilities.apiEngine.models.responses.productVersioning.DigitalAssetProperty;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductMaster;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductVariantDigitalAssetSet;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductVariantInstancePath;
import com.DC.utilities.enums.Enums;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

import static com.DC.apitests.ApiValidations.*;
import static com.DC.utilities.productManager.ProductVersioningCommonMethods.*;
import static java.util.Arrays.asList;

public class ProductDigitalAssetsApiTests extends ProductApiTestsBaseClass {

    ProductDigitalAssetsApiTests() {
        logger = Logger.getLogger(ProductDigitalAssetsApiTests.class);
        PropertyConfigurator.configure("log4j.properties");
    }

    private static final CreateProductMasterRequestBody QA_API_DA = new CreateProductMasterRequestBody(
            "QA-API-DA",
            "QA API DA",
            null
    );

    private ProductMaster productMaster;

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception {
        productMaster = addProductMasterIfNeeded(QA_API_DA);
        productMaster = createProductVariantIfNeeded(productMaster, localeId);
    }

    @AfterClass(alwaysRun = true)
    public void cleanupProduct() throws Exception {
        cleanupAddedProduct(QA_API_DA.uniqueId);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanReplaceVariantDigitalAssetSet_GlobalLevel() throws Exception {
        productMaster = ProductVersioningApiService.getProductMaster(productMaster._id, jwt);

        ProductMaster.VariantSets.Live liveVariantBeforeUpdate = productMaster.variantSets.live
                .stream().filter(variant -> variant.localeId.equals(localeId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        ProductVariantDigitalAssetSet digitalAssetSetOriginal;
        String digitalAssetSetId = liveVariantBeforeUpdate.instances.global.digitalAssetSetId;
        digitalAssetSetOriginal = digitalAssetSetId == null ? null : new ProductMasterVariantDigitalAssetCollection().getDigitalAsset(digitalAssetSetId);

        List<DigitalAssetProperty> expectedDigitalAssets = getDigitalAssetsToAdd();
        ProductVariantDigitalAssetSetRequestBody propertySetToAdd = new ProductVariantDigitalAssetSetRequestBody(
                Enums.ProductVariantLevel.GLOBAL.getLevel(),
                localeId,
                null,
                null,
                expectedDigitalAssets
        );

        Response response = ProductVersioningApiRequests.replaceVariantDigitalAssetSet(propertySetToAdd, productMaster._id, jwt);
        ProductVariantDigitalAssetSet digitalAssetPropertyCreated = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductVariantDigitalAssetSet.class);

        verifyDigitalAssetSetWasUpdatedProperly(digitalAssetPropertyCreated, digitalAssetSetOriginal, propertySetToAdd, expectedDigitalAssets);

        String expectedSourceSetId = digitalAssetPropertyCreated._id;
        digitalAssetPropertyCreated = ProductVersioningApiService.replaceVariantDigitalAssetSet(propertySetToAdd, productMaster._id, jwt);
        verifyProductBaseData(digitalAssetPropertyCreated, productMaster, propertySetToAdd, expectedSourceSetId);

        productMaster = PRODUCT_MASTER_COLLECTION.getProductMaster(productMaster._id);
        ProductMaster.VariantSets.Live updatedLiveVariant = productMaster.variantSets.live
                .stream().filter(variant -> variant.localeId.equals(localeId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        Assert.assertEquals(
                updatedLiveVariant.instances.global.digitalAssetSetId,
                digitalAssetPropertyCreated._id,
                "Global live instance didn't point to the most recent digital asset set created"
        );
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotReplaceVariantDigitalAssetSet_DuplicateEntries() throws Exception {
        DigitalAssetProperty digitalAsset = getDigitalAssetsToAdd().get(0);

        ProductVariantDigitalAssetSetRequestBody propertySetToAdd = new ProductVariantDigitalAssetSetRequestBody(
                Enums.ProductVariantLevel.GLOBAL.getLevel(),
                localeId,
                null,
                null,
                asList(digitalAsset, digitalAsset)
        );

        Response response = ProductVersioningApiRequests.replaceVariantDigitalAssetSet(propertySetToAdd, productMaster._id, jwt);

        String expectedError = "Type: DigitalAssetSetError. Subtype: DigitalAssetSetValidationError. Message: Duplicate entries detected for digital asset property id.";
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanUpdateVariantDigitalAssets() throws Exception {
        List<DigitalAssetProperty> digitalAssets = getDigitalAssetsToAdd();

        ProductVariantDigitalAssetSetRequestBody requestBody = new ProductVariantDigitalAssetSetRequestBody(
                Enums.ProductVariantLevel.GLOBAL.getLevel(),
                localeId,
                null,
                null,
                digitalAssets
        );

        ProductVariantDigitalAssetSet digitalAssetsOriginalSet = ProductVersioningApiService.replaceVariantDigitalAssetSet(requestBody, productMaster._id, jwt);

        int indexOfDigitalAssetToUpdate = digitalAssets.size() - 1;

        var digitalAssetPropertyToUpdate = new DigitalAssetProperty(digitalAssets.get(indexOfDigitalAssetToUpdate).id, List.of(getTestingMemeImageDigitalAsset(READ_CONFIG.getInsightsEnvironment())));

        requestBody = new ProductVariantDigitalAssetSetRequestBody(
                digitalAssetsOriginalSet.level.getLevel(),
                digitalAssetsOriginalSet.locale,
                digitalAssetsOriginalSet.retailerId,
                digitalAssetsOriginalSet.campaignId,
                Collections.singletonList(digitalAssetPropertyToUpdate)
        );

        Response response = ProductVersioningApiRequests.updateVariantDigitalAssetSet(requestBody, productMaster._id, jwt);
        ProductVariantDigitalAssetSet digitalAssetSetUpdated = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductVariantDigitalAssetSet.class);

        digitalAssets.set(indexOfDigitalAssetToUpdate, digitalAssetPropertyToUpdate);

        verifyDigitalAssetSetWasUpdatedProperly(digitalAssetSetUpdated, digitalAssetsOriginalSet, requestBody, digitalAssets);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotUpdateVariantDigitalAssets_NonExistentDigitalAssetSet() throws Exception {
        productMaster = createProductVariantIfNeeded(productMaster, localeId_3);

        List<DigitalAssetProperty> digitalAssets = getDigitalAssetsToAdd();

        ProductVariantDigitalAssetSetRequestBody requestBody = new ProductVariantDigitalAssetSetRequestBody(
                Enums.ProductVariantLevel.GLOBAL.getLevel(),
                localeId_3,
                null,
                null,
                digitalAssets
        );

        Response response = ProductVersioningApiRequests.updateVariantDigitalAssetSet(requestBody, productMaster._id, jwt);
        String expectedErrorMsg = "Type: ProductMasterError. Subtype: ProductMasterVariantError. Message: Could not find existing digital assets.";
        validateUnprocessableEntityError(response, expectedErrorMsg);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotUpdateVariantDigitalAssets_NonExistentDigitalAssetInSet() throws Exception {
        productMaster = createProductVariantIfNeeded(productMaster, localeId_2);

        List<DigitalAssetProperty> digitalAssets = getDigitalAssetsToAdd();

        ProductVariantDigitalAssetSetRequestBody requestBody = new ProductVariantDigitalAssetSetRequestBody(
                Enums.ProductVariantLevel.GLOBAL.getLevel(),
                localeId_2,
                null,
                null,
                digitalAssets
        );

        ProductVersioningApiService.replaceVariantDigitalAssetSet(requestBody, productMaster._id, jwt);

        DigitalAssetProperty invalidAsset = new DigitalAssetProperty(
                "test_digital_asset_4",
                Collections.singletonList(
                        new DigitalAssetProperty.Assets(
                                "https://os-media-service.s3.amazonaws.com/development/OneSpaceTest/Image+Comparison/imageCompareTestData/small.jpg",
                                null
                        )
                )
        );

        requestBody.digitalAssets = Collections.singletonList(invalidAsset);
        Response response = ProductVersioningApiRequests.updateVariantDigitalAssetSet(requestBody, productMaster._id, jwt);
        String expectedErrorMsg = "Item does not exist in array";
        validateUnprocessableEntityError(response, expectedErrorMsg);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanAddDigitalAssetsToVariantDigitalAssetsSet() throws Exception {
        List<DigitalAssetProperty> digitalAssets = getDigitalAssetsToAdd();

        ProductVariantDigitalAssetSetRequestBody requestBody = new ProductVariantDigitalAssetSetRequestBody(
                Enums.ProductVariantLevel.GLOBAL.getLevel(),
                localeId,
                null,
                null,
                digitalAssets
        );

        ProductVariantDigitalAssetSet digitalAssetsOriginalSet = ProductVersioningApiService.replaceVariantDigitalAssetSet(requestBody, productMaster._id, jwt);

        var assetToMerge = new DigitalAssetProperty("test_digital_asset_3", List.of(getTestingMemeImageDigitalAsset(READ_CONFIG.getInsightsEnvironment())));

        requestBody = new ProductVariantDigitalAssetSetRequestBody(
                Enums.ProductVariantLevel.GLOBAL.getLevel(),
                localeId,
                null,
                null,
                Collections.singletonList(assetToMerge)
        );

        Response response = ProductVersioningApiRequests.addDigitalAssetsToVariantDigitalAssetsSet(requestBody, productMaster._id, jwt);
        ProductVariantDigitalAssetSet digitalAssetSetUpdated = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductVariantDigitalAssetSet.class);

        digitalAssets.add(assetToMerge);
        verifyDigitalAssetSetWasUpdatedProperly(digitalAssetSetUpdated, digitalAssetsOriginalSet, requestBody, digitalAssets);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanGetDigitalAssetSetData() throws Exception {
        productMaster = ProductVersioningApiService.getProductMaster(productMaster._id, jwt);
        String digitalAssetSetId = productMaster.variantSets.live
                .stream()
                .filter(attribute -> attribute.localeId.equals(localeId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new)
                .instances.global.digitalAssetSetId;

        ProductVariantDigitalAssetSet expectedDigitalAssetSet;
        if (digitalAssetSetId == null) {
            List<DigitalAssetProperty> digitalAssets = getDigitalAssetsToAdd();

            ProductVariantDigitalAssetSetRequestBody requestBody = new ProductVariantDigitalAssetSetRequestBody(
                    Enums.ProductVariantLevel.GLOBAL.getLevel(),
                    localeId,
                    null,
                    null,
                    digitalAssets
            );

            expectedDigitalAssetSet = ProductVersioningApiService.replaceVariantDigitalAssetSet(requestBody, productMaster._id, jwt);
        } else {
            expectedDigitalAssetSet = new ProductMasterVariantDigitalAssetCollection().getDigitalAsset(digitalAssetSetId);
        }

        var instancePath = new ProductVariantInstancePath(productMaster._id, localeId, Enums.ProductVariantType.LIVE, null, null);
        Response response = ProductVersioningApiRequests.getDigitalAssetSetData(instancePath, null, jwt);
        ProductVariantDigitalAssetSet digitalAssetsReturned = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductVariantDigitalAssetSet.class);
        verifyReturnedDigitalAssetsMatchWithExpectedAssets(digitalAssetsReturned, expectedDigitalAssetSet);

        instancePath.productMasterId = productMaster.uniqueId;
        response = ProductVersioningApiRequests.getDigitalAssetSetDataByUniqueId(instancePath, null, jwt);
        digitalAssetsReturned = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductVariantDigitalAssetSet.class);
        verifyReturnedDigitalAssetsMatchWithExpectedAssets(digitalAssetsReturned, expectedDigitalAssetSet);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanDeleteDigitalAssetFromVariantDigitalAssetSet() throws Exception {
        List<DigitalAssetProperty> digitalAssets = getDigitalAssetsToAdd();

        ProductVariantDigitalAssetSetRequestBody requestBody = new ProductVariantDigitalAssetSetRequestBody(
                Enums.ProductVariantLevel.GLOBAL.getLevel(),
                localeId,
                null,
                null,
                digitalAssets
        );

        ProductVariantDigitalAssetSet digitalAssetsOriginalSet = ProductVersioningApiService.replaceVariantDigitalAssetSet(requestBody, productMaster._id, jwt);

        int indexOfDigitalAssetToDelete = digitalAssets.size() - 1;
        DeleteVariantDigitalAssetsRequestBody requestBodyToDeleteDigitalAsset = new DeleteVariantDigitalAssetsRequestBody(
                requestBody.level,
                requestBody.localeId,
                requestBody.retailerId,
                requestBody.campaignId,
                Collections.singletonList(digitalAssets.get(indexOfDigitalAssetToDelete).id)
        );

        Response response = ProductVersioningApiRequests.deleteDigitalAssetsFromVariantDigitalAssetsSet(requestBodyToDeleteDigitalAsset, productMaster._id, jwt);
        ProductVariantDigitalAssetSet digitalAssetSetUpdated = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductVariantDigitalAssetSet.class);

        digitalAssets.remove(indexOfDigitalAssetToDelete);
        verifyDigitalAssetSetWasUpdatedProperly(digitalAssetSetUpdated, digitalAssetsOriginalSet, requestBody, digitalAssets);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotReplaceUpdateMergeGetOrDeleteDigitalAssets_MissingRequiredParameters() throws Exception {
        String incompleteBody = "{\n" +
                "    \"level\": \"global\"\n" +
                "}";

        List<String> expectedErrors = asList("localeId", "retailerId", "campaignId", "digitalAssets");

        Response response = ProductVersioningApiRequests.replaceVariantDigitalAssetSet(incompleteBody, productMaster._id, jwt);
        validateMissingRequestParametersError(response, expectedErrors);

        response = ProductVersioningApiRequests.updateVariantDigitalAssetSet(incompleteBody, productMaster._id, jwt);
        validateMissingRequestParametersError(response, expectedErrors);

        response = ProductVersioningApiRequests.addDigitalAssetsToVariantDigitalAssetsSet(incompleteBody, productMaster._id, jwt);
        validateMissingRequestParametersError(response, expectedErrors);

        response = ProductVersioningApiRequests.deleteDigitalAssetsFromVariantDigitalAssetsSet(incompleteBody, productMaster._id, jwt);
        expectedErrors = asList("localeId", "retailerId", "campaignId", "digitalAssetIds");
        validateMissingRequestParametersError(response, expectedErrors);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotReplaceUpdateMergeGetOrDeleteDigitalAssets_InvalidParameters() throws Exception {
        String bodyWithInvalidParameters = "{\n" +
                "    \"level\": \"invalid\",\n" +
                "    \"localeId\": \"locale\",\n" +
                "    \"retailerId\": \"retailer\",\n" +
                "    \"campaignId\": \"campaign\",\n" +
                "    \"digitalAssets\": [{ \n" +
                "        \"id\" : 12345,\n" +
                "        \"assets\": [{\n" +
                "            \"url\": 123,\n" +
                "            \"mediaAssetId\": \"invalid\"\n" +
                "        }]\n" +
                "    }]\n" +
                "}";

        List<String> expectedErrors = new ArrayList<>();
        expectedErrors.add("\"level\" must be one of [global, retailer, globalCampaign, retailerCampaign]");
        expectedErrors.add("\"localeId\" needs to be a mongo Binary object");
        expectedErrors.add("\"retailerId\" needs to be a mongo Binary object");
        expectedErrors.add("\"campaignId\" needs to be a mongo Binary object");
        expectedErrors.add("\"mediaAssetId\" needs to be a mongo Binary object");
        expectedErrors.add("\"mediaAssetId\" needs to be a mongo Binary object");
        expectedErrors.add("\"mediaAssetId\" needs to be a mongo Binary object");

        // POST
        Response response = ProductVersioningApiRequests.replaceVariantDigitalAssetSet(bodyWithInvalidParameters, productMaster._id, jwt);
        validateInvalidRequestParametersError(response, expectedErrors);

        // PUT
        response = ProductVersioningApiRequests.updateVariantDigitalAssetSet(bodyWithInvalidParameters, productMaster._id, jwt);
        validateInvalidRequestParametersError(response, expectedErrors);

        // DELETE
        bodyWithInvalidParameters = "{\n" +
                "    \"level\": \"invalid\",\n" +
                "    \"localeId\": \"locale\",\n" +
                "    \"retailerId\": false,\n" +
                "    \"campaignId\": true,\n" +
                "    \"digitalAssetIds\": [123]\n" +
                "}";

        expectedErrors = new ArrayList<>();
        expectedErrors.add("\"level\" must be one of [global, retailer, globalCampaign, retailerCampaign]");
        expectedErrors.add("\"localeId\" needs to be a mongo Binary object");
        expectedErrors.add("\"retailerId\" needs to be a mongo Binary object");
        expectedErrors.add("\"campaignId\" needs to be a mongo Binary object");
        expectedErrors.add("\"0\" must be a string");

        response = ProductVersioningApiRequests.deleteDigitalAssetsFromVariantDigitalAssetsSet(bodyWithInvalidParameters, productMaster._id, jwt);
        validateInvalidRequestParametersError(response, expectedErrors);

        // GET
        expectedErrors = new ArrayList<>();
        expectedErrors.add("\"productMasterId\" needs to be a mongo Binary object");
        expectedErrors.add("\"type\" must be one of [live, staged]");
        expectedErrors.add("\"localeId\" needs to be a mongo Binary object");

        response = ProductVersioningApiRequests.getDigitalAssetSetData("12345", "invalidLocale", "invalidType", jwt);
        validateInvalidRequestParametersError(response, expectedErrors);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotReplaceUpdateMergeDeleteOrGetDigitalAsset_NonExistentProductMaster() throws Exception {
        String randomUUID = UUID.randomUUID().toString();
        String errorPrefix = "Type: ProductMasterError. Subtype: ProductMasterVariantError. Message: ";
        String expectedError = errorPrefix + "Could not find productMaster by id.";

        ProductVariantDigitalAssetSetRequestBody digitalAssetRequestBody = new ProductVariantDigitalAssetSetRequestBody(
                Enums.ProductVariantLevel.RETAILER.getLevel(),
                localeId,
                null,
                null,
                new ArrayList<>()
        );

        DeleteVariantDigitalAssetsRequestBody requestBodyToDelete = new DeleteVariantDigitalAssetsRequestBody(
                digitalAssetRequestBody.level,
                digitalAssetRequestBody.localeId,
                digitalAssetRequestBody.retailerId,
                digitalAssetRequestBody.campaignId,
                new ArrayList<>()
        );

        Response response = ProductVersioningApiRequests.replaceVariantDigitalAssetSet(digitalAssetRequestBody, randomUUID, jwt);
        validateUnprocessableEntityError(response, expectedError);

        response = ProductVersioningApiRequests.addDigitalAssetsToVariantDigitalAssetsSet(digitalAssetRequestBody, randomUUID, jwt);
        validateUnprocessableEntityError(response, expectedError);

        expectedError = errorPrefix + "Could not get product master by id.";
        response = ProductVersioningApiRequests.updateVariantDigitalAssetSet(digitalAssetRequestBody, randomUUID, jwt);
        validateUnprocessableEntityError(response, expectedError);

        response = ProductVersioningApiRequests.deleteDigitalAssetsFromVariantDigitalAssetsSet(requestBodyToDelete, randomUUID, jwt);
        validateUnprocessableEntityError(response, expectedError);

        expectedError = "Type: ProductMasterError. Subtype: ProductMasterMissingError. Message: Could not find product master.";
        response = ProductVersioningApiRequests.getDigitalAssetSetData(randomUUID, localeId, Enums.ProductVariantType.LIVE.getType(), jwt);
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotReplaceUpdateMergeDeleteOrGetDigitalAsset_NonExistentVariantForLocale() throws Exception {
        String expectedError = "Type: ProductMasterError. Subtype: ProductMasterVariantError. Message: Could not find variant for the locale.";

        ProductVersioningApiService.deleteProductMasterVariants(
                productMaster._id,
                Collections.singletonList(localeId_2),
                jwt
        );

        ProductVariantDigitalAssetSetRequestBody digitalAssetRequestBody = new ProductVariantDigitalAssetSetRequestBody(
                Enums.ProductVariantLevel.RETAILER.getLevel(),
                localeId_2,
                null,
                null,
                new ArrayList<>()
        );

        DeleteVariantDigitalAssetsRequestBody requestBodyToDelete = new DeleteVariantDigitalAssetsRequestBody(
                digitalAssetRequestBody.level,
                digitalAssetRequestBody.localeId,
                digitalAssetRequestBody.retailerId,
                digitalAssetRequestBody.campaignId,
                new ArrayList<>()
        );

        Response response = ProductVersioningApiRequests.replaceVariantDigitalAssetSet(digitalAssetRequestBody, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);

        response = ProductVersioningApiRequests.updateVariantDigitalAssetSet(digitalAssetRequestBody, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);

        response = ProductVersioningApiRequests.addDigitalAssetsToVariantDigitalAssetsSet(digitalAssetRequestBody, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);

        response = ProductVersioningApiRequests.deleteDigitalAssetsFromVariantDigitalAssetsSet(requestBodyToDelete, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);

        response = ProductVersioningApiRequests.getDigitalAssetSetData(productMaster._id, localeId_2, Enums.ProductVariantType.LIVE.getType(), jwt);
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotReplaceUpdateMergeOrDeleteDigitalAsset_RetailerLevel_NullRetailerId() throws Exception {
        ProductVariantDigitalAssetSetRequestBody digitalAssetToAdd = new ProductVariantDigitalAssetSetRequestBody(
                Enums.ProductVariantLevel.RETAILER.getLevel(),
                localeId,
                null,
                null,
                new ArrayList<>()
        );

        String expectedError = "Type: ProductMasterError. Subtype: ProductMasterVariantError. Message: Must supply a retailerId for retailer level variant.";

        Response response = ProductVersioningApiRequests.replaceVariantDigitalAssetSet(digitalAssetToAdd, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);

        response = ProductVersioningApiRequests.updateVariantDigitalAssetSet(digitalAssetToAdd, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);

        response = ProductVersioningApiRequests.addDigitalAssetsToVariantDigitalAssetsSet(digitalAssetToAdd, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotReplaceUpdateMergeOrDeleteDigitalAssetToVariant_CampaignLevel_NullCampaignId() throws Exception {
        ProductVariantDigitalAssetSetRequestBody digitalAssetToAdd = new ProductVariantDigitalAssetSetRequestBody(
                Enums.ProductVariantLevel.GLOBAL_CAMPAIGN.getLevel(),
                localeId,
                null,
                null,
                new ArrayList<>()
        );

        String expectedError = "Type: ProductMasterError. Subtype: ProductMasterVariantError. Message: Must supply a campaignId for campaign level variant.";

        Response response = ProductVersioningApiRequests.replaceVariantDigitalAssetSet(digitalAssetToAdd, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);

        response = ProductVersioningApiRequests.updateVariantDigitalAssetSet(digitalAssetToAdd, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);

        response = ProductVersioningApiRequests.addDigitalAssetsToVariantDigitalAssetsSet(digitalAssetToAdd, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotReplaceUpdateMergeOrDeleteDigitalAssetToVariant_RetailerCampaignLevel_NullIds() throws Exception {
        ProductVariantDigitalAssetSetRequestBody digitalAssetToAdd = new ProductVariantDigitalAssetSetRequestBody(
                Enums.ProductVariantLevel.RETAILER_CAMPAIGN.getLevel(),
                localeId,
                null,
                null,
                new ArrayList<>()
        );

        String expectedError = "Type: ProductMasterError. Subtype: ProductMasterVariantError. Message: Must supply a campaignId and retailerId for retailerCampaign level variant.";

        Response response = ProductVersioningApiRequests.replaceVariantDigitalAssetSet(digitalAssetToAdd, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);

        response = ProductVersioningApiRequests.updateVariantDigitalAssetSet(digitalAssetToAdd, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);

        response = ProductVersioningApiRequests.addDigitalAssetsToVariantDigitalAssetsSet(digitalAssetToAdd, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);
    }

    private List<DigitalAssetProperty> getDigitalAssetsToAdd() {
        DigitalAssetProperty firstDigitalAsset = new DigitalAssetProperty("test_digital_asset_1", List.of(getSmallImageDigitalAsset(READ_CONFIG.getInsightsEnvironment())));
        DigitalAssetProperty secondDigitalAsset = new DigitalAssetProperty("test_digital_asset_2", List.of(getSmallModifiedImageDigitalAsset(READ_CONFIG.getInsightsEnvironment())));
        return new ArrayList<>(asList(firstDigitalAsset, secondDigitalAsset));
    }

    private void verifyReturnedDigitalAssetsMatchWithExpectedAssets(ProductVariantDigitalAssetSet digitalAssetsReturned, ProductVariantDigitalAssetSet expectedDigitalAssets) {
        Assert.assertEquals(
                digitalAssetsReturned, expectedDigitalAssets,
                "Returned digital asset set didn't match with the expected digital asset" +
                        "Expected digital asset set: \n" + expectedDigitalAssets +
                        "Returned digital asset set: \n" + digitalAssetsReturned
        );
    }

    private void verifyDigitalAssetSetWasUpdatedProperly(
            ProductVariantDigitalAssetSet digitalAssetSetReturned,
            ProductVariantDigitalAssetSet digitalAssetSetOriginal,
            ProductVariantDigitalAssetSetRequestBody requestBody,
            List<DigitalAssetProperty> expectedDigitalAssets
    ) throws Exception {
        Assert.assertNotEquals(digitalAssetSetReturned._id, digitalAssetSetOriginal == null ? null : digitalAssetSetOriginal._id, "AttributeSetId was not updated");
        Assert.assertEquals(digitalAssetSetReturned._version, 1, "Version of digital asset set didn't match with the expected version");
        Assert.assertNotNull(digitalAssetSetReturned.dateCreated, "DateCreated of digital asset set was null");
        Assert.assertEquals(digitalAssetSetReturned.dateUpdated, digitalAssetSetReturned.dateCreated, "DateUpdated of digital asset set didn't match with the dateCreated");

        verifyProductBaseData(digitalAssetSetReturned, productMaster, requestBody, digitalAssetSetOriginal == null ? null : digitalAssetSetOriginal._id);

        String companyPropertiesId = CompanyApiService.getCompany(jwt).companyPropertiesId;
        Assert.assertEquals(companyPropertiesId, digitalAssetSetReturned.companyPropertiesId, "companyPropertiesId didn't match with the expected id");

        Assert.assertEquals(digitalAssetSetReturned.digitalAssets, expectedDigitalAssets, "Digital assets created didn't match with the expected assets");

        productMaster = PRODUCT_MASTER_COLLECTION.getProductMaster(productMaster._id);

        ProductMaster.VariantSets.Live updatedLiveVariant = productMaster.variantSets.live
                .stream().filter(variant -> variant.localeId.equals(requestBody.localeId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        Assert.assertEquals(
                updatedLiveVariant.instances.global.digitalAssetSetId,
                digitalAssetSetReturned._id,
                "Global live instance didn't point to the digital asset set created"
        );
    }
}
