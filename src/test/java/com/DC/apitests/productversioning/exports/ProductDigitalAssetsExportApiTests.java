package com.DC.apitests.productversioning.exports;

import com.DC.apitests.productversioning.ApiTestConfig;
import com.DC.db.productVersioning.CompanyCollection;
import com.DC.db.productVersioning.ProductMasterVariantDigitalAssetCollection;
import com.DC.objects.productVersioning.ExportRecord;
import com.DC.objects.productVersioning.UserFriendlyInstancePath;
import com.DC.testcases.BaseClass;
import com.DC.tests.sharedAssertions.ExportCoreAssertions;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.ProductVersioningApiRequests;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.apiEngine.models.responses.productVersioning.Company;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductMaster;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductVariantInstancePath;
import com.DC.utilities.enums.Enums;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.DC.utilities.SecurityAPI.loginAndGetJwt;
import static com.DC.utilities.SharedMethods.*;
import static io.restassured.path.json.JsonPath.with;

public class ProductDigitalAssetsExportApiTests extends BaseClass {
    private static String jwt;
    private static Company company;
    private static final ApiTestConfig.TestConfig TEST_CONFIG = ApiTestConfig.getTestConfig();
    private static final String TEST_DATA_DIRECTORY = System.getProperty("user.dir") + "/src/test/java/com/DC/downloads/";
    private static final ProductMasterVariantDigitalAssetCollection DIGITAL_ASSET_COLLECTION = new ProductMasterVariantDigitalAssetCollection();
    private static final List<ProductMaster> PRODUCTS_IN_COMPANY = new ArrayList<>();

    @BeforeClass(alwaysRun = true)
    public void setupTests() throws Exception {
        company = new CompanyCollection().getCompany(TEST_CONFIG.companyID);
        jwt = loginAndGetJwt(TEST_CONFIG.loginEndpoint, TEST_CONFIG.supportUsername, TEST_CONFIG.password);
        PRODUCTS_IN_COMPANY.addAll(ProductVersioningApiService.getAllProductMastersFromCompany(jwt));
    }

    @AfterClass()
    public void deleteFiles() throws Exception {
        String prefixToDelete = "DigitalAssetExport";

        try (var stream = Files.walk(Paths.get(TEST_DATA_DIRECTORY))) {
            stream.filter(path -> path.getFileName().toString().startsWith(prefixToDelete))
                    .sorted((path1, path2) -> -path1.compareTo(path2))
                    .forEach(SharedMethods::deletePath);
        }
    }

    @Test(groups = {"ProductDigitalAssetsExportApiTests", "NoDataProvider"})
    public void Api_ProductDigitalAssetsExport_CanExportLiveProductDigitalAssets_AllProductsAndDigitalAssets() throws Exception {
        performTest(new ArrayList<>(), new ArrayList<>(), Enums.ProductVariantType.LIVE);
    }

    @Test(groups = {"ProductDigitalAssetsExportApiTests", "NoDataProvider"})
    public void Api_ProductDigitalAssetsExport_CanExportLiveProductDigitalAssets_AllProductsAndSpecificDigitalAsset() throws Exception {
        List<String> digitalAssetToExport = Collections.singletonList("test_digital_asset_1");
        performTest(new ArrayList<>(), digitalAssetToExport, Enums.ProductVariantType.LIVE);
    }

    @Test(groups = {"ProductDigitalAssetsExportApiTests", "NoDataProvider"})
    public void Api_ProductDigitalAssetsExport_CanExportLiveProductDigitalAssets_SpecificProductsAndSpecificDigitalAssets() throws Exception {
        UserFriendlyInstancePath productToExport = new UserFriendlyInstancePath("QA-IMPORTS-001", "es-MX", "Amazon.com", "");
        UserFriendlyInstancePath secondProductToExport = new UserFriendlyInstancePath("QA-EXPORTS-002", "fr-FR", "Amazon.com", "Halloween");
        UserFriendlyInstancePath thirdProductToExport = new UserFriendlyInstancePath("QA-IMPORTS-003", "fr-FR", null, null);
        UserFriendlyInstancePath fourthProductToExport = new UserFriendlyInstancePath("QA-IMPORTS-003", "fr-FR", "Amazon.com", "Christmas");

        List<UserFriendlyInstancePath> pathsOfProductsToExport = List.of(productToExport, secondProductToExport, thirdProductToExport, fourthProductToExport);

        List<String> digitalAssetsToExport = Collections.singletonList("test_digital_asset_1");
        performTest(pathsOfProductsToExport, digitalAssetsToExport, Enums.ProductVariantType.LIVE);
    }

