package com.DC.apitests.productversioning.products;

import com.DC.apitests.ApiValidations;
import com.DC.apitests.productversioning.ApiTestConfig;
import com.DC.db.productVersioning.ProductMasterCollection;
import com.DC.db.productVersioning.ProductMasterVariantDigitalAssetCollection;
import com.DC.db.productVersioning.ProductMasterVariantPropertySetCollection;
import com.DC.testcases.BaseClass;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.ProductVersioningApiRequests;
import com.DC.utilities.apiEngine.apiServices.insights.CPGData.SearchPhrases.SearchPhrasesService;
import com.DC.utilities.apiEngine.apiServices.productversioning.CompanyApiService;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.apiEngine.models.requests.productVersioning.CommitOrClearStagedDataRequestBody;
import com.DC.utilities.apiEngine.models.responses.insights.CPGData.SearchPhrases.SearchPhraseVolume;
import com.DC.utilities.apiEngine.models.responses.productVersioning.*;
import com.DC.utilities.enums.Enums;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.DC.utilities.SecurityAPI.changeInsightsCompanyAndGetJwt;
import static com.DC.utilities.SecurityAPI.loginAndGetJwt;
import static com.DC.utilities.productManager.ProductVersioningCommonMethods.*;
import static java.util.Arrays.asList;

public class ProductMasterStagedApiTests extends BaseClass {
    public final ApiTestConfig.TestConfig TEST_CONFIG = ApiTestConfig.getTestConfig();
    public final ProductMasterCollection PRODUCT_MASTER_COLLECTION = new ProductMasterCollection();
    public final ProductMasterVariantPropertySetCollection PROPERTY_SET_COLLECTION = new ProductMasterVariantPropertySetCollection();
    public final ProductMasterVariantDigitalAssetCollection DIGITAL_ASSET_COLLECTION = new ProductMasterVariantDigitalAssetCollection();
    public final String PROPERTY_SET_ID = "3cbcd2ea-2bad-447e-9205-d14640bb4ab1";
    public final String DIGITAL_ASSET_SET_ID = "cf5df3e3-e5a3-432a-9bd2-297ac59a541b";

    public String jwt;
    public Company company;

    @BeforeClass(alwaysRun = true)
    public void setupTests() throws Exception {
        jwt = loginAndGetJwt(TEST_CONFIG.loginEndpoint, TEST_CONFIG.supportUsername, TEST_CONFIG.password);
        jwt = changeInsightsCompanyAndGetJwt(jwt, TEST_CONFIG.companyID, TEST_CONFIG.companyName);
        company = CompanyApiService.getCompany(jwt);
    }

    @Test(groups = {"ProductApiTests", "ProductStagedApiTests"})
    public void Api_Products_CanDeleteStagedData_StandardProperties() throws Exception {
        List<ProductVariantProperty> properties = getPropertiesToStage();

        var originalPropertySet = PROPERTY_SET_COLLECTION.updatePropertiesInSet(PROPERTY_SET_ID, properties, TEST_CONFIG.companyID);
        var instancePath = new ProductVariantInstancePath(originalPropertySet.productMasterId, originalPropertySet.locale, originalPropertySet.type, originalPropertySet.retailerId, originalPropertySet.campaignId);
        var productMasterBefore = PRODUCT_MASTER_COLLECTION.replaceStagedSetOfProduct(instancePath.productMasterId, instancePath.localeId, PROPERTY_SET_ID, DIGITAL_ASSET_SET_ID, originalPropertySet.taskMeta.chainItemId);

        performTestToDeleteStageData(originalPropertySet.taskMeta.chainItemId, productMasterBefore);
    }

