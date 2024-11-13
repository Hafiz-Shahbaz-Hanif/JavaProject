package com.DC.apitests.productversioning.products;

import com.DC.constants.ProductVersioningConstants;
import com.DC.db.productVersioning.ProductMasterVariantKeywordSetCollection;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.ProductVersioningApiRequests;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.apiEngine.models.requests.productVersioning.CreateProductMasterRequestBody;
import com.DC.utilities.apiEngine.models.requests.productVersioning.ProductVariantKeywordSetRequestBody;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductMaster;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductVariantKeywords;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductVariantKeywordSet;
import com.DC.utilities.enums.Enums;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.*;

import static com.DC.apitests.ApiValidations.*;
import static java.util.Arrays.asList;

public class ProductKeywordsApiTests extends ProductApiTestsBaseClass {

    ProductKeywordsApiTests() {
        logger = Logger.getLogger(ProductKeywordsApiTests.class);
        PropertyConfigurator.configure("log4j.properties");
    }

    private static final CreateProductMasterRequestBody QA_API_KEYWORDS = new CreateProductMasterRequestBody(
            "QA-API-Keywords",
            "QA API Keywords",
            null
    );

    private ProductMaster productMaster;

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception {
        productMaster = addProductMasterIfNeeded(QA_API_KEYWORDS);
        productMaster = createProductVariantIfNeeded(productMaster, localeId);
    }