    @Test(groups = {"ProductDigitalAssetsExportApiTests", "NoDataProvider"})
    public void Api_ProductDigitalAssetsExport_CanExportLiveProductDigitalAssets_SpecificProductsAndAllDigitalAssets() throws Exception {
        UserFriendlyInstancePath productToExport = new UserFriendlyInstancePath("QA-IMPORTS-001", "es-MX", "Amazon.com", "");
        UserFriendlyInstancePath secondProductToExport = new UserFriendlyInstancePath("QA-EXPORTS-002", "fr-FR", "Amazon.com", "Halloween");
        UserFriendlyInstancePath thirdProductToExport = new UserFriendlyInstancePath("QA-EXPORTS-EMPTY", "es-MX", "", "");
        UserFriendlyInstancePath fourthProductToExport = new UserFriendlyInstancePath("QA-IMPORTS-003", "fr-FR", null, null);
        UserFriendlyInstancePath fifthProductToExport = new UserFriendlyInstancePath("QA-IMPORTS-003", "fr-FR", "Amazon.com", "Christmas");

        List<UserFriendlyInstancePath> pathsOfProductsToExport = List.of(productToExport, secondProductToExport, thirdProductToExport, fourthProductToExport, fifthProductToExport);
        performTest(pathsOfProductsToExport, new ArrayList<>(), Enums.ProductVariantType.LIVE);
    }

    @Test(groups = {"ProductDigitalAssetsExportApiTests", "NoDataProvider"})
    public void Api_ProductDigitalAssetsExport_CanExportStagedProductDigitalAssets_AllProductsAndDigitalAssets() throws Exception {
        performTest(new ArrayList<>(), new ArrayList<>(), Enums.ProductVariantType.STAGED);
    }

    @Test(groups = {"ProductDigitalAssetsExportApiTests", "NoDataProvider"})
    public void Api_ProductDigitalAssetsExport_CanExportStagedProductDigitalAssets_AllProductsAndSpecificDigitalAsset() throws Exception {
        List<String> digitalAssetToExport = Collections.singletonList("calories_image");
        performTest(new ArrayList<>(), digitalAssetToExport, Enums.ProductVariantType.STAGED);
    }

    @Test(groups = {"ProductDigitalAssetsExportApiTests", "NoDataProvider"})
    public void Api_ProductDigitalAssetsExport_CanExportStagedProductDigitalAssets_SpecificProductsAndSpecificDigitalAssets() throws Exception {
        UserFriendlyInstancePath productToExport = new UserFriendlyInstancePath("QA-IMPORTS-001", "es-MX", "Amazon.com", "");
        UserFriendlyInstancePath secondProductToExport = new UserFriendlyInstancePath("QA-IMPORTS-003", "fr-FR", null, null);
        UserFriendlyInstancePath thirdProductToExport = new UserFriendlyInstancePath("QA-IMPORTS-003", "fr-FR", "Amazon.com", "Christmas");

        List<UserFriendlyInstancePath> pathsOfProductsToExport = new ArrayList<>(List.of(productToExport, secondProductToExport, thirdProductToExport));
        List<String> digitalAssetsToExport = Collections.singletonList("calories_image");
        performTest(pathsOfProductsToExport, digitalAssetsToExport, Enums.ProductVariantType.STAGED);
    }

    @Test(groups = {"ProductDigitalAssetsExportApiTests", "NoDataProvider"})
    public void Api_ProductDigitalAssetsExport_CanExportStagedProductDigitalAssets_SpecificProductsAndAllDigitalAssets() throws Exception {
        UserFriendlyInstancePath productToExport = new UserFriendlyInstancePath("QA-IMPORTS-001", "es-MX", "Amazon.com", "");
        UserFriendlyInstancePath secondProductToExport = new UserFriendlyInstancePath("QA-EXPORTS-002", "fr-FR", "Amazon.com", "Halloween");
        UserFriendlyInstancePath thirdProductToExport = new UserFriendlyInstancePath("QA-IMPORTS-003", "fr-FR", null, null);
        UserFriendlyInstancePath fourthProductToExport = new UserFriendlyInstancePath("QA-IMPORTS-003", "fr-FR", "Amazon.com", "Christmas");

        List<UserFriendlyInstancePath> pathsOfProductsToExport = new ArrayList<>(List.of(productToExport, secondProductToExport, thirdProductToExport, fourthProductToExport));
        performTest(pathsOfProductsToExport, new ArrayList<>(), Enums.ProductVariantType.STAGED);
    }