    @Test(groups = {"ProductApiTests", "ProductStagedApiTests"})
    public void Api_Products_CanDeleteStagedData_DigitalAssets() throws Exception {
        List<DigitalAssetProperty> digitalAssets = getDigitalAssetsToStage();

        var originalDigitalAssetSet = DIGITAL_ASSET_COLLECTION.updateDigitalAssetsInSet(DIGITAL_ASSET_SET_ID, digitalAssets, TEST_CONFIG.companyID);
        var instancePath = new ProductVariantInstancePath(originalDigitalAssetSet.productMasterId, originalDigitalAssetSet.locale, originalDigitalAssetSet.type, originalDigitalAssetSet.retailerId, originalDigitalAssetSet.campaignId);
        var productMasterBefore = PRODUCT_MASTER_COLLECTION.replaceStagedSetOfProduct(instancePath.productMasterId, instancePath.localeId, PROPERTY_SET_ID, DIGITAL_ASSET_SET_ID, originalDigitalAssetSet.taskMeta.chainItemId);

        performTestToDeleteStageData(originalDigitalAssetSet.taskMeta.chainItemId, productMasterBefore);
    }

    @Test(groups = {"ProductApiTests", "ProductStagedApiTests"})
    public void Api_Products_CanClearStagedData_StandardProperties() throws Exception {
        List<ProductVariantProperty> properties = getPropertiesToStage();

        var originalPropertySet = PROPERTY_SET_COLLECTION.updatePropertiesInSet(PROPERTY_SET_ID, properties, TEST_CONFIG.companyID);
        var instancePath = new ProductVariantInstancePath(originalPropertySet.productMasterId, originalPropertySet.locale, originalPropertySet.type, originalPropertySet.retailerId, originalPropertySet.campaignId);
        var productMaster = PRODUCT_MASTER_COLLECTION.replaceStagedSetOfProduct(instancePath.productMasterId, instancePath.localeId, PROPERTY_SET_ID, DIGITAL_ASSET_SET_ID, originalPropertySet.taskMeta.chainItemId);

        // TEST STARTS HERE
        var requestBody = generateRequestBodyForClearOrCommitData(instancePath, List.of(properties.get(0).id, properties.get(2).id), originalPropertySet.taskMeta.chainItemId, false);
        var response = ProductVersioningApiRequests.clearOrCommitStagedData(requestBody, productMaster._id, jwt, "clear");
        ApiValidations.checkResponseStatus(testMethodName.get(), 200, response.statusCode());

        var propertySetFromResponse = response.jsonPath().getObject("propertySet", ProductVariantPropertySet.class);
        var expectedProperties = new ArrayList<>(properties);
        requestBody.propertyIds.forEach(propertyId -> expectedProperties.removeIf(property -> property.id.equals(propertyId)));

        Assert.assertEquals(propertySetFromResponse._id, originalPropertySet._id, "Returned property set id is not the same as the set linked to the product and chain item");
        Assert.assertEquals(propertySetFromResponse.taskMeta.chainItemId, originalPropertySet.taskMeta.chainItemId, "Returned property set chain item id is not the same as the id sent in the request");
        Assert.assertEquals(propertySetFromResponse.properties, expectedProperties, "Returned property set does not have the expected properties");
    }

