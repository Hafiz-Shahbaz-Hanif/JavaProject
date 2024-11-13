package com.DC.apitests.productversioning.products;

import com.DC.db.productVersioning.ProductMasterVariantAttributeSetCollection;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.ProductVersioningApiRequests;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.apiEngine.models.requests.productVersioning.CreateProductMasterRequestBody;
import com.DC.utilities.apiEngine.models.requests.productVersioning.ProductInvariantAttributeSetRequestBody;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductInvariantAttribute;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductInvariantAttributeSet;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductMaster;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.DC.apitests.ApiValidations.*;
import static java.util.Arrays.asList;

public class ProductAttributesApiTests extends ProductApiTestsBaseClass {

    public ProductAttributesApiTests() {
        logger = Logger.getLogger(ProductAttributesApiTests.class);
        PropertyConfigurator.configure("log4j.properties");
    }

    private static final CreateProductMasterRequestBody QA_API_ATTRIBUTES = new CreateProductMasterRequestBody(
            "QA-API-Attributes",
            "QA API Attributes",
            null
    );

    private ProductMaster productMaster;

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception {
        productMaster = addProductMasterIfNeeded(QA_API_ATTRIBUTES);
        productMaster = createProductVariantIfNeeded(productMaster, localeId);
    }