    // NOTE: Pass empty pathsOfProductsToExport to export all products, pass empty digitalAssetsToExport to export all digital assets
    private void performTest(List<UserFriendlyInstancePath> pathsOfProductsToExport, List<String> digitalAssetsToExport, Enums.ProductVariantType type) throws Exception {
        List<ProductVariantInstancePath> productsToExport = new ArrayList<>();
        List<String> digitalAssetSetIds;

        if (pathsOfProductsToExport.isEmpty()) {
            digitalAssetSetIds = type == Enums.ProductVariantType.LIVE ? getIdsOfAvailableDigitalAssetSets() : getIdsOfAvailableStagedDigitalAssetSets();
        } else {
            digitalAssetSetIds = getIdsOfAvailableDigitalAssetSets(pathsOfProductsToExport, productsToExport, type);
        }

        Map<String, List<Map.Entry<String, List<String>>>> expectedFoldersAndFiles = getExpectedDataInZipFile(digitalAssetSetIds, digitalAssetsToExport);

        var response = ProductVersioningApiRequests.exportProductDigitalAssets(productsToExport, digitalAssetsToExport, type.getType(), jwt);
        var exportId = ExportCoreAssertions.verifyResponseReturnsAnExportId(testMethodName.get(), response);
        ExportRecord exportRecord = ExportCoreAssertions.waitForExportToBeInDB(exportId);

        if (expectedFoldersAndFiles.isEmpty()) {
            exportRecord = ExportCoreAssertions.verifyExportStatusChangesToExpectedStatus(Enums.ProcessStatus.FAILED, exportRecord._id);
            var error = exportRecord.errors.get(0);
            Assert.assertTrue(error.contains("Error: There are no assests included to export"), "Error message didn't match with expected error message");
            return;
        }

        exportRecord = ExportCoreAssertions.verifyExportStatusChangesToExpectedStatus(Enums.ProcessStatus.SUCCESS, exportRecord._id);

        String json = new ObjectMapper().writeValueAsString(exportRecord.meta);
        String assetZipLink = with(json).get("assetZipLink");

        String filePath = TEST_DATA_DIRECTORY + Paths.get(new URI(assetZipLink).getPath()).getFileName().toString();
        String unzippedFileDestination = Paths.get(filePath).toString().replace(".zip", "");
        downloadFileFromUrl(assetZipLink, filePath);
        unzipFile(filePath, unzippedFileDestination);

        Map<String, List<Map.Entry<String, List<String>>>> folderMapInZipFile = getDataInZipFile(unzippedFileDestination);

        expectedFoldersAndFiles = sortFoldersAndFiles(expectedFoldersAndFiles);
        folderMapInZipFile = sortFoldersAndFiles(folderMapInZipFile);
        Assert.assertEquals(folderMapInZipFile, expectedFoldersAndFiles, "The contents of the zip file do not match the expected contents," +
                "Expected: " + expectedFoldersAndFiles +
                "\nActual: " + folderMapInZipFile);
    }

    private static List<String> getIdsOfAvailableDigitalAssetSets(List<UserFriendlyInstancePath> pathsOfProductsToExport, List<ProductVariantInstancePath> productsToExport, Enums.ProductVariantType type) throws Exception {
        List<String> digitalAssetSetIds = new ArrayList<>();
        for (var product : pathsOfProductsToExport) {
            ProductMaster productMaster = PRODUCTS_IN_COMPANY.stream()
                    .filter(p -> p.uniqueId.equals(product.productIdentifier))
                    .findFirst()
                    .orElseThrow();

            String localeId = company.getLocaleId(product.localeName);
            String retailerId = company.getRetailerId(product.retailerName);
            String campaignId = company.getCampaignId(product.campaignName);

            ProductVariantInstancePath variantInstancePath = new ProductVariantInstancePath(productMaster._id, localeId, type, retailerId, campaignId);
            productsToExport.add(variantInstancePath);

            String digitalAssetSetId;

            if (type == Enums.ProductVariantType.LIVE) {
                var instanceBaseData = productMaster.getInstanceBaseData(variantInstancePath);
                digitalAssetSetId = instanceBaseData == null ? null : instanceBaseData.digitalAssetSetId;
            } else {
                var instanceBaseData = productMaster.getStagedInstanceBaseData(variantInstancePath);
                digitalAssetSetId = instanceBaseData == null ? null : instanceBaseData.digitalAssetSetId;
            }

            if (digitalAssetSetId != null) {
                digitalAssetSetIds.add(digitalAssetSetId);
            }
        }
        return digitalAssetSetIds;
    }