    @Test(groups = {"ProductApiTests", "ProductStagedApiTests"})
    public void Api_Products_CanClearStagedData_DigitalAssets() throws Exception {
        List<DigitalAssetProperty> digitalAssets = getDigitalAssetsToStage();

        var originalPropertySet = DIGITAL_ASSET_COLLECTION.updateDigitalAssetsInSet(DIGITAL_ASSET_SET_ID, digitalAssets, TEST_CONFIG.companyID);
        var instancePath = new ProductVariantInstancePath(originalPropertySet.productMasterId, originalPropertySet.locale, originalPropertySet.type, originalPropertySet.retailerId, originalPropertySet.campaignId);
        var productMaster = PRODUCT_MASTER_COLLECTION.replaceStagedSetOfProduct(instancePath.productMasterId, instancePath.localeId, PROPERTY_SET_ID, DIGITAL_ASSET_SET_ID, originalPropertySet.taskMeta.chainItemId);

        // TEST STARTS HERE
        var requestBody = generateRequestBodyForClearOrCommitData(instancePath, List.of(digitalAssets.get(0).id, digitalAssets.get(2).id), originalPropertySet.taskMeta.chainItemId, true);
        var response = ProductVersioningApiRequests.clearOrCommitStagedData(requestBody, productMaster._id, jwt, "clear");
        ApiValidations.checkResponseStatus(testMethodName.get(), 200, response.statusCode());

        var propertySetFromResponse = response.jsonPath().getObject("digitalAssetSet", ProductVariantDigitalAssetSet.class);
        var expectedProperties = new ArrayList<>(digitalAssets);
        requestBody.digitalAssetIds.forEach(propertyId -> expectedProperties.removeIf(property -> property.id.equals(propertyId)));

        Assert.assertEquals(propertySetFromResponse._id, originalPropertySet._id, "Returned property set id is not the same as the set linked to the product and chain item");
        Assert.assertEquals(propertySetFromResponse.taskMeta.chainItemId, originalPropertySet.taskMeta.chainItemId, "Returned property set chain item id is not the same as the id sent in the request");
        Assert.assertEquals(propertySetFromResponse.digitalAssets, expectedProperties, "Returned property set does not have the expected properties");
    }

    @Test(groups = {"ProductApiTests", "ProductStagedApiTests"})
    public void Api_Products_CanCommitStagedData_StandardProperties() throws Exception {
        List<ProductVariantProperty> stagedProperties = getPropertiesToStage();
        var propertiesToCommit = List.of(stagedProperties.get(0), stagedProperties.get(2));

        var originalPropertySet = PROPERTY_SET_COLLECTION.updatePropertiesInSet(PROPERTY_SET_ID, stagedProperties, TEST_CONFIG.companyID);
        var instancePath = new ProductVariantInstancePath(originalPropertySet.productMasterId, originalPropertySet.locale, originalPropertySet.type, originalPropertySet.retailerId, originalPropertySet.campaignId);

        var productMaster = PRODUCT_MASTER_COLLECTION.replaceStagedSetOfProduct(instancePath.productMasterId, instancePath.localeId, PROPERTY_SET_ID, DIGITAL_ASSET_SET_ID, originalPropertySet.taskMeta.chainItemId);
        var propertySetIdLive = productMaster.variantSets.live.stream().filter(set -> set.localeId.equals(instancePath.localeId)).findFirst().orElseThrow().instances.global.propertySetId;
        var propertySetLive = PROPERTY_SET_COLLECTION.getPropertySet(propertySetIdLive);

        // TEST STARTS HERE
        var requestBody = generateRequestBodyForClearOrCommitData(instancePath, propertiesToCommit.stream().map(prop -> prop.id).collect(Collectors.toList()), originalPropertySet.taskMeta.chainItemId, false);
        var response = ProductVersioningApiRequests.clearOrCommitStagedData(requestBody, productMaster._id, jwt, "commit");
        ApiValidations.checkResponseStatus(testMethodName.get(), 200, response.statusCode());

        var livePropertySetFromResponse = response.jsonPath().getObject("live.propertySet", ProductVariantPropertySet.class);
        var stagedPropertySetFromResponse = response.jsonPath().getObject("staged.propertySet", ProductVariantPropertySet.class);
        stagedProperties.removeAll(propertiesToCommit);

        Assert.assertEquals(stagedPropertySetFromResponse._id, originalPropertySet._id, "Returned property set id is not the same as the set linked to the product and chain item");
        Assert.assertEquals(stagedPropertySetFromResponse.taskMeta.chainItemId, originalPropertySet.taskMeta.chainItemId, "Returned property set chain item id is not the same as the id sent in the request");
        Assert.assertEquals(stagedPropertySetFromResponse.properties, stagedProperties, "Returned staged property set does not have the expected properties");

        var expectedLiveProperties = new ArrayList<>(propertySetLive.properties);
        for (var property : propertiesToCommit) {
            var propertyWasAlreadyInLive = expectedLiveProperties.stream().anyMatch(prop -> prop.id.equals(property.id));
            if (!propertyWasAlreadyInLive) {
                expectedLiveProperties.add(property);
            } else {
                expectedLiveProperties.stream().filter(prop -> prop.id.equals(property.id)).findFirst().orElseThrow().values = property.values;
            }
        }

        productMaster = PRODUCT_MASTER_COLLECTION.getProductMaster(productMaster._id);
        propertySetIdLive = productMaster.variantSets.live.stream().filter(set -> set.localeId.equals(instancePath.localeId)).findFirst().orElseThrow().instances.global.propertySetId;
        Assert.assertEquals(livePropertySetFromResponse._id, propertySetIdLive, "Live property set id is not the same as the id returned after committing data");
        Assert.assertEquals(livePropertySetFromResponse.properties, expectedLiveProperties, "Live property set does not have the expected properties after committing data");
    }