    @AfterClass(alwaysRun = true)
    public void cleanupProduct() throws Exception {
        cleanupAddedProduct(QA_API_ATTRIBUTES.uniqueId);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanReplaceVariantAttributeSet() throws Exception {
        productMaster = ProductVersioningApiService.getProductMaster(productMaster._id, jwt);

        ProductMaster.VariantSets.Live liveVariantBeforeUpdate = productMaster.variantSets.live
                .stream().filter(variant -> variant.localeId.equals(localeId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        ProductInvariantAttributeSet attributeSetOriginal;
        if (liveVariantBeforeUpdate.invariantData.attributeSetId == null) {
            attributeSetOriginal = null;
        } else {
            attributeSetOriginal = new ProductMasterVariantAttributeSetCollection().getAttributeSet(liveVariantBeforeUpdate.invariantData.attributeSetId);
        }

        List<ProductInvariantAttribute> attributes = asList(
                new ProductInvariantAttribute(1, 1),
                new ProductInvariantAttribute(1, 2),
                new ProductInvariantAttribute(2, 2),
                new ProductInvariantAttribute(3, 3)
        );

        ProductInvariantAttributeSetRequestBody attributeSetToAdd = new ProductInvariantAttributeSetRequestBody(
                localeId,
                1,
                attributes
        );

        Response response = ProductVersioningApiRequests.replaceVariantAttributeSet(attributeSetToAdd, productMaster._id, jwt);
        ProductInvariantAttributeSet attributeSetCreated = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductInvariantAttributeSet.class);
        verifyAttributeSetWasUpdatedProperly(attributeSetCreated, attributeSetOriginal, attributeSetToAdd, attributeSetToAdd.attributes);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotReplaceVariantAttributeSet_DuplicateAttributes() throws Exception {
        List<ProductInvariantAttribute> attributes = asList(
                new ProductInvariantAttribute(1, 1),
                new ProductInvariantAttribute(1, 1),
                new ProductInvariantAttribute(3, 3)
        );

        ProductInvariantAttributeSetRequestBody attributeSetToAdd = new ProductInvariantAttributeSetRequestBody(
                localeId,
                1,
                attributes
        );

        Response response = ProductVersioningApiRequests.replaceVariantAttributeSet(attributeSetToAdd, productMaster._id, jwt);
        String expectedError = "Type: AttributeSetError. Subtype: AttributeSetValidationError. Message: Duplicate attribute detected.";
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanAddAttributesToVariantAttributeSet() throws Exception {
        List<ProductInvariantAttribute> originalAttributes = asList(
                new ProductInvariantAttribute(1, 1),
                new ProductInvariantAttribute(2, 2),
                new ProductInvariantAttribute(3, 3)
        );

        List<ProductInvariantAttribute> attributesToAdd = asList(
                new ProductInvariantAttribute(2, 2),
                new ProductInvariantAttribute(4, 4),
                new ProductInvariantAttribute(6, 6),
                new ProductInvariantAttribute(8, 8)
        );

        List<ProductInvariantAttribute> expectedAttributes = Stream.of(originalAttributes, attributesToAdd)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());

        ProductInvariantAttributeSetRequestBody attributeSetRequestBody = new ProductInvariantAttributeSetRequestBody(
                localeId,
                1,
                originalAttributes
        );

        ProductInvariantAttributeSet attributeSetOriginal = ProductVersioningApiService.replaceVariantAttributeSet(attributeSetRequestBody, productMaster._id, jwt);

        attributeSetRequestBody.attributes = attributesToAdd;
        Response response = ProductVersioningApiRequests.addAttributesToVariantAttributeSet(attributeSetRequestBody, productMaster._id, jwt);
        ProductInvariantAttributeSet attributeSetReturned = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductInvariantAttributeSet.class);
        verifyAttributeSetWasUpdatedProperly(attributeSetReturned, attributeSetOriginal, attributeSetRequestBody, expectedAttributes);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanGetAttributeData() throws Exception {
        productMaster = ProductVersioningApiService.getProductMaster(productMaster._id, jwt);

        String attributeSetId = productMaster.variantSets.live
                .stream()
                .filter(attribute -> attribute.localeId.equals(localeId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new)
                .invariantData.attributeSetId;

        ProductInvariantAttributeSet expectedAttributeSet;

        if (attributeSetId == null) {
            List<ProductInvariantAttribute> attributes = asList(
                    new ProductInvariantAttribute(7, 7),
                    new ProductInvariantAttribute(8, 8),
                    new ProductInvariantAttribute(9, 9)
            );
            ProductInvariantAttributeSetRequestBody attributeSetRequestBody = new ProductInvariantAttributeSetRequestBody(
                    localeId,
                    1,
                    attributes
            );
            expectedAttributeSet = ProductVersioningApiService.replaceVariantAttributeSet(attributeSetRequestBody, productMaster._id, jwt);
        } else {
            expectedAttributeSet = new ProductMasterVariantAttributeSetCollection().getAttributeSet(attributeSetId);
        }

        Response response = ProductVersioningApiRequests.getAttributeSetData(productMaster._id, localeId, Enums.ProductVariantType.LIVE.getType(), jwt);
        ProductInvariantAttributeSet returnedAttributeSet = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductInvariantAttributeSet.class);
        verifyReturnedAttributeSetMatchWithExpectedAttributeSet(returnedAttributeSet, expectedAttributeSet);

        response = ProductVersioningApiRequests.getAttributeSetDataByUniqueId(productMaster.uniqueId, localeId, Enums.ProductVariantType.LIVE.getType(), jwt);
        returnedAttributeSet = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductInvariantAttributeSet.class);
        verifyReturnedAttributeSetMatchWithExpectedAttributeSet(returnedAttributeSet, expectedAttributeSet);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CanDeleteAttributesFromVariantAttributeSet() throws Exception {
        List<ProductInvariantAttribute> attributes = new LinkedList<>(asList(
                new ProductInvariantAttribute(1, 1),
                new ProductInvariantAttribute(2, 2),
                new ProductInvariantAttribute(3, 3),
                new ProductInvariantAttribute(4, 4),
                new ProductInvariantAttribute(5, 5),
                new ProductInvariantAttribute(6, 6),
                new ProductInvariantAttribute(7, 7),
                new ProductInvariantAttribute(8, 8)
        ));

        List<ProductInvariantAttribute> attributesToRemove = new LinkedList<>(asList(
                new ProductInvariantAttribute(2, 2),
                new ProductInvariantAttribute(4, 4),
                new ProductInvariantAttribute(6, 6)
        ));

        ProductInvariantAttributeSetRequestBody attributeSetRequestBody = new ProductInvariantAttributeSetRequestBody(
                localeId,
                1,
                attributes
        );

        ProductInvariantAttributeSet attributeSetOriginal = ProductVersioningApiService.replaceVariantAttributeSet(attributeSetRequestBody, productMaster._id, jwt);

        Response response = ProductVersioningApiRequests.deleteAttributesFromVariantAttributeSet(productMaster._id, attributeSetRequestBody.localeId, attributesToRemove, jwt);
        ProductInvariantAttributeSet attributeSetReturned = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductInvariantAttributeSet.class);
        attributes.removeAll(attributesToRemove);
        verifyAttributeSetWasUpdatedProperly(attributeSetReturned, attributeSetOriginal, attributeSetRequestBody, attributes);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotReplaceMergeOrDeleteAttributes_MissingRequiredParameters() throws Exception {
        String invalidBody = "{}";

        List<String> missingParameters = new ArrayList<>();
        missingParameters.add("categoryId");
        missingParameters.add("attributes");
        missingParameters.add("localeId");

        Response response = ProductVersioningApiRequests.replaceVariantAttributeSet(invalidBody, productMaster._id, jwt);
        validateMissingRequestParametersError(response, missingParameters);

        response = ProductVersioningApiRequests.addAttributesToVariantAttributeSet(invalidBody, productMaster._id, jwt);
        validateMissingRequestParametersError(response, missingParameters);

        missingParameters = asList("localeId", "attributes");
        response = ProductVersioningApiRequests.deleteAttributesFromVariantAttributeSet(invalidBody, productMaster._id, jwt);
        validateMissingRequestParametersError(response, missingParameters);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotReplaceMergeDeleteOrGetAttributes_InvalidParameters() throws Exception {
        String randomUUID = UUID.randomUUID().toString();

        String bodyWithInvalidParameters = "{\n" +
                "    \"invalid\": \"invalid\",\n" +
                "    \"localeId\": \"invalid\",\n" +
                "    \"categoryId\": false,\n" +
                "    \"attributes\": [\"qa\"]\n" +
                "}";

        List<String> expectedErrors = new ArrayList<>();
        expectedErrors.add("\"categoryId\" must be a number");
        expectedErrors.add("\"0\" must be an object");
        expectedErrors.add("\"localeId\" needs to be a mongo Binary object");
        expectedErrors.add("\"invalid\" is not allowed");

        // POST
        Response response = ProductVersioningApiRequests.replaceVariantAttributeSet(bodyWithInvalidParameters, randomUUID, jwt);
        validateInvalidRequestParametersError(response, expectedErrors);

        // PUT
        response = ProductVersioningApiRequests.addAttributesToVariantAttributeSet(bodyWithInvalidParameters, randomUUID, jwt);
        validateInvalidRequestParametersError(response, expectedErrors);

        // DELETE
        bodyWithInvalidParameters = "{\n" +
                "    \"invalid\": \"invalid\",\n" +
                "    \"localeId\": \"invalid\",\n" +
                "    \"attributes\": [\"qa\"]\n" +
                "}";

        expectedErrors.remove("\"categoryId\" must be a number");
        response = ProductVersioningApiRequests.deleteAttributesFromVariantAttributeSet(bodyWithInvalidParameters, randomUUID, jwt);
        validateInvalidRequestParametersError(response, expectedErrors);

        // GET
        expectedErrors = new ArrayList<>();
        expectedErrors.add("\"productMasterId\" needs to be a mongo Binary object");
        expectedErrors.add("\"type\" must be one of [live, staged]");
        expectedErrors.add("\"localeId\" needs to be a mongo Binary object");

        response = ProductVersioningApiRequests.getAttributeSetData("12345", "invalidLocale", "invalidType", jwt);
        validateInvalidRequestParametersError(response, expectedErrors);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotReplaceMergeDeleteOrGetAttributes_NonExistentProductMaster() throws Exception {
        String randomUUID = UUID.randomUUID().toString();
        String expectedError = "Type: ProductMasterError. Subtype: ProductMasterVariantError. Message: Could not find product master.";

        List<ProductInvariantAttribute> attributes = asList(
                new ProductInvariantAttribute(1, 1),
                new ProductInvariantAttribute(2, 2),
                new ProductInvariantAttribute(3, 3)
        );

        ProductInvariantAttributeSetRequestBody attributeSetRequestBody = new ProductInvariantAttributeSetRequestBody(
                localeId,
                1,
                attributes
        );

        // POST
        Response response = ProductVersioningApiRequests.replaceVariantAttributeSet(attributeSetRequestBody, randomUUID, jwt);
        validateUnprocessableEntityError(response, expectedError);

        // PUT
        response = ProductVersioningApiRequests.addAttributesToVariantAttributeSet(attributeSetRequestBody, randomUUID, jwt);
        validateUnprocessableEntityError(response, expectedError);

        // DELETE
        response = ProductVersioningApiRequests.deleteAttributesFromVariantAttributeSet(
                randomUUID,
                attributeSetRequestBody.localeId,
                attributeSetRequestBody.attributes,
                jwt
        );
        validateUnprocessableEntityError(response, expectedError);

        // GET
        expectedError = expectedError.replace("ProductMasterVariantError", "ProductMasterMissingError");
        response = ProductVersioningApiRequests.getAttributeSetData(randomUUID, localeId, Enums.ProductVariantType.LIVE.getType(), jwt);
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"ProductApiTests", "NoDataProvider"})
    public void Api_Products_CannotReplaceMergeDeleteOrGetAttributes_NonExistentVariantForLocale() throws Exception {
        String expectedError = "Type: ProductMasterError. Subtype: ProductMasterVariantError. Message: Could not find variant for the locale.";

        ProductVersioningApiService.deleteProductMasterVariants(
                productMaster._id,
                Collections.singletonList(localeId_2),
                jwt
        );

        List<ProductInvariantAttribute> attributes = Collections.singletonList(
                new ProductInvariantAttribute(1, 1)
        );

        ProductInvariantAttributeSetRequestBody attributeSetRequestBody = new ProductInvariantAttributeSetRequestBody(
                localeId_2,
                1,
                attributes
        );

        // POST
        Response response = ProductVersioningApiRequests.replaceVariantAttributeSet(attributeSetRequestBody, productMaster._id, jwt);

        logger.info(response.getBody().jsonPath());
        validateUnprocessableEntityError(response, expectedError);

        // PUT
        response = ProductVersioningApiRequests.addAttributesToVariantAttributeSet(attributeSetRequestBody, productMaster._id, jwt);
        validateUnprocessableEntityError(response, expectedError);

        // DELETE
        response = ProductVersioningApiRequests.deleteAttributesFromVariantAttributeSet(
                productMaster._id,
                attributeSetRequestBody.localeId,
                attributeSetRequestBody.attributes,
                jwt
        );
        validateUnprocessableEntityError(response, expectedError);

        // GET
        response = ProductVersioningApiRequests.getAttributeSetData(productMaster._id, localeId_2, Enums.ProductVariantType.LIVE.getType(), jwt);
        validateUnprocessableEntityError(response, expectedError);
    }

    private void verifyReturnedAttributeSetMatchWithExpectedAttributeSet(ProductInvariantAttributeSet returnedAttributeSet, ProductInvariantAttributeSet expectedAttributeSet) {
        Assert.assertEquals(
                returnedAttributeSet, expectedAttributeSet,
                "Returned attribute set didn't match with the expected attribute" +
                        "Expected attribute set: \n" + expectedAttributeSet +
                        "Returned attribute set: \n" + returnedAttributeSet
        );
    }

    private void verifyAttributeSetWasUpdatedProperly(
            ProductInvariantAttributeSet attributeSetReturned,
            ProductInvariantAttributeSet attributeSetOriginal,
            ProductInvariantAttributeSetRequestBody requestBody,
            List<ProductInvariantAttribute> expectedAttributes
    ) throws IOException {
        Assert.assertNotEquals(attributeSetReturned._id, attributeSetOriginal == null ? null : attributeSetOriginal._id, "AttributeSetId was not updated");
        Assert.assertEquals(attributeSetReturned._version, 1, "Version of attribute set didn't match with the expected version");
        Assert.assertNotNull(attributeSetReturned.dateCreated, "DateCreated of attribute set was null");
        Assert.assertEquals(attributeSetReturned.dateUpdated, attributeSetReturned.dateCreated, "DateUpdated of attribute set didn't match with the dateCreated");
        Assert.assertEquals(String.valueOf(attributeSetReturned.categoryId), String.valueOf(requestBody.categoryId), "CategoryId didn't match with the expected category");
        Assert.assertEquals(attributeSetReturned.attributes, expectedAttributes, "Attributes returned didn't match with the expected attributes");
        Assert.assertFalse(attributeSetReturned.isEditable, "isEditable field was true");
        Assert.assertEquals(attributeSetReturned.type, Enums.ProductVariantType.LIVE, "Instance type didn't match with the expected type");
        Assert.assertEquals(attributeSetReturned.companyId, productMaster.companyId, "CompanyId didn't match with the expected id");
        Assert.assertEquals(attributeSetReturned.productMasterId, productMaster._id, "Data was not stored in correct product master");
        Assert.assertEquals(attributeSetReturned.locale, localeId, "Data was not stored in correct locale");
        Assert.assertEquals(attributeSetReturned.meta.sourceSetId, attributeSetOriginal == null ? null : attributeSetOriginal._id, "SourceSetId didn't match with the expected id");

        productMaster = PRODUCT_MASTER_COLLECTION.getProductMaster(productMaster._id);

        ProductMaster.VariantSets.Live updatedLiveVariant = productMaster.variantSets.live
                .stream().filter(variant -> variant.localeId.equals(requestBody.localeId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        Assert.assertEquals(updatedLiveVariant.invariantData.attributeSetId, attributeSetReturned._id, "Variant was not pointing to the expected attribute set");
    }
}