    private static List<String> getIdsOfAvailableDigitalAssetSets() {
        List<String> digitalAssetSetIds = new ArrayList<>();
        for (ProductMaster product : PRODUCTS_IN_COMPANY) {
            for (var variantSet : product.variantSets.live) {

                String globalDigitalAssetSetId = variantSet.instances.global.digitalAssetSetId;
                if (globalDigitalAssetSetId != null) {
                    digitalAssetSetIds.add(globalDigitalAssetSetId);
                }

                for (var instance : variantSet.instances.retailer) {
                    String retailerDigitalAssetSetId = instance.digitalAssetSetId;
                    if (retailerDigitalAssetSetId != null) {
                        digitalAssetSetIds.add(retailerDigitalAssetSetId);
                    }
                }

                for (var instance : variantSet.instances.globalCampaign) {
                    String campaignDigitalAssetSetId = instance.digitalAssetSetId;
                    if (campaignDigitalAssetSetId != null) {
                        digitalAssetSetIds.add(campaignDigitalAssetSetId);
                    }
                }

                for (var instance : variantSet.instances.retailerCampaign) {
                    String retailerCampaignDigitalAssetSetId = instance.digitalAssetSetId;
                    if (retailerCampaignDigitalAssetSetId != null) {
                        digitalAssetSetIds.add(retailerCampaignDigitalAssetSetId);
                    }
                }
            }
        }
        return digitalAssetSetIds;
    }

    private static List<String> getIdsOfAvailableStagedDigitalAssetSets() {
        List<String> digitalAssetSetIds = new ArrayList<>();
        for (ProductMaster product : PRODUCTS_IN_COMPANY) {
            for (var variantSet : product.variantSets.staged) {

                String globalDigitalAssetSetId = variantSet.instances.global.digitalAssetSetId;
                if (globalDigitalAssetSetId != null) {
                    digitalAssetSetIds.add(globalDigitalAssetSetId);
                }

                for (var instance : variantSet.instances.retailer) {
                    String retailerDigitalAssetSetId = instance.digitalAssetSetId;
                    if (retailerDigitalAssetSetId != null) {
                        digitalAssetSetIds.add(retailerDigitalAssetSetId);
                    }
                }

                for (var instance : variantSet.instances.globalCampaign) {
                    String campaignDigitalAssetSetId = instance.digitalAssetSetId;
                    if (campaignDigitalAssetSetId != null) {
                        digitalAssetSetIds.add(campaignDigitalAssetSetId);
                    }
                }

                for (var instance : variantSet.instances.retailerCampaign) {
                    String retailerCampaignDigitalAssetSetId = instance.digitalAssetSetId;
                    if (retailerCampaignDigitalAssetSetId != null) {
                        digitalAssetSetIds.add(retailerCampaignDigitalAssetSetId);
                    }
                }
            }
        }
        return digitalAssetSetIds;
    }