    @Test(groups = {"ProductApiTests", "ProductStagedApiTests"})
    public void Api_Products_CanCommitStagedData_DigitalAssets() throws Exception {
        List<DigitalAssetProperty> stagedDigitalAssets = getDigitalAssetsToStage();
        var propertiesToCommit = List.of(stagedDigitalAssets.get(0), stagedDigitalAssets.get(2));

        var originalPropertySet = DIGITAL_ASSET_COLLECTION.updateDigitalAssetsInSet(DIGITAL_ASSET_SET_ID, stagedDigitalAssets, TEST_CONFIG.companyID);
        var instancePath = new ProductVariantInstancePath(originalPropertySet.productMasterId, originalPropertySet.locale, originalPropertySet.type, originalPropertySet.retailerId, originalPropertySet.campaignId);
        var productMaster = PRODUCT_MASTER_COLLECTION.replaceStagedSetOfProduct(instancePath.productMasterId, instancePath.localeId, PROPERTY_SET_ID, DIGITAL_ASSET_SET_ID, originalPropertySet.taskMeta.chainItemId);

        var propertySetIdLive = productMaster.variantSets.live.stream().filter(set -> set.localeId.equals(instancePath.localeId)).findFirst().orElseThrow().instances.global.digitalAssetSetId;
        var propertySetLive = DIGITAL_ASSET_COLLECTION.getDigitalAsset(propertySetIdLive);

        // TEST STARTS HERE
        var requestBody = generateRequestBodyForClearOrCommitData(instancePath, propertiesToCommit.stream().map(prop -> prop.id).collect(Collectors.toList()), originalPropertySet.taskMeta.chainItemId, true);
        var response = ProductVersioningApiRequests.clearOrCommitStagedData(requestBody, productMaster._id, jwt, "commit");
        ApiValidations.checkResponseStatus(testMethodName.get(), 200, response.statusCode());

        var livePropertySetFromResponse = response.jsonPath().getObject("live.digitalAssetSet", ProductVariantDigitalAssetSet.class);
        var stagedPropertySetFromResponse = response.jsonPath().getObject("staged.digitalAssetSet", ProductVariantDigitalAssetSet.class);
        stagedDigitalAssets.removeAll(propertiesToCommit);

        Assert.assertEquals(stagedPropertySetFromResponse._id, originalPropertySet._id, "Returned property set id is not the same as the set linked to the product and chain item");
        Assert.assertEquals(stagedPropertySetFromResponse.taskMeta.chainItemId, originalPropertySet.taskMeta.chainItemId, "Returned property set chain item id is not the same as the id sent in the request");
        Assert.assertEquals(stagedPropertySetFromResponse.digitalAssets, stagedDigitalAssets, "Returned staged property set does not have the expected properties");

        var expectedLiveProperties = new ArrayList<>(propertySetLive.digitalAssets);
        for (var property : propertiesToCommit) {
            var propertyWasAlreadyInLive = expectedLiveProperties.stream().anyMatch(prop -> prop.id.equals(property.id));
            if (!propertyWasAlreadyInLive) {
                expectedLiveProperties.add(property);
            } else {
                expectedLiveProperties.stream().filter(prop -> prop.id.equals(property.id)).findFirst().orElseThrow().assets = property.assets;
            }
        }

        productMaster = PRODUCT_MASTER_COLLECTION.getProductMaster(productMaster._id);
        propertySetIdLive = productMaster.variantSets.live.stream().filter(set -> set.localeId.equals(instancePath.localeId)).findFirst().orElseThrow().instances.global.digitalAssetSetId;
        Assert.assertEquals(livePropertySetFromResponse._id, propertySetIdLive, "Live property set id is not the same as the id returned after committing data");
        Assert.assertEquals(livePropertySetFromResponse.digitalAssets, expectedLiveProperties, "Live property set does not have the expected properties after committing data");
    }