    @AfterClass(alwaysRun = true)
    public void cleanupProduct() throws Exception {
        cleanupAddedProduct(QA_API_KEYWORDS.uniqueId);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanReplaceVariantKeywordSet_GlobalLevel() throws Exception {
        productMaster = ProductVersioningApiService.getProductMaster(productMaster._id, jwt);

        ProductMaster.VariantSets.Live liveVariantBeforeUpdate = productMaster.variantSets.live
                .stream().filter(variant -> variant.localeId.equals(localeId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        ProductVariantKeywordSet keywordSetOriginal;
        String keywordSetId = liveVariantBeforeUpdate.instances.global.keywordSetId;
        keywordSetOriginal = keywordSetId == null ? null : new ProductMasterVariantKeywordSetCollection().getKeywordSet(keywordSetId);

        ProductVariantKeywords keywordsToAdd = new ProductVariantKeywords(
                asList("Title Keyword 1", "Title Keyword 2"),
                asList("OnPage Keyword 1", "OnPage Keyword 2"),
                asList("Optional Keyword 1", "Optional Keyword 2"),
                asList("Reserved Keyword 1", "Reserved Keyword 2"),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        ProductVariantKeywordSetRequestBody keywordSetToAdd = new ProductVariantKeywordSetRequestBody(
                Enums.ProductVariantLevel.GLOBAL.getLevel(),
                localeId,
                null,
                null,
                keywordsToAdd
        );

        Response response = ProductVersioningApiRequests.replaceVariantKeywordSet(keywordSetToAdd, productMaster._id, jwt);
        ProductVariantKeywordSet keywordSetCreated = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductVariantKeywordSet.class);

        verifyKeywordSetWasUpdatedProperly(keywordSetCreated, keywordSetOriginal, keywordSetToAdd, keywordsToAdd);

        String expectedSourceSetId = keywordSetCreated._id;
        keywordSetCreated = ProductVersioningApiService.replaceVariantKeywordSet(keywordSetToAdd, productMaster._id, jwt);
        verifyProductBaseData(keywordSetCreated, productMaster, keywordSetToAdd, expectedSourceSetId);

        productMaster = PRODUCT_MASTER_COLLECTION.getProductMaster(productMaster._id);
        ProductMaster.VariantSets.Live updatedLiveVariant = productMaster.variantSets.live
                .stream().filter(variant -> variant.localeId.equals(localeId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        Assert.assertEquals(
                updatedLiveVariant.instances.global.keywordSetId,
                keywordSetCreated._id,
                "Global live instance didn't point to the most recent keyword set created"
        );
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotReplaceKeywordSet_DuplicateEntries() throws Exception {
        ProductVariantKeywords keywordsToAdd = new ProductVariantKeywords(
                asList("Title Keyword 1", "Title Keyword 1"),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        ProductVariantKeywordSetRequestBody keywordSetToAdd = new ProductVariantKeywordSetRequestBody(
                Enums.ProductVariantLevel.GLOBAL.getLevel(),
                localeId,
                null,
                null,
                keywordsToAdd
        );

        Response response = ProductVersioningApiRequests.replaceVariantKeywordSet(keywordSetToAdd, productMaster._id, jwt);
        String expectedError = "Type: KeywordSetError. Subtype: KeywordValidationError. Message: Duplicate keyword detected.";
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanGetKeywordData() throws Exception {
        productMaster = ProductVersioningApiService.getProductMaster(productMaster._id, jwt);

        String keywordSetId = productMaster.variantSets.live
                .stream()
                .filter(attribute -> attribute.localeId.equals(localeId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new)
                .instances.global.keywordSetId;

        ProductVariantKeywordSet expectedKeywordSet;

        if (keywordSetId == null) {
            ProductVariantKeywords keywords = new ProductVariantKeywords(
                    new ArrayList<>(asList("Title Keyword 1", "Title Keyword 2")),
                    new ArrayList<>(asList("OnPage Keyword 1", "OnPage Keyword 2")),
                    new ArrayList<>(asList("Optional Keyword 1", "Optional Keyword 2")),
                    new ArrayList<>(asList("Reserved Keyword 1", "Reserved Keyword 2")),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>()
            );

            ProductVariantKeywordSetRequestBody keywordSetRequestBody = new ProductVariantKeywordSetRequestBody(
                    Enums.ProductVariantLevel.GLOBAL.getLevel(),
                    localeId,
                    null,
                    null,
                    keywords
            );
            expectedKeywordSet = ProductVersioningApiService.replaceVariantKeywordSet(keywordSetRequestBody, productMaster._id, jwt);
        } else {
            expectedKeywordSet = new ProductMasterVariantKeywordSetCollection().getKeywordSet(keywordSetId);
        }

        Response response = ProductVersioningApiRequests.getKeywordSetData(productMaster._id, localeId, Enums.ProductVariantType.LIVE.getType(), jwt);
        ProductVariantKeywordSet returnedKeywordSet = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductVariantKeywordSet.class);
        verifyReturnedKeywordSetMatchWithExpectedSet(returnedKeywordSet, expectedKeywordSet);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanAddKeywordsToVariantKeywordSet() throws Exception {
        ProductVariantKeywords keywords = new ProductVariantKeywords(
                new ArrayList<>(asList("Title Keyword 1", "Title Keyword 2")),
                new ArrayList<>(asList("OnPage Keyword 1", "OnPage Keyword 2")),
                new ArrayList<>(asList("Optional Keyword 1", "Optional Keyword 2")),
                new ArrayList<>(asList("Reserved Keyword 1", "Reserved Keyword 2")),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        ProductVariantKeywords keywordsToAdd = new ProductVariantKeywords(
                new ArrayList<>(asList("Title Keyword 3", "Title Keyword 4")),
                new ArrayList<>(asList("OnPage Keyword 3", "OnPage Keyword 4")),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(asList("Unused Keyword 1", "Unused Keyword 2")),
                new ArrayList<>(asList("Rank Tracking Keyword 1", "Rank Tracking Keyword 2"))
        );

        ProductVariantKeywordSetRequestBody keywordSetRequestBody = new ProductVariantKeywordSetRequestBody(
                Enums.ProductVariantLevel.GLOBAL.getLevel(),
                localeId,
                null,
                null,
                keywords
        );

        ProductVariantKeywordSet originalKeywordSet = ProductVersioningApiService.replaceVariantKeywordSet(keywordSetRequestBody, productMaster._id, jwt);

        keywordSetRequestBody.keywords = keywordsToAdd;
        Response response = ProductVersioningApiRequests.addKeywordsToVariantKeywordSet(keywordSetRequestBody, productMaster._id, jwt);
        ProductVariantKeywordSet returnedKeywordSet = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductVariantKeywordSet.class);

        keywords.title.addAll(keywordsToAdd.title);
        keywords.onPage.addAll(keywordsToAdd.onPage);
        keywords.optional.addAll(keywordsToAdd.optional);
        keywords.reserved.addAll(keywordsToAdd.reserved);
        keywords.branded.addAll(keywordsToAdd.branded);
        keywords.hidden.addAll(keywordsToAdd.hidden);
        keywords.unused.addAll(keywordsToAdd.unused);
        keywords.rankTracking.addAll(keywordsToAdd.rankTracking);
        verifyKeywordSetWasUpdatedProperly(returnedKeywordSet, originalKeywordSet, keywordSetRequestBody, keywords);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanDeleteKeywordsFromVariantKeywordSet() throws Exception {
        ProductVariantKeywords keywords = new ProductVariantKeywords(
                new ArrayList<>(asList("Title Keyword 1", "Title Keyword 2", "Title Keyword 3")),
                new ArrayList<>(asList("OnPage Keyword 1", "OnPage Keyword 2")),
                new ArrayList<>(asList("Optional Keyword 1", "Optional Keyword 2")),
                new ArrayList<>(asList("Reserved Keyword 1", "Reserved Keyword 2")),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        ProductVariantKeywordSetRequestBody keywordSetRequestBody = new ProductVariantKeywordSetRequestBody(
                Enums.ProductVariantLevel.GLOBAL.getLevel(),
                localeId,
                null,
                null,
                keywords
        );

        ProductVariantKeywords keywordsToDelete = new ProductVariantKeywords(
                new ArrayList<>(asList("Title Keyword 2", "Title Keyword 3")),
                keywords.onPage,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        ProductVariantKeywordSet originalKeywordSet = ProductVersioningApiService.replaceVariantKeywordSet(keywordSetRequestBody, productMaster._id, jwt);

        keywordSetRequestBody.keywords = keywordsToDelete;
        Response response = ProductVersioningApiRequests.deleteKeywordsFromVariantKeywordSet(keywordSetRequestBody, productMaster._id, jwt);
        ProductVariantKeywordSet returnedKeywordSet = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductVariantKeywordSet.class);

        keywords.title.removeAll(keywordsToDelete.title);
        keywords.onPage.removeAll(keywordsToDelete.onPage);
        keywords.optional.removeAll(keywordsToDelete.optional);
        keywords.reserved.removeAll(keywordsToDelete.reserved);
        keywords.branded.removeAll(keywordsToDelete.branded);
        keywords.hidden.removeAll(keywordsToDelete.hidden);
        keywords.unused.removeAll(keywordsToDelete.unused);
        keywords.rankTracking.removeAll(keywordsToDelete.rankTracking);
        verifyKeywordSetWasUpdatedProperly(returnedKeywordSet, originalKeywordSet, keywordSetRequestBody, keywords);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotReplaceMergeOrDeleteKeywords_MissingRequiredParameters() throws Exception {
        String invalidBody = "{\n" +
                "    \"keywords\": {\n" +
                "    }\n" +
                "}";

        List<String> missingParameters = new ArrayList<>();
        missingParameters.add("level");
        missingParameters.add("localeId");
        missingParameters.add("retailerId");
        missingParameters.add("campaignId");
        missingParameters.addAll(ProductVersioningConstants.PRODUCT_KEYWORD_BUCKETS);

        Response response = ProductVersioningApiRequests.replaceVariantKeywordSet(invalidBody, productMaster._id, jwt);
        validateMissingRequestParametersError(response, missingParameters);

        response = ProductVersioningApiRequests.addKeywordsToVariantKeywordSet(invalidBody, productMaster._id, jwt);
        validateMissingRequestParametersError(response, missingParameters);

        response = ProductVersioningApiRequests.deleteKeywordsFromVariantKeywordSet(invalidBody, productMaster._id, jwt);
        validateMissingRequestParametersError(response, missingParameters);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotReplaceMergeDeleteOrGetKeywords_InvalidParameters() throws Exception {
        String randomUUID = UUID.randomUUID().toString();

        String bodyWithInvalidParameters = "{\n" +
                "    \"level\": \"invalid\",\n" +
                "    \"localeId\": \"invalid\",\n" +
                "    \"retailerId\": \"invalid\",\n" +
                "    \"campaignId\": 123,\n" +
                "    \"keywords\": {\n" +
                "        \"title\" : [123, \"hello\"],\n" +
                "        \"onPage\": [false],\n" +
                "        \"optional\": false,\n" +
                "        \"reserved\":[1],\n" +
                "        \"branded\":[true, \"hello\", 1],\n" +
                "        \"hidden\": \"invalid\",\n" +
                "        \"unused\":[false],\n" +
                "        \"rankTracking\":[true]\n" +
                "    }\n" +
                "}";

        List<String> expectedErrors = new ArrayList<>();
        expectedErrors.add("\"level\" must be one of [global, retailer, globalCampaign, retailerCampaign]");
        expectedErrors.add("\"localeId\" needs to be a mongo Binary object");
        expectedErrors.add("\"retailerId\" needs to be a mongo Binary object");
        expectedErrors.add("\"campaignId\" needs to be a mongo Binary object");
        expectedErrors.add("\"0\" must be a string");
        expectedErrors.add("\"0\" must be a string");
        expectedErrors.add("\"optional\" must be an array");
        expectedErrors.add("\"0\" must be a string");
        expectedErrors.add("\"0\" must be a string");
        expectedErrors.add("\"2\" must be a string");
        expectedErrors.add("\"hidden\" must be an array");
        expectedErrors.add("\"0\" must be a string");
        expectedErrors.add("\"0\" must be a string");

        // POST
        Response response = ProductVersioningApiRequests.replaceVariantKeywordSet(bodyWithInvalidParameters, randomUUID, jwt);
        validateInvalidRequestParametersError(response, expectedErrors);

        // PUT
        response = ProductVersioningApiRequests.addKeywordsToVariantKeywordSet(bodyWithInvalidParameters, randomUUID, jwt);
        validateInvalidRequestParametersError(response, expectedErrors);

        // DELETE
        response = ProductVersioningApiRequests.deleteKeywordsFromVariantKeywordSet(bodyWithInvalidParameters, randomUUID, jwt);
        validateInvalidRequestParametersError(response, expectedErrors);

        // GET
        expectedErrors = new ArrayList<>();
        expectedErrors.add("\"productMasterId\" needs to be a mongo Binary object");
        expectedErrors.add("\"type\" must be one of [live, staged]");
        expectedErrors.add("\"localeId\" needs to be a mongo Binary object");
        response = ProductVersioningApiRequests.getKeywordSetData("12345", "invalidLocale", "invalidType", jwt);
        validateInvalidRequestParametersError(response, expectedErrors);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotReplaceMergeDeleteOrGetKeywords_NonExistentProductMaster() throws Exception {
        String randomUUID = UUID.randomUUID().toString();
        String expectedError = "Type: ProductMasterError. Subtype: ProductMasterVariantError. Message: Could not find product master.";

        ProductVariantKeywordSetRequestBody keywordSetToAdd = new ProductVariantKeywordSetRequestBody(
                Enums.ProductVariantLevel.GLOBAL.getLevel(),
                localeId,
                null,
                null,
                new ProductVariantKeywords(
                        asList("Title Keyword 1", "Title Keyword 1"),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>()
                )
        );

        // POST
        Response response = ProductVersioningApiRequests.replaceVariantKeywordSet(keywordSetToAdd, randomUUID, jwt);
        validateUnprocessableEntityError(response, expectedError);

        // PUT
        response = ProductVersioningApiRequests.addKeywordsToVariantKeywordSet(keywordSetToAdd, randomUUID, jwt);
        validateUnprocessableEntityError(response, expectedError);

        // DELETE
        response = ProductVersioningApiRequests.deleteKeywordsFromVariantKeywordSet(keywordSetToAdd, randomUUID, jwt);
        validateUnprocessableEntityError(response, expectedError);

        // GET
        expectedError = expectedError.replace("ProductMasterVariantError", "ProductMasterMissingError");
        response = ProductVersioningApiRequests.getKeywordSetData(randomUUID, localeId, Enums.ProductVariantType.LIVE.getType(), jwt);
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotReplaceMergeDeleteOrGetKeywords_NonExistentVariantForLocale() throws Exception {
        String expectedError = "Type: ProductMasterError. Subtype: ProductMasterVariantError. Message: Could not find variant for the locale.";

        ProductVersioningApiService.deleteProductMasterVariants(
                productMaster._id,
                Collections.singletonList(localeId_2),
                jwt
        );

        ProductVariantKeywordSetRequestBody keywordSetToAdd = new ProductVariantKeywordSetRequestBody(
                Enums.ProductVariantLevel.GLOBAL.getLevel(),
                localeId_2,
                null,
                null,
                new ProductVariantKeywords(
                        asList("Title Keyword 1", "Title Keyword 2"),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>()
                )
        );

        // POST
        Response response = ProductVersioningApiRequests.replaceVariantKeywordSet(keywordSetToAdd, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);

        // PUT
        response = ProductVersioningApiRequests.addKeywordsToVariantKeywordSet(keywordSetToAdd, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);

        // DELETE
        response = ProductVersioningApiRequests.deleteKeywordsFromVariantKeywordSet(keywordSetToAdd, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);

        // GET
        response = ProductVersioningApiRequests.getKeywordSetData(productMaster._id, localeId_2, Enums.ProductVariantType.LIVE.getType(), jwt);
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotReplaceOrMergeKeywordSet_DuplicateKeywordsAcrossBuckets() throws Exception {
        var keywordsToAdd = new ProductVariantKeywords(
                asList("Title Keyword 1", "Title Keyword 2"),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                asList("Title Keyword 3", "title Keyword 1"),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        var keywordSetToAdd = new ProductVariantKeywordSetRequestBody(
                Enums.ProductVariantLevel.GLOBAL.getLevel(),
                localeId,
                null,
                null,
                keywordsToAdd
        );

        var response = ProductVersioningApiRequests.replaceVariantKeywordSet(keywordSetToAdd, productMaster._id, jwt);
        var expectedError = "Type: KeywordSetError. Subtype: KeywordValidationError. Message: Duplicate keyword(s) detected.";
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotAddKeywordSetToVariant_RetailerLevel_NullRetailerId() throws Exception {
        ProductVariantKeywords keywordsToAdd = new ProductVariantKeywords(
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        ProductVariantKeywordSetRequestBody keywordSetToAdd = new ProductVariantKeywordSetRequestBody(
                Enums.ProductVariantLevel.RETAILER.getLevel(),
                localeId,
                null,
                null,
                keywordsToAdd
        );

        Response response = ProductVersioningApiRequests.replaceVariantKeywordSet(keywordSetToAdd, productMaster._id, jwt);
        String expectedError = "Type: ProductMasterError. Subtype: ProductMasterVariantError. Message: Must supply a retailerId for retailer level variant.";
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotAddKeywordSetToVariant_CampaignLevel_NullCampaignId() throws Exception {
        ProductVariantKeywords keywordsToAdd = new ProductVariantKeywords(
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        ProductVariantKeywordSetRequestBody keywordSetToAdd = new ProductVariantKeywordSetRequestBody(
                Enums.ProductVariantLevel.GLOBAL_CAMPAIGN.getLevel(),
                localeId,
                null,
                null,
                keywordsToAdd
        );

        Response response = ProductVersioningApiRequests.replaceVariantKeywordSet(keywordSetToAdd, productMaster._id, jwt);
        String expectedError = "Type: ProductMasterError. Subtype: ProductMasterVariantError. Message: Must supply a campaignId for campaign level variant.";
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotAddKeywordSetToVariant_RetailerCampaignLevel_NullIds() throws Exception {
        ProductVariantKeywords keywordsToAdd = new ProductVariantKeywords(
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        ProductVariantKeywordSetRequestBody keywordSetToAdd = new ProductVariantKeywordSetRequestBody(
                Enums.ProductVariantLevel.RETAILER_CAMPAIGN.getLevel(),
                localeId,
                null,
                null,
                keywordsToAdd
        );

        Response response = ProductVersioningApiRequests.replaceVariantKeywordSet(keywordSetToAdd, productMaster._id, jwt);
        String expectedError = "Type: ProductMasterError. Subtype: ProductMasterVariantError. Message: Must supply a campaignId and retailerId for retailerCampaign level variant.";
        validateUnprocessableEntityError(response, expectedError);
    }

    private void verifyReturnedKeywordSetMatchWithExpectedSet(ProductVariantKeywordSet returnedKeywordSet, ProductVariantKeywordSet expectedKeywordSet) {
        Assert.assertEquals(
                returnedKeywordSet, expectedKeywordSet,
                "Returned keyword set didn't match with the expected keyword" +
                        "Expected keyword set: \n" + expectedKeywordSet +
                        "Returned keyword set: \n" + returnedKeywordSet
        );
    }

    private void verifyKeywordSetWasUpdatedProperly(
            ProductVariantKeywordSet keywordSetReturned,
            ProductVariantKeywordSet keywordSetOriginal,
            ProductVariantKeywordSetRequestBody keywordSetRequestBody,
            ProductVariantKeywords expectedKeywords
    ) throws IOException {
        Assert.assertNotEquals(keywordSetReturned._id, keywordSetOriginal == null ? null : keywordSetOriginal._id, "KeywordSetId was not updated");
        Assert.assertEquals(keywordSetReturned._version, 1, "Version of Keyword set didn't match with the expected version");
        Assert.assertNotNull(keywordSetReturned.dateCreated, "DateCreated of Keyword set was null");
        Assert.assertEquals(keywordSetReturned.dateUpdated, keywordSetReturned.dateCreated, "DateUpdated of Keyword set didn't match with the dateCreated");

        verifyProductBaseData(keywordSetReturned, productMaster, keywordSetRequestBody, keywordSetOriginal == null ? null : keywordSetOriginal._id);

        Assert.assertEquals(keywordSetReturned.keywords, expectedKeywords, "Keywords didn't match with the expected keywords");

        productMaster = PRODUCT_MASTER_COLLECTION.getProductMaster(productMaster._id);

        ProductMaster.VariantSets.Live updatedLiveVariant = productMaster.variantSets.live
                .stream().filter(variant -> variant.localeId.equals(localeId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        Assert.assertEquals(
                updatedLiveVariant.instances.global.keywordSetId,
                keywordSetReturned._id,
                "Global live instance didn't point to the keyword set returned"
        );
    }
}