    // Pass empty digitalAssetsToExport to get ALL DIGITAL ASSETS
    private Map<String, List<Map.Entry<String, List<String>>>> getExpectedDataInZipFile(List<String> digitalAssetSetIds, List<String> digitalAssetsToExport) throws Exception {
        Map<String, List<Map.Entry<String, List<String>>>> expectedFoldersAndFiles = new HashMap<>();

        for (String digitalAssetSetId : digitalAssetSetIds) {
            var digitalAssetSet = DIGITAL_ASSET_COLLECTION.getDigitalAsset(digitalAssetSetId);
            if (!digitalAssetSet.digitalAssets.isEmpty()) {

                String uniqueIdOfProductMaster = PRODUCTS_IN_COMPANY.stream()
                        .filter(product -> product._id.equals(digitalAssetSet.productMasterId))
                        .findFirst()
                        .orElseThrow()
                        .uniqueId;

                String localeName = company.getLocaleName(digitalAssetSet.locale);
                String retailerName = company.getRetailerName(digitalAssetSet.retailerId);
                String campaignName = company.getCampaignName(digitalAssetSet.campaignId);

                String subfolder = uniqueIdOfProductMaster + "_" + localeName
                        + (retailerName.isEmpty() ? "" : "_" + retailerName)
                        + (campaignName.isEmpty() ? "" : "_" + campaignName);

                List<String> expectedFiles;

                if (digitalAssetsToExport.isEmpty()) {
                    expectedFiles = digitalAssetSet.digitalAssets.stream()
                            .map(d -> d.assets.stream().map(a -> getExpectedDigitalAssetName(a.url)).collect(Collectors.toList()))
                            .flatMap(List::stream)
                            .collect(Collectors.toList());
                } else {
                    expectedFiles = digitalAssetSet.digitalAssets.stream()
                            .filter(digitalAsset -> digitalAssetsToExport.contains(digitalAsset.id))
                            .flatMap(digitalAsset -> digitalAsset.assets.stream())
                            .map(asset -> getExpectedDigitalAssetName(asset.url))
                            .collect(Collectors.toList());
                }

                if (expectedFiles.isEmpty()) {
                    continue;
                }

                expectedFiles = expectedFiles.stream().distinct().collect(Collectors.toList());

                List<Map.Entry<String, List<String>>> entry = expectedFoldersAndFiles.get(uniqueIdOfProductMaster);
                Map.Entry<String, List<String>> subfolderAndFiles = Map.entry(subfolder, expectedFiles);
                if (expectedFoldersAndFiles.containsKey(uniqueIdOfProductMaster)) {
                    entry.add(subfolderAndFiles);
                } else {
                    entry = new ArrayList<>();
                    entry.add(subfolderAndFiles);
                    expectedFoldersAndFiles.put(uniqueIdOfProductMaster, entry);
                }
            }
        }

        return expectedFoldersAndFiles;
    }

    private Map<String, List<Map.Entry<String, List<String>>>> getDataInZipFile(String folderPath) throws Exception {
        Map<String, List<Map.Entry<String, List<String>>>> folderMap = new HashMap<>();

        try (var directoryStream = Files.newDirectoryStream(Paths.get(folderPath))) {
            for (var subfolderPath : directoryStream) {
                if (Files.isDirectory(subfolderPath)) {
                    String firstSubfolder = subfolderPath.getFileName().toString();
                    List<Map.Entry<String, List<String>>> entries = new ArrayList<>();
                    try (var subSubFoldersStream = Files.newDirectoryStream(subfolderPath, Files::isDirectory)) {
                        for (var subSubfolderPath : subSubFoldersStream) {
                            String secondSubfolder = subSubfolderPath.getFileName().toString();
                            List<String> files = new ArrayList<>();
                            try (var filesStream = Files.newDirectoryStream(subSubfolderPath, Files::isRegularFile)) {
                                for (var filePath : filesStream) {
                                    files.add(filePath.getFileName().toString());
                                }
                            }
                            entries.add(new AbstractMap.SimpleEntry<>(secondSubfolder, files));
                        }
                    }
                    folderMap.put(firstSubfolder, entries);
                }
            }
        } catch (IOException e) {
            throw new Exception("Error while reading contents of: " + folderPath, e);
        }

        return folderMap;
    }

    private String getExpectedDigitalAssetName(String url) {
        String expectedName = url.substring(url.lastIndexOf("/") + 1);
        int queryIndex = expectedName.indexOf('?');
        if (queryIndex != -1) {
            expectedName = expectedName.substring(0, queryIndex);
        }
        return expectedName.replace("%20", " ");
    }

    private Map<String, List<Map.Entry<String, List<String>>>> sortFoldersAndFiles(Map<String, List<Map.Entry<String, List<String>>>> expectedFoldersAndFiles) {
        expectedFoldersAndFiles = expectedFoldersAndFiles.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey()) // Sort main map by keys
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue()
                                .stream()
                                .sorted(Map.Entry.comparingByKey()) // Sort entries within each key
                                .map(innerEntry -> new AbstractMap.SimpleEntry<>(
                                        innerEntry.getKey(),
                                        innerEntry.getValue().stream().sorted().collect(Collectors.toList()) // Sort list items within each entry
                                ))
                                .collect(Collectors.toList()),
                        (a, b) -> a, LinkedHashMap::new // Maintain insertion order
                ));
        return expectedFoldersAndFiles;
    }
}