    @Test(groups = {"ProductApiTests", "ProductStagedApiTests"})
    public void Api_Products_CanGetFullProductCompositionStaged() throws Exception {
        var productToTest = "QA-IMPORTS-003";
        List<ProductMasterCompositionStaged> expectedProductMasterCompositions = new ArrayList<>();

        var productMaster = ProductVersioningApiService.getProductMasterByUniqueId(productToTest, jwt);

        for (ProductMaster.VariantSets.Staged variantSet : productMaster.variantSets.staged) {
            ProductMasterCompositionStaged expectedProductMasterComposition = getExpectedVariantComposition(productMaster, variantSet);
            expectedProductMasterCompositions.add(expectedProductMasterComposition);
        }

        List<ProductMasterCompositionStaged> compositionsReturned = ProductVersioningApiService.getProductMasterComposedStaged(productMaster._id, jwt);

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

    private List<ProductVariantProperty> getPropertiesToStage() {
        return new ArrayList<>() {{
            add(new ProductVariantProperty("bullet_1", List.of("testing " + SharedMethods.generateRandomNumber())));
            add(new ProductVariantProperty("bullet_2", List.of("testing 2" + SharedMethods.generateRandomNumber())));
            add(new ProductVariantProperty("bullet_3", List.of("testing 3" + SharedMethods.generateRandomNumber())));
        }};
    }

    private List<DigitalAssetProperty> getDigitalAssetsToStage() {
        var firstImage = new DigitalAssetProperty("test_digital_asset_1", List.of(getSmallModifiedImageDigitalAsset(READ_CONFIG.getInsightsEnvironment())));
        var secondImage = new DigitalAssetProperty("test_digital_asset_2", List.of(getSmallImageDigitalAsset(READ_CONFIG.getInsightsEnvironment())));
        var thirdImage = new DigitalAssetProperty("calories_image", List.of(getTestingMemeImageDigitalAsset(READ_CONFIG.getInsightsEnvironment())));
        return new ArrayList<>() {{
            add(firstImage);
            add(secondImage);
            add(thirdImage);
        }};
    }

    private void performTestToDeleteStageData(String chainItemId, ProductMaster productMasterBefore) throws Exception {
        var liveVariantSetsBefore = productMasterBefore.variantSets.live;

        var response = ProductVersioningApiRequests.deleteProductVariantStaged(chainItemId, productMasterBefore._id, jwt);
        var productMasterAfter = ApiValidations.verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ProductMaster.class);
        var liveVariantSetsAfter = productMasterAfter.variantSets.live;
        Assert.assertEquals(liveVariantSetsAfter, liveVariantSetsBefore, "Live variant sets were changed after deleting staged data");

        var stagedVariantSetsAfter = productMasterAfter.variantSets.staged;
        var expectedStagedVariantSets = productMasterBefore.variantSets.staged.stream().filter(set -> !set.chainItemId.equals(chainItemId)).collect(Collectors.toList());
        Assert.assertEquals(stagedVariantSetsAfter, expectedStagedVariantSets, "Staged variant sets were not changed after deleting staged data");
    }

    private CommitOrClearStagedDataRequestBody generateRequestBodyForClearOrCommitData(ProductVariantInstancePath instancePath, List<String> ids, String chainItemId, boolean isDigitalAsset) {
        return new CommitOrClearStagedDataRequestBody(
                instancePath.getProductLevel(),
                instancePath.localeId,
                instancePath.retailerId,
                instancePath.campaignId,
                isDigitalAsset ? new ArrayList<>() : ids,
                isDigitalAsset ? ids : new ArrayList<>(),
                chainItemId
        );
    }

    private ProductMasterCompositionStaged getExpectedVariantComposition(ProductMaster productMaster, ProductMaster.VariantSets.Staged stagedVariant) throws Exception {
        ProductVariantInstancePath instancePath = new ProductVariantInstancePath(
                productMaster._id,
                stagedVariant.localeId,
                Enums.ProductVariantType.STAGED,
                null,
                null
        );

        var liveProductMasterComposition = ProductVersioningApiService.getProductMasterComposed(productMaster._id, jwt);
        var liveCompositionForStagedLocale = liveProductMasterComposition.stream().filter(composition -> composition.localeId.equals(stagedVariant.localeId)).findFirst().orElseThrow();

        ProductMasterCompositionStaged expectedProductMasterComposition = new ProductMasterCompositionStaged();
        expectedProductMasterComposition.localeId = instancePath.localeId;
        expectedProductMasterComposition.chainItemId = stagedVariant.chainItemId;
        expectedProductMasterComposition.global = getExpectedInstanceComposition(stagedVariant, liveCompositionForStagedLocale.global, instancePath, stagedVariant.instances.global, productMaster);
        expectedProductMasterComposition.retailer = new ArrayList<>();
        expectedProductMasterComposition.globalCampaign = new ArrayList<>();
        expectedProductMasterComposition.retailerCampaign = new ArrayList<>();

        for (ProductMaster.VariantSets.Staged.ProductVariantInstancesStaged.ProductInstanceStagedRetailer retailerInstance : stagedVariant.instances.retailer) {
            instancePath = new ProductVariantInstancePath(
                    productMaster._id,
                    stagedVariant.localeId,
                    Enums.ProductVariantType.STAGED,
                    retailerInstance.retailerId,
                    null
            );

            var liveCompositionForInstance = liveCompositionForStagedLocale.retailer.stream().filter(inst -> inst.productMasterInfo.retailerId.equals(retailerInstance.retailerId)).findFirst().orElseThrow();
            ProductMasterInstanceComposition retailerInstanceBase = getExpectedInstanceComposition(stagedVariant, liveCompositionForInstance, instancePath, retailerInstance, productMaster);
            ProductMasterRetailerInstanceComposition retailerInstanceComposition = new ProductMasterRetailerInstanceComposition(retailerInstanceBase, liveCompositionForInstance.rpc, liveCompositionForInstance.previousRpcs, liveCompositionForInstance.businessUnits);
            expectedProductMasterComposition.retailer.add(retailerInstanceComposition);
        }

        for (ProductMaster.VariantSets.Staged.ProductVariantInstancesStaged.ProductInstanceStagedCampaign campaignInstance : stagedVariant.instances.globalCampaign) {
            instancePath = new ProductVariantInstancePath(
                    productMaster._id,
                    stagedVariant.localeId,
                    Enums.ProductVariantType.STAGED,
                    null,
                    campaignInstance.campaignId
            );
            var liveCompositionForInstance = liveCompositionForStagedLocale.globalCampaign.stream().filter(inst -> inst.productMasterInfo.campaignId.equals(campaignInstance.campaignId)).findFirst().orElseThrow();
            expectedProductMasterComposition.globalCampaign.add(getExpectedInstanceComposition(stagedVariant, liveCompositionForInstance, instancePath, campaignInstance, productMaster));
        }

        for (ProductMaster.VariantSets.Staged.ProductVariantInstancesStaged.ProductInstanceStagedRetailerCampaign retailerCampaignInstance : stagedVariant.instances.retailerCampaign) {
            instancePath = new ProductVariantInstancePath(
                    productMaster._id,
                    stagedVariant.localeId,
                    Enums.ProductVariantType.STAGED,
                    retailerCampaignInstance.retailerId,
                    retailerCampaignInstance.campaignId
            );

            var liveCompositionForInstance = liveCompositionForStagedLocale.retailerCampaign.stream().filter(inst -> inst.productMasterInfo.retailerId.equals(retailerCampaignInstance.retailerId)
                            && inst.productMasterInfo.campaignId.equals(retailerCampaignInstance.campaignId))
                    .findFirst().orElseThrow();
            ProductMasterInstanceComposition retailerCampaignInstanceBase = getExpectedInstanceComposition(stagedVariant, liveCompositionForInstance, instancePath, retailerCampaignInstance, productMaster);
            ProductMasterRetailerInstanceComposition retailerInstanceComposition = new ProductMasterRetailerInstanceComposition(
                    retailerCampaignInstanceBase,
                    liveCompositionForInstance.rpc,
                    liveCompositionForInstance.previousRpcs,
                    liveCompositionForInstance.businessUnits
            );
            expectedProductMasterComposition.retailerCampaign.add(retailerInstanceComposition);
        }
        return expectedProductMasterComposition;
    }

    private ProductMasterInstanceComposition getExpectedInstanceComposition(
            ProductMaster.VariantSets.Staged stagedVariantSet,
            ProductMasterInstanceComposition liveCompositionForInstance,
            ProductVariantInstancePath instancePath,
            ProductMaster.VariantSets.Staged.ProductVariantInstancesStaged.ProductInstanceStagedGlobal instance,
            ProductMaster productMaster
    ) throws Exception {
        ProductInvariantAttributeSetCore expectedAttributes = null;
        if (stagedVariantSet.invariantData.attributeSetId != null) {
            ProductInvariantAttributeSet attributeSet = ProductVersioningApiService.getAttributeSetData(
                    instancePath.productMasterId, stagedVariantSet.localeId,
                    Enums.ProductVariantType.STAGED,
                    jwt
            );
            expectedAttributes = new ProductInvariantAttributeSetCore(attributeSet.categoryId, attributeSet.attributes);
        }
        
        List<ProductVariantProperty> expectedProperties = ProductVersioningApiService.getPropertySetData(instancePath, stagedVariantSet.chainItemId, jwt).properties;
        expectedProperties = expectedProperties == null ? new ArrayList<>() : expectedProperties;

        // TODO: Uncomment this when we can stage keywords
        //ProductVariantKeywords expectedKeywords = ProductVersioningApiService.getProductKeywordSet(instancePath, stagedVariantSet.chainItemId, jwt).keywords;

        ProductVariantKeywords expectedKeywords = null;
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

        List<DigitalAssetProperty> expectedDigitalAssets = ProductVersioningApiService.getDigitalAssetSetData(instancePath, stagedVariantSet.chainItemId, jwt).digitalAssets;

        if (expectedDigitalAssets == null) {
            expectedDigitalAssets = new ArrayList<>();
        }

        return new ProductMasterInstanceComposition(
                liveCompositionForInstance.productMasterInfo,
                liveCompositionForInstance.instanceUniqueId,
                liveCompositionForInstance.instanceName,
                liveCompositionForInstance.instanceThumbnail,
                instance.id,
                productMaster.uniqueId,
                productMaster.name,
                productMaster.thumbnail,
                new ArrayList<>(),
                expectedAttributes,
                expectedProperties,
                expectedKeywords,
                expectedKeywordVolumes,
                expectedDigitalAssets
        );
    }

    private void sortCompositionSearchPhraseVolumes(ProductMasterComposition composition) {
        composition.global.sortSearchPhraseVolumes();
        composition.retailer.forEach(ProductMasterRetailerInstanceComposition::sortSearchPhraseVolumes);
        composition.globalCampaign.forEach(ProductMasterInstanceComposition::sortSearchPhraseVolumes);
        composition.retailerCampaign.forEach(ProductMasterRetailerInstanceComposition::sortSearchPhraseVolumes);
    }
}
